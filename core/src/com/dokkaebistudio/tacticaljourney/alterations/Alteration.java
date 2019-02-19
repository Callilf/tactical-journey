/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations;

import com.badlogic.ashley.core.Entity;

/**
 * A state alteration which can be positive or negative.
 * @author Callil
 *
 */
public abstract class Alteration {

	/** Called when this alteration is received by an entity. */
	public abstract void onReceive(Entity entity);
	
	/** Called when this alteration is removed from an entity. */
	public abstract void onRemove(Entity entity);
}
