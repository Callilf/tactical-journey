package com.dokkaebistudio.tacticaljourney.components.orbs;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.interfaces.MovableInterface;
import com.dokkaebistudio.tacticaljourney.enums.DirectionEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.OrbUtil;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Marker to indicate that this entity is an orb that will have a special effect when on contact
 * with an enemy.
 * @author Callil
 *
 */
public class OrbCarrierComponent implements Component, Poolable, MovableInterface, RoomSystem {

	public Room room;

	private Entity northOrb;
	private Entity southOrb;
	private Entity westOrb;
	private Entity eastOrb;
	
	private int freeOrbSlotsNumber = 4;
	
	
	
	@Override
	public void reset() {
		clearOrb(DirectionEnum.UP);
		clearOrb(DirectionEnum.DOWN);
		clearOrb(DirectionEnum.LEFT);
		clearOrb(DirectionEnum.RIGHT);
		freeOrbSlotsNumber = 4;
	}
	
	public void clearOrb(Entity orb) {
		if (orb == northOrb) {
			clearOrb(DirectionEnum.UP);
		} else if (orb == southOrb) {
			clearOrb(DirectionEnum.DOWN);
		} else if (orb == westOrb) {
			clearOrb(DirectionEnum.LEFT);
		} else if (orb == eastOrb) {
			clearOrb(DirectionEnum.RIGHT);
		}
	}
	
	public void clearOrb(DirectionEnum direction) {
		switch (direction) {
		case UP:
			if (northOrb != null) {
				room.removeEntity(northOrb);
				northOrb = null;
			}
			break;
		case DOWN:
			if (southOrb != null) {
				room.removeEntity(southOrb);
				southOrb = null;
			}
			break;
		case LEFT:
			if (westOrb != null) {
				room.removeEntity(westOrb);
				westOrb = null;
			}
			break;
		case RIGHT:
			if (eastOrb != null) {
				room.removeEntity(eastOrb);
				eastOrb = null;
			}
			break;
			default:
		}
		
		freeOrbSlotsNumber ++;
	}

	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
		
		if (northOrb != null) {
			for (Component compo : northOrb.getComponents()) {
				if (compo instanceof RoomSystem) {
					((RoomSystem)compo).enterRoom(newRoom);
				}
			}
		}
		if (southOrb != null) {
			for (Component compo : southOrb.getComponents()) {
				if (compo instanceof RoomSystem) {
					((RoomSystem)compo).enterRoom(newRoom);
				}
			}
		}
		if (westOrb != null) {
			for (Component compo : westOrb.getComponents()) {
				if (compo instanceof RoomSystem) {
					((RoomSystem)compo).enterRoom(newRoom);
				}
			}
		}
		if (eastOrb != null) {
			for (Component compo : eastOrb.getComponents()) {
				if (compo instanceof RoomSystem) {
					((RoomSystem)compo).enterRoom(newRoom);
				}
			}
		}
	}
	
	
	
	// Aquire orb
	
	public boolean acquire(Entity carrier, Entity orb) {
		boolean result = false;
		
		if (freeOrbSlotsNumber > 0) {
			selectSlot(orb);
			placeOrb(carrier, orb);
			
			Mappers.orbComponent.get(orb).setParent(carrier);
			
			freeOrbSlotsNumber --;
			Journal.addEntry("A " + Mappers.orbComponent.get(orb).getType().getLabel() + " has been channeled");
		} else {
			Journal.addEntry("[RED]" + Mappers.orbComponent.get(orb).getType().getLabel() + " was lost");
		}
		
		return result;
	}

	
	private void selectSlot(Entity orb) {
		RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();
		int posIndex = unseededRandom.nextInt(freeOrbSlotsNumber);

		int counter = 0;
		if (northOrb == null) {
			if (posIndex == counter) {
				northOrb = orb;
				return;
			} else {
				counter ++;
			}
		}
		if (southOrb == null) {
			if (posIndex == counter) {
				southOrb = orb;
				return;
			} else {
				counter ++;
			}
		}
		if (westOrb == null) {
			if (posIndex == counter) {
				westOrb = orb;
				return;
			} else {
				counter ++;
			}
		}
		if (eastOrb == null) {
			if (posIndex == counter) {
				eastOrb = orb;
				return;
			} else {
				counter ++;
			}
		}
	}
	
	
	public void placeOrb(Entity carrier, Entity orb) {
		GridPositionComponent carrierPos = Mappers.gridPositionComponent.get(carrier);
		GridPositionComponent orbPos = Mappers.gridPositionComponent.get(orb);

		if (northOrb == orb) {
			orbPos.coord(orb, (int) carrierPos.coord().x, (int) carrierPos.coord().y + 1, room);
		} else if (southOrb == orb) {
			orbPos.coord(orb, (int) carrierPos.coord().x, (int) carrierPos.coord().y - 1, room);
		} else if (westOrb == orb) {
			orbPos.coord(orb, (int) carrierPos.coord().x - 1, (int) carrierPos.coord().y, room);
		} else {
			orbPos.coord(orb, (int) carrierPos.coord().x + 1, (int) carrierPos.coord().y, room);
		}
	}
	
	
	
	
	
	//****************
	// Movable
	
	@Override
	public void initiateMovement(Vector2 currentPos) {
		initiateMovementForOrb(northOrb);
		initiateMovementForOrb(southOrb);
		initiateMovementForOrb(westOrb);
		initiateMovementForOrb(eastOrb);
	}

	private void initiateMovementForOrb(Entity orb) {
		if (orb != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(orb);
			
			//Add the tranfo component to the entity to perform real movement on screen
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(gridPositionComponent.coord());
			gridPositionComponent.absolutePos((int)startPos.x, (int)startPos.y);
		}
	}
	
	@Override
	public void performMovement(float xOffset, float yOffset) {
		performMovementForOrb(northOrb, xOffset, yOffset);
		performMovementForOrb(southOrb, xOffset, yOffset);
		performMovementForOrb(westOrb, xOffset, yOffset);
		performMovementForOrb(eastOrb, xOffset, yOffset);
	}

	private void performMovementForOrb(Entity orb, float xOffset, float yOffset) {
		if (orb != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(orb);
			gridPositionComponent.absolutePos((int)(gridPositionComponent.getAbsolutePos().x + xOffset), 
					(int)(gridPositionComponent.getAbsolutePos().y + yOffset));
		}
	}
	
	
	@Override
	public void endMovement(Vector2 finalPos) {
		placeOrb(northOrb, finalPos);
		placeOrb(southOrb, finalPos);
		placeOrb(westOrb, finalPos);
		placeOrb(eastOrb, finalPos);
	}
	
	@Override
	public void place(Vector2 tilePos) {
		placeOrb(northOrb, tilePos);
		placeOrb(southOrb, tilePos);
		placeOrb(westOrb, tilePos);
		placeOrb(eastOrb, tilePos);
	}
	
	private void placeOrb(Entity orb, Vector2 finalPos) {
		if (orb != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(orb);
			if (orb == northOrb) {
				gridPositionComponent.coord(northOrb, (int)finalPos.x, (int)finalPos.y + 1, room);
			} else if (orb == southOrb) {
				gridPositionComponent.coord(southOrb, (int)finalPos.x, (int)finalPos.y - 1, room);
			} else if (orb == westOrb) {
				gridPositionComponent.coord(westOrb, (int)finalPos.x - 1, (int)finalPos.y, room);
			} else if (orb == eastOrb) {
				gridPositionComponent.coord(eastOrb, (int)finalPos.x + 1, (int)finalPos.y, room);
			}
			
			OrbUtil.checkContact(orb, room);
		}
	}
	
	//***********************
	// Getters and Setters
	
	public Entity getNorthOrb() {
		return northOrb;
	}

	public void setNorthOrb(Entity northOrb) {
		this.northOrb = northOrb;
	}

	public Entity getSouthOrb() {
		return southOrb;
	}

	public void setSouthOrb(Entity southOrb) {
		this.southOrb = southOrb;
	}

	public Entity getWestOrb() {
		return westOrb;
	}

	public void setWestOrb(Entity westOrb) {
		this.westOrb = westOrb;
	}

	public Entity getEasthOrb() {
		return eastOrb;
	}

	public void setEasthOrb(Entity easthOrb) {
		this.eastOrb = easthOrb;
	}

}
