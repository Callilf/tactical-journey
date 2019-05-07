package com.dokkaebistudio.tacticaljourney.components.neutrals;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.enums.AmmoTypeEnum;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.ItemArrow;
import com.dokkaebistudio.tacticaljourney.items.ItemBomb;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.AbstractInfusableItem;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is a recycling machine.
 * @author Callil
 *
 */
public class RecyclingMachineComponent implements Component, Poolable {

	
	@Override
	public void reset() {
	}

	
	public void recycle(Entity item, Room room) {
		InventoryComponent playerInventoryCompo = Mappers.inventoryComponent.get(GameScreen.player);
		ItemComponent itemComponent = Mappers.itemComponent.get(item);

		recycle(itemComponent.getItemType(), room);
		
		if (itemComponent.getItemType() instanceof AbstractInfusableItem) {
			((AbstractInfusableItem)itemComponent.getItemType()).removeBlessingsAndCurses(GameScreen.player, item);
		}
		playerInventoryCompo.remove(item);		
		room.removeEntity(item);
	}
	
	public void recycle(AbstractItem item, Room room) {
		WalletComponent walletComponent = Mappers.walletComponent.get(GameScreen.player);			
		walletComponent.receive(item.getRecyclePrice());
		
		Journal.addEntry("[GOLDENROD]Recycled the " + item.getLabel() 
			+ " in exchange of " + item.getRecyclePrice() + " gold coins");
		
		AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(GameScreen.player);
		if (item instanceof ItemArrow) {
			ammoCarrierComponent.useAmmo(AmmoTypeEnum.ARROWS, 1);
		} else if (item instanceof ItemBomb) {
			ammoCarrierComponent.useAmmo(AmmoTypeEnum.BOMBS, 1);
		}
	}
	
	
	
	//*********************************
	// Getters and Setters

	
	
	
	public static Serializer<RecyclingMachineComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<RecyclingMachineComponent>() {

			@Override
			public void write(Kryo kryo, Output output, RecyclingMachineComponent object) {
			}

			@Override
			public RecyclingMachineComponent read(Kryo kryo, Input input, Class<RecyclingMachineComponent> type) {
				RecyclingMachineComponent compo = engine.createComponent(RecyclingMachineComponent.class);
				return compo;
			}
		
		};
	}


	


}
