package lol.athena.plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.file.YamlConfiguration;

import lol.athena.Athena;
import lombok.Getter;

public class PluginDescriptionFile {
	@Getter private YamlConfiguration descriptionFile;
	
	@Getter private String main;
	@Getter private String name;
	@Getter private String version;
	@Getter private List<String> authors;
	@Getter private List<String> dependencies;
	//@Getter private List<String> softDependencies; // TODO Implement this in a future release.
	
	@Getter private int minApiVersion = 0;
	
	protected PluginDescriptionFile(InputStream input) {
		descriptionFile = YamlConfiguration.loadConfiguration(new InputStreamReader(input));
		
		main = descriptionFile.getString("main");
		name = descriptionFile.getString("name");
		version = descriptionFile.getString("version");
		
		authors = descriptionFile.getStringList("authors");
		minApiVersion = descriptionFile.getInt("api-version", Athena.API_VERSION);
		
		dependencies = descriptionFile.getStringList("depend");
		dependencies.addAll(descriptionFile.getStringList("depends"));
		
		Validate.notNull(main, "Plugin main class is not defined.");
		Validate.notNull(name, "Plugin name cannot be null.");
		Validate.notNull(version, "Plugin version not specified.");
		Validate.isTrue(minApiVersion >= 0, "Minimum API Version must be a positive integer.");
	}
}
