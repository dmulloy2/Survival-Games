package net.dmulloy2.survivalgames.events;

import java.util.HashSet;
import java.util.Random;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.managers.GameManager;
import net.dmulloy2.survivalgames.types.Game.GameMode;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestReplaceEvent implements Listener
{
	private final SurvivalGames plugin;
	public ChestReplaceEvent(SurvivalGames plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void ChestListener(PlayerInteractEvent e)
	{
		try
		{
			HashSet<Block> openedChest3 = new HashSet<Block>();

			if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
			{
				return;
			}

			Block clickedBlock = e.getClickedBlock();
			int gameid = plugin.getGameManager().getPlayerGameId(e.getPlayer());
			if (gameid == -1)
			{
				return;
			}
			GameManager gm = plugin.getGameManager();

			if (!gm.isPlayerActive(e.getPlayer()))
			{
				return;
			}

			if (gm.getGame(gameid).getMode() != GameMode.INGAME)
			{
				e.setCancelled(true);
				return;
			}

			if (plugin.getGameManager().getOpenedChest().get(gameid) != null)
			{
				openedChest3.addAll(plugin.getGameManager().getOpenedChest().get(gameid));
			}

			if (openedChest3.contains(clickedBlock))
			{
				return;
			}

			Inventory inv;
			int size = 0;

			BlockState state = clickedBlock.getState();
			if (state instanceof Chest)
			{
				size = 1;
				inv = ((Chest) state).getInventory();
			}
			else if (state instanceof DoubleChest)
			{
				size = 2;
				inv = ((DoubleChest) state).getInventory();
			}
			else
			{
				return;
			}

			inv.clear();

			Random r = new Random();

			for (ItemStack i : plugin.getChestRatioStorage().getItems())
			{
				int l = r.nextInt(26 * size);

				while (inv.getItem(l) != null)
				{
					l = r.nextInt(26 * size);
				}

				inv.setItem(l, i);
			}

			openedChest3.add(clickedBlock);
			plugin.getGameManager().getOpenedChest().put(gameid, openedChest3);
		}
		catch (Exception ex)
		{
			//
		}
	}
}