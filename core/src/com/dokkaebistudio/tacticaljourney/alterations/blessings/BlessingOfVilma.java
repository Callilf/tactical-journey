/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemFataMorgana;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.entitysystems.AlterationSystem;
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
		return "On kill, chance to [GREEN]restore hp[] based on the damages of the final blow. Chance increased while holding the Fata Morgana";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_of_vilma;
	}
	
	@Override
	public Integer getCurrentProcChance(Entity user) {
		int chanceToProc = this.initialChanceToProc;
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(user);
		if (inventoryComponent.contains(ItemFataMorgana.class)) {
			chanceToProc += this.fataMorganaInInventoryAdd;
		}
		return chanceToProc;
	}

	
	@Override
	public void onKill(Entity attacker, Entity target, Room room) {		
		float randomValue = RandomSingleton.getNextChanceWithKarma();
		if (randomValue < getCurrentProcChance(attacker)) {
			HealthComponent healthComponent = Mappers.healthComponent.get(attacker);
			
			if (healthComponent != null) {
				int healAmount = Mappers.healthComponent.get(target).getLatestAttackDamage();
				healthComponent.restoreHealth(healAmount);
				
				Journal.addEntry("[GREEN]Blessing of Vilma restored " + healAmount + " hp.");
				AlterationSystem.addAlterationProc(this);
			}
		}
	}

}
