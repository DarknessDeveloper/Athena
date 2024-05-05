package lol.athena.commandline.commands;

import java.util.List;

import lol.athena.Athena;
import lol.athena.commandline.CommandLineCommand;

public class CommandEcho extends CommandLineCommand {

	public CommandEcho(Athena bot, String name, List<String> aliases) {
		super(bot, name, aliases);
	}

	@Override
	public boolean execute(String alias, String[] args) {
		if (args.length < 1) {
			getLogger().info("ECHO... ECHo... ECho... Echo... echo...");
			return true;
		}
		
		StringBuilder sb = new StringBuilder();
		for (String string : args) {
			sb.append(string + " ");
		}
		
		getLogger().info(sb.toString().substring(0, sb.length() - 1));
		return true;
	}

}
