package lol.athena.plugin.events;

import lol.athena.plugin.Plugin;
import net.dv8tion.jda.api.JDA;

public class PluginLoadEvent extends PluginEvent {
	
	public PluginLoadEvent(JDA api, Plugin plugin) {
		super(api, plugin);
	}

}
