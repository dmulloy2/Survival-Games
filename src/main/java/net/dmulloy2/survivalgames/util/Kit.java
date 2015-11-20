package net.dmulloy2.survivalgames.util;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.survivalgames.SurvivalGames;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Kit {
    private String name;
    private double cost;
    private ArrayList<ItemStack> items = new ArrayList<>();
    private ItemStack icon;

    private final SurvivalGames plugin;

    public Kit(SurvivalGames plugin, String name) {
        this.plugin = plugin;

        this.name = name;
        load();
    }

    public void load() {
        FileConfiguration c = plugin.getSettingsHandler().getKits();
        cost = c.getDouble("kits." + name + ".cost", 0);

        icon = ItemReader.read(c.getString("kits." + name + ".icon"));
        List<String> cont = c.getStringList("kits." + name + ".contents");
        for (String s : cont) {
            items.add(ItemReader.read(s));
        }

    }

    public ArrayList<ItemStack> getContents() {
        return items;
    }

    public boolean canUse(Player p) {
        return p.hasPermission("sg.kit." + name);
    }

    public String getName() {
        return name;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public double getCost() {
        return cost;
    }
}
