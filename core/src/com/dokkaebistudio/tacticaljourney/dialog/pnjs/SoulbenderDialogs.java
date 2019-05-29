package com.dokkaebistudio.tacticaljourney.dialog.pnjs;

import java.io.Serializable;
import java.util.function.Predicate;

import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ashley.PublicEntity;
import com.dokkaebistudio.tacticaljourney.components.neutrals.SoulbenderComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.dialog.AbstractDialogs;
import com.dokkaebistudio.tacticaljourney.dialog.Dialog;
import com.dokkaebistudio.tacticaljourney.dialog.DialogBuilder;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemDivineCatalyst;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class SoulbenderDialogs extends AbstractDialogs {
	
	public final static String NOT_ENOUGH_MONEY_TAG = "NOT_ENOUGH_MONEY";

	
	@Override
	protected void setSpeaker(Dialog d) {
		d.setSpeaker(Descriptions.SOULBENDER_TITLE);
	}
	
	@SuppressWarnings("unchecked")
	public SoulbenderDialogs() {

		// Basic dialog
		
		this.addDialog(new DialogBuilder()
				.addText("Hello there ! I'm a soul bender.")
				.addText("I can infuse items' auras into your soul.")
				.addText("This allows you keeping the blessings provided by items permanently, and free an inventory slot.")
				.addText("However it's not free.")
				.addText("Come closer if you want to infuse an item!")
				.setRepeat(false)
				.build());
		this.addDialog(new DialogBuilder()
				.addText("Come closer if you want to infuse an item!")
				.setRepeat(true)
				.build());
		
		
		// After infusion
		
		Predicate<PublicEntity> infusionPredicate = (Predicate<PublicEntity> & Serializable) e -> {
			SoulbenderComponent soulbenderComponent = Mappers.soulbenderComponent.get(e);
			InventoryComponent playerInventoryCompo = Mappers.inventoryComponent.get(GameScreen.player);
			return soulbenderComponent != null && soulbenderComponent.hasInfused() 
					&& playerInventoryCompo != null && !playerInventoryCompo.contains(ItemDivineCatalyst.class);
		};
		
		this.addDialog(new DialogBuilder()
				.addText("I'm out of energy, I need some rest.")
				.addText("I'm getting too old for this.")
				.addText("If you get me something to restore my energy I could probably infuse another item for you.")
				.setCondition(infusionPredicate)
				.setRepeat(false)
				.build());
		this.addDialog(new DialogBuilder()
				.addText("If you get me something to restore my energy I could probably infuse another item for you.")
				.setCondition(infusionPredicate)
				.setRepeat(true)
				.build());
		
		// With catalyst
		
		Predicate<PublicEntity> catalystPredicate = (Predicate<PublicEntity> & Serializable) e -> {
			SoulbenderComponent soulbenderComponent = Mappers.soulbenderComponent.get(e);
			InventoryComponent playerInventoryCompo = Mappers.inventoryComponent.get(GameScreen.player);
			return soulbenderComponent != null && soulbenderComponent.hasInfused() 
					&& playerInventoryCompo != null && playerInventoryCompo.contains(ItemDivineCatalyst.class);
		};
		
		this.addDialog(new DialogBuilder()
				.addText("You are carrying a very powerful artifact, I can feel it. Come closer and let me have a look.")
				.setCondition(catalystPredicate)
				.setRepeat(true)
				.build());
		
		// After giving catalyst
		
		Predicate<PublicEntity> receivedCatalystPredicate = (Predicate<PublicEntity> & Serializable) e -> {
			SoulbenderComponent soulbenderComponent = Mappers.soulbenderComponent.get(e);
			return soulbenderComponent != null && soulbenderComponent.isReceivedCatalyst();
		};
		
		this.addDialog(new DialogBuilder()
				.addText("Thank you! I feel strong enough to infuse another item now.")
				.setCondition(receivedCatalystPredicate)
				.setRepeat(false)
				.build());
		
		this.addDialog(new DialogBuilder()
				.addText("Do you want me to infuse another item for you?")
				.setCondition(receivedCatalystPredicate)
				.setRepeat(true)
				.build());
		
		// Not enough money
		
		this.addDialog(NOT_ENOUGH_MONEY_TAG, new DialogBuilder()
				.addText("Come back when you've got enough gold coins.")
				.setRepeat(true)
				.build());
	}

}
