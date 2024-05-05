package lol.athena.engine;

import java.util.Collection;
import java.util.List;

import lol.athena.plugin.Plugin;
import lol.athena.plugin.naming.NamespacedKey;
import net.dv8tion.jda.api.entities.Guild;

/**
 * Provides methods for storing and retrieving data from a database.
 * 
 * @author DarknessDev
 * @version 1.0
 * 
 * @since Athena 0.1
 */
public interface Engine extends PermissionEngine {
	
	boolean connect();
	void close();
	void createNonexistantTables();
	
	
	boolean isPluginActiveInGuild(Plugin plugin, String guildId);
	boolean isPluginActiveInGuild(String name, String guildId);
	boolean isPluginActiveInGuild(int id, String guildId);
	
	Collection<Guild> getActiveGuilds(Plugin plugin);
	Collection<Guild> getActiveGuilds(int id);
	
	boolean activate(Plugin plugin, String guild);
	boolean activate(String name, String guild);
	boolean activate(int id, String guild);
	
	boolean deactivate(Plugin plugin, String guild);
	boolean deactivate(String name, String guild);
	boolean deactivate(int id, String guild);
	
	boolean isPluginRegistered(Plugin plugin);
	boolean isPluginRegistered(String name);
	boolean isPluginRegistered(int id);
	
	boolean registerPlugin(Plugin plugin);
	
	boolean unregisterPlugin(Plugin plugin);
	boolean unregisterPlugin(String name);
	boolean unregisterPlugin(int id);
	
	boolean isBanned(Plugin plugin);
	boolean isBanned(String name);
	boolean isBanned(int id);
	
	boolean banPlugin(Plugin plugin);
	boolean banPlugin(String name);
	boolean banPlugin(int id);
	
	boolean unbanPlugin(Plugin plugin);
	boolean unbanPlugin(String name);
	boolean unbanPlugin(int id);
	
	String getPluginDescription(Plugin plugin);
	String getPluginDescription(String name);
	String getPluginDescription(int id);
	
	int getPluginIdFromName(String name);
	String getPluginNameFromId(int id);	
	List<String> getGuildPluginInformation(String guildId);
	
	void saveServerConfig(Guild guild, String json);
	void saveServerConfig(String id, String json);
	
	String retrieveServerConfig(Guild guild);
	String retrieveServerConfig(String id);
	
	boolean doesServerHaveConfig(Guild guild);
	boolean doesServerHaveConfig(String id);
	
	boolean isCachingEnabled();
	
	NamespacedKey getNamespace();
}
