package me.dbstudios.dayjobs;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class DayJobs extends JavaPlugin {
	// Private variables
	private final String defPath = "plugins" + File.separator + "dbstudios" + File.separator + "DayJobs" + File.separator;
	private final Configuration config = new Configuration(new File(defPath + "config.yml"));
	private final Configuration players = new Configuration(new File(defPath + "players.yml"));
	private final Configuration tickets = new Configuration(new File(defPath + "tickets.yml"));
	private final Configuration zones = new Configuration(new File(defPath + "zones.yml"));
	private final Configuration spawns = new Configuration(new File(defPath + "spawns.yml"));
	private final Logger log = Logger.getLogger("Minecraft");
	private PermissionHandler permHandler;
	private DJBlockListener blockListener = new DJBlockListener(this);
	private DJPlayerListener playerListener = new DJPlayerListener(this);
	private DJServerListener serverListener = new DJServerListener(this);
	private DJEntityListener entityListener = new DJEntityListener(this);
	private DJInventoryListener inventoryListener;
	private Boolean debug = false;
	private Boolean usingPermissions = false;
	private Boolean usingPermissionsBukkit = false;
	@SuppressWarnings("unused")
	private Boolean usingSpout = false;
	
	// Public variables
	public final String version = "2.0";
	public final String prefix = "<DayJobs> ";
	
	@Override
	public void onEnable() {
		PluginManager manager = this.getServer().getPluginManager();
		
		config.load();
		zones.load();
		players.load();
		tickets.load();
		spawns.load();
		
		manager.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.PLUGIN_ENABLE, serverListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, playerListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Normal, this);
		
		if (manager.getPlugin("Spout") != null) {
			enableSpout();
		}
		
		if (manager.getPlugin("Permissions") != null) {
			enablePermissions();
		}
		
		if (manager.getPlugin("PermissionsBukkit") != null) {
			enablePermissionsBukkit();
		}
		
		debug = config.getBoolean("config.debug", false);
		ifDebug("Verbose logging enabled.");
		
		log.info(prefix + "Version " + version + " enabled.");
	}
	
	@Override
	public void onDisable() {
		log.info(prefix + "Plugin disabled.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("job")) {
			return (new DJCommander(this)).tryCommand(sender, label, args);
		} else {
			return false;
		}
	}
	
	/**
	 * Enables the listeners that require Spout, and registers the necessary events.
	 */
	public void enableSpout() {
		PluginManager manager = this.getServer().getPluginManager();
		
		inventoryListener = new DJInventoryListener(this);
		manager.registerEvent(Event.Type.CUSTOM_EVENT, inventoryListener, Event.Priority.Normal, this);
		manager.registerEvent(Event.Type.CUSTOM_EVENT, inventoryListener, Event.Priority.Normal, this);
		usingSpout = true;
		
		log.info(prefix + "Spout found and enabled, using " + manager.getPlugin("Spout").getDescription().getFullName() + ".");
	}
	
	/**
	 * Enables the use of Permissions.
	 */
	public void enablePermissions() {
		PluginManager manager = this.getServer().getPluginManager();
		
		permHandler = ((Permissions)manager.getPlugin("Permissions")).getHandler();
		usingPermissions = true;
		
		log.info(prefix + "Permssions found and enabled, using " + manager.getPlugin("Permissions").getDescription().getFullName() + ".");
	}
	
	public void enablePermissionsBukkit() {
		PluginManager manager = this.getServer().getPluginManager();
		
		log.info(prefix + "PermissionsBukkit found and enabled, using " + manager.getPlugin("PermissionsBukkit").getDescription().getFullName() + ".");
		usingPermissionsBukkit = true;
	}
	
	/**
	 * Outputs additional debug logging to the console if debug is enabled.
	 * 
	 * @param msg	The message to output to the conosle.
	 * @return		The debug state (if it is enabled or not).
	 */
	public Boolean ifDebug(String msg) {
		if (debug) {
			log.info(prefix + msg);
		}
		
		return debug;
	}
	
	/**
	 * Attempts to determine if the player has the given permission. Will use the currently enabled
	 * Permission handler, or Op by default.
	 * 
	 * @param player	The player to check permissions against.
	 * @param perm		The permission to check if the player has.
	 * @param opOnly	If a Permission handler is absent, is this an Op only command.
	 * @return			A boolean value representative of the player's permission status.
	 */
	public Boolean hasPerm(String player, String perm, Boolean opOnly) {
		Boolean hasPerm = false;
		perm = "dbstudios.dayjobs." + perm;
		
		if (usingPermissions) {
			hasPerm = permHandler.has(getPlayer(player), perm);
		} else if (usingPermissionsBukkit) {
			hasPerm = getPlayer(player).hasPermission(perm);
		} else {
			if (opOnly && getPlayer(player).isOp()) {
				hasPerm = true;
			} else if (!opOnly) {
				hasPerm = true;
			}
		}
		
		return hasPerm;
	}
	
	/**
	 * Gets the closest matching Player to the given pattern.
	 * 
	 * @param pattern	The pattern to match the player's name to.
	 * @return			The Player whose name is closest to the pattern.
	 */
	public Player getPlayer(String pattern) {
		return this.getServer().getPlayer(pattern);
	}
	
	/**
	 * Gets the closest matching String name of the given pattern.
	 * 
	 * @param pattern	The pattern to match the player's name to.
	 * @return			The display name of the Player whose name is closest to the pattern.
	 */
	public String getPlayerName(String pattern) {
		return this.getServer().getPlayer(pattern).getDisplayName();
	}
	
	/**
	 * Gets the Configuration object handling config.yml.
	 * 
	 * @return The Configuration object handling config.yml.
	 */
	public Configuration getConfig() {
		return config;
	}
	
	/**
	 * Gets the config.yml option at path.
	 * 
	 * @param path	The path of the option to get.
	 * @return		The value located at path.
	 */
	public String getConfig(String path) {
		return config.getString("config." + path);
	}
	
	/**
	 * Gets the Configuration object handling players.yml.
	 * 
	 * @return	The Configuration object handling players.yml.
	 */
	public Configuration getPlayerConfig() {
		return players;
	}
	
	/**
	 * Gets the players.yml option at the given path.
	 * 
	 * @param player	The player whose option is being retrieved.
	 * @param option	The option of the player to retrieve.
	 * @return			The value located at the player's option path.
	 */
	public String getPlayerConfig(String player, String option) {
		return players.getString("players." + player + "." + option);
	}
	
	/**
	 * Gets the Configuration object handling tickets.yml.
	 * 
	 * @return		The Configuration object handling tickets.yml.
	 */
	public Configuration getTicket() {
		return tickets;
	}
	
	/**
	 * Gets the ticket currently opened by the given player.
	 * 
	 * @param player	The player whose ticket is being retrieved.
	 * @return			An array containing the ticket info for the given player.<br />
	 * 					The first element is the player's desired job, the second
	 * 					is the timestamp of the request.
	 */
	public String[] getTicket(String player) {
		String[] ticket = {tickets.getString("tickets." + player + ".job"),
				tickets.getString("tickets." + player + ".time")};
		
		return ticket;
	}
	
	/**
	 * Sets the option located at the given path to val.
	 * 
	 * @param path		The path of the option to be changed.
	 * @param val		The new value of the option.
	 * @return			A Boolean representation of the success of the operation.
	 */
	public Boolean setConfig(String path, String val) {
		config.setProperty("config." + path, val);
		config.save();
		config.load();
		
		return (config.getString("config." + path) == val);
	}
	
	/**
	 * Sets the option located at the given path for player to val.
	 * 
	 * @param player	The player whose option is being changed.
	 * @param path		The path of the option to change.
	 * @param val		The new value of the option.
	 * @return			A Boolean representation of the success of the operation.
	 */
	public Boolean setPlayerConfig(String player, String path, String val) {
		players.setProperty("players." + player + "." + path, val);
		players.save();
		players.load();
		
		return (players.getString("players." + player + "." + path) == val);
	}
	
	/**
	 * Gets the player's existance based on the presence of a set job class in players.yml.
	 * 
	 * @param player	The player whose existence is being checked for.
	 * @return			A Boolean representation of the player's presence is players.yml.
	 */
	public Boolean playerExists(String player) {
		return (getPlayerConfig(player, "job") != null);
	}
	
	/**
	 * Gets the existence of the given job.
	 * 
	 * @param job		The job whose existence is being checked for.
	 * @return			A Boolean representation of the job's presence in config.yml.
	 */
	public Boolean jobExists(String job) {
		return (getConfig("jobs." + job + ".friendly-name") != null);
	}
	
	/**
	 * Gets the existence of an opened ticket made by player.
	 * 
	 * @param player	The player whose open tickets are being checked.
	 * @return			A Boolean representation of player's open ticket status.
	 */
	public Boolean ticketExists(String player) {
		String[] ticket = getTicket(player);
		return (ticket[0] != null && ticket[1] != null);
	}
	
	/**
	 * Attempts to create a new ticket for the given player.
	 * 
	 * @param player	The player requesting the job change ticket.
	 * @param job		The job that player is requesting a change to.
	 * @param time		The timestamp of the change request.
	 * @return			A Boolean representation of the operation's success.
	 */
	public Boolean createTicket(String player, String job, String time) {
		tickets.setProperty("tickets." + player + ".job", job);
		tickets.setProperty("tickets." + player + ".time", time);
		tickets.save();
		tickets.load();
		
		return ticketExists(player);
	}
	
	/**
	 * Attempts to delete the ticket opened by player.
	 * 
	 * @param player	The player whose ticket is being closed.
	 * @return			A Boolean representation of the operation's success.
	 */
	public Boolean closeTicket(String player) {
		tickets.removeProperty("tickets." + player);
		tickets.save();
		tickets.load();
		
		return (!ticketExists(player));
	}
	
	/**
	 * Gets the current job of player.
	 * 
	 * @param player	The player whose job is being retrieved.
	 * @return			The job of the given player.
	 */
	public String getJob(String player) {
		return (players.getString("players." + player + ".job"));
	}
	
	/**
	 * Checks to see if item ever equals the given jobs permits, or if the permit contains
	 * ALL or NOTHING.
	 * 
	 * @param item		The item to check a match for.
	 * @param job		The job whose permits are being checked.
	 * @param type		The type of permit to check.
	 * @return			A Boolean value representative of the presence of a match.
	 */
	public Boolean checkMatch(String item, String player, String type) {
		String job = getJob(player);
		Boolean matched = false;
		List<String> values = config.getStringList("config.all." + type, null);
		
		if (values != null) {
			for (String val : values) {
				if (val.equalsIgnoreCase("NOTHING")) {
					break;
				} else if (val.equalsIgnoreCase(item) || val.equalsIgnoreCase("ALL")) {
					matched = true;
					break;
				}
			}
		}
		
		values = config.getStringList("config.jobs." + job + "." + type, null);
		
		if (values != null) {
			for (String val : values) {
				if (val.equalsIgnoreCase("NOTHING")) {
					matched = false;
					break;
				} else if (val.equalsIgnoreCase(item) || val.equalsIgnoreCase("ALL")) {
					matched = true;
					break;
				}
			}
		}
		
		return matched;
	}

	/**
	 * Attempts to get the zone player is currently standing in.
	 * 
	 * @param player	The player whose location is being checked.
	 * @return			Returns the zone name if the player is in a zone, or null
	 * 					if the player is not in a zone.
	 */
	public String getInZone(String player) {
		List<String> zoneList = zones.getKeys("zones");
		String inZone = null;
		
		if (zoneList != null) {
			for (String zone : zoneList) {
				ifDebug("Checking inZone for zone '" + inZone + "'.");
				
				int[] upper = getZoneCoords(zone, "upper");
				int[] lower = getZoneCoords(zone, "lower");
				double[] locat = {Math.floor(getPlayer(player).getLocation().getX()),
						Math.floor(getPlayer(player).getLocation().getY()),
						Math.floor(getPlayer(player).getLocation().getZ())};
				
				if (debug) {
					for (int i = 0; i < 3; i++) {
						ifDebug("Upper[" + i + "]: " + upper[i]);
						ifDebug("Lower[" + i + "]: " + lower[i]);
						ifDebug("Locat[" + i + "]: " + locat[i]);
					}
				}
				
				if ((upper[0] >= locat[0] && lower[0] <= locat[0]) &&
					(upper[1] >= locat[1] && lower[1] <= locat[1]) &&
					(upper[2] >= locat[2] && lower[2] <= locat[2])) {
					
					inZone = zone;
					break;
				}
			}
		}
		
		return inZone;
	}
	
	/**
	 * Gets the coordinate set of the given zone.
	 * 
	 * @param zone		The zone to retrieve coordinates for.
	 * @param type		The type of coordinate set to retrieve.
	 * @return			The coordinate set of the requested zone, of the requested type.
	 */
	public int[] getZoneCoords(String zone, String type) {
		int[] coords = {zones.getInt("zones." + zone + ".coords." + type + ".x", 0),
				zones.getInt("zones." + zone + ".coords." + type + ".y", 0),
				zones.getInt("zones." + zone + ".coords." + type + ".z", 0)};
		
		return coords;
	}
	
	/**
	 * Determines if the given zone exists.
	 * 
	 * @param zone		The zone to check the existence of.
	 * @return			A Boolean representation of the zone's existence.
	 */
	public Boolean zoneExists(String zone) {
		return (zones.getString("zones." + zone + ".order") != null);
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
		zones.setProperty(base + "deny-msg", "A mysterious force pushes you away...");
		zones.setProperty(base + "coords.upper.x", upper[0]);
		zones.setProperty(base + "coords.upper.y", upper[1]);
		zones.setProperty(base + "coords.upper.z", upper[2]);
		zones.setProperty(base + "coords.lower.x", lower[0]);
		zones.setProperty(base + "coords.lower.y", lower[1]);
		zones.setProperty(base + "coords.lower.z", lower[2]);
			
		zones.save();
		zones.load();
			
		return (zoneExists(name));
	}
	
	/**
	 * Deletes the given zone.
	 * 
	 * @param zone		The zone to delete.	
	 * @return			A Boolean value representative of the success of the operation.
	 */
	public Boolean deleteZone(String zone) {
		zones.removeProperty("zones." + zone);
		zones.save();
		zones.load();
		
		return (!zoneExists(zone));
	}
	
	/**
	 * Gets the option located at path.
	 * 
	 * @param zone		The zone to retrieve the option from.
	 * @param option	The option to be retrieved.
	 * @return			The value at the given path, or null if non-existant.
	 */
	public String getZoneInfo(String zone, String path) {
		return (zones.getString("zones." + zone + "." + path));
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
		int dir = 0;
		
		if (locFrom.getX() > locTo.getX() && locFrom.getBlockZ() == locTo.getBlockZ()) {
			dir = 1;
		} else if (locFrom.getZ() > locTo.getZ() && locFrom.getBlockX() == locTo.getBlockX()) {
			dir = 2;
		} else if (locFrom.getX() < locTo.getX() && locFrom.getBlockZ() == locTo.getBlockZ()) {
			dir = 3;
		} else if (locFrom.getZ() < locTo.getZ() && locFrom.getBlockX() == locTo.getBlockX()) {
			dir = 4;
		}
		
		return dir;
	}
	
	/**
	 * Gets the set damage for the given damage type, as defined in config.yml.
	 * 
	 * @param player		The player to check damage for.
	 * @param dCause		The cause of the incurred damage.
	 * @param defaultDamage	The default value to return if no option is set for the given damage type.
	 * @return				The damage to deal to the player.
	 */
	public int getDamage(String player, DamageCause dCause, int defaultDamage) {
		String job = getJob(player);
		List<String> damages = config.getKeys("config.all.damages");
		int damage = defaultDamage;
		
		if (damages != null) {
			for (String item : damages) {
				if (item.equalsIgnoreCase(dCause.name())) {
					damage = config.getInt("config.all.damages." + item, defaultDamage);
					break;
				}
			}
		}
		
		damages = config.getKeys("config.jobs." + job + ".damages");
		
		if (damages != null) {
			for (String item : damages) {
				if (item.equalsIgnoreCase(dCause.name())) {
					damage = config.getInt("config.jobs." + job + ".damages." + item, defaultDamage);
					break;
				}
			}
		}
		
		return damage;
	}
	
	/**
	 * Gets the block place deny message from config.yml.
	 * 
	 * @return		The String to output when block placement is denied.
	 */
	public String getPlaceDenyMsg(String player) {
		String msg = config.getString("config.place-deny-msg");
		
		if (msg == null) {
			msg = "You may not place this type of block.";
		}
		
		msg = parseSpecialChars(msg, player);
		return msg;
	}
	
	/**
	 * Gets the block break deny message from config.yml.
	 * 
	 * @return		The String to output when block break is denied.
	 */
	public String getBreakDenyMsg(String player) {
		String msg = config.getString("config.break-deny-msg");
		
		if (msg == null) {
			msg = "You may not break this type of block.";
		}
		
		msg = parseSpecialChars(msg, player);
		
		return msg;
	}
	
	/**
	 * Gets the item usage deny message from config.yml.
	 * 
	 * @return		The String to output when item usage is denied.
	 */
	public String getUseDenyMsg(String player) {
		String msg = config.getString("config.use-deny-msg");
		
		if (msg == null) {
			msg = "You may not use type of item.";
		}
		
		msg = parseSpecialChars(msg, player);
		
		return msg;
	}
	
	/**
	 * Gets the armor usage deny message from config.yml.
	 * 
	 * @return		The String to output when armor usage is denied.
	 */
	public String getWearDenyMsg(String player) {
		String msg = config.getString("config.wear-deny-msg");
		
		if (msg == null) {
			msg = "You may not wear this type of armor.";
		}
		
		msg = parseSpecialChars(msg, player);
		
		return msg;
	}
	
	/**
	 * Gets the full inventory message (for wear denied) from config.yml.
	 * 
	 * @return		The String to output when an item is dropped as a result of denied armor.
	 */
	public String getWearDenyInvFullMsg(String player) {
		String msg = config.getString("config.wear-deny-inv-full-msg");
		
		if (msg == null) {
			msg = "Your inventory is full. The item has been dropped.";
		}
		
		msg = parseSpecialChars(msg, player);
		
		return msg;
	}
	
	/**
	 * Gets the message to display when a new player joins.
	 * 
	 * @param player	The name of the new player.
	 * @return			The message to display to the new player.
	 */
	public String getNewPlayerMsg(String player) {
		String msg = config.getString("config.new-player-msg");
		
		if (msg == null) {
			msg = "Welcome, %p. You have joined as %j. Type /job help for more info.";
		}
		
		msg = parseSpecialChars(msg, player);
		
		return msg;
	}
	
	/**
	 * Gets the message to display when a user is denied access to a zone.
	 * 
	 * @return		The message to display to the player.
	 */
	public String getZoneDenyMsg(String zone, String player) {
		String msg = zones.getString("zones." + zone + "deny-msg");
		
		if (msg == null) {
			msg = "A mysterious force pushes you away...";
		}
		
		msg = parseSpecialChars(msg, player);
		
		return msg;
	}
	
	/**
	 * Gets the player's exempt status.
	 * 
	 * @param player	Player to check exempt status for.
	 * @return			The player's exempt status.
	 */
	public Boolean isExempt(String player) {
		return (players.getBoolean("players." + player + ".exempt", false));
	}
	
	/**
	 * Attempts to create a new player in players.yml.
	 * 
	 * @param player	The new player to add to players.yml.
	 * @return			Whether or not the player exists after the operation has run.
	 */
	public Boolean createPlayer(String player) {
		players.setProperty("players." + player + ".job", config.getString("config.default-job"));
		players.setProperty("players." + player + ".exempt", false);
		players.save();
		players.load();
		
		return (playerExists(player));
	}
	
	/**
	 * Get a list of all jobs in config.yml
	 *
 	 * @return		Returns a String List containing all jobs
	 *				available to the players.
	 */
	public List<String> getJobList() {
		return (config.getKeys("config.jobs"));
	}
	
	/**
	 * Returns a specific option from within 'job's configuration
	 * section in config.yml
	 *
	 * @param job	The job whose options are being retrieved.
	 * @param node	The node, or option, under <job> that is being retrieved.
	 * @return		A String value containing the value of the option requested.
	 *				If the option is not set, or can't be retrieved, null is returned.
	 */
	public String getInfo(String job, String node) {
		return (config.getString("config.jobs." + job + "." + node));
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
	 * Reloads all {@link Configuration} files used by DayJobs.
	 */
	public void reloadConfigs() {
		config.load();
		zones.load();
		players.load();
		tickets.load();
		spawns.load();
	}
	
	/**
	 * Gets a String List of all currently open tickets.
	 *
	 * @return	A String List of all tickets, as listed in
	 *			ticket.yml.
	 */
	public List<String> getOpenTickets() {
		return (tickets.getKeys("tickets"));
	}
	
	/**
	 * Toggle debug level for DayJobs.	
	 */
	public void toggleDebug() {
		debug = !debug;
	}
	
	/**
	 * Toggles exempt status on the given player.
	 * 
	 * @param player	The player to toggle exempt on.
	 * @return			A Boolean representation of the method's
	 * 					success.
	 */
	public Boolean toggleExempt(String player) {
		Boolean exempt = players.getBoolean("players." + player + ".exempt", true);
		
		players.setProperty("players." + player + ".exempt", !exempt);
		players.save();
		players.load();
		
		return (!exempt == players.getBoolean("players." + player + ".exempt", false));
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
			players.setProperty("players." + player + ".job", job);
			
			players.save();
			players.load();

			return (getJob(player).equalsIgnoreCase(job));
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the Location of the given world's death spawn.
	 * 
	 * @param world		The world to get the death spawn for.
	 * @return			The Location of the death spawn.
	 */
	public Location getDeathSpawn(String world) {
		Location spawn = this.getServer().getWorld(world).getSpawnLocation();
		
		if (deathSpawnExists(world)) {
			spawn.setX(spawns.getDouble("death." + world + ".x", spawn.getX()));
			spawn.setY(spawns.getDouble("death." + world + ".y", spawn.getY()));
			spawn.setZ(spawns.getDouble("death." + world + ".z", spawn.getZ()));
			spawn.setYaw(Float.parseFloat(spawns.getString("death." + world + ".yaw")));
			spawn.setPitch(Float.parseFloat(spawns.getString("death." + world + ".pitch")));
		}
		
		return spawn;
	}
	
	/**
	 * Sets the new location of the death spawn.
	 * 
	 * @param spawn		The location to set the death spawn to (world based).
	 * @return			Whether or not the spawn exists after the operation.
	 */
	public Boolean setDeathSpawn(Location spawn) {
		String path = "death." + spawn.getWorld().getName() + ".";
		
		spawns.setProperty(path + "x", spawn.getX());
		spawns.setProperty(path + "y", spawn.getY());
		spawns.setProperty(path + "z", spawn.getZ());
		spawns.setProperty(path + "yaw", spawn.getYaw());
		spawns.setProperty(path + "pitch", spawn.getPitch());
		
		spawns.save();
		spawns.load();
		
		return (deathSpawnExists(spawn.getWorld().getName()));
	}
	
	/**
	 * Gets the existance of a death spawn in world.
	 * 
	 * @param world		The world to check the existance of a death spawn for.
	 * @return			A Boolean value representative of a death spawn in world.
	 */
	public Boolean deathSpawnExists(String world) {
		return (spawns.getString("death." + world + ".x") != null);
	}
	
	/**
	 * Gets the existance of a new player spawn in world.
	 * 
	 * @param world		The world to check the existance of a new player spawn for.
	 * @return			A Boolean value representative of a new player spawn in world.
	 */
	public Boolean newPlayerSpawnExists(String world) {
		return (spawns.getString("new-player-spawn." + world + ".x") != null);
	}
	
	/**
	 * Gets the Location of the given world's new player spawn.
	 * 
	 * @param world		The world to get the new player spawn for.
	 * @return			The Location of the new player spawn.
	 */
	public Location getNewPlayerSpawn(String world) {
		Location spawn = this.getServer().getWorld(world).getSpawnLocation();
		
		if (deathSpawnExists(world)) {
			spawn.setX(spawns.getDouble("new-player-spawn." + world + ".x", spawn.getX()));
			spawn.setY(spawns.getDouble("new-player-spawn." + world + ".y", spawn.getY()));
			spawn.setZ(spawns.getDouble("new-player-spawn." + world + ".z", spawn.getZ()));
			spawn.setYaw(Float.parseFloat(spawns.getString("new-player-spawn." + world + ".yaw")));
			spawn.setPitch(Float.parseFloat(spawns.getString("new-player-spawn." + world + ".pitch")));
		}
		
		return spawn;
	}
	
	/**
	 * Sets the new location of the new player spawn.
	 * 
	 * @param spawn		The location to set the new player spawn to (world based).
	 * @return			Whether or not the spawn exists after the operation.
	 */
	public Boolean setNewPlayerSpawn(Location spawn) {
		String path = "new-player-spawn." + spawn.getWorld().getName() + ".";
		
		spawns.setProperty(path + "x", spawn.getX());
		spawns.setProperty(path + "y", spawn.getY());
		spawns.setProperty(path + "z", spawn.getZ());
		spawns.setProperty(path + "yaw", spawn.getYaw());
		spawns.setProperty(path + "pitch", spawn.getPitch());
		
		spawns.save();
		spawns.load();
		
		return (deathSpawnExists(spawn.getWorld().getName()));
	}
	
	/**
	 * Gets the message to display when a crafting event is denied.
	 * 
	 * @return		The message to display to the player whose craft event was cancelled.
	 */
	public String getCraftDenyMsg(String player) {
		String msg = config.getString("config.craft-deny-msg");
		
		if (msg == null) {
			msg = "The purpose of the materials in front of you evades you...";
		}
		
		msg = parseSpecialChars(msg, player);
		
		return msg;
	}
	
	/**
	 * Parses special characters (such as %p or %j) out of a given string.
	 * 
	 * @param toParse		The string to replace special characters in.
	 * @return				The parsed string.
	 */
	public String parseSpecialChars(String toParse, String player) {
		toParse = toParse.replace("%p", player);
		toParse = toParse.replace("%j", getJob(player));
		
		return toParse;
	}
	
	/**
	 * Gets the message to display to players when the respawn.
	 * 
	 * @param player		The player who is recieving the respawn message.
	 * @return				A String contianing the respawn message.
	 */
	public String getRespawnMsg(String player) {
		String msg = config.getString("config.respawn-msg");
		
		if (msg == null) {
			msg = "Welcom back, %p";
		}
		
		msg = parseSpecialChars(msg, player);
		
		return msg;
	}
}
