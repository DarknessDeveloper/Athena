package lol.athena.commandline.commands;

import java.util.List;

import lol.athena.Athena;
import lol.athena.commandline.CommandLineCommand;

public class CommandHelp extends CommandLineCommand {

	public CommandHelp(Athena bot, String name, List<String> aliases) {
		super(bot, name, aliases);
	}

	@Override
	public boolean execute(String alias, String[] args) {
		getLogger().info("&#7373FFHere is a list of registered command line commands: ");

		for (CommandLineCommand command : getAthena().getCommandLine().getRegisteredCommands()) {
			getLogger().info("&#ff00bb" + command.getName());
		}
		return true;
	}

}
