/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.curses;

import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;

/**
 * Curse of slowness. Reduce the entity's movement speed by 1.
 * @author Callil
 *
 */
public class CurseSlowness extends Curse {

	@Override
	public String title() {
		return "Curse of slowness";
	}
	
	@Override
	public String description() {
		return "Reduce movement by 1";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.curse_slowness;
	}
}
