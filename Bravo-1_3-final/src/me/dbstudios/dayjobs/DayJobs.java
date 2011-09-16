package me.dbstudios.dayjobs;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;


public class DayJobs extends JavaPlugin {
	// Private variables
	public String pluginDir = "plugins" + File.separator + "dbstudios" + File.separator + "DayJobs" + File.separator;
	private Logger log = Logger.getLogger("Minecraft");
	private String version = "1.3";
	private static PermissionHandler PermHandler;
	public String prefix = "<DayJobs> ";
	private Boolean debug = false;
	private Boolean usePerms = false;
		
	// Configuration links
	private Configuration config = new Configuration(new File(pluginDir + "config.yml"));
	private Configuration players = new Configuration(new File(pluginDir + "player.yml"));
	private Configuration ticket = new Configuration(new File(pluginDir + "ticket.yml"));
	private Configuration zones = new Configuration(new File(pluginDir + "zones.yml"));
	
	//Listeners
	private final DayJobsBlockListener blockListener = new DayJobsBlockListener(this);
	private final DayJobsPlayerListener playerListener = new DayJobsPlayerListener(this);
	private final DayJobsInventoryListener inventoryListener = new DayJobsInventoryListener(this);
	private final DayJobsEntityListener entityListener = new DayJobsEntityListener(this);
	
	@Override
	public void onEnable() {
		// Register listeners
	 	PluginManager manager = this.getServer().getPluginManager();
		manager.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Normal, this);
		
		if (setupSpout()) {
			manager.registerEvent(Event.Type.CUSTOM_EVENT, inventoryListener, Event.Priority.Normal, this);
		}
		
		//Initialize Permissions
		setupPerms();
						
		config.load();
		players.load();
		ticket.load();
		zones.load();
		
		if (config.getString("config.enabled") == "false") {
			log.info(prefix + "Plugin disabled (Reason: config.enabled set to 'false').");
		}
		
		if (config.getString("config.debug") == "true") {
			log.info(prefix + "Verbose logging enabled.");
			debug = true;
		}
		
		log.info(prefix + "Plugin version " + version + " enabled.");
	}
	
	@Override
	public void onDisable() {
		log.info(prefix + "Plugin disabled.");
	}
	
	public void setupPerms() {
		if (PermHandler != null) {
			return;
		}
		
		Plugin PermPlug = this.getServer().getPluginManager().getPlugin("Permissions");
		
		if (PermPlug == null) {
			log.info(prefix + "Permissions not found, defaulting to OP.");
			usePerms = false;
			return;
		}
		
		PermHandler = ((Permissions)PermPlug).getHandler();
		log.info(prefix + "Permissions found, using " + ((Permissions)PermPlug).getDescription().getFullName());
		usePerms = true;
	}
	
	public Boolean setupSpout() {
		if (this.getServer().getPluginManager().getPlugin("Spout") == null) {
			log.info("Spout not found! Many features will NOT be available.");
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return (new DayJobsCommands(this)).tryCommand(sender, cmd, label, args);
	}
	
	/* *** COMMON *** */
	
	/**
	 * Returns the Boolean 'true' if a block, or item, has been matched in
	 * 'player's job class.
	 *
	 * @param block		The block (or item) to check against.
	 * @param player	The player whose job class to check
	 * @param type		The type of check to perform (can-place, can-wear, etc.)
	 * @return 		'true' if a match is found, 'false' otherwise
	 */
	public Boolean checkMatch(String block, String player, String type) {
		Boolean matched = false;
		block = block.replace("[", "").replace("]", "");
		
		String job = getJob(player);
		String[] toCheck = parseToList(getConfig("jobs." + job + "." + type));
		
		for (String item : toCheck) {
			ifDebug("Checking '" + item + "' against '" + block + "'.");
			
			if (item.equalsIgnoreCase(block) || item.equalsIgnoreCase("ALL") && !item.equalsIgnoreCase("NOTHING")) {
				matched = true;
				break;
			}
		}
		
		if (!matched) {
			toCheck = parseToList(getConfig("all." + type));
			for (String item : toCheck) {
				if (item.equalsIgnoreCase(block) || item.equalsIgnoreCase("ALL")) {
					matched = true;
					break;
				}
			}
		}
		
		ifDebug("CheckMatch returning '" + matched.toString() + "'.");
		return matched;
	}
	
	/**
	 * Parse a comman separated enum list from a YAML
	 * configuration file.
	 *
	 * @param toParse	The string to parse
	 * @return		A parsed String array
	 */
	public String[] parseToList(String toParse) {
		return (toParse.replace("[", "").replace("]", "").replace(" ", "").split(","));
	}
	
	/**
	 * Get a list of all jobs in config.yml
	 *
 	 * @return	Returns a String List containing all jobs
	 *		available to the players.
	 */
	public List<String> getJobList() {
		return config.getKeys("config.jobs");
	}

	/**
	 * Returns a specific option from within 'job's configuration
	 * section in config.yml
	 *
	 * @param job	The job whose options are being retrieved.
	 * @param node	The node, or option, under <job> that is being retrieved.
	 * @return	A String value containing the value of the option requested.
	 *		If the option is not set, or can't be retrieved, null is returned.
	 */
	public String getInfo(String job, String node) {
		String toReturn = config.getString("config.jobs." + job + "." + node);
		
		if (toReturn == null) {
			toReturn = "null";
		}

		return toReturn;
	}

	/**
	 * Attempts to create a new ticket in ticket.yml.
	 *
	 * @param subject	The player the ticket is being created by.
	 * @param time		The timestamp of the ticket.
	 * @param job		The job that <subject> is requesting a change
	 * 			to.
	 * @return 		Boolean 'true' if creation was successful, 
	 *			false if there was an error.
	 */
	public Boolean createTicket(String subject, String time, String job) {
		String base = "ticket." + subject;

		ticket.setProperty(base + ".time", time);
		ticket.setProperty(base + ".job", job);
		ticket.save();

		return (getTicket(subject, "job") == job);
	}

	/**
	 * Gets the job from player.yml of 'player'.
	 *
	 * @param player	The player whose job is being retrieved.
	 * @return		The job of the given player.
	 */
	public String getJob(String player) {
		player = getPlayerName(player);
		
		String toReturn = null;
		
		if (player != null) {
			toReturn = players.getString("player."  + player + ".job");
		}
		
		return toReturn;
	}

	/**
	 * Parses a YAML value list to a more visually
	 * appealing, single line, comma separated String.
	 *
	 * @param toParse	The String result from a multi-item getString
	 *					method call.
	 * @return			A comma-separated, single line, visually appealing
	 *					version of a multi-item result from a getString
	 *					{@link Configuration} call.
	 */
	public String parseToLine(String toParse) {
		String toReturn = "";
		ifDebug("parseToLine recieved: " + toParse);
		
		if (toParse.length() > 4) {
			toParse = toParse.replace("[", "").replace("]", "").replace(",", ", ").replace("_", " ");
			
			for (String part : toParse.split(", ")) {
				String first = part.substring(0, 1).toUpperCase();
				String rest = part.substring(1, part.length()).toLowerCase();
				toReturn = toReturn + first + rest + ", ";
			}
			
			toReturn = toReturn.substring(0, toReturn.length() - 2);
		} else {
			toReturn = null;
		}
		
		return toReturn;
	}
	
	/**
	 * Checks the permissions of 'player' and attempts to find the
	 * given 'perm'.
	 *
	 * @param player	The player whose permissions are being checked.
	 * @param perm		The permission node being checked for.
	 * @param opOnly	A Boolean value representative of a permission
	 *					being available ONLY to operators should
	 *		 			Permissions not be present.
	 * @return			A Boolean value representative of the presence, or
	 *					lack thereof, of the permission node.
	 */
	public Boolean hasPerm(String player, String perm, Boolean opOnly) {
		perm = "dbstudios.dayjobs." + perm;
		Boolean hasPerm = false;
		
		if (!usePerms) {
			if (!opOnly) {
				hasPerm = true;
			} else {
				hasPerm = this.getServer().getPlayer(player).isOp();
			}
		} else {
			hasPerm = PermHandler.has(this.getServer().getPlayer(player), perm);
		}
		
		return hasPerm;
	}

	/**
	 * Reloads all {@link Configuration} files used by DayJobs.
	 */
	public void reloadConf() {
		config.load();
		ticket.load();
		players.load();
		zones.load();
	}

	/**
	 * Gets a String List of all currently open tickets.
	 *
	 * @return	A String List of all tickets, as listed in
	 *			ticket.yml.
	 */
	public List<String> getOpenTickets() {
		return (ticket.getKeys("ticket"));
	}

	/**
	 * Gets a particular setting from a player's ticket.
	 *
	 * @param subject	The player whose ticket the option is being
	 *					retrieved from.
	 * @param node		The option to retrieve from <subject>'s
	 *					ticket definition.
	 * @return			The value of the requested ticket option.
	 */
	public String getTicket(String subject, String node) {
		return ticket.getString("ticket." + subject + "." + node);
	}

	/**
	 * Attempts to find 'player's job in order to determine if the
	 * player exists or not.
	 *
	 * @param player	The player whose existence is being checked for.
	 * @return 		A Boolean representation of 'player's existence.
	 */
	public Boolean playerExists(String player) {
		return (getJob(player) != null);
	}

	/**
	 * Attempts to find 'job's friendly name within config.yml to determine it's existence
	 *
	 * @param job		The job whose existence is being checked for.
	 * @return 			A Boolean representation of 'job's existence.
	 */
	public Boolean jobExists(String job) {
		return (config.getString("config.jobs." + job + ".friendly-name") != null);
	}

	/**
	 * Changes the current job of the given 'player'
	 *
	 * @param player	The player whose job is being changed.
	 * @param job		The job that 'player' is being changed to.
	 * @return		A Boolean representation of the success of the
	 *			operation.
	 */
	public Boolean changeJob(String player, String job) {
		if (playerExists(player) && jobExists(job)) {
			players.setProperty("player." + player + ".job", job);
			players.save();

			return (getJob(player) == job);
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if a ticket has been submitted by player by attempting
	 * to retrieve it's 'time' value.
	 *
	 * @param		The player whose ticket status is being checked.
	 * @return		A Boolean representation of the existence
	 *				of a ticket submitted by 'subject'.
	 */
	public Boolean ticketExists(String subject) {
		return (getTicket(subject, "time") != null);
	}

	/**
	 * Attempts to close a ticket opened by 'subject'.
	 *
	 * @param subject	The player whose ticket is being closed.
	 * @return 			A Boolean representation of the success
	 *					of the operation.
	 */
	public Boolean closeTicket(String subject) {
		if (ticketExists(subject)) {
			ticket.removeProperty("ticket." + subject);
			ticket.save();
			return (ticket.getProperty("ticket." + subject) == null);
		} else {
			return false;
		}
	}
	
	/**
	 * Gets an option as defined in config.yml.
	 *
 	 * @param opt		The option to be retrieved from config.yml.
	 * @return			The value of the option retreived from
	 *					config.yml.
	 */
	public String getConfig(String opt) {
		String toReturn = config.getString("config." + opt);
		
		if (toReturn == null) {
			toReturn = "null";
		}
		
		return toReturn;
	}
	
	/**
	 * Writes a new value to the given option in config.yml.
	 *
	 * @param opt		The option to modify.
	 * @param val		The new value for 'opt'.
	 * @return			A Boolean representation of the success
	 * 					of the operation.
	 */
	public Boolean writeConfig(String opt, String val) {
		config.setProperty("config." + opt, val);
		config.save();
		
		reloadConf();
		
		return (config.getString("config." + opt) == val);
	}
	
	/**
	 * Attempts to create a new player in player.yml.
	 *
	 * @param player	The name of the player to create.
	 * @param job		The job to set as the job of 'player'.
	 * @return			The Boolean representation of the existence
	 *					of 'player' in player.yml.
	 */
	public Boolean createPlayer(String player, String job) {
		players.setProperty("player." + player + ".job", job);
		players.setProperty("player." + player + ".exempt", "false");
		players.save();
		
		return (getJob(player) != null);
	}
	
	/**
	 * Output additional debug information under the DayJobs tag.
	 * 
	 * @param msg		The message to output to the console.
	 */
	public void ifDebug(String msg) {
		if (debug) {
			log.info(prefix + msg);
		}
	}
	
	/**
	 * Toggle debug level for DayJobs.	
	 */
	public void toggleDebug() {
		debug = !debug;
	}
	
	/**
	 * Creates a new zone from the provided values.
	 *
	 * @param name		The name of the new zone being created.
	 * @param upper		An integer array of the upper-left coordinates
	 *					of the zone.
	 * @param lower		An integer array of the lower-right coordinates
	 * 					of the zone.
	 * @param order		The access order (allow,deny/deny,allow) of the zone.
	 * @param acl		The access list for the new zone.
	 * @return			Returns true if zone exists as determined by zoneExists.
	 */
	public Boolean createZone(String name, Integer[] upper, Integer[] lower, String order, String acl) {
		String base = "zones." + name + ".";
		String[] tmp = order.split(",");
		String fromType = tmp[1];
		
		zones.setProperty(base + "order", order);
		zones.setProperty(base + fromType + "-from", acl);
		zones.setProperty(base + "deny-message", "A mysterious force pushes you away...");
		zones.setProperty(base + "coords.upper.x", upper[0]);
		zones.setProperty(base + "coords.upper.y", upper[1]);
		zones.setProperty(base + "coords.upper.z", upper[2]);
		zones.setProperty(base + "coords.lower.x", lower[0]);
		zones.setProperty(base + "coords.lower.y", lower[1]);
		zones.setProperty(base + "coords.lower.z", lower[2]);
		
		zones.save();
		
		return (zoneExists(name));
	}
	
	/**
	 * Determines if the given zone exists based on the existence of the 'order'
	 * option in zones.yml.
	 * 
	 * @param zone		The zone to check existence for.
	 * @return			A Boolean representation of the zone's existence.
	 */
	public Boolean zoneExists(String zone) {
		return (zones.getString("zones." + zone + ".order") != null);		
	}
	
	/**
	 * Attempts to delete the given zone.
	 * 
	 * @param zone		The zone to attempt to delete.
	 * @return			A Boolean representation of the zone's existence
	 * 					after the operation.
	 */
	public Boolean deleteZone(String zone) {
		zones.removeProperty("zones." + zone);
		zones.save();
		reloadConf();
		return (!zoneExists(zone));
	}
	
	/**
	 * Gets the String List of all zones currently present.
	 * 
	 * @return		A String List of all present zones.
	 */
	public List<String> getZones() {
		return (zones.getKeys("zones"));
	}
	
	/**
	 * Gets a specific option from underneath the zones node in zones.yml.
	 * 
	 * @param zone		The zone to retrieve the option from.
	 * @param path		The path to the desired option.
	 * @return			The string present at the given path, or null if the
	 * 					option does not exist.
	 */
	public String getZoneInfo(String zone, String path) {
		return (zones.getString("zones." + zone + "." + path));
	}
	
	/**
	 * Checks to see if a player is within a zone.
	 * 
	 * @param player	The player whose current coordinates are to be checked.
	 * @param zone		The zone to check the player's location against.
	 * @return			A Boolean representation of the player's presense
	 * 					within the zone.
	 */
	public Boolean isWithin(String player, String zone) {
		Integer[] locat = getPlayerLocation(player);
		Integer[] upper = getZoneUpper(zone);
		Integer[] lower = getZoneLower(zone);
		Boolean isWithin;
		
		if (lower[0] <= locat[0] && lower[1] <= locat[1] && lower[2] <= locat[2] &&
				upper[0] >= locat[0] && upper[1] >= locat[1] && upper[2] >= locat[2]) {
			isWithin = true;			
		} else {
			isWithin = false;
		}
		
		return isWithin;	
	}
	
	/**
	 * Gets the upper-left coordinates of the given zone.
	 * 
	 * @param zone		The zone to retrieve coordinates for.
	 * @return			An Integer array containing upper the x, y,
	 * 					and z coordinates of the given zone.
	 */
	public Integer[] getZoneUpper(String zone) {
		Integer[] upper = {Integer.parseInt(getZoneInfo(zone, "coords.upper.x")),
				Integer.parseInt(getZoneInfo(zone, "coords.upper.y")),
				Integer.parseInt(getZoneInfo(zone, "coords.upper.z"))};
		
		return upper;
	}
	
	/**
	 * Gets the lower-right coordinates of the given zone.
	 * 
	 * @param zone		The zone to retrieve coordinates for.
	 * @return			An Integer array containing the lower x, y,
	 * 					and z coordinates of the given zone.
	 */
	public Integer[] getZoneLower(String zone) {
		Integer[] lower = {Integer.parseInt(getZoneInfo(zone, "coords.lower.x")),
				Integer.parseInt(getZoneInfo(zone, "coords.lower.y")),
				Integer.parseInt(getZoneInfo(zone, "coords.lower.z"))};
		
		return lower;
	}
	
	/**
	 * Gets the Block coordinates of the given player's location.
	 * 
	 * @param player	The player to retrieve the coordinates of.
	 * @return			An Integer array of the Block coordinates of the
	 * 					player's current location.
	 */
	public Integer[] getPlayerLocation(String player) {
		Location playerLoc = this.getServer().getPlayer(player).getLocation();
		Integer[] locat = {playerLoc.getBlockX(),
				playerLoc.getBlockY(),
				playerLoc.getBlockZ()};
		
		return locat;
	}
	
	/**
	 * Determines the direction a player is moving based on two sets
	 * of coordinates.
	 * 
	 * @param locFrom	The position the player was at before moving.
	 * @param locTo		The position the player is currently at.
	 * @return			An int representation of the direction the player is moving:<br />
	 * 					1 - Negative X direction,
	 * 					2 - Negative Z direction,
	 * 					3 - Positive X direction,
	 * 					0 - Positive Z direction.
	 */
	public int getDirection(Location locFrom, Location locTo) {
		int direction = -1;
				
		if (locFrom.getX() > locTo.getX() && locFrom.getBlockZ() == locTo.getBlockZ()) {
			direction = 1;
		} else if (locFrom.getZ() > locTo.getZ() && locFrom.getBlockX() == locTo.getBlockX()) {
			direction = 2;
		} else if (locFrom.getX() < locTo.getX() && locFrom.getBlockZ() == locTo.getBlockZ()) {
			direction = 3;
		} else if (locFrom.getZ() < locTo.getZ() && locFrom.getBlockX() == locTo.getBlockX()) {
			direction = 0;
		}
		
		return direction;
	}
	
	/**
	 * Gets the damage of a particular cause as defined in config.yml.
	 * 
	 * @param player	The player to get damage for.
	 * @param dCause	The cause of the damage done to player. 
	 * @param dDamage	The default value of damage to be done.
	 * @return			An int representation of the damage to be done, as
	 * 					defined in config.yml.
	 */
	public int getDamage(String player, String dCause, int dDamage) {
		String type = null;
		int damage = dDamage;
		List<String> damages = getDamages(getJob(player));
		
		if (damages != null) {
			for (String item : damages) {
				if (item.equalsIgnoreCase(dCause)) {
					type = item;
					break;
				}
			}
		}
		
		if (type == null) {
			damages = getDamages("all");
			
			if (damages != null) {
				for (String item : damages) {
					if (item.equalsIgnoreCase(dCause)) {
						type = item;
						damage = 0;
						break;
					}
				}
			}
		}
		
		if (type != null) {
			if (damage == dDamage) {
				damage = config.getInt("config.jobs." + getJob(player) + ".damages." + type, dDamage);
			} else {
				damage = config.getInt("config.all.damages." + type, dDamage);
			}
		}
		
		return damage;
	}
	
	/**
	 * Gets a String List of all set damage types as defined in config.yml.
	 * 
	 * @param job	The job for which to retrieve damage types.
	 * @return		A String List containing all configured
	 * 				damage types.
	 */
	public List<String> getDamages(String job) {
		String path = "config.jobs." + job + ".damages";
		
		if (job.equalsIgnoreCase("all")) {
			path = "config.all.damages";
		}
		return (config.getKeys(path));
	}
	
	/**
	 * Gets the display name of the given player. Case insensitive.
	 * 
	 * @param name	The case insensitive name of the player whose
	 * 				display name is being requested.
	 * @return		The proper display name of the requested player.
	 */
	public String getPlayerName(String name) {
		return (this.getServer().getPlayer(name).getDisplayName());
	}
	
	/**
	 * Gets the Player object of the player with the given name.
	 * 
	 * @param name	The player whose Player object is being retrieved.
	 * @return		The Player object of the player with the given name. 
	 */
	public Player getPlayer(String name) {
		return (this.getServer().getPlayer(name));
	}
	
	/**
	 * Toggles exempt status on the given player.
	 * 
	 * @param player	The player to toggle exempt on.
	 * @return			A Boolean representation of the method's
	 * 					success.
	 */
	public Boolean toggleExempt(String player) {
		Boolean exempt = players.getBoolean("player." + player + ".exempt", true);
		
		players.setProperty("player." + player + ".exempt", !exempt);
		
		return (!exempt == players.getBoolean("player." + player + ".exempt", false));
	}
	
	/**
	 * Determines if the given player has exempt status.
	 * 
	 * @param player	The player to check exempt status on.
	 * @return			A Boolean representation of the player's
	 * 					exempt status.
	 */
	public Boolean isExempt(String player) {
		return (players.getBoolean("player." + player + ".exempt", false));
	}
}