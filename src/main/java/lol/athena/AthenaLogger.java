package lol.athena;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.craftbukkit.command.ColouredConsoleSender;

public class AthenaLogger extends Logger {

	public AthenaLogger(String context) {
		super(context, null);
		setParent(Logger.getLogger("Athena"));
	}

	@Override
	public void log(LogRecord record) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String formattedMessage = String.format("[%s] [%s] [Athena/%s] %s", sdfDate.format(date), sdfTime.format(date), record.getLevel().toString(), record.getMessage());

		try {
			Athena.getInstance().getTerminal().output().write(String.format("%s\n", Athena.hansi(translateMinecraftColorCodes(formattedMessage))).getBytes());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static String translateMinecraftColorCodes(String message) {
		String regex = "\\&[a-zA-F0-9]{1}";
		Pattern searchPattern = Pattern.compile(regex);
		Matcher matcher = searchPattern.matcher(message);
		StringBuilder builder = new StringBuilder();
		
		while (matcher.find()) {
			String replacement = matcher.group(0);
			matcher.appendReplacement(builder, codes.get(replacement.substring(1).toLowerCase()));
		}
		
		matcher.appendTail(builder);
		return builder.toString();
	}
	
	private static Map<String, String> codes = new HashMap<>();
	static {
		String ansiEscape = String.valueOf(ColouredConsoleSender.ANSI_ESC_CHAR);
		
		codes.put("0", ansiEscape + "[38;2;0;0;0m");
		codes.put("1", ansiEscape + "[38;2;0;0;170m");
		codes.put("2", ansiEscape + "[38;2;0;170;0m");
		codes.put("3", ansiEscape + "[38;2;0;170;170m");
		codes.put("4", ansiEscape + "[38;2;170;0;0m");
		codes.put("5", ansiEscape + "[38;2;170;0;170m");
		codes.put("6", ansiEscape + "[38;2;255;170;0m");
		codes.put("7", ansiEscape + "[38;2;170;170;170m");
		codes.put("8", ansiEscape + "[38;2;85;85;85m");
		codes.put("9", ansiEscape + "[38;2;85;85;255m");
		codes.put("a", ansiEscape + "[38;2;85;255;85m");
		codes.put("b", ansiEscape + "[38;2;85;255;255m");
		codes.put("c", ansiEscape + "[38;2;255;85;85m");
		codes.put("d", ansiEscape + "[38;2;255;85;255m");
		codes.put("e", ansiEscape + "[38;2;255;255;85m");
		codes.put("f", ansiEscape + "[38;2;255;255;255m");
		
		codes.put("l", ansiEscape + "[1m");
		codes.put("o", ansiEscape + "[3m");
		codes.put("n", ansiEscape + "[4m");
		codes.put("m", ansiEscape + "[9m");
		
		codes.put("r", Athena.ANSI_RESET);
	}
}
