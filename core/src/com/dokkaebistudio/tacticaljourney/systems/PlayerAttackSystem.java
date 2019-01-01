package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.AttackWheel;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.ai.movements.TileSearchUtil;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.SkillComponent;
import com.dokkaebistudio.tacticaljourney.components.WheelComponent.Sector;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
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
		Entity skillEntity = playerCompo.getActiveSkill();

    	
    	if (!room.state.isPlayerTurn()) {
    		return;
    	}
    	    	
    	switch(room.state) {
	        
    	case PLAYER_MOVE_TILES_DISPLAYED:
    		//When clicking on an attack tile, display the wheel
            selectAttackTile(attackCompo, attackerCurrentPos);
            break;
    		
            
    	case PLAYER_WHEEL_FINISHED:
    		
    		Sector pointedSector = wheel.getPointedSector();
    		room.attackManager.performAttack(attackerEntity, wheel.getAttackComponent(), pointedSector);
    		clearAllEntityTiles(attackerEntity);
    		
    		//TODO : remove this or move it elsewhere
			wheel.getAttackComponent().clearAttackableTiles();
			wheel.setAttackComponent(null);
			
			room.turnManager.endPlayerTurn();
    		
    		break;
    		
    		
    	case PLAYER_TARGETING_START:
    		
    		if (skillEntity != null) {
	    		GridPositionComponent skillPos = Mappers.gridPositionComponent.get(skillEntity);
	    		skillPos.coord.set(attackerCurrentPos.coord);
	    		TileSearchUtil.buildMoveTilesSet(skillEntity, room);
	    		TileSearchUtil.buildAttackTilesSet(skillEntity, room, false);
	    		
	    		TransformComponent indicatorTransfo = Mappers.transfoComponent.get(playerCompo.getActiveSkillIndicator());
	    		SkillComponent skillComponent = Mappers.skillComponent.get(skillEntity);
	    		
	    		switch(skillComponent.getSkillNumber()) {
	    		case 1:
		    		TransformComponent activeSkill1BtnTransfo = Mappers.transfoComponent.get(playerCompo.getSkill1Button());
		    		indicatorTransfo.pos.set(activeSkill1BtnTransfo.pos);
		    		break;
	    		case 2:
		    		TransformComponent activeSkill2BtnTransfo = Mappers.transfoComponent.get(playerCompo.getSkill2Button());
		    		indicatorTransfo.pos.set(activeSkill2BtnTransfo.pos);
		    		break;
		    		default:
	    		}
	    		indicatorTransfo.pos.x = indicatorTransfo.pos.x - 5;
	    		indicatorTransfo.pos.y = indicatorTransfo.pos.y - 5;

	
	    		room.state = RoomState.PLAYER_TARGETING;
    		}
    		
    		break;
    		
    	case PLAYER_TARGETING:
    		
    		if (skillEntity != null) {
	    		// Display all attackable tiles
	    		
	    		//Display the wheel is a tile is clicked
	    		AttackComponent skillAttackCompo = Mappers.attackComponent.get(skillEntity);
	            selectAttackTile(skillAttackCompo, attackerCurrentPos);
	            
	            //Handle the change of skill
	    		PlayerMoveSystem.handleSkillSelection(attackerEntity, room);
    		}
    		
    		break;
    		
    	case PLAYER_TARGETING_STOP:
    		
    		if (skillEntity != null) {
				//Clear the skill
				MoveComponent skillMoveCompo = Mappers.moveComponent.get(skillEntity);
				skillMoveCompo.clearMovableTiles();
				AttackComponent skillAttackComponent = Mappers.attackComponent.get(skillEntity);
				skillAttackComponent.clearAttackableTiles();
				//unselect skill
        		playerCompo.setActiveSkill(null);
	    		TransformComponent indicatorTransfo = Mappers.transfoComponent.get(playerCompo.getActiveSkillIndicator());
	    		indicatorTransfo.pos.set(-100,-100,0);
				
				room.state = RoomState.PLAYER_MOVE_TILES_DISPLAYED;
    		}

    		
    		break;

    		
    	default:
    		break;
    	
    	}
    	
    }

	private void selectAttackTile(AttackComponent attackCompo, GridPositionComponent attackerCurrentPos) {
		if (InputSingleton.getInstance().leftClickJustReleased) {
			Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
			int x = (int) touchPoint.x;
        	int y = (int) touchPoint.y;
			
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
						wheel.setAttackComponent(attackCompo);
						room.state = RoomState.PLAYER_WHEEL_START;

		    			break;
					}
				}
			}
			
		}
	}
	
	private void clearAllEntityTiles(Entity player) {
		AttackComponent attackComponent = Mappers.attackComponent.get(player);
		MoveComponent moveComponent = Mappers.moveComponent.get(player);
		
		moveComponent.clearMovableTiles();
		attackComponent.clearAttackableTiles();
		
		PlayerComponent playerComponent = Mappers.playerComponent.get(player);
		if (playerComponent != null && playerComponent.getSkill1() != null) {
			clearAllEntityTiles(playerComponent.getSkill1());
		}
	}
    
}
