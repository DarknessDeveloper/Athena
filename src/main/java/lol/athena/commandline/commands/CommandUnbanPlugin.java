package lol.athena.commandline.commands;

import java.util.List;

import lol.athena.Athena;
import lol.athena.commandline.CommandLineCommand;
import lol.athena.engine.Engine;

public class CommandUnbanPlugin extends CommandLineCommand {

	public CommandUnbanPlugin(Athena athena, String name, List<String> aliases) {
		super(athena, name, aliases);
	}

	@Override
	public boolean execute(String alias, String[] args) {
		if (args.length < 1) {
			getLogger().info("Usage: " + alias + " <plugin name>");
			return true;
		}
		
		String pluginName = args[0];
		Engine storageEngine = Athena.db();
		
		if (storageEngine.getPluginIdFromName(pluginName) == -1) {
			getLogger().info(pluginName + " is not registered.");
			return true;
		}
		
		if (storageEngine.unbanPlugin(pluginName)) {
			getLogger().info(pluginName + " is now unbanned.");
			return true;
		} else {
			getLogger().info("Failed to unban plugin " + pluginName);
			return true;
		}
	}

}
