package net.dmulloy2.survivalgames.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.dmulloy2.survivalgames.SurvivalGames;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class ChestRatioStorage {
    private final HashMap<Integer, List<ItemStack>> lvlstore = new HashMap<>();

    private int ratio = 2;
    private int maxlevel = 0;

    public ChestRatioStorage(SurvivalGames plugin) {
        FileConfiguration conf = plugin.getSettingsHandler().getChest();

        for (int clevel = 1; clevel <= 16; clevel++) {
            List<ItemStack> lvl = new ArrayList<>();
            List<String> list = conf.getStringList("chest.lvl" + clevel);

            if (!list.isEmpty()) {
                for (String aList : list) {
                    ItemStack i = ItemReader.read(aList);
                    lvl.add(i);
                }

                lvlstore.put(clevel, lvl);
            } else {
                maxlevel = clevel - 1;
                break;
            }
        }

        ratio = conf.getInt("chest.ratio", ratio);
    }

    public int getLevel(int base) {
        Random rand = new Random();
        int max = Math.min(base + 5, maxlevel);
        while (rand.nextInt(ratio) == 0 && base < max) {
            base++;
        }

        return base;
    }

    public List<ItemStack> getItems(int level) {
        Random r = new Random();
        List<ItemStack> items = new ArrayList<>();

        for (int a = 0; a < r.nextInt(7) + 10; a++) {
            if (r.nextBoolean()) {
                while (level < level + 5 && level < maxlevel && r.nextInt(ratio) == 1) {
                    level++;
                }

                List<ItemStack> lvl = lvlstore.get(level);
                ItemStack item = lvl.get(r.nextInt(lvl.size()));

                items.add(item);
            }
        }

        return items;
    }
}
