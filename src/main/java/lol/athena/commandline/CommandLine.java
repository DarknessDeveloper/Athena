package lol.athena.commandline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import lol.athena.Athena;
import lombok.Getter;
import lombok.Setter;

public class CommandLine implements Runnable {

	@Getter
	@Setter private boolean quit = false;
	private final Map<String, CommandLineCommand> registeredCommands;

	public CommandLine() {
		registeredCommands = new HashMap<>();
	}

	public void run() {
		Athena.debug("Initialising command line thread...");
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			while (!quit) {
				if (!inputReader.ready()) {
					Thread.sleep(200);
					continue;
				}

				if (quit) {
					break;
				}

				String line = inputReader.readLine();
				process(line);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			quit = true;
		}
	}

	public boolean registerCommand(@NotNull String name, @NotNull CommandLineCommand command) {
		if (registeredCommands.containsKey(name.toLowerCase()) || registeredCommands.containsValue(command)) {
			return false;
		}

		Athena.debug("Registering command line command '" + name + "', Aliases: " + Arrays.deepToString(command.getAliases().toArray()));
		return registeredCommands.put(name, command) != null;
	}

	public boolean unregisterCommand(String name) {
		if (!registeredCommands.containsKey(name.toLowerCase())) {
			return false;
		}

		return registeredCommands.remove(name.toLowerCase()) != null;
	}

	public boolean unregisterCommand(String name, CommandLineCommand command) {
		if (!registeredCommands.containsValue(command)) {
			return false;
		}

		return unregisterCommand(command.getName());
	}

	@Deprecated
	public void clearCommands() {
		registeredCommands.clear();
	}

	public boolean onCommand(String command, String[] args) {
		CommandLineCommand cmd = null;

		if (registeredCommands.containsKey(command)) {
			cmd = registeredCommands.get(command);
		} else {
			for (CommandLineCommand iCommand : registeredCommands.values()) {
				if (iCommand.isAlias(command)) {
					cmd = iCommand;
					break;
				}
			}
		}

		if (cmd == null) {
			return true;
		}

		try {
			return cmd.execute(command, args);
		} catch (Exception ex) {
			return false;
		}
	}

	public Collection<CommandLineCommand> getRegisteredCommands() {
		return Collections.unmodifiableCollection(registeredCommands.values());
	}

	protected void process(@NotNull String line) throws IOException {
		String[] split = line.contains(" ") ? line.split(" ") : new String[0];
		String command = (split.length > 0 ? split[0] : line).toLowerCase();
		String[] args = new String[split.length == 0 ? 0 : split.length - 1];

		if (args.length > 0) {
			System.arraycopy(split, 1, args, 0, split.length - 1);
		}

		try {
			Validate.notBlank(command, "Command must not be null or empty.");
		} catch (NullPointerException | IllegalArgumentException ex) {
			return;
		}

		if (!onCommand(command.toLowerCase(), args)) {
			Athena.getInstance().getLogger().warning("&cCommand execution failed for command '" + command.toLowerCase() + "'");
		}
	}
}
