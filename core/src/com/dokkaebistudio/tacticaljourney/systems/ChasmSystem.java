/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.dokkaebistudio.tacticaljourney.systems;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomCreatureState;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Handle entities that fall into chasms.
 * @author Callil
 *
 */
public class ChasmSystem extends EntitySystem implements RoomSystem {	
	
	private Room room;
	
    /** The explosives of the current room that need updating. */
    private List<Tile> allChasmOfCurrentRoom = new ArrayList<>();
	private List<Entity> entitiesToRemoveFromPosition = new ArrayList<>();

	
	public ChasmSystem(Room r) {
		this.priority = 7;

		this.room = r;
	}
	
	
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
    
	@Override
	public void update(float deltaTime) {
		
		if (room.getState() == RoomState.PLAYER_TURN_INIT
				|| (room.getState() == RoomState.ENEMY_TURN && room.getCreatureState() == RoomCreatureState.TURN_INIT)) {
			fillChasmsOfCurrentRoom();
		}
		
		entitiesToRemoveFromPosition.clear();
		for (Tile chasmTile : allChasmOfCurrentRoom) {
			Set<Entity> entitiesOnChasm = room.getEntitiesAtPosition(chasmTile.getGridPos());
			entitiesOnChasm.stream()
				.filter(e -> canFall(e))
				.forEach(e -> fall(e, chasmTile));
		}
		
		entitiesToRemoveFromPosition.parallelStream()
			.forEach(e -> room.removeEntityFromPosition(e));
	}

	
	//*********
	// Utils
	
	private void fillChasmsOfCurrentRoom() {
		allChasmOfCurrentRoom.clear();
		for (Entity e : room.getAllEntities()) {
			if (Mappers.chasmComponent.has(e)) {
				allChasmOfCurrentRoom.add(TileUtil.getTileAtGridPos(Mappers.gridPositionComponent.get(e).coord(), room));
			}
		}
	}
	
	/**
	 * Whether the given entity can fall into a chasm or not.
	 * @param e the entity
	 * @return true if it should fall, false if not
	 */
	public boolean canFall(Entity e) {
		return Mappers.gravityComponent.has(e) && !Mappers.flyComponent.has(e);
	}

	/**
	 * Make the entity fall into the chasm.
	 * Special case : if the entity is the player, it respawns near the chasm but loses 30 hp.
	 * @param entity the entity that is falling
	 * @param tile the tile where the entity and the chasm are
	 */
	private void fall(Entity entity, Tile tile) {
		room.pauseState();
		entitiesToRemoveFromPosition.add(entity);
		
		SpriteComponent spriteComponent = Mappers.spriteComponent.get(entity);
		spriteComponent.hide = true;
		final Image image = new Image(spriteComponent.getSprite());
		Action removeImageAction = new Action(){
		  @Override
		  public boolean act(float delta){
			  
			  // Remove the entity, except if it's the player
			  if (GameScreen.player == entity) {
				  // Place the player on the last ground tile he visited
				  MoveComponent moveComponent = Mappers.moveComponent.get(entity);
				  Vector2 lastGroundTile = moveComponent != null && moveComponent.getLastWalkableTile() != null ? 
						  moveComponent.getLastWalkableTile() : new Vector2(11,11);
				  
				  MovementHandler.placeEntity(entity, lastGroundTile, room);
				  Mappers.spriteComponent.get(entity).hide = false;
				  // Deal 30 damages
				  Mappers.healthComponent.get(entity).hit(30, entity, null);
				  
				  Journal.addEntry(Journal.getLabel(entity) + " fell into a chasm and lost 30 hp");
			  } else {
				  room.removeEntity(entity);
				  Journal.addEntry(Journal.getLabel(entity) + " fell into a chasm");
			  }
			  
			  image.remove();
			  room.unpauseState();
			  return true;
		  }
		};
		
		PoolableVector2 pixelPos = TileUtil.convertGridPosIntoPixelPos(tile.getGridPos());
		image.setPosition(pixelPos.x, pixelPos.y);
		pixelPos.free();

		image.setOrigin(Align.center);
		
		ScaleToAction scaleToZero = Actions.scaleTo(0, 0, 2f);
		RotateByAction rotate = Actions.rotateBy(1080f, 2f);
		image.addAction(Actions.sequence(Actions.parallel(scaleToZero, rotate), removeImageAction));
		
		GameScreen.fxStage.addActor(image);	
	}

}
