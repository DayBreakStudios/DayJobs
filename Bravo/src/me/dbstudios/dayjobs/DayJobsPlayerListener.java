package me.dbstudios.dayjobs;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
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
}
