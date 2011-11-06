package me.dbstudios.dayjobs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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
		if (ev.getInventory() instanceof PlayerInventory) {
			Inventory inv = ev.getPlayer().getInventory();
			String player = ev.getPlayer().getName();
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
	}
	
	@Override
	public void onInventoryCraft(InventoryCraftEvent ev) {
		ItemStack item = ev.getResult();
		String player = ev.getPlayer().getName();
		
		if (!common.checkMatch(item.getType().name(), player, "can-craft") && !common.isExempt(player)) {
			item.setType(Material.AIR);
			
			ev.setCursor(item);
			ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_AQUA + common.getCraftDenyMsg(player));
		}
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent ev) {
		if (ev.getPlayer().getTargetBlock(null, 4).getState() instanceof Furnace && !ev.getSlotType().name().equalsIgnoreCase("SMELTING")) {
			String player = ev.getPlayer().getName();
			ItemStack item = ev.getItem();
			
			if (item != null && !common.isExempt(player)) {
				if (!common.checkMatch(item.getType().name(), player, "can-smelt")) {
					ev.setCancelled(true);
					ev.getPlayer().sendMessage(common.prefix + ChatColor.DARK_AQUA + common.getSmeltDenyMsg(player));
				}
			}
		}
	}
}
