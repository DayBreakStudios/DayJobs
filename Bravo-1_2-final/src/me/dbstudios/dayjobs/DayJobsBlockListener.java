package me.dbstudios.dayjobs;

import org.bukkit.ChatColor;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class DayJobsBlockListener extends BlockListener {
	DayJobs common;
	
	public DayJobsBlockListener(DayJobs instance) {
		common = instance;
	}

	@Override
	public void onBlockBreak(BlockBreakEvent ev) {
		String block = ev.getBlock().getType().name();
		String player = ev.getPlayer().getDisplayName();
		
		if (!common.checkMatch(block, player, "can-break")) {
			ev.setCancelled(true);
			ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_GREEN + "Notice: " + ChatColor.AQUA +
					"Your job class may not break that block.");
		}
	}
	
	@Override
	public void onBlockPlace(BlockPlaceEvent ev) {
		String block = ev.getBlock().getType().name();
		String player = ev.getPlayer().getDisplayName();
		
		if (!common.checkMatch(block, player, "can-place")) {
			ev.setCancelled(true);
			ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_GREEN + "Notice: " + ChatColor.AQUA + 
					"Your job class may not place that block.");
		}
	}
}
