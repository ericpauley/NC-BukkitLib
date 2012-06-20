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

public abstract class Command {
	
	protected final JavaPlugin plugin;
	
	protected final CommandManager manager;
	
	public Command(JavaPlugin plugin, CommandManager manager) {
		this.plugin = plugin;
		this.manager = manager;
	}
	
	public final void invalidArgLength(CommandSender sender, Command main) {
		sender.sendMessage("[" + plugin.getName() + "] " + ChatColor.RED + "Invalid Argument Length");
		sender.sendMessage("[" + plugin.getName() + "] Usage: /" + main.getClass().getAnnotation(CommandUsage.class).usage());
	}
	
	public final void invalidArgLength(CommandSender sender, Command main, String command) {
		sender.sendMessage("[" + plugin.getName() + "] " + ChatColor.RED + "Invalid Argument Length");
		sender.sendMessage("[" + plugin.getName() + "] Usage: /" + manager.getExecutor(main, command).getUsage());
	}
	
	public abstract void invalidCommand(CommandSender sender);
	
	public abstract void main(CommandSender sender);
	
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