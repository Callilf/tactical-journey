package com.dokkaebistudio.tacticaljourney.util;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.neutrals.ShopKeeperComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;

public class ShopUtil {

	
	public static int getNumberOfItemsInShop(Entity shopkeeper, Room room) {
		WalletComponent walletComponent = Mappers.walletComponent.get(GameScreen.player);
		AlterationReceiverComponent alterationReceiverCompo = Mappers.alterationReceiverComponent.get(GameScreen.player);
		ShopKeeperComponent shopKeeperComponent = Mappers.shopKeeperComponent.get(shopkeeper);
		int result = shopKeeperComponent.getNumberOfItems();
		result += walletComponent.getExtraItemsInShops();
		result += alterationReceiverCompo.onShopNumberOfItems(GameScreen.player, shopkeeper, room);
		return result;
	}
	
	
	
	public static List<Entity> findShopKeeper(Room room, List<Entity> listToPopulate) {
		for (Entity e : room.getNeutrals()) {
			if (Mappers.shopKeeperComponent.has(e)) {
				listToPopulate.add(e);
			}
		}
		return listToPopulate;
	}
	
}
