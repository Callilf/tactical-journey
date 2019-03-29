/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations;

import com.dokkaebistudio.tacticaljourney.alterations.blessings.basics.BlessingAccuracy;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.basics.BlessingCelerity;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.basics.BlessingStrength;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.basics.BlessingVigor;

/**
 * A blessing, which is a positive alteration.
 * @author Callil
 *
 */
public abstract class Blessing extends Alteration {

	public enum BlessingsEnum {
		VIGOR,
		STRENGTH,
		CELERITY,
		ACCURACY;
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
		case ACCURACY:
			result = new BlessingAccuracy();
			break;
			
			default:
		}
		
		return result;
	}
}
