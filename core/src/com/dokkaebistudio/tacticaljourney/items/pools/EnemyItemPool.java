package com.dokkaebistudio.tacticaljourney.items.pools;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;

public final class EnemyItemPool {

	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> enemyItemPool = new ArrayList<>();
	
	private static int sumOfChances;
	
	static {
		enemyItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_ARROW, 10));
		enemyItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_BOMB, 10));
		
		enemyItemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_PIECE, 10));
		enemyItemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_LIGHT, 5));

		enemyItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_FIRE, 10));
		enemyItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_SMALL_HEALTH, 15));
		
		enemyItemPool.add(new PooledItemDescriptor(ItemEnum.WEB_SACK, 50));
		
		for (PooledItemDescriptor pid : enemyItemPool) {
			sumOfChances += pid.getChanceToDrop();
		}
	}
	
	
	/**
	 * Randomly retrieve x item types. There can be duplicates.
	 * @param numberOfItemsToGet the number of items to retrieve
	 * @return the list of item types randomly chosen
	 */
	public static List<PooledItemDescriptor> getItemTypes(int numberOfItemsToGet) {
		List<PooledItemDescriptor> result = new ArrayList<>();
		
		RandomXS128 seededRandom = RandomSingleton.getInstance().getSeededRandom();
		int randomInt = 0;
		
		int chance = 0;
		for (int i=0 ; i<numberOfItemsToGet ; i++) {
			
			randomInt = seededRandom.nextInt(sumOfChances);
			for (PooledItemDescriptor pid : enemyItemPool) {
				if (randomInt >= chance && randomInt < chance + pid.getChanceToDrop()) {
					//This item is chosen
					result.add(pid);
					break;
				}
				chance += pid.getChanceToDrop();
			}
			
			chance = 0;
		}
		
		return result;
	}
}
