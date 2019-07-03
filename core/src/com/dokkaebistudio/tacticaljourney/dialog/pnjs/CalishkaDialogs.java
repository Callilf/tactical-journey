package com.dokkaebistudio.tacticaljourney.dialog.pnjs;

import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.dialog.AbstractDialogs;
import com.dokkaebistudio.tacticaljourney.dialog.Dialog;
import com.dokkaebistudio.tacticaljourney.dialog.DialogBuilder;

public class CalishkaDialogs extends AbstractDialogs {
	
	@Override
	protected void setSpeaker(Dialog d) {
		d.setSpeaker(Descriptions.CALISHKA_TITLE);
	}
	
	@SuppressWarnings("unchecked")
	public CalishkaDialogs() {

		// Basic dialog
		
		this.addDialog(new DialogBuilder()
				.addText("Welcome in my dungeon, adventurer. I am Calishka, the keeper of this place.")
				.addText("It is the universal cure you seek I suppose, you're not the first.")
				.addText("Well I've been bonded to this place centuries ago, and were you to succeed in finding the cure, I would be freed. Thus I shall assist any adventurer like you that enters these walls.")
				.setRepeat(false)
				.build());
		this.addDialog(new DialogBuilder()
				.addText("You should start by practicing combat and gathering stuff. This floor is perfect for this, the next ones won't be as nice.")
				.setRepeat(false)
				.build());
		this.addDialog(new DialogBuilder()
				.addText("Go on, little one.")
				.setRepeat(true)
				.build());
		
	}

}
