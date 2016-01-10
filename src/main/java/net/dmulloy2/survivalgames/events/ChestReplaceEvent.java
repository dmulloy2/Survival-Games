package net.dmulloy2.survivalgames.events;

import java.util.HashSet;

import net.dmulloy2.survivalgames.SurvivalGames;
import net.dmulloy2.survivalgames.types.Game;
import net.dmulloy2.survivalgames.types.Game.GameMode;
import net.dmulloy2.util.Util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

public class ChestReplaceEvent implements Listener {
    private final SurvivalGames plugin;

    public ChestReplaceEvent(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void ChestListener(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            BlockState clicked = e.getClickedBlock().getState();
            if (clicked instanceof Chest || clicked instanceof DoubleChest) {
                Game game = plugin.getGameHandler().getGame(e.getPlayer());
                if (game != null) {
                    if (game.getMode() == GameMode.INGAME) {
                        HashSet<Block> openedChest = plugin.getGameHandler().getOpenedChest().get(game.getID());
                        openedChest = (openedChest == null) ? new HashSet<Block>() : openedChest;
                        if (!openedChest.contains(e.getClickedBlock())) {
                            Inventory[] invs = ((clicked instanceof Chest)) ? new Inventory[] { ((Chest) clicked).getBlockInventory() }
                                : new Inventory[] { ((DoubleChest) clicked).getLeftSide().getInventory(),
                                    ((DoubleChest) clicked).getRightSide().getInventory() };
                            ItemStack item = invs[0].getItem(0);

                            int data = 1;
                            if (item != null && item.getType() == Material.WOOL) {
                                Wool wool = (Wool) item.getData();
                                data = wool.getColor().ordinal() + 1;
                            }

                            int level = plugin.getChestRatioStorage().getLevel(data);

                            for (Inventory inv : invs) {
                                inv.setContents(new ItemStack[inv.getContents().length]);
                                for (ItemStack i : plugin.getChestRatioStorage().getItems(level)) {
                                    int l = Util.random(26);
                                    while (inv.getItem(l) != null)
                                        l = Util.random(26);
                                    inv.setItem(l, i);
                                }
                            }
                        }

                        openedChest.add(e.getClickedBlock());
                        plugin.getGameHandler().getOpenedChest().put(game.getID(), openedChest);
                    } else {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
