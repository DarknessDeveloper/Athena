package lol.athena.server;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ServerPluginConfig {

	protected ServerPluginConfig() {
		
	}
	
	@Getter @Setter(value = AccessLevel.PROTECTED) private ServerConfig serverConfig;
	
	@Getter private String plugin;
	@Getter private Map<String, Object> configVals;
	
	@NotNull
	public String serializeToJsonString() {
		return serverConfig.getSerializer().toJson(this);
	}
	
	public Object get(String key) {
		return get(key, null);
	}
	
	public Object get(String key, Object def) {
		return configVals.containsKey(key) ? configVals.get(key) : def;
	}
	
	@Override
	public String toString() {
		return serializeToJsonString();
	}
}
