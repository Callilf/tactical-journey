/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.enums;

/**
 * @author Callil
 *
 */
public enum LootableEnum {
	
	BONES("Old bones", 2) {
		@Override
		public String getDescription() {
			return "The remaining bones of a humanoid being. This could be you in like a hundred years.";
		}
	},
	
	SATCHEL("Adventurer's lost satchel", 3) {
		@Override
		public String getDescription() {
			return "A satchel in an advanced state of decay. It looks like it has been lost here for quite a long time, but it could still contain something useful.";
		}
	};
	
	/** The name of the lootable that will be displayed in game. */
	private String label;
	
	/** The number of turns it takes to open. */
	private int nbTurnsToOpen;
	
	
	private LootableEnum(String label, int nbTurnsToOpen) {
		this.setLabel(label);
		this.setNbTurnsToOpen(nbTurnsToOpen);
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
	
}
