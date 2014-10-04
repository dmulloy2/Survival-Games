package net.dmulloy2.survivalgames.api;

import lombok.Getter;
import net.dmulloy2.survivalgames.types.Game;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class PlayerWinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player winner;
    private Player victim;
    private Game game;

    public PlayerWinEvent(Player winner, Player victim, Game game) {
        this.winner = winner;
        this.victim = victim;
        this.game = game;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
