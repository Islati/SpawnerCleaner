package com.caved_in.spawnercleaner.config;

import org.simpleframework.xml.Element;

public class Configuration {
	@Element(name = "spawner-limit")
	private int spawnerLimit = 12;

	public Configuration(@Element(name="spawner-limit")int spawnerLimit) {
		this.spawnerLimit = spawnerLimit;
	}

	public Configuration() {}

	public int getSpawnerLimit() {
		return spawnerLimit;
	}
}
