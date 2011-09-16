package me.dbstudios.dayjobs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;


public class DayJobsInventoryListener extends InventoryListener {
	private DayJobs common;
	
	public DayJobsInventoryListener(DayJobs instance) {
		common = instance;
	}
	
	@Override
	public void onInventoryClose(InventoryCloseEvent ev) {
		Inventory inv = ev.getPlayer().getInventory();
		String player = ev.getPlayer().getDisplayName();
		Integer i = 0;
		ItemStack[] armor = {
				inv.getItem(36),
				inv.getItem(37),
				inv.getItem(38),
				inv.getItem(39)
				};
		
		for (ItemStack item : armor) {
			if (i > 3) { i = 3; }
			
			if (!common.checkMatch(item.getType().name(), player, "can-wear") && !(item.getType().name().equalsIgnoreCase("AIR")) &&
					!common.isExempt(player)) {
				Integer freeSlot = inv.firstEmpty();
				
				common.ifDebug("Match not found, removing '" + item.getType().name() + "' from '" + player + "'s equip slot.");
				common.ifDebug("Free slot found for '" + player + "' at slot '" + freeSlot + "'.");
				
				ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_AQUA + "You may not equip this item.");
				
				inv.clear(36 + i);
				
				if (freeSlot != -1) {
					inv.setItem(freeSlot, armor[i]);
				} else {
					Location loc = ev.getPlayer().getLocation();
					loc.setY(loc.getY() + 1);
					
					common.ifDebug("Player '" + player + "''s inventory is full. Spawning drop at '" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + "'.");
					
					ev.getPlayer().getWorld().dropItem(loc, item);
					ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_AQUA + "Your inventory is full. The item has been dropped.");
				}
			}
			
			i++;
		}
	}
}
