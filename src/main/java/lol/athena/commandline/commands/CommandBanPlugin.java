package lol.athena.commandline.commands;

import java.util.List;

import lol.athena.Athena;
import lol.athena.commandline.CommandLineCommand;
import lol.athena.engine.Engine;

public class CommandBanPlugin extends CommandLineCommand {

	public CommandBanPlugin(Athena athena, String name, List<String> aliases) {
		super(athena, name, aliases);
	}

	@Override
	public boolean execute(String alias, String[] args) {
		if (args.length < 1) {
			getLogger().info("Usage: " + alias + " <plugin name>");
			return true;
		}
		
		String pluginName = args[0];
		Engine mysql = Athena.db();
		
		if (mysql.getPluginIdFromName(pluginName) == -1) {
			getLogger().info(pluginName + " is not registered.");
			return true;
		}
		
		if (mysql.banPlugin(pluginName)) {
			getLogger().info(pluginName + " is now banned.");
			return true;
		} else {
			getLogger().info("Failed to ban plugin " + pluginName);
			return true;
		}
	}

}
