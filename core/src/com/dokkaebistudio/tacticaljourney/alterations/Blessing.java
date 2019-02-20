/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations;

import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingCelerity;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingStrength;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingVigor;

/**
 * A blessing, which is a positive alteration.
 * @author Callil
 *
 */
public abstract class Blessing extends Alteration {

	public enum BlessingsEnum {
		VIGOR,
		STRENGTH,
		CELERITY;
	}
	
	
	/**
	 * Blessing factory.
	 * @param type the type of blessing to create
	 * @return the created blessing
	 */
	public static Blessing createBlessing(BlessingsEnum type) {
		Blessing result = null;
		
		switch (type) {
		case VIGOR:
			result = new BlessingVigor();
			break;
		case STRENGTH:
			result = new BlessingStrength();
			break;
		case CELERITY:
			result = new BlessingCelerity();
			break;
			
			
			default:
		}
		
		return result;
	}
}
