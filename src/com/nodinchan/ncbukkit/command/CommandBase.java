package com.nodinchan.ncbukkit.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.nodinchan.ncbukkit.command.casting.Parameter;
import com.nodinchan.ncbukkit.loader.Loadable;

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

public class CommandBase extends Loadable {
	
	protected final JavaPlugin plugin;
	
	protected String description;
	protected String permission;
	protected String usage;
	
	protected String[] aliases;
	
	public CommandBase(String name, JavaPlugin plugin) {
		super(name);
		this.plugin = plugin;
		this.description = "";
		this.usage = "/<command>";
		this.permission = "";
		this.aliases = new String[0];
	}
	
	/**
	 * Gets the aliases of the command
	 * 
	 * @return The command aliases
	 */
	public final String[] getAliases() {
		return aliases;
	}
	
	/**
	 * Gets the description of the command
	 * 
	 * @return The command description
	 */
	public final String getDescription() {
		return description;
	}
	
	/**
	 * Gets the permission required for usage of the command
	 * 
	 * @return The command permission
	 */
	public final String getPermission() {
		return permission;
	}
	
	/**
	 * Gets the usage of the command
	 * 
	 * @return The command usage
	 */
	public final String getUsage() {
		return usage;
	}
	
	/**
	 * Gets the parameters used in the command methods
	 * 
	 * @return the parameters used in the command methods
	 */
	public Parameter<?>[] getUsedParameters() {
		return new Parameter<?>[0];
	}
	
	/**
	 * Called when a sub-command of the main command is not found
	 * 
	 * @param sender The sender of the command
	 * 
	 * @param args The arguments of the command sent
	 */
	public void commandNotFound(CommandSender sender, String[] args) {
		sender.sendMessage("[" + plugin.getName() + "] Command usage incorrect");
		sender.sendMessage("[" + plugin.getName() + "] Usage: " + usage);
	}
	
	/**
	 * Called when the sender is invalid for the command
	 * 
	 * @param sender The sender of the command
	 */
	public void invalidSender(CommandSender sender) {
		if (sender instanceof Player)
			sender.sendMessage("[" + plugin.getName() + "] You cannot use this command as a player");
		
		if (sender instanceof ConsoleCommandSender)
			sender.sendMessage("[" + plugin.getName() + "] You cannot use this command from the console");
	}
	
	/**
	 * Called when the sender do not have permission
	 * 
	 * @param sender The sender of the command
	 */
	public void noPermission(CommandSender sender) {
		sender.sendMessage("[" + plugin.getName() + "] You do not have permission");
	}
}