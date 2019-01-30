package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.AttackWheel;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTileSearchService;
import com.dokkaebistudio.tacticaljourney.ai.movements.TileSearchService;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.SkillComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WheelComponent.Sector;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class PlayerAttackSystem extends IteratingSystem implements RoomSystem {
	
	/** The attack wheel. */
    private final AttackWheel wheel;
    
    /** The current room. */
    private Room room;
    
	/** The tile search service. */
	private TileSearchService tileSearchService;
	/** The attack tile search service. */
	private AttackTileSearchService attackTileSearchService;

    public PlayerAttackSystem(Room room, AttackWheel attackWheel) {
        super(Family.all(PlayerComponent.class, GridPositionComponent.class).get());
		this.priority = 10;

        this.room = room;
        this.wheel = attackWheel;
		this.tileSearchService = new TileSearchService();
		this.attackTileSearchService = new AttackTileSearchService();

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

    	
    	if (!room.getState().isPlayerTurn()) {
    		return;
    	}
    	    	
    	switch(room.getState()) {
	        
    	case PLAYER_MOVE_TILES_DISPLAYED:
    		//When clicking on an attack tile, display the wheel
            selectAttackTile(attackCompo, attackerCurrentPos, false);
            break;

    	case PLAYER_TARGETING_START:
    		moveCompo.hideMovableTiles();
			moveCompo.clearSelectedTile();
			attackCompo.hideAttackableTiles();
    		
    		if (skillEntity != null) {
    			//unselect any other skill
    			if (playerCompo.getSkillMelee() != skillEntity) {
    				stopSkillUse(playerCompo, playerCompo.getSkillMelee(), moveCompo, attackCompo);
    			}
    			if (playerCompo.getSkillRange() != skillEntity) {
    				stopSkillUse(playerCompo, playerCompo.getSkillRange(), moveCompo, attackCompo);
    			}
    			if (playerCompo.getSkillBomb() != skillEntity) {
    				stopSkillUse(playerCompo, playerCompo.getSkillBomb(), moveCompo, attackCompo);
    			}
    			
    			AttackComponent skillAttackComponent = Mappers.attackComponent.get(skillEntity);
	    		if (room.attackManager.isAttackAllowed(attackerEntity, skillAttackComponent)) {
	    			// Find attackable tiles with the activated skill
		    		GridPositionComponent skillPos = Mappers.gridPositionComponent.get(skillEntity);
		    		skillPos.coord(attackerCurrentPos.coord());
		    		tileSearchService.buildMoveTilesSet(skillEntity, room);
		    		attackTileSearchService.buildAttackTilesSet(skillEntity, room, false);

		    		room.setNextState(RoomState.PLAYER_TARGETING);
	    		} else {
	    			//Cannot attack because the skill has run out of ammos
	    			// unselect the skill
					stopSkillUse(playerCompo, skillEntity, moveCompo, attackCompo);
					
					room.setNextState(RoomState.PLAYER_MOVE_TILES_DISPLAYED);
	    		}
    		}
    		
    		break;
    		
    	case PLAYER_TARGETING:
    		
    		if (skillEntity != null) {
	    		// Display all attackable tiles
				SkillComponent skillComponent = Mappers.skillComponent.get(skillEntity);
	    		
	    		//Display the wheel is a tile is clicked
	    		AttackComponent skillAttackCompo = Mappers.attackComponent.get(skillEntity);
	    		selectAttackTile(skillAttackCompo, attackerCurrentPos, skillComponent.getType().isThrowing());
	    		
    		}
    		
    		break;
    		
    	case PLAYER_TARGETING_STOP:
    		
    		if (skillEntity != null) {
    			// unselect the skill
				stopSkillUse(playerCompo, skillEntity, moveCompo, attackCompo);
				
				room.setNextState(RoomState.PLAYER_MOVE_TILES_DISPLAYED);
    		}

    		
    		break;
    		
    		
    	case PLAYER_WHEEL_FINISHED:
    		
			room.turnManager.endPlayerTurn();

    		Sector pointedSector = wheel.getPointedSector();
    		room.attackManager.performAttack(attackerEntity, wheel.getAttackComponent(), pointedSector);
    		clearAllEntityTiles(attackerEntity);
    		
    		//TODO : remove this or move it elsewhere
			wheel.getAttackComponent().clearAttackableTiles();
			wheel.setAttackComponent(null);
			
			if (skillEntity != null) {
    			// unselect the skill
				stopSkillUse(playerCompo, skillEntity, moveCompo, attackCompo);
			}
    		
    		break;
    		
    		
    	case PLAYER_THROWING:
    		
    		if (skillEntity != null) {
	    		AttackComponent skillAttackCompo = Mappers.attackComponent.get(skillEntity);
	    		Entity targetedTile = skillAttackCompo.getTargetedTile();
	    		GridPositionComponent targetedPosition = Mappers.gridPositionComponent.get(targetedTile);
	    		
	
				Entity bomb = room.entityFactory.createBomb(room, targetedPosition.coord(), attackerEntity);
				
				AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(attackerEntity);
				if (ammoCarrierComponent != null) {
					ammoCarrierComponent.useAmmo(skillAttackCompo.getAmmoType(), skillAttackCompo.getAmmosUsedPerAttack());
				}
				
	    		clearAllEntityTiles(attackerEntity);
	    		
    			// unselect the skill
				stopSkillUse(playerCompo, skillEntity, moveCompo, attackCompo);
				
				room.turnManager.endPlayerTurn();
			}

    		
    	default:
    		break;
    	
    	}
    	
    }

	private void stopSkillUse(PlayerComponent playerCompo, Entity skillEntity, MoveComponent playerMoveCompo, AttackComponent playerAttackCompo) {
		//Clear the skill
		MoveComponent skillMoveCompo = Mappers.moveComponent.get(skillEntity);
		skillMoveCompo.clearMovableTiles();
		AttackComponent skillAttackComponent = Mappers.attackComponent.get(skillEntity);
		skillAttackComponent.clearAttackableTiles();
		
		//unselect skill if the skill being cleared is the one currently is use
		if (playerCompo.getActiveSkill() == skillEntity) {
			playerCompo.setActiveSkill(null);			
			playerMoveCompo.showMovableTiles();
			playerAttackCompo.showAttackableTiles();
		}
		
	}

	private void selectAttackTile(AttackComponent attackCompo, GridPositionComponent attackerCurrentPos, boolean isThrow) {
		if (InputSingleton.getInstance().leftClickJustReleased) {
			Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
			int x = (int) touchPoint.x;
        	int y = (int) touchPoint.y;
			
			for (Entity tile : attackCompo.attackableTiles) {
				SpriteComponent spriteComponent = Mappers.spriteComponent.get(tile);
				if (spriteComponent.containsPoint(x, y)) {
					
					//TODO : is this needed ? Distance was already checked when computing the attackable tiles
					//Check the distance of this attackableTile
					GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(tile);
					int distanceBetweenTiles = TileUtil.getDistanceBetweenTiles(attackerCurrentPos.coord(), gridPositionComponent.coord());
					
					if (distanceBetweenTiles >= attackCompo.getRangeMin() && distanceBetweenTiles <= attackCompo.getRangeMax()) {
						attackCompo.setTargetedTile(tile);
						
						//Attack is possible !
						if (isThrow) {
							room.setNextState(RoomState.PLAYER_THROWING);
						} else {
			    			Entity target = TileUtil.getAttackableEntityOnTile(gridPositionComponent.coord(), room);
							attackCompo.setTarget(target);
							wheel.setAttackComponent(attackCompo);
							room.setNextState(RoomState.PLAYER_WHEEL_START);
						}

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
		if (playerComponent != null && playerComponent.getSkillMelee() != null) {
			clearAllEntityTiles(playerComponent.getSkillMelee());
		}
	}
    
}
