/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.rewards.RoomRewardMoney;

/**
 * Blessing of celerity. Increase the entity's movement speed by 1.
 * @author Callil
 *
 */
public class BlessingContractKiller extends Blessing {
	
	@Override
	public String title() {
		return "Blessing of the Contract Killer";
	}
	
	@Override
	public String description() {
		return "Upon clearing a room, earn more gold coins.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_contract_killer;
	}

	@Override
	public void onRoomCleared(Entity entity, Room room) {
		RoomRewardMoney money = new RoomRewardMoney(1 + RandomSingleton.getInstance().getUnseededRandom().nextInt(3));
		money.setTitle("extra gold coin(s)");
		room.addRewards(money);
	}

}
