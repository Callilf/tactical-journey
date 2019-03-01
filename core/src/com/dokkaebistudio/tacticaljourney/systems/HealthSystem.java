package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.DamageDisplayComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.rendering.MapRenderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomClearedState;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;

public class HealthSystem extends IteratingSystem implements RoomSystem {
	    
	public GameScreen gameScreen;
	public Stage fxStage;
	
	/** The current room. */
    private Room room;    

    public HealthSystem(GameScreen gameScreen, Room r, Stage s) {
        super(Family.one(HealthComponent.class, DamageDisplayComponent.class).get());
		this.priority = 21;

		this.gameScreen = gameScreen;
        this.room = r;
        this.fxStage = s;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }

    @Override
    protected void processEntity(final Entity entity, float deltaTime) {
    	
    	
    	// 1 - Handle entities with healthComponent
    	HealthComponent healthCompo = Mappers.healthComponent.get(entity);
    	if (healthCompo != null) {
    		
    		if (room.getState() == RoomState.PLAYER_MOVE_TILES_DISPLAYED) {
    			healthCompo.setReceivedDamageLastTurn(false);
    		}
    		
    		// If a health modification has occurred during this frame
	    	if (healthCompo.getHealthChange() != HealthChangeEnum.NONE) {
	    		
				GridPositionComponent gridPos = Mappers.gridPositionComponent.get(entity);
	
	    		switch(healthCompo.getHealthChange()) {
	    		case HIT_INTERRUPT:
		    		healthCompo.setReceivedDamageLastTurn(true);
	    		case HIT:	    			
					room.entityFactory.createDamageDisplayer(String.valueOf(healthCompo.getHealthLostAtCurrentFrame()), 
							gridPos, healthCompo.getHealthChange(), 0, room);
	
    				// Alert the enemy if the player attacked it or if the enemy attacked the played when it was close enough
	    			alertEnemy(entity, healthCompo);

	    			break;
	    		case HEALED:
	    		case ARMOR:
					room.entityFactory.createDamageDisplayer(String.valueOf(healthCompo.getHealthRecoveredAtCurrentFrame()), 
							gridPos, healthCompo.getHealthChange(), 0, room);
	
	    			break;
	    		default:
	    		}

	    		healthCompo.clearModified();
	    	}
    	
    	
	    	// Handle death
	    	if (healthCompo.getHp() <= 0) {
				//Entity is dead
				
	    		//TODO : try to handle experience here, but for now it's in AttackManager since
	    		// when a bomb explodes, it is removed from the game before this code is executed...
				//earn xp
	    		if (healthCompo.getAttacker() != null) {
					ExperienceComponent expCompo = getExperienceComponent(healthCompo.getAttacker());
					ExpRewardComponent expRewardCompo = Mappers.expRewardComponent.get(entity);
					if (expCompo != null && expRewardCompo != null) {
						expCompo.earnXp(expRewardCompo.getExpGain());
					}
	    		}
				
				
				PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
				if (playerComponent != null) {
					// Death of the player!
					gameScreen.state = GameScreen.GAME_OVER;
					
				} else {
					// Death of any other entity than the player
					LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(entity);
					if (lootRewardComponent != null && lootRewardComponent.getDrop() != null) {
						// Drop reward
						dropItem(entity, lootRewardComponent);
					}
					
					room.removeEnemy(entity);					
					if (!room.hasEnemies()) {
						
						if (!room.isCleared()) {
							room.setCleared(RoomClearedState.JUST_CLEARED);

							Entity attacker = healthCompo.getAttacker();
							AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(attacker);
							if (alterationReceiverComponent != null) {
								alterationReceiverComponent.onRoomCleared(attacker, room);
							}
						}
						
						//TODO move this
						MapRenderer.requireRefresh();
					}
					
				}
			
	    	}

	    	healthCompo.setAttacker(null);
    	}
    	    
    	
    	
    	// 2 - Handle entities with DamageDisplayComponent
    	DamageDisplayComponent damageDisplayComponent = Mappers.damageDisplayCompoM.get(entity);
    	if (damageDisplayComponent != null) {
	    	GridPositionComponent gridPosCompo = Mappers.gridPositionComponent.get(entity);
	    	gridPosCompo.absolutePos(gridPosCompo.getAbsolutePos().x, gridPosCompo.getAbsolutePos().y + 1);
	    	
	    	if (gridPosCompo.getAbsolutePos().y > damageDisplayComponent.getInitialPosition().y + 100) {
	    		room.removeEntity(entity);
	    	}
    	}
    }

    /**
     * Switch the alert state of an enemy if the player attacked it or it the enemy came close enough to
     * the player to attack it.
     * @param entity the entity that received damage
     * @param healthCompo the health component
     */
	private void alertEnemy(final Entity entity, HealthComponent healthCompo) {
		if (healthCompo.getAttacker() != null) {
			// Alert the enemy the player just attacked
			if ((Mappers.enemyComponent.has(entity) && Mappers.playerComponent.has(healthCompo.getAttacker()))) {
				Mappers.enemyComponent.get(entity).setAlerted(true);
			}
			// Alert the enemy that attacked the player
			if (Mappers.playerComponent.has(entity) && Mappers.enemyComponent.has(healthCompo.getAttacker())) {
				Mappers.enemyComponent.get(healthCompo.getAttacker()).setAlerted(true);
			}
		}
	}

    /**
     * Drop an item on death.
     * @param entity the entity that died and will drop the item
     * @param lootRewardComponent the lootRewardComponent of the entity
     */
	private void dropItem(final Entity entity, final LootRewardComponent lootRewardComponent) {
		final Entity dropItem = lootRewardComponent.getDrop();
		final ItemComponent itemComponent = Mappers.itemComponent.get(dropItem);
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
		final PoolableVector2 dropLocation = PoolableVector2.create(gridPositionComponent.coord());

		// Drop animation
		Action finishDropAction = new Action(){
		  @Override
		  public boolean act(float delta){
			itemComponent.drop(dropLocation, dropItem, room);
			dropLocation.free();
			
			room.getAddedItems().add(dropItem);
		    return true;
		  }
		};
		Image dropAnimationImage = itemComponent.getDropAnimationImage(entity, dropItem, finishDropAction);
		fxStage.addActor(dropAnimationImage);
	}

	
	//********************************
	// Private methods
	
	private ExperienceComponent getExperienceComponent(Entity attacker) {
		ExperienceComponent result = null;
		result = Mappers.experienceComponent.get(attacker);
		if (result == null) {
			ParentEntityComponent parentEntityComponent = Mappers.parentEntityComponent.get(attacker);
			if (parentEntityComponent != null) {
				Entity parent = parentEntityComponent.getParent();
				if (parent != null) {
					result = getExperienceComponent(parent);
				}
			}
		}
		
		return result;
	}
	
}
