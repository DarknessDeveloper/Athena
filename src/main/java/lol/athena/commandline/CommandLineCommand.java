package lol.athena.commandline;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import lol.athena.Athena;
import lol.athena.NamedLogger;
import lombok.Getter;

public abstract class CommandLineCommand {

	@Getter private Athena athena;
	@Getter private String name;
	@Getter private List<String> aliases;
	@Getter private Logger logger;

	public CommandLineCommand(Athena athena, String name, List<String> aliases) {
		this.athena = athena;
		this.name = name;
		this.aliases = aliases;
		this.logger = new NamedLogger(name);
	}

	public CommandLineCommand(Athena athena, String name) {
		this(athena, name, new ArrayList<>());
	}

	public abstract boolean execute(String alias, String[] args);

	public boolean registerAlias(String alias) {
		if (aliases.contains(alias.toLowerCase())) {
			return false;
		}

		return aliases.add(alias.toLowerCase());
	}

	public boolean isAlias(String alias) {
		return aliases.contains(alias.toLowerCase());
	}
	
	public boolean any(String needle, String... haystack) {
		return Athena.any(needle, haystack);
	}
}
