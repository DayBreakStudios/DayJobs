package me.dbstudios.dayjobs;

import org.bukkit.ChatColor;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class DJBlockListener extends BlockListener {
	DayJobs common;
	
	public DJBlockListener(DayJobs instance) {
		common = instance;
	}
	
	@Override
	public void onBlockPlace(BlockPlaceEvent ev) {
		String block = ev.getBlock().getType().name();
		String player = ev.getPlayer().getName();
		
		if (!common.checkMatch(block, player, "can-place") && !common.isExempt(player)) {
			ev.setCancelled(true);
			ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_AQUA + common.getPlaceDenyMsg(player));
		}
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent ev) {
		String block = ev.getBlock().getType().name();
		String player = ev.getPlayer().getName();
		
		if (!common.checkMatch(block, player, "can-break") && !common.isExempt(player)) {
			ev.setCancelled(true);
			ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_AQUA + common.getBreakDenyMsg(player));
		}
	}
}
