package lol.athena.versioning;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


import lol.athena.Athena;
import lol.athena.NamedLogger;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VersionChecker {

	public static final String LATEST_CHECK_URL = "https://cubecubed.altervista.org/athena/latest.txt";
	public static final String UPDATE_DOWNLOAD_URL = "https://cubecubed.altervista.org/athena/download/{0}/Athena.jar";
	public static double LATEST_AVAILABLE = 0d;

	public static boolean check(boolean silent) {
		NamedLogger logger = new NamedLogger("Version Checker");
		if (!silent) {
			logger.info("Checking for updates...");
		}
		String current = Athena.BUILD;

		try {
			double currentDouble = Double.parseDouble(current);
			double responseDouble;
			URL url = new URL(LATEST_CHECK_URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			InputStream responseStream = connection.getInputStream();
			String response = Athena.read(responseStream);

			responseDouble = Double.parseDouble(response);
			if (currentDouble >= responseDouble) {
				if (!silent) {
					logger.info("&aYou are running the latest version of Athena.");
				}
				return false;
			} else {
				if (!silent) {
					logger.warning("&aAn update is available for Athena! Latest: &c" + responseDouble + "&a, Your Version: &c" + currentDouble);
				}
				LATEST_AVAILABLE = responseDouble;
				return true;
			}
		} catch (Exception ex) {

			if (!silent) {
				logger.warning("&cUpdate check failed.");
			}
			return false;
		}
	}

	public static void downloadUpdate(File newFile, double version) {
		try {
			InputStream in = new URL(UPDATE_DOWNLOAD_URL.replace("{0}", String.valueOf(version))).openStream();
			Files.copy(in, Paths.get(newFile.getPath()), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			ex.printStackTrace();
			Athena.getInstance().getLogger().warning("[Version Checker] [Updater] Failed to download Athena version " + version + ": " + ex.getMessage());
		}
	}
}
