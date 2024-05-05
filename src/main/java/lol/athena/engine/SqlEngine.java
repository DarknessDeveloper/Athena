package lol.athena.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;

public interface SqlEngine extends RelationalDatabaseEngine {
	
	String escapeString(String string);
	
	PreparedStatement prepare(String query);
	PreparedStatement prepare(String query, Object... args);
	
	Connection getConnection();
}
