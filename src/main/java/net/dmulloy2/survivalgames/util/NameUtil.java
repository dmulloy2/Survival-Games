package net.dmulloy2.survivalgames.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;

public class NameUtil {
    private static final List<String> AUTHORS = Arrays.asList("dmulloy2");

    public static String stylize(String name, boolean inactive) {
        if (AUTHORS.contains(name) && inactive) {
            name = ChatColor.DARK_RED + name;
        }
        if (AUTHORS.contains(name) && !inactive) {
            name = ChatColor.DARK_BLUE + name;
        }

        return name;
    }

    public static List<String> getAuthors() {
        return AUTHORS;
    }
}
