package com.caved_in.spawnercleaner.listeners;

import com.caved_in.spawnercleaner.world.ChunkManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkUnloadListener implements Listener {
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e) {
		if (e.isCancelled()) {
			return;
		}

		ChunkManager.removeData(e.getChunk());
	}
}
