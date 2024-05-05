package lol.athena.commandline.commands;

import java.io.File;
import java.util.List;

import lol.athena.Athena;
import lol.athena.commandline.CommandLineCommand;
import lol.athena.versioning.VersionChecker;

public class CommandUpdate extends CommandLineCommand {

	public CommandUpdate(Athena bot, String name, List<String> aliases) {
		super(bot, name, aliases);
	}

	@Override
	public boolean execute(String alias, String[] args) {
		if (alias.equalsIgnoreCase("download")) {
			if (args.length < 1) {
				getLogger().info("Please specify a version of Athena to download.");
				return true;
			}
			
			String versionString = args[0];
			double version = 0.0d;
			try {
				version = Double.parseDouble(versionString);
			} catch (NumberFormatException ex) {
				getLogger().info("Please specify a valid version.");
				return true;
			}
			
			VersionChecker.downloadUpdate(new File("Athena-" + version + "-md.jar"), version);
			return true;
		}
		
		if (!VersionChecker.check(true)) {
			getLogger().info("No update is available for Athena.");
			return true;
		}
		
		getLogger().info("Downloading Athena update " + VersionChecker.LATEST_AVAILABLE);
		VersionChecker.downloadUpdate(new File("Athena-" + VersionChecker.LATEST_AVAILABLE + ".jar"), VersionChecker.LATEST_AVAILABLE);
		return true;
	}

}
