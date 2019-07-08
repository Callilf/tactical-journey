package com.dokkaebistudio.tacticaljourney.dialog.pnjs.shopkeeper;

import com.dokkaebistudio.tacticaljourney.ashley.PublicEntity;
import com.dokkaebistudio.tacticaljourney.components.neutrals.ShopKeeperComponent;
import com.dokkaebistudio.tacticaljourney.dialog.DialogCondition;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class ShopkeeperSoldPredicate implements DialogCondition {

	@Override
	public boolean test(PublicEntity e) {
		ShopKeeperComponent shopKeeperComponent = Mappers.shopKeeperComponent.get(e);
		return shopKeeperComponent.hasSoldItems();
	}

}
