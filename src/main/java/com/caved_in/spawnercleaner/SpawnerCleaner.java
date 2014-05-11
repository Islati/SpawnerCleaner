package com.caved_in.spawnercleaner;

import com.caved_in.commons.Commons;
import com.caved_in.commons.commands.CommandController;
import com.caved_in.commons.file.Folder;
import com.caved_in.commons.plugin.Plugins;
import com.caved_in.commons.time.TimeHandler;
import com.caved_in.commons.time.TimeType;
import com.caved_in.commons.yml.YMLIO;
import com.caved_in.spawnercleaner.commands.SpawnersCommand;
import com.caved_in.spawnercleaner.config.Configuration;
import com.caved_in.spawnercleaner.listeners.BlockBreakListener;
import com.caved_in.spawnercleaner.listeners.BlockPlaceListener;
import com.caved_in.spawnercleaner.listeners.ChunkLoadListener;
import com.caved_in.spawnercleaner.listeners.ChunkUnloadListener;
import com.caved_in.spawnercleaner.world.ChunkManager;
import com.caved_in.spawnercleaner.world.ChunkWrapper;
import net.minecraft.server.v1_7_R3.Chunk;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;

public class SpawnerCleaner extends JavaPlugin {
	private static final String PLUGIN_FOLDER = "plugins/SpawnerCleaner/";
	private static Serializer serializer = new Persister();
	private static Configuration configuration;

	public static final Commons commons = Commons.getInstance();
	public static YMLIO config;
	public static Folder chunksFolder;

	@Override
	public void onEnable() {
		if (!Plugins.hasDataFolder(this)) {
			Plugins.makeDataFolder(this);
		}

		//If the config isn't loaded, then disable this plugin
		if (!initConfig()) {
			Commons.debug(
					"Unable to load spawner-cleaner, error with loading configuration files",
					"Check the log files for more information"
			);
			Plugins.disablePlugin(this);
			return;
		}

		registerListeners();
		registerCommands();

		Commons.threadManager.registerSynchRepeatTask("Save Chunk Config", new Runnable() {
			@Override
			public void run() {
				Commons.debug("Beginning to save all the chunk data");
				ChunkManager.saveData();
				Commons.debug("Chunk data saved");
			}
		},TimeHandler.getTimeInTicks(10,TimeType.MINUTE), TimeHandler.getTimeInTicks(1, TimeType.MINUTE));
	}

	@Override
	public void onDisable() {
		Plugins.unregisterHooks(this);
		ChunkManager.saveData();
	}

	private boolean initConfig() {
		boolean configInitialized = true;

		try {
			File configFile = new File(PLUGIN_FOLDER + "Config.xml");
			if (!configFile.exists()) {
				serializer.write(new Configuration(), configFile);
			}
			configuration = serializer.read(Configuration.class,new File(PLUGIN_FOLDER + "Config.xml"));
		} catch (Exception e) {
			e.printStackTrace();
			configInitialized = false;
		}

		File chunkFolder = new File(PLUGIN_FOLDER + "Chunks/");
		if (!chunkFolder.exists() && !chunkFolder.mkdirs()) {
			return false;
		}

		chunksFolder = new Folder(PLUGIN_FOLDER + "Chunks/");
		if (chunksFolder.getFiles().size() > 0) {
			for(String chunkFile : chunksFolder.getFiles()) {
				try {
					File fileChunkData = new File(chunkFile);
					ChunkWrapper chunkWrapper = serializer.read(ChunkWrapper.class, fileChunkData);
					chunkWrapper.setChunkDataFile(fileChunkData);
					ChunkManager.initChunkWrapper(chunkWrapper);
				} catch (Exception e) {
					e.printStackTrace();
					configInitialized = false;
				}
			}
		}
		return configInitialized;

	}

	private void registerListeners() {
		Plugins.registerListeners(this,
			new ChunkLoadListener(),
			new ChunkUnloadListener(),
			new BlockBreakListener(),
			new BlockPlaceListener()
		);
	}

	private void registerCommands() {
		CommandController.registerCommands(this, new SpawnersCommand());
	}

	public static Configuration getConfiguration() {
		return configuration;
	}

	public static int getSpawnerLimit() {
		return configuration.getSpawnerLimit();
	}
}
