package com.caved_in.spawnercleaner.world;

import com.caved_in.commons.Commons;
import com.caved_in.commons.world.ChunkData;
import com.caved_in.commons.world.Chunks;
import com.caved_in.commons.yml.YMLIO;
import com.caved_in.spawnercleaner.SpawnerCleaner;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.util.*;

public class ChunkManager {
	private static Map<Location,int[]> mobSpawnerLocations = new HashMap<>();
	private static Map<int[], ChunkWrapper> chunkDataMap = new HashMap<>();

	public static boolean initChunk(Chunk chunk) {
		if (!chunk.isLoaded() && !chunk.load()) {
			return false;
		}

		//Init the chunk data
		ChunkWrapper chunkWrapper = new ChunkWrapper(chunk);
		//Get the co-ords for the chunk
		int[] chunkCords = Chunks.getChunkCords(chunk);
		chunkDataMap.put(chunkCords,chunkWrapper);
		return true;
	}

	public static boolean initChunkWrapper(ChunkWrapper chunkWrapper) {
		Chunk chunk = chunkWrapper.getChunk();
		if (!chunk.isLoaded() && !chunk.load()) {
			return false;
		}
		//Init the chunk wrapper and in the map of chunks for the chunk manager
		chunkDataMap.put(chunkWrapper.getCords(),chunkWrapper);
		//Get all the spawner locations and add them to the chunkmanagers list of spawner locations
		loadChunkwrapperLocations(chunkWrapper);
		return true;
	}

	public static void loadChunkwrapperLocations(ChunkWrapper wrapper) {
		for(Location location : wrapper.getSpawnerLocations()) {
			mobSpawnerLocations.put(location,Chunks.getChunkCords(location.getChunk()));
		}
	}

	public static boolean hasData(Chunk chunk) {
		return chunkDataMap.containsKey(Chunks.getChunkCords(chunk));
	}

	public static ChunkWrapper getData(Chunk chunk) {
		return chunkDataMap.get(Chunks.getChunkCords(chunk));
	}

	public static ChunkWrapper getData(Location location) {
		return getData(location.getChunk());
	}

	public static void removeData(Chunk chunk) {
		chunkDataMap.remove(Chunks.getChunkCords(chunk));
	}

	public static void saveData() {
		final Serializer serializer = new Persister();
		Commons.threadManager.runTaskNow(new Runnable() {
			@Override
			public void run() {
				for(ChunkWrapper wrapper : ChunkManager.getChunkWrappers()) {
					try {
						serializer.write(wrapper,wrapper.getChunkDataFile());
					} catch (Exception e) {
						Commons.debug("Error saving chunk " + wrapper.getX() + "x, " + wrapper.getZ() + "z.");
						e.printStackTrace();
					}
				}
			}
		});
	}

	public static boolean isSpawnerLocation(Location location) {
		return mobSpawnerLocations.keySet().contains(location) || chunkDataMap.keySet().contains(Chunks.getChunkCords(location.getChunk()));
	}


	public static boolean removeSpawnerLocation(Location location) {
		if (!isSpawnerLocation(location)) {
			return false;
		}

		ChunkWrapper wrapper = getData(location);
		return chunkDataMap.remove(wrapper.getCords()) != null;
	}

	public static boolean addSpawnerLocation(Location location) {
		if (isSpawnerLocation(location)) {
			return false;
		}

		Chunk chunk = location.getChunk();
		int[] chunkCords = Chunks.getChunkCords(location.getChunk());
		//If there is no data for this chunk, then init the chunk
		if (!hasData(chunk)) {
			initChunk(chunk);
		}

		ChunkWrapper chunkWrapper = getData(chunk);
		if (chunkWrapper.getSpawnerCount() >= SpawnerCleaner.getSpawnerLimit()) {
			return false;
		}

		chunkWrapper.addSpawnerLocation(location);
		return true;
	}

	public static Collection<ChunkWrapper> getChunkWrappers() {
		return chunkDataMap.values();
	}
}
