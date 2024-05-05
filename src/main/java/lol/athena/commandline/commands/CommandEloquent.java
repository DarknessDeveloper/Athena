package lol.athena.commandline.commands;

import java.util.List;

import lol.athena.Athena;
import lol.athena.commandline.CommandLineCommand;

public class CommandEloquent extends CommandLineCommand {

	public CommandEloquent(Athena bot, String name, List<String> aliases) {
		super(bot, name, aliases);
	}

	@Override
	public boolean execute(String alias, String[] args) {
		getLogger().info("&#ff00bbEloquent &7-&#ff00bA Java Remake of PHP Laravel's Eloquent");
		getLogger().info("&cThis version of Eloquent is incomplete. Functionaity will be added over time.");
		return true;
	}

}
