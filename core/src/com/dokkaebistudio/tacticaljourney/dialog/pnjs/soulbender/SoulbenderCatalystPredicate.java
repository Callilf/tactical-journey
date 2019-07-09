package com.dokkaebistudio.tacticaljourney.dialog.pnjs.soulbender;

import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.SoulbenderComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.ces.entity.PublicEntity;
import com.dokkaebistudio.tacticaljourney.dialog.DialogCondition;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemDivineCatalyst;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class SoulbenderCatalystPredicate implements DialogCondition {

	@Override
	public boolean test(PublicEntity e) {
		SoulbenderComponent soulbenderComponent = Mappers.soulbenderComponent.get(e);
		InventoryComponent playerInventoryCompo = Mappers.inventoryComponent.get(GameScreen.player);
		return soulbenderComponent != null && soulbenderComponent.hasInfused() 
				&& playerInventoryCompo != null && playerInventoryCompo.contains(ItemDivineCatalyst.class);
	}

}
