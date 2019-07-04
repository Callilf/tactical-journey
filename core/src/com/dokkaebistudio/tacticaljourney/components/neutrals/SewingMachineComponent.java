package com.dokkaebistudio.tacticaljourney.components.neutrals;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemLeather;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is a sewing machine.
 * @author Callil
 *
 */
public class SewingMachineComponent implements Component, Poolable {

	
	@Override
	public void reset() {
	}

	
	
	public void sew(Room room) {
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(GameScreen.player);
		if (inventoryComponent.contains(ItemLeather.class)) {
			Journal.addEntry("[GREEN]" + Journal.getLabel(GameScreen.player) + " used the sewing machine to get 1 more inventory slot.");
			int indexOfLeather = inventoryComponent.indexOf(ItemLeather.class);
			Entity leather = inventoryComponent.getAndRemove(indexOfLeather);
			inventoryComponent.addSlot();
			
			room.removeEntity(leather);
			
			
			VFXUtil.createStatsUpNotif("Inventory slot +1", Mappers.gridPositionComponent.get(GameScreen.player).coord());
			
		}
	}
	
	
	//*********************************
	// Getters and Setters

	
	
	
	public static Serializer<SewingMachineComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<SewingMachineComponent>() {

			@Override
			public void write(Kryo kryo, Output output, SewingMachineComponent object) {
			}

			@Override
			public SewingMachineComponent read(Kryo kryo, Input input, Class<? extends SewingMachineComponent> type) {
				SewingMachineComponent compo = engine.createComponent(SewingMachineComponent.class);
				return compo;
			}
		
		};
	}


	


}
