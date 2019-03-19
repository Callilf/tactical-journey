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
		
		allItemPools.add(basicShopItemPool);
		
		allItemPools.add(oldBones);
		allItemPools.add(satchel);
		allItemPools.add(personalBelongings);
		allItemPools.add(orbBag);
		
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
		
	}
	
	
	public void removeItemFromPools(ItemEnum item) {
		for (ItemPool pool : allItemPools) {
			pool.removeItemFromPool(item);
		}
	}
}