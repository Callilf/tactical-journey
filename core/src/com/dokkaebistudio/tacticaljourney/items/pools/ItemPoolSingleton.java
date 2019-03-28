package com.dokkaebistudio.tacticaljourney.items.pools;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.PangolinItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.PangolinMotherItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.ScorpionItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.SpiderItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.StingerItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.VenomSpiderItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.WebSpiderItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.tribesmen.TribesmenScoutItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.tribesmen.TribesmenShieldItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.tribesmen.TribesmenSpearItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.lootables.AdventurersSatchelItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.lootables.OldBonesItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.lootables.OrbBagItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.lootables.PersonalBelongingsItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.shops.BasicShopItemPool;

public class ItemPoolSingleton {

	private static ItemPoolSingleton instance;
	
	public static ItemPoolSingleton getInstance() {
		if (instance == null) {
			instance = new ItemPoolSingleton();
		}
		return instance;
	}
	
	private List<ItemEnum> removedItems = new ArrayList<>();

	
	private List<ItemPool> allItemPools = new ArrayList<>();
	
	
	// SHOPS
	public BasicShopItemPool basicShopItemPool = new BasicShopItemPool();
	
	// LOOTABLES
	public OldBonesItemPool oldBones = new OldBonesItemPool();
	public AdventurersSatchelItemPool satchel = new AdventurersSatchelItemPool();
	public PersonalBelongingsItemPool personalBelongings = new PersonalBelongingsItemPool();
	public OrbBagItemPool orbBag = new OrbBagItemPool();
	
	// ENEMIES
	public SpiderItemPool spider = new SpiderItemPool();
	public WebSpiderItemPool webSpider = new WebSpiderItemPool();
	public VenomSpiderItemPool venomSpider = new VenomSpiderItemPool();
	public ScorpionItemPool scorpion = new ScorpionItemPool();
	public StingerItemPool stinger = new StingerItemPool();
	public PangolinItemPool pangolin = new PangolinItemPool();
	public PangolinMotherItemPool pangolinMatriarch = new PangolinMotherItemPool();
	
	public TribesmenSpearItemPool tribesmanSpear = new TribesmenSpearItemPool();
	public TribesmenShieldItemPool tribesManShield = new TribesmenShieldItemPool();
	public TribesmenScoutItemPool tribesmanScout = new TribesmenScoutItemPool();
	
	
	public ItemPoolSingleton() {
		
		// Shops
		basicShopItemPool.id = 1;
		allItemPools.add(basicShopItemPool);
		
		// Lootables
		oldBones.id = 2;
		allItemPools.add(oldBones);
		satchel.id = 3;
		allItemPools.add(satchel);
		personalBelongings.id = 4;
		allItemPools.add(personalBelongings);
		orbBag.id = 5;
		allItemPools.add(orbBag);
		
		// Enemies
		spider.id = 6;
		allItemPools.add(spider);
		webSpider.id = 7;
		allItemPools.add(webSpider);
		venomSpider.id = 8;
		allItemPools.add(venomSpider);
		scorpion.id = 9;
		allItemPools.add(scorpion);
		stinger.id = 10;
		allItemPools.add(stinger);
		pangolin.id = 11;
		allItemPools.add(pangolin);
		pangolinMatriarch.id = 12;
		allItemPools.add(pangolinMatriarch);
		tribesmanSpear.id = 13;
		allItemPools.add(tribesmanSpear);
		tribesManShield.id = 14;
		allItemPools.add(tribesManShield);
		tribesmanScout.id = 15;
		allItemPools.add(tribesmanScout);
		
	}
	
	
	public ItemPool getPoolById(int id) {
		for (ItemPool pool : allItemPools) {
			if (pool.id == id) {
				return pool;
			}
		}
		return null;
	}
	
	public void removeItemFromPools(ItemEnum item) {
		this.removedItems.add(item);
		for (ItemPool pool : allItemPools) {
			pool.removeItemFromPool(item);
		}
	}
	
	
	public List<ItemEnum> getRemovedItems() {
		return removedItems;
	}
	
	public void restoreRemoveditems(List<ItemEnum> removedItemsToRestore) {
		removedItems.clear();
		removedItems.addAll(removedItemsToRestore);
		for (ItemEnum removedItem : removedItems) {
			for (ItemPool pool : allItemPools) {
				pool.removeItemFromPool(removedItem);
			}
		}
	}
}
