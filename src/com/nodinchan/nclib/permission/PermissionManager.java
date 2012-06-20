package com.nodinchan.nclib.permission;

import java.util.Map;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

public class PermissionManager {
	
	private final Plugin plugin;
	
	public PermissionManager(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public Permission getPermission(String name) {
		return plugin.getServer().getPluginManager().getPermission(name);
	}
	
	public String getPermissionNode(String name) {
		Permission permission = getPermission(name);
		
		if (permission != null)
			return permission.getName();
		else
			return "";
	}
	
	public Permission register(String permission) {
		Permission perm = new Permission(permission);
		plugin.getServer().getPluginManager().addPermission(perm);
		return perm;
	}
	
	public Permission register(String permission, Map<String, Boolean> children) {
		Permission perm = new Permission(permission, children);
		plugin.getServer().getPluginManager().addPermission(perm);
		return perm;
	}
	
	public Permission register(String permission, PermissionDefault defaultValue) {
		Permission perm = new Permission(permission, defaultValue);
		plugin.getServer().getPluginManager().addPermission(perm);
		return perm;
	}
	
	public Permission register(String permission, PermissionDefault defaultValue, Map<String, Boolean> children) {
		Permission perm = new Permission(permission, defaultValue, children);
		plugin.getServer().getPluginManager().addPermission(perm);
		return perm;
	}
	
	public Permission register(String permission, String description) {
		Permission perm = new Permission(permission, description);
		plugin.getServer().getPluginManager().addPermission(perm);
		return perm;
	}
	
	public Permission register(String permission, String description, Map<String, Boolean> children) {
		Permission perm = new Permission(permission, description, children);
		plugin.getServer().getPluginManager().addPermission(perm);
		return perm;
	}
	
	public Permission register(String permission, String description, PermissionDefault defaultValue) {
		Permission perm = new Permission(permission, description, defaultValue);
		plugin.getServer().getPluginManager().addPermission(perm);
		return perm;
	}
	
	public Permission register(String permission, String description, PermissionDefault defaultValue, Map<String, Boolean> children) {
		Permission perm = new Permission(permission, description, defaultValue, children);
		plugin.getServer().getPluginManager().addPermission(perm);
		return perm;
	}
}