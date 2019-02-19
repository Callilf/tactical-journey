/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.curses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Curse of slowness. Reduce the entity's movement speed by 1.
 * @author Callil
 *
 */
public class CurseSlowness extends Curse {

	@Override
	public String title() {
		return "Curse of slowness";
	}
	
	@Override
	public AtlasRegion texture() {
		return Assets.curse_slowness;
	}

	@Override
	public void onReceive(Entity entity) {
		MoveComponent moveCompo = Mappers.moveComponent.get(entity);
		
		if (moveCompo != null) {
			moveCompo.increaseMoveSpeed(-1);
		}
	}

	@Override
	public void onRemove(Entity entity) {
		MoveComponent moveCompo = Mappers.moveComponent.get(entity);
		
		if (moveCompo != null) {
			moveCompo.increaseMoveSpeed(1);
		}
	}

}
