package com.nodinchan.ncbukkit.command;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nodinchan.ncbukkit.command.casting.Parameter;
import com.nodinchan.ncbukkit.command.info.*;

public final class CommandManager {
	
	private final JavaPlugin plugin;
	
	private final CommandMap commandMap;
	
	private final Map<String, CommandBase> commands;
	private final Map<String, Map<String, Executor>> executors;
	private final Map<Class<?>, Parameter<?>> params;
	
	public CommandManager(JavaPlugin plugin) throws Exception {
		this.plugin = plugin;
		Field field = SimplePluginManager.class.getDeclaredField("commandMap");
		field.setAccessible(true);
		this.commandMap = (CommandMap) field.get(plugin.getServer().getPluginManager());
		this.commands = new HashMap<String, CommandBase>();
		this.executors = new HashMap<String, Map<String, Executor>>();
		this.params = new HashMap<Class<?>, Parameter<?>>();
		registerDefaultParams();
	}
	
	/**
	 * Casts the parameter if found
	 * 
	 * @param clazz The class to cast to
	 * 
	 * @param argument The String argument given
	 * 
	 * @return The casted argument if can be casted, else the original String
	 * 
	 * @throws Exception
	 */
	protected Object castParameter(Class<?> clazz, String argument) throws Exception {
		if (params.get(clazz) != null)
			return params.get(clazz).cast(argument);
		
		return argument;
	}
	
	/**
	 * Regsters a new parameter
	 * 
	 * @param parameter
	 */
	private void newParameter(Parameter<?> parameter) {
		if (params.get(parameter.castTo()) != null)
			return;
		
		params.put(parameter.castTo(), parameter);
	}
	
	/**
	 * To be called when a command is used 
	 * 
	 * @param sender The sender of the command
	 * 
	 * @param command The command used
	 * 
	 * @param args The arguments given
	 * 
	 * @throws Exception
	 */
	public void onCommand(CommandSender sender, String command, String[] args) throws Exception {
		if (executors.get(command) != null) {
			Executor executor = executors.get(command).get(args[0]);
			
			if (executor != null) {
				executor.execute(sender, Arrays.copyOfRange(args, 1, args.length));
				return;
			}
			
			commands.get(command).commandNotFound(sender, args);
			return;
		}
		
		sender.sendMessage("[" + plugin.getName() + "] Unknown command");
	}
	
	/**
	 * Registers the sub-commands
	 * 
	 * @param command The command of the sub-commands to register
	 */
	private void register(CommandBase command) {
		for (Method method : command.getClass().getDeclaredMethods()) {
			if (method.getAnnotation(Command.class) == null)
				continue;
			
			if (executors.get(command.getName()).get(method.getName()) != null)
				continue;
			
			Executor executor = new Executor(command, method, this);
			
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
	
	/**
	 * Registers the command and its sub-commands
	 * 
	 * @param command The command to register
	 * 
	 * @return The command if successful, the already registered command if not
	 */
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
		
		for (Parameter<?> param : command.getUsedParameters())
			newParameter(param);
		
		if (executors.get(command.getName()) == null)
			executors.put(command.getName(), new HashMap<String, Executor>());
		
		if (commands.get(command.getName()) == null)
			commands.put(command.getName(), command);
		
		register(command);
		
		String prefix = plugin.getDescription().getPrefix();
		
		if (commandMap.register(command.getName(), (prefix != null) ? prefix : plugin.getName(), pCommand))
			return pCommand;
		else
			return plugin.getCommand(command.getName());
	}
	
	/**
	 * Registers all primitive parameters
	 */
	private void registerDefaultParams() {
		newParameter(new Parameter<Byte>() {
			
			@Override
			public Class<Byte> castTo() {
				return Byte.class;
			}
			
			@Override
			public Byte cast(String argument) {
				return Byte.parseByte(argument);
			}
		});
		newParameter(new Parameter<Short>() {
			
			@Override
			public Class<Short> castTo() {
				return Short.class;
			}
			
			@Override
			public Short cast(String argument) {
				return Short.parseShort(argument);
			}
		});
		newParameter(new Parameter<Integer>() {
			
			@Override
			public Class<Integer> castTo() {
				return Integer.class;
			}
			
			@Override
			public Integer cast(String argument) {
				return Integer.parseInt(argument);
			}
		});
		newParameter(new Parameter<Long>() {
			
			@Override
			public Class<Long> castTo() {
				return Long.class;
			}
			
			@Override
			public Long cast(String argument) {
				return Long.parseLong(argument);
			}
		});
		newParameter(new Parameter<Float>() {
			
			@Override
			public Class<Float> castTo() {
				return Float.class;
			}
			
			@Override
			public Float cast(String argument) {
				return Float.parseFloat(argument);
			}
		});
		newParameter(new Parameter<Double>() {
			
			@Override
			public Class<Double> castTo() {
				return Double.class;
			}
			
			@Override
			public Double cast(String argument) {
				return Double.parseDouble(argument);
			}
		});
		newParameter(new Parameter<Character>() {
			
			@Override
			public Class<Character> castTo() {
				return Character.class;
			}
			
			@Override
			public Character cast(String argument) throws Exception {
				return argument.charAt(0);
			}
		});
		newParameter(new Parameter<String>() {
			
			@Override
			public Class<String> castTo() {
				return String.class;
			}
			
			@Override
			public String cast(String argument) throws Exception {
				return argument;
			}
		});
		newParameter(new Parameter<Boolean>() {
			
			@Override
			public Class<Boolean> castTo() {
				return Boolean.class;
			}
			
			@Override
			public Boolean cast(String argument) throws Exception {
				return Boolean.parseBoolean(argument);
			}
		});
	}
}