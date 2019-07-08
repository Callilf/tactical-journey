package com.dokkaebistudio.tacticaljourney.dialog.pnjs;

import com.dokkaebistudio.tacticaljourney.dialog.AbstractDialogs;
import com.dokkaebistudio.tacticaljourney.dialog.DialogBuilder;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.shopkeeper.ShopkeeperRestockDialogEffect;
import com.dokkaebistudio.tacticaljourney.dialog.pnjs.shopkeeper.ShopkeeperSoldPredicate;

public class ShopkeeperDialogs extends AbstractDialogs {
	
	public final static String NOT_ENOUGH_MONEY_TAG = "NOT_ENOUGH_MONEY";
	public final static String SOLD_TAG = "SOLD";
	
	
	public ShopkeeperDialogs() {

		// Basic dialog
		
		this.addDialog(new DialogBuilder()
				.addText("Hey! I'm the shop keeper.")
				.addText("It's good to see a new face around here!")
				.addText("I'm sure my wares can be of interest to your journey.")
				.setRepeat(false)
				.build());
		this.addDialog(new DialogBuilder()
				.addText("I can restock my shop if you want to, given that you can afford it.")
				.setRepeat(true)
				.build());
		this.addDialog(new DialogBuilder()
				.addText("Did you know that you can break the crates with a simple sword slash?")
				.setRepeat(false)
				.build());
		
		
		
		// Restock
		
		ShopkeeperSoldPredicate shopkeeperSoldPredicate = new ShopkeeperSoldPredicate();
		
		this.addDialog(new DialogBuilder()
				.addText("I have many things of interest in my stuff, I can restock the shop if you want to.")
				.setRepeat(true)
				.setCondition(shopkeeperSoldPredicate)
				.setEffect(new ShopkeeperRestockDialogEffect())
				.setActivateMarker(true)
				.build());
		
		
		// After selling
		
		this.addDialog(SOLD_TAG,new DialogBuilder()
				.addText("Good choice!")
				.setRepeat(true)
				.build());
		
		// Not enough money
		
		this.addDialog(NOT_ENOUGH_MONEY_TAG, new DialogBuilder()
				.addText("Come back when you've got enough gold coins.")
				.setRepeat(true)
				.build());
	}

}
