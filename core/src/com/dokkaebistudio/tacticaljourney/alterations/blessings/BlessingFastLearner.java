/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of the fast learner. Can choose one more level up reward.
 * @author Callil
 *
 */
public class BlessingFastLearner extends Blessing {
	
	@Override
	public String title() {
		return "Blessing of the fast learner";
	}
	
	@Override
	public String description() {
		return "When [GOLD]leveling up[], allow to choose an additional reward";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_fast_learner;
	}

	@Override
	public void onReceive(Entity entity) {
		ExperienceComponent experienceComponent = Mappers.experienceComponent.get(entity);
		experienceComponent.setSelectNumber(experienceComponent.getSelectNumber() + 1);
	}

	@Override
	public void onRemove(Entity entity) {
		ExperienceComponent experienceComponent = Mappers.experienceComponent.get(entity);
		experienceComponent.setSelectNumber(experienceComponent.getSelectNumber() - 1);
	}

	@Override
	public void onLevelUp(Entity entity, Room room) {
		Journal.addEntry("Blessing of the fast learner granted you an additional level up reward");
		AlterationSystem.addAlterationProc(this);
	}
}
