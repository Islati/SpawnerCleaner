package com.caved_in.spawnercleaner.commands;

import com.caved_in.commons.commands.CommandController;
import com.caved_in.commons.menu.HelpScreen;
import com.caved_in.commons.menu.ItemFormat;
import com.caved_in.commons.menu.Menus;
import com.caved_in.commons.menu.PageDisplay;
import com.caved_in.commons.player.Players;
import com.caved_in.spawnercleaner.world.ChunkManager;
import com.caved_in.spawnercleaner.world.ChunkWrapper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;

public class SpawnersCommand {
	private static HelpScreen spawnersHelpScreen = null;
	@CommandController.CommandHandler(name = "spawners", usage = "/<command>", description = "Help menu for the spawner cleaners")
	public void onSpawnersCommand(Player player, String[] args) {
		if (args.length > 0) {
			return;
		}

		if (spawnersHelpScreen == null) {
			spawnersHelpScreen = Menus.generateHelpScreen("Spawners Help Menu", PageDisplay.DEFAULT, ItemFormat.SINGLE_DASH, ChatColor.YELLOW);
			spawnersHelpScreen.addEntry("/spawners amount", "View the amount of spawners in your current chunk");
		}

		spawnersHelpScreen.sendTo(player,1,7);
	}

	@CommandController.SubCommandHandler(name = "amount", parent = "spawners")
	public void onSpawnersAmountCommand(Player player, String[] args) {
		ChunkWrapper chunkWrapper = ChunkManager.getData(player.getLocation().getChunk());
		Players.sendMessage(player, String.format("&aThere are &e%s&a mob spawners in your current chunk &7(%sx,%sz)",chunkWrapper.getSpawnerCount(),chunkWrapper.getX(),chunkWrapper.getZ()));
	}
}
