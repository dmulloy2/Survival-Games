package net.dmulloy2.survivalgames.handlers;

import java.util.HashMap;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Prefix;
import net.dmulloy2.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class MessageHandler {
    private @Getter HashMap<Prefix, String> prefixes;

    private final SurvivalGames plugin;

    public MessageHandler(SurvivalGames plugin) {
        this.plugin = plugin;
        this.prefixes = new HashMap<Prefix, String>();

        FileConfiguration f = plugin.getSettingsManager().getMessageConfig();
        prefixes.put(Prefix.MAIN, replaceColors(f.getString("prefix.main", plugin.getPrefix())));
        prefixes.put(Prefix.INFO, replaceColors(f.getString("prefix.states.info", "")));
        prefixes.put(Prefix.WARNING, replaceColors(f.getString("prefix.states.warning", "&c[Warning] ")));
        prefixes.put(Prefix.ERROR, replaceColors(f.getString("prefix.states.error", "&4[Error] ")));
    }

    /**
     * Loads a Message from messages.yml, converts its colors and replaces vars
     * in the form of {$var} with its correct values, then sends to the player,
     * adding the correct prefix
     */
    public void sendFMessage(Prefix type, String input, Player player, String... args) {
        String msg = plugin.getSettingsManager().getMessageConfig().getString("messages." + input);
        if (msg == null) {
            player.sendMessage(ChatColor.RED + "Failed to load message for messages." + input);
            logMessage(Prefix.WARNING, "Failed to load message for messages." + input);
            return;
        }

        if (!plugin.getSettingsManager().getMessageConfig().getBoolean("messages." + input + "_enabled", true)) {
            return;
        }

        if (args != null && args.length != 0) {
            msg = replaceVars(msg, args);
        }

        player.sendMessage(prefixes.get(Prefix.MAIN) + " " + prefixes.get(type) + replaceColors(msg));
    }

    public void sendFMessage(String input, Player player, String... args) {
        sendFMessage(Prefix.INFO, input, player, args);
    }

    /**
     * Sends a pre formated message from the plugin to a player, adding correct
     * prefix first
     */
    public void sendMessage(Prefix type, String msg, Player player) {
        player.sendMessage(prefixes.get(Prefix.MAIN) + " " + prefixes.get(type) + replaceColors(msg));
    }

    public void sendMessage(String msg, Player player) {
        sendMessage(Prefix.INFO, msg, player);
    }

    public void logMessage(Prefix type, String msg, Object... args) {
        switch (type) {
            case INFO:
                plugin.getLogHandler().log(prefixes.get(type) + msg, args);
                break;
            case WARNING:
                plugin.getLogHandler().log(Level.WARNING, prefixes.get(type) + msg, args);
                break;
            case ERROR:
                plugin.getLogHandler().log(Level.SEVERE, prefixes.get(type) + msg, args);
                break;
            default:
                break;
        }
    }

    public void logMessage(String msg, Object... args) {
        logMessage(Prefix.INFO, msg, args);
    }

    public void broadcastFMessage(Prefix type, String input, String... args) {
        String msg = plugin.getSettingsManager().getMessageConfig().getString("messages." + input);
        if (msg == null) {
            logMessage(Prefix.WARNING, "Failed to load message for messages." + input);
            return;
        }

        if (!plugin.getSettingsManager().getMessageConfig().getBoolean("messages." + input + "_enabled", true)) {
            return;
        }

        if (args != null && args.length != 0) {
            msg = replaceVars(msg, args);
        }

        plugin.getServer().broadcastMessage(prefixes.get(Prefix.MAIN) + prefixes.get(type) + " " + replaceColors(msg));
    }

    public void broadcastFMessage(String input, String... args) {
        broadcastFMessage(Prefix.INFO, input, args);
    }

    public void broadcastMessage(Prefix type, String msg) {
        plugin.getServer().broadcastMessage(prefixes.get(Prefix.MAIN) + " " + prefixes.get(type) + replaceColors(msg));
    }

    public void broadcastMessage(String msg) {
        broadcastMessage(Prefix.INFO, msg);
    }

    public String replaceColors(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public String replaceVars(String msg, HashMap<String, String> vars) {
        for (String s : vars.keySet()) {
            try {
                msg = msg.replace("{$" + s + "}", vars.get(s));
            } catch (Exception e) {
                plugin.log(Level.SEVERE, Util.getUsefulStack(e, "replacing variable " + s + " in message: " + msg));
                break;
            }
        }

        return msg;
    }

    public String replaceVars(String msg, String... vars) {
        HashMap<String, String> map = new HashMap<String, String>();

        for (String str : vars) {
            String[] s = str.split("-");
            map.put(s[0], s[1]);
        }

        return replaceVars(msg, map);
    }
}
