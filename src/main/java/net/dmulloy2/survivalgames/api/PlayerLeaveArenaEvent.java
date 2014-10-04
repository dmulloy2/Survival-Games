package net.dmulloy2.survivalgames.api;

import lombok.Getter;
import net.dmulloy2.survivalgames.types.Game;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class PlayerLeaveArenaEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Game game;

    public PlayerLeaveArenaEvent(Player player, Game game) {
        this.player = player;
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
