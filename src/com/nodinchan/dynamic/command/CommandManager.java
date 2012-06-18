package com.nodinchan.dynamic.command;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

public class CommandManager {
	
	private final CommandMap commandMap;
	
	private final Plugin plugin;
	
	public CommandManager(Plugin plugin) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		this.plugin = plugin;
		
		Field field = plugin.getServer().getClass().getField("commandMap");
		field.setAccessible(true);
		this.commandMap = (CommandMap) field.get(plugin.getServer());
	}
	
	public PluginCommand getCommand(String name) {
		return plugin.getServer().getPluginCommand(name);
	}
	
	public Command register(String cmd, String... aliases) {
		Command command = new Command(cmd, plugin);
		command.setAliases(Arrays.asList(aliases));
		commandMap.register(cmd, plugin.getName(), command);
		return command;
	}
}