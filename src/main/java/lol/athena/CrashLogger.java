package lol.athena;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.Instant;

public class CrashLogger {

	public static final void logCrash(Exception ex) {
		try {
			Instant crashTime = Instant.now();
			
			File logsDir = new File("crash-logs/");
			File crashLog = new File(logsDir, "crash-" + crashTime.getEpochSecond() + ".log");
			
			logsDir.mkdir();
			if (!crashLog.exists()) {
				crashLog.createNewFile();
			}
			
			FileWriter fileWriter = new FileWriter(crashLog);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			
			ex.printStackTrace(new PrintWriter(writer));
			
			writer.close();
			fileWriter.close();
		} catch (Exception ex1) {
			ex1.printStackTrace();
			System.err.println("Unable to save crash log to dedicated crash file.");
			Runtime.getRuntime().exit(Athena.EXIT_CODE_CRASH_LOG_FAILURE);
		}

	}
	
}
