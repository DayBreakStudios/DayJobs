package me.dbstudios.dayjobs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DayJobsCommands {
	private DayJobs common;

	private static String name = null;
	private static Integer[] upper = {null, null, null};
	private static Integer[] lower = {null, null, null};
	private static String order = null;
	private static String acl = null;
	
	public DayJobsCommands(DayJobs instance) {
		common = instance;
	}
	
	public Boolean tryCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("job")) {
			switch (args.length) {
				case 1:
					if (args[0].equalsIgnoreCase("list") && common.hasPerm(((Player)sender).getDisplayName(), "player.list", false)) {
						List<String> jobs = common.getJobList();

						sender.sendMessage(common.prefix + "Available jobs:");
						if (jobs == null) {
							sender.sendMessage(ChatColor.AQUA + "--> No jobs available.");
						} else {
							for (String job : jobs) {
								String fname = common.getInfo(job, "friendly-name");

								sender.sendMessage(ChatColor.AQUA + "--> " + job + " (" + fname + ")");
							}
						}

						return true;
					} else if (args[0].equalsIgnoreCase("info") && common.hasPerm(((Player)sender).getDisplayName(), "player.info", false)) {
						String job = common.getJob(((Player)sender).getDisplayName());
						String bio = common.getInfo(job, "bio");
						String fname = common.getInfo(job, "friendly-name");
						String canPlace = common.parseToLine(common.getInfo(job, "can-place"));
						String canUse = common.parseToLine(common.getInfo(job, "can-use"));
						String canWear = common.parseToLine(common.getInfo(job, "can-wear"));
						String canBreak = common.parseToLine(common.getInfo(job, "can-break"));
						
						if (canPlace == null) {
							canPlace = "Nothing";
						}
						if (canUse == null) {
							canUse = "Nothing";
						}
						if (canWear == null) {
							canWear = "Nothing";
						}
						if (canBreak  == null) {
							canBreak = "Nothing";
						}

						sender.sendMessage(common.prefix + "Job info for '" + job + "'");
						sender.sendMessage(ChatColor.AQUA + "--> Name: " + ChatColor.DARK_AQUA + fname);
						sender.sendMessage(ChatColor.AQUA + "--> Bio: " + ChatColor.DARK_AQUA + bio);
						sender.sendMessage(ChatColor.AQUA + "--> Can place: " + ChatColor.DARK_AQUA + canPlace);
						sender.sendMessage(ChatColor.AQUA + "--> Can use: " + ChatColor.DARK_AQUA + canUse);
						sender.sendMessage(ChatColor.AQUA + "--> Can wear: " + ChatColor.DARK_AQUA + canWear);
						sender.sendMessage(ChatColor.AQUA + "--> Can break: " + ChatColor.DARK_AQUA + canBreak);

						return true;
                    } else if (args[0].equalsIgnoreCase("help")) {
                        sender.sendMessage(common.prefix + "Commands and syntax:");
                        
                        sender.sendMessage(ChatColor.DARK_AQUA + "--> /job list");
                        sender.sendMessage(ChatColor.AQUA + "  Display available jobs");
                        sender.sendMessage(ChatColor.DARK_AQUA + "--> /job info [<job>]");
                        sender.sendMessage(ChatColor.AQUA + "  Display info on your job, or on <job> if given");
                        sender.sendMessage(ChatColor.DARK_AQUA + "--> /job whois <player>");
                        sender.sendMessage(ChatColor.AQUA + "  Display basic info for <player>");
                        sender.sendMessage(ChatColor.DARK_AQUA + "--> /job change <job>");
                        sender.sendMessage(ChatColor.AQUA + "  Request to be changed to <job>");
                        
                        return true;
					} else {
						return false;
					}
					
				case 2:
					if (args[0].equalsIgnoreCase("change") && common.hasPerm(((Player)sender).getDisplayName(), "player.change", false)) {
						if (common.jobExists(args[1])) {
							String time = (new SimpleDateFormat("M/d @ h:mm a").format(Calendar.getInstance().getTime()));

							if (common.createTicket(((Player)sender).getDisplayName(), time, args[1])) {
								sender.sendMessage(common.prefix + ChatColor.AQUA + "Change request submitted successfully.");
							} else {
								sender.sendMessage(common.prefix + ChatColor.RED + "Error submitting ticket...");
							}

							return true;
						}
					} else if (args[0].equalsIgnoreCase("whois") && common.hasPerm(((Player)sender).getDisplayName(), "player.whois", false)) {
						String job = common.getJob(args[1]);

						if (job != null) {
							sender.sendMessage(common.prefix + "Whois lookup for '" + args[1] + "':"); // <-- Happy face 'cuz we found them!
							sender.sendMessage(ChatColor.AQUA + "--> Job: " + ChatColor.DARK_AQUA + job);
						} else {
							sender.sendMessage(common.prefix + ChatColor.RED + "Whois failed: " +
									ChatColor.DARK_AQUA + "Player '" + args[1] + "' does not exist."); // <-- sad face 'cuz we didn't...
						}

						return true;
					} else if (args[0].equalsIgnoreCase("info") && common.hasPerm(((Player)sender).getDisplayName(), "player.info", false)) {
						if (common.jobExists(args[1])) {
							String job = args[1];
							String bio = common.getInfo(job, "bio");
							String fname = common.getInfo(job, "friendly-name");
							String canPlace = common.parseToLine(common.getInfo(job, "can-place"));
							String canUse = common.parseToLine(common.getInfo(job, "can-use"));
							String canWear = common.parseToLine(common.getInfo(job, "can-wear"));
							String canBreak = common.parseToLine(common.getInfo(job, "can-break"));
							
							if (canPlace == null) {
								canPlace = "Nothing";
							}
							if (canUse == null) {
								canUse = "Nothing";
							}
							if (canWear == null) {
								canWear = "Nothing";
							}
							if (canBreak  == null) {
								canBreak = "Nothing";
							}
	
							sender.sendMessage(common.prefix + "Job info for '" + job + "'");
							sender.sendMessage(ChatColor.AQUA + "--> Name: " + ChatColor.DARK_AQUA + fname);
							sender.sendMessage(ChatColor.AQUA + "--> Bio: " + ChatColor.DARK_AQUA + bio);
							sender.sendMessage(ChatColor.AQUA + "--> Can place: " + ChatColor.DARK_AQUA + canPlace);
							sender.sendMessage(ChatColor.AQUA + "--> Can use: " + ChatColor.DARK_AQUA + canUse);
							sender.sendMessage(ChatColor.AQUA + "--> Can wear: " + ChatColor.DARK_AQUA + canWear);
							sender.sendMessage(ChatColor.AQUA + "--> Can break: " + ChatColor.DARK_AQUA + canBreak);
						} else {
							sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + "Job '" + args[1] + "' does not exist.");
						}
	
						return true;
					} else if (args[0].equalsIgnoreCase("admin")) {
						if (args[1].equalsIgnoreCase("reload") && common.hasPerm(((Player)sender).getDisplayName(), "admin.reload", true)) {
							common.reloadConf();
							common.ifDebug("YAMLs reloaded by '" + ((Player)sender).getDisplayName());
							sender.sendMessage(common.prefix + ChatColor.AQUA + "Reload successful.");
							
							return true;
						} else if (args[1].equalsIgnoreCase("tickets") && common.hasPerm(((Player)sender).getDisplayName(), "admin.tickets", true)) {
							List<String> tickets = common.getOpenTickets();
	
							sender.sendMessage(common.prefix + "Open tickets:");
							if (tickets == null) {
								sender.sendMessage(ChatColor.AQUA + "--> None!");
							} else {
								for (String subject : tickets) {
									String time = common.getTicket(subject, "time");
									String job = common.getTicket(subject, "job");
	
									sender.sendMessage(ChatColor.AQUA + "--> (" + time + ") '" + subject + "' to '" + job + "'");
								}
							}
	
							return true;
						} else if (args[1].equalsIgnoreCase("debug") && common.hasPerm(((Player)sender).getDisplayName(), "admin.debug", true)) {
							common.ifDebug("Player '" + ((Player)sender).getDisplayName() + "' toggled debug mode.");
							common.toggleDebug();
							
							return true;
						} else if (args[1].equalsIgnoreCase("help") && common.hasPerm(((Player)sender).getDisplayName(), "admin.help", true)) {
							sender.sendMessage(common.prefix + "Admin commands and syntax:");
							
							sender.sendMessage(ChatColor.DARK_AQUA + "--> /job admin debug");
							sender.sendMessage(ChatColor.AQUA + "  Toggle verbose logging");
							sender.sendMessage(ChatColor.DARK_AQUA + "--> /job admin reload");
							sender.sendMessage(ChatColor.AQUA + "  Reload configuration files");
							sender.sendMessage(ChatColor.DARK_AQUA + "--> /job admin change <player> <job>");
							sender.sendMessage(ChatColor.AQUA + "  Change <player> to <job>");
							sender.sendMessage(ChatColor.DARK_AQUA + "--> /job admin ticket [close <player>]");
							sender.sendMessage(ChatColor.AQUA + "  Display a list of open tickers, or close a request by <player>, if given");
							
							return true;
						} else {
							return false;
						}
					} else {
						return false;
					}
				
					return false;
					
			case 3:
					if (args[0].equalsIgnoreCase("zone") && common.hasPerm(((Player)sender).getDisplayName(), "admin.zones", true)) {
						if (args[1].equalsIgnoreCase("create")) {
							if (name == null) {
								name = args[2];
								
								sender.sendMessage(common.prefix + ChatColor.AQUA + "Creating new zone '" + name + "'.");
							} else {
								sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + "There is a already" +
										" a new zone in queue. Commit or discard be fore continuing.");
							}
							
							return true;
						} else if (args[1].equalsIgnoreCase("set")) {
							Location targ = ((Player)sender).getTargetBlock(null, 128).getLocation();
							Integer[] tmp = {targ.getBlockX(),
									targ.getBlockY(),
									targ.getBlockZ()};
							
							if (args[2].equalsIgnoreCase("upper")) {
								for (Integer i = 0; i < 3; i++) {
									upper[i] = tmp[i];
								}
								
								sender.sendMessage(common.prefix + ChatColor.AQUA + "Upper left-most point set to '" + upper[0] + ", " +
										upper[1] + ", " + upper[2] + "'.");
							} else if (args[2].equalsIgnoreCase("lower")) {
								for (Integer i = 0; i < 3; i++) {
									lower[i] = tmp[i];
								}
								
								sender.sendMessage(common.prefix + ChatColor.AQUA + "Lower right-most point set to '" + lower[0] + ", " +
										lower[1] + ", " + lower[2] + "'.");
							} else {
								sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + "Valid set types are " +
										"'upper' and 'lower'.");
							}
							
							return true;
						} else if (args[1].equalsIgnoreCase("order")) {
							if (args[2].equalsIgnoreCase("allow,deny") || args[2].equalsIgnoreCase("deny,allow")) {
								order = args[2];
								
								sender.sendMessage(common.prefix + ChatColor.AQUA + "Order set to '" + order + "'.");
							} else if (args[2].endsWith(",")) {
								if (args[2].startsWith("allow")) {
									order = "allow,deny";
									sender.sendMessage(common.prefix + ChatColor.DARK_AQUA + "Incomplete order. Inferring 'allow,deny'.");
								} else if (args[2].startsWith("deny")) {
									order = "deny,allow";
									sender.sendMessage(common.prefix + ChatColor.DARK_AQUA + "Incomplete order. Inferring 'deny,allow'.");
								} else {
									sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
										"Incomplete order. Could not infer order type.");	
								}
							} else {
								sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
										"Invalid order type.");
							}
							
							return true;
						} else if (args[1].equalsIgnoreCase("allow") || args[1].equalsIgnoreCase("deny")) {
							if (order != null) {
								String[] oParts = order.split(",");
								
								if (args[1].equalsIgnoreCase(oParts[1])) {
									acl = args[2];
									
									if (acl.endsWith(",")) {
										sender.sendMessage(common.prefix + ChatColor.GREEN + "Non fatal error: " + ChatColor.DARK_AQUA +
												"Comma at end of access list. Did you enter spaces?");
									}
									
									sender.sendMessage(common.prefix + ChatColor.AQUA + "Access list is '" + acl + "'.");
								} else {
									sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + 
											"Given access type does not match order. Should be '" + oParts[1] + "'.");
								}
							} else {
								sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + "Order must be set first.");
							}
							
							return true;
						} else if (args[1].equalsIgnoreCase("commit")) {
							if (name != null && upper[0] != null && lower[0] != null && order != null && acl != null) {
								if (args[2].equalsIgnoreCase("yes")) {
									if (common.createZone(name, upper, lower, order, acl)) {
										sender.sendMessage(common.prefix + ChatColor.AQUA + "New zone '" + name + "' created successfully.");
									
										clearVars();
									} else {
										sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + 
												"Could not save new zone...");
									}
								} else {
									clearVars();
									sender.sendMessage(common.prefix + ChatColor.AQUA + "Zone changes discarded.");
								}
							} else {
								sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + 
										"Cannot commit. All zone options must be set first.");
							}
							
							return true;
						} else if (args[1].equalsIgnoreCase("delete")) {
							if (common.zoneExists(args[2])) {
								if (common.deleteZone(args[2])) {
									sender.sendMessage(common.prefix + ChatColor.AQUA + "Zone '" + args[2] + "' deleted.");
								} else {
									sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
											"There was a problem deleting zone '" + args[2] + "'.");
								}
							}
							
							return true;
						} else {
							return false;
						}
					} else if (args[0].equalsIgnoreCase("admin") && common.hasPerm(((Player)sender).getDisplayName(), "admin.exempt", true)) {
						if (args[1].equalsIgnoreCase("exempt")) {
							String player = common.getPlayerName(args[2]);
							
							if (player != null) {
								if (common.toggleExempt(player)) {
									sender.sendMessage(common.prefix + ChatColor.AQUA + "Toggled exempt on '" + player + "'.");
									common.getPlayer(player).sendMessage(common.prefix + ChatColor.AQUA + ((Player)sender).getDisplayName() +
											" has toggled exempt status on you.");
								} else {
									sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
											"Could not toggle exempt on '" + player + "'.");
								}
							} else {
								sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + 
										"Could not find player '" + args[2] + "'.");
							}
							
							return true;
						} else {
							return false;
						}
					} else {
						return false;
					}
			case 4:
				if (args[0].equalsIgnoreCase("admin")) {
					if (args[1].equalsIgnoreCase("change") && common.hasPerm(((Player)sender).getDisplayName(), "admin.change", true)) {
						String player = common.getPlayerName(args[2]);
						String job = args[3];

						if (common.playerExists(player) && common.jobExists(job)) {
							if (common.changeJob(player, job)) {
								sender.sendMessage(common.prefix +
									"Successfully changed '" + player + "' to '" + job + "'");
								
								Player subject = common.getPlayer(player);
								if (subject != null) {
									subject.sendMessage(common.prefix + ChatColor.AQUA + "Your job has been changed to '" + job + "' by '" +
											((Player)sender).getDisplayName() + "'.");
								}
							} else {
								sender.sendMessage(common.prefix + ChatColor.RED + "Error: " +
										ChatColor.DARK_AQUA + "Could not change '" + player + "' to '" + job + "'...");
							}
						} else if (!common.playerExists(player)) {
							sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
									"Player '" + player + "' does not exist.");
						} else if (!common.jobExists(job)) {
							sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
									"Job '" + job + "' does not exist.");
						}
						
						return true;
					} else if (args[1].equalsIgnoreCase("tickets") && args[2].equalsIgnoreCase("close") && common.hasPerm(((Player)sender).getDisplayName(), "admin.tickets", true)) {
						String ticket = args[3];
						
						if (common.ticketExists(ticket)) {
							if (common.closeTicket(ticket)) {
								sender.sendMessage(common.prefix + ChatColor.AQUA +"Ticket for '" + ticket + "' closed successfully.");
							} else {
								sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
										"Could not close ticket for '" + ticket + "'...");
							}
						} else {
							sender.sendMessage(common.prefix + ChatColor.AQUA + "No open tickets for '" + ticket + "'.");
						}
						
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Clears all variables used to store temporary zone information
	 * during creation.
	 */
	private void clearVars() {
		for (Integer i = 0; i < 3; i++) {
			upper[i] = null;
			lower[i] = null;
		}
		name = null;
		order = null;
		acl = null;
	}
}

