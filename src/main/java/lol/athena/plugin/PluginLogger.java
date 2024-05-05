package lol.athena.plugin;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.jetbrains.annotations.NotNull;

import lol.athena.Athena;
import lol.athena.AthenaLogger;

public class PluginLogger extends AthenaLogger {
	private String pluginName;

	/**
	 * Creates a new PluginLogger that extracts the name from a plugin.
	 *
	 * @param context A reference to the plugin
	 */
	public PluginLogger(@NotNull Plugin context) {
		super(context.getClass().getCanonicalName());
		String prefix = context.getDescription().getName();
		pluginName = "&r[&b" + prefix + "&r] ";
		setParent(Athena.getInstance().getLogger());
		setLevel(Level.ALL);
	}

	@Override
	public void log(@NotNull LogRecord logRecord) {
		logRecord.setMessage(pluginName + logRecord.getMessage());
		super.log(logRecord);
	}

}