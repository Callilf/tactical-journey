/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations;

import com.dokkaebistudio.tacticaljourney.alterations.curses.basics.CurseBadLuck;
import com.dokkaebistudio.tacticaljourney.alterations.curses.basics.CurseFrailty;
import com.dokkaebistudio.tacticaljourney.alterations.curses.basics.CurseSlowness;
import com.dokkaebistudio.tacticaljourney.alterations.curses.basics.CurseTremors;
import com.dokkaebistudio.tacticaljourney.alterations.curses.basics.CurseWeakness;

/**
 * A curse, which is a negative alteration.
 * @author Callil
 *
 */
public abstract class Curse extends Alteration {

	public enum CursesEnum {
		FRAILTY,
		WEAKNESS,
		SLOWNESS,
		TREMORS,
		BAD_LUCK,
	}
	
	/**
	 * Curse factory.
	 * @param type the type of curse
	 * @return the created curse
	 */
	public static Curse createCurse(CursesEnum type) {
		Curse result = null;
		
		switch (type) {
		case FRAILTY:
			result = new CurseFrailty();
			break;
		case WEAKNESS:
			result = new CurseWeakness();
			break;
		case SLOWNESS:
			result = new CurseSlowness();
			break;
		case TREMORS:
			result = new CurseTremors();
			break;
		case BAD_LUCK:
			result = new CurseBadLuck();
			break;
			
			default:
		}
		
		return result;
	}
}
