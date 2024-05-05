package lol.athena.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.gson.Gson;

import lol.athena.Athena;
import lol.athena.plugin.events.Listener;
import lombok.Getter;
import lombok.Setter;

public class Plugin {

	@Getter private Athena athena;
	@Getter private File dataFolder;
	@Getter private File configFile;
	@Getter private PluginDescriptionFile description;
	@Getter private Logger logger;
	@Getter @Setter private boolean saveConfigOnDisable = false;
	
	private FileConfiguration config;
	private JarFile file;
	private boolean enabled = false;

	public Plugin() {}
	
	@SuppressWarnings("unused") // it IS used via reflection in PluginManager
	protected Plugin(Athena athena, JarFile file, PluginDescriptionFile description) {
		this.athena = athena;
		this.file = file;
		this.description = description;
	}

	public void onEnable() {
		logger.info("Default plugin onEnable(): Plugin " + getName() + " is likely a library plugin.");
		if (Athena.useClasspathLoading()) {
			logger.info("Library plugins can be replaced by the experimental Classpath Loading feature.");
		}
	}

	public void onDisable() {
	}

	protected final void onLoad() {
		dataFolder = new File("plugins/" + description.getName());
		configFile = new File(dataFolder, "config.yml");
		logger = new PluginLogger(this);
	}

	protected final void onUnload() throws IOException {
		if (enabled) {
			try {
				setEnabled(false);
			} catch (Exception ex) {
				athena.getLogger().log(Level.SEVERE, "An unhandled exception ocurred disabling plugin " + getName(), ex);
			}
		}

		// we have 2 separate try/catch IO exceptions so that the jar is still closed
		// even after a failed config save.
		try {
			if (saveConfigOnDisable && config != null) {
				config.save(configFile);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			file.close();
		}
	}

	public final boolean isEnabled() {
		return enabled;
	}

	protected final void setEnabled(boolean enabled) {
		if (!enabled) {
			athena.getPluginManager().disablePlugin2(this);
		}

		this.enabled = enabled;
	}

	public final boolean isActive(String guildId) {
		int id = Athena.db().getPluginIdFromName(getName());
		if (id == -1) {
			athena.getLogger().warning("Loaded and enabled plugin " + getName() + " is not registered and cannot be used!");
			return false;
		}

		return Athena.db().isPluginActiveInGuild(id, guildId);
	}

	public final boolean isActive(long guildId) {
		return isActive(String.valueOf(guildId));
	}

	public FileConfiguration getConfig() {
		if (config == null) {
			saveDefaultConfig();
		}

		return config;
	}

	/**
	 * Saves any changes made to the config loaded in memory to the disk at
	 * '${WORKING_DIR}/plugins/${PLUGIN_NAME}/config.yml'
	 */
	public void saveConfig() {
		try {
			config.save(configFile);
		} catch (IOException ex) {
			athena.getLogger().log(Level.SEVERE, "An error occurred saving the config.", ex);
		}
	}

	/**
	 * Discards any changes made the the config loaded in memory, and reloads from
	 * disk.
	 */
	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(configFile);
		config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(getResource("config.yml"))));
	}

	public void saveDefaultConfig() {
		if (!configFile.exists()) {
			saveResource("config.yml", false);
		}

		config = YamlConfiguration.loadConfiguration(configFile);
		config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(getResource("config.yml"))));
	}

	public final String getName() {
		return description.getName();
	}

	public InputStream getResource(String name) {
		try {
			ZipEntry entry = file.getEntry(name);
			if (entry == null) {
				return null;
			}
			return file.getInputStream(entry);
		} catch (IOException ex) {
			return null;
		}
	}

	public void registerEvents(Listener listener) {
		athena.getPluginManager().registerEvents(listener, this);
	}

	protected void saveResource(String resourcePath, boolean replace) {
		Validate.notBlank(resourcePath, "ResourcePath cannot be null or empty.");

		resourcePath = resourcePath.replace('\\', '/');
		InputStream in = getResource(resourcePath);
		if (in == null) {
			throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found.");
		}

		File outFile = new File(dataFolder, resourcePath);
		int lastIndex = resourcePath.lastIndexOf('/');
		File outDir = new File(dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

		if (!outDir.exists()) {
			outDir.mkdirs();
		}

		try {
			if (!outFile.exists() || replace) {
				OutputStream out = new FileOutputStream(outFile);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
			} else {
				athena.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
			}
		} catch (IOException ex) {
			athena.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
		}
	}

//	public PluginType getPluginType() {
//		return PluginType.SERVER;
//	}
//
//	public NamespacedKey getSystemPluginKey() {
//		return null;
//	}

	public Gson getGson() {
		return athena.getGson();
	}
	
//	public ServerConfig getServerConfig(String serverId) {
//		return null;
//	}
//	
//	public ServerConfig getServerConfig(Guild guild) {
//		return getServerConfig(guild.getId());
//	}
	
	protected void setLogger(PluginLogger logger) {
		this.logger = logger;
	}
}
