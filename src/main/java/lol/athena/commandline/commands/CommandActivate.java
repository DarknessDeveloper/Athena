package lol.athena.commandline.commands;

import java.util.List;

import lol.athena.Athena;
import lol.athena.commandline.CommandLineCommand;

public class CommandActivate extends CommandLineCommand {

	public CommandActivate(Athena bot, String name, List<String> aliases) {
		super(bot, name, aliases);
	}

	@Override
	public boolean execute(String alias, String[] args) {
		if (args.length < 2) {
			getLogger().info("You must specify two arguments: <plugin id/name> <guild id>");
			return true;
		}

		String pIdOrName = args[0];
		boolean isId = false;

		int pid = -1;
		try {
			pid = Integer.parseInt(pIdOrName);
			isId = true;
		} catch (NumberFormatException ex) {

		}

		if (isId) {
			boolean exists = Athena.db().isPluginRegistered(pid);
			if (!exists) {
				getLogger().warning("Plugin with id " + pid + " does not exist.");
				return true;
			}

		} else {
			pid = Athena.db().getPluginIdFromName(pIdOrName);
			if (pid == -1) {
				getLogger().warning("Plugin with id " + pid + " does not exist.");
				return true;
			}
		}

		boolean activated = Athena.db().activate(pid, args[1]);
		if (activated) {
			getLogger().info("Plugin with id " + pid + " is now active in guild id " + args[1]);
		} else {
			getLogger().info("Unable to activate plugin (ID: " + pid + ", Guild ID: " + args[1] + ")");
		}
		return true;
	}

}
