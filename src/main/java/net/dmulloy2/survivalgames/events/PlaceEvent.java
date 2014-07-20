package net.dmulloy2.survivalgames.events;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.util.MaterialUtil;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlaceEvent implements Listener
{
	public List<Material> allowedPlace = new ArrayList<Material>();

	private final SurvivalGames plugin;
	public PlaceEvent(SurvivalGames plugin)
	{
		this.plugin = plugin;

		for (String s : plugin.getSettingsManager().getConfig().getStringList("block.place.whitelist"))
		{
			allowedPlace.add(MaterialUtil.getMaterial(s));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Player p = event.getPlayer();
		int id = plugin.getGameManager().getPlayerGameId(p);

		if (id == -1)
		{
			int gameblockid = plugin.getGameManager().getBlockGameId(event.getBlock().getLocation());
			if (gameblockid != -1)
			{
				if (plugin.getGameManager().getGame(gameblockid).getGameMode() != Game.GameMode.DISABLED)
				{
					event.setCancelled(true);
				}
			}

			return;
		}

		Game g = plugin.getGameManager().getGame(id);
		if (g.isPlayerInactive(p))
			return;

		if (g.getMode() == Game.GameMode.DISABLED)
			return;

		if (g.getMode() != Game.GameMode.INGAME)
		{
			event.setCancelled(true);
			return;
		}

		if (! allowedPlace.contains(event.getBlock().getType()))
		{
			event.setCancelled(true);
		}
	}
}