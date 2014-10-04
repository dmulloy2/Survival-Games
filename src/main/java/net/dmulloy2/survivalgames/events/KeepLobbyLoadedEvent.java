package net.dmulloy2.survivalgames.events;

import net.dmulloy2.survivalgames.SurvivalGames;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class KeepLobbyLoadedEvent implements Listener {
    private final SurvivalGames plugin;

    public KeepLobbyLoadedEvent(SurvivalGames plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        if (plugin.getLobbyManager().getLobbychunks().contains(e.getChunk())) {
            plugin.debug("Kept lobby chunk from unloading!");
            e.setCancelled(true);
        }
    }
}
