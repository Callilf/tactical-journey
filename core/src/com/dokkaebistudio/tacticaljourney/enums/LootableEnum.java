/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.enums;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.Assets;

/**
 * @author Callil
 *
 */
public enum LootableEnum {
	
	BONES("Old bones", 2, Assets.remains_bones, Assets.remains_bones_opened) {
		@Override
		public String getDescription() {
			return "The remaining bones of a humanoid being. This could be you in like a hundred years.";
		}
	},
	
	SATCHEL("Adventurer's lost satchel", 3, Assets.remains_satchel, Assets.remains_satchel_opened) {
		@Override
		public String getDescription() {
			return "A satchel in an advanced state of decay. It looks like it has been lost here for quite a long time, but it could still contain something useful.";
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
