package lol.athena.plugin.naming;

import lol.athena.plugin.Plugin;

public class PluginNamespacedKey extends NamespacedKey {

	public PluginNamespacedKey(Plugin plugin, String value) {
		super(plugin.getName().toLowerCase().trim().replace(" ", "_"), value);
	}

}
