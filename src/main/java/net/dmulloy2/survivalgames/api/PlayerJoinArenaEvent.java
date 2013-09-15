package net.dmulloy2.survivalgames.api;

import net.dmulloy2.survivalgames.types.Game;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJoinArenaEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private Game game;

	public PlayerJoinArenaEvent(Player p, Game g)
	{
		player = p;
		game = g;
	}

	public Player getPlayer()
	{
		return player;
	}

	public Game getGame()
	{
		return game;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}