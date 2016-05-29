package me.JustinTheKoolKid.ZeroPrisonSell;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Admin on 5/28/16.
 */
public class CommandsClass implements CommandExecutor {

    private double number = 0;
    private long time = 0;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        final File playerfile = new File("plugins/ZeroPrisonSell/playerdata/" + player.getUniqueId() + ".yml");
        final FileConfiguration playerconfig = YamlConfiguration.loadConfiguration(playerfile);
        FileConfiguration languageconfig = YamlConfiguration.loadConfiguration(ZeroPrisonSell.language);
        if (cmd.getName().equalsIgnoreCase("sell")) {
            if (player.hasPermission("zeroprisonsell.player")) {
                if (args.length == 0) {
                    player.sendMessage(ZeroPrisonSell.format("usage").replace("%e", "/sell <shopname>"));
                } else if (args.length == 1) {
                    File sellfile = new File("plugins/ZeroPrisonSell/selldata/" + args[0] + ".yml");
                    FileConfiguration sellconfig = YamlConfiguration.loadConfiguration(sellfile);
                    if (!sellfile.exists()) {
                        player.sendMessage(ZeroPrisonSell.format("shop-doesnt-exist"));
                    } else {
                        if (player.hasPermission("zeroprisonsell.sell." + args[0])) {
                            ZeroPrisonSell.sellItems(args[0], player);
                        } else {
                            ZeroPrisonSell.format("no-permission");
                        }
                    }
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("listsellshops")) {
            if (args.length == 0) {
                StringBuilder sb = new StringBuilder();
                for (File file : new File("plugins/ZeroPrisonSell/selldata/").listFiles()) {
                    if (player.hasPermission("zeroprisonsell.sell." + file.getName().replace(".yml", ""))) {
                        sb.append(file.getName().replace(".yml", "") + ", ");
                    }
                }
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', languageconfig.getString("prefix")) + ChatColor.YELLOW + " Sell Shops List");
                String sellshoplist = sb.toString();
                Pattern pattern = Pattern.compile(", $");
                Matcher matcher = pattern.matcher(sellshoplist);
                sellshoplist = matcher.replaceAll("");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', languageconfig.getString("prefix")) + ChatColor.GOLD + " Sell Shops: " + ChatColor.WHITE + sellshoplist);
            } else {
                player.sendMessage(ZeroPrisonSell.format("usage").replace("%e", "/listsellshops"));
            }
        } else if (cmd.getName().equalsIgnoreCase("selladmin")) {
            if (player.hasPermission("zeroprisonsell.admin")) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.AQUA + "" + ChatColor.STRIKETHROUGH + "------------" + ChatColor.AQUA + ChatColor.BOLD + " (Sell Admin Commands) " + ChatColor.AQUA + ChatColor.STRIKETHROUGH + "------------");
                    player.sendMessage(ChatColor.DARK_AQUA + " > " + ChatColor.BOLD + "/selladmin reload" + ChatColor.AQUA + " | " + ChatColor.GRAY + languageconfig.getString("help-reload"));
                    player.sendMessage(ChatColor.DARK_AQUA + " > " + ChatColor.BOLD + "/selladmin createsell <name>" + ChatColor.AQUA + " | " + ChatColor.GRAY + languageconfig.getString("help-createsell"));
                    player.sendMessage(ChatColor.DARK_AQUA + " > " + ChatColor.BOLD + "/selladmin deletesell <name>" + ChatColor.AQUA + " | " + ChatColor.GRAY + languageconfig.getString("help-deletesell"));
                    player.sendMessage(ChatColor.DARK_AQUA + " > " + ChatColor.BOLD + "/selladmin add <name> <material> <price>" + ChatColor.AQUA + " | " + ChatColor.GRAY + languageconfig.getString("help-add"));
                    player.sendMessage(ChatColor.DARK_AQUA + " > " + ChatColor.BOLD + "/selladmin remove <name> <material>" + ChatColor.AQUA + " | " + ChatColor.GRAY + languageconfig.getString("help-remove"));
                    player.sendMessage(ChatColor.DARK_AQUA + " > " + ChatColor.BOLD + "/selladmin listitems <name>" + ChatColor.AQUA + " | " + ChatColor.GRAY + languageconfig.getString("help-listitems"));
                    player.sendMessage(ChatColor.DARK_AQUA + " > " + ChatColor.BOLD + "/selladmin setmultiplier <player> <multiplier> <time>" + ChatColor.AQUA + " | " + ChatColor.GRAY + languageconfig.getString("help-multiplier"));
                    player.sendMessage(ChatColor.AQUA + "" + ChatColor.STRIKETHROUGH + "------------" + ChatColor.AQUA + ChatColor.BOLD + " (Sell Admin Commands) " + ChatColor.AQUA + ChatColor.STRIKETHROUGH + "------------");
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        File selldata = new File("plugins/ZeroPrisonSell/selldata");
                        File playerdata = new File("plugins/ZeroPrisonSell/playerdata");
                        for (File selldatas : selldata.listFiles()) {
                            FileConfiguration sellconfigs = YamlConfiguration.loadConfiguration(selldatas);
                            try {
                                sellconfigs.load(selldatas);
                            } catch (IOException ex) {
                                player.sendMessage(ZeroPrisonSell.format("error-saving"));
                                ZeroPrisonSell.getPlugin().getLogger().log(Level.SEVERE, ZeroPrisonSell.format("error-saving"));
                            } catch (InvalidConfigurationException ex) {
                                player.sendMessage(ZeroPrisonSell.format("invalid-configuration"));
                                ZeroPrisonSell.getPlugin().getLogger().log(Level.SEVERE, ZeroPrisonSell.format("invalid-configuration"));
                            }
                        }
                        for (File playerdatas : playerdata.listFiles()) {
                            FileConfiguration playerconfigs = YamlConfiguration.loadConfiguration(playerdatas);
                            try {
                                playerconfigs.load(playerdatas);
                            } catch (IOException ex) {
                                player.sendMessage(ZeroPrisonSell.format("error-saving"));
                                ZeroPrisonSell.getPlugin().getLogger().log(Level.SEVERE, ZeroPrisonSell.format("error-saving"));
                            } catch (InvalidConfigurationException ex) {
                                player.sendMessage(ZeroPrisonSell.format("invalid-configuration"));
                                ZeroPrisonSell.getPlugin().getLogger().log(Level.SEVERE, ZeroPrisonSell.format("invalid-configuration"));
                            }
                        }
                        player.sendMessage(ZeroPrisonSell.format("reload-success"));
                    } else if (args[0].equalsIgnoreCase("createsell") || args[0].equalsIgnoreCase("deletesell") || args[0].equalsIgnoreCase("listitems")) {
                        player.sendMessage(ZeroPrisonSell.format("usage").replace("%e", "/selladmin " + args[0] + " <name>"));
                    } else if (args[0].equalsIgnoreCase("add")) {
                        player.sendMessage(ZeroPrisonSell.format("usage").replace("%e", " /selladmin " + args[0] + " <name> <material> <price>"));
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        player.sendMessage(ZeroPrisonSell.format("usage").replace("%e", " /selladmin " + args[0] + " <name> <material>"));
                    } else if (args[0].equalsIgnoreCase("setmultiplier")) {
                        player.sendMessage(ZeroPrisonSell.format("usage").replace("%e", " /selladmin " + args[0] + " <player> <multiplier> <time>"));
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("createsell")) {
                        File sellfile = new File("plugins/ZeroPrisonSell/selldata/" + args[1] + ".yml");
                        FileConfiguration sellconfig = YamlConfiguration.loadConfiguration(sellfile);
                        if (sellfile.exists()) {
                            player.sendMessage(ZeroPrisonSell.format("shop-exists"));
                        } else {
                            sellconfig.createSection("items");
                            try {
                                sellconfig.save(sellfile);
                            } catch (IOException ex) {
                                player.sendMessage(ZeroPrisonSell.format("error-saving"));
                                ZeroPrisonSell.getPlugin().getLogger().log(Level.SEVERE, ZeroPrisonSell.format("error-saving"));
                            }
                            player.sendMessage(ZeroPrisonSell.format("shop-created").replace("%e", args[1]));
                        }
                    } else if (args[0].equalsIgnoreCase("deletesell")) {
                        File sellfile = new File("plugins/ZeroPrisonSell/selldata/" + args[1] + ".yml");
                        if (!sellfile.exists()) {
                            player.sendMessage(ZeroPrisonSell.format("shop-doesnt-exist").replace("%e", args[1]));
                        } else {
                            sellfile.delete();
                            player.sendMessage(ZeroPrisonSell.format("shop-deleted"));
                        }
                    } else if (args[0].equalsIgnoreCase("listitems")) {
                        File sellfile = new File("plugins/ZeroPrisonSell/selldata/" + args[1] + ".yml");
                        FileConfiguration sellconfig = YamlConfiguration.loadConfiguration(sellfile);
                        player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---------------" + ChatColor.GRAY + ChatColor.BOLD + " (" + ChatColor.BLUE + ChatColor.BOLD + "J" + ChatColor.DARK_GREEN + ChatColor.BOLD + "C" + ChatColor.GOLD + ChatColor.BOLD + "Sell" + ChatColor.GRAY + ChatColor.BOLD + ") " + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "---------------");
                        for (String s : sellconfig.getConfigurationSection("items").getKeys(false)) {
                            DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,##0.00");
                            player.sendMessage(ChatColor.GRAY + " > " + ChatColor.AQUA + s + " - " + ChatColor.GREEN + "$" + df.format(sellconfig.getDouble("items." + s)));
                        }
                        player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---------------" + ChatColor.GRAY + ChatColor.BOLD + " (" + ChatColor.BLUE + ChatColor.BOLD + "J" + ChatColor.DARK_GREEN + ChatColor.BOLD + "C" + ChatColor.GOLD + ChatColor.BOLD + "Sell" + ChatColor.GRAY + ChatColor.BOLD + ") " + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "---------------");
                    } else if (args[0].equalsIgnoreCase("add")) {
                        player.sendMessage(ZeroPrisonSell.format("usage").replace("%e", " /selladmin " + args[0] + " <name> <material> <price>"));
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        player.sendMessage(ZeroPrisonSell.format("usage").replace("%e", " /selladmin " + args[0] + " <name> <material>"));
                    } else if (args[0].equalsIgnoreCase("setmultiplier")) {
                        player.sendMessage(ZeroPrisonSell.format("usage").replace("%e", " /selladmin " + args[0] + " <player> <multiplier> <time>"));
                    }
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("add")) {
                        player.sendMessage(ZeroPrisonSell.format("usage").replace("%e", " /selladmin " + args[0] + " <name> <material> <price>"));
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        File sellfile = new File("plugins/ZeroPrisonSell/selldata/" + args[1] + ".yml");
                        FileConfiguration sellconfig = YamlConfiguration.loadConfiguration(sellfile);
                        if (!sellconfig.contains("items." + args[2])) {
                            player.sendMessage(ZeroPrisonSell.format("item-doesnt-exist"));
                        } else {
                            sellconfig.set("items." + args[2], null);
                            try {
                                sellconfig.save(sellfile);
                            } catch (IOException ex) {
                                player.sendMessage(ZeroPrisonSell.format("error-saving"));
                                ZeroPrisonSell.getPlugin().getLogger().log(Level.SEVERE, ZeroPrisonSell.format("error-saving"));
                            }
                            player.sendMessage(ZeroPrisonSell.format("item-removed"));
                        }
                    } else if (args[0].equalsIgnoreCase("setmultiplier")) {
                        player.sendMessage(ZeroPrisonSell.format("usage").replace("%e", " /selladmin " + args[0] + " <player> <multiplier> <time>"));
                    }
                } else if (args.length == 4) {
                    if (args[0].equalsIgnoreCase("add")) {
                        File sellfile = new File("plugins/ZeroPrisonSell/selldata/" + args[1] + ".yml");
                        FileConfiguration sellconfig = YamlConfiguration.loadConfiguration(sellfile);
                        sellconfig.set("items." + args[2], Double.valueOf(args[3]));
                        try {
                            sellconfig.save(sellfile);
                        } catch (IOException ex) {
                            player.sendMessage(ZeroPrisonSell.format("error-saving"));
                            ZeroPrisonSell.getPlugin().getLogger().log(Level.SEVERE, ZeroPrisonSell.format("error-saving"));
                        }
                        player.sendMessage(ZeroPrisonSell.format("item-added"));
                    } else if (args[0].equalsIgnoreCase("setmultiplier")) {
                        final Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            player.sendMessage(ZeroPrisonSell.format("player-not-found"));
                        } else {
                            playerconfig.set("multiplier", Integer.valueOf(args[2]));
                            try {
                                playerconfig.save(playerfile);
                            } catch (IOException ex) {
                                player.sendMessage(ZeroPrisonSell.format("error-saving"));
                                ZeroPrisonSell.getPlugin().getLogger().log(Level.SEVERE, ZeroPrisonSell.format("error-saving"));
                            }
                            player.sendMessage(ZeroPrisonSell.format("added-multiplier"));
                            target.sendMessage(ZeroPrisonSell.format("multiplier-recieved"));
                            time = Integer.valueOf(args[3]) * 20;
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ZeroPrisonSell.getPlugin(), new Runnable() {
                                @Override
                                public void run() {
                                    playerconfig.set("multiplier", 1);
                                    try {
                                        playerconfig.save(playerfile);
                                    } catch (IOException ex) {
                                        player.sendMessage(ZeroPrisonSell.format("error-saving"));
                                        ZeroPrisonSell.getPlugin().getLogger().log(Level.SEVERE, ZeroPrisonSell.format("error-saving"));
                                    }
                                    target.sendMessage(ZeroPrisonSell.format("multiplier-worn-off"));
                                }
                            }, time);
                        }
                    }
                }
            } else {
                player.sendMessage(ZeroPrisonSell.format("no-permission"));
            }
        }
        return false;
    }

}
