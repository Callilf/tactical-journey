package com.dokkaebistudio.tacticaljourney.dialog.pnjs.soulbender;

import com.dokkaebistudio.tacticaljourney.ashley.PublicEntity;
import com.dokkaebistudio.tacticaljourney.components.neutrals.SoulbenderComponent;
import com.dokkaebistudio.tacticaljourney.dialog.DialogCondition;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class SoulbenderReceivedCatalystPredicate implements DialogCondition {

	@Override
	public boolean test(PublicEntity e) {
		SoulbenderComponent soulbenderComponent = Mappers.soulbenderComponent.get(e);
		return soulbenderComponent != null && soulbenderComponent.isReceivedCatalyst();
	}

}
