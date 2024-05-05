package lol.athena.plugin.events;

import lol.athena.Athena;
import lol.athena.plugin.Plugin;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;

public abstract class PluginEvent extends Event {
	
	@Getter private Plugin plugin;
	
	public PluginEvent(JDA api, Plugin plugin) {
		super(api);
		
		this.plugin = plugin;
	}
	
	public PluginEvent(Plugin plugin) {
		this(Athena.getInstance().getJda(), plugin);
	}
	
	public void call() {
		Athena.getInstance().getEventListener().callPluginEvent(this);
	}
	
	/**
	 * This method exists for modifying event information post-execution.
	 */
	public void postCall() {
		
	}
}
