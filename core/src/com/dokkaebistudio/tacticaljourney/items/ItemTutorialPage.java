/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * A tutorial page.
 * @author Callil
 *
 */
public class ItemTutorialPage extends Item {
	
	private int pageNumber;

	public ItemTutorialPage(int pageNumber) {
		super("Piece of armor", Assets.armor_piece_item, false, true);
		this.pageNumber = pageNumber;
	}
	
	@Override
	public String getDescription() {
		String result = null;
		
		switch (this.pageNumber) {
		case 1:
			result = "Welcome to Tactical Journey.\n"
			+ "Your goal is to reach the end of the last floor. As of now, there is only one floor, so reaching the end of this floor will be enough.";	
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
					+ " end your turn, so stay away from enemies when managing your inventory. Note that picking up money does not end your turn.";	
			break;
			
		case 4:
			result = "The wheel:\n"
					+ "When attacking an enemy, the attack wheel pops up. The damage you deal depends on the color you hit.\n"
					+ " - [GREEN]Green[WHITE]: normal hit, the amount you deal is equal to your strength\n"
					+ " - [GRAY]Gray[WHITE]: graze, the amount you deal is a bit lower than your strength\n"
					+ " - [BLACK]Black[WHITE]: miss, you don't deal any damage\n"
					+ " - [RED]Red[WHITE]: critical, the amount you deal is 2 times your strength.";	
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
	public boolean use(Entity user, Entity item, Room room) {return true;}
}
