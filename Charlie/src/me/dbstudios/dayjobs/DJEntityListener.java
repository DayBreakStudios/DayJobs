package me.dbstudios.dayjobs;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

public class DJEntityListener extends EntityListener {
	DayJobs common;
	
	public DJEntityListener(DayJobs instance) {
		common = instance;
	}
	
	@Override
	public void onEntityDamage(EntityDamageEvent ev) {
		if (ev.getEntity() instanceof Player) {
			String player = ((Player)ev.getEntity()).getDisplayName();
			
			if (!common.isExempt(player)) {
				int damage = common.getDamage(player, ev.getCause(), ev.getDamage());
				
				if (common.getPlayer(player).getHealth() > damage) {
					common.getPlayer(player).setHealth(common.getPlayer(player).getHealth() - damage);
				} else {
					common.getPlayer(player).setHealth(0);
				}
				
				ev.setCancelled(true);
			}
		} else if (ev instanceof EntityDamageByEntityEvent) {
			if (((EntityDamageByEntityEvent)ev).getDamager() instanceof Player) {
				Player player = ((Player)((EntityDamageByEntityEvent)ev).getDamager());
				ItemStack inHand = player.getItemInHand();
				
				if (!common.checkMatch(inHand.getType().name(), player.getName(), "can-use")) {
					ev.setCancelled(true);
					
					player.sendMessage(common.prefix + ChatColor.DARK_AQUA + common.getUseDenyMsg(player.getName()));
				}
			}
		}
	}
}
