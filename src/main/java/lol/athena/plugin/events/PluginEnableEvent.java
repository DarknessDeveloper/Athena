package lol.athena.plugin.events;

import lol.athena.plugin.Plugin;
import net.dv8tion.jda.api.JDA;

public class PluginEnableEvent extends PluginEvent {
	
	public PluginEnableEvent(JDA api, Plugin plugin) {
		super(api, plugin);
	}

}
