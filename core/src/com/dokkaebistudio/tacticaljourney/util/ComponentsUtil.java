/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.util;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;

/**
 * Util class with methods related to components.
 * @author Callil
 *
 */
public final class ComponentsUtil {

	public static Entity getMainParent(Entity e) {
		Entity result = e;
		ParentEntityComponent parentEntityComponent = Mappers.parentEntityComponent.get(e);
		if (parentEntityComponent != null && parentEntityComponent.getParent() != null) {
			result = getMainParent(parentEntityComponent.getParent());
		}
		return result;
	}
	
}
