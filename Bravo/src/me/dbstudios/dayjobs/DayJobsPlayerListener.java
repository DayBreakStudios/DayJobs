package me.dbstudios.dayjobs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class DayJobsPlayerListener extends PlayerListener{
	private DayJobs common;
	
	public DayJobsPlayerListener(DayJobs instance) {
		common = instance;
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent ev) {
		String player = ev.getPlayer().getDisplayName();
		
		if (!common.playerExists(player)) {
			if (common.createPlayer(player, common.getConfig("default-name"))) {
				ev.getPlayer().sendMessage(common.prefix + "Welcome to the game, " + player + ".");
				ev.getPlayer().sendMessage("--> You have joined as '" + common.getJob(player) + "'. Type /job help for more information.");
			} else {
				common.ifDebug("Player '" + player + "' kicked. Could not set job class.");
				ev.getPlayer().kickPlayer("Error: Could not resolve job class... Please contact a moderator or the server owner.");
			}
		}
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent ev) {
		String player = ev.getPlayer().getDisplayName();
		
		if (ev.getItem() != null && ev.getAction().name().equalsIgnoreCase("LEFT_CLICK_BLOCK")) {
			String used = ev.getItem().getType().name();
			common.ifDebug("Caught PlayerInteractEvent for '" + player + "'.");
		
			Boolean matched = common.checkMatch(used, player, "can-use");		
			if (!matched && !common.hasPerm(player, "exempt") && !ev.isCancelled()) {
				common.ifDebug("PlayerInteractEvent canceled for '" + player + "'.");
				ev.setCancelled(true);
				ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_GREEN + "Notice: " + ChatColor.AQUA +
						"Your job class may not use this item.");
			}
		}
	}
	
	@Override
	public void onPlayerPickupItem(PlayerPickupItemEvent ev) {
		/*
		 *  If our config file tells us we should NOT allow all inventory, attempt to cancel the event with a checkMatch on
		 *  all fields.
		 * 
		 */
		if (common.getConfig("allow-all-inventory").equalsIgnoreCase("false") && !common.hasPerm(ev.getPlayer().getDisplayName(), "exempt")) {
			String item = ev.getItem().getItemStack().getType().name();
			String player = ev.getPlayer().getDisplayName();
			
			common.ifDebug("Caught PlayerPickupItemEvent for '" + player + "'.");
			
			ev.setCancelled(!common.checkMatch(item, player, "can-place"));
			ev.setCancelled(!common.checkMatch(item, player, "can-use"));
			ev.setCancelled(!common.checkMatch(item, player, "can-wear"));
			
			if (ev.isCancelled()) {
				common.ifDebug("PlayerPickupItemEvent cancelled for '" + player + "'.");
				ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_GREEN + "Notice: " + ChatColor.AQUA +
						"Your job class may not pick up this item.");
			}
		}
	}
	
	@Override
	public void onPlayerMove(PlayerMoveEvent ev) {
		String player = ev.getPlayer().getDisplayName();
		Boolean isWithin = false;
		String inZone = null;
		
		if (common.getZones() != null && !common.hasPerm(player, "exempt")) {
			for (String zone : common.getZones()) {
				if (common.isWithin(player, zone)) {
					isWithin = true;
					inZone = zone;
					break;
				}
			}
			
			if (isWithin) {
				String[] order = common.getZoneInfo(inZone, "order").split(",");
				String[] acl = common.getZoneInfo(inZone, order[1] + "-from").split(",");
				Boolean found = false;
				Boolean cancel = false;
				
				for (String job : acl) {
					if (common.getJob(player).equalsIgnoreCase(job)) {
						found = true;
						break;
					}
				}
				
				if (order[1].equalsIgnoreCase("allow")) {
					if (found) {
						cancel = false;
					} else {
						cancel = true;
					}
				} else if (order[1].equalsIgnoreCase("deny")) {
					if (found) {
						cancel = true;
					} else {
						cancel = false;
					}
				}
				
				if (cancel) {
					ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_AQUA + common.getZoneInfo(inZone, "deny-message"));
					Location newLoc = ev.getTo();
					
					switch (common.getDirection(ev.getFrom(), ev.getTo())) {
					case 0:
						newLoc.setZ(newLoc.getZ() - 1.5d);
						break;
					case 1:
						newLoc.setX(newLoc.getX() + 1.5d);
						break;
					case 2:
						newLoc.setZ(newLoc.getZ() + 1.5d);
						break;
					case 3:
						newLoc.setX(newLoc.getX() - 1.5d);
						break;
					default:
						common.ifDebug("Error: Could not determine '" + ev.getPlayer().getDisplayName() + "'s direction.");						
					}
					
					ev.getPlayer().teleport(newLoc);
				}
			}
		}
	}
}
