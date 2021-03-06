package com.dokkaebistudio.tacticaljourney.items.pools;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.OrangutanItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.PangolinItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.PangolinMotherItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.ScorpionItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.ShinobiItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.SmallOrangutanItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.SpiderItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.StingerItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.VenomSpiderItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.WebSpiderItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.destructibles.AmmoCrateItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.destructibles.BushItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.destructibles.GoldenVaseItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.destructibles.StatueItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.destructibles.VaseItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.destructibles.VineBushItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.destructibles.WallItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.tribesmen.TribesmenScoutItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.tribesmen.TribesmenShieldItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.tribesmen.TribesmenSpearItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.lootables.AdventurersSatchelItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.lootables.OldBonesItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.lootables.OrbBagItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.lootables.PersonalBelongingsItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.lootables.SpellBookItemPool;
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
	public SpellBookItemPool spellBook = new SpellBookItemPool();

	// DESTRUCTIBLES
	public AmmoCrateItemPool ammoCrate = new AmmoCrateItemPool();
	public VaseItemPool vase = new VaseItemPool();
	public GoldenVaseItemPool goldenVase = new GoldenVaseItemPool();
	public WallItemPool wall = new WallItemPool();
	public StatueItemPool statue = new StatueItemPool();
	public BushItemPool bush = new BushItemPool();
	public VineBushItemPool vineBush = new VineBushItemPool();
	
	// ENEMIES
	public SpiderItemPool spider = new SpiderItemPool();
	public WebSpiderItemPool webSpider = new WebSpiderItemPool();
	public VenomSpiderItemPool venomSpider = new VenomSpiderItemPool();
	public ScorpionItemPool scorpion = new ScorpionItemPool();
	public StingerItemPool stinger = new StingerItemPool();
	public PangolinItemPool pangolin = new PangolinItemPool();
	public PangolinMotherItemPool pangolinMatriarch = new PangolinMotherItemPool();
	public ShinobiItemPool shinobi = new ShinobiItemPool();
	public OrangutanItemPool orangutan = new OrangutanItemPool();
	public SmallOrangutanItemPool smallOrangutan = new SmallOrangutanItemPool();
	
	public TribesmenSpearItemPool tribesmanSpear = new TribesmenSpearItemPool();
	public TribesmenShieldItemPool tribesManShield = new TribesmenShieldItemPool();
	public TribesmenScoutItemPool tribesmanScout = new TribesmenScoutItemPool();
	
	
	public ItemPoolSingleton() {
		
		// Shops
		allItemPools.add(basicShopItemPool);
		
		// Lootables
		allItemPools.add(oldBones);
		allItemPools.add(satchel);
		allItemPools.add(personalBelongings);
		allItemPools.add(orbBag);
		allItemPools.add(spellBook);
		
		//Destructibles
		allItemPools.add(ammoCrate);
		allItemPools.add(vase);
		allItemPools.add(goldenVase);
		allItemPools.add(wall);
		allItemPools.add(statue);
		allItemPools.add(bush);
		allItemPools.add(vineBush);
		
		// Enemies
		allItemPools.add(spider);
		allItemPools.add(webSpider);
		allItemPools.add(venomSpider);
		allItemPools.add(scorpion);
		allItemPools.add(stinger);
		allItemPools.add(pangolin);
		allItemPools.add(pangolinMatriarch);
		
		allItemPools.add(tribesmanSpear);
		allItemPools.add(tribesManShield);
		allItemPools.add(tribesmanScout);
		
		allItemPools.add(shinobi);
		allItemPools.add(orangutan);
		allItemPools.add(smallOrangutan);

		
	}
	
	
	public ItemPool getPoolById(String id) {
		for (ItemPool pool : allItemPools) {
			if (pool.id.equals(id)) {
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
	
	
	public List<ItemPool> getAllItemPools() {
		return allItemPools;
	}
}
