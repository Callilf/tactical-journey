/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of Mithridatism. 50% resistance to poison.
 * @author Callil
 *
 */
public class BlessingMithridatism extends Blessing {
	
	@Override
	public String title() {
		return "Mithridatism";
	}
	
	@Override
	public String description() {
		return "Grants a 50% resistance to [PURPLE]poison[]";
	}
	
	@Override
	public AtlasRegion texture() {
		return Assets.blessing_mithridatism;
	}

	@Override
	public void onReceive(Entity entity) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		
		if (healthComponent != null) {
			healthComponent.addResistance(DamageType.POISON, 50);
		}
	}

	@Override
	public void onRemove(Entity entity) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		
		if (healthComponent != null) {
			if (healthComponent != null) {
				healthComponent.reduceResistance(DamageType.POISON, 50);
			}
		}
	}

}