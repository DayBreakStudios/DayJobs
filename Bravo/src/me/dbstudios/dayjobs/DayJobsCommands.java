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
	private static String[] upper = null;
	private static String[] lower = null;
	private static String order = null;
	private static String acl = null;
	
	public DayJobsCommands(DayJobs instance) {
		common = instance;
	}
	
	public Boolean tryCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("job")) {
			switch (args.length) {
				case 1:
					if (args[0].equalsIgnoreCase("list") && common.hasPerm(((Player)sender).getDisplayName(), "player.list")) {
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
					} else if (args[0].equalsIgnoreCase("info") && common.hasPerm(((Player)sender).getDisplayName(), "player.info")) {
						String job = common.parseToLine(common.getJob(((Player)sender).getDisplayName()));
						String bio = common.parseToLine(common.getInfo(job, "bio"));
						String fname = common.parseToLine(common.getInfo(job, "friendly-name"));
						String canPlace = common.parseToLine(common.getInfo(job, "can-place"));
						String canUse = common.parseToLine(common.getInfo(job, "can-use"));
						//String canWear = common.parseToLine(common.getInfo(job, "can-wear"));
						
						if (canPlace.length() == 0) {
							canPlace = "Nothing";
						}
						if (canUse.length() == 0) {
							canUse = "Nothing";
						}
						//if (canWear.length() == 0) {
						//	canWear = "Nothing";
						//}

						sender.sendMessage(common.prefix + "Job info for '" + job + "'");
						sender.sendMessage(ChatColor.AQUA + "--> Name: " + ChatColor.DARK_AQUA + fname);
						sender.sendMessage(ChatColor.AQUA + "--> Bio: " + ChatColor.DARK_AQUA + bio);
						sender.sendMessage(ChatColor.AQUA + "--> Can place: " + ChatColor.DARK_AQUA + canPlace);
						sender.sendMessage(ChatColor.AQUA + "--> Can use: " + ChatColor.DARK_AQUA + canUse);
						//sender.sendMessage(ChatColor.AQUA + "--> Can wear: " + ChatColor.DARK_AQUA + canWear);

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
					if (args[0].equalsIgnoreCase("change") && common.hasPerm(((Player)sender).getDisplayName(), "player.change")) {
						if (common.jobExists(args[1])) {
							String time = (new SimpleDateFormat("M/d @ h:mm a").format(Calendar.getInstance().getTime()));

							if (common.createTicket(((Player)sender).getDisplayName(), time, args[1])) {
								sender.sendMessage(common.prefix + ChatColor.AQUA + "Change request submitted successfully.");
							} else {
								sender.sendMessage(common.prefix + ChatColor.RED + "Error submitting ticket...");
							}

							return true;
						}
					} else if (args[0].equalsIgnoreCase("whois") && common.hasPerm(((Player)sender).getDisplayName(), "player.whois")) {
						String job = common.getJob(args[1]);

						if (job != null) {
							sender.sendMessage(common.prefix + "Whois lookup for '" + args[1] + "':"); // <-- Happy face 'cuz we found them!
							sender.sendMessage(ChatColor.AQUA + "--> Job: " + ChatColor.DARK_AQUA + job);
						} else {
							sender.sendMessage(common.prefix + ChatColor.RED + "Whois failed: " +
									ChatColor.DARK_AQUA + "Player '" + args[1] + "' does not exist."); // <-- sad face 'cuz we didn't...
						}

						return true;
					} else if (args[0].equalsIgnoreCase("info") && common.hasPerm(((Player)sender).getDisplayName(), "player.info")) {
						if (common.jobExists(args[1])) {
							String job = args[1];
							String bio = common.parseToLine(common.getInfo(job, "bio"));
							String fname = common.parseToLine(common.getInfo(job, "friendly-name"));
							String canPlace = common.parseToLine(common.getInfo(job, "can-place"));
							String canUse = common.parseToLine(common.getInfo(job, "can-use"));
							//String canWear = common.parseToLine(common.getInfo(job, "can-wear"));
							
							if (canPlace.length() == 0) {
								canPlace = "Nothing";
							}
							if (canUse.length() == 0) {
								canUse = "Nothing";
							}
							//if (canWear.length() == 0) {
							//	canWear = "Nothing";
							//}
	
							sender.sendMessage(common.prefix + "Job info for '" + job + "'");
							sender.sendMessage(ChatColor.AQUA + "--> Name: " + ChatColor.DARK_AQUA + fname);
							sender.sendMessage(ChatColor.AQUA + "--> Bio: " + ChatColor.DARK_AQUA + bio);
							sender.sendMessage(ChatColor.AQUA + "--> Can place: " + ChatColor.DARK_AQUA + canPlace);
							sender.sendMessage(ChatColor.AQUA + "--> Can use: " + ChatColor.DARK_AQUA + canUse);
							//sender.sendMessage(ChatColor.AQUA + "--> Can wear: " + ChatColor.DARK_AQUA + canWear);
						} else {
							sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + "Job '" + args[1] + "' does not exist.");
						}
	
						return true;
					} else if (args[0].equalsIgnoreCase("admin")) {
						if (args[1].equalsIgnoreCase("reload") && common.hasPerm(((Player)sender).getDisplayName(), "admin.reload")) {
							common.reloadConf();
							common.ifDebug("YAMLs reloaded by '" + ((Player)sender).getDisplayName());
							sender.sendMessage(common.prefix + ChatColor.AQUA + "Reload successful.");
							
							return true;
						} else if (args[1].equalsIgnoreCase("tickets") && common.hasPerm(((Player)sender).getDisplayName(), "admin.tickets")) {
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
						} else if (args[1].equalsIgnoreCase("debug") && common.hasPerm(((Player)sender).getDisplayName(), "admin.debug")) {
							common.ifDebug("Player '" + ((Player)sender).getDisplayName() + "' toggled debug mode.");
							common.toggleDebug();
							
							return true;
						} else if (args[1].equalsIgnoreCase("help") && common.hasPerm(((Player)sender).getDisplayName(), "admin.help")) {
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
					if (args[0].equalsIgnoreCase("zone") && common.hasPerm(((Player)sender).getDisplayName(), "admin.zones")) {
						if (args[1].equalsIgnoreCase("create")) {
							if (name == null) {
								name = args[1];
								
								sender.sendMessage(common.prefix + ChatColor.AQUA + "Creating new zone '" + name + "'.");
							} else {
								sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + "There is a already" +
										" a new zone in queue. Commit or discard be fore continuing.");
							}
							
							return true;
						} else if (args[1].equalsIgnoreCase("set")) {
							Location targ = ((Player)sender).getTargetBlock(null, 128).getLocation();
							String[] tmp = {String.valueOf(targ.getBlockX()),
									String.valueOf(targ.getBlockY()),
									String.valueOf(targ.getBlockZ())};
							
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
							
						}
					}
			case 4:
				if (args[0].equalsIgnoreCase("admin")) {
					if (args[1].equalsIgnoreCase("change") && common.hasPerm(((Player)sender).getDisplayName(), "admin.change")) {
						String player = args[2];
						String job = args[3];

						if (common.playerExists(player) && common.jobExists(job)) {
							if (common.changeJob(player, job)) {
								sender.sendMessage(common.prefix +
									"Successfully changed '" + player +
									"' to '" + job + "'");
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
					} else if (args[1].equalsIgnoreCase("tickets") && args[2].equalsIgnoreCase("close") && common.hasPerm(((Player)sender).getDisplayName(), "admin.tickets")) {
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
				}
			}
		}
		
		return false;
	}
}

