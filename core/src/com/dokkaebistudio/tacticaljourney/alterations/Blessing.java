/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations;

import com.dokkaebistudio.tacticaljourney.alterations.blessings.basics.BlessingAccuracy;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.basics.BlessingBombmasterMight;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.basics.BlessingBowmasterAccuracy;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.basics.BlessingBowmasterMight;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.basics.BlessingBowmasterSteadiness;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.basics.BlessingCelerity;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.basics.BlessingRockThrower;
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
		ACCURACY,
		BOWMASTER_MIGHT,
		BOWMASTER_ACCURACY,
		BOWMASTER_STEADYNESS,
		BOMBMASTER_MIGHT,
		PITCHER,
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
		case BOWMASTER_MIGHT:
			result = new BlessingBowmasterMight();
			break;
		case BOWMASTER_ACCURACY:
			result = new BlessingBowmasterAccuracy();
			break;
		case BOWMASTER_STEADYNESS:
			result = new BlessingBowmasterSteadiness();
		case BOMBMASTER_MIGHT:
			result = new BlessingBombmasterMight();
			break;
		case PITCHER:
			result = new BlessingRockThrower();
			break;
			
			default:
		}
		
		return result;
	}
}
