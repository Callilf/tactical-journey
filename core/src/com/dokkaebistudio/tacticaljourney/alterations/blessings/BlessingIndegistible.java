/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;

/**
 * Blessing of the indegistible. Allow free movements on spider web + improved the efficiency of web sacks.
 * @author Callil
 *
 */
public class BlessingIndegistible extends Blessing {
		
	@Override
	public String title() {
		return "Blessing of the indegistible";
	}
	
	@Override
	public String description() {
		return "Walking on spider web costs 0 movement and does not alert spiders. Also web related items are stonger.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_indegistible;
	}

}
