package net.dmulloy2.survivalgames.events;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakEvent implements Listener
{
	public List<Integer> allowedBreak = new ArrayList<Integer>();

	private final SurvivalGames plugin;
	public BreakEvent(SurvivalGames plugin)
	{
		this.plugin = plugin;

		allowedBreak.addAll(plugin.getSettingsManager().getConfig().getIntegerList("block.break.whitelist"));
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		Player p = event.getPlayer();
		int pid = plugin.getGameManager().getPlayerGameId(p);

		if (pid == -1)
		{
			int blockgameid = plugin.getGameManager().getBlockGameId(event.getBlock().getLocation());

			if (blockgameid != -1)
			{
				if (plugin.getGameManager().getGame(blockgameid).getGameMode() != Game.GameMode.DISABLED)
				{
					event.setCancelled(true);
				}
			}

			return;
		}

		Game g = plugin.getGameManager().getGame(pid);

		if (g.getMode() == Game.GameMode.DISABLED)
		{
			return;
		}

		if (g.getMode() != Game.GameMode.INGAME)
		{
			event.setCancelled(true);
			return;
		}

		if (!allowedBreak.contains(event.getBlock().getTypeId()))
		{
			event.setCancelled(true);
		}
	}
}