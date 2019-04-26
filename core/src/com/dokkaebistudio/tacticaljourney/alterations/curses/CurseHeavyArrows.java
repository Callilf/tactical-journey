/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.curses;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Curse of heavy arrows. Range - 1.
 * @author Callil
 *
 */
public class CurseHeavyArrows extends Curse {

	@Override
	public String title() {
		return "Curse of heavy arrows";
	}
	
	@Override
	public String description() {
		return "Reduce bow range by 1";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.curse_heavy_arrows;
	}

	@Override
	public void onReceive(Entity entity) {
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		
		if (playerComponent != null) {
			AttackComponent attackComponent = Mappers.attackComponent.get(playerComponent.getSkillRange());
			attackComponent.increaseRangeMax(-1);
		}
		
		AlterationSystem.addAlterationProc(this);
	}

	@Override
	public void onRemove(Entity entity) {
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		
		if (playerComponent != null) {
			AttackComponent attackComponent = Mappers.attackComponent.get(playerComponent.getSkillRange());
			attackComponent.increaseRangeMax(1);
		}
	}

}
