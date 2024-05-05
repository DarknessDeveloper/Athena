package lol.athena.commandline.commands;

import java.util.List;

import lol.athena.Athena;
import lol.athena.commandline.CommandLineCommand;
import lol.athena.engine.Engine;
import lol.athena.plugin.Plugin;

public class CommandRegister extends CommandLineCommand {

	public CommandRegister(Athena athena, String name, List<String> aliases) {
		super(athena, name, aliases);
	}

	@Override
	public boolean execute(String alias, String[] args) {
		if (args.length < 1) {
			getLogger().info("Usage: register <plugin name>");
			return true;
		}
		
		String pluginName = args[0];
		if (!getAthena().getPluginManager().isLoaded(pluginName)) {
			getLogger().info("That plugin isn't loaded and cannot be registered.");
			return true;
		}
		
		Plugin plugin = getAthena().getPluginManager().getPlugin(pluginName);
		Engine mysql = Athena.db();
		
		if (mysql.getPluginIdFromName(plugin.getName()) != -1) {
			getLogger().info(plugin.getName() + " is already registered.");
			return true;
		}
		
		if (mysql.registerPlugin(plugin)) {
			getLogger().info(plugin.getName() + " is now registered.");
			return true;
		} else {
			getLogger().info("Failed to register plugin " + plugin.getName());
			return true;
		}
	}

}
