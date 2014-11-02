package net.dmulloy2.survivalgames.events;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.MaterialUtil;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockEvents implements Listener {
    private final List<Material> allowedBreak = new ArrayList<>();
    private final List<Material> allowedPlace = new ArrayList<>();
    private final SurvivalGames plugin;

    public BlockEvents(SurvivalGames plugin) {
        this.plugin = plugin;

        for (String s : plugin.getConfig().getStringList("block.break.whitelist")) {
            allowedBreak.add(MaterialUtil.getMaterial(s));
        }

        for (String s : plugin.getConfig().getStringList("block.place.whitelist")) {
            allowedPlace.add(MaterialUtil.getMaterial(s));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        Game game = plugin.getGameHandler().getGame(player);
        if (game == null) {
            Game blockGame = plugin.getGameHandler().getGame(block.getLocation());
            if (blockGame != null) {
                if (blockGame.getGameMode() != Game.GameMode.DISABLED) {
                    player.sendMessage(plugin.getPrefix() + FormatUtil.format("&cDisable arena {0} before building!", blockGame.getID()));
                    event.setCancelled(true);
                }
            }

            return;
        }

        if (game.getMode() == Game.GameMode.DISABLED) {
            return;
        }

        if (game.getMode() != Game.GameMode.INGAME) {
            event.setCancelled(true);
            return;
        }

        if (!allowedBreak.contains(block.getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        Game game = plugin.getGameHandler().getGame(player);
        if (game == null) {
            Game blockGame = plugin.getGameHandler().getGame(block.getLocation());
            if (blockGame != null) {
                if (blockGame.getGameMode() != Game.GameMode.DISABLED) {
                    player.sendMessage(plugin.getPrefix() + FormatUtil.format("&cDisable arena {0} before building!", blockGame.getID()));
                    event.setCancelled(true);
                }
            }

            return;
        }

        if (game.getMode() == Game.GameMode.DISABLED) {
            return;
        }

        if (game.getMode() != Game.GameMode.INGAME) {
            event.setCancelled(true);
            return;
        }

        if (!allowedPlace.contains(block.getType())) {
            event.setCancelled(true);
        }
    }
}
