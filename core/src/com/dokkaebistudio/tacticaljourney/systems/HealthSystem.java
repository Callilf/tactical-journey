package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.display.DamageDisplayComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

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
    		
	    	// Display experience gained
	    	if (healthCompo.getHealthChange() != HealthChangeEnum.NONE) {
	    		
				GridPositionComponent gridPos = Mappers.gridPositionComponent.get(entity);
	
	    		switch(healthCompo.getHealthChange()) {
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

	    		healthCompo.setReceivedDamageLastTurn(true);
	    		healthCompo.clearModified();
	    	}
    	
    	
	    	// Handle death
	    	if (healthCompo.getHp() <= 0) {
				//Entity is dead
				
	    		//TODO : try to handle experience here, but for now it's in AttackManager since
	    		// when a bomb explodes, it is removed from the game before this code is executed...
//				//earn xp
//	    		if (healthCompo.getAttacker() != null) {
//					ExperienceComponent expCompo = getExperienceComponent(healthCompo.getAttacker());
//					ExpRewardComponent expRewardCompo = Mappers.expRewardComponent.get(entity);
//					if (expCompo != null && expRewardCompo != null) {
//						expCompo.earnXp(expRewardCompo.getExpGain());
//					}
//	    		}
				
				
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
						
						// Do not remove the entity from the room yet, remove it once the drop animation if over
						entity.remove(EnemyComponent.class);
						entity.remove(HealthComponent.class);
						entity.remove(SpriteComponent.class);
					} else {
					
						room.removeEnemy(entity);
						//TODO: play death animation
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
		if ((Mappers.enemyComponent.has(entity) && Mappers.playerComponent.has(healthCompo.getAttacker()))) {
			Mappers.enemyComponent.get(entity).setAlerted(true);
		}
		if (Mappers.playerComponent.has(entity) && Mappers.enemyComponent.has(healthCompo.getAttacker())) {
			Mappers.enemyComponent.get(healthCompo.getAttacker()).setAlerted(true);
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
		
		// Drop animation
		Action finishDropAction = new Action(){
		  @Override
		  public boolean act(float delta){
			itemComponent.drop(entity, dropItem, room);
			
			room.getAddedItems().add(dropItem);
			room.removeEnemy(entity);
		    return true;
		  }
		};
		Image dropAnimationImage = itemComponent.getDropAnimationImage(entity, dropItem, finishDropAction);
		fxStage.addActor(dropAnimationImage);
	}

}
