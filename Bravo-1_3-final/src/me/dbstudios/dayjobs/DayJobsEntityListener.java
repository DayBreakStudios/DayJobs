package me.dbstudios.dayjobs;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class DayJobsEntityListener extends EntityListener {
	DayJobs common;
	
	public DayJobsEntityListener(DayJobs instance) {
		common = instance;
	}
	
	@Override
	public void onEntityDamage(EntityDamageEvent ev) {
		if (ev.getEntity() instanceof Player) {
			Player player = (Player)ev.getEntity();
			
			if (!common.isExempt(player.getDisplayName())) {
				int defaultDamage = ev.getDamage();
				int damage = common.getDamage(player.getDisplayName(), ev.getCause().name(), defaultDamage);
			
				player.damage(damage);
			}
		}
	}
}
