/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.curses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Curse of weakness. Reduce the entity's strength by 1.
 * @author Callil
 *
 */
public class CurseWeakness extends Curse {

	@Override
	public String title() {
		return "Curse of weakness";
	}
	
	@Override
	public String description() {
		return "Reduce strength by 1";
	}
	
	@Override
	public AtlasRegion texture() {
		return Assets.curse_weakness;
	}

	@Override
	public void onReceive(Entity entity) {
		AttackComponent attackCompo = Mappers.attackComponent.get(entity);
		
		if (attackCompo != null) {
			attackCompo.setStrength(attackCompo.getStrength() - 1);
		}
	}

	@Override
	public void onRemove(Entity entity) {
		AttackComponent attackCompo = Mappers.attackComponent.get(entity);
		
		if (attackCompo != null) {
			attackCompo.setStrength(attackCompo.getStrength() + 1);
		}
	}

}
