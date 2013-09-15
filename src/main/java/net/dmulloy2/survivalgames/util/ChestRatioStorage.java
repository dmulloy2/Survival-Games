package net.dmulloy2.survivalgames.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import lombok.Getter;
import net.dmulloy2.survivalgames.SurvivalGames;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class ChestRatioStorage
{
	private HashMap<Integer, List<ItemStack>> lvlstore = new HashMap<Integer, List<ItemStack>>();

	private @Getter
	int ratio = 2;

	private final SurvivalGames plugin;

	public ChestRatioStorage(SurvivalGames plugin)
	{
		this.plugin = plugin;
	}

	public void setup()
	{
		FileConfiguration conf = plugin.getSettingsManager().getChest();

		for (int a = 1; a < 5; a++)
		{
			List<ItemStack> lvl = new ArrayList<ItemStack>();
			List<String> list = conf.getStringList("chest.lvl" + a);

			for (int b = 0; b < list.size(); b++)
			{
				ItemStack i = ItemReader.read(list.get(b));
				lvl.add(i);
			}

			lvlstore.put(a, lvl);

		}

		ratio = conf.getInt("chest.ratio") + 1;
	}

	public List<ItemStack> getItems()
	{
		Random r = new Random();
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (int a = 0; a < r.nextInt(7) + 5; a++)
		{
			if (r.nextBoolean() == true)
			{
				int i = 1;
				while (i < 6 && r.nextInt(ratio) == 1)
				{
					i++;
				}

				List<ItemStack> lvl = lvlstore.get(i);
				ItemStack item = lvl.get(r.nextInt(lvl.size()));

				items.add(item);
			}
		}

		return items;
	}
}