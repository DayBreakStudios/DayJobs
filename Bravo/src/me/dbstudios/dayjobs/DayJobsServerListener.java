package me.dbstudios.dayjobs;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

public class DayJobsServerListener extends ServerListener {
	DayJobs common;
	
	public DayJobsServerListener(DayJobs instance) {
		common = instance;
	}

	@Override
	public void onPluginEnable(PluginEnableEvent ev) {
		if (ev.getPlugin().getDescription().getName().equalsIgnoreCase("Spout")) {
			common.setupSpout();
		}
	}
}
