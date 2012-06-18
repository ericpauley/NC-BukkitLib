package com.nodinchan.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

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
 * Command - Command base
 * 
 * @author NodinChan
 *
 */
public class Command {
	
	private final Plugin plugin;
	
	private final CommandManager manager;
	
	public Command(Plugin plugin, CommandManager manager) {
		this.plugin = plugin;
		this.manager = manager;
	}
	
	/**
	 * Sends a warning for invalid argument length
	 * 
	 * @param player the player to send to
	 * 
	 * @param name the command's name
	 */
	public final void invalidArgLength(CommandSender sender, String name) {
		sender.sendMessage("[" + plugin.getName() + "] " + ChatColor.RED + "Invalid Argument Length");
		Executor executor = manager.getCommandExecutor(name);
		
		if (executor.getMethod().getAnnotation(CommandInfo.class) != null)
			sender.sendMessage("[" + plugin.getName() + "] Usage: /" + manager.getCommand() + " " + executor.getMethod().getAnnotation(CommandInfo.class).usage());
	}
	
	/**
	 * Executor - Represents each command method in a Command
	 * 
	 * @author NodinChan
	 *
	 */
	public static final class Executor {
		
		private final Method method;
		
		private final Command command;
		
		private final String name;
		
		public Executor(Method method, Command command) {
			this.method = method;
			this.command = command;
			this.name = method.getAnnotation(CommandID.class).name();
		}
		
		@Override
		public boolean equals(Object object) {
			if (object instanceof Executor)
				if (((Executor) object).getMethod().equals(method))
					if (((Executor) object).getCommand().equals(command))
						if (((Method) object).getName().equals(name))
							return true;
			
			return false;
		}
		
		/**
		 * Executes the command
		 * 
		 * @param player The command sender
		 * 
		 * @param args The command arguments
		 * 
		 * @throws InvocationTargetException 
		 * @throws IllegalArgumentException 
		 * @throws IllegalAccessException 
		 */
		public void execute(CommandSender sender, String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			method.invoke(command, sender, args);
		}
		
		/**
		 * Gets the Command
		 * 
		 * @return The Command
		 */
		public Command getCommand() {
			return command;
		}
		
		/**
		 * Gets the method
		 * 
		 * @return The Method
		 */
		public Method getMethod() {
			return method;
		}
		
		/**
		 * Gets the name of the command
		 * 
		 * @return The command name
		 */
		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return "Command:" + name;
		}
	}
}