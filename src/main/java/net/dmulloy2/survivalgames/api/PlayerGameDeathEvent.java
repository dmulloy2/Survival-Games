package net.dmulloy2.survivalgames.api;

import net.dmulloy2.survivalgames.types.Game;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerGameDeathEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	private Player dead;
	private Player killer;
	private Game game;

	public PlayerGameDeathEvent(Player dead, Player killer, Game g)
	{
		this.dead = dead;
		this.killer = killer;
		game = g;
	}

	public Player getPlayer()
	{
		return dead;
	}

	public Player getKiller()
	{
		return killer;
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