package lol.athena.permissions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PermissionManager {

	public static boolean grant(Guild guild, String userId, String permission) {
		return grant(guild, userId, new String[] { permission });
	}

	public static boolean grant(Guild guild, String userId, String[] permissions) {
		return false;
		
		
//		String ownerId = guild.getOwnerId();
//		if (ownerId.equals(userId)) {
//			return false;
//		}
//		
//		User user = Athena.getInstance().getJda().retrieveUserById(userId).complete();
//		List<String> added = new ArrayList<>();
//		
//		try {
//			String query = "INSERT INTO `" + Queries.PERMISSIONS_TABLE_NAME + "` (guild_id, user_id, permission) VALUES ";
//			for (String permission : permissions) {
//				if (Permission.hasPermission(guild, user, permission))
//				{
//					continue;	
//				}
//				query += "(?, ?, ?), ";
//				added.add(permission);
//			}
//			query = query.substring(0, query.length() - 2) + ";";
//
//			PreparedStatement statement = Athena.db().getConnection().prepareStatement(query);
//			for (int i = 0; i < added.size(); i++) {
//				int guildIndex = 1 + (3 * i);
//				int userIndex = 2 + (3 * i);
//				int permissionIndex = 3 + (3 * i);
//				
//				statement.setString(guildIndex, guild.getId());
//				statement.setString(userIndex, userId);
//				statement.setString(permissionIndex, added.get(i));
//			}
//			
//			int affected = statement.executeUpdate();
//			return affected > 0;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return false;
//		}
		
	}
	
	public static boolean take(Guild guild, String userId, String permission) {
		return take(guild, userId, new String[] { permission });
	}
	
	public static boolean take(Guild guild, String userId, String[] permissions) {
//		String ownerId = guild.getOwnerId();
//		if (ownerId.equals(userId)) {
//			return false;
//		}
//		
//		try {
//			String query = "DELETE FROM `" + Queries.PERMISSIONS_TABLE_NAME + "` WHERE guild_id = ? AND user_id = ? AND permission = ?;";
//
//			for(String permission : permissions) {
//				PreparedStatement statement = Athena.db().getConnection().prepareStatement(query);
//				
//				statement.setString(1, guild.getId());
//				statement.setString(2, userId);
//				statement.setString(3, permission);
//				statement.executeUpdate();
//			}
//			
//			return true;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return false;
//		}
		
		return false;
	}

}
