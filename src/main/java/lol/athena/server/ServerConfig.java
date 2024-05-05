package lol.athena.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lol.athena.Athena;
import lombok.AccessLevel;
import lombok.Getter;

public class ServerConfig {

	@Getter(value = AccessLevel.PROTECTED) private Gson serializer;
	
	private ServerConfig(String id, boolean prettyPrint) {
		serializer = prettyPrint ? new GsonBuilder().setPrettyPrinting().create() : new GsonBuilder().create();
		serverId = id;
	}
	
	@Getter private String serverId;
	
	@Getter private List<String> enabledPlugins;
	@Getter private Map<String, ServerPluginConfig> pluginConfigs;
	
	
	public String serializeToString() {
		return serializer.toJson(this);
	}
	
	public static ServerConfig create(String serverId) {
		return new ServerConfig(serverId, false);
	}
	
	public static ServerConfig pretty(String serverId) {
		return new ServerConfig(serverId, true);
	}
	
	public void save() {
		Map<String, String> pluginJson = new HashMap<>();
		pluginConfigs.forEach((plugin, config) -> {
			try {
				String jsonData = config.serializeToJsonString();
				pluginJson.put(plugin, jsonData);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		
		String json = serializer.toJson(pluginJson);
		Athena.getInstance().getStorageEngine().saveServerConfig(serverId, json);
	}
}
