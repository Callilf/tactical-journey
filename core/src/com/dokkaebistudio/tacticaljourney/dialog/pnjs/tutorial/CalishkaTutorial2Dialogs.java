package com.dokkaebistudio.tacticaljourney.dialog.pnjs.tutorial;

import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ashley.PublicEntity;
import com.dokkaebistudio.tacticaljourney.components.ChasmComponent;
import com.dokkaebistudio.tacticaljourney.components.TutorialComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.CalishkaComponent;
import com.dokkaebistudio.tacticaljourney.dialog.AbstractDialogs;
import com.dokkaebistudio.tacticaljourney.dialog.Dialog;
import com.dokkaebistudio.tacticaljourney.dialog.DialogBuilder;
import com.dokkaebistudio.tacticaljourney.dialog.DialogCondition;
import com.dokkaebistudio.tacticaljourney.dialog.DialogEffect;
import com.dokkaebistudio.tacticaljourney.enums.TileEnum;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

public class CalishkaTutorial2Dialogs extends AbstractDialogs {
	
	@Override
	protected void setSpeaker(Dialog d) {
		d.setSpeaker(Descriptions.CALISHKA_TITLE);
	}
	
	@SuppressWarnings("unchecked")
	public CalishkaTutorial2Dialogs() {

		// Basic dialog
		
		this.addDialog(new DialogBuilder()
				.addText("Now let's talk about items. There are various items that you can find in the dungeon. Some can be consumed, some can be thrown, some just have passive effects.")
				.addText("It will be up to you to discover items and understand what they can do.")
				.addText("To pick up an item, you just have to stop on the tile where this item is and it will be automatically picked up.")
				.addText("Try picking up one of the rocks in this room.")
				.setRepeat(false)
				.build());
		this.addDialog(new DialogBuilder()
				.addText("To pick up an item, you just have to stop on the tile where this item is and it will be automatically picked up. Try picking up one of the rocks in this room.")
				.setRepeat(true)
				.build());
		
		// After picking up a rock
		
		DialogCondition pickedUpRockCondition = new DialogCondition() {
			@Override
			public boolean test(PublicEntity e) {
				TutorialComponent tc = Mappers.tutorialComponent.get(e);
				return tc.isGoal1Reached() && !tc.isGoal2Reached();
			}
		};
		
		this.addDialog(new DialogBuilder()
				.addText("Perfect.")
				.addText("Did you see that the rock went right into your inventory?")
				.addText("You can check you inventory at any time by [ORANGE]clicking[] on the inventory button at the right of the profile button.")
				.addText("In your inventory, you can see your inventory slots. If all slots are taken, you won't be able to pick up any new item.")
				.addText("By [ORANGE]clicking[] on an item, you can drop, throw or 'use' the item. Note that the definition of 'use' depend on the item. Also some items cannot be used, like these rocks.")
				.addText("Now try throwing a rock into the mud over this chasm.")
				.setRepeat(false)
				.setCondition(pickedUpRockCondition)
				.build());
		
		this.addDialog(new DialogBuilder()
				.addText("To throw a rock, [YELLOW]open[] your inventory, [YELLOW]click[] on the rock and [YELLOW]select[] throw. Try throwing a rock into the mud over this chasm")
				.setRepeat(true)
				.setCondition(pickedUpRockCondition)
				.build());
		
		
		// After throwing a rock
		
		DialogCondition threwRockCondition = new DialogCondition() {
			@Override
			public boolean test(PublicEntity e) {
				TutorialComponent tc = Mappers.tutorialComponent.get(e);
				return tc.isGoal2Reached() && !tc.isGoal3Reached();			}
		};
		
		
		this.addDialog(new DialogBuilder()
				.addText("Nice throw!")
				.addText("Most of the items can be thrown. Some might get broken when you throw them, though. If you want to place an item on the ground without activating nor breaking it, "
						+ "you should just drop it.")
				.addText("You can drop as many items as you want on the same tile. Just keep in mind that throwing, dropping or picking up an item will consume a turn.")
				.addText("Even if sometimes you will find items on the ground like these rocks, the most common way of finding items is to loot containers.")
				.addText("Go and loot the bag on the other side of the pit, grab the armor inside and then equip it!")
				.setRepeat(false)
				.setCondition(threwRockCondition)
				.setEffect(new DialogEffect() {
					public void play(Room room) {
						VFXUtil.createSmokeEffect(new Vector2(18, 5));
						Optional<Entity> chasm = TileUtil.getEntityWithComponentOnTile(new Vector2(18, 5), ChasmComponent.class, room);
						if (chasm.isPresent()) {
							room.removeEntity(chasm.get());
						}
						VFXUtil.createSmokeEffect(new Vector2(18, 6));
						chasm = TileUtil.getEntityWithComponentOnTile(new Vector2(18, 6), ChasmComponent.class, room);
						if (chasm.isPresent()) {
							room.removeEntity(chasm.get());
						}				
						VFXUtil.createSmokeEffect(new Vector2(18, 7));
						chasm = TileUtil.getEntityWithComponentOnTile(new Vector2(18, 7), ChasmComponent.class, room);
						if (chasm.isPresent()) {
							room.removeEntity(chasm.get());
						}
						
						Optional<Entity> lootable = TileUtil.getEntityWithComponentOnTile(new Vector2(11, 6), LootableComponent.class, room);
						if (lootable.isPresent()) {
							LootableComponent lootableComponent = Mappers.lootableComponent.get(lootable.get());
							lootableComponent.getItems().clear();
							lootableComponent.getItems().add(room.entityFactory.itemFactory.createItem(ItemEnum.ARMOR_LIGHT));
						}
						room.turnManager.endPlayerTurn();
					}
				})
				.build());
		this.addDialog(new DialogBuilder()
				.addText("To loot a container, end your turn on it. After spending several turns opening it, you will see what it contains. [YELLOW]Click[] on the items you want to pick up. "
						+ "Note that each item will take one turn to get picked up.")
				.setRepeat(true)
				.setCondition(threwRockCondition)
				.build());
		
		// After equipping armor
		
		DialogCondition equippedArmorCondition = new DialogCondition() {
			@Override
			public boolean test(PublicEntity e) {
				TutorialComponent tc = Mappers.tutorialComponent.get(e);
				return tc.isGoal3Reached();			
			}
		};
		
		this.addDialog(new DialogBuilder()
				.addText("Well done! You know enough about items now.")
				.addText("Find your way to the next room so that we get serious and talk about combat!")
				.setRepeat(false)
				.setCondition(equippedArmorCondition)
				.setEffect(new DialogEffect() {
					public void play(Room room) {
						room.openDoors();
						
						VFXUtil.createSmokeEffect(new Vector2(5, 6));
						Entity satchel = room.entityFactory.lootableFactory.createSatchel(room, new Vector2(5, 6));
						Mappers.lootableComponent.get(satchel).getItems().add(room.entityFactory.itemFactory.createItem(ItemEnum.POTION_WING));
						
						VFXUtil.createSmokeEffect(new Vector2(20, 7));
						Optional<Entity> calishka = TileUtil.getEntityWithComponentOnTile(new Vector2(20, 7), CalishkaComponent.class, room);
						room.removeEntity(calishka.get());
					}
				})
				.build());

	}

}
