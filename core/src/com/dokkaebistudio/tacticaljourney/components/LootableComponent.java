package com.dokkaebistudio.tacticaljourney.components;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.enums.LootableEnum;

/**
 * Indicate that this entity can be looted by the player.
 * @author Callil
 *
 */
public class LootableComponent implements Component {
	
	/** The type of lootable. */
	private LootableEnum type;
	
	/** The loot. */
	private List<Entity> items = new ArrayList<>();

	
	
	
	
	// Getters and Setters
	
	public List<Entity> getItems() {
		return items;
	}

	public void setItems(List<Entity> items) {
		this.items = items;
	}

	public LootableEnum getType() {
		return type;
	}

	public void setType(LootableEnum type) {
		this.type = type;
	}

	
	
}
