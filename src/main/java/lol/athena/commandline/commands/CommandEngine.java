package lol.athena.commandline.commands;

import java.util.List;

import lol.athena.Athena;
import lol.athena.commandline.CommandLineCommand;

public class CommandEngine extends CommandLineCommand {

	public CommandEngine(Athena bot, String name, List<String> aliases) {
		super(bot, name, aliases);
	}

	@Override
	public boolean execute(String alias, String[] args) {
		getLogger().info("&aThe current storage engine class is &c" + Athena.db().getClass().getName());
		//getLogger().info("&#ffff00NOTE: THE ENGINE SYSTEM IS BEING REPLACED WITH A SYSTEM NAMED ELOQUENT.");
		//getLogger().info("&#ffff00NOTE: ENGINE IS NOW DEPRECATED, BUT WILL REMAIN ACTIVE FOR THE FORSEEABLE FUTURE FOR LEGACY REASONS.");
		return true;
	}

}
