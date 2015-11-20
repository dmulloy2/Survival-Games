package net.dmulloy2.survivalgames.handlers;

import java.util.HashMap;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Prefix;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author dmulloy2
 */

public class MessageHandler {
    private @Getter HashMap<Prefix, String> prefixes;

    private final SurvivalGames plugin;

    public MessageHandler(SurvivalGames plugin) {
        this.plugin = plugin;
        this.prefixes = new HashMap<>();

        FileConfiguration f = plugin.getSettingsHandler().getMessageConfig();
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
    public void sendFMessage(Prefix type, String input, CommandSender sender, String... args) {
        String msg = plugin.getSettingsHandler().getMessageConfig().getString("messages." + input);
        if (msg == null) {
            sender.sendMessage(ChatColor.RED + "Failed to load message for messages." + input);
            logMessage(Prefix.WARNING, "Failed to load message for messages." + input);
            return;
        }

        if (!plugin.getSettingsHandler().getMessageConfig().getBoolean("messages." + input + "_enabled", true)) {
            return;
        }

        if (args != null && args.length != 0) {
            msg = replaceVars(msg, args);
        }

        sender.sendMessage(prefixes.get(Prefix.MAIN) + " " + prefixes.get(type) + replaceColors(msg));
    }

    public void sendFMessage(String input, CommandSender sender, String... args) {
        sendFMessage(Prefix.INFO, input, sender, args);
    }

    /**
     * Sends a pre formated message from the plugin to a player, adding correct
     * prefix first
     */
    public void sendMessage(Prefix type, String msg, CommandSender sender) {
        sender.sendMessage(prefixes.get(Prefix.MAIN) + " " + prefixes.get(type) + replaceColors(msg));
    }

    public void sendMessage(String msg, CommandSender sender) {
        sendMessage(Prefix.INFO, msg, sender);
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
        String msg = plugin.getSettingsHandler().getMessageConfig().getString("messages." + input);
        if (msg == null) {
            logMessage(Prefix.WARNING, "Failed to load message for messages." + input);
            return;
        }

        if (!plugin.getSettingsHandler().getMessageConfig().getBoolean("messages." + input + "_enabled", true)) {
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

    public void broadcastMessage(Prefix type, String msg, Object... args) {
        plugin.getServer().broadcastMessage(prefixes.get(Prefix.MAIN) + " " + prefixes.get(type) + FormatUtil.format(msg, args));
    }

    public void broadcastMessage(String msg, Object... args) {
        broadcastMessage(Prefix.INFO, msg, args);
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
        HashMap<String, String> map = new HashMap<>();

        for (String str : vars) {
            String[] s = str.split("-");
            map.put(s[0], s[1]);
        }

        return replaceVars(msg, map);
    }
}
