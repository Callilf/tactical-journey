package com.dokkaebistudio.tacticaljourney.dialog.pnjs.tutorial;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ashley.PublicEntity;
import com.dokkaebistudio.tacticaljourney.dialog.AbstractDialogs;
import com.dokkaebistudio.tacticaljourney.dialog.DialogBuilder;
import com.dokkaebistudio.tacticaljourney.dialog.DialogCondition;
import com.dokkaebistudio.tacticaljourney.dialog.DialogEffect;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

public class CalishkaTutorial1Dialogs extends AbstractDialogs {
	
	public CalishkaTutorial1Dialogs() {

		// Basic dialog
		
		this.addDialog(new DialogBuilder()
				.addText("Welcome in my dungeon, adventurer. I am Calishka, the keeper of this place.")
				.addText("It is the [YELLOW]universal cure[] you seek I suppose, you're not the first.")
				.addText("Well I've been bonded to this place centuries ago, and were you to succeed in finding the cure, I would be freed. Thus I shall assist any adventurer like you that enters these walls.")
				.addText("First of all, let's discuss about movements.")
				.addText("In order to move, you can [ORANGE]click[] on any tile you want, and if the movement towards this tile is possible, a path will "
						+ "be displayed. [ORANGE]Click again[] on the same tile to execute the movement for real.")
				.addText("Come on, try reaching the [GREEN]bush[] and come back here.")
				.setRepeat(false)
				.setEffect(new DialogEffect() {
					public void play(Entity speaker, Room room) {
						VFXUtil.createSmokeEffect(new Vector2(21, 6));
						room.entityFactory.creepFactory.createBush(room, new Vector2(21, 6), false);
					}
				})
				.build());
		this.addDialog(new DialogBuilder()
				.addText("In order to move, you can [ORANGE]click[] on any tile you want, and if the movement towards this tile is possible, a path will "
						+ "be displayed. [ORANGE]Click[] again on the same tile to execute the movement for real. Try reaching the [GREEN]bush[] at the east of the room and come back.")
				.setRepeat(true)
				.build());
		
		// After reaching the bush
		
		DialogCondition reachedBushCondition = new DialogCondition() {
			@Override
			public boolean test(PublicEntity e) {
				return Mappers.tutorialComponent.get(e).isGoal1Reached();
			}
		};
		
		this.addDialog(new DialogBuilder()
				.addText("Well done.")
				.addText("Did you notice the NEW TURN markers that have appeared during your trip?")
				.addText("In here, ever action takes turns. Most of them take one turn but some can take many turn.")
				.addText("During your turn, you can move and then perform an action such as attacking an enemy, using a potion, activating something and so on.")
				.addText("Right now, your maximum number of movements during a turn is 5, you can see it, among other stats, in your profile page if you [ORANGE]click[] on the profile button at the bottom of the screen.")
				.addText("Try opening your profile page and locate the 'move' characteristic.")
				.setRepeat(false)
				.setCondition(reachedBushCondition)
				.setActivateMarker(true)
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
				.addText("Now join me in the next room, it's time to show you more about items and inventory.")
				.setRepeat(false)
				.setCondition(reachedBushCondition)
				.setEffect(new DialogEffect() {
					public void play(Entity speaker, Room room) {
						room.openDoors();
						
						VFXUtil.createSmokeEffect(new Vector2(5, 6));
						room.removeEntity(speaker);
					}
				})
				.build());
	}

}
