package net.dmulloy2.survivalgames.events;

import java.util.HashMap;
import java.util.Map;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Game.GameMode;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class MoveEvent implements Listener
{
	private final Map<String, Vector> positions;

	private final SurvivalGames plugin;
	public MoveEvent(SurvivalGames plugin)
	{
		this.plugin = plugin;
		this.positions = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void frozenSpawnHandler(PlayerMoveEvent e)
	{
		if (plugin.getGameManager().getPlayerGameId(e.getPlayer()) == -1)
		{
			positions.remove(e.getPlayer());
			return;
		}
		if (plugin.getGameManager().getGame(plugin.getGameManager().getPlayerGameId(e.getPlayer())).getMode() == Game.GameMode.INGAME)
		{
			return;
		}

		GameMode mo3 = plugin.getGameManager().getGameMode(plugin.getGameManager().getPlayerGameId(e.getPlayer()));
		if (plugin.getGameManager().isPlayerActive(e.getPlayer()) && mo3 != Game.GameMode.INGAME)
		{
			if (positions.get(e.getPlayer().getName()) == null)
			{
				positions.put(e.getPlayer().getName(), e.getPlayer().getLocation().toVector());
				return;
			}
			Location l = e.getPlayer().getLocation();
			Vector v = positions.get(e.getPlayer().getName());
			if (l.getBlockX() != v.getBlockX() || l.getBlockZ() != v.getBlockZ())
			{
				l.setX(v.getBlockX() + .5);
				l.setZ(v.getBlockZ() + .5);
				l.setYaw(e.getPlayer().getLocation().getYaw());
				l.setPitch(e.getPlayer().getLocation().getPitch());
				e.getPlayer().teleport(l);
			}
		}
	}
}