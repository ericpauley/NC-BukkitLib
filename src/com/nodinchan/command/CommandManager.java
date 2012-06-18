package com.nodinchan.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.nodinchan.command.Command.Executor;

/*     Copyright (C) 2012  Nodin Chan <nodinchan@live.com>
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * CommandManager - Easier command management
 * 
 * @author NodinChan
 *
 */
public class CommandManager implements CommandExecutor {
	
	private final Plugin plugin;
	
	private final String command;
	
	private final Map<String, Executor> executors;
	
	public CommandManager(Plugin plugin, String command) {
		this.plugin = plugin;
		this.command = command;
		this.executors = new LinkedHashMap<String, Executor>();
		((JavaPlugin) plugin).getCommand(command).setExecutor(this);
	}
	
	/**
	 * Searches for the command and executes it if found
	 * 
	 * @param player The command sender
	 * 
	 * @param command The command
	 * 
	 * @param args The arguments
	 */
	public void execute(CommandSender sender, String command, String[] args) {
		for (Executor executor : executors.values()) {
			for (String trigger : executor.getMethod().getAnnotation(CommandID.class).triggers()) {
				if (trigger.equalsIgnoreCase(command)) {
					try {
						executor.execute(sender, args);
						return;
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
						e.getTargetException().printStackTrace();
					}
				}
			}
		}
		
		sender.sendMessage("[" + plugin.getName() + "] " + ChatColor.RED + "Invalid Command");
		sender.sendMessage("[" + plugin.getName() + "] " + ChatColor.GOLD + "\"/titanchat commands [page]\" for command list");
	}
	
	public String getCommand() {
		return command;
	}
	
	/**
	 * Gets the amount of commands
	 * 
	 * @return The amount of commands
	 */
	public int getCommandAmount() {
		return executors.size();
	}
	
	public Executor getCommandExecutor(String name) {
		return executors.get(name.toLowerCase());
	}
	
	public Executor getCommandExecutor(int exeNum) {
		return new LinkedList<Executor>(executors.values()).get(exeNum);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase(command)) {
			execute(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
			return true;
		}
		
		return false;
	}
	
	public void register(Command command) {
		for (Method method : command.getClass().getMethods()) {
			Annotation annotation = method.getAnnotation(CommandID.class);
			
			if (annotation != null)
				executors.put(((CommandID) annotation).name().toLowerCase(), new Executor(method, command));
		}
	}
	
	public void registerAll(Command... commands) {
		for (Command command : commands)
			register(command);
	}
	
	public void registerAll(List<Command> commands) {
		registerAll(commands.toArray(new Command[0]));
	}
	
	public void sort() {
		Map<String, Executor> executors = new LinkedHashMap<String, Executor>();
		List<String> names = new ArrayList<String>(this.executors.keySet());
		
		Collections.sort(names);
		
		for (String name : names)
			executors.put(name, getCommandExecutor(name));
		
		this.executors.clear();
		this.executors.putAll(executors);
	}
	
	public void unload() {
		executors.clear();
	}
}