package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.dokkaebistudio.tacticaljourney.AttackWheel;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.WheelComponent.Sector;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class PlayerAttackSystem extends IteratingSystem implements RoomSystem {
	
	/** The attack wheel. */
    private final AttackWheel wheel;
    
    /** The current room. */
    private Room room;

    public PlayerAttackSystem(Room room, AttackWheel attackWheel) {
        super(Family.all(PlayerComponent.class, GridPositionComponent.class).get());
        this.room = room;
        this.wheel = attackWheel;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }

    @Override
    protected void processEntity(Entity attackerEntity, float deltaTime) {
    	AttackComponent attackCompo = Mappers.attackComponent.get(attackerEntity);
    	MoveComponent moveCompo = Mappers.moveComponent.get(attackerEntity);
    	GridPositionComponent attackerCurrentPos = Mappers.gridPositionComponent.get(attackerEntity);
    	PlayerComponent playerCompo = Mappers.playerComponent.get(attackerEntity);
    	
    	if (!room.state.isPlayerTurn()) {
    		return;
    	}
    	    	
    	switch(room.state) {
	        
    	case PLAYER_MOVE_TILES_DISPLAYED:
    		//When clicking on a moveTile, display it as the destination
            if (InputSingleton.getInstance().leftClickJustReleased) {
            	int x = Gdx.input.getX();
            	int y = GameScreen.SCREEN_H - Gdx.input.getY();
            	
            	for (Entity tile : attackCompo.attackableTiles) {
            		SpriteComponent spriteComponent = Mappers.spriteComponent.get(tile);
            		if (spriteComponent.containsPoint(x, y)) {
            			//Check the distance of this attackableTile
            			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(tile);
            			int distanceBetweenTiles = TileUtil.getDistanceBetweenTiles(attackerCurrentPos.coord, gridPositionComponent.coord);
            			
            			if (distanceBetweenTiles >= attackCompo.getRangeMin() && distanceBetweenTiles <= attackCompo.getRangeMax()) {
            				
            				//Attack is possible !
	            			Entity target = TileUtil.getAttackableEntityOnTile(gridPositionComponent.coord, room);
            				attackCompo.setTarget(target);
            				room.state = RoomState.PLAYER_WHEEL_START;

	            			break;
            			}
            		}
            	}
            	
            }
            break;
    		
            
    	case PLAYER_WHEEL_FINISHED:
    		
    		Sector pointedSector = wheel.getPointedSector();
    		room.attackManager.performAttack(attackerEntity, attackCompo.getTarget(), pointedSector);
			moveCompo.clearMovableTiles();
    		attackCompo.clearAttackableTiles();
			room.turnManager.endPlayerTurn();
    		
    		break;
    		
    		
    	default:
    		break;
    	
    	}
    	
    }
    
}
