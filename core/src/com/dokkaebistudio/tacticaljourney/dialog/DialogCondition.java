package com.dokkaebistudio.tacticaljourney.dialog;

import com.dokkaebistudio.tacticaljourney.ashley.PublicEntity;

public interface DialogCondition {

	/**
	 * @param e the entity speaking (and not the player !!!).
	 * @return whether the condition has been validated
	 */
	public boolean test(PublicEntity e);
}
