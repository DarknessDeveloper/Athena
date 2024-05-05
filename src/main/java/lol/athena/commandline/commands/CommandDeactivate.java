package lol.athena.commandline.commands;

import java.util.List;

import lol.athena.Athena;
import lol.athena.commandline.CommandLineCommand;

public class CommandDeactivate extends CommandLineCommand {

	public CommandDeactivate(Athena bot, String name, List<String> aliases) {
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
				getLogger().warning("Plugin id " + pid + " isn't registered.");
				return true;
			}

		} else {
			pid = Athena.db().getPluginIdFromName(pIdOrName);
			if (pid == -1) {
				getLogger().warning("Plugin id " + args[0] + " does not exist.");
				return true;
			}
		}

		boolean deactivated = Athena.db().deactivate(pid, args[1]);
		if (deactivated) {
			getLogger().info("&aPlugin id &c" + pid + "&a is now deactived in guild id &c" + args[1]);
		} else {
			getLogger().info("&cUnable to deactivate.");
		}
		return true;
	}

}
