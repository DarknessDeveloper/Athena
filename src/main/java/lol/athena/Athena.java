package lol.athena;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lol.athena.commandline.CommandLineColoured;
import lol.athena.commandline.commands.*;
import lol.athena.engine.Engine;
import lol.athena.engine.sql.EngineSql;
import lol.athena.engine.sqlite.EngineSqLite;
import lol.athena.events.AthenaEventListener;
import lol.athena.plugin.PluginManager;
import lol.athena.staff.StaffManager;
import lol.athena.versioning.VersionChecker;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.Status;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.internal.JDAImpl;
import org.apache.commons.lang3.Validate;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.NotNull;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Athena {

	public static final String BUILD = "0.11";
	public static final String ANSI_RESET = ColouredConsoleSender.ANSI_ESC_CHAR + "[0m";

	public static final int API_VERSION = 0;
	public static final boolean API_MAJOR_RELEASE = true;
	public static final int EXIT_CODE_CRASH = 0x1;
	public static final int EXIT_CODE_DATABASE_ERROR = 0x2;
	public static final int EXIT_CODE_CRASH_LOG_FAILURE = 0xFF;
	@Getter private static Instant timeStarted;
	@Getter private static Athena instance;

	private boolean debug = false;
	private boolean useClasspathLoading = false;
	private ClassLoader classLoader;
	@Getter private Logger logger;
	@Getter private File dataFolder;
	@Getter private File configFile;
	@Getter private FileConfiguration config;
	@Getter private Engine storageEngine;
	@Getter private Terminal terminal;
	@Getter private CommandLineColoured commandLine;
	@Getter private Thread commandLineThread;
	@Getter private PluginManager pluginManager;
	@Getter private StaffManager staffManager;
	@Getter private AthenaEventListener eventListener;
	@Getter private Gson gson;
	@Getter private JDA jda;
	@Getter private boolean skipCancelledEventInvocations = true;
	@Getter private boolean gracefulShutdown = true;

	//@Getter private Eloquent eloquent;

	private static ColouredConsoleSender console;

	private Athena() {
		if (instance != null) {
			throw new UnsupportedOperationException("Cannot create a second instance of singleton Athena");
		}
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {

		instance = new Athena();
		timeStarted = Instant.now();

		try {
			instance.run();
		} catch (Exception ex) {
			ex.printStackTrace();
			instance.logger.log(Level.SEVERE, "An unhandled exception occurred and Athena has crashed.", ex);
			if (instance.jda != null && Status.CONNECTED.equals(instance.jda.getStatus())) {
				instance.jda.shutdown();
			}
			if (instance.commandLineThread != null && instance.commandLineThread.isAlive()) {
				instance.commandLine.setQuit(true);
				instance.commandLineThread.stop();
			}

			CrashLogger.logCrash(ex);
			Runtime.getRuntime().exit(EXIT_CODE_CRASH);
		}
	}

	private void run() throws Exception {
		AnsiConsole.systemInstall();

		initCore();
		establishDatabase();
		loadCommandLine();

// DISCORD

		var builder = JDABuilder.createDefault(getConfig().getString("token"));
		builder.enableIntents(Arrays.asList(GatewayIntent.values())); // all intents, as we don't know which ones
																		// plugins will require.
		eventListener = new AthenaEventListener(this);

		/////////////////////////////////////////////////
		// Connect to Discord, 10 max failed attempts. //
		// - DarknessDev //
		/////////////////////////////////////////////////

		for (int attempt = 0; attempt < 10; attempt++) {
			int attempt2 = attempt + 1;
			try {
				logger.info("(" + attempt2 + ") &e&nConnecting to Discord.");
				redirectJDALogger();
				jda = builder.build().awaitReady(); // Attempt the connection.

				if (jda != null) {
					logger.info("(" + attempt2 + ") &#00ff00Connection successful.");
					break;
				}
			} catch (Exception ex) {
				logger.severe("(" + attempt2 + ") &cFailed to connect to Discord.");

				if (attempt < 9) {
					logger.severe("(" + attempt2 + ") &cRetrying in " + attempt2 + " second(s).");
					Thread.sleep(1000 * attempt2); // Wait for 1*attempt second(s) before retrying.
				} else {
					throw ex;
				}
			}
		}

		jda.addEventListener(eventListener);

		ActivityType type = ActivityType.LISTENING;
		String typeUnparsed = config.getString("status.type", "LISTENING").toUpperCase();

		try {
			type = ActivityType.valueOf(typeUnparsed);
		} catch (EnumConstantNotPresentException ex) {
			logger.warning("Unknown activity type: " + typeUnparsed);
		}

		jda.getPresence().setActivity(Activity.of(type, config.getString("status.message", "my plugins.")));

// PLUGINS

		pluginManager = new PluginManager(this);
		loadClasspath(); // we load classpath first in-case any plugins rely on something from it.
		loadPlugins();
		enablePlugins();

// LOG STARTUP

		if (!logAthenaStatus(true)) {
			logger.warning(Lang.get("log.startup.fail"));
		}

		TextChannel channel = (TextChannel) jda.getGuildChannelById(config.getString("ids.announcements", "987855895113388052"));
		if (channel != null) {
			logSponsors(channel);
		}

//  CHECK FOR UPDATES (VERBOSE/NON-SILENT MODE).

		VersionChecker.check(false);
	}

	/**
	 * All this just for 3 lines in the console to have the Athena log prefix... why
	 * did i do this? why does it matter?
	 */
	private void redirectJDALogger() {
		try {
			Field field = JDAImpl.class.getField("LOG");
			Class<?> classFetched = field.get(null).getClass();
			Field desired = classFetched.getDeclaredField("TARGET_STREAM");
			desired.setAccessible(true);
			desired.set(desired, new RedirectedPrintStreamLog(terminal.output()));
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void logSponsors(TextChannel channel) {
		if (debug) {
			return;
		}

		final String[] sponsorImages = new String[] { "https://my.hosthatchet.com/templates/lagom2/assets/img/logo/logo_big.1616840655.png" };

		List<MessageEmbed> embeds = new ArrayList<>();
		for (String sponsor : sponsorImages) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("Sponsor");
			builder.setColor(Color.green.brighter());
			builder.setImage(sponsor);
			embeds.add(builder.build());
		}

		channel.sendMessageEmbeds(embeds).queue();
	}

	private void initCore() {
		classLoader = getClass().getClassLoader();
		logger = new AthenaLogger("Athena");
		try {
			terminal = TerminalBuilder.builder().jansi(true).build();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		console = new ColouredConsoleSender(null);
		dataFolder = new File(System.getProperty("user.dir"));
		
		Lang.init();
		saveConfig("bot.yml");
		
		debug = config.getBoolean("debug", false);
		skipCancelledEventInvocations = config.getBoolean("events.skip-cancelled", false);
		gracefulShutdown = config.getBoolean("graceful-shutdown", true);

		staffManager = new StaffManager(this, new File("staff.yml"));
		gson = new GsonBuilder().setPrettyPrinting().create();
	}

	private void establishDatabase() throws AthenaInitException {

		String database = config.getString("storage.engine", "sqlite").toLowerCase();
		switch (database) {
		case "sqlite" -> storageEngine = new EngineSqLite(this, config.getString("sqlite.file", "athena.db"));
		case "mysql", "sql", "mongodb" -> storageEngine = new EngineSql(this, config.getConfigurationSection("mysql"));
		default -> throw new RuntimeException("Unknown storage engine supplied.");
		}

		try {
			if (!storageEngine.connect()) {
				if (storageEngine instanceof EngineSqLite) {
					throw new SQLException("Failed to initialize SQLite.");
				} else {
					logger.warning("&#ff4444The main database connection failed. Defaulting to SQLite.");
					storageEngine = new EngineSqLite(this, config.getString("sqlite.file", "athena.db"));
					if (!storageEngine.connect()) {
						throw new SQLException("The primary database connection failed, and so did the backup.");
					}
				}
			}
			storageEngine.createNonexistantTables();
		} catch (Exception ex) {
			throw new AthenaInitException(ex);
		}

//		try {
//			eloquent = Eloquent.instance(config.getConfigurationSection("mysql"));
//			eloquent.runMigrations(CreateTablePlugins.class);
//		} catch (ClassNotFoundException ex) {
//			ex.printStackTrace();
//		}
	}

	private void loadCommandLine() {

		commandLine = new CommandLineColoured();
		commandLineThread = new Thread(commandLine);
		commandLineThread.start();

		CommandShutdown commandShutdown = new CommandShutdown(this, "shutdown", Lists.newArrayList("exit", "stop", "pots"));
		CommandPlugins commandPlugins = new CommandPlugins(this, "plugins", Lists.newArrayList("pl", "mods", "extensions"));
		CommandActivate commandActivate = new CommandActivate(this, "activate", Lists.newArrayList());
		CommandDeactivate commandDeactivate = new CommandDeactivate(this, "deactivate", Lists.newArrayList());
		CommandStaff commandStaff = new CommandStaff(this, "staff", Lists.newArrayList("staffmanager"));
		CommandHelp commandHelp = new CommandHelp(this, "help", Lists.newArrayList("helpme", "commandhelp"));
		CommandLog commandLog = new CommandLog(this, "log", Lists.newArrayList("say", "echo"));
		CommandReload commandReload = new CommandReload(this, "reload", Lists.newArrayList("rl"));
		CommandUpdate commandUpdate = new CommandUpdate(this, "update", Lists.newArrayList("downloadupdate", "download"));
		CommandEngine commandEngine = new CommandEngine(this, "engine", Lists.newArrayList("storage", "storageengine", "storage-engine", "storage.engine"));
		CommandEloquent commandEloquent = new CommandEloquent(this, "eloquent", Lists.newArrayList("elo", "laravel"));

		CommandPlugMan commandPlugMan = new CommandPlugMan(this, "plugman", Lists.newArrayList("pm", "pluginmanager"));
		CommandRegister commandRegister = new CommandRegister(this, "register", Lists.newArrayList("registerplugin"));
		CommandBanPlugin commandBanPlugin = new CommandBanPlugin(this, "banplugin", Lists.newArrayList("ban"));
		CommandUnbanPlugin commandUnbanPlugin = new CommandUnbanPlugin(this, "unbanplugin", Lists.newArrayList("unban", "pardon"));

		// Core Commands
		commandLine.registerCommand(commandShutdown.getName(), commandShutdown);
		commandLine.registerCommand(commandStaff.getName(), commandStaff);
		commandLine.registerCommand(commandHelp.getName(), commandHelp);
		commandLine.registerCommand(commandLog.getName(), commandLog);
		commandLine.registerCommand(commandReload.getName(), commandReload);
		commandLine.registerCommand(commandUpdate.getName(), commandUpdate);
		commandLine.registerCommand(commandEngine.getName(), commandEngine);
		commandLine.registerCommand(commandEloquent.getName(), commandEloquent);

		// Plugin Commands
		commandLine.registerCommand(commandPlugins.getName(), commandPlugins);
		commandLine.registerCommand(commandRegister.getName(), commandRegister);
		commandLine.registerCommand(commandActivate.getName().toLowerCase(), commandActivate);
		commandLine.registerCommand(commandDeactivate.getName().toLowerCase(), commandDeactivate);
		commandLine.registerCommand(commandPlugMan.getName().toLowerCase(), commandPlugMan);
		commandLine.registerCommand(commandBanPlugin.getName(), commandBanPlugin);
		commandLine.registerCommand(commandUnbanPlugin.getName(), commandUnbanPlugin);

	}

	public void shutdown() {
		logger.info(Lang.get("log.shutdown.header"));

		if (!logAthenaStatus(false)) {
			logger.warning(Lang.get("log.shutdown.fail"));
		}

		staffManager.save();
		commandLine.setQuit(true);

		if (gracefulShutdown) {
			jda.shutdown();
		} else {
			jda.shutdownNow();
		}

		storageEngine.close();
		//eloquent.close();

		logger.info("Goodbye");
	}

	private @NotNull EmbedBuilder getAthenaStatusBuilder(Color color, String message) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setColor(color);
		embedBuilder.setAuthor("Athena System Message", "https://www.youtube.com/channel/UCw07W7dt5vR9VXqIGb3tIJA", jda.getSelfUser().getAvatarUrl());
//		embedBuilder.setFooter("Athena version " + BUILD, jda.getSelfUser().getAvatarUrl());

		User dev = jda.retrieveUserById("213655288039866368").complete();
//		embedBuilder.setFooter(dev.getName() + "#" + dev.getDiscriminator() + " (UID: " + dev.getId() + ")", dev.getAvatarUrl());
		embedBuilder.setFooter(String.format("%s#%s | %s", dev.getName(), dev.getDiscriminator(), dev.getId()));
		embedBuilder.addField("Athena Status Update", message, false);
//		embedBuilder.addField("Developer", jda.retrieveUserById("213655288039866368").complete().getAsMention(), false);

		return embedBuilder;
	}

	private boolean logAthenaStatus(boolean online) {
		EmbedBuilder builder = null;

		if (online) {
			builder = getAthenaStatusBuilder(Color.green.brighter(), "Athena is now online.");
		} else {
			builder = getAthenaStatusBuilder(Color.red.brighter(), "Athena is now offline.");
		}

		if (online && debug) {

			builder.getFields().add(1, new net.dv8tion.jda.api.entities.MessageEmbed.Field("Debug Mode Active", "Debug mode is active. There will be more information printed to console.", false));
			builder.getFields().add(1, new net.dv8tion.jda.api.entities.MessageEmbed.Field("Storage Engine", storageEngine.getClass().getSimpleName(), false));
		}

		GuildChannel channel = jda.getGuildChannelById(config.getString("ids.announcements", "987855895113388052"));
		if (channel == null) {
			logger.warning("&#ff4444Unable to locate the Athena status channel.");
			return false;
		}

		return logAthenaStatus(builder.build());
	}

	private boolean logAthenaStatus(MessageEmbed embed) {
		try {

			TextChannel channel = (TextChannel)  jda.getGuildChannelById(config.getString("ids.announcements", "987855895113388052"));
			if (channel == null) {
				logger.warning("&#ff4444Unable to locate the Athena status channel.");
				return false;
			}

			channel.sendMessageEmbeds(embed).queue();
			return true;
		} catch (ClassCastException ex) {
			logger.warning("&#ff4444The provided status channel is not a text channel.");
			return false;
		} catch (Exception ex) {
			return false;
		}
	}

	private void saveConfig(String configName) {
		configFile = new File(dataFolder, configName);

		if (!configFile.exists()) {
			saveResource(configFile.getName(), false);
			logger.severe(Lang.get("log.config.not-found"));
			System.exit(0);
		}

		config = YamlConfiguration.loadConfiguration(configFile);
		config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(getResource(configFile.getName()))));
	}

	private void loadClasspath() throws IOException {
		NamedLogger namedLogger = new NamedLogger("&#ffff00ClassPath&r");
		useClasspathLoading = config.getBoolean("enable-classpath-jar-loading", false);

		if (!useClasspathLoading) {
			namedLogger.info("&#ff4444Classpath loading is not enabled.");
			return;
		}
		namedLogger.info("&#ff4444Classpath loading is experimental.");

		File classpathDirectory = new File(getDataFolder(), "classpath/");
		classpathDirectory.mkdirs();
		File[] files = classpathDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar") || name.toLowerCase().endsWith(".zip"));

		if (files == null || files.length < 1) {
			namedLogger.info("&#ffff00No classpath files detected.");
			return;
		}
		for (File file : files) {
			namedLogger.info("&#ff0000Adding &#ff4444" + file.getAbsolutePath() + "&#ff4444 classes to the classpath.");
			URL[] urls = new URL[] { new URL("jar:file:" + file.getAbsolutePath() + "!/") };
			pluginManager.addUrls(urls);

		}
	}

	private void loadPlugins() throws Exception {

		File pluginsDirectory = new File("plugins");
		pluginsDirectory.mkdir();

		int loaded = pluginManager.loadPlugins(pluginsDirectory);
		pluginManager.getLogger().info(Lang.get("plugins.loaded", loaded));
		if (pluginManager.getSkippedPluginCount() > 0) {
			pluginManager.getLogger().warning("&#ff4444" + Lang.get("plugins.skipped.total", pluginManager.getSkippedPluginCount()));
		}
	}

	public static String getUptime() {
		long seconds = Instant.now().getEpochSecond() - timeStarted.getEpochSecond();
		int day = (int) TimeUnit.SECONDS.toDays(seconds);
		int hour = (int) TimeUnit.SECONDS.toHours(seconds) - (day * 24);
		int minute = (int) (TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60));
		int second = (int) (TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60));

		return String.format("%d:%d:%d:%d", day, hour, minute, second);
	}

	private void enablePlugins() {
		pluginManager.enablePlugins();
	}
	public void experimentalReload() throws Exception {
		NamedLogger reloadLogger = new NamedLogger("&aReload");
		MessageEmbed msg = getAthenaStatusBuilder(Color.yellow.brighter(), "A reload has been requested. This is not officially supported.").build();
		logAthenaStatus(msg);

		pluginManager.getPlugins().forEach(plugin -> {
			try {
				pluginManager.disablePlugin(plugin);
			} catch (Exception ex) {
				reloadLogger.log(Level.SEVERE, "&#ff4444An unhandled exception occurred disabling plugin " + plugin.getName(), ex);
			}
		});

		try {
			reloadLogger.info("Disconnecting from SQL database...");
			storageEngine.close();
//            databaseConnection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			reloadLogger.severe("&#ff4444Unable to close Athena's SQL connection.");
		}

		reloadLogger.info("Stopping command line...");
		commandLine.setQuit(true);

		reloadLogger.info("&#00FFFFReinitializing Athena...");
		initCore();
		establishDatabase();
		loadCommandLine();

		pluginManager = new PluginManager(this);
		loadClasspath();
		loadPlugins();
		enablePlugins();
	}

	public static void debug(String message) {
		if (!instance.debug) {
			return;
		}

		instance.logger.info("&#0090FF[DEBUG]&r " + message);
	}

	public static boolean isDebug() {
		return instance.debug;
	}

	public static boolean useClasspathLoading() {
		return instance.useClasspathLoading;
	}

	public static FileConfiguration config() {
		return instance.config;
	}

	public static ConfigurationSection config(String section) {
		return instance.config.getConfigurationSection(section);
	}

	public static Gson gson() {
		return instance.gson;
	}

	public static Engine db() {
		return instance.storageEngine;
	}

	public static boolean any(String needle, String... haystack) {
		for (String hay : haystack) {
			if (needle.equalsIgnoreCase(hay)) {
				return true;
			}
		}
		return false;
	}

	public static String read(InputStream stream) throws IOException {
		InputStreamReader iReader = new InputStreamReader(stream);
		BufferedReader reader = new BufferedReader(iReader);
		StringBuilder builder = new StringBuilder();

		String line;
		while ((line = reader.readLine()) != null) {
			builder.append("\n").append(line);
		}

		reader.close();
		return builder.substring(1);
	}

	public static File getRunDirectory() {
		File folder = new File(System.getProperty("user.dir"));
		if (instance == null || instance.dataFolder == null) {
			return folder;
		}
		return instance.dataFolder;
	}

	/**
	 * @deprecated Use {@link #getJda()}
	 */
	public JDA getBot() {
		return getJda();
	}

	/*
	 * Credits to SpigotMC and Bukkit for all the code to do with YAML. You guys are
	 * legends.
	 */

	public void saveResource(@NotNull String resourcePath, boolean replace) {
		Validate.notBlank(resourcePath, "ResourcePath cannot be null or empty.");

		resourcePath = resourcePath.replace('\\', '/');
		InputStream in = getResource(resourcePath);
		if (in == null) {
			throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + configFile);
		}

		File outFile = new File(dataFolder, resourcePath);
		int lastIndex = resourcePath.lastIndexOf('/');
		File outDir = new File(dataFolder, resourcePath.substring(0, Math.max(lastIndex, 0)));

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
				logger.log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
			}
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
		}
	}

	public InputStream getResource(@NotNull String filename) {
		Validate.notNull(filename, "Filename must not be null.");

		try {
			URL url = getClassLoader().getResource(filename);

			if (url == null) {
				return null;
			}

			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			return connection.getInputStream();
		} catch (IOException ex) {
			return null;
		}
	}

	public final ClassLoader getClassLoader() {
		return classLoader;
	}

	public static ColouredConsoleSender console() {
		return console;
	}

	public static String tl(String message) {
		return ChatColor.tl(message);
	}

	public static String hansi(String message) {
		String H_ANSI_FORMAT = String.valueOf(ColouredConsoleSender.ANSI_ESC_CHAR) + "[38;2;%d;%d;%dm";
		String H_REGEX = "\\&\\#([0-9A-Fa-f]{3}){2}";

		Pattern pattern = Pattern.compile(H_REGEX);
		Matcher matcher = pattern.matcher(message);

		StringBuilder builder = new StringBuilder();

		while (matcher.find()) {
			String replacement = matcher.group(0);
			int[] rgbSegments = new int[3];

			for (int i = 0; i < replacement.length() / 2; i++) {
				if (i == 0) {
					continue;
				}

				String segment = replacement.substring(2 * i, 2 + (2 * i));
				rgbSegments[i - 1] = Integer.parseInt(segment, 16);
			}

			matcher.appendReplacement(builder, String.format(H_ANSI_FORMAT, rgbSegments[0], rgbSegments[1], rgbSegments[2]));
		}

		matcher.appendTail(builder);
		return builder.toString() + ANSI_RESET;
	}

//	public static String tl2(String message) {
//		
//	}
}