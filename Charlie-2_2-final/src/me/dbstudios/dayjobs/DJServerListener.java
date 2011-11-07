package me.dbstudios.dayjobs;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

public class DJServerListener extends ServerListener {
	DayJobs common;
	
	public DJServerListener(DayJobs instance) {
		common = instance;
	}
	
	@Override
	public void onPluginEnable(PluginEnableEvent ev) {
		String plugin = ev.getPlugin().getDescription().getName();
		
		common.ifDebug("Checking enabled plugin: '" + plugin + "'.");
		
		if (plugin.equalsIgnoreCase("Spout")) {
			common.enableSpout();
		} else if (plugin.equalsIgnoreCase("Permissions")) {
			common.enablePermissions();
		} else if (plugin.equalsIgnoreCase("PermissionsBukkit")) {
			common.enablePermissionsBukkit();
		} else if (plugin.equalsIgnoreCase("PermissionsEx")) {
			common.enablePermissionsEx();
		}
	}
}
