package net.dmulloy2.survivalgames.events;

import java.util.HashMap;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Game.GameMode;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class MoveEvent implements Listener
{
	private HashMap<Player, Vector> playerpos = new HashMap<Player, Vector>();

	private final SurvivalGames plugin;
	public MoveEvent(SurvivalGames plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void frozenSpawnHandler(PlayerMoveEvent e)
	{
		if (plugin.getGameManager().getPlayerGameId(e.getPlayer()) == -1)
		{
			playerpos.remove(e.getPlayer());
			return;
		}
		if (plugin.getGameManager().getGame(plugin.getGameManager().getPlayerGameId(e.getPlayer())).getMode() == Game.GameMode.INGAME)
		{
			return;
		}

		GameMode mo3 = plugin.getGameManager().getGameMode(plugin.getGameManager().getPlayerGameId(e.getPlayer()));
		if (plugin.getGameManager().isPlayerActive(e.getPlayer()) && mo3 != Game.GameMode.INGAME)
		{
			if (playerpos.get(e.getPlayer()) == null)
			{
				playerpos.put(e.getPlayer(), e.getPlayer().getLocation().toVector());
				return;
			}
			Location l = e.getPlayer().getLocation();
			Vector v = playerpos.get(e.getPlayer());
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