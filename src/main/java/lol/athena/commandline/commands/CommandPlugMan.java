package lol.athena.commandline.commands;

import java.util.Arrays;
import java.util.List;

import lol.athena.Athena;
import lol.athena.commandline.CommandLineCommand;
import lol.athena.plugin.Plugin;
import lol.athena.plugin.PluginManager;

public class CommandPlugMan extends CommandLineCommand {

	public CommandPlugMan(Athena bot, String name, List<String> aliases) {
		super(bot, name, aliases);
	}

	@Override
	public boolean execute(String alias, String[] args) {
		getLogger().warning("# Dynamically unloading and loading plugins in this way is untested!");
		getLogger().warning("# Unloading a plugin will also unload any plugins that depend on it!");

		if (args.length < 2) {
			getLogger().info("This command serves as a management system for plugins.");
			getLogger().info("Usage: " + alias + " <load | unload | enable | disable | info> <plugin>");
			getLogger().warning("Warning: Unloading a plugin will also unload any plugins that depend on it!");
			return true;
		}

		String target = args[1];
		PluginManager pluginManager = Athena.getInstance().getPluginManager();

		if (args[0].equalsIgnoreCase("info")) {
			if (!pluginManager.isLoaded(target)) {
				getLogger().info("That plugin is not loaded.");
				return true;
			}

			Plugin plugin = pluginManager.getPlugin(target);
			String name = plugin.getName();
			String version = plugin.getDescription().getVersion();
			String mainClass = plugin.getDescription().getMain();
			String authors = Arrays.deepToString(plugin.getDescription().getAuthors().toArray());
			String dependencies = Arrays.deepToString(plugin.getDescription().getDependencies().toArray());

			getLogger().info("=-=-=-= Plugin Information =-=-=-=");
			getLogger().info(" Name: " + name);
			getLogger().info(" Version: " + version);
			getLogger().info(" Main Class: " + mainClass);
			getLogger().info(" Authors: " + authors);
			getLogger().info(" Dependencies: " + dependencies);
			getLogger().info("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");

			return true;
		}

		if (args[0].equalsIgnoreCase("enable")) {
			if (!pluginManager.isLoaded(target)) {
				getLogger().info("That plugin is not loaded.");
				return true;
			}

			Plugin plugin = pluginManager.getPlugin(target);
			if (plugin.isEnabled()) {
				getLogger().info(plugin.getName() + " is already enabled.");
				return true;
			}

			List<String> dependencies = plugin.getDescription().getDependencies();

			dependencies.forEach(dependency -> {
				if (!pluginManager.isEnabled(dependency)) {
					pluginManager.enablePlugin(pluginManager.getPlugin(dependency));
				}
			});
			pluginManager.enablePlugin(plugin);
			return true;
		}

		if (args[0].equalsIgnoreCase("disable")) {
			if (!pluginManager.isLoaded(target)) {
				getLogger().info("That plugin is not loaded.");
				return true;
			}

			Plugin plugin = pluginManager.getPlugin(target);
			if (!plugin.isEnabled()) {
				getLogger().info(plugin.getName() + " is already disabled.");
				return true;
			}

			disablePlugin(pluginManager, plugin);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("load")) {
			
			return true;
		}
		
		if (args[0].equalsIgnoreCase("unload")) {
			return true;
		}

		getLogger().info("Not implemented");
		return true;
	}

	private void disablePlugin(PluginManager pluginManager, Plugin plugin) {
		pluginManager.getEnabledPlugins().forEach(pmPlugin -> {
			if (!pmPlugin.equals(plugin)) {
				//getLogger().info(pmPlugin.getName());
				List<String> dependencies = pmPlugin.getDescription().getDependencies();
				dependencies.forEach(dependency -> {
					if (dependency.equalsIgnoreCase(plugin.getName())) {
						disablePlugin(pluginManager, pmPlugin);
					}
				});
			}
		});

		pluginManager.disablePlugin(plugin);
	}

}
