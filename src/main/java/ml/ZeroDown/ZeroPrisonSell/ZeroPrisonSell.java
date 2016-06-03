/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.ZeroDown.ZeroPrisonSell;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.update.spigot.SpigotUpdater;

/**
 *
 * @author justinthekoolkid
 */
public class ZeroPrisonSell extends JavaPlugin {

    private static Plugin plugin;
    public static File language;
    private static double price = 0;
    private static FileConfiguration config;
    private static int itemint = 0;
    private static Economy eco;

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getPluginManager().registerEvents(new EventsClass(), plugin);
        File selldata = new File("plugins/ZeroPrisonSell/selldata");
        File playerdata = new File("plugins/ZeroPrisonSell/playerdata");
        if (!selldata.exists()) {
            selldata.mkdir();
        }
        if (!playerdata.exists()) {
            playerdata.mkdir();
        }
        setupEconomy();
        plugin.saveDefaultConfig();
        plugin.saveResource("lang-en.yml", true);
        config = plugin.getConfig();
        language = new File("plugins/ZeroPrisonSell/lang-" + config.getString("Language") + ".yml");
        if (!language.exists()) {
            plugin.getLogger().log(Level.WARNING, "Language (lang-" + config.getString("Language") + ") doesn't exist! Switching to English!");
            language = new File("plugins/ZeroPrisonSell/lang-en.yml");
            plugin.getLogger().log(Level.WARNING, "Language (lang-en)");
        } else {
            plugin.getLogger().log(Level.WARNING, "Language (lang-" + config.getString("Language") + ")");
        }
        getCommand("sell").setExecutor(new CommandsClass());
        getCommand("selladmin").setExecutor(new CommandsClass());
        getCommand("listsellshops").setExecutor(new CommandsClass());
        if (config.getBoolean("Update Messages") == true) {
            try {
                new SpigotUpdater(plugin, 23840);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return eco != null;
    }


    public static void sellItems(String b, Player player) {
        FileConfiguration languageconfig = YamlConfiguration.loadConfiguration(language);
        File playerfile = new File("plugins/ZeroPrisonSell/playerdata/" + player.getUniqueId() + ".yml");
        FileConfiguration playerconfig = YamlConfiguration.loadConfiguration(playerfile);
        File sellfile = new File("plugins/ZeroPrisonSell/selldata/" + b + ".yml");
        FileConfiguration sellconfig = YamlConfiguration.loadConfiguration(sellfile);
        for (String s : sellconfig.getConfigurationSection("items").getKeys(false)) {
            if (player.getInventory().contains(Material.matchMaterial(s))) {
                for (ItemStack items : player.getInventory().all(Material.matchMaterial(s)).values()) {
                    player.getInventory().remove(items);
                    price = price + sellconfig.getDouble("items." + s) * items.getAmount() * playerconfig.getInt("multiplier");
                    itemint = itemint + items.getAmount();
                }
            }
        }
        DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,##0.00");
        String finalprice = df.format(price);
        eco.depositPlayer(player, price);
        price = 0;
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---------------" + ChatColor.RESET + " " + ChatColor.translateAlternateColorCodes('&', languageconfig.getString("prefix")) + " " + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "---------------");
        player.sendMessage(ChatColor.GRAY + " > " + ChatColor.translateAlternateColorCodes('&', languageconfig.getString("sold-success").replace("%e", String.valueOf(itemint))));
        itemint = 0;
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + " > " + ChatColor.translateAlternateColorCodes('&', languageconfig.getString("price-multiplied").replace("%e", String.valueOf(playerconfig.getInt("multiplier"))) + "x"));
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + " > " + ChatColor.translateAlternateColorCodes('&', languageconfig.getString("price-recieved").replace("%e", finalprice)));
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---------------" + ChatColor.RESET + " " + ChatColor.translateAlternateColorCodes('&', languageconfig.getString("prefix")) + " " + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "---------------");
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static String format(String s) {
        FileConfiguration langconfig = YamlConfiguration.loadConfiguration(language);
        return ChatColor.translateAlternateColorCodes('&', langconfig.get("prefix") + " " + langconfig.get(s));
    }
}
