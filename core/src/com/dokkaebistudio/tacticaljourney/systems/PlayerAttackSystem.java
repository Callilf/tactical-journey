package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTileSearchService;
import com.dokkaebistudio.tacticaljourney.ai.movements.TileSearchService;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.SkillComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WheelComponent;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.wheel.AttackWheel;
import com.dokkaebistudio.tacticaljourney.wheel.Sector;

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
    		    		WheelComponent wheelComponent = Mappers.wheelComponentMapper.get(playerCompo.getSkillMelee());
    	    			selectAttackTile(attackCompo, wheelComponent, false, attackTile, targetPos);
    	    			break;
    				}
    			}
    			
    			moveCompo.setSelectedAttackTile(null);
    			moveCompo.setFastAttackTarget(null);
    		} else {
	    		// Normal attack mode, check if the user clicked on an attackable tile at attackable range
    			WheelComponent wheelComponent = null;
    			if (skillEntity != null) {
    				wheelComponent = Mappers.wheelComponentMapper.get(skillEntity);
    			} else {
    				wheelComponent = Mappers.wheelComponentMapper.get(playerCompo.getSkillMelee());
    			}
	            checkIfAttackTileSelected(attackCompo, wheelComponent, attackerCurrentPos, false);
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

		    		HUDRenderer.isTargeting = true;
		    		
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
	    		WheelComponent wheelComponent = Mappers.wheelComponentMapper.get(skillEntity);
	    		checkIfAttackTileSelected(skillAttackCompo, wheelComponent, 
	    				attackerCurrentPos, skillComponent.getType().isThrowing());
	    		
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
			final AttackComponent wheelAttackComponent = wheel.getAttackComponent();
			Tile targetedTile = wheelAttackComponent.getTargetedTile();

			Action finishAttackAction = new Action(){
			  @Override
			  public boolean act(float delta){
				finishAttack(player, wheelAttackComponent, skillEntity);
			    return true;
			  }
			};
			
			if (!wheelAttackComponent.getActiveSkill().getAttackAnimation().isPlaying()) {
				// Orient the sprite so that it looks towards its target
				Mappers.spriteComponent.get(GameScreen.player).orientSprite(GameScreen.player, targetedTile.getGridPos());

				boolean hasAnim = wheelAttackComponent.getActiveSkill().setAttackImage(attackerCurrentPos.coord(), 
						targetedTile, 
						wheel.getPointedSector(),
						stage,
						finishAttackAction);
				
				if (!hasAnim) {
    				finishAttack(player, wheelAttackComponent, skillEntity);
				}
			}
    		
    		
    		break;
    		
    		
    	case PLAYER_THROWING:
    		
    		if (skillEntity != null) {
	    		final AttackComponent skillAttackCompo = Mappers.attackComponent.get(skillEntity);

	    		if (!skillAttackCompo.getActiveSkill().getAttackAnimation().isPlaying()) {
		    		targetedTile = skillAttackCompo.getTargetedTile();
		    		
					// Orient the sprite so that it looks towards its target
					Mappers.spriteComponent.get(GameScreen.player).orientSprite(GameScreen.player, targetedTile.getGridPos());
		    		
		    		final Vector2 targetedPosition = targetedTile.getGridPos();
		    		
		    		Action finishThrowAction = null;
		    		if (skillAttackCompo.getThrownEntity() != null) {
		    			ItemComponent itemComponent = Mappers.itemComponent.get(skillAttackCompo.getThrownEntity());
		    			skillAttackCompo.getActiveSkill().getAttackAnimation().setAttackAnim(itemComponent.getItemImageName().getRegion());
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
					
					skillAttackCompo.getActiveSkill().setAttackImage(attackerCurrentPos.coord(), 
							targetedTile, 
							null,
							stage,
							finishThrowAction);
	    		}
	    		
			}
    		break;
    		
    	case PLAYER_END_TURN:
    		clearAllEntityTiles(player);

    		// unselect the skill
    		if (skillEntity != null) {
    			stopSkillUse(playerCompo, skillEntity, moveCompo, attackCompo);
    		}
    		
			AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(player);
			alterationReceiverComponent.onPlayerTurnEnds(player, room);

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
	private void finishBombThrow(final Entity player, final Entity skillEntity, Vector2 targetedGridPosition) {		
		AttackComponent skillAttackCompo = Mappers.attackComponent.get(skillEntity);
		skillAttackCompo.getActiveSkill().clearAttackImage();
		
		Entity bomb = room.entityFactory.createBomb(room, targetedGridPosition, player,
				skillAttackCompo.getBombRadius(), skillAttackCompo.getBombTurnsToExplode(), skillAttackCompo.getStrength());
		
		AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(player);
		if (ammoCarrierComponent != null) {
			ammoCarrierComponent.useAmmo(skillAttackCompo.getAmmoType(), skillAttackCompo.getAmmosUsedPerAttack());
		}
		
		Journal.addEntry("You threw a bomb");

		room.turnManager.endPlayerTurn();
	}
	
    /**
     * Finish the bomb throw. This is called after the animation of the bomb being thrown is done.
     * @param player the player
     * @param skillEntity the skill used
     * @param targetedPosition the targeted position
     */
	private void finishItemThrow(final Entity player, final Entity skillEntity, Vector2 targetedGridPosition) {		
		AttackComponent skillAttackCompo = Mappers.attackComponent.get(skillEntity);
		skillAttackCompo.getActiveSkill().clearAttackImage();
		
		Entity thrownEntity = skillAttackCompo.getThrownEntity();
		ItemComponent itemComponent = Mappers.itemComponent.get(thrownEntity);
		Journal.addEntry("You threw " + itemComponent.getItemLabel());

		itemComponent.onThrow(targetedGridPosition, player, thrownEntity, room);		
		
		if (skillAttackCompo.isDoNotConsumeTurn()) {
			stopSkillUse(Mappers.playerComponent.get(player), 
					skillEntity, 
					Mappers.moveComponent.get(player), 
					Mappers.attackComponent.get(player));

			room.setNextState(RoomState.PLAYER_TURN_INIT);
		} else {
			room.turnManager.endPlayerTurn();
		}
	}
	
	/**
	 * Finish the attack. This is called after the wheel and the attack animation has been played.
	 * @param player the attacker entity (the player or a skill)
	 * @param wheelAttackCompo the attack component used for the wheel
	 * @param skillEntity the skill entity (if any).
	 */
	private void finishAttack(Entity player, AttackComponent wheelAttackCompo, Entity skillEntity) {	

		
		Sector pointedSector = wheel.getPointedSector();
		room.attackManager.performAttack(player, wheel.getAttackComponent(), pointedSector);
		
		//TODO : remove this or move it elsewhere
		wheelAttackCompo.clearAttackableTiles();
		wheelAttackCompo.getActiveSkill().clearAttackImage();
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
	private void checkIfAttackTileSelected(AttackComponent attackCompo, WheelComponent wheelCompo, GridPositionComponent attackerCurrentPos, boolean isThrow) {
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
						selectAttackTile(attackCompo, wheelCompo, isThrow, tile, gridPositionComponent);
		    			break;
					}
				}
			}
			
		}
	}

	private void selectAttackTile(AttackComponent attackCompo, WheelComponent wheelCompo, boolean isThrow, Entity targetedTile,
			GridPositionComponent targetedTileGridPositionComponent) {
		attackCompo.setTargetedTile(TileUtil.getTileAtGridPos(Mappers.gridPositionComponent.get(targetedTile).coord(), room));
		
		//Attack is possible !
		if (isThrow) {
			room.setNextState(RoomState.PLAYER_THROWING);
		} else {
			Entity target = TileUtil.getAttackableEntityOnTile(GameScreen.player, targetedTileGridPositionComponent.coord(), room);
			attackCompo.setTarget(target);
			wheel.setAttackComponent(attackCompo);
			wheel.setWheelComponent(wheelCompo);

			if (target == null) {
				//TODO For now, the attack wheel is not displayed if we attack a tile with no attackable entity on it
				// This might need to change later.
				room.setNextState(RoomState.PLAYER_ATTACK_ANIMATION);
			} else {
				room.setNextState(RoomState.PLAYER_WHEEL_START);
			}
		}
		
		HUDRenderer.isTargeting = false;
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
