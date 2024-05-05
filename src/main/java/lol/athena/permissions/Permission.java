package lol.athena.permissions;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

@AllArgsConstructor
public class Permission {

    @Getter private Guild guild;
    @Getter private User user;
    @Getter private String permission;

    public boolean hasPermission() {
        return Permission.hasPermission(guild, user, permission);
    }

    public static boolean hasPermission(@NotNull Guild guild, @NotNull User user, String permission) {
        String ownerId = guild.getOwnerId();
        String userId = user.getId();

        if (ownerId.equals(userId)) {
            return true;
        }

        if (permission == null) {
            return false;
        }

//        Engine mySql = Athena.db();
//        try {
//            PreparedStatement permissionCheck = mySql.getConnection().prepareStatement("SELECT * FROM `" + Queries.PERMISSIONS_TABLE_NAME + "` WHERE guild_id = ? AND user_id = ? AND permission = ?;");
//            permissionCheck.setString(1, guild.getId());
//            permissionCheck.setString(2, user.getId());
//            permissionCheck.setString(3, permission);
//
//            ResultSet results = permissionCheck.executeQuery();
//            return results.next();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            return false;
//        }
        return false;
    }

    public static @NotNull List<String> getPermissions(Guild guild, User user) {
        return getPermissions(guild, user.getId());
    }

    public static @NotNull List<String> getPermissions(@NotNull Guild guild, String userId) {
        String ownerId = guild.getOwnerId();
        if (ownerId.equals(userId)) {
            return Lists.newArrayList("*");
        }
        if (userId == null) {
            return Lists.newArrayList();
        }

//        try {
//            PreparedStatement statement = Athena.db().getConnection().prepareStatement("SELECT * FROM `" + Queries.PERMISSIONS_TABLE_NAME + "` WHERE user_id = ? AND guild_id = ?;");
//            statement.setString(1, userId);
//            statement.setString(2, guild.getId());
//
//            ResultSet results = statement.executeQuery();
//            List<String> permissions = new ArrayList<>();
//
//            while (results.next()) {
//                permissions.add(results.getString("permission"));
//            }
//
//            return permissions;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return Lists.newArrayList();
//        }
        
        return Lists.newArrayList();
    }
}
