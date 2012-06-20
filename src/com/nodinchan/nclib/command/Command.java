package com.nodinchan.nclib.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.nodinchan.nclib.command.info.CommandAlias;
import com.nodinchan.nclib.command.info.CommandDescription;
import com.nodinchan.nclib.command.info.CommandInfo;
import com.nodinchan.nclib.command.info.CommandUsage;

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

public abstract class Command {
	
	protected final JavaPlugin plugin;
	
	protected final CommandManager manager;
	
	public Command(JavaPlugin plugin, CommandManager manager) {
		this.plugin = plugin;
		this.manager = manager;
	}
	
	/**
	 * Sends an invalid argument length message to the sender, showing the usage of the command
	 * 
	 * @param sender The sender of the command
	 * 
	 * @param main The command
	 */
	public final void invalidArgLength(CommandSender sender, Command main) {
		sender.sendMessage("[" + plugin.getName() + "] " + ChatColor.RED + "Invalid Argument Length");
		sender.sendMessage("[" + plugin.getName() + "] Usage: /" + main.getClass().getAnnotation(CommandUsage.class).usage());
	}
	
	/**
	 * Sends an invalid argument length message to the sender, showing the usage of the sub-command
	 * 
	 * @param sender The sender of the command
	 * 
	 * @param main The command
	 * 
	 * @param command The sub-command
	 */
	public final void invalidArgLength(CommandSender sender, Command main, String command) {
		sender.sendMessage("[" + plugin.getName() + "] " + ChatColor.RED + "Invalid Argument Length");
		sender.sendMessage("[" + plugin.getName() + "] Usage: /" + manager.getExecutor(main, command).getUsage());
	}
	
	/**
	 * Called when the command is invalid
	 * 
	 * @param sender The sender of the command
	 * 
	 * @return An array of messages to be sent to the command sender
	 */
	public abstract String[] invalidCommand(CommandSender sender);
	
	/**
	 * Called when the sender did not provide any arguments
	 * 
	 * @param sender The sender of the command
	 */
	public abstract void main(CommandSender sender);
	
	/**
	 * Executor - 
	 * 
	 * @author NodinChan
	 *
	 */
	public static final class Executor {
		
		private final Command command;
		
		private final Method method;
		
		private final String name;
		private final String description;
		private final String[] aliases;
		private final String usage;
		
		private final int maxArgLength;
		private final int minArgLength;
		
		public Executor(Command command, Method method) {
			this.command = command;
			this.method = method;
			this.name = method.getAnnotation(CommandInfo.class).name();
			this.description = method.getAnnotation(CommandDescription.class).description();
			this.aliases = method.getAnnotation(CommandAlias.class).aliases();
			this.usage = method.getAnnotation(CommandUsage.class).usage();
			this.maxArgLength = method.getAnnotation(CommandInfo.class).maxArgs();
			this.minArgLength = method.getAnnotation(CommandInfo.class).minArgs();
		}
		
		@Override
		public boolean equals(Object object) {
			if (object instanceof Executor)
				if (((Executor) object).method.equals(method))
					if (((Executor) object).command.equals(command))
						return true;
			
			return false;
		}
		
		public void execute(CommandSender sender, String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			method.invoke(command, sender, args);
		}
		
		public String[] getAliases() {
			return aliases;
		}
		
		public Command getCommand() {
			return command;
		}
		
		public String getDescription() {
			return description;
		}
		
		public int getMaximumArgumentLength() {
			return maxArgLength;
		}
		
		public Method getMethod() {
			return method;
		}
		
		public int getMinimumArgumentLength() {
			return minArgLength;
		}
		
		public String getName() {
			return name;
		}
		
		public String getUsage() {
			return usage;
		}
		
		@Override
		public String toString() {
			return "Command:" + method.getAnnotation(CommandInfo.class).name();
		}
	}
}