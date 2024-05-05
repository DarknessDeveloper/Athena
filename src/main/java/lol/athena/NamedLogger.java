package lol.athena;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class NamedLogger extends AthenaLogger {

	private String pluginName;

	/**
	 * Creates a new PluginLogger that extracts the name from a plugin.
	 *
	 * @param context A reference to the plugin
	 */
	public NamedLogger(@NotNull String context) {
		super(context.getClass().getCanonicalName());
		
		pluginName = String.format("%s[%s]%s ", Athena.ANSI_RESET, context, Athena.ANSI_RESET);
		setParent(Athena.getInstance().getLogger());
		setLevel(Level.ALL);
	}

	@Override
	public void log(@NotNull LogRecord logRecord) {
		logRecord.setMessage(pluginName + logRecord.getMessage() + Athena.ANSI_RESET);
		super.log(logRecord);
	}

}