/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * A shuriken that can be thrown to enemies
 * @author Callil
 *
 */
public class ItemShuriken extends AbstractItem {

	public ItemShuriken() {
		super(ItemEnum.SHURIKEN, Assets.shuriken_item, false, true);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_SHURIKEN_DESCRIPTION;		
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean isStackable() {
		return true;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		return true;
	}
	
	@Override
	public void onThrow(Vector2 thrownPosition, Entity thrower, Entity item, Room room) {
		super.onThrow(thrownPosition, thrower, item, room);
		
		Optional<Entity> enemyOpt = TileUtil.getEntityWithComponentOnTile(thrownPosition, EnemyComponent.class, room);
		if (enemyOpt.isPresent()) {
			Entity enemy = enemyOpt.get();
			
			AttackComponent attackCompo = null;
			PlayerComponent playerComponent = Mappers.playerComponent.get(thrower);
			if (playerComponent != null) {
				attackCompo = Mappers.attackComponent.get(playerComponent.getSkillThrow());
			}
			
			// DEAL 5 DAMAGES
			room.attackManager.applyDamage(thrower, enemy, 5, DamageType.NORMAL, attackCompo);
			
			room.removeEntity(item);
		}
	}
}
