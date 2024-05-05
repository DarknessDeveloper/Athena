package lol.athena.commandline.commands;

import java.util.List;

import org.bukkit.ChatColor;

import lol.athena.Athena;
import lol.athena.commandline.CommandLineCommand;

public class CommandPlugins extends CommandLineCommand {

	public CommandPlugins(Athena athena, String name, List<String> aliases) {
		super(athena, name, aliases);
	}

	@Override
	public boolean execute(String alias, String[] args) {
		if (getAthena().getPluginManager().getPlugins().size() < 1) {
			getLogger().info(ChatColor.tl("&#ff0000No plugins installed."));
			return true;
		}
		
		StringBuilder stringBuilder = new StringBuilder("[");
		getAthena().getPluginManager().getPlugins().forEach(plugin -> {
			String color = "&#" + (plugin.isEnabled() ? "00ff00" : "FF8484" );
			if (Athena.db().isBanned(plugin.getName())) {
				color = "&#ff0000";
			}
			stringBuilder.append(color + plugin.getName() + Athena.ANSI_RESET + ", ");
		});
		
		getLogger().info(stringBuilder.substring(0, stringBuilder.length() - 2) + "]");
		return true;
	}

}
