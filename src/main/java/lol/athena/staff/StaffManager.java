package lol.athena.staff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import lol.athena.Athena;
import lombok.Getter;

public class StaffManager {

	@Getter private final Athena athena;
	@Getter private File dataFolder;
	@Getter private final File file;
	@Getter private FileConfiguration config;

	private Map<String, GlobalStaffRole> staffList = new HashMap<>();

	public StaffManager(Athena athena, File file) {
		this.athena = athena;
		this.file = file;

		init();
	}

	private void init() {
		try {
			dataFolder = athena.getDataFolder();
			if (!file.exists()) {
				file.createNewFile();
			}

			config = YamlConfiguration.loadConfiguration(file);
			ConfigurationSection section = config.getConfigurationSection("staff");

			if (section == null) {
				config.set("staff.0.role", GlobalStaffRole.MODERATOR.toString());
				save();
				section = config.getConfigurationSection("staff");
			}

			Collection<String> keys = section.getKeys(false);

			for (String string : keys) {
				GlobalStaffRole role = null;
				try {
					role = GlobalStaffRole.valueOf(section.getString(string + ".role").toUpperCase());
				} catch (EnumConstantNotPresentException | NullPointerException ex) {
					ex.printStackTrace();
				}

				staffList.put(string, role);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void registerStaffMember(String id, GlobalStaffRole role) {
		if (staffList.containsKey(id)) {
			staffList.replace(id, role);
			config.set("staff." + id + ".role", role.toString());
			return;
		}

		staffList.put(id, role);
		config.set("staff." + id + ".role", role.toString());
		save();
	}

	public void removeStaffMember(String id) {
		if (staffList.containsKey(id)) {
			staffList.remove(id);
			config.set("staff." + id + ".role", null);
			config.set("staff." + id, null);
			save();
		}
	}

	public List<String> getPluginStaffIds(String pluginId) {
		return Lists.newArrayList();
	}

	public boolean isStaff(String id) {
		return staffList.containsKey(id);
	}

	public boolean isGlobalAdmin(String id) {
		return isStaff(id) && staffList.get(id).getRolePriority() <= GlobalStaffRole.ADMINISTRATOR.getRolePriority();
	}

	public List<String> getStaff() {
		return getStaff(null);
	}

	public List<String> getStaff(@Nullable GlobalStaffRole role) {
		if (role == null) {
			return Lists.newArrayList(staffList.keySet());
		}

		List<String> ids = new ArrayList<>();
		for (Entry<String, GlobalStaffRole> roles : staffList.entrySet()) {
			if (roles.getValue().equals(role)) {
				ids.add(roles.getKey());
			}
		}

		return ids;
	}

	public void save() {
		try {
			config.save(file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
