/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of strength. Increase the entity's strength by 1.
 * @author Callil
 *
 */
public class BlessingStrength extends Blessing {
	
	@Override
	public String title() {
		return "Blessing of strength";
	}
	
	@Override
	public AtlasRegion texture() {
		return Assets.blessing_strength;
	}

	@Override
	public void onReceive(Entity entity) {
		AttackComponent attackCompo = Mappers.attackComponent.get(entity);
		
		if (attackCompo != null) {
			attackCompo.setStrength(attackCompo.getStrength() + 1);
		}
	}

	@Override
	public void onRemove(Entity entity) {
		AttackComponent attackCompo = Mappers.attackComponent.get(entity);
		
		if (attackCompo != null) {
			attackCompo.setStrength(attackCompo.getStrength() - 1);
		}
	}

}