package me.dbstudios.dayjobs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;

public class DJInventoryListener extends InventoryListener {
	DayJobs common;
	
	public DJInventoryListener(DayJobs instance) {
		common = instance;
	}
	
	@Override
	public void onInventoryClose(InventoryCloseEvent ev) {
		Inventory inv = ev.getPlayer().getInventory();
		String player = ev.getPlayer().getDisplayName();
		ItemStack[] armor = {inv.getItem(36), inv.getItem(37), inv.getItem(38), inv.getItem(39)};
		int i = 0;
		
		for (ItemStack item : armor) {
			if (!common.checkMatch(item.getType().name(), player, "can-wear") && !item.getType().name().equalsIgnoreCase("AIR") &&
					!common.isExempt(player)) {
				int freeSlot = inv.firstEmpty();
				
				ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_AQUA + common.getWearDenyMsg(player));
				inv.clear(36 + i);
				
				if (freeSlot != -1) {
					inv.setItem(36 + i, item);
				} else {
					Location loc = ev.getPlayer().getLocation();
					
					loc.setY(loc.getY() + 1.0);
					ev.getPlayer().getWorld().dropItem(loc, item);
					ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_AQUA + common.getWearDenyInvFullMsg(player));
				}
			}
			
			i++;
		}
	}
	
	@Override
	public void onInventoryCraft(InventoryCraftEvent ev) {
		ItemStack item = ev.getResult();
		String player = ev.getPlayer().getDisplayName();
		
		if (!common.checkMatch(item.getType().name(), player, "can-craft")) {
			item.setType(Material.AIR);
			
			ev.setCursor(item);
			ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_AQUA + common.getCraftDenyMsg(player));
		}
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent ev) {
		if (ev.getSlotType().name().equalsIgnoreCase("SMELTING")) {
			if (ev.getItem() != null) {
				Player player = ev.getPlayer();
				ItemStack item = ev.getItem();
				
				if (!common.checkMatch(item.getType().name(), player.getName(), "can-smelt")) {
					ev.setCancelled(true);
					
					player.sendMessage(common.prefix + ChatColor.DARK_AQUA + common.getSmeltDenyMsg(player.getName()));
				}
			}
		}
	}
}
