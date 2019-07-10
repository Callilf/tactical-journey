package com.dokkaebistudio.tacticaljourney.ces.entity;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.ces.components.interfaces.MarkerInterface;

public class PublicEntity extends Entity {

	public boolean scheduledForRemoval;
	public long id;

	public void hideMarkers() {
		for (Component c : this.getComponents()) {
			if (c instanceof MarkerInterface) {
				((MarkerInterface) c).hideMarker();
			}
		}
	}

	public void showMarkers() {
		for (Component c : this.getComponents()) {
			if (c instanceof MarkerInterface) {
				((MarkerInterface) c).showMarker(this);
			}
		}
	}
}
