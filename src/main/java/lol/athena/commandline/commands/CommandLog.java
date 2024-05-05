package lol.athena.commandline.commands;

import java.awt.Color;
import java.util.List;

import lol.athena.Athena;
import lol.athena.commandline.CommandLineCommand;
import net.dv8tion.jda.api.EmbedBuilder;

public class CommandLog extends CommandLineCommand {

	public CommandLog(Athena bot, String name, List<String> aliases) {
		super(bot, name, aliases);
	}

	@Override
	public boolean execute(String alias, String[] args) {
		if (args.length < 1) {
			getLogger().info("Specify a log message...");
			return true;
		}
		
		StringBuilder sb = new StringBuilder();
		for (String string : args) {
			sb.append(string + " ");
		}
		
		String message = sb.toString().substring(0, sb.length() - 1);
		
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setColor(Color.yellow);
		embedBuilder.setAuthor("Athena System Message", "https://www.youtube.com/channel/UCw07W7dt5vR9VXqIGb3tIJA", getAthena().getJda().getSelfUser().getAvatarUrl());
		embedBuilder.setFooter("Athena version " + Athena.BUILD, getAthena().getJda().getSelfUser().getAvatarUrl());

		embedBuilder.addField("Athena System Log", message, false);
		
		getAthena().getJda().getNewsChannelById(getAthena().getConfig().getString("ids.announcements", "1093256643778723921"))
			.sendMessageEmbeds(embedBuilder.build()).queue();
		
		getLogger().info("&aLog sent with the following message: \"&c" + message + "&a\".");
		
		return true;
	}

}
