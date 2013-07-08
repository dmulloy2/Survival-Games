package org.mcsg.survivalgames.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.mcsg.survivalgames.Game;
import org.mcsg.survivalgames.GameManager;
import org.mcsg.survivalgames.util.SpectatorUtil;

public class SpectatorEvents implements Listener {
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (GameManager.getInstance().isSpectator(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        if (GameManager.getInstance().isSpectator(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (GameManager.getInstance().isSpectator(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerClickEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (SpectatorUtil.isSpectating(player)) {
        	if (player.getItemInHand().getType() == Material.COMPASS) {
        		Game game = SpectatorUtil.getGame(player);
        		if (game != null) {
        			event.setCancelled(true);
        			SpectatorUtil.openInventory(player, game);
        		} else {
        			SpectatorUtil.removeSpectator(player);
        		}
        	}
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
    	if (event.getWhoClicked() instanceof Player) {
    		Player player = (Player)event.getWhoClicked();
    		if (SpectatorUtil.isBrowsingInventory(player)) {
    			ItemStack stack = event.getCurrentItem();
    			if (stack.getType() == Material.SKULL_ITEM) {
    				SkullMeta meta = (SkullMeta)stack.getItemMeta();
    				if (meta.hasOwner()) {
    					Player p = Bukkit.getPlayerExact(meta.getOwner());
    					if (p != null) {
    						if (GameManager.getInstance().isPlayerActive(p)) {
    							event.setCancelled(true);
    							player.teleport(p);
    						}
    					}
    				}
    			}
    		}
    	}
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
    	if (event.getPlayer() instanceof Player) {
    		Player player = (Player)event.getPlayer();
    		if (SpectatorUtil.isBrowsingInventory(player)) {
    			SpectatorUtil.closeInventory(player);
    		}
    	}
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (GameManager.getInstance().isSpectator(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
    	if (event.getDamager() instanceof Player) {
    		Player player = (Player)event.getDamager();
    		if (GameManager.getInstance().isSpectator(player)) {
    			event.setCancelled(true);
    		}
    	}
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
    	if (event.getEntity() instanceof Player) {
    		Player player = (Player)event.getEntity();
    		if (GameManager.getInstance().isSpectator(player)) {
    			event.setCancelled(true);
    		}
    	}
    }
}