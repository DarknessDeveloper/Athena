package lol.athena.commandline;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import lol.athena.Athena;

public class CommandLineColoured extends CommandLine {
	
	private Athena athena;
	
	public CommandLineColoured() {
		super();
		athena = Athena.getInstance();
	}

	public void run() {
		try {
			LineReader lineReader = LineReaderBuilder.builder().terminal(athena.getTerminal()).build();

			while (!isQuit()) {
				String line = lineReader.readLine();
				process(line);
			}
		} catch (Exception ex) {
			if (ex instanceof EndOfFileException) {
				return;
			}
			ex.printStackTrace();
		}
	}

	@Override
	public void setQuit(boolean quit) {
		if (quit) {
			try {
				if (athena.getTerminal() != null) {
					athena.getTerminal().close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
