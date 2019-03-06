/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemFataMorgana;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of Vilma. Restore hp on kill based on the amount of damage of the final blow.
 * @author Callil
 *
 */
public class BlessingOfVilma extends Blessing {
	
	private final int initialChanceToProc = 25;
	private final int fataMorganaInInventoryAdd = 8;
	
	@Override
	public String title() {
		return "Blessing of Vilma";
	}
	
	@Override
	public String description() {
		return "On kill, chance to restore hp based on the damages of the final blow";
	}
	
	@Override
	public AtlasRegion texture() {
		return Assets.blessing_of_vilma;
	}

	
	@Override
	public void onKill(Entity attacker, Entity target, Room room) {
		int chanceToProc = this.initialChanceToProc;
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(attacker);
		if (inventoryComponent.contains(ItemFataMorgana.class)) {
			chanceToProc += this.fataMorganaInInventoryAdd;
		}
		
		int chance = RandomSingleton.getInstance().getUnseededRandom().nextInt(100);
		if (chance < chanceToProc) {
			HealthComponent healthComponent = Mappers.healthComponent.get(attacker);
			
			if (healthComponent != null) {
				int healAmount = Mappers.healthComponent.get(target).getLatestAttackDamage();
				healthComponent.restoreHealth(healAmount);
				
				Journal.addEntry("[GREEN]Blessing of Vilma restored " + healAmount + " hp.");
			}
		}
	}

}
