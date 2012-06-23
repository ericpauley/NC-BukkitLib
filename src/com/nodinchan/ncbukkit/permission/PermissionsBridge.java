package com.nodinchan.ncbukkit.permission;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.anjocaido.groupmanager.utils.PermissionCheckResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;

public final class PermissionsBridge {
	
	private static JavaPlugin plugin;
	
	private static Plugin permissionsPlugin;
	
	private String name = "SuperPerms";
	
	private boolean checked = false;
	
	public PermissionsBridge(JavaPlugin plugin) {
		PermissionsBridge.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(new PluginListener(), plugin);
	}
	
	/**
	 * Check if a package exists
	 * 
	 * @param packages The file path
	 * 
	 * @return True if the package exists
	 */
	public boolean exists(String pkg) {
		try { Class.forName(pkg); return true; } catch (Exception e) { return false; }
	}
	
	/**
	 * Gets the group prefix of the Player
	 * 
	 * @param player The Player to check
	 * 
	 * @return The group prefix of the Player
	 */
	public String getGroupPrefix(Player player) {
		String prefix = "";
		
		for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
			if (!permInfo.getPermission().startsWith("titanchat.g.prefix.") || !permInfo.getValue())
				continue;
			
			if (getPermission(permInfo.getPermission()) != null) {
				prefix = getPermissionNode(permInfo.getPermission()).substring(19);
				break;
			}
		}
		
		if (prefix.equals("") && using().equals(Permissions.PERMISSIONSEX)) {
			PermissionGroup[] groups = PermissionsEx.getPermissionManager().getUser(player).getGroups(player.getWorld().getName());
			
			if (groups != null && groups.length > 0) {
				for (String perm : groups[0].getPermissions(player.getWorld().getName())) {
					if (perm.startsWith("TitanChat.g.prefix.")) {
						prefix = perm.substring(19);
						break;
					}
				}
			}
		}
		
		if (prefix.equals(""))
			prefix = using().getGroupPrefix(player);
		
		return (prefix == null || prefix.equals("")) ? "" : prefix;
	}
	
	/**
	 * Gets the group suffix of the Player
	 * 
	 * @param player The Player to check
	 * 
	 * @return The group suffix of the Player
	 */
	public String getGroupSuffix(Player player) {
		String suffix = "";
		
		for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
			if (!permInfo.getPermission().startsWith("titanchat.g.suffix.") || !permInfo.getValue())
				continue;
			
			if (getPermission(permInfo.getPermission()) != null) {
				suffix = getPermissionNode(permInfo.getPermission()).substring(19);
				break;
			}
		}
		
		if (suffix.equals("") && using().equals(Permissions.PERMISSIONSEX)) {
			PermissionGroup[] groups = PermissionsEx.getPermissionManager().getUser(player).getGroups(player.getWorld().getName());
			
			if (groups != null && groups.length > 0) {
				for (String perm : groups[0].getPermissions(player.getWorld().getName())) {
					if (perm.startsWith("TitanChat.g.suffix.")) {
						suffix = perm.substring(19);
						break;
					}
				}
			}
		}
		
		if (suffix.equals(""))
			suffix = using().getGroupSuffix(player);
		
		return (suffix == null || suffix.equals("")) ? "" : suffix;
	}
	
	/**
	 * Gets the Permission
	 * 
	 * @param name The permission node
	 * 
	 * @return The Permission
	 */
	public org.bukkit.permissions.Permission getPermission(String name) {
		return plugin.getServer().getPluginManager().getPermission(name);
	}
	
	/**
	 * Gets the exact Permission Node
	 * 
	 * @param name The permission node
	 * 
	 * @return The exact Permission Node
	 */
	public String getPermissionNode(String name) {
		org.bukkit.permissions.Permission permission = getPermission(name);
		
		if (permission != null)
			return permission.getName();
		else
			return "";
	}
	
	/**
	 * Gets the Player prefix
	 * 
	 * @param player The Player to check
	 * 
	 * @return The Player prefix
	 */
	public String getPlayerPrefix(Player player) {
		String prefix = "";
		
		for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
			if (!permInfo.getPermission().startsWith("titanchat.p.prefix.") || !permInfo.getValue())
				continue;
			
			if (getPermission(permInfo.getPermission()) != null) {
				prefix = getPermissionNode(permInfo.getPermission()).substring(19);
				break;
			}
		}
		
		if (prefix.equals("") && using().equals(Permissions.PERMISSIONSEX)) {
			PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
			for (String perm : user.getPermissions(player.getWorld().getName())) {
				if (perm.startsWith("TitanChat.p.prefix.")) {
					prefix = perm.substring(19);
					break;
				}
			}
		}
		
		if (prefix.equals(""))
			prefix = using().getPlayerPrefix(player);
		
		return (prefix == null || prefix.equals("")) ? getGroupPrefix(player) : prefix;
	}
	
	/**
	 * Gets the Player suffix
	 * 
	 * @param player The Player to check
	 * 
	 * @return The Player prefix
	 */
	public String getPlayerSuffix(Player player) {
		String suffix = "";
		
		for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
			if (!permInfo.getPermission().startsWith("titanchat.p.suffix.") || !permInfo.getValue())
				continue;
			
			if (getPermission(permInfo.getPermission()) != null) {
				suffix = getPermissionNode(permInfo.getPermission()).substring(19);
				break;
			}
		}
		
		if (suffix.equals("") && using().equals(Permissions.PERMISSIONSEX)) {
			PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
			for (String perm : user.getPermissions(player.getWorld().getName())) {
				if (perm.startsWith("TitanChat.p.suffix.")) {
					suffix = perm.substring(19);
					break;
				}
			}
		}
		
		if (suffix.equals(""))
			suffix = using().getPlayerSuffix(player);
		
		return (suffix == null || suffix.equals("")) ? getGroupSuffix(player) : suffix;
	}
	
	/**
	 * Check if a Player has a permission
	 * 
	 * @param player The Player to be checked
	 * 
	 * @param permission The permission to be checked
	 * 
	 * @return True if the Player has the permission
	 */
	public boolean has(Player player, String permission) {
		return has(player, permission, false);
	}
	
	/**
	 * Check if a Player has a permission
	 * 
	 * @param player The Player to be checked
	 * 
	 * @param permission The permission to be checked
	 * 
	 * @param avoidWildcard Should wildcard and OP be avoided
	 * 
	 * @return True if the Player has the permission
	 */
	public boolean has(Player player, String permission, boolean avoidWildcard) {
		return using().has(player, permission, avoidWildcard);
	}
	
	/**
	 * Gets the Permissions System
	 * 
	 * @return The Permissions System the Server is using
	 */
	public Permissions using() {
		if (permissionsPlugin != null)
			return Permissions.fromName(permissionsPlugin.getName());
		
		return Permissions.SUPERPERMS;
	}
	
	public class PluginListener implements Listener {
		
		/**
		 * Listens to the PluginDisableEvent
		 * 
		 * @param event PlayerDisableEvent
		 */
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event) {
			if (permissionsPlugin != null) {
				if (event.getPlugin().getName().equals(name)) {
					permissionsPlugin = null;
					plugin.getLogger().log(Level.INFO, name + " unhooked");
				}
			}
		}
		
		/**
		 * Listens to the PluginEnableEvent
		 * 
		 * @param event PluginEnableEvent
		 */
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			if (permissionsPlugin == null) {
				Plugin perms = null;
				
				if (perms == null) {
					if (exists("ru.tehkode.permissions.bukkit.PermissionsEx"))
						perms = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
					
					else if (exists("de.bananaco.bpermissions.imp.Permissions"))
						perms = plugin.getServer().getPluginManager().getPlugin("bPermissions");
					
					else if (exists("com.platymuus.bukkit.permissions.PermissionsPlugin"))
						perms = plugin.getServer().getPluginManager().getPlugin("PermissionsBukkit");
					
					else if (exists("org.anjocaido.groupmanager.GroupManager"))
						perms = plugin.getServer().getPluginManager().getPlugin("GroupManager");
					
					else if (exists("org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsPlugin"))
						perms = plugin.getServer().getPluginManager().getPlugin("zPermissions");
				}
				
				if (perms != null) {
					if (perms.isEnabled()) {
						permissionsPlugin = perms;
						name = permissionsPlugin.getName();
					}
					
				} else { if (!checked) { plugin.getLogger().log(Level.INFO, name + " detected and hooked"); checked = true; } }
			}
		}
	}
	
	/**
	 * Permissions - Permissions Plugin the Server is using
	 * 
	 * @author NodinChan
	 *
	 */
	public enum Permissions {
		/**
		 * Represents PermissionsEx
		 */
		PERMISSIONSEX("PermissionsEx") {
			
			@Override
			protected String getGroupPrefix(Player player) {
				PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
				
				if (user != null) {
					if (user.getGroups(player.getWorld().getName()).length > 0) {
						PermissionGroup group = user.getGroups(player.getWorld().getName())[0];
						return (group != null) ? group.getPrefix() : "";
					}
				}
				
				return "";
			}
			
			@Override
			protected String getGroupSuffix(Player player) {
				PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
				
				if (user != null) {
					if (user.getGroups(player.getWorld().getName()).length > 0) {
						PermissionGroup group = user.getGroups(player.getWorld().getName())[0];
						return (group != null) ? group.getSuffix() : "";
					}
				}
				
				return "";
			}
			
			@Override
			protected String getPlayerPrefix(Player player) {
				PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
				return (user != null) ? user.getPrefix() : "";
			}
			
			@Override
			protected String getPlayerSuffix(Player player) {
				PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
				return (user != null) ? user.getSuffix() : "";
			}
			
			@Override
			protected boolean has(Player player, String permission, boolean avoidWildcard) {
				if (avoidWildcard) {
					PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
					
					for (String perm : user.getPermissions(player.getWorld().getName())) {
						if (perm.equals(permission))
							return true;
					}
					
				} else {
					return PermissionsEx.getPermissionManager().getUser(player).has(permission, player.getWorld().getName());
				}
				
				return false;
			}
			
			@Override
			protected void remove(Player player, String permission) {
				PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
				
				if (user != null) {
					user.removePermission(permission);
					
					if (user.getGroupsNames(player.getWorld().getName()).length > 0) {
						PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(user.getGroupsNames(player.getWorld().getName())[0]);
						group.removePermission(permission);
					}
				}
			}
		},
		/**
		 * Represents bPermissions
		 */
		BPERMISSIONS("bPermissions") {
			
			@Override
			protected String getGroupPrefix(Player player) {
				String[] groups = ApiLayer.getGroups(player.getWorld().getName(), CalculableType.USER, player.getName());
				
				if (groups != null) {
					if (groups.length > 0)
						return ApiLayer.getValue(player.getWorld().getName(), CalculableType.GROUP, groups[0], "prefix");
				}
				
				return "";
			}
			
			@Override
			protected String getGroupSuffix(Player player) {
				String[] groups = ApiLayer.getGroups(player.getWorld().getName(), CalculableType.USER, player.getName());
				
				if (groups != null) {
					if (groups.length > 0)
						return ApiLayer.getValue(player.getWorld().getName(), CalculableType.GROUP, groups[0], "suffix");
				}
				
				return "";
			}
			
			@Override
			protected String getPlayerPrefix(Player player) {
				return ApiLayer.getValue(player.getWorld().getName(), CalculableType.USER, player.getName(), "prefix");
			}
			
			@Override
			protected String getPlayerSuffix(Player player) {
				return ApiLayer.getValue(player.getWorld().getName(), CalculableType.USER, player.getName(), "suffix");
			}
			
			@Override
			protected boolean has(Player player, String permission, boolean avoidWildcard) {
				if (avoidWildcard) {
					de.bananaco.bpermissions.api.util.Permission[] perms = ApiLayer.getPermissions(player.getWorld().getName(), CalculableType.USER, player.getName());
					
					for (de.bananaco.bpermissions.api.util.Permission perm : perms) {
						if (perm.name().equals(permission) && perm.isTrue())
							return true;
					}
					
				} else {
					return ApiLayer.hasPermission(player.getWorld().getName(), CalculableType.USER, player.getName(), permission);
				}
				
				return false;
			}
			
			@Override
			protected void remove(Player player, String permission) {
				ApiLayer.removePermission(player.getWorld().getName(), CalculableType.USER, player.getName(), permission);
				
				String[] groups = ApiLayer.getGroups(player.getWorld().getName(), CalculableType.USER, player.getName());
				
				if (groups != null && groups.length > 0)
					ApiLayer.removePermission(player.getWorld().getName(), CalculableType.GROUP, groups[0], permission);
			}
		},
		/**
		 * Represents SuperPerms
		 */
		SUPERPERMS("SuperPerms") {
			
			@Override
			protected String getGroupPrefix(Player player) {
				return "";
			}
			
			@Override
			protected String getGroupSuffix(Player player) {
				return "";
			}
			
			@Override
			protected String getPlayerPrefix(Player player) {
				return "";
			}
			
			@Override
			protected String getPlayerSuffix(Player player) {
				return "";
			}
			
			@Override
			protected boolean has(Player player, String permission, boolean avoidWildcard) {
				if (avoidWildcard) {
					for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
						if (permInfo.getPermission().equalsIgnoreCase(permission) && permInfo.getValue())
							return true;
					}
					
				} else {
					return player.hasPermission(permission);
				}
				
				return false;
			}
			
			@Override
			protected void remove(Player player, String permission) {
				for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
					if (permInfo.getAttachment() != null && permInfo.getAttachment().getPlugin().equals(permissionsPlugin))
						permInfo.getAttachment().unsetPermission(permission);
				}
			}
		},
		/**
		 * Represents PermissionsBukkit
		 */
		PERMISSIONSBUKKIT("PermissionsBukkit") {
			
			@Override
			protected String getGroupPrefix(Player player) {
				return "";
			}
			
			@Override
			protected String getGroupSuffix(Player player) {
				return "";
			}
			
			@Override
			protected String getPlayerPrefix(Player player) {
				return "";
			}
			
			@Override
			protected String getPlayerSuffix(Player player) {
				return "";
			}
			
			@Override
			protected boolean has(Player player, String permission, boolean avoidWildcard) {
				if (avoidWildcard) {
					for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
						if (permInfo.getPermission().equalsIgnoreCase(permission) && permInfo.getValue())
							return true;
					}
					
				} else {
					return player.hasPermission(permission);
				}
				
				return false;
			}
			
			@Override
			protected void remove(Player player, String permission) {
				for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
					if (permInfo.getAttachment() != null && permInfo.getAttachment().getPlugin().equals(permissionsPlugin))
						permInfo.getAttachment().unsetPermission(permission);
				}
			}
		},
		/**
		 * Represents GroupManager
		 */
		GROUPMANAGER("GroupManager") {
			
			@Override
			protected String getGroupPrefix(Player player) {
				AnjoPermissionsHandler handler = ((GroupManager) PermissionsBridge.permissionsPlugin).getWorldsHolder().getWorldPermissions(player.getWorld().getName());
				return (handler != null) ? handler.getGroupPrefix(handler.getGroup(player.getName())) : "";
			}
			
			@Override
			protected String getGroupSuffix(Player player) {
				AnjoPermissionsHandler handler = ((GroupManager) PermissionsBridge.permissionsPlugin).getWorldsHolder().getWorldPermissions(player.getWorld().getName());
				return (handler != null) ? handler.getGroupSuffix(handler.getGroup(player.getName())) : "";
			}
			
			@Override
			protected String getPlayerPrefix(Player player) {
				AnjoPermissionsHandler handler = ((GroupManager) PermissionsBridge.permissionsPlugin).getWorldsHolder().getWorldPermissions(player.getWorld().getName());
				return (handler != null) ? handler.getUserPrefix(player.getName()) : "";
			}
			
			@Override
			protected String getPlayerSuffix(Player player) {
				AnjoPermissionsHandler handler = ((GroupManager) PermissionsBridge.permissionsPlugin).getWorldsHolder().getWorldPermissions(player.getWorld().getName());
				return (handler != null) ? handler.getUserSuffix(player.getName()) : "";
			}
			
			@Override
			protected boolean has(Player player, String permission, boolean avoidWildcard) {
				if (avoidWildcard) {
					OverloadedWorldHolder holder = ((GroupManager) plugin.getServer().getPluginManager().getPlugin("GroupManager")).getWorldsHolder().getWorldDataByPlayerName(player.getName());
					AnjoPermissionsHandler handler = ((GroupManager) plugin.getServer().getPluginManager().getPlugin("GroupManager")).getWorldsHolder().getWorldPermissionsByPlayerName(player.getName());
					
					if (holder != null && handler != null) {
						User gmUser = holder.getUser(player.getName());
						
						if (gmUser != null) {
							PermissionCheckResult result = handler.checkFullGMPermission(gmUser, permission, false);
							return result.resultType.equals(PermissionCheckResult.Type.EXCEPTION) || result.resultType.equals(PermissionCheckResult.Type.FOUND);
						}
					}
					
				} else {
					return ((GroupManager) permissionsPlugin).getWorldsHolder().getWorldPermissions(player.getWorld().getName()).permission(player, permission);
				}
				
				return false;
			}
			
			@Override
			protected void remove(Player player, String permission) {
				OverloadedWorldHolder holder = ((GroupManager) plugin.getServer().getPluginManager().getPlugin("GroupManager")).getWorldsHolder().getWorldDataByPlayerName(player.getName());
				if (holder != null) {
					User user = holder.getUser(player.getName());
					Group group = user.getGroup();
					
					if (user != null) { user.removePermission(permission); }
					if (group != null) { group.removePermission(permission); }
				}
			}
		},
		/**
		 * Represents zPermissions
		 */
		ZPERMISSIONS("zPermissions") {
			
			@Override
			protected String getGroupPrefix(Player player) {
				return "";
			}
			
			@Override
			protected String getGroupSuffix(Player player) {
				return "";
			}
			
			@Override
			protected String getPlayerPrefix(Player player) {
				return "";
			}
			
			@Override
			protected String getPlayerSuffix(Player player) {
				return "";
			}
			
			@Override
			protected boolean has(Player player, String permission, boolean avoidWildcard) {
				if (avoidWildcard) {
					for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
						if (permInfo.getPermission().equalsIgnoreCase(permission) && permInfo.getValue())
							return true;
					}
					
				} else {
					return player.hasPermission(permission);
				}
				
				return false;
			}
			
			@Override
			protected void remove(Player player, String permission) {
				plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player " + player.getName() + " unset " + permission);
				
				List<String> groups = permissionsPlugin.getServer().getServicesManager().load(ZPermissionsService.class).getPlayerAssignedGroups(player.getName());
				
				if (groups != null)
					plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group " + groups.get(0) + " unset " + permission);
			}
		};
		
		private String name;
		
		private static Map<String, Permissions> NAME_MAP = new HashMap<String, Permissions>();
		
		/**
		 * Enum of Permissions Plugins TitanChat supports
		 * 
		 * @param name Plugin name
		 */
		private Permissions(String name) {
			this.name = name;
		}
		
		static {
			for (Permissions permission : EnumSet.allOf(Permissions.class))
				NAME_MAP.put(permission.name, permission);
		}
		
		/**
		 * Gets the Permissions Enum from the plugin's name
		 * 
		 * @param name Plugin name
		 * 
		 * @return Permissions Enum of that name
		 */
		public static Permissions fromName(String name) {
			return NAME_MAP.get(name);
		}
		
		/**
		 * Gets the group prefix of the Player
		 * 
		 * @param player The Player to check
		 * 
		 * @return The group prefix of the Player
		 */
		protected abstract String getGroupPrefix(Player player);
		
		/**
		 * Gets the group suffix of the Player
		 * 
		 * @param player The Player to check
		 * 
		 * @return The group suffix of the Player
		 */
		protected abstract String getGroupSuffix(Player player);
		
		/**
		 * Gets the name of the Permissions Plugin
		 * 
		 * @return Permissions Plugin's name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Gets the Player prefix
		 * 
		 * @param player The Player to check
		 * 
		 * @return The Player prefix
		 */
		protected abstract String getPlayerPrefix(Player player);
		
		/**
		 * Gets the Player suffix
		 * 
		 * @param player The Player to check
		 * 
		 * @return The Player prefix
		 */
		protected abstract String getPlayerSuffix(Player player);
		
		/**
		 * Check if a Player has a permission
		 * 
		 * @param player The Player to be checked
		 * 
		 * @param permission The permission to be checked
		 * 
		 * @param avoidWildcard Should wildcard and OP be avoided
		 * 
		 * @return True if the Player has the permission
		 */
		protected abstract boolean has(Player player, String permission, boolean avoidWildcard);
		
		/**
		 * Removes the permission from the player
		 * 
		 * @param player The player to remove from
		 * 
		 * @param permission The permission to remove
		 */
		protected abstract void remove(Player player, String permission);
	}
}