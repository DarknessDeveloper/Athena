package lol.athena;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RedirectedPrintStreamLog extends PrintStream {

	public RedirectedPrintStreamLog(OutputStream out) {
		super(out);
	}
	
	@Override
	public void println(String x) {
		String message = String.format("%s\n", Athena.hansi(AthenaLogger.translateMinecraftColorCodes(x)));
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String formattedMessage = String.format("[%s] [%s] [Athena/INFO] &#ff00bb%s", sdfDate.format(date), sdfTime.format(date), message);
		super.println(formattedMessage);
	}
}
