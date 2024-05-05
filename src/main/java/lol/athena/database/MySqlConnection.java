package lol.athena.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lol.athena.Athena;
import lol.athena.engine.sql.Queries;
import lol.athena.plugin.Plugin;
import lombok.Getter;

@Deprecated(forRemoval = true)
/**
 */
public class MySqlConnection {

	@Getter private final Athena athena;
	@Getter private Connection connection;
	@Getter private final String host;
	@Getter private final String database;

	private final String username;
	private final String password;

	public MySqlConnection(Athena athena, String host, String database, String username, String password) {
		this.athena = athena;
		this.host = host;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	public boolean connect() throws SQLException {
		try {
			athena.getLogger().warning("&6You are using out-of-date legacy database code. Consider switching to `lol.athena.engine` for future database operations.");
			Queries.DB_NAME = database;
			connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?user=" + username + (password == null ? "" : ("&password=" + password)));

			return connection != null && connection.isValid(10);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public void close() throws SQLException {
		if (connection == null || connection.isClosed()) {
			return;
		}

		connection.close();
	}

	@Deprecated
	public void makeTables() throws SQLException {

		Athena.debug("Using database " + Queries.DB_NAME);

		// Licenses Table
		Athena.debug("Creating License table: " + Queries.LICENSES_TABLE_NAME);
		PreparedStatement makeLicenses = connection.prepareStatement(Queries.CREATE_TABLE_LICENSES);
		makeLicenses.executeUpdate();

		// Modules Table
		Athena.debug("Creating Modules table: " + Queries.PLUGINS_TABLE_NAME);
		PreparedStatement makeModules = connection.prepareStatement(Queries.CREATE_TABLE_PLUGINS);
		makeModules.executeUpdate();

		// Module Active Guilds Table
		Athena.debug("Creating Guild Active Plugins table: " + Queries.ACTIVATIONS_TABLE_NAME);
		PreparedStatement makeGAModules = connection.prepareStatement(Queries.CREATE_TABLE_ACTIVATIONS);
		makeGAModules.executeUpdate();

		// Permissions Table
		Athena.debug("Creating Permissions table: " + Queries.PERMISSIONS_TABLE_NAME);
		PreparedStatement makePermissions = connection.prepareStatement(Queries.CREATE_TABLE_PERMISSIONS);
		makePermissions.executeUpdate();
	}

	public boolean isPluginActive(int pluginId, String guild) {
		if (isBanned(pluginId)) {
			return false;
		}

		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + Queries.ACTIVATIONS_TABLE_NAME + " WHERE plugin_id = ? AND guild = ?;");
			statement.setInt(1, pluginId);
			statement.setString(2, guild);

			ResultSet results = statement.executeQuery();
			return results.next();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public int getPluginIdFromName(String name) {
		try {
			PreparedStatement fetchStatement = connection.prepareStatement("SELECT * FROM " + Queries.PLUGINS_TABLE_NAME + " WHERE name = ?;");
			fetchStatement.setString(1, name);

			ResultSet results = fetchStatement.executeQuery();
			if (!results.next()) {
				return -1;
			}

			return results.getInt("id");
		} catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	public String getPluginNameFromId(int id) {
		try {
			PreparedStatement fetchStatement = connection.prepareStatement("SELECT * FROM " + Queries.PLUGINS_TABLE_NAME + " WHERE id = ?;");
			fetchStatement.setInt(1, id);

			ResultSet results = fetchStatement.executeQuery();
			if (!results.next()) {
				return null;
			}

			return results.getString("name");
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public String getPluginDescription(String name) {
		int id = getPluginIdFromName(name);
		if (id == -1) {
			return "Nonexistant or not registered plugin.";
		}

		try {
			PreparedStatement fetchStatement = connection.prepareStatement("SELECT * FROM " + Queries.PLUGINS_TABLE_NAME + " WHERE id = ?;");
			fetchStatement.setInt(1, getPluginIdFromName(name));

			ResultSet results = fetchStatement.executeQuery();
			if (!results.next()) {
				return "Nonexistant or not registered plugin.";
			}

			return results.getString("description");
		} catch (SQLException ex) {
			ex.printStackTrace();
			return "error fetching description";
		}
	}

	public boolean doesPluginIdExist(int id) {
		try {
			PreparedStatement fetchStatement = connection.prepareStatement("SELECT * FROM " + Queries.PLUGINS_TABLE_NAME + " WHERE id = ?;");
			fetchStatement.setInt(1, id);

			ResultSet results = fetchStatement.executeQuery();
			return results.next();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean isActiveInGuild(int plugin, String guild) {
		if (isBanned(plugin)) {
			return false;
		}
		try {
			PreparedStatement fetchStatement = connection.prepareStatement("SELECT * FROM " + Queries.ACTIVATIONS_TABLE_NAME + " WHERE plugin_id = ? AND guild = ?;");
			fetchStatement.setInt(1, plugin);
			fetchStatement.setString(2, guild);

			ResultSet results = fetchStatement.executeQuery();
			return results.next();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean activateById(int pluginId, String guild) {
		if (isActiveInGuild(pluginId, guild)) {
			return true;
		}
		if (isBanned(pluginId)) {
			return false;
		}

		try {
			PreparedStatement statement = connection.prepareStatement("INSERT INTO " + Queries.ACTIVATIONS_TABLE_NAME + " (plugin_id, guild) VALUES (?, ?);");
			statement.setInt(1, pluginId);
			statement.setString(2, guild);

			int affected = statement.executeUpdate();
			return affected > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean deactivateById(int pluginId, String guild) {
		if (!isActiveInGuild(pluginId, guild)) {
			return true;
		}

		try {
			PreparedStatement statement = connection.prepareStatement("DELETE FROM " + Queries.ACTIVATIONS_TABLE_NAME + " WHERE plugin_id = ? AND guild = ?;");
			statement.setInt(1, pluginId);
			statement.setString(2, guild);

			int affected = statement.executeUpdate();
			return affected > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public List<String> getGuildPluginInformation(String guildId) {
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + Queries.ACTIVATIONS_TABLE_NAME + " WHERE guild = ?;");
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
		}
		return null;
	}

	public boolean registerPlugin(Plugin plugin) {
		try {
			PreparedStatement statement = connection.prepareStatement("INSERT INTO " + Queries.PLUGINS_TABLE_NAME + " (name, description, approved, awaiting_approval) VALUES (?, ?, 1, 0);");
			statement.setString(1, plugin.getName());
			statement.setString(2, "Registered via Command Line.");

			int affected = statement.executeUpdate();
			return affected > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean isBanned(String name) {
		return isBanned(getPluginIdFromName(name));
	}

	public boolean isBanned(int id) {
		try {
			PreparedStatement fetchStatement = connection.prepareStatement("SELECT * FROM " + Queries.PLUGINS_TABLE_NAME + " WHERE id = ?;");
			fetchStatement.setInt(1, id);

			ResultSet results = fetchStatement.executeQuery();
			if (results.next()) {
				return results.getInt("banned") != 0;
			} else {
				return false;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean banPlugin(String name) {
		if (isBanned(name)) {
			return true;
		}

		try {
			PreparedStatement statement = connection.prepareStatement("UPDATE " + Queries.PLUGINS_TABLE_NAME + " SET banned = 1 WHERE name = ?");
			statement.setString(1, name);

			int affected = statement.executeUpdate();
			return affected > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean banPlugin(int id) {
		if (isBanned(id)) {
			return true;
		}

		try {
			PreparedStatement statement = connection.prepareStatement("UPDATE " + Queries.PLUGINS_TABLE_NAME + " SET banned = 1 WHERE id = ?");
			statement.setInt(1, id);

			int affected = statement.executeUpdate();
			return affected > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
}