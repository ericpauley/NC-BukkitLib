package com.nodinchan.dynamic.command;

import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class Command extends org.bukkit.command.Command {
	
	private final Plugin plugin;
	private CommandExecutor executor;
	
	protected Command(String name, Plugin plugin) {
		super(name);
		this.plugin = plugin;
		this.executor = plugin;
		this.usageMessage = "";
	}
	
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		boolean success = false;
		
		if (!plugin.isEnabled())
			return false;
		
		if (!testPermission(sender))
			return true;
		
		try {
			success = executor.onCommand(sender, this, commandLabel, args);
		} catch (Throwable ex) {
			throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + plugin.getDescription().getFullName(), ex);
		}
		
		if (!success && usageMessage.length() > 0) {
			for (String line : usageMessage.replace("<command", commandLabel).split("\n"))
				sender.sendMessage(line);
		}
		
		return success;
	}
	
	public CommandExecutor getExecutor() {
		return executor;
	}
	
	public Plugin getPlugin() {
		return plugin;
	}
	
	public void setExecutor(CommandExecutor executor) {
		this.executor = executor;
	}
}