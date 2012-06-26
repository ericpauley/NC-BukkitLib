package com.nodinchan.ncbukkit.command;

import java.lang.reflect.Method;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.nodinchan.ncbukkit.command.info.Permission;

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

public final class Executor {
	
	private final CommandBase command;
	
	private final Method method;
	
	private final CommandManager manager;
	
	private final String permission;
	
	public Executor(CommandBase command, Method method, CommandManager manager) {
		this.command = command;
		this.method = method;
		this.manager = manager;
		
		if (method.getAnnotation(Permission.class) != null)
			permission = method.getAnnotation(Permission.class).value();
		else
			permission = "";
	}
	
	/**
	 * Called when onCommand is called in the CommandManager
	 * 
	 * @param sender The command sender
	 * 
	 * @param args The given arguments
	 * 
	 * @throws Exception
	 */
	public void execute(CommandSender sender, String[] args) throws Exception {
		if (!permission.equals("") && !sender.hasPermission(permission)) {
			command.noPermission(sender);
			return;
		}
		
		Class<?>[] parameters = method.getParameterTypes();
		
		Object[] params = new Object[parameters.length];
		
		if (sender instanceof Player) {
			if (parameters[0].isAssignableFrom(Player.class)) {
				params[0] = (Player) sender;
				
			} else { command.invalidSender(sender); return; }
			
		} else {
			if (parameters[0].isAssignableFrom(ConsoleCommandSender.class)) {
				params[0] = (ConsoleCommandSender) sender;
				
			} else { command.invalidSender(sender); return; }
		}
		
		for (int parameter = 1; parameter < parameters.length; parameter++) {
			try {
				params[parameter] = manager.castParameter(parameters[parameter], args[parameter - 1]);
				
			} catch (IndexOutOfBoundsException e) { break; }
		}
		
		method.invoke(command, params);
	}
	
	/**
	 * Gets the permission required to use the sub-command
	 * 
	 * @return The required permission
	 */
	public String getPermission() {
		return permission;
	}
}