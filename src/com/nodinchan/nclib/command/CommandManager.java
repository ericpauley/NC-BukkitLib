package com.nodinchan.nclib.command;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.nodinchan.nclib.command.Command.Executor;
import com.nodinchan.nclib.command.info.CommandAlias;
import com.nodinchan.nclib.command.info.CommandDescription;
import com.nodinchan.nclib.command.info.CommandInfo;
import com.nodinchan.nclib.command.info.CommandPermission;
import com.nodinchan.nclib.command.info.CommandUsage;

public final class CommandManager implements CommandExecutor {
	
	private final JavaPlugin plugin;
	
	private final CommandMap commandMap;
	
	private final Map<Command, Map<String, String>> aliases;
	private final Map<String, Command> commands;
	private final Map<Command, Map<String, Executor>> executors;
	
	public CommandManager(JavaPlugin plugin) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		this.plugin = plugin;
		Field field = plugin.getServer().getClass().getField("commandMap");
		field.setAccessible(true);
		this.commandMap = (CommandMap) field.get(plugin.getServer());
		this.aliases = new LinkedHashMap<Command, Map<String, String>>();
		this.commands = new LinkedHashMap<String, Command>();
		this.executors = new LinkedHashMap<Command, Map<String, Executor>>();
	}
	
	public Executor getExecutor(Command command, String name) {
		return executors.get(command).get(aliases.get(command).get(name.toLowerCase()).toLowerCase());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		if (commands.get(cmd.getName()) != null) {
			Command command = commands.get(cmd.getName());
			
			if (args.length == 0) {
				command.main(sender);
				return true;
				
			} else {
				if (getExecutor(command, args[0]) != null) {
					Executor executor = getExecutor(command, args[0]);
					
					if (args.length < command.getClass().getAnnotation(CommandInfo.class).minArgs()) {
						command.invalidArgLength(sender, command);
						return true;
					}
					
					if (args.length - 1 < executor.getMinimumArgumentLength()) {
						command.invalidArgLength(sender, command, args[0]);
						return true;
					}
					
					String[] arguments = Arrays.copyOfRange(args, 1, args.length);
					
					try {
						executor.getMethod().invoke(command, sender, arguments);
						return true;
						
					} catch (IllegalAccessException e) {
						sender.sendMessage("[" + plugin.getName() + "] " + ChatColor.RED + "An error seems to have occured, please check console");
						plugin.getLogger().log(Level.SEVERE, "[" + plugin.getName() + "] An IllgealAccessException has occured while using command: " + executor.getName());
						e.printStackTrace();
						
					} catch (IllegalArgumentException e) {
						sender.sendMessage("[" + plugin.getName() + "] " + ChatColor.RED + "An error seems to have occured, please check console");
						plugin.getLogger().log(Level.SEVERE, "[" + plugin.getName() + "] An IllgealArgumentException has occured while using command: " + executor.getName());
						e.printStackTrace();
						
					} catch (InvocationTargetException e) {
						sender.sendMessage("[" + plugin.getName() + "] " + ChatColor.RED + "An error seems to have occured, please check console");
						plugin.getLogger().log(Level.SEVERE, "[" + plugin.getName() + "] An InvocationTargetException has occured while using command: " + executor.getName());
						e.printStackTrace();
					}
				}
			}
			
			command.invalidCommand(sender);
			return true;
		}
		
		return false;
	}
	
	public org.bukkit.command.Command regsiterCommand(Command cmd) {
		CommandAlias alias = cmd.getClass().getAnnotation(CommandAlias.class);
		CommandDescription description = cmd.getClass().getAnnotation(CommandDescription.class);
		CommandInfo info = cmd.getClass().getAnnotation(CommandInfo.class);
		CommandUsage usage = cmd.getClass().getAnnotation(CommandUsage.class);
		
		if (alias == null || description == null || info == null || usage == null)
			return null;
		
		PluginCommand command = new PluginCommand(info.name(), plugin);
		command.setAliases(Arrays.asList(alias.aliases()));
		command.setDescription(description.description());
		command.setUsage(usage.usage());
		
		if (command.getClass().getAnnotation(CommandPermission.class) != null)
			command.setPermission(command.getClass().getAnnotation(CommandPermission.class).permission());
		
		command.setExecutor(this);
		register(cmd);
		
		commands.put(command.getName(), cmd);
		
		if (commandMap.register(command.getName(), plugin.getDescription().getPrefix(), command))
			return command;
		else
			return plugin.getCommand(command.getName());
	}
	
	private void register(Command cmd) {
		for (Method method : cmd.getClass().getMethods()) {
			CommandAlias alias = method.getAnnotation(CommandAlias.class);
			CommandDescription description = method.getAnnotation(CommandDescription.class);
			CommandInfo info = method.getAnnotation(CommandInfo.class);
			CommandUsage usage = method.getAnnotation(CommandUsage.class);
			
			if (alias == null || description == null || info == null || usage == null)
				continue;
			
			Executor executor = new Executor(cmd, method);
			
			if (aliases.get(cmd) == null)
				aliases.put(cmd, new LinkedHashMap<String, String>());
			
			if (executors.get(cmd) == null)
				executors.put(cmd, new LinkedHashMap<String, Executor>());
			
			aliases.get(cmd).put(executor.getName(), executor.getName());
			
			for (String cmdAlias : executor.getAliases())
				aliases.get(cmd).put(cmdAlias, executor.getName());
			
			executors.get(cmd).put(executor.getName(), executor);
		}
	}
}