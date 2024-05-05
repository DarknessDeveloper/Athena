package lol.athena.engine.sql;

public class Queries {

	private Queries() {}
	
	public static String DB_NAME = "athena";
	
	public static final String
		ACTIVATIONS_TABLE_NAME = "activations",
		LICENSES_TABLE_NAME = "licenses",
		PLUGINS_TABLE_NAME = "plugins",
		RATELIMIT_TABLE_NAME = "ratelimit",
		PERMISSIONS_TABLE_NAME = "permissions";
	
	public static final String 
		CREATE_TABLE_LICENSES = "CREATE TABLE IF NOT EXISTS `" + LICENSES_TABLE_NAME + "` "
			+ "( `id` INT NOT NULL AUTO_INCREMENT , `user_id` TEXT NOT NULL DEFAULT '213655288039866368' , "
			+ "`license_key` TEXT NULL DEFAULT NULL , `max_uses` INT NOT NULL DEFAULT '3' , "
			+ "`uses` INT NOT NULL DEFAULT '0' , `plugin_id` BIGINT NOT NULL DEFAULT '0' , "
			+ "PRIMARY KEY (`id`));",
			
		CREATE_TABLE_ACTIVATIONS = "CREATE TABLE IF NOT EXISTS `" + ACTIVATIONS_TABLE_NAME + "` "
				+ "(`id` int(11) NOT NULL AUTO_INCREMENT, `plugin_id` int(11) NOT NULL DEFAULT 0, `guild` text DEFAULT NULL, "
				+ "PRIMARY KEY (`id`));",
				
		CREATE_TABLE_PLUGINS = "CREATE TABLE IF NOT EXISTS `" + PLUGINS_TABLE_NAME + "` "
			+ "( `id` INT NOT NULL AUTO_INCREMENT , `owner_user_id` TEXT NOT NULL DEFAULT '213655288039866368' , "
			+ "`name` TEXT NULL DEFAULT NULL , "
			+ "`description` TEXT NOT NULL , `creation_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , "
			+ "`updated_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , `approved` TINYINT NOT NULL DEFAULT '0' , "
			+ "`awaiting_approval` TINYINT NOT NULL DEFAULT '1' , `banned` TINYINT NOT NULL DEFAULT '0' , "
			+ "`banned_by` TEXT NULL DEFAULT NULL , `banned_date` TIMESTAMP NULL DEFAULT NULL , "
			+ "`version` TEXT NOT NULL DEFAULT '1.0.0-SNAPSHOT' , "
			+ "PRIMARY KEY (`id`));",
			
		CREATE_TABLE_PERMISSIONS = "CREATE TABLE IF NOT EXISTS `" + PERMISSIONS_TABLE_NAME 
			+ "` (`id` INT NOT NULL AUTO_INCREMENT , "
			+ "`user_id` TEXT NULL DEFAULT NULL , "
			+ "`permission` TEXT NULL DEFAULT NULL , "
			+ "`guild_id` TEXT NULL DEFAULT NULL , "
			+ "PRIMARY KEY (`id`));",
			
		CREATE_TABLE_RATELIMIT = "CREATE TABLE IF NOT EXISTS `" + RATELIMIT_TABLE_NAME + "`"
			+ " ( `id` INT NOT NULL AUTO_INCREMENT , "
			+ "`user_id` TEXT NULL DEFAULT NULL , `limited_until` "
			+ "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , "
			+ "PRIMARY KEY (`id`));",
			
		CREATE_TABLE_ROLES = "CREATE TABLE IF NOT EXISTS roles (`id` INT NOT NULL AUTO_INCREMENT , `name` TEXT NULL , `guild_id` TEXT NULL , PRIMARY KEY (`id`));",
		CREATE_TABLE_ROLE_PERMISSIONS = "CREATE TABLE IF NOT EXISTS role_permissions (`id` INT NOT NULL AUTO_INCREMENT , `role_id` INT NOT NULL DEFAULT '0' , `guild_id` TEXT NULL , `permission` TEXT NULL , PRIMARY KEY (`id`));",
		CREATE_TABLE_ROLE_USERS = "CREATE TABLE IF NOT EXISTS role_users (`id` INT NOT NULL AUTO_INCREMENT , `role_id` INT NOT NULL DEFAULT '0' , `user_id` TEXT NULL , `guild_id` TEXT NULL , PRIMARY KEY (`id`));"
		;

}
