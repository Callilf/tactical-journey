package com.dokkaebistudio.tacticaljourney.dialog;

import com.dokkaebistudio.tacticaljourney.ashley.PublicEntity;

public interface DialogCondition {

	public boolean test(PublicEntity e);
}
