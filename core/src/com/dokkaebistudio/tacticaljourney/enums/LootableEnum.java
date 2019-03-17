/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.enums;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;

/**
 * @author Callil
 *
 */
public enum LootableEnum {
	
	BONES(Descriptions.LOOTABLE_OLD_BONES_TITLE, 2, Assets.lootable_bones, Assets.lootable_bones_opened) {
		@Override
		public String getDescription() {
			return Descriptions.LOOTABLE_OLD_BONES_DESCRIPTION;
		}
	},
	
	SATCHEL(Descriptions.LOOTABLE_SATCHEL_TITLE, 3, Assets.lootable_satchel, Assets.lootable_satchel_opened) {
		@Override
		public String getDescription() {
			return Descriptions.LOOTABLE_SATCHEL_DESCRIPTION;
		}
	},
	
	PERSONAL_BELONGINGS(Descriptions.LOOTABLE_PERSONAL_BELONGINGS_TITLE, 3, Assets.lootable_belongings, Assets.lootable_belongings_opened) {
		@Override
		public String getDescription() {
			return Descriptions.LOOTABLE_PERSONAL_BELONGINGS_DESCRIPTION;
		}
	},
	
	ORB_BAG(Descriptions.LOOTABLE_ORB_BAG_TITLE, 3, Assets.lootable_orb_bag, Assets.lootable_orb_bag_opened) {
		@Override
		public String getDescription() {
			return Descriptions.LOOTABLE_ORB_BAG_DESCRIPTION;
		}
	};
	
	/** The name of the lootable that will be displayed in game. */
	private String label;
	
	/** The number of turns it takes to open. */
	private int nbTurnsToOpen;
	
	private AtlasRegion closedTexture;
	private AtlasRegion openedTexture;
	
	
	private LootableEnum(String label, int nbTurnsToOpen, AtlasRegion closedTexture, AtlasRegion openedTexture) {
		this.setLabel(label);
		this.setNbTurnsToOpen(nbTurnsToOpen);
		this.setClosedTexture(closedTexture);
		this.setOpenedTexture(openedTexture);
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

	public AtlasRegion getClosedTexture() {
		return closedTexture;
	}

	public void setClosedTexture(AtlasRegion closedTexture) {
		this.closedTexture = closedTexture;
	}

	public AtlasRegion getOpenedTexture() {
		return openedTexture;
	}

	public void setOpenedTexture(AtlasRegion openedTexture) {
		this.openedTexture = openedTexture;
	}
	
}
