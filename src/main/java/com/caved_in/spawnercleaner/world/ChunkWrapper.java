package com.caved_in.spawnercleaner.world;

import com.caved_in.commons.Commons;
import com.caved_in.commons.Messages;
import com.caved_in.commons.config.XmlLocation;
import com.caved_in.commons.threading.tasks.RetrieveChunkMaterialLocationCallable;
import com.caved_in.commons.world.ChunkData;
import com.caved_in.commons.world.ChunkState;
import com.caved_in.commons.world.Chunks;
import com.caved_in.commons.world.Worlds;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.io.File;
import java.util.*;

public class ChunkWrapper {
	@Element(name = "world")
	private String worldName;

	@Element(name = "x-pos")
	private final int x;

	@Element(name = "y-pos")
	private final int z;

	private final int[] cords;

	@Element(name = "state", type = ChunkState.class)
	private ChunkState state = ChunkState.UNPROCESSED;

	private File chunkDataFile = null;
	private Set<Location> spawnerLocations = new HashSet<>();

	@ElementList(name = "spawner-locations", entry = "spawner", type = XmlLocation.class, inline = true)
	private List<XmlLocation> xmlSpawnerLocations = new ArrayList<>();

	public ChunkWrapper(Chunk chunk) {
		worldName = chunk.getWorld().getName();
		x = chunk.getX();
		z = chunk.getZ();
		this.cords = new int[]{x, z};
		findSpawners();
	}

	public boolean similarTo(Object o) {
		if (o instanceof ChunkWrapper) {
			ChunkWrapper chunkData = (ChunkWrapper) o;
			return (equals(o)) || (x == chunkData.x && z == chunkData.z);
		} else if (o instanceof Chunk) {
			Chunk chunk = (Chunk) o;
			return x == chunk.getX() && z == chunk.getZ();
		} else {
			return false;
		}
	}

	private ChunkWrapper getWrapper() {
		return this;
	}

	public ChunkWrapper(@Element(name = "world") String worldName, @Element(name = "x-pos") int x,
						@Element(name = "z-pos") int z, @Element(name = "state", type = ChunkState.class) ChunkState state,
						@ElementList(name = "spawner-locations", entry = "spawner", type = XmlLocation.class, inline = true) ArrayList<XmlLocation> spawnerLocations) {
		this.x = x;
		this.z = z;
		this.cords = new int[]{x, z};
		this.worldName = worldName;
		xmlSpawnerLocations.addAll(spawnerLocations);
		for(XmlLocation location : spawnerLocations) {
			this.spawnerLocations.add(location.getLocation());
		}
	}

	public void addSpawnerLocation(Location location) {
		spawnerLocations.add(location);
		xmlSpawnerLocations.add(new XmlLocation(location));
	}

	private void findSpawners() {
		ListenableFuture<Map<Location, Material>> locateSpawnersFuture = Commons.syncExecutor.submit(new RetrieveChunkMaterialLocationCallable(getChunk(), new Material[]{Material.MOB_SPAWNER}));
		Futures.addCallback(locateSpawnersFuture, new FutureCallback<Map<Location, Material>>() {
			@Override
			public void onSuccess(Map<Location, Material> locationMaterialMap) {
				for (Map.Entry<Location, Material> entry : locationMaterialMap.entrySet()) {
					Location location = entry.getKey();
					spawnerLocations.add(location);

					Commons.debug("New chunk wrapper for chunk @ " + getX() + "x, " + getZ() + "z");
					Commons.debug(Messages.locationInfo(location));
				}
				ChunkManager.loadChunkwrapperLocations(getWrapper());
				state = ChunkState.PROCESSED;
			}

			@Override
			public void onFailure(Throwable throwable) {
				Commons.debug(
						String.format("&eError in ChunkWrapper @ %sx, %sz", getX(), getZ()),
						String.format("&c%s", throwable.getMessage())
				);
			}
		});
	}

	public int getZ() {
		return z;
	}

	public Set<Location> getSpawnerLocations() {
		return spawnerLocations;
	}

	public int getSpawnerCount() {
		return spawnerLocations.size();
	}

	public boolean isSpawnerLocation(Location location) {
		return spawnerLocations.contains(location);
	}

	public void removeSpawnerLocation(Location location) throws NullPointerException {
		spawnerLocations.remove(location);
		xmlSpawnerLocations.remove(getXmlLocation(location));
	}

	private XmlLocation getXmlLocation(Location location) {
		for (XmlLocation xmlLocation : xmlSpawnerLocations) {
			if (location.equals(xmlLocation.getLocation())) {
				return xmlLocation;
			}
		}
		return null;
	}

	public File getChunkDataFile() {
		if (chunkDataFile == null) {
			chunkDataFile = new File(String.format("plugins/SpawnerCleaner/Chunks/%s-%s.xml",getX(),getZ()));
		}
		return chunkDataFile;
	}

	public void setChunkDataFile(File chunkDataFile) {
		this.chunkDataFile = chunkDataFile;
	}

	public String getWorldName() {
		return worldName;
	}

	public int getX() {
		return x;
	}

	public Chunk getChunk() {
		return Chunks.getChunkAt(Worlds.getWorld(worldName), x, z);
	}

	public int[] getCords() {
		return cords;
	}
}
