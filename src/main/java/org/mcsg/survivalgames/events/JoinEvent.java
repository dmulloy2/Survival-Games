package org.mcsg.survivalgames.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcsg.survivalgames.GameManager;
import org.mcsg.survivalgames.SettingsManager;
import org.mcsg.survivalgames.SurvivalGames;
import org.mcsg.survivalgames.util.UpdateChecker;

public class JoinEvent implements Listener {
	private SurvivalGames plugin;
	
	public JoinEvent(SurvivalGames plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void PlayerJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();

        if (GameManager.getInstance().getBlockGameId(p.getLocation()) != -1) {
        	new TeleportTask(p).runTaskLater(plugin, 5L);
        }
        
        if ((p.isOp() || p.hasPermission("sg.admin.reload")) 
        		&& SettingsManager.getInstance().getConfig().getBoolean("check-for-update", true)) {
        	new UpdateNotifyTask(p).runTaskLater(plugin, 60L);
        }
    }
	
	public class TeleportTask extends BukkitRunnable {
		private Player player;
		public TeleportTask(Player player) {
			this.player = player;
		}
		
		@Override
		public void run() {
			player.teleport(SettingsManager.getInstance().getLobbySpawn());
		}
	}
	
	public class UpdateNotifyTask extends BukkitRunnable {
		private Player player;
		public UpdateNotifyTask(Player player) {
			this.player = player;
		}
		
		@Override
		public void run() {
            new UpdateChecker().check(player, plugin);
		}
	}
}
