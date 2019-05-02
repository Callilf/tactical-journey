/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.enums;

import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;

/**
 * @author Callil
 *
 */
public enum LootableEnum {
	
	BONES(Descriptions.LOOTABLE_OLD_BONES_TITLE, 2, Assets.lootable_bones.getName(), Assets.lootable_bones_opened.getName()) {
		@Override
		public String getDescription() {
			return Descriptions.LOOTABLE_OLD_BONES_DESCRIPTION;
		}
	},
	
	SATCHEL(Descriptions.LOOTABLE_SATCHEL_TITLE, 3, Assets.lootable_satchel.getName(), Assets.lootable_satchel_opened.getName()) {
		@Override
		public String getDescription() {
			return Descriptions.LOOTABLE_SATCHEL_DESCRIPTION;
		}
	},
	
	PERSONAL_BELONGINGS(Descriptions.LOOTABLE_PERSONAL_BELONGINGS_TITLE, 3, Assets.lootable_belongings.getName(), Assets.lootable_belongings_opened.getName()) {
		@Override
		public String getDescription() {
			return Descriptions.LOOTABLE_PERSONAL_BELONGINGS_DESCRIPTION;
		}
	},
	
	ORB_BAG(Descriptions.LOOTABLE_ORB_BAG_TITLE, 3, Assets.lootable_orb_bag.getName(), Assets.lootable_orb_bag_opened.getName()) {
		@Override
		public String getDescription() {
			return Descriptions.LOOTABLE_ORB_BAG_DESCRIPTION;
		}
	},
	
	SPELL_BOOK(Descriptions.LOOTABLE_SPELL_BOOK_TITLE, 2, Assets.lootable_spell_book.getName(), Assets.lootable_spell_book_opened.getName()) {
		@Override
		public String getDescription() {
			return Descriptions.LOOTABLE_SPELL_BOOK_DESCRIPTION;
		}
	};
	
	/** The name of the lootable that will be displayed in game. */
	private String label;
	
	/** The number of turns it takes to open. */
	private int nbTurnsToOpen;
	
	private String closedTextureName;
	private String openedTextureName;
	
	
	private LootableEnum(String label, int nbTurnsToOpen, String closedTexture, String openedTexture) {
		this.setLabel(label);
		this.setNbTurnsToOpen(nbTurnsToOpen);
		this.setClosedTextureName(closedTexture);
		this.setOpenedTextureName(openedTexture);
	}

	/**
	 * Get the description of the lootable.
	 * @return the description.
	 */
	public abstract String getDescription();
	
	
	// Getters and Setters

	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public int getNbTurnsToOpen() {
		return nbTurnsToOpen;
	}


	public void setNbTurnsToOpen(int nbTurnsToOpen) {
		this.nbTurnsToOpen = nbTurnsToOpen;
	}

	public RegionDescriptor getClosedTexture() {
		return Assets.findSprite(closedTextureName);
	}

	public void setClosedTextureName(String closedTexture) {
		this.closedTextureName = closedTexture;
	}

	public RegionDescriptor getOpenedTexture() {
		return Assets.findSprite(openedTextureName);
	}

	public void setOpenedTextureName(String openedTexture) {
		this.openedTextureName = openedTexture;
	}
	
}
