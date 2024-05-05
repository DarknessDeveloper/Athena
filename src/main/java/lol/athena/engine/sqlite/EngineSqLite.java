package lol.athena.engine.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.CheckForSigned;

import com.google.common.collect.Lists;

import lol.athena.Athena;
import lol.athena.AthenaRuntimeException;
import lol.athena.engine.SqlEngine;
import lol.athena.plugin.Plugin;
import lol.athena.plugin.naming.NamespacedKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

@RequiredArgsConstructor
public class EngineSqLite implements SqlEngine {

	@Getter private final Athena athena;
	@Getter private final String database;

	private final String allowedSqlCharacters = "qwertyuiopasdfghjklzxcvbnm_-QWERTYUIOPASDFGHJKLZXCVBNM0123456789";
	
	private NamespacedKey namespace = new NamespacedKey("athena", "engine_sqlite");
	private Connection connection;

	@Override
	public boolean connect() {
		try {
			QueriesLite.DB_NAME = database;
			String url = "jdbc:sqlite:" + new File(athena.getConfig().getString("sqlite.file", database + ".db")).getAbsolutePath();
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(url);
			
//			prepare("CREATE DATABASE IF NOT EXISTS `athena`");
			
			return connection != null && !connection.isClosed();
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public void close() {
		try {
			connection.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new AthenaRuntimeException("Failed to close SQLite database connection(s)!");
		}
	}

	@Override
	public void createNonexistantTables() {
		String[] tableQueries = { QueriesLite.CREATE_TABLE_ACTIVATION, QueriesLite.CREATE_TABLE_PLUGINS, QueriesLite.CREATE_TABLE_PERMISSIONS, QueriesLite.CREATE_TABLE_ROLES, QueriesLite.CREATE_TABLE_ROLE_PERMISSIONS, QueriesLite.CREATE_TABLE_ROLE_USERS };

		for (String query : tableQueries) {
			try {
				PreparedStatement statement = prepare(query);
				statement.executeUpdate();
			} catch (SQLException | NullPointerException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public boolean isPluginActiveInGuild(Plugin plugin, String guildId) {
		return isPluginActiveInGuild(plugin.getName(), guildId);
	}

	@Override
	public boolean isPluginActiveInGuild(String name, String guildId) {
		return isPluginActiveInGuild(getPluginIdFromName(name), guildId);
	}

	@Override
	public boolean isPluginActiveInGuild(int id, String guildId) {
		if (isBanned(id)) {
			return false;
		}

		try {
			PreparedStatement statement = prepare("SELECT * FROM activations WHERE plugin_id = ? AND guild = ?");
			statement.setInt(1, id);
			statement.setString(2, guildId);
			ResultSet results = statement.executeQuery();
			return results.next();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean activate(Plugin plugin, String guild) {
		return activate(plugin.getName(), guild);
	}

	@Override
	public boolean activate(String name, String guild) {
		return activate(getPluginIdFromName(name), guild);
	}

	@Override
	public boolean activate(int id, String guild) {
		if (isPluginActiveInGuild(id, guild)) {
			return true;
		}
		if (isBanned(id)) {
			return false;
		}

		try {
			PreparedStatement statement = prepare("INSERT INTO activations (plugin_id, guild) VALUES (?, ?);");
			statement.setInt(1, id);
			statement.setString(2, guild);

			int affected = statement.executeUpdate();
			return affected > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deactivate(Plugin plugin, String guild) {
		return deactivate(plugin.getName(), guild);
	}

	@Override
	public boolean deactivate(String name, String guild) {
		return deactivate(getPluginIdFromName(name), guild);
	}

	@Override
	public boolean deactivate(int id, String guild) {
		if (!isPluginActiveInGuild(id, guild)) {
			return true;
		}

		try {
			PreparedStatement statement = prepare("DELETE FROM activations WHERE plugin_id = ? AND guild = ?;");
			statement.setInt(1, id);
			statement.setString(2, guild);

			int affected = statement.executeUpdate();
			return affected > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean isPluginRegistered(Plugin plugin) {
		return isPluginRegistered(plugin.getName());
	}

	@Override
	public boolean isPluginRegistered(String name) {
		return isPluginRegistered(getPluginIdFromName(name));
	}

	@Override
	public boolean isPluginRegistered(int id) {
		try {
			PreparedStatement statement = prepare("SELECT * FROM plugins WHERE id = ?;");
			statement.setInt(1, id);
			ResultSet results = statement.executeQuery();
			return results.next();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean registerPlugin(Plugin plugin) {
		if (isPluginRegistered(getPluginIdFromName(plugin.getName()))) {
			return true;
		}

		try {
			PreparedStatement statement = prepare("INSERT INTO plugins (name, description, version) VALUES (?, ?, ?);");
			statement.setString(1, plugin.getName());
			statement.setString(2, "No description provided.");
			statement.setString(3, plugin.getDescription().getVersion());

			return statement.executeUpdate() > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean unregisterPlugin(Plugin plugin) {
		return unregisterPlugin(plugin.getName());
	}

	@Override
	public boolean unregisterPlugin(String name) {
		return unregisterPlugin(getPluginIdFromName(name));
	}

	@Override
	public boolean unregisterPlugin(int id) {
		if (!isPluginRegistered(id)) {
			return true;
		}

		try {
			PreparedStatement statement = prepare("DELETE FROM plugins WHERE id = ?;");
			statement.setInt(1, id);

			return statement.executeUpdate() > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean isBanned(Plugin plugin) {
		return isBanned(plugin.getName());
	}

	@Override
	public boolean isBanned(String name) {
		return isBanned(getPluginIdFromName(name));
	}

	@Override
	public boolean isBanned(int id) {
		try {
			PreparedStatement statement = prepare("SELECT * FROM plugins WHERE banned = 1 AND id = ?;");
			statement.setInt(1, id);

			return statement.executeQuery().next();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean banPlugin(Plugin plugin) {
		return banPlugin(plugin.getName());
	}

	@Override
	public boolean banPlugin(String name) {
		return banPlugin(getPluginIdFromName(name));
	}

	@Override
	public boolean banPlugin(int id) {
		try {
			PreparedStatement statement = prepare("UPDATE plugins SET banned = 1 WHERE id = ?;");
			statement.setInt(1, id);

			return statement.executeUpdate() > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean unbanPlugin(Plugin plugin) {
		return unbanPlugin(plugin.getName());
	}

	@Override
	public boolean unbanPlugin(String name) {
		return unbanPlugin(getPluginIdFromName(name));
	}

	@Override
	public boolean unbanPlugin(int id) {
		try {
			PreparedStatement statement = prepare("UPDATE plugins SET banned = 0 WHERE id = ?;");
			statement.setInt(1, id);

			return statement.executeUpdate() > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public String getPluginDescription(Plugin plugin) {
		return getPluginDescription(plugin.getName());
	}

	@Override
	public String getPluginDescription(String name) {
		return getPluginDescription(getPluginIdFromName(name));
	}

	@Override
	public String getPluginDescription(int id) {
		try {
			PreparedStatement statement = prepare("SELECT * FROM plugins WHERE id = ?;");
			statement.setInt(1, id);
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return results.getString("description");
			}
			return null;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	@CheckForSigned
	public int getPluginIdFromName(String name) {
		try {
			PreparedStatement statement = prepare("SELECT * FROM plugins WHERE name = ?;");
			statement.setString(1, name);
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return results.getInt("id");
			}
			return -1;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	@Override
	public String getPluginNameFromId(int id) {
		try {
			PreparedStatement statement = prepare("SELECT * FROM plugins WHERE id = ?;");
			statement.setInt(1, id);
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return results.getString("name");
			}
			return null;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public List<String> getGuildPluginInformation(String guildId) {
		try {
			PreparedStatement statement = prepare("SELECT * FROM activations WHERE guild = ?;");
			statement.setString(1, guildId);

			ResultSet result = statement.executeQuery();
			List<String> pluginNames = new ArrayList<>();
			while (result.next()) {
				int id = result.getInt("plugin_id");
				if (!isBanned(id)) {
					pluginNames.add(getPluginNameFromId(id));
				}
			}

			return pluginNames;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return Lists.newArrayList();
		}
	}

	@Override
	public boolean isCachingEnabled() {
		return true;
	}

	@Override
	public PreparedStatement prepare(String query) {
		try {
			return getConnection().prepareStatement(query);
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public PreparedStatement prepare(String query, Object... args) {
		try {
			PreparedStatement statement = prepare(query);
			for (int i = 0; i < args.length; i++) {
				statement.setObject(i + 1, args[i]);
			}

			return statement;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public String escapeString(String string) {
		return escapeString(string, false);
	}

	public String escapeString(String string, boolean useEncode) {
		if (useEncode) {
			return e_encode(string);
		}

		StringBuilder builder = new StringBuilder();
		for (char character : string.toCharArray()) {
			if (allowedSqlCharacters.indexOf(character) != -1) {
				builder.append(character);
			}
		}
		return builder.toString();
	}

	private String e_encode(String unencoded) {
		StringBuilder builder = new StringBuilder();

		for (char character : unencoded.toCharArray()) {
			if (allowedSqlCharacters.indexOf(character) == -1) {
				builder.append('\\');
			}
			builder.append(character);
		}

		return builder.toString();
	}

	@Override
	public boolean hasPermission(User user, Guild guild, String permission) {
		return hasPermission(user.getId(), guild.getId(), permission);
	}

	@Override
	public boolean hasPermission(User user, String guild, String permission) {
		return hasPermission(user.getId(), guild, permission);
	}

	@Override
	public boolean hasPermission(String user, Guild guild, String permission) {
		return hasPermission(user, guild.getId(), permission);
	}

	@Override
	public boolean hasPermission(String user, String guild, String permission) {
		try {
			PreparedStatement statement = prepare("SELECT * FROM permissions WHERE user_id = ? AND guild_id = ? AND permission = ?;");
			statement.setString(1, user);
			statement.setString(2, guild);
			statement.setString(3, permission);

			return statement.executeQuery().next();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public Collection<String> getPermissions(User user, Guild guild) {
		return getPermissions(user.getId(), guild.getId());
	}

	@Override
	public Collection<String> getPermissions(User user, String guild) {
		return getPermissions(user.getId(), guild);
	}

	@Override
	public Collection<String> getPermissions(String user, Guild guild) {
		return getPermissions(user, guild.getId());
	}

	@Override
	public Collection<String> getPermissions(String user, String guild) {
		try {
			List<String> permissions = new ArrayList<>();

			PreparedStatement statement = prepare("SELECT * FROM permissions WHERE user_id = ? AND guild_id = ?;");
			statement.setString(1, user);
			statement.setString(2, guild);

			var results = statement.executeQuery();
			while (results.next()) {
				permissions.add(results.getString("permission"));
			}

			return permissions;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return Lists.newArrayList();
		}
	}

	@Override
	public boolean grantPermission(User user, Guild guild, String permission) {
		return grantPermission(user.getId(), guild.getId(), permission);
	}

	@Override
	public boolean grantPermission(User user, String guild, String permission) {
		return grantPermission(user.getId(), guild, permission);
	}

	@Override
	public boolean grantPermission(String user, Guild guild, String permission) {
		return grantPermission(user, guild.getId(), permission);
	}

	@Override
	public boolean grantPermission(String user, String guild, String permission) {
		if (hasPermission(permission, guild, permission)) {
			return false;
		}

		try {
			PreparedStatement statement = prepare("INSERT INTO permissions (user_id, guild_id, permission) VALUES (?, ?, ?);");
			statement.setString(1, user);
			statement.setString(2, guild);
			statement.setString(3, permission);

			return statement.executeUpdate() > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean takePermission(User user, Guild guild, String permission) {
		return takePermission(user.getId(), guild.getId(), permission);
	}

	@Override
	public boolean takePermission(User user, String guild, String permission) {
		return takePermission(user.getId(), guild, permission);
	}

	@Override
	public boolean takePermission(String user, Guild guild, String permission) {
		return takePermission(user, guild.getId(), permission);
	}

	@Override
	public boolean takePermission(String user, String guild, String permission) {
		try {
			PreparedStatement statement = prepare("DELETE FROM permissions WHERE user_id = ? AND guild_id = ? AND permission = ?;");
			statement.setString(1, user);
			statement.setString(2, guild);
			statement.setString(3, permission);

			return statement.executeUpdate() > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean grantPermissions(User user, Guild guild, String... permissions) {
		return grantPermissions(user.getId(), guild.getId(), permissions);
	}

	@Override
	public boolean grantPermissions(User user, String guild, String... permissions) {
		return grantPermissions(user.getId(), guild, permissions);
	}

	@Override
	public boolean grantPermissions(String user, Guild guild, String... permissions) {
		return grantPermissions(user, guild.getId(), permissions);
	}

	@Override
	public boolean grantPermissions(String user, String guild, String... permissions) {
		List<String> toGrant = new ArrayList<>();
		for (String permission : permissions) {
			if (!hasPermission(user, guild, permission)) {
				toGrant.add(permission);
			}
		}

		if (toGrant.size() < 1) {
			return true;
		}

		StringBuilder queryBuilder = new StringBuilder("INSERT INTO permissions (user_id, guild_id, permission) VALUES ");
		toGrant.forEach(permission -> {
			queryBuilder.append("(?, ?, ?), ");
		});

		String query = queryBuilder.substring(0, queryBuilder.length() - 2) + ";";
		try {
			PreparedStatement statement = prepare(query);
			for (int i = 0; i < toGrant.size(); i++) {
				statement.setString((i * 3) + 1, user);
				statement.setString((i * 3) + 2, guild);
				statement.setString((i * 3) + 3, toGrant.get(i));
			}

			return statement.executeUpdate() > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean takePermissions(User user, Guild guild, String... permissions) {
		return takePermissions(user.getId(), guild.getId(), permissions);
	}

	@Override
	public boolean takePermissions(User user, String guild, String... permissions) {
		return takePermissions(user.getId(), guild, permissions);
	}

	@Override
	public boolean takePermissions(String user, Guild guild, String... permissions) {
		return takePermissions(user, guild.getId(), permissions);
	}

	@Override
	public boolean takePermissions(String user, String guild, String... permissions) {
		String query = "DELETE FROM permissions WHERE user_id = ? AND guild_id = ? AND permission = ?;";
		int affected = 0;

		for (String permission : permissions) {
			try {
				PreparedStatement statement = prepare(query);

				statement.setString(1, user);
				statement.setString(2, guild);
				statement.setString(3, permission);
				affected += statement.executeUpdate();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return affected > 0;
	}

	@Override
	public boolean hasRole(User user, Guild guild, int id) {
		return hasRole(user.getId(), guild.getId(), id);
	}

	@Override
	public boolean hasRole(User user, String guild, int id) {
		return hasRole(user.getId(), guild, id);
	}

	@Override
	public boolean hasRole(String user, Guild guild, int id) {
		return hasRole(user, guild.getId(), id);
	}

	@Override
	public boolean hasRole(String user, String guild, int id) {
		try {
			PreparedStatement statement = prepare("SELECT * FROM role_users WHERE user_id = ? AND guild_id = ? AND role_id = ?;");
			statement.setString(1, user);
			statement.setString(2, guild);
			statement.setInt(3, id);

			return statement.executeQuery().next();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public Collection<String> getRoles(User user, Guild guild) {
		return getRoles(user.getId(), guild.getId());
	}

	@Override
	public Collection<String> getRoles(User user, String guild) {
		return getRoles(user.getId(), guild);
	}

	@Override
	public Collection<String> getRoles(String user, Guild guild) {
		return getRoles(user, guild.getId());
	}

	@Override
	public Collection<String> getRoles(String user, String guild) {
		List<String> roles = new ArrayList<>();

		try {
			PreparedStatement statement = prepare("SELECT * FROM role_users WHERE user_id = ? AND guild_id = ?;");
			statement.setString(1, user);
			statement.setString(2, guild);

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				roles.add(getRoleName(results.getInt("role_id")));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return roles;
	}

	@Override
	public Collection<String> getRolePermissions(int id, Guild guild) {
		return getRolePermissions(id, guild.getId());
	}

	@Override
	public Collection<String> getRolePermissions(int id, String guild) {
		List<String> permissions = new ArrayList<>();
		try {
			PreparedStatement statement = prepare("SELECT * FROM role_permissions WHERE role_id = ? AND guild_id = ?;");
			statement.setInt(1, id);
			statement.setString(2, guild);

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				permissions.add(results.getString("permission"));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return permissions;
	}

	@Override
	public boolean grantRole(User user, Guild guild, int id) {
		return grantRole(user.getId(), guild.getId(), id);
	}

	@Override
	public boolean grantRole(User user, String guild, int id) {
		return grantRole(user.getId(), guild, id);
	}

	@Override
	public boolean grantRole(String user, Guild guild, int id) {
		return grantRole(user, guild.getId(), id);
	}

	@Override
	public boolean grantRole(String user, String guild, int id) {
		if (hasRole(user, guild, id)) {
			return true;
		}

		try {
			PreparedStatement statement = prepare("INSERT INTO role_users (role_id, user_id, guild_id) VALUES (?, ?, ?);");
			statement.setInt(1, id);
			statement.setString(2, user);
			statement.setString(3, guild);

			return statement.executeUpdate() > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean takeRole(User user, Guild guild, int id) {
		return takeRole(user.getId(), guild.getId(), id);
	}

	@Override
	public boolean takeRole(User user, String guild, int id) {
		return takeRole(user.getId(), guild, id);
	}

	@Override
	public boolean takeRole(String user, Guild guild, int id) {
		return takeRole(user, guild.getId(), id);
	}

	@Override
	public boolean takeRole(String user, String guild, int id) {
		if (!hasRole(user, guild, id)) {
			return true;
		}

		try {
			PreparedStatement statement = prepare("DELETE FROM role_users WHERE role_id = ? AND user_id = ? AND guild_id = ?;");
			statement.setInt(1, id);
			statement.setString(2, user);
			statement.setString(3, guild);

			return statement.executeUpdate() > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean hasRolePermission(int id, Guild guild, String permission) {
		return hasRolePermission(id, guild.getId(), permission);
	}

	@Override
	public boolean hasRolePermission(int id, String guild, String permission) {
		try {
			PreparedStatement statement = prepare("SELECT * FROM role_permissions WHERE role_id = ? AND guild_id = ? AND permission = ?;");
			statement.setInt(1, id);
			statement.setString(2, guild);
			statement.setString(3, permission);

			return statement.executeQuery().next();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean grantRolePermission(int id, Guild guild, String permission) {
		return grantRolePermission(id, guild.getId(), permission);
	}

	@Override
	public boolean grantRolePermission(int id, String guild, String permission) {
		if (hasRolePermission(id, guild, permission)) {
			return true;
		}

		try {
			PreparedStatement statement = prepare("INSERT INTO role_permissions (role_id, guild_id, permission) VALUES (?, ?, ?);");
			statement.setInt(1, id);
			statement.setString(2, guild);
			statement.setString(3, permission);

			return statement.executeUpdate() > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean takeRolePermission(int id, Guild guild, String permission) {
		return takeRolePermission(id, guild.getId(), permission);
	}

	@Override
	public boolean takeRolePermission(int id, String guild, String permission) {
		if (!hasRolePermission(id, guild, permission)) {
			return true;
		}

		try {
			PreparedStatement statement = prepare("DELETE FROM role_permissions WHERE role_id = ? AND guild_id = ? AND permission = ?;");
			statement.setInt(1, id);
			statement.setString(2, guild);
			statement.setString(3, permission);

			return statement.executeUpdate() > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean grantRolePermissions(int id, Guild guild, String... permissions) {
		return grantRolePermissions(id, guild.getId(), permissions);
	}

	@Override
	public boolean grantRolePermissions(int id, String guild, String... permissions) {
		List<String> toGrant = new ArrayList<>();
		for (String permission : permissions) {
			if (!hasRolePermission(id, guild, permission)) {
				toGrant.add(permission);
			}
		}

		if (toGrant.size() < 1) {
			return true;
		}

		StringBuilder queryBuilder = new StringBuilder("INSERT INTO role_permissions (role_id, guild_id, permission) VALUES ");
		toGrant.forEach(permission -> {
			queryBuilder.append("(?, ?, ?), ");
		});

		String query = queryBuilder.substring(0, queryBuilder.length() - 2) + ";";
		try {
			PreparedStatement statement = prepare(query);
			for (int i = 0; i < toGrant.size(); i++) {
				statement.setInt((i * 3) + 1, id);
				statement.setString((i * 3) + 2, guild);
				statement.setString((i * 3) + 3, toGrant.get(i));
			}

			return statement.executeUpdate() > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean takeRolePermissions(int id, Guild guild, String... permissions) {
		return takeRolePermissions(id, guild.getId(), permissions);
	}

	@Override
	public boolean takeRolePermissions(int id, String guild, String... permissions) {
		String query = "DELETE FROM role_permissions WHERE role_id = ? AND guild_id = ? AND permission = ?;";
		int affected = 0;

		for (String permission : permissions) {
			try {
				PreparedStatement statement = prepare(query);

				statement.setInt(1, id);
				statement.setString(2, guild);
				statement.setString(3, permission);
				affected += statement.executeUpdate();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return affected > 0;
	}

	@Override
	public String getRoleName(int id) {
		try {
			PreparedStatement statement = prepare("SELECT * FROM roles WHERE id = ?;");
			statement.setInt(1, id);

			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return results.getString("name");
			}

			return null;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public int getRoleId(String name) {
		try {
			PreparedStatement statement = prepare("SELECT * FROM roles WHERE name = ?;");
			statement.setString(1, name);

			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return results.getInt("id");
			}

			return -1;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	@Override
	public NamespacedKey getNamespace() {
		return namespace;
	}
	

	@Override
	public void saveServerConfig(Guild guild, String json) {
		saveServerConfig(guild.getId(), json);
	}

	@Override
	public void saveServerConfig(String id, String json) {
		try {
			if (!doesServerHaveConfig(id)) {
				PreparedStatement statement = prepare("INSERT INTO configs (guild_id, json) VALUES (?, ?);");
				statement.setString(1, id);
				statement.setString(2, json);
				statement.executeUpdate();
				return;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public String retrieveServerConfig(Guild guild) {
		return retrieveServerConfig(guild.getId());
	}

	@Override
	public String retrieveServerConfig(String id) {
		if (!doesServerHaveConfig(id)) {
			return null;
		}
		
		try {
			PreparedStatement statement = prepare("SELECT * FROM configs WHERE guild_id = ?;");
			statement.setString(1, id);
			
			ResultSet results = statement.executeQuery();
			if (!results.next()) {
				return null;
			}
			
			return results.getString("json");
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean doesServerHaveConfig(Guild guild) {
		return doesServerHaveConfig(guild.getId());
	}

	@Override
	public boolean doesServerHaveConfig(String id) {
		try {
			PreparedStatement statement = prepare("SELECT * FROM configs WHERE guild_id = ?;");
			statement.setString(1, id);
			
			ResultSet results = statement.executeQuery();
			return results.next();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	@Override
	public Collection<Guild> getActiveGuilds(Plugin plugin) {
		return getActiveGuilds(getPluginIdFromName(plugin.getName()));
	}
	
	@Override
	public Collection<Guild> getActiveGuilds(int id) {
		System.out.println(id);
		try {
			PreparedStatement statement = prepare("SELECT * FROM activations WHERE plugin_id = ?;");
			statement.setInt(1, id);

			ResultSet results = statement.executeQuery();
			List<Guild> guilds = new ArrayList<>();

			while (results.next()) {
				try {
					guilds.add(Athena.getInstance().getJda().getGuildById(results.getString("guild_id")));
				} catch (Exception ex) {
					continue;
				}
			}
			
			return guilds;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return Lists.newArrayList();
		}
	}
}
