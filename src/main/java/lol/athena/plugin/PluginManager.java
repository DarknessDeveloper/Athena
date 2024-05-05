package lol.athena.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Lists;

import lol.athena.Athena;
import lol.athena.NamedLogger;
import lol.athena.events.EventHandler;
import lol.athena.events.EventRegistration;
import lol.athena.plugin.events.Listener;
import lol.athena.plugin.events.PluginDisableEvent;
import lol.athena.plugin.events.PluginEnableEvent;
import lol.athena.plugin.events.PluginLoadEvent;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;

public class PluginManager {

	@Getter private final Athena athena;
	@Getter private final Logger logger;
	@Getter private Map<String, Plugin> loadedPlugins;
	@Getter private Map<String, Plugin> missingDependencies;
	@Getter private Map<Class<? extends Event>, List<EventRegistration>> registeredEvents;

	private PluginClassLoader globalPluginClasses;

	@Getter private int skippedPluginCount = 0;

	public PluginManager(Athena athena) {
		this.athena = athena;
		logger = new NamedLogger("&#ff00bbPlugin Manager" + Athena.ANSI_RESET);

		globalPluginClasses = new PluginClassLoader("Global", new URL[0], getClass().getClassLoader());
		Thread.currentThread().setContextClassLoader(globalPluginClasses);
	}

	/**
	 * Fetches all JAR files from a given directory and attempts to load them.
	 *
	 * @param directory The directory to load any valid plugins from.
	 * @throws Exception If loading any of the plugins fails.
	 */
	public void loadPlugins(String directory) throws Exception {
		File file = new File(directory.replace("/", File.pathSeparator).replace("\\", File.pathSeparator));
		logger.info(file.getPath() + " // " + file.getAbsolutePath());
		loadPlugins(file);
	}

	/**
	 * Fetches all JAR files from a given directory and attempts to load them.
	 *
	 * @param directory The directory to load any valid plugins from.
	 * @throws RuntimeException if one is to occur.
	 */
	@SuppressWarnings("unchecked")
	public int loadPlugins(File directory) throws Exception {

		loadedPlugins = new HashMap<>();
		missingDependencies = new HashMap<>();
		int loaded = 0;

		File[] files = directory.listFiles(pathname -> pathname.getName().toLowerCase().endsWith(".jar"));

		if (files == null || files.length < 1) {
			getLogger().info("&#ff0000No plugins detected.");
			return 0;
		}

		for (File file : files) {
			URL[] urls = new URL[] { new URL("jar:file:" + file.getAbsolutePath() + "!/") };
			globalPluginClasses.addPluginClasses(urls);
		}
		for (File file : files) {
			try {
				logger.info("Loading plugin from file '" + file.getName() + "'");
				JarFile jar = new JarFile(file);
				ZipEntry pluginYamlEntry = jar.getEntry("plugin.yml");

				if (pluginYamlEntry == null) {
					logger.warning("Unable to load plugin '" + file.getName() + "': Missing required plugin.yml file.");
					skippedPluginCount += 1;
					continue;
				}

				PluginDescriptionFile descriptionFile = new PluginDescriptionFile(jar.getInputStream(pluginYamlEntry));

				if (loadedPlugins.containsKey(descriptionFile.getName().toLowerCase())) {
					jar.close();
					logger.warning("Ambiguous plugin name " + descriptionFile.getName() + ", skipping.");
					skippedPluginCount += 1;
					continue;
				}

				if (descriptionFile.getMinApiVersion() > Athena.API_VERSION) {
					jar.close();
					logger.warning("&c" + descriptionFile.getName() + "&c is incompatible with this version of Athena, skipping.");
					skippedPluginCount += 1;
					continue;
				}

				Class<? extends Plugin> pluginMain = (Class<? extends Plugin>) Class.forName(descriptionFile.getMain(), true, globalPluginClasses);
				Constructor<? extends Plugin> pluginMainConstructor = pluginMain.getConstructor(Athena.class, JarFile.class, PluginDescriptionFile.class);
				Plugin plugin = pluginMainConstructor.newInstance(athena, jar, descriptionFile);
				boolean skip = false;

				List<String> dependencies = descriptionFile.getDependencies();
				for (String dependency : dependencies) {
					if (!isLoaded(dependency)) {
						missingDependencies.put(plugin.getName(), plugin);
						skip = true;
						break;
					}
				}

				if (skip) {
					continue;
				}

				if (!isRegistered(plugin)) {
					getLogger().info("&#0000ffRegistering plugin &#ff00bb" + plugin.getName() + "&#0000bb.");
					register(plugin);
				}

				plugin.onLoad();
				loadedPlugins.put(descriptionFile.getName().toLowerCase(), plugin);
				new PluginLoadEvent(athena.getJda(), plugin).call();

				loaded += 1;
			} catch (Exception ex) {
				if (ex instanceof RuntimeException) {
					throw ex;
				}

				skippedPluginCount += 1;
				ex.printStackTrace();
			}
		}

		loaded += loadMissingDependencies();
		return loaded;
	}

	public void addUrls(URL[] urls) {
		globalPluginClasses.addPluginClasses(urls);
	}

	private int loadMissingDependencies() {
		int loaded = 0;

		for (Entry<String, Plugin> toLoad : missingDependencies.entrySet()) {
			String name = toLoad.getKey();
			Plugin plugin = toLoad.getValue();
			List<String> dependencies = plugin.getDescription().getDependencies();
			List<String> missingDeps = new ArrayList<>();
			boolean load = true;

			for (String dependency : dependencies) {
				if (!isLoaded(dependency)) {
					missingDeps.add(dependency);
					load = false;
					break;
				}
			}

			if (!load) {
				logger.warning("&#ff0000Plugin '" + name + "' attempted to load with " + missingDeps.size() + " missing dependencies. Skipping.");
				skippedPluginCount += 1;
				continue;
			}
			plugin.onLoad();
			loadedPlugins.put(plugin.getName().toLowerCase(), plugin);
			new PluginLoadEvent(athena.getJda(), plugin).call();

			loaded += 1;
		}

		return loaded;
	}

	public void enablePlugins() {
		loadedPlugins.values().forEach(this::enablePlugin);
	}

	public void enablePlugin(Plugin plugin) {
		if (plugin == null || plugin.isEnabled()) {
			return;
		}
		
		try {
			// bot.getLogger().severe(plugin.getClass().getClassLoader().getClass().getName());
			List<String> dependencies = plugin.getDescription().getDependencies();
			if (dependencies.size() > 0) {
				String missing = "";
				boolean continueLoading = true;
				for (String dependency : dependencies) {
					if (!isLoaded(dependency)) {
						missing = dependency;
						continueLoading = false;
						break;
					}
					if (!isEnabled(dependency)) {
						enablePlugin(getPlugin(dependency));
					}
				}

				if (!continueLoading) {
					throw new MissingDependencyException(plugin.getName() + " is missing dependency '" + missing + "', and therefore cannot enable.");
				}
			}
			logger.info("Enabling plugin " + plugin.getName());
			plugin.setEnabled(true);
			plugin.onEnable();
			PluginEnableEvent event = new PluginEnableEvent(athena.getJda(), plugin);
			event.call();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "&#ff0000An error occurred enabling plugin " + plugin.getName(), ex);
			ex.printStackTrace();
			plugin.setEnabled(false);
		}

	}

	/**
	 * Disables a plugin and removes all event registrations that it has registered.
	 *
	 * @param plugin The plugin to disable
	 */
	public void disablePlugin(Plugin plugin) {
		if (!plugin.isEnabled()) {
			return;
		}

		plugin.getLogger().info("Disabling plugin " + plugin.getName());
		plugin.setEnabled(false);

	}

	/**
	 * Same as desableModule(Module) but this one doesn't change the status of
	 * {@link Plugin#enabled}
	 *
	 * @param plugin The plugin to disable
	 */
	protected void disablePlugin2(Plugin plugin) {
		if (!plugin.isEnabled()) {
			return;
		}

		PluginDisableEvent pluginDisableEvent = new PluginDisableEvent(athena.getJda(), plugin);

		// plugin.getLogger().info("[DP2] Disabling plugin " + plugin.getName());
		plugin.onDisable();
		unregisterAllEvents(plugin);

		pluginDisableEvent.call();
	}

	public void unloadPlugin(Plugin plugin) {
		Validate.notNull(plugin, "Plugin must not be null.");

		if (plugin.isEnabled()) {
			disablePlugin(plugin);
		}

		try {
			plugin.onUnload();
		} catch (IOException ex) {
			ex.printStackTrace();
			plugin.getLogger().warning("&eFailed onUnload()");
		}
		loadedPlugins.remove(plugin.getName().toLowerCase(), plugin);
	}

	@SuppressWarnings("unchecked")
	/***
	 * @param listener The listener class to register all events for.
	 * @param plugin   The plugin in which the provided Listener class belongs to.
	 * 
	 * @throws PluginException - Thrown if the plugin events are being registered
	 *                         to, is disabled.
	 */
	public void registerEvents(Listener listener, Plugin plugin) {
		if (registeredEvents == null) {
			registeredEvents = new HashMap<>();
		}

		if (!plugin.isEnabled()) {
			throw new PluginException(plugin, "Cannot register events to a disabled plugin.");
		}

		Class<? extends Listener> listenerClass = listener.getClass();
		Method[] methods = listenerClass.getDeclaredMethods();

		for (Method method : methods) {

			EventHandler handler = method.getAnnotation(EventHandler.class);
			if (handler == null) {
//				if (Athena.isDebug()) {
//					plugin.getLogger().info("&9[DEBUG]&r Skipping listener method " + method.getName() + " as it does not contain an EventHandler annotation.");
//				}
				continue;
			}
			if (method.getParameters().length != 1) {
				if (Athena.isDebug()) {
					plugin.getLogger()
							.info("&9[DEBUG]&r Skipping listener method " + method.getName() + " as it contains an invalid parameter count (Expected 1, got " + method.getParameterCount() + ").");
				}
				continue;
			}
			if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
				plugin.getLogger().warning("EventHandler method (" + method.getName() + ") for " + listenerClass.getName() + " is not a valid event handler.");
				continue;
			}

			Class<? extends Event> event = (Class<? extends Event>) method.getParameterTypes()[0];
			EventRegistration registration = new EventRegistration(plugin, listener, method);

			if (registeredEvents.containsKey(event)) {
				List<EventRegistration> registrations = registeredEvents.get(event);
				if (registrations.contains(registration)) {
					continue;
				}
				registrations.add(registration);
				registeredEvents.replace(event, registrations);
			} else {
				registeredEvents.put(event, Lists.newArrayList(registration));
			}
		}
	}

	/**
	 * Loops through all event registrations, removing any for a specific plugin.
	 *
	 * @param plugin The plugin to unregister all events from.
	 */
	protected void unregisterAllEvents(Plugin plugin) {
		Map<Class<? extends Event>, List<EventRegistration>> newRegistrations = registeredEvents;

		for (Entry<Class<? extends Event>, List<EventRegistration>> registrations : registeredEvents.entrySet()) {
			List<EventRegistration> registrationList = registrations.getValue();
			List<EventRegistration> toRemove = new ArrayList<>();

			for (EventRegistration registration : registrationList) {
				if (registration.getPlugin().equals(plugin)) {
					toRemove.add(registration);
				}
			}
			for (EventRegistration eventRegistration : toRemove) {
				registrationList.remove(eventRegistration);
				plugin.getLogger()
						.info("Event unregister - " + eventRegistration.getInvokingMethod().getName() + "(" + eventRegistration.getInvokingMethod().getParameters()[0].getType().getSimpleName() + ")");
			}

			newRegistrations.replace(registrations.getKey(), registrationList);
		}

		registeredEvents = newRegistrations;
	}

	/**
	 * @param plugin The plugin to check if loaded.
	 * @return true if a plugin is found, false otherwise.
	 */
	public boolean isLoaded(Plugin plugin) {
		return loadedPlugins.containsValue(plugin);
	}

	/**
	 * @param pluginName The plugin to check if loaded.
	 * @return true if a plugin is found, false otherwise.
	 */
	public boolean isLoaded(String pluginName) {
		return loadedPlugins.containsKey(pluginName.toLowerCase());
	}

	/**
	 * @param plugin The plugin to check if enabled.
	 * @return true if a plugin is enabled, false otherwise.
	 */
	public boolean isEnabled(Plugin plugin) {
		Validate.notNull(plugin, "Plugin must not be null...");

		return plugin.isEnabled();
	}

	/**
	 * @param plugin The plugin to check if enabled.
	 * @return true if a plugin is enabled, false otherwise.
	 */
	public boolean isEnabled(String plugin) {
		Validate.notNull(plugin, "Plugin name must not be null...");
		return loadedPlugins.containsKey(plugin.toLowerCase()) && isEnabled(loadedPlugins.get(plugin.toLowerCase()));
	}

	public boolean isRegistered(Plugin plugin) {
		return Athena.db().isPluginRegistered(plugin);
	}

	public void register(Plugin plugin) {
		Athena.db().registerPlugin(plugin);
	}

	public void unregister(Plugin plugin) {
		Athena.db().unregisterPlugin(plugin);
	}

	public boolean isActive(Plugin plugin, Guild guild) {
		return Athena.db().isPluginActiveInGuild(plugin, guild.getId());
	}

	public void activate(Plugin plugin, Guild guild) {
		Validate.notNull(plugin, "Plugin must not be null...");
		Validate.notNull(guild, "Guild must not be null...");

		if (plugin.isActive(guild.getId())) {
			return;
		}

		Athena.db().activate(plugin, guild.getId());
	}

	public void deactivate(Plugin plugin, Guild guild) {
		if (!plugin.isActive(guild.getId())) {
			return;
		}

		Athena.db().deactivate(plugin, guild.getId());
	}

	public Plugin getPlugin(String name) {
		return loadedPlugins.getOrDefault(name.toLowerCase(), null);
	}

	public Collection<Plugin> getPlugins() {
		return loadedPlugins.values();
	}

	public Collection<Plugin> getEnabledPlugins() {
		List<Plugin> plugins = new ArrayList<>();

		getPlugins().forEach(plugin -> {
			if (plugin.isEnabled()) {
				plugins.add(plugin);
			}
		});

		return plugins;
	}

	public Collection<Plugin> getDisabledPlugins() {
		List<Plugin> plugins = new ArrayList<>();

		getPlugins().forEach(plugin -> {
			if (!plugin.isEnabled()) {
				plugins.add(plugin);
			}
		});

		return plugins;
	}

	protected PluginClassLoader getGlobalPluginClassLoader() {
		return globalPluginClasses;
	}

	public void unload() {
		registeredEvents.clear();
		loadedPlugins.clear();

		globalPluginClasses = new PluginClassLoader("Global", new URL[0], getClass().getClassLoader());
		Thread.currentThread().setContextClassLoader(globalPluginClasses);
	}

}
