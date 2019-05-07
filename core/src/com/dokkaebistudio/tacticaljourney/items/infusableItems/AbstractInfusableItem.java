/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.alterations.Alteration;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.ashley.PublicEntity;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent.AlterationActionEnum;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Infusable item.
 * @author Callil
 *
 */
public abstract class AbstractInfusableItem extends AbstractItem {

	protected List<Blessing> blessings = new ArrayList<>();
	protected List<Curse> curses = new ArrayList<>();
	
	protected AbstractInfusableItem(ItemEnum itemType, RegionDescriptor texture, boolean instaPickUp,
			boolean goIntoInventory) {
		super(itemType, texture, instaPickUp, goIntoInventory);
	}
	
	public void setItemEntity(PublicEntity itemEntity) {
		for (Alteration a : blessings) {
			a.setItemEntityId((int) itemEntity.id);
		}
		for (Alteration a : curses) {
			a.setItemEntityId((int) itemEntity.id);
		}
	}
	
	public void removeBlessing(Blessing b) {
		blessings.remove(b);
	}
	public void removeCurse(Curse c) {
		curses.remove(c);
	}
	
	@Override
	public boolean pickUp(Entity picker, Entity item, Room room) {
		boolean pickedUp = super.pickUp(picker, item, room);
		
		if (pickedUp) {
			AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(picker);
			
			for (Blessing b : blessings) {
				if (alterationReceiverComponent != null) {
					alterationReceiverComponent.requestAction(AlterationActionEnum.RECEIVE_BLESSING, b);
				}
			}
			for (Curse c : curses) {
				if (alterationReceiverComponent != null) {
					alterationReceiverComponent.requestAction(AlterationActionEnum.RECEIVE_CURSE, c);
				}
			}
		}
		
		return pickedUp;
	}
	
	
	@Override
	public boolean drop(Entity dropper, Entity item, Room room) {
		boolean dropped = super.drop(dropper, item, room);
	
		if (dropped) {
			removeBlessingsAndCurses(dropper, item);
		}
		
		return dropped;
	}
	
	@Override
	public void onThrow(Vector2 thrownPosition, Entity thrower, Entity item, Room room) {
		super.onThrow(thrownPosition, thrower, item, room);
		
		removeBlessingsAndCurses(thrower, item);
	}

	
	@Override
	public boolean infuse(Entity player, Entity item, Room room) {
		for (Blessing itemBlessing : blessings) {
			itemBlessing.setInfused(true);
		}
		for (Curse itemCurse : curses) {
			itemCurse.setInfused(true);
		}
		
		return super.infuse(player, item, room);
	}
	
	
	

	public void removeBlessingsAndCurses(Entity dropper, Entity item) {
		AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(dropper);
		for (Blessing b : alterationReceiverComponent.getBlessings()) {
			if (b.getItemEntityId() != null && b.getItemEntityId() == ((PublicEntity)item).id) {
				alterationReceiverComponent.requestAction(AlterationActionEnum.REMOVE_BLESSING, b);
			}
		}
		for (Curse c : alterationReceiverComponent.getCurses()) {
			if (c.getItemEntityId() != null && c.getItemEntityId() == ((PublicEntity)item).id) {
				alterationReceiverComponent.requestAction(AlterationActionEnum.REMOVE_CURSE, c);
			}
		}
	}
}
