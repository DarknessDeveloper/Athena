package lol.athena.plugin.events;

import lol.athena.plugin.Plugin;
import net.dv8tion.jda.api.JDA;

public class PluginDisableEvent extends PluginEvent {
	public PluginDisableEvent(JDA api, Plugin plugin) {
		super(api, plugin);
	}	
}
