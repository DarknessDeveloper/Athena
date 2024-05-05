package lol.athena.plugin;

import java.io.InputStreamReader;
import java.util.logging.Level;

import org.bukkit.configuration.file.YamlConfiguration;

public final class PluginLanguageFile {


	public static void autoLoadDefaults(Plugin plugin, YamlConfiguration config) {
		try {
			config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("lang.yml"))));
		} catch (Exception ex) {
			ex.printStackTrace();
			plugin.getLogger().log(Level.SEVERE, String.format("Unable to load language defaults from plugin jar for plugin '%s'", plugin.getName()), ex);
		}
	}

}
