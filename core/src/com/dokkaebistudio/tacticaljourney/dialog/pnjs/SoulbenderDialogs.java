package com.dokkaebistudio.tacticaljourney.dialog.pnjs;

import com.dokkaebistudio.tacticaljourney.dialog.AbstractDialogs;
import com.dokkaebistudio.tacticaljourney.dialog.DialogBuilder;
import com.dokkaebistudio.tacticaljourney.dialog.DialogCondition;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.soulbender.SoulbenderCatalystPredicate;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.soulbender.SoulbenderGiveDivineCatalystDialogEffect;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.soulbender.SoulbenderInfuseDialogEffect;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.soulbender.SoulbenderInfusionPredicate;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.soulbender.SoulbenderReceivedCatalystPredicate;

public class SoulbenderDialogs extends AbstractDialogs {
	
	public final static String NOT_ENOUGH_MONEY_TAG = "NOT_ENOUGH_MONEY";

	
	public SoulbenderDialogs() {
		
		SoulbenderInfuseDialogEffect infusionEffect = new SoulbenderInfuseDialogEffect();
		SoulbenderGiveDivineCatalystDialogEffect giveCatalystEffect = new SoulbenderGiveDivineCatalystDialogEffect();

		// Basic dialog
		
		this.addDialog(new DialogBuilder()
				.addText("Hello there ! I'm a soul bender.")
				.addText("Given you have money, I can infuse items' auras into your soul. This allows you keeping the blessings provided by items permanently, and free an inventory slot.")
				.setRepeat(true)
				.setEffect(infusionEffect)
				.build());
		
		
		// After infusion
		
		DialogCondition infusionPredicate = new SoulbenderInfusionPredicate();
		
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

		DialogCondition catalystPredicate = new SoulbenderCatalystPredicate();
		
		this.addDialog(new DialogBuilder()
				.addText("You are carrying a very powerful artifact, I can feel it. If you give it to me, it will restore all my energy and I'll be able to infuse another item for you. "
						+ "For free of course!")
				.setCondition(catalystPredicate)
				.setRepeat(true)
				.setEffect(giveCatalystEffect)
				.setActivateMarker(true)
				.build());
		
		// After giving catalyst

		DialogCondition receivedCatalystPredicate = new SoulbenderReceivedCatalystPredicate();
		
		this.addDialog(new DialogBuilder()
				.addText("Thank you! I feel strong enough to infuse another item now.")
				.addText("Do you want me to infuse another item for you?")
				.setCondition(receivedCatalystPredicate)
				.setRepeat(false)
				.setEffect(infusionEffect)
				.setActivateMarker(true)
				.build());
		
		this.addDialog(new DialogBuilder()
				.addText("Do you want me to infuse another item for you?")
				.setCondition(receivedCatalystPredicate)
				.setRepeat(true)
				.setEffect(infusionEffect)
				.build());
		
		// Not enough money
		
		this.addDialog(NOT_ENOUGH_MONEY_TAG, new DialogBuilder()
				.addText("Come back when you've got enough gold coins.")
				.setRepeat(true)
				.build());
	}

}
