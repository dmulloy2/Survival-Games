package org.mcsg.survivalgames.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.mcsg.survivalgames.LobbyManager;
import org.mcsg.survivalgames.SurvivalGames;

public class KeepLobbyLoadedEvent implements Listener {
    
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
		if (LobbyManager.lobbychunks.contains(e.getChunk())) {
			SurvivalGames.debug("Kept lobby chunk from unloading!");
            e.setCancelled(true);
        }
    }
}