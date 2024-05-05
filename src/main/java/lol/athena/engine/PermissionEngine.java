package lol.athena.engine;

import java.util.Collection;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public interface PermissionEngine {
	boolean hasPermission(User user, Guild guild, String permission);
	boolean hasPermission(User user, String guild, String permission);
	boolean hasPermission(String user, Guild guild, String permission);
	boolean hasPermission(String user, String guild, String permission);
	
	Collection<String> getPermissions(User user, Guild guild);
	Collection<String> getPermissions(User user, String guild);
	Collection<String> getPermissions(String user, Guild guild);
	Collection<String> getPermissions(String user, String guild);
	
	boolean grantPermission(User user, Guild guild, String permission);
	boolean grantPermission(User user, String guild, String permission);
	boolean grantPermission(String user, Guild guild, String permission);
	boolean grantPermission(String user, String guild, String permission);
	
	boolean takePermission(User user, Guild guild, String permission);
	boolean takePermission(User user, String guild, String permission);
	boolean takePermission(String user, Guild guild, String permission);
	boolean takePermission(String user, String guild, String permission);
	
	boolean grantPermissions(User user, Guild guild, String... permissions);
	boolean grantPermissions(User user, String guild, String... permissions);
	boolean grantPermissions(String user, Guild guild, String... permissions);
	boolean grantPermissions(String user, String guild, String... permissions);
	
	boolean takePermissions(User user, Guild guild, String... permissions);
	boolean takePermissions(User user, String guild, String... permissions);
	boolean takePermissions(String user, Guild guild, String... permissions);
	boolean takePermissions(String user, String guild, String... permissions);
	
	boolean hasRole(User user, Guild guild, int id);
	boolean hasRole(User user, String guild,int id);
	boolean hasRole(String user, Guild guild, int id);
	boolean hasRole(String user, String guild, int id);
	
	Collection<String> getRoles(User user, Guild guild);
	Collection<String> getRoles(User user, String guild);
	Collection<String> getRoles(String user, Guild guild);
	Collection<String> getRoles(String user, String guild);
	
	Collection<String> getRolePermissions(int id, Guild guild);
	Collection<String> getRolePermissions(int id, String guild);
	
	boolean grantRole(User user, Guild guild, int id);
	boolean grantRole(User user, String guild, int id);
	boolean grantRole(String user, Guild guild, int id);
	boolean grantRole(String user, String guild, int id);
	
	boolean takeRole(User user, Guild guild, int id);
	boolean takeRole(User user, String guild, int id);
	boolean takeRole(String user, Guild guild, int id);
	boolean takeRole(String user, String guild, int id);
	
	boolean hasRolePermission(int id, Guild guild, String permission);
	boolean hasRolePermission(int id, String guild, String permission);
	
	boolean grantRolePermission(int id, Guild guild, String permission);
	boolean grantRolePermission(int id, String guild, String permission);
	
	boolean takeRolePermission(int id, Guild guild, String permission);
	boolean takeRolePermission(int id, String guild, String permission);
	
	boolean grantRolePermissions(int id, Guild guild, String... permissions);
	boolean grantRolePermissions(int id, String guild, String... permissions);
	
	boolean takeRolePermissions(int id, Guild guild, String... permissions);
	boolean takeRolePermissions(int id, String guild, String... permissions);
	
	String getRoleName(int id);
	int getRoleId(String name);
}
