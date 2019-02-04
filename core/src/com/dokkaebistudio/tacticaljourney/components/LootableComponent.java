package com.dokkaebistudio.tacticaljourney.components;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.enums.LootableEnum;

/**
 * Indicate that this entity can be looted by the player.
 * @author Callil
 *
 */
public class LootableComponent implements Component, Poolable {
	
	/** The type of lootable. */
	private LootableEnum type;
	
	/** The loot. */
	private List<Entity> items = new ArrayList<>();

	private LootableStateEnum lootableState = LootableStateEnum.CLOSED;
	
	public enum LootableStateEnum {
		CLOSED,
		OPENED;
	}
	
	
	@Override
	public void reset() {
		items.clear();
		lootableState = LootableStateEnum.CLOSED;
	}
	
	/**
	 * Get the number of turns to open the lootable.
	 * @return the number of turns
	 */
	public int getNbTurnsToOpen() {
		if (lootableState == LootableStateEnum.CLOSED) {
			return type.getNbTurnsToOpen();
		} else {
			return 0;
		}
	}
	
	
	
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


	public LootableStateEnum getLootableState() {
		return lootableState;
	}


	public void setLootableState(LootableStateEnum lootableState) {
		this.lootableState = lootableState;
	}

	
	
}