package org.mcsg.survivalgames.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.mcsg.survivalgames.GameManager;
import org.mcsg.survivalgames.SettingsManager;
import org.mcsg.survivalgames.SurvivalGames;
import org.mcsg.survivalgames.util.UpdateChecker;



public class JoinEvent implements Listener {
    
    Plugin plugin;
    
    public JoinEvent(Plugin plugin){
        this.plugin = plugin;
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void PlayerJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();

        if(GameManager.getInstance().getBlockGameId(p.getLocation()) != -1){
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                public void run(){
                    p.teleport(SettingsManager.getInstance().getLobbySpawn());

                }
            }, 5L);
        }
        if ((p.isOp() || p.hasPermission("sg.admin.reload")) && SettingsManager.getInstance().getConfig().getBoolean("check-for-update", true)){
            Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {

                public void run() {
                    SurvivalGames.$("Checking for updates");
                    new UpdateChecker().check(p, plugin);
                }
             }, 60L);
        }
    }
    
}
