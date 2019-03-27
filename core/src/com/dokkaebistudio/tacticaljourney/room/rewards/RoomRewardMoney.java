/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.rewards;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public class RoomRewardMoney extends AbstractRoomReward {

	public RoomRewardMoney() {
		super("gold coin(s)", 0, "[GOLDENROD]");
	}
	
	public RoomRewardMoney(int quantity) {
		super("gold coin(s)", quantity, "[GOLDENROD]");
	}

	@Override
	public void receive(Entity e, Room r) {
		Journal.addEntry("Room reward: " + this.color + this.quantity + " " + this.title);

		WalletComponent walletComponent = Mappers.walletComponent.get(e);
		walletComponent.receive(this.quantity);
	}

}
