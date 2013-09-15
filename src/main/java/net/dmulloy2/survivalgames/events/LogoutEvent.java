package net.dmulloy2.survivalgames.events;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LogoutEvent implements Listener
{
	private final SurvivalGames plugin;
	public LogoutEvent(SurvivalGames plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void PlayerLoggout(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		plugin.getGameManager().removeFromOtherQueues(p, -1);
		int id = plugin.getGameManager().getPlayerGameId(p);
		if (plugin.getGameManager().isSpectator(p))
		{
			plugin.getGameManager().removeSpectator(p);
		}

		if (id == -1)
		{
			return;
		}

		if (plugin.getGameManager().getGameMode(id) == Game.GameMode.INGAME)
		{
			plugin.getGameManager().getGame(id).killPlayer(p, true);
		}
		else
		{
			plugin.getGameManager().getGame(id).removePlayer(p, true);
		}
	}
}