package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.AttackWheel;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTileSearchService;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTypeEnum;
import com.dokkaebistudio.tacticaljourney.ai.movements.TileSearchService;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
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
    private Stage stage;
    
	/** The tile search service. */
	private TileSearchService tileSearchService;
	/** The attack tile search service. */
	private AttackTileSearchService attackTileSearchService;

    public PlayerAttackSystem(Stage s, Room room, AttackWheel attackWheel) {
        super(Family.all(PlayerComponent.class, GridPositionComponent.class).get());
		this.priority = 10;

		this.stage = s;
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
    protected void processEntity(final Entity player, float deltaTime) {
    	if (!room.getState().isPlayerTurn()) {
    		return;
    	}
    	
    	final AttackComponent attackCompo = Mappers.attackComponent.get(player);
    	final MoveComponent moveCompo = Mappers.moveComponent.get(player);
    	GridPositionComponent attackerCurrentPos = Mappers.gridPositionComponent.get(player);
    	final PlayerComponent playerCompo = Mappers.playerComponent.get(player);
		final Entity skillEntity = playerCompo.getActiveSkill();
    	    	
		
    	switch(room.getState()) {
	        
    	case PLAYER_MOVE_TILES_DISPLAYED:
    		
    		if (moveCompo.getFastAttackTarget() != null) {
    			// Handle fast attack ! (i.e. the user clicked on an attackable tile from a distance)
				GridPositionComponent targetPos = Mappers.gridPositionComponent.get(moveCompo.getFastAttackTarget());

    			for (Entity attackTile : attackCompo.attackableTiles) {
    				GridPositionComponent attackTilePos = Mappers.gridPositionComponent.get(attackTile);
    				if (targetPos.coord().equals(attackTilePos.coord())) {
    	    			selectAttackTile(attackCompo, false, attackTile, targetPos);
    	    			break;
    				}
    			}
    			
    			moveCompo.setSelectedAttackTile(null);
    			moveCompo.setFastAttackTarget(null);
    		} else {
	    		// Normal attack mode, check if the user clicked on an attackable tile at attackable range
	            checkIfAttackTileSelected(attackCompo, attackerCurrentPos, false);
    		}
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
	    		if (room.attackManager.isAttackAllowed(player, skillAttackComponent)) {
	    			// Find attackable tiles with the activated skill
		    		GridPositionComponent skillPos = Mappers.gridPositionComponent.get(skillEntity);
		    		skillPos.coord(attackerCurrentPos.coord());
		    		tileSearchService.buildMoveTilesSet(skillEntity, room);
		    		attackTileSearchService.buildAttackTilesSet(skillEntity, room, false, false);

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
	    		checkIfAttackTileSelected(skillAttackCompo, attackerCurrentPos, skillComponent.getType().isThrowing());
	    		
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
    		room.setNextState(RoomState.PLAYER_ATTACK_ANIMATION);
    		
    		break;
    		
    	case PLAYER_ATTACK_ANIMATION:
    		
    		if (wheel.getAttackComponent().getAttackType() == AttackTypeEnum.RANGE) {
    			
    			AttackComponent attackComponent = wheel.getAttackComponent();
    			if (attackCompo.getProjectileImage() == null) {
    				
    				Action finishAttackAction = new Action(){
					  @Override
					  public boolean act(float delta){
						finishAttack(player, attackCompo, skillEntity);
					    return true;
					  }
					};
    				
    				GridPositionComponent targetPos = Mappers.gridPositionComponent.get(attackComponent.getTargetedTile());
    				attackCompo.setProjectileImage(Assets.projectile_arrow,
    						attackerCurrentPos.coord(), 
    						targetPos.coord(), 
    						true,
    						finishAttackAction);
    				
    				stage.addActor(attackCompo.getProjectileImage());
    			}
    			
    		} else {
				finishAttack(player, attackCompo, skillEntity);
    		}
    		
    		
    		break;
    		
    		
    	case PLAYER_THROWING:
    		
    		if (skillEntity != null) {
	    		final AttackComponent skillAttackCompo = Mappers.attackComponent.get(skillEntity);

	    		AtlasRegion projectileTexture = Assets.projectile_bomb;
	    		if (skillAttackCompo.getProjectileImage() == null) {
		    		Entity targetedTile = skillAttackCompo.getTargetedTile();
		    		final GridPositionComponent targetedPosition = Mappers.gridPositionComponent.get(targetedTile);
		    		
		    		Action finishThrowAction = null;
		    		if (skillAttackCompo.getThrownEntity() != null) {
		    			ItemComponent itemComponent = Mappers.itemComponent.get(skillAttackCompo.getThrownEntity());
		    			projectileTexture = itemComponent.getItemImageName();
		    			// Throw item from inventory
		    			finishThrowAction = new Action(){
							  @Override
							  public boolean act(float delta){
								  finishItemThrow(player, skillEntity, targetedPosition);
							    return true;
							  }
							};
		    		} else {
		    			// Throw bomb
						finishThrowAction = new Action(){
						  @Override
						  public boolean act(float delta){
							  finishBombThrow(player, skillEntity, targetedPosition);
						    return true;
						  }
						};
		    		}
					
					skillAttackCompo.setProjectileImage(projectileTexture,
							attackerCurrentPos.coord(), 
							targetedPosition.coord(), 
							false,
							finishThrowAction);
	
					
					stage.addActor(skillAttackCompo.getProjectileImage());
	    		}
	    		
			}
    		break;
    		
    	case PLAYER_END_TURN:
    		clearAllEntityTiles(player);

    		// unselect the skill
    		if (skillEntity != null) {
    			stopSkillUse(playerCompo, skillEntity, moveCompo, attackCompo);
    		}
    		break;
    		
    	default:
    		break;
    	
    	}
    	
    }


    /**
     * Finish the bomb throw. This is called after the animation of the bomb being thrown is done.
     * @param player the player
     * @param skillEntity the skill used
     * @param targetedPosition the targeted position
     */
	private void finishBombThrow(final Entity player, final Entity skillEntity, GridPositionComponent targetedPosition) {		
		AttackComponent skillAttackCompo = Mappers.attackComponent.get(skillEntity);

		if (skillAttackCompo.getProjectileImage() != null) {
			skillAttackCompo.getProjectileImage().remove();
			skillAttackCompo.setProjectileImage(null);
		}
		
		Entity bomb = room.entityFactory.createBomb(room, targetedPosition.coord(), player,
				skillAttackCompo.getBombRadius(), skillAttackCompo.getBombTurnsToExplode(), skillAttackCompo.getStrength());
		
		AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(player);
		if (ammoCarrierComponent != null) {
			ammoCarrierComponent.useAmmo(skillAttackCompo.getAmmoType(), skillAttackCompo.getAmmosUsedPerAttack());
		}
		
		room.turnManager.endPlayerTurn();
	}
	
    /**
     * Finish the bomb throw. This is called after the animation of the bomb being thrown is done.
     * @param player the player
     * @param skillEntity the skill used
     * @param targetedPosition the targeted position
     */
	private void finishItemThrow(final Entity player, final Entity skillEntity, GridPositionComponent targetedPosition) {		
		AttackComponent skillAttackCompo = Mappers.attackComponent.get(skillEntity);

		if (skillAttackCompo.getProjectileImage() != null) {
			skillAttackCompo.getProjectileImage().remove();
			skillAttackCompo.setProjectileImage(null);
		}
		
		Entity thrownEntity = skillAttackCompo.getThrownEntity();
		ItemComponent itemComponent = Mappers.itemComponent.get(thrownEntity);
		itemComponent.onThrow(targetedPosition.coord(), player, thrownEntity, room);		
		
		room.turnManager.endPlayerTurn();
	}

	
	/**
	 * Finish the attack. This is called after the wheel and the attack animation has been played.
	 * @param player the attacker entity (the player or a skill)
	 * @param wheelAttackCompo the attack component used for the wheel
	 * @param skillEntity the skill entity (if any).
	 */
	private void finishAttack(Entity player, AttackComponent wheelAttackCompo, Entity skillEntity) {		
		if (wheelAttackCompo.getProjectileImage() != null) {
			wheelAttackCompo.getProjectileImage().remove();
			wheelAttackCompo.setProjectileImage(null);
		}
		
		Sector pointedSector = wheel.getPointedSector();
		room.attackManager.performAttack(player, wheel.getAttackComponent(), pointedSector);
		
		//TODO : remove this or move it elsewhere
		wheel.getAttackComponent().clearAttackableTiles();
		wheel.setAttackComponent(null);
		
		room.turnManager.endPlayerTurn();
	}

	/**
	 * Clear the interface after stopping using a skill.
	 * @param playerCompo the player component
	 * @param skillEntity the skill entity
	 * @param playerMoveCompo the player move component
	 * @param playerAttackCompo the player attack component
	 */
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

	/**
	 * Select the attack tile on which we want to attack/throw.
	 * @param attackCompo the attack component
	 * @param attackerCurrentPos the position of the attacker
	 * @param isThrow whether it is a throw or an attack
	 */
	private void checkIfAttackTileSelected(AttackComponent attackCompo, GridPositionComponent attackerCurrentPos, boolean isThrow) {
		if (InputSingleton.getInstance().leftClickJustReleased) {
			Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
			int x = (int) touchPoint.x;
        	int y = (int) touchPoint.y;
			
			for (Entity tile : attackCompo.attackableTiles) {
				if (TileUtil.isPixelPosOnEntity(x, y, tile)) {
					
					//Check the distance of this attackableTile
					GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(tile);
					int distanceBetweenTiles = TileUtil.getDistanceBetweenTiles(attackerCurrentPos.coord(), gridPositionComponent.coord());
					
					if (distanceBetweenTiles >= attackCompo.getRangeMin() && distanceBetweenTiles <= attackCompo.getRangeMax()) {
						selectAttackTile(attackCompo, isThrow, tile, gridPositionComponent);
		    			break;
					}
				}
			}
			
		}
	}

	private void selectAttackTile(AttackComponent attackCompo, boolean isThrow, Entity targetedTile,
			GridPositionComponent targetedTileGridPositionComponent) {
		attackCompo.setTargetedTile(targetedTile);
		
		//Attack is possible !
		if (isThrow) {
			room.setNextState(RoomState.PLAYER_THROWING);
		} else {
			Entity target = TileUtil.getAttackableEntityOnTile(targetedTileGridPositionComponent.coord(), room);
			attackCompo.setTarget(target);
			wheel.setAttackComponent(attackCompo);
			room.setNextState(RoomState.PLAYER_WHEEL_START);
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
