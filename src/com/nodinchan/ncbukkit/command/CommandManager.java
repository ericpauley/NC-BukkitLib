package com.nodinchan.ncbukkit.command;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nodinchan.ncbukkit.command.info.*;

public final class CommandManager {
	
	private final JavaPlugin plugin;
	
	private final CommandMap commandMap;
	
	private final Map<String, CommandBase> commands;
	private final Map<String, Map<String, Executor>> executors;
	
	public CommandManager(JavaPlugin plugin) throws Exception {
		this.plugin = plugin;
		Field field = SimplePluginManager.class.getDeclaredField("commandMap");
		field.setAccessible(true);
		this.commandMap = (CommandMap) field.get(plugin.getServer().getPluginManager());
		this.commands = new HashMap<String, CommandBase>();
		this.executors = new HashMap<String, Map<String, Executor>>();
	}
	
	public void onCommand(CommandSender sender, String command, String[] args) {
		if (executors.get(command) != null) {
			Executor executor = executors.get(command).get(args[0]);
			
			if (executor != null) {
				try {
					executor.execute(sender, Arrays.copyOfRange(args, 1, args.length));
					return;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					plugin.getLogger().log(Level.SEVERE, "An IllegalAccessException as occurred");
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					plugin.getLogger().log(Level.SEVERE, "An IllegalArgumentException as occurred");
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					plugin.getLogger().log(Level.SEVERE, "An IllegalTargetException as occurred");
				}
			}
			
			commands.get(command).commandNotFound(sender, args);
			return;
		}
		
		sender.sendMessage("[" + plugin.getName() + "] Unknown command");
	}
	
	public org.bukkit.command.Command registerCommand(CommandBase command) {
		if (plugin == null || command == null)
			return null;
		
		String name = command.getName();
		String description = command.getDescription();
		String[] aliases = command.getAliases();
		String usage = command.getUsage();
		String permission = command.getPermission();
		
		if (name == null)
			return null;
		
		PluginCommand pCommand = new PluginCommand(command.getName(), plugin);
		
		if (description != null)
			pCommand.setDescription(description);
		
		if (aliases != null)
			pCommand.setAliases(Arrays.asList(aliases));
		
		if (usage != null)
			pCommand.setUsage(usage);
		
		if (permission != null && !permission.equals(""))
			pCommand.setPermission(permission);
		
		String prefix = plugin.getDescription().getPrefix();
		
		if (executors.get(command.getName()) == null)
			executors.put(command.getName(), new HashMap<String, Executor>());
		
		if (commands.get(command.getName()) == null)
			commands.put(command.getName(), command);
		
		register(command);
		
		if (commandMap.register(command.getName(), (prefix != null) ? prefix : plugin.getName(), pCommand))
			return pCommand;
		else
			return plugin.getCommand(command.getName());
	}
	
	private void register(CommandBase command) {
		for (Method method : command.getClass().getDeclaredMethods()) {
			if (method.getAnnotation(Command.class) == null)
				continue;
			
			if (executors.get(command.getName()).get(method.getName()) != null)
				continue;
			
			Executor executor = new Executor(command, method);
			
			if (executors.get(command.getName()).get(method.getName()) == null)
				executors.get(command.getName()).put(method.getName(), executor);
			
			if (method.getAnnotation(Aliases.class) != null) {
				for (String alias : method.getAnnotation(Aliases.class).value()) {
					if (executors.get(command.getName()).get(alias) != null)
						continue;
					
					executors.get(command.getName()).put(alias, executor);
				}
			}
		}
	}
}