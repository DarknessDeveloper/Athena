package lol.athena.plugin;

import lombok.Getter;

public class PluginException extends RuntimeException {
	private static final long serialVersionUID = 8162255599411914447L;
	@Getter private Plugin plugin;
	
	public PluginException(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public PluginException(Plugin plugin, String message) {
		super(message);
		this.plugin = plugin;
	}
}
