package ml.ZeroDown.ZeroPrisonSell;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by Admin on 5/28/16.
 */
public class EventsClass implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        File playerfile = new File("plugins/ZeroPrisonSell/playerdata/" + player.getUniqueId() + ".yml");
        FileConfiguration playerconfig = YamlConfiguration.loadConfiguration(playerfile);
        if (!playerfile.exists()) {
            playerconfig.addDefault("multiplier", 1);
            playerconfig.options().copyDefaults(true);
            try {
                playerconfig.save(playerfile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        FileConfiguration languageconfig = YamlConfiguration.loadConfiguration(ZeroPrisonSell.language);
        if (event.getLine(0).equals("zerosell")) {
            if (player.hasPermission("zerocommandcooldown.createsign")) {
                if (event.getLine(1) == "") {
                    event.getBlock().breakNaturally();
                    player.sendMessage(ZeroPrisonSell.format("invalid-shop"));
                } else {
                    File sellfile = new File("plugins/ZeroPrisonSell/selldata/" + event.getLine(1) + ".yml");
                    if (!sellfile.exists()) {
                        event.getBlock().breakNaturally();
                        player.sendMessage(ZeroPrisonSell.format("invalid-shop"));
                    } else {
                        event.setLine(0, ChatColor.translateAlternateColorCodes('&', languageconfig.getString("sign-prefix")));
                        event.setLine(3, ChatColor.translateAlternateColorCodes('&', languageconfig.getString("right-click-me")));
                    }
                }
            }
        } else {
            event.getBlock().breakNaturally();
            player.sendMessage(ZeroPrisonSell.format("no-permission"));
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        FileConfiguration languageconfig = YamlConfiguration.loadConfiguration(ZeroPrisonSell.language);
        if (event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (player.hasPermission("zeroprisonsell.player")) {
                    Sign sign = (Sign) event.getClickedBlock().getState();
                    if (sign.getLine(0).equals(ChatColor.translateAlternateColorCodes('&', languageconfig.getString("sign-prefix"))) && sign.getLine(3).equals(ChatColor.translateAlternateColorCodes('&', languageconfig.getString("right-click-me")))) {
                        if (player.hasPermission("zeroprisonsell.sell." + sign.getLine(1))) {
                            ZeroPrisonSell.sellItems(sign.getLine(1), player);
                        }
                    }
                }
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Sign sign = (Sign) event.getClickedBlock().getState();
                File sellfile = new File("plugins/ZeroPrisonSell/selldata/" + sign.getLine(1) + ".yml");
                FileConfiguration sellconfig = YamlConfiguration.loadConfiguration(sellfile);
                if (sign.getLine(0).equals(ChatColor.AQUA + "[" + ChatColor.GREEN + "Sell" + ChatColor.AQUA + "]") && sign.getLine(3).equals("Right Click Me!")) {
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---------------" + ChatColor.translateAlternateColorCodes('&', languageconfig.getString("prefix")) + " " + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "---------------");
                    for (String s : sellconfig.getConfigurationSection("items").getKeys(false)) {
                        DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,##0.00");
                        player.sendMessage(ChatColor.GRAY + " > " + ChatColor.AQUA + s + " - " + ChatColor.GREEN + "$" + df.format(sellconfig.getDouble("items." + s)));
                    }
                    player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---------------" + ChatColor.translateAlternateColorCodes('&', languageconfig.getString("prefix")) + " " + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "---------------");
                }
            }
        }
    }


    }
