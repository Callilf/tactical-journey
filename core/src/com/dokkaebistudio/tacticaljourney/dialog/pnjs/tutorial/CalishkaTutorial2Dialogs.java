package com.dokkaebistudio.tacticaljourney.dialog.pnjs.tutorial;

import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ashley.PublicEntity;
import com.dokkaebistudio.tacticaljourney.dialog.AbstractDialogs;
import com.dokkaebistudio.tacticaljourney.dialog.Dialog;
import com.dokkaebistudio.tacticaljourney.dialog.DialogBuilder;
import com.dokkaebistudio.tacticaljourney.dialog.DialogCondition;
import com.dokkaebistudio.tacticaljourney.dialog.DialogEffect;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

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
		
		// After reaching the bush
		
		DialogCondition pickedUpRockCondition = new DialogCondition() {
			@Override
			public boolean test(PublicEntity e) {
				return Mappers.tutorialComponent.get(e).isGoal1Reached();
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
				.addText("In the profile popup, you can see 3 tabs: the Characteristics, the [YELLOW]Blessings[] and the [PURPLE]Curses[].")
				.addText("The characteristics tab shows all your current stats. If you want more information about them, just click on the small (i) beside the tab's name.")
				.addText("[YELLOW]Blessings[] and [PURPLE]curses[] are passive abilities that you can receive during your journey through the dungeon. [YELLOW]Blessings[] are good and will "
						+ "help you overcome the trials ahead. [PURPLE]Curses[], on the other hand, are negative perks that will make your journey harder.")
				.addText("There are several ways of receiving [YELLOW]blessings[] and [PURPLE]curses[], but the most common one is to find personal items.")
				.addText("Personal items are objects that belonged to previous adventurers that came here. All of them will grant you at least one blessing, but most of them will also "
						+ "be cursed.")
				.addText("Notice that you already have one [YELLOW]Blessings[], this is my gift and it will help you get started.")
				.addText("Now go to the next room, it's time to show you more about items, personal items and inventory.")
				.setRepeat(false)
				.setCondition(pickedUpRockCondition)
				.setEffect(new DialogEffect() {
					public void play(Room room) {
						room.openDoors();
					}
				})
				.build());
		this.addDialog(new DialogBuilder()
				.addText("Go to the next room, to the west.")
				.setRepeat(true)
				.setCondition(pickedUpRockCondition)
				.build());
	}

}
