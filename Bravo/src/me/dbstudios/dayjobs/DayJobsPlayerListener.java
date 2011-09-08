package me.dbstudios.dayjobs;

import org.bukkit.ChatColor;
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
		if (ev.getItem() != null) {
			String used = ev.getItem().getType().name();
			String player = ev.getPlayer().getDisplayName();
			common.ifDebug("Caught PlayerInteractEvent for '" + player + "'.");
		
			Boolean matched = common.checkMatch(used, player, "can-place");
			if (!matched) {
				matched = common.checkMatch(used, player, "can-use");
			}
		
			if (!matched && !common.hasPerm(player, "exempt")) {
				common.ifDebug("PlayerInteractEvent canceled for '" + player + "'.");
				ev.setCancelled(true);
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
		if (common.getConfig("allow-all-inventory") == "false" && !common.hasPerm(ev.getPlayer().getDisplayName(), "exempt")) {
			String item = ev.getItem().getItemStack().getType().name();
			String player = ev.getPlayer().getDisplayName();
			
			common.ifDebug("Caught PlayerPickupItemEvent for '" + player + "'.");
			
			ev.setCancelled(!common.checkMatch(item, player, "can-place"));
			ev.setCancelled(!common.checkMatch(item, player, "can-use"));
			ev.setCancelled(!common.checkMatch(item, player, "can-wear"));
			
			if (ev.isCancelled()) {
				common.ifDebug("PlayerPickupItemEvent cancelled for '" + player + "'.");
			}
		}
	}
	
	@Override
	public void onPlayerMove(PlayerMoveEvent ev) {
		String player = ev.getPlayer().getDisplayName();
		Double[] locat = {ev.getPlayer().getLocation().getX(),
				ev.getPlayer().getLocation().getY(),
				ev.getPlayer().getEyeHeight(true)};
		Boolean isWithin = false;
		String inZone = null;
		
		if (common.getZones() != null) {
			for (String zone : common.getZones()) {
				Integer[] upper = common.getZoneCoords(zone, "upper");
				Integer[] lower = common.getZoneCoords(zone, "lower");
			
				for (Integer i = 0; i < 3; i++) {
					if (common.isWithin(lower[i], locat[i], upper[i])) {
						isWithin = true;
						inZone = zone;
						break;
					}
				}
			}
		}		
		
		if (inZone != null && isWithin) {
			String job = common.getJob(player);
			String order = common.getZoneInfo(inZone, "order");
			Boolean permit;
			
			if (order.startsWith("allow")) {
				String[] denyFrom = common.parseToList(common.getZoneInfo(inZone, "deny-from"));
				
				permit = true;
				
				for (String deny : denyFrom) {
					if (deny.equalsIgnoreCase(job) || deny.equalsIgnoreCase("all")) {
						permit = false;
						break;
					}
				}
			} else {
				String[] allowFrom = common.parseToList(common.getZoneInfo(inZone, "allow-from"));
				
				permit = false;
				
				for (String allow : allowFrom) {
					if (allow.equalsIgnoreCase(job) || allow.equalsIgnoreCase("all")) {
						permit = true;
						break;
					}
				}
			}
			
			if (!permit) {
				ev.getPlayer().sendMessage(ChatColor.DARK_AQUA + common.prefix + common.getZoneInfo(inZone, "deny-message"));
			}
		}
	}
}
