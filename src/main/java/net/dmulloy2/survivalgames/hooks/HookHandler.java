package net.dmulloy2.survivalgames.hooks;

import java.util.HashMap;

import net.dmulloy2.survivalgames.SurvivalGames;

import org.bukkit.configuration.file.FileConfiguration;

public class HookHandler {
    private HashMap<String, HookBase> hooks;

    private final SurvivalGames plugin;

    public HookHandler(SurvivalGames plugin) {
        this.plugin = plugin;
        this.hooks = new HashMap<String, HookBase>();
        this.hooks.put("c", new CommandHook(plugin));
    }

    public void runHook(String hook, String... args) {
        FileConfiguration c = plugin.getSettingsHandler().getConfig();

        for (String str : c.getStringList("hooks." + hook)) {
            String[] split = str.split("!");
            String p = plugin.getMessageHandler().replaceVars(split[0], args);
            String[] commands = plugin.getMessageHandler().replaceVars(split[1], args).split(";");
            if (checkConditions(split[2], args)) {
                if (p.equalsIgnoreCase("console") || (split.length == 4 && plugin.getServer().getPlayer(p).hasPermission(split[3])) || (split.length == 3)) {
                    for (String s1 : commands) {
                        String[] s2 = s1.split("#");
                        hooks.get(s2[0]).executeHook(p, s2);
                    }
                }
            }
        }
    }

    public boolean checkConditions(String str, String... args) {
        String[] C = { "<", ">", "=", ">=", "<=" };
        str = str.trim();
        if (str.equalsIgnoreCase("true")) {
            return true;
        }

        for (String split : plugin.getMessageHandler().replaceVars(str, args).split(";")) {
            boolean flag = false;
            for (String c : C) {
                int i = split.indexOf(c);
                if (i != -1) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                plugin.log("HookHandler Condition does not contian a compare operator: " + split);
                return false;
            }
            try {
                // attempt to compare the "values", probably a better way to do
                // this
                if (split.contains(">")) {
                    if (Double.parseDouble(split.substring(0, split.indexOf(">")).trim()) > Double.parseDouble(split.substring(split.indexOf(">")).trim())) {
                        //
                    } else {
                        return false;
                    }
                } else if (split.contains("<")) {
                    if (Double.parseDouble(split.substring(0, split.indexOf("<")).trim()) < Double.parseDouble(split.substring(split.indexOf("<")).trim())) {
                        //
                    } else {
                        return false;
                    }
                } else if (split.contains("=")) {
                    if (Double.parseDouble(split.substring(0, split.indexOf("=")).trim()) == Double.parseDouble(split.substring(split.indexOf("=")).trim())) {
                        //
                    } else {
                        return false;
                    }
                } else if (split.contains(">=")) {
                    if (Double.parseDouble(split.substring(0, split.indexOf(">=")).trim()) >= Double.parseDouble(split.substring(split.indexOf(">=")).trim())) {
                        //
                    } else {
                        return false;
                    }
                } else if (split.contains("<=")) {
                    if (Double.parseDouble(split.substring(0, split.indexOf("<=")).trim()) <= Double.parseDouble(split.substring(split.indexOf("<=")).trim())) {
                    } else {
                        return false;
                    }
                }
            } catch (Exception e) {
                plugin.log("HookHandler: Error parsing value for: " + split);
                return false;
            }
        }

        return true;
    }
}
