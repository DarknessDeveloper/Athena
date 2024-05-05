package lol.athena;

import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.collect.Lists;

public class Lang {

	private static File langFile;
	private static FileConfiguration config;

	private Lang() {

	}

	public static void init() {
		langFile = new File("lang.yml");

		if (!langFile.exists()) {
			Athena.getInstance().saveResource(langFile.getName(), false);
		}

		config = YamlConfiguration.loadConfiguration(langFile);
		config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(Athena.getInstance().getResource(langFile.getName()))));
	}

	public static String get(String key) {
		return config.getString(key, "(Error: No language file entry for key '" + key + "')");
	}

	public static String get(String key, Object... replacements) {
		String finalString = get(key);

		for (int i = 0; i < replacements.length; i++) {
			if (finalString.contains("{" + i + "}")) {
				finalString = finalString.replace("{" + i + "}", String.valueOf(replacements[i]));
			}
		}

		return finalString;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> list(String key) {
		try {
			return (List<T>) config.getList(key);
		} catch (ClassCastException ex) {
			return Lists.newArrayList();
		}
	}
}
