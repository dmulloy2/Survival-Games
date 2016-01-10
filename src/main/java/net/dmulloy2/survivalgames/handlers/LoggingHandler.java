package net.dmulloy2.survivalgames.handlers;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.BlockData;
import net.dmulloy2.survivalgames.types.Game;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LoggingHandler implements Listener {
    private @Getter Map<String, Integer> i = new HashMap<>();

    private final SurvivalGames plugin;

    public LoggingHandler(SurvivalGames plugin) {
        this.plugin = plugin;

        i.put("BCHANGE", 1);
        i.put("BPLACE", 1);
        i.put("BFADE", 1);
        i.put("BBLOW", 1);
        i.put("BSTARTFIRE", 1);
        i.put("BBURN", 1);
        i.put("BREDSTONE", 1);
        i.put("LDECAY", 1);
        i.put("BSPREAD", 1);
        i.put("BPISTION", 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockChanged(BlockBreakEvent e) {
        if (e.isCancelled())
            return;

        logBlockDestroyed(e.getBlock());
        i.put("BCHANGE", i.get("BCHANGE") + 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockChanged(BlockPlaceEvent e) {
        if (e.isCancelled())
            return;

        logBlockCreated(e.getBlock());
        i.put("BPLACE", i.get("BPLACE") + 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockChanged(BlockFadeEvent e) {
        if (e.isCancelled())
            return;

        logBlockDestroyed(e.getBlock());
        i.put("BFADE", i.get("BFADE") + 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockChange(EntityExplodeEvent e) {
        if (e.isCancelled())
            return;

        for (Block b : e.blockList()) {
            logBlockDestroyed(b);
        }

        i.put("BBLOW", i.get("BBLOW") + 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockChange(BlockIgniteEvent e) {
        if (e.isCancelled())
            return;

        logBlockCreated(e.getBlock());
        i.put("BSTARTFIRE", i.get("BSTARTFIRE") + 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockChanged(BlockBurnEvent e) {
        if (e.isCancelled())
            return;

        logBlockDestroyed(e.getBlock());
        i.put("BBURN", i.get("BBURN") + 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockChanged(BlockGrowEvent e) {
        if (e.isCancelled())
            return;

        logBlockCreated(e.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockChanged(BlockFormEvent e) {
        if (e.isCancelled())
            return;

        logBlockCreated(e.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockChanged(LeavesDecayEvent e) {
        if (e.isCancelled())
            return;

        logBlockDestroyed(e.getBlock());
        i.put("LDECAY", i.get("LDECAY") + 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockChanged(BlockSpreadEvent e) {
        if (e.isCancelled())
            return;

        logBlockCreated(e.getBlock());
        i.put("BSPREAD", i.get("BSPREAD") + 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockChanged(PlayerInteractEvent e) {
        if (e.isCancelled() || e.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (e.getClickedBlock().getType() != Material.FIRE) {
            return;
        }

        logBlockDestroyed(e.getClickedBlock());
        i.put("BCHANGE", i.get("BCHANGE") + 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockChange(BlockPistonExtendEvent e) {
        if (e.isCancelled())
            return;

        for (Block b : e.getBlocks()) {
            logBlockCreated(b);
        }

        i.put("BPISTION", i.get("BPISTION") + 1);
    }

    public void logBlockCreated(Block block) {
        Game game = plugin.getGameHandler().getGame(block.getLocation());
        if (game == null) {
            return;
        }

        Game.GameMode mode = game.getGameMode();
        if (mode == Game.GameMode.DISABLED) {
            return;
        }

        plugin.getQueueHandler().add(new BlockData(game.getID(), block.getWorld().getName(), null, null, block.getType(),
                block.getState().getData(), block.getX(), block.getY(), block.getZ(), null));
    }

    public void logBlockDestroyed(Block block) {
        if (block.getType() == Material.FIRE) {
            return;
        }

        Game game = plugin.getGameHandler().getGame(block.getLocation());
        if (game == null) {
            return;
        }

        Game.GameMode mode = game.getGameMode();
        if (mode == Game.GameMode.DISABLED) {
            return;
        }

        plugin.getQueueHandler().add(new BlockData(game.getID(), block.getWorld().getName(), null, null, block.getType(),
                block.getState().getData(), block.getX(), block.getY(), block.getZ(), null));
    }
}
