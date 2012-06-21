package com.nodinchan.ncbukkit.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.nodinchan.ncbukkit.command.info.CommandAlias;
import com.nodinchan.ncbukkit.command.info.CommandDescription;
import com.nodinchan.ncbukkit.command.info.CommandInfo;
import com.nodinchan.ncbukkit.command.info.CommandUsage;

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
	public String[] invalidCommand(CommandSender sender) { return new String[0]; }
	
	/**
	 * Called when the sender did not provide any arguments
	 * 
	 * @param sender The sender of the command
	 */
	public void main(CommandSender sender) {}
	
	public boolean isMain() {
		return true;
	}
	
	/**
	 * Executor - 
	 * 
	 * @author NodinChan
	 *
	 */
	public static final class Executor {
		
		private final Command command;
		
		private final Method method;
		
		private String name;
		private String description;
		private String[] aliases;
		private String usage;
		
		private int maxArgLength;
		private int minArgLength;
		
		public Executor(Command command, Method method) {
			this.command = command;
			this.method = method;
			init(method);
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
		
		private void init(Method method) {
			CommandAlias alias = method.getAnnotation(CommandAlias.class);
			CommandDescription description = method.getAnnotation(CommandDescription.class);
			CommandInfo info = method.getAnnotation(CommandInfo.class);
			CommandUsage usage = method.getAnnotation(CommandUsage.class);
			
			this.name = info.name();
			this.maxArgLength = info.maxArgs();
			this.minArgLength = info.minArgs();
			
			if (alias != null)
				this.aliases = method.getAnnotation(CommandAlias.class).aliases();
			else
				this.aliases = new String[0];
			
			if (description != null)
				this.description = method.getAnnotation(CommandDescription.class).description();
			else
				this.description = "";
			
			if (usage != null)
				this.usage = method.getAnnotation(CommandUsage.class).usage();
			else
				this.usage = "";
		}
		
		@Override
		public String toString() {
			return "Command:" + method.getAnnotation(CommandInfo.class).name();
		}
	}
}