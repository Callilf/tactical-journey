package com.dokkaebistudio.tacticaljourney.wheel;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WheelComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class AttackWheel {

	/** Whether the wheel is visible or not. */
	private boolean displayed;
	
	/** The offset of the wheel. Changes anytime the wheel is displayed. */
	private int rotationOffset;
	
	/** The list of sectors that compose the wheel. */
	private List<Sector> sectors = new ArrayList<>();
	
//	/** The 360 arcs that compose the wheel. */
//	private List<Sprite> arcs;
	
	/** The arrow that spins. */
	private Sprite arrow;
	
	/** The sector pointed by the arrow. */
	private Sector pointedSector;
	
	/** The wheel component used to call the wheel. */
	private WheelComponent wheelComponent;
	/** The attack component used to call the wheel. */
	private AttackComponent attackComponent;
	
	public AttackWheel() {
		this.displayed = false;
		
//		this.sectors = new LinkedList<WheelComponent.Sector>();
		this.arrow = new Sprite(Assets.wheel_arrow.getRegion());
		
//		this.arcs = new ArrayList<>();
//		for (int i=0 ; i<360 ; i++) {
//			Sprite arcSprite = new Sprite(Assets.getTexture(Assets.wheel_arc));
//			arcSprite.setPosition(GameScreen.WHEEL_X - arcSprite.getWidth()/2, GameScreen.WHEEL_Y - arcSprite.getHeight()/2);
//			arcSprite.setRotation(i);
//			this.arcs.add(arcSprite);
//		}
	}
	
	
	public void modifySectors(Entity player, Room room) {
		this.sectors.clear();
		this.sectors.addAll(wheelComponent.sectors);

		AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(player);
		if (alterationReceiverComponent != null) {
			alterationReceiverComponent.onModifyWheelSectors(this, player, room);
		}
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
		
		rotation -= this.rotationOffset;
		if (rotation < 0) rotation = 360 - Math.abs(rotation);
		
    	Sector pointedSector = null;
    	int totalRange = 0;
    	for (Sector s : this.sectors) {
    		if (totalRange <= rotation && totalRange + s.range > rotation) {
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
		if (displayed) {
			this.rotationOffset = RandomSingleton.getInstance().getUnseededRandom().nextInt(360);
		}
	}


	public List<Sector> getSectors() {
		return this.sectors;
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

	public AttackComponent getAttackComponent() {
		return attackComponent;
	}

	public void setAttackComponent(AttackComponent attackComponent) {
		this.attackComponent = attackComponent;
	}

	public int getRotationOffset() {
		return rotationOffset;
	}

	public void setRotationOffset(int rotationOffset) {
		this.rotationOffset = rotationOffset;
	}

	public WheelComponent getWheelComponent() {
		return wheelComponent;
	}

	public void setWheelComponent(WheelComponent wheelComponent) {
		this.wheelComponent = wheelComponent;
	}

//	public List<Sprite> getArcs() {
//		return arcs;
//	}
//
//	public void setArcs(List<Sprite> arcs) {
//		this.arcs = arcs;
//	}
	
	
}
