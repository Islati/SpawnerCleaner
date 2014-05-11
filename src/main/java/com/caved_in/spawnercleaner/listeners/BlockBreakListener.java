package com.caved_in.spawnercleaner.listeners;

import com.caved_in.commons.player.Players;
import com.caved_in.spawnercleaner.SpawnerCleaner;
import com.caved_in.spawnercleaner.world.ChunkManager;
import com.caved_in.spawnercleaner.world.ChunkWrapper;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Block block = e.getBlock();
		Player player = e.getPlayer();
		Location location = block.getLocation();

		if (!ChunkManager.removeSpawnerLocation(location)) {
			return;
		}

		ChunkWrapper chunkWrapper = ChunkManager.getData(location);
		Players.sendMessage(player,String.format("&aThere's now &e%s&a spawners in your chunk. Rememeber, &e%s&a is the limit.",chunkWrapper.getSpawnerCount(), SpawnerCleaner.getSpawnerLimit()));
	}
}
