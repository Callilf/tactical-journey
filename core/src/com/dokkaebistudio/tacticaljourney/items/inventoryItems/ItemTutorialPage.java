/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

/**
 * A tutorial page.
 * @author Callil
 *
 */
public class ItemTutorialPage extends AbstractItem {
	
	private int pageNumber;
	
	public ItemTutorialPage() {
		super("Tutorial page", Assets.tutorial_page_item, false, true);
		this.setPaper(true);
	}

	public ItemTutorialPage(int pageNumber) {
		super("Tutorial page", Assets.tutorial_page_item, false, true);
		this.pageNumber = pageNumber;
		this.setPaper(true);
	}
	
	@Override
	public String getLabel() {
		String result = null;

		switch (this.pageNumber) {
		case 1:
			result = "Tutorial page 1";
			break;
		case 2:
			result = "Tutorial page 2";
			break;
		case 3:
			result = "Tutorial page 3";
			break;
		case 4:
			result = "Tutorial page 4";
			break;
		case 5:
			result = "Tutorial page 5";
			break;
		}
		
		return result;
	}
	
	
	@Override
	public String getDescription() {
		String result = null;
		
		switch (this.pageNumber) {
		case 1:
			result = "Welcome to Calishka's Trial.\n"
			+ "Your goal is to reach the end of the last floor and get your hands on the [GOLDENROD]Universal Cure[], a legendary concoction that can apparently"
			+ " cure all diseases.\n\n"
			+ "Calishka is not here to help you yet, she'll arrive soon...";	
			break;
			
		case 2:
			result = "Skills: \n"
					+ "On the bottom right of the screen you have 3 skills:\n"
					+ " - The melee skill allowing you to attack anything close\n"
					+ " - The range skill, allowing you to use your bow given you have arrows\n"
					+ " - The bomb skill that allows you throwing bombs that explode after 2 turns.";
			break;
			
		case 3:
			result = "Turns:\n"
					+ "Remember that mostly everything in this game except movement takes a turn. Using, droping or picking up an item will"
					+ " end your turn, so stay away from enemies when managing your inventory.";	
			break;
			
		case 4:
			result = "The wheel:\n"
					+ "When attacking an enemy, the attack wheel pops up. The damage you deal depends on the color you hit.\n"
					+ " - [GREEN]Green[WHITE]: normal hit, the amount you deal is equal to your strength\n"
					+ " - [GRAY]Gray[WHITE]: graze, the amount you deal is a bit lower than your strength\n"
					+ " - [BLACK]Black[WHITE]: miss, you don't deal any damage\n"
					+ " - [RED]Red[WHITE]: critical, the amount you deal is 2 times your strength.";	
			break;
			
		case 5:
			result = "Orbs:\n"
					+ "Orbs are volatile entities that can be found in the dungeon. Upon discovering an orb, it will automatically take an empty orb"
					+ " spot around you. You only have 4 orbs spots which are the 4 tiles around you (up, down, left and right). If all spots are taken and you discover"
					+ " a new orb, it will just vanish.\n"
					+ "Orbs have special properties that will automatically be activated upon entering on contact with another living creature.\n"
					+ "[GOLDENROD]Activating an orb doesn't consume you turn.";	
			break;
			
			default:
				
		}
			
		return result;
	}
	
	@Override
	public String getActionLabel() {
		return "Tear";
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		Journal.addEntry("You tore down the tutorial page " + pageNumber + ".");
		VFXUtil.createDisappearanceEffect(Mappers.gridPositionComponent.get(user).coord(), Mappers.spriteComponent.get(item).getSprite());
		return true;
	}
}
