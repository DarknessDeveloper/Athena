package lol.athena.commandline.commands;

import java.util.List;

import lol.athena.Athena;
import lol.athena.commandline.CommandLineCommand;

public class CommandShutdown extends CommandLineCommand {

	public CommandShutdown(Athena bot, String name, List<String> aliases) {
		super(bot, name, aliases);
	}

	@Override
	public boolean execute(String alias, String[] args) {
		if (alias.equalsIgnoreCase("perish")) {
			getLogger().severe("No u!");
		}
		
		getAthena().shutdown();
		return true;
	}

}
