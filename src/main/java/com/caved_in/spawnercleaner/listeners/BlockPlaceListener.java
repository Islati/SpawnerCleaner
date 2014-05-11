package com.caved_in.spawnercleaner.listeners;

import com.caved_in.commons.player.Players;
import com.caved_in.spawnercleaner.world.ChunkManager;
import com.caved_in.spawnercleaner.world.ChunkWrapper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Player player = e.getPlayer();
		Block block = e.getBlock();
		//If the block placed isn't a mob spawner, then quit listening
		if (Material.MOB_SPAWNER != block.getType()) {
			return;
		}

		Location spawnerLoc = block.getLocation();

		if (ChunkManager.addSpawnerLocation(spawnerLoc)) {
			return;
		}

		ChunkWrapper wrapper = ChunkManager.getData(spawnerLoc);
		Players.sendMessage(player, String.format("&eThis chunk has reached it's limit for mob spawners &7(%s).",wrapper.getSpawnerCount()));
		e.setCancelled(true);
	}
}
