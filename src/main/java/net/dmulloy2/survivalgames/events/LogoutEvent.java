package net.dmulloy2.survivalgames.events;

import lombok.AllArgsConstructor;
import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class LogoutEvent implements Listener
{
	private final SurvivalGames plugin;

	@EventHandler(priority = EventPriority.MONITOR)
	public void PlayerLoggout(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		if (player == null)
			return;

		try
		{
			plugin.getGameManager().removeFromOtherQueues(player, - 1);
		} catch (Throwable ex) { }

		int id = plugin.getGameManager().getPlayerGameId(player);
		if (plugin.getGameManager().isSpectator(player))
		{
			plugin.getGameManager().removeSpectator(player);
		}

		if (id == - 1)
			return;

		if (plugin.getGameManager().getGameMode(id) == Game.GameMode.INGAME)
		{
			plugin.getGameManager().getGame(id).killPlayer(player, true);
		}
		else
		{
			plugin.getGameManager().getGame(id).removePlayer(player, true);
		}
	}
}