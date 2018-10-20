package com.dokkaebistudio.tacticaljourney;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.dokkaebistudio.tacticaljourney.components.WheelComponent;
import com.dokkaebistudio.tacticaljourney.components.WheelComponent.Sector;

public class AttackWheel {

	/** Whether the wheel is visible or not. */
	private boolean displayed;
	
	/** The list of sectors that compose the wheel. */
	private List<WheelComponent.Sector> sectors;
	
//	/** The 360 arcs that compose the wheel. */
//	private List<Sprite> arcs;
	
	/** The arrow that spins. */
	private Sprite arrow;
	
	/** The sector pointed by the arrow. */
	private Sector pointedSector;
	
	
	public AttackWheel() {
		this.displayed = false;
		this.sectors = new LinkedList<WheelComponent.Sector>();
		this.arrow = new Sprite(Assets.getTexture(Assets.wheel_arrow));
		
//		this.arcs = new ArrayList<>();
//		for (int i=0 ; i<360 ; i++) {
//			Sprite arcSprite = new Sprite(Assets.getTexture(Assets.wheel_arc));
//			arcSprite.setPosition(GameScreen.WHEEL_X - arcSprite.getWidth()/2, GameScreen.WHEEL_Y - arcSprite.getHeight()/2);
//			arcSprite.setRotation(i);
//			this.arcs.add(arcSprite);
//		}
	}
	
	/**
	 * Retrieve the sector targeted by the needle.
	 * @return the sector targeted by the needle.
	 */
	public void setPointedSector() {
		float rotation = 0;
		if (this.arrow.getRotation() < 0) {
			rotation = 360 + (this.arrow.getRotation() % 360);
		} else {
			rotation = this.arrow.getRotation() % 360;
		}
		
    	Sector pointedSector = this.sectors.get(0);
    	int totalRange = 0;
    	for (Sector s : this.sectors) {
    		if (totalRange + s.range > rotation) {
    			pointedSector = s;
    			break;
    		}
    		totalRange += s.range;
    	}
    	this.pointedSector = pointedSector;
	}


	
	
	
	public boolean isDisplayed() {
		return displayed;
	}


	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}


	public List<WheelComponent.Sector> getSectors() {
		return sectors;
	}


	public void setSectors(List<WheelComponent.Sector> sectors) {
		this.sectors = sectors;
	}

	public Sprite getArrow() {
		return arrow;
	}

	public void setArrow(Sprite arrow) {
		this.arrow = arrow;
	}

	public Sector getPointedSector() {
		return pointedSector;
	}

//	public List<Sprite> getArcs() {
//		return arcs;
//	}
//
//	public void setArcs(List<Sprite> arcs) {
//		this.arcs = arcs;
//	}
	
	
}