package me.dbstudios.dayjobs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DJPlayerListener extends PlayerListener {
	DayJobs common;
	
	public DJPlayerListener(DayJobs instance) {
		common = instance;
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent ev) {
		String player = ev.getPlayer().getName();
		
		if (ev.getItem() != null && ev.getAction().name().equalsIgnoreCase("LEFT_CLICK_BLOCK") && !common.isExempt(player)) {
			String item = ev.getItem().getType().name();
			
			if (!common.checkMatch(item, player, "can-use")) {
				ev.setCancelled(true);
				ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_AQUA + common.getUseDenyMsg(player));
			}
		}
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent ev) {
		String player = ev.getPlayer().getName();
		
		common.ifDebug("Player '" + player + "' has joined the server.");
		if (!common.playerExists(player)) {
			common.ifDebug("Player not found, attempting to create in players.yml.");
			
			if (!common.createPlayer(player)) {
				common.ifDebug("Error: Player could not be created, kicking.");
				
				ev.getPlayer().getServer().broadcastMessage(common.prefix + ChatColor.DARK_RED + "Player " + player + 
						" has been kicked. Could not create player data.");
				ev.getPlayer().kickPlayer("Error: Could not create player data. Please contact an administrator.");
				
				return;
			} else {
				common.ifDebug("Player created successfully.");
				
				ev.getPlayer().teleport(common.getNewPlayerSpawn(ev.getPlayer().getWorld().getName()));
				ev.getPlayer().sendMessage(common.prefix + ChatColor.AQUA + common.getNewPlayerMsg(player));
			}
		}
		
		common.addJobPerms(player);
	}
	
	@Override
	public void onPlayerMove(PlayerMoveEvent ev) {
		String player = ev.getPlayer().getName();
		String zone = common.getInZone(player);
		
		common.ifDebug("Player '" + player + "' in zone '" + zone + "'.");
		
		if (zone != null) {
			String[] order = common.getZoneInfo(zone, "order").split(",");
			String[] acl = common.getZoneInfo(zone, order[1] + "-from").split(",");
			Boolean cancel = false;
			Boolean found = false;
			
			for (String job : acl) {
				common.ifDebug("Checking match between player '" + player + "' (" + common.getJob(player) + ") and job '" + job + "'.");
				
				if (job.equalsIgnoreCase(common.getJob(player))) {
					common.ifDebug("Match found for the above.");
					
					found = true;
					break;
				}
			}
			
			common.ifDebug("Access type: '" + order[1] + "-from'.");
			
			if (order[1].equalsIgnoreCase("allow")) {
				if (found) {
					cancel = false;
				} else {
					cancel = true;
				}
			} else {
				if (found) {
					cancel = true;
				} else {
					cancel = false;
				}
			}
			
			if (cancel) {				
				Location newLoc = ev.getTo();
				
				switch (common.getDirection(ev.getFrom(), ev.getTo())) {
				case 1:
					newLoc.setX(newLoc.getX() + 1.0d);
					break;
				case 2:
					newLoc.setZ(newLoc.getZ() + 1.0d);
					break;
				case 3:
					newLoc.setX(newLoc.getX() - 1.0d);
					break;
				case 4:
					newLoc.setZ(newLoc.getZ() - 1.0d);
					break;
				case 0:
					common.ifDebug("Error: Could not determine " + ev.getPlayer().getName() + "'s direction.");						
				}
				
				ev.getPlayer().teleport(newLoc);
				ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_AQUA + common.getZoneDenyMsg(zone, player));
			}
		}
	}
	
	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityEvent ev) {
		String player = ev.getPlayer().getName();
		
		if (ev.getPlayer().getItemInHand() != null && !common.isExempt(player)) {
			String item = ev.getPlayer().getItemInHand().getType().name();
			
			if (!common.checkMatch(item, player, "can-use")) {
				ev.setCancelled(true);
				ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_AQUA + common.getUseDenyMsg(player));
			}
		}
	}
	
	@Override
	public void onPlayerRespawn(PlayerRespawnEvent ev) {
		ev.setRespawnLocation(common.getDeathSpawn(ev.getPlayer().getWorld().getName()));
		ev.getPlayer().sendMessage(common.prefix + ChatColor.AQUA + common.getRespawnMsg(ev.getPlayer().getName()));
	}
}
