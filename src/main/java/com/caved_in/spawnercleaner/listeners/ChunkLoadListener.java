package com.caved_in.spawnercleaner.listeners;

import com.caved_in.spawnercleaner.world.ChunkManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkLoadListener implements Listener {

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
		ChunkManager.initChunk(e.getChunk());
	}
}
