package lol.athena.commandline.commands;

import java.util.Arrays;
import java.util.List;

import lol.athena.Athena;
import lol.athena.commandline.CommandLineCommand;
import lol.athena.staff.GlobalStaffRole;

public class CommandStaff extends CommandLineCommand {
	
	public CommandStaff(Athena bot, String name, List<String> aliases) {
		super(bot, name, aliases);
	}

	@Override
	public boolean execute(String alias, String[] args) {
		if (args.length < 1) {
			getLogger().info("No command specified.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("list")) {
			List<String> staff = getAthena().getStaffManager().getStaff();
			
			if (args.length > 1) {
				String roleName = args[1];

				GlobalStaffRole role = null;
				try {
					role = GlobalStaffRole.valueOf(roleName.toUpperCase());
				} catch (EnumConstantNotPresentException ex) {
					getLogger().severe("Invalid role.");
					return true;
				}
				
				staff = getAthena().getStaffManager().getStaff(role);
			}
			
//			StringBuilder builder = new StringBuilder("[");
//			for (String string : staff) {
//				builder.append(string + ", "); 
//			}
//			
//			String str = builder.substring(0,  builder.length() - 2) + "]";
			getLogger().info(Arrays.deepToString(staff.toArray()));
			return true;
		}

		if (args[0].equalsIgnoreCase("add")) {
			if (args.length < 3) {
				getLogger().info("/" + alias + " add <id> <role>");
				return true;
			}

			String id = args[1];
			String roleName = args[2];

			GlobalStaffRole role = null;
			try {
				role = GlobalStaffRole.valueOf(roleName.toUpperCase());
			} catch (EnumConstantNotPresentException ex) {
				getLogger().severe("Invalid role.");
				return true;
			}
			
			getAthena().getStaffManager().registerStaffMember(id, role);
			getLogger().info("Success.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("remove")) {
			if (args.length < 2) {
				getLogger().info("/" + alias + " remove <id>");
				return true;
			}
			
			getAthena().getStaffManager().removeStaffMember(args[1]);
			getLogger().info("Success.");
			return true;
		}
		
		getLogger().info("Unknown arguments.");
		return true;
	}

}
