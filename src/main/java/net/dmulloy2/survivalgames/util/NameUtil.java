package net.dmulloy2.survivalgames.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;

public class NameUtil {
    public static final List<String> auth = Arrays.asList(new String[] { "dmulloy2", "irene325", "minermac8521", "brett_setchfield" });

    public static String stylize(String name, boolean r) {
        if (auth.contains(name) && r) {
            name = ChatColor.DARK_RED + name;
        }
        if (auth.contains(name) && !r) {
            name = ChatColor.DARK_BLUE + name;
        }

        return name;
    }
}
