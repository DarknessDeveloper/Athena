package lol.athena.commandline.commands;

import java.util.List;

import lol.athena.Athena;
import lol.athena.commandline.CommandLineCommand;

public class CommandReload extends CommandLineCommand {

	public CommandReload(Athena bot, String name, List<String> aliases) {
		super(bot, name, aliases);
	}

	@Override
	public boolean execute(String alias, String[] args) {
		getLogger().warning("&c#");
		getLogger().warning("&c# WARNING!");
		getLogger().warning("&c#");
		getLogger().warning("&c# RELOADING IS NOT SUPPORTED OFFICIALLY, THIS CAN AND PROBABLY WILL CAUSE PROBLEMS. NO SUPPORT WILL BE GIVEN.");
		getLogger().warning("&c#");
		getLogger().warning("&c# IF PROBLEMS OCCUR AS A RESULT OF THIS RELOAD, RESTART THE BOT.");
		getLogger().warning("&c#");
		
		try {
			Athena.getInstance().experimentalReload();
		} catch (Exception ex) {
			ex.printStackTrace();
			Athena.getInstance().shutdown();
		}
		return true;
	}

}
