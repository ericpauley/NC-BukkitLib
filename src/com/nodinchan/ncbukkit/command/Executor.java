package com.nodinchan.ncbukkit.command;

import java.lang.reflect.InvocationTargetException;
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
	
	private final String permission;
	
	public Executor(CommandBase command, Method method) {
		this.command = command;
		this.method = method;
		
		if (method.getAnnotation(Permission.class) != null)
			permission = method.getAnnotation(Permission.class).value();
		else
			permission = "";
	}
	
	public void execute(CommandSender sender, String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (!permission.equals("") && !sender.hasPermission(permission)) {
			command.noPermission(sender);
			return;
		}
		
		if (sender instanceof Player) {
			if (method.getParameterTypes()[0].isAssignableFrom(Player.class))
				method.invoke(command, (Player) sender, args);
			else
				command.invalidSender(sender);
			
		} else {
			if (method.getParameterTypes()[0].isAssignableFrom(ConsoleCommandSender.class))
				method.invoke(command, (ConsoleCommandSender) sender, args);
			else
				command.invalidSender(sender);
		}
	}
	
	public String getPermission() {
		return permission;
	}
}