package me.dbstudios.dayjobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DJCommander {
	private DayJobs common;

	private static String name = null;
	private static Integer[] upper = {null, null, null};
	private static Integer[] lower = {null, null, null};
	private static String order = null;
	private static String acl = null;
	
	public DJCommander(DayJobs instance) {
		common = instance;
	}
	
	public Boolean tryCommand(CommandSender sender, String[] args) {
		String senderName = ((Player)sender).getName();
		
		switch (args.length) {
		case 1:
			if (args[0].equalsIgnoreCase("list") && common.hasPerm(senderName, "player.list", false)) {
				Set<String> jobs = common.getJobList();
				
				sender.sendMessage(common.prefix + ChatColor.AQUA + "Available jobs:");
				
				if (jobs == null) {
					sender.sendMessage(ChatColor.DARK_AQUA + "--> No available jobs");
				} else {
					for (String job : jobs) {
						String fname = common.getConfigFile().getString("config.jobs." + job + ".friendly-name");
						
						sender.sendMessage(ChatColor.AQUA + "--> " + fname + " (" + job + ").");
					}
				}
				
				return true;
			} else if (args[0].equalsIgnoreCase("info") && common.hasPerm(senderName, "player.info", false)) {
				String job = common.getJob(senderName);
				String bio = common.getConfigFile().getString("config.jobs." + job + ".bio");
				String fname = common.getConfigFile().getString("config.jobs." + job + ".friendly-name");
				List<?> genericList;
				String path = "config.jobs." + job;
				
				genericList = common.getConfigFile().getList(path + ".can-place");
				List<String> canPlaceList = new ArrayList<String>();
				
				if (!genericList.isEmpty() && genericList != null) {
					for (Object o : genericList) {
						if (o instanceof String) {
							String item = (String)o;
							
							if (!item.startsWith("-")) {
								if (item.startsWith("+")) {
									item = item.replace("+", "");
								}
								
								canPlaceList.add(item);
							}
						}
					}
				} else {
					canPlaceList.add("Nothing");
				}
				
				genericList = common.getConfigFile().getList(path + ".can-use");
				List<String> canUseList = new ArrayList<String>();
				
				if (!genericList.isEmpty() && genericList != null) {
					for (Object o : genericList) {
						if (o instanceof String) {
							String item = (String)o;
							
							if (!item.startsWith("-")) {
								if (item.startsWith("+")) {
									item = item.replace("+", "");
								}
								
								canUseList.add(item);
							}
						}
					}
				} else {
					canUseList.add("Nothing");
				}
				
				genericList = common.getConfigFile().getList(path + ".can-wear");
				List<String> canWearList = new ArrayList<String>();
				
				if (!genericList.isEmpty() && genericList != null) {
					for (Object o : genericList) {
						if (o instanceof String) {
							String item = (String)o;
							
							if (!item.startsWith("-")) {
								if (item.startsWith("+")) {
									item = item.replace("+", "");
								}
								
								canWearList.add(item);
							}
						}
					}
				} else {
					canWearList.add("Nothing");
				}
				
				genericList = common.getConfigFile().getList(path + ".can-break");
				List<String> canBreakList = new ArrayList<String>();
				
				if (!genericList.isEmpty() && genericList != null) {
					for (Object o : genericList) {
						if (o instanceof String) {
							String item = (String)o;
							
							if (!item.startsWith("-")) {
								if (item.startsWith("+")) {
									item = item.replace("+", "");
								}
								
								canBreakList.add(item);
							}
						}
					}
				} else {
					canBreakList.add("Nothing");
				}
				
				genericList = common.getConfigFile().getList(path + ".can-craft");
				List<String> canCraftList = new ArrayList<String>();
				
				if (!genericList.isEmpty() && genericList != null) {
					for (Object o : genericList) {
						if (o instanceof String) {
							String item = (String)o;
							
							if (!item.startsWith("-")) {
								if (item.startsWith("+")) {
									item = item.replace("+", "");
								}
								
								canCraftList.add(item);
							}
						}
					}
				} else {
					canCraftList.add("Nothing");
				}
				
				String canPlace = common.parseToLine(canPlaceList);
				String canUse   = common.parseToLine(canUseList);
				String canWear  = common.parseToLine(canWearList);
				String canBreak = common.parseToLine(canBreakList);
				String canCraft = common.parseToLine(canCraftList);
				
				sender.sendMessage(common.prefix + ChatColor.AQUA + "Job info for '" + job + "':");
				sender.sendMessage(ChatColor.AQUA + "--> Full name: " + ChatColor.DARK_AQUA + fname);
				sender.sendMessage(ChatColor.AQUA + "--> Bio: " + ChatColor.DARK_AQUA + bio);
				sender.sendMessage(ChatColor.AQUA + "--> Can place: " + ChatColor.DARK_AQUA + canPlace);
				sender.sendMessage(ChatColor.AQUA + "--> Can use: " + ChatColor.DARK_AQUA + canUse);
				sender.sendMessage(ChatColor.AQUA + "--> Can wear: " + ChatColor.DARK_AQUA + canWear);
				sender.sendMessage(ChatColor.AQUA + "--> Can break: " + ChatColor.DARK_AQUA + canBreak);
				sender.sendMessage(ChatColor.AQUA + "--> Can craft: " + ChatColor.DARK_AQUA + canCraft);
				
				return true;
			} else if (args[0].equalsIgnoreCase("help")) {				
				sender.sendMessage(common.prefix + ChatColor.AQUA + "Commands and syntax:");
				
				if (common.hasPerm(senderName, "player.list", false)) {
					sender.sendMessage(ChatColor.DARK_AQUA + "--> /job list");
					sender.sendMessage(ChatColor.AQUA + "  Display available jobs");
				}
				
				if (common.hasPerm(senderName, "player.info", false)) {
					sender.sendMessage(ChatColor.DARK_AQUA + "--> /job info [<job>]");
					sender.sendMessage(ChatColor.AQUA + "  Display info on your job, or on <job> if given");
				}
				
				if (common.hasPerm(senderName, "player.whois", false)) {
					sender.sendMessage(ChatColor.DARK_AQUA + "--> /job whois <player>");
					sender.sendMessage(ChatColor.AQUA + "  Display basic info for <player>");
				}
				
				if (common.hasPerm(senderName, "player.change", false)) {
					sender.sendMessage(ChatColor.DARK_AQUA + "--> /job change <job>");
					sender.sendMessage(ChatColor.AQUA + "  Request a job change to <job>");
				}
				
				return true;
			} else {
				return false;
			}
			
		case 2:
			if (args[0].equalsIgnoreCase("change") && common.hasPerm(senderName, "player.change", false)) {
				String job = args[1];
				
				if (common.jobExists(job)) {
					if (common.getJob(senderName).equalsIgnoreCase(common.getConfigFile().getString("config.default-job")) && common.getConfigFile().getBoolean("config.instant-first-job-change")) {
						if (common.changeJob(senderName, job)) {
							sender.sendMessage(common.prefix + ChatColor.AQUA + "Your job has been changed to '" + job + "'.");
						} else {
							sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
									"Your job could not be changed. Please contact an administrator.");
						}
					} else {
						String time = new SimpleDateFormat("M/d @ h:mm a").format(Calendar.getInstance().getTime());
						
						if (common.createTicket(senderName, job, time)) {
							sender.sendMessage(common.prefix + ChatColor.AQUA + "Job change request submitted successfully.");
						} else {
							sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + 
									"Ticket could not be submitted. Please contact an admin.");
						}
					}
				} else {
					sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
							"The job '" + job + "' does not exist.");
				}
				
				return true;
			} else if (args[0].equalsIgnoreCase("whois") && common.hasPerm(senderName, "player.whois", false)) {
				String job = common.getJob(common.getPlayerName(args[1]));
				
				if (job != null) {
					sender.sendMessage(common.prefix + ChatColor.DARK_AQUA + "Whois lookup for '" + args[1] + "':");
					sender.sendMessage(ChatColor.AQUA + "--> Job: " + ChatColor.DARK_AQUA + job);
				} else {
					sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
							"Whois lookup failed. Player '" + args[1] + "' does not exist.");
				}
				
				return true;
			} else if (args[0].equalsIgnoreCase("info") && common.hasPerm(senderName, "player.info", false)) {
				if (common.jobExists(args[1])) {
					String job = args[1];
					String bio = common.getConfigFile().getString("config.jobs." + job + ".bio");
					String fname = common.getConfigFile().getString("config.jobs." + job + ".friendly-name");
					List<?> genericList;
					String path = "config.jobs." + job;
					
					genericList = common.getConfigFile().getList(path + ".can-place");
					List<String> canPlaceList = new ArrayList<String>();
					
					if (!genericList.isEmpty() && genericList != null) {
						for (Object o : genericList) {
							if (o instanceof String) {
								String item = (String)o;
								
								if (!item.startsWith("-")) {
									if (item.startsWith("+")) {
										item = item.replace("+", "");
									}
									
									canPlaceList.add(item);
								}
							}
						}
					} else {
						canPlaceList.add("Nothing");
					}
					
					genericList = common.getConfigFile().getList(path + ".can-use");
					List<String> canUseList = new ArrayList<String>();
					
					if (!genericList.isEmpty() && genericList != null) {
						for (Object o : genericList) {
							if (o instanceof String) {
								String item = (String)o;
								
								if (!item.startsWith("-")) {
									if (item.startsWith("+")) {
										item = item.replace("+", "");
									}
									
									canUseList.add(item);
								}
							}
						}
					} else {
						canUseList.add("Nothing");
					}
					
					genericList = common.getConfigFile().getList(path + ".can-wear");
					List<String> canWearList = new ArrayList<String>();
					
					if (!genericList.isEmpty() && genericList != null) {
						for (Object o : genericList) {
							if (o instanceof String) {
								String item = (String)o;
								
								if (!item.startsWith("-")) {
									if (item.startsWith("+")) {
										item = item.replace("+", "");
									}
									
									canWearList.add(item);
								}
							}
						}
					} else {
						canWearList.add("Nothing");
					}
					
					genericList = common.getConfigFile().getList(path + ".can-break");
					List<String> canBreakList = new ArrayList<String>();
					
					if (!genericList.isEmpty() && genericList != null) {
						for (Object o : genericList) {
							if (o instanceof String) {
								String item = (String)o;
								
								if (!item.startsWith("-")) {
									if (item.startsWith("+")) {
										item = item.replace("+", "");
									}
									
									canBreakList.add(item);
								}
							}
						}
					} else {
						canBreakList.add("Nothing");
					}
					
					genericList = common.getConfigFile().getList(path + ".can-craft");
					List<String> canCraftList = new ArrayList<String>();
					
					if (!genericList.isEmpty() && genericList != null) {
						for (Object o : genericList) {
							if (o instanceof String) {
								String item = (String)o;
								
								if (!item.startsWith("-")) {
									if (item.startsWith("+")) {
										item = item.replace("+", "");
									}
									
									canCraftList.add(item);
								}
							}
						}
					} else {
						canCraftList.add("Nothing");
					}
					
					String canPlace = common.parseToLine(canPlaceList);
					String canUse   = common.parseToLine(canUseList);
					String canWear  = common.parseToLine(canWearList);
					String canBreak = common.parseToLine(canBreakList);
					String canCraft = common.parseToLine(canCraftList);
					
					sender.sendMessage(common.prefix + ChatColor.AQUA + "Job info for '" + job + "':");
					sender.sendMessage(ChatColor.AQUA + "--> Full name: " + ChatColor.DARK_AQUA + fname);
					sender.sendMessage(ChatColor.AQUA + "--> Bio: " + ChatColor.DARK_AQUA + bio);
					sender.sendMessage(ChatColor.AQUA + "--> Can place: " + ChatColor.DARK_AQUA + canPlace);
					sender.sendMessage(ChatColor.AQUA + "--> Can use: " + ChatColor.DARK_AQUA + canUse);
					sender.sendMessage(ChatColor.AQUA + "--> Can wear: " + ChatColor.DARK_AQUA + canWear);
					sender.sendMessage(ChatColor.AQUA + "--> Can break: " + ChatColor.DARK_AQUA + canBreak);
					sender.sendMessage(ChatColor.AQUA + "--> Can craft: " + ChatColor.DARK_AQUA + canCraft);
				} else {
					sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + "Job '" + args[1] + "' does not exist.");
				}
				
				return true;
			} else if (args[0].equalsIgnoreCase("telnet") && args[1].equalsIgnoreCase("mordor")) {
				sender.sendMessage(common.prefix + ChatColor.GREEN + "What are you thinking?? One does not simply telnet into Mordor!");
			} else if (args[0].equalsIgnoreCase("admin")) {
				if (args[1].equalsIgnoreCase("reload") && common.hasPerm(senderName, "admin.reload", true)) {
					common.reloadConfigs();
					common.ifDebug("Configuration files reloaded by " + senderName + ".");
					
					sender.sendMessage(common.prefix + ChatColor.AQUA + "Relaod successful.");
					
					return true;
				} else if (args[1].equalsIgnoreCase("tickets") && common.hasPerm(senderName, "admin.tickets", true)) {
					Set<String> tickets = common.getOpenTickets();
					
					sender.sendMessage(common.prefix + ChatColor.DARK_AQUA + "Open tickets: ");	
					if (tickets.isEmpty() || tickets == null) {
						sender.sendMessage(ChatColor.AQUA + "--> None! ^_^");
					} else {
						for (String subject : tickets) {
							String[] ticket = common.getTicket(subject);
							
							sender.sendMessage(ChatColor.AQUA + "--> (" + ticket[1] + ") Change '" + subject + "' to '" + ticket[0] + "'");
						}
					}
					
					return true;
				} else if (args[1].equalsIgnoreCase("debug") && common.hasPerm(senderName, "admin.debug", true)) {
					String state;
					
					if (!common.ifDebug("Player '" + senderName + "' toggled debug mode!")) {
						state = "HIGH";
					} else {
						state = "LOW";
					}
					
					common.toggleDebug();
					sender.sendMessage(common.prefix + ChatColor.AQUA + "Toggled debug verbosity (" + state + ").");
					
					return true;
				} else if (args[1].equalsIgnoreCase("help") && common.hasPerm(senderName, "admin.help", true)) {
					sender.sendMessage(common.prefix + ChatColor.DARK_AQUA + "Admin commands and syntax:");
					
					if (common.hasPerm(senderName, "admin.debug", true)) {
						sender.sendMessage(ChatColor.DARK_AQUA + "--> /job admin debug");
						sender.sendMessage(ChatColor.AQUA + "  Toggle verbose logging");
					}
					
					if (common.hasPerm(senderName, "admin.reload", true)) {
						sender.sendMessage(ChatColor.DARK_AQUA + "--> /job admin reload");
						sender.sendMessage(ChatColor.AQUA + "  Reload configuration files");
					}
					
					if (common.hasPerm(senderName, "admin.change", true)) {
						sender.sendMessage(ChatColor.DARK_AQUA + "--> /job admin change <player> <job>");
						sender.sendMessage(ChatColor.AQUA + "  Change <player> to <job>");
					}
					
					if (common.hasPerm(senderName, "admin.tickets", true)) {
						sender.sendMessage(ChatColor.DARK_AQUA + "--> /job admin ticket [close <player>]");
						sender.sendMessage(ChatColor.AQUA + "  Display a list of open tickers, or close a request by <player>, if given");
					}
					
					if (common.hasPerm(senderName, "admin.spawn", true)) {
						sender.sendMessage(ChatColor.DARK_AQUA + "--> /job admin spawn [set/tp] [death/new]");
						sender.sendMessage(ChatColor.AQUA + "  Allows for management of new player and death spawns");
					}
					
					if (common.hasPerm(senderName, "admin.zones", true)) {
						sender.sendMessage(ChatColor.DARK_AQUA + "--> /job zone");
						sender.sendMessage(ChatColor.AQUA + "  Zone commands are many. Please refer to the online documentation for help");
					}
				} else {
					return false;
				}
			}
			
		case 3:
			if (args[0].equalsIgnoreCase("zone") && common.hasPerm(senderName, "admin.zones", false)) {
				if (args[1].equalsIgnoreCase("create")) {
					if (name == null) {
						name = args[2];
						
						sender.sendMessage(common.prefix + ChatColor.AQUA + "Creating new zone '" + name + "'.");
					} else {
						sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
								"There is already an unsaved zone in the queue. Commit or discard before continuing.");
					}
					
					return true;
				} else if (args[1].equalsIgnoreCase("set")) {
					Location targ = ((Player)sender).getTargetBlock(null, 128).getLocation();
					int tmp[] = {targ.getBlockX(), targ.getBlockY(), targ.getBlockZ()};
					
					if (args[2].equalsIgnoreCase("upper")) {
						for (int i = 0; i < 3; i++) {
							upper[i] = tmp[i];
						}
						
						sender.sendMessage(common.prefix + ChatColor.AQUA + "Upper left-most point set to '" + upper[0] + ", " + upper[1] +
								", " + upper[2] + "'.");						
					} else if (args[2].equalsIgnoreCase("lower")) {
						for (int i = 0; i < 3; i++) {
							lower[i] = tmp[i];
						}
						
						sender.sendMessage(common.prefix + ChatColor.AQUA + "Lower right-most point set to '" + lower[0] + ", " + lower[1] +
								", " + lower[2] + "'.");
					} else {
						sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + "Valid set types are 'upper and 'lower'.");
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
							sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + "Incomplete order. Could not infer order type.");
						}
					} else {
						sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + "Invalid order type.");
					}
					
					return true;
				} else if (args[1].equalsIgnoreCase("allow") || args[1].equalsIgnoreCase("deny")) {
					if (order != null) {
						String[] oParts = order.split(",");
						
						if (args[1].equalsIgnoreCase(oParts[1])) {
							acl = args[2];
							
							if (acl.endsWith(",")) {
								sender.sendMessage(common.prefix + ChatColor.GREEN + "Warning: " + ChatColor.DARK_AQUA +
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
								sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + "Could not create new zone.");
							}
						} else if (args[2].equalsIgnoreCase("no")) {
							clearVars();
							sender.sendMessage(common.prefix + ChatColor.AQUA + "Zone changes discarded.");
						} else {
							sender.sendMessage(common.prefix + ChatColor.DARK_AQUA + "Invalid syntax, '" + args[2] + "' is not valid for /job zone commit.");
						}
					} else {
						sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
								"Cannot commit. All zone values must be set first.");
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
					} else {
						sender.sendMessage(common.prefix + ChatColor.DARK_AQUA + "Zone '" + args[2] + "' does not exist.");
					}
					
					return true;
				} else {
					return false;
				}
			} else if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("exempt") && common.hasPerm(senderName, "admin.exempt", true)) {
				String player = common.getPlayerName(args[2]);
				
				if (player != null && common.playerExists(player)) {
					if (common.toggleExempt(player)) {
						String state;
						
						if (common.isExempt(player)) {
							state = "on";
						} else {
							state = "off";
						}
						
						sender.sendMessage(common.prefix + ChatColor.AQUA + "Set exempt status on '" + player + "' to " + state + ".");
						
						Player toInform = common.getPlayer(player);
						
						if (toInform != null) {
							toInform.sendMessage(common.prefix + ChatColor.AQUA + senderName + " has set exempt status on you to " + state + ".");
						}
					} else {
						sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA + "Could not toggle exempt on '" + player + "'.");
					}
				} else {
					sender.sendMessage(common.prefix + ChatColor.DARK_AQUA + "Could not find player '" + player + "'.");
				}
				
				return true;
			} else {
				return false;
			}
			
		case 4:
			if (args[0].equalsIgnoreCase("admin")) {
				if (args[1].equalsIgnoreCase("change") && common.hasPerm(senderName, "admin.change", true)) {
					String player = common.getPlayerName(args[2]);
					String job = args[3];
					
					if (common.playerExists(player) && common.jobExists(job)) {						
						if (common.changeJob(player, job)) {
							sender.sendMessage(common.prefix + ChatColor.AQUA + "Successfully changed " + player + " to " + job + ".");
							
							Player subject = common.getPlayer(player);
							if (subject != null) {
								subject.sendMessage(common.prefix + ChatColor.AQUA + "Your job has been changed to '" + job + "' by " + senderName + ".");
							}
						} else {
							sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
									"Could not change " + player + "'s job to " + job + ".");
						}
					} else if (!common.playerExists(args[2])) {
						sender.sendMessage(common.prefix + ChatColor.DARK_AQUA + "Player '" + args[2] + "' does not exist.");
					} else if (!common.jobExists(args[3])) {
						sender.sendMessage(common.prefix + ChatColor.DARK_AQUA + "Job '" + args[3] + "' does not exist.");
					}
					
					return true;
				} else if (args[1].equalsIgnoreCase("tickets") && args[2].equalsIgnoreCase("close") && common.hasPerm(senderName, "admin.tickets", true)) {
					String ticket = common.getPlayerName(args[3]);
					
					if (common.ticketExists(ticket)) {
						if (common.closeTicket(ticket)) {
							sender.sendMessage(common.prefix + ChatColor.AQUA + "Ticket for " + ticket + " has been closed.");
						} else {
							sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
									"Could not closed ticket for " + ticket + ".");
						}
					}
					
					return true;
				} else if (args[1].equalsIgnoreCase("spawn")) {
					if (args[2].equalsIgnoreCase("set")) {
						String type = args[3];
						
						if (type.equalsIgnoreCase("death") && common.hasPerm(senderName, "admin.spawn.set.deathspawn", true)) {
							if (common.setDeathSpawn(((Player)sender).getLocation())) {
								sender.sendMessage(common.prefix + ChatColor.AQUA + "Death spawn set successfully.");
							} else {
								sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
										"Could not set death spawn.");
							}
						} else if (type.equalsIgnoreCase("new") && common.hasPerm(senderName, "admin.spawn.set.newspawn", true)) {
							if (common.setNewPlayerSpawn(((Player)sender).getLocation())) {
								sender.sendMessage(common.prefix + ChatColor.AQUA + "New player spawn set successfully.");
							} else {
								sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
										"Could not set new player spawn.");
							}
						} else {
							sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
									"Can only set 'new' and 'death' spawn locations.");
						}
						
						return true;
					} else if (args[2].equalsIgnoreCase("tp")) {
						String type = args[3];
						
						if (type.equalsIgnoreCase("death") && common.hasPerm(senderName, "admin.spawn.tp.deathspawn", true)) {
							sender.sendMessage(common.prefix + ChatColor.AQUA + "Teleporting to spawn location.");
							((Player)sender).teleport(common.getDeathSpawn(((Player)sender).getLocation().getWorld().getName()));
						} else if (type.equalsIgnoreCase("new") && common.hasPerm(senderName, "admin.spawn.tp.newspawn", true)) {
							sender.sendMessage(common.prefix + ChatColor.AQUA + "Teleporting to new player spawn location.");
							((Player)sender).teleport(common.getNewPlayerSpawn(((Player)sender).getLocation().getWorld().getName()));
						} else {
							sender.sendMessage(common.prefix + ChatColor.RED + "Error: " + ChatColor.DARK_AQUA +
									"Can only teleport to 'new' and 'death' spawn locations.");
						}
					}
					
					return true;
				} else {
					return false;
				}
			} else {
				return false;
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