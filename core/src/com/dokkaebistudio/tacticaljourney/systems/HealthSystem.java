package com.dokkaebistudio.tacticaljourney.systems;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.GameTimeSingleton;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.display.DamageDisplayComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.MapRenderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomClearedState;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;

public class HealthSystem extends IteratingSystem implements RoomSystem {
	    
	public GameScreen gameScreen;
	public Stage fxStage;
	
	/** The current room. */
    private Room room;
    
    private List<Float> offsetTimes = new ArrayList<>();

    public HealthSystem(GameScreen gameScreen, Room r, Stage s) {
        super(Family.one(HealthComponent.class, DamageDisplayComponent.class).get());
		this.priority = 22;

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
    	float elapsedTime = GameTimeSingleton.getInstance().getElapsedTime();
    	
    	Iterator<Float> iterator = offsetTimes.iterator();
    	while(iterator.hasNext()) {
    		if (iterator.next() < elapsedTime) {
    			iterator.remove();
    		}
    	}
    	
    	
    	// 1 - Handle entities with healthComponent
    	HealthComponent healthCompo = Mappers.healthComponent.get(entity);
    	if (healthCompo != null) {
    		
    		if (room.getState() == RoomState.PLAYER_MOVE_TILES_DISPLAYED) {
    			healthCompo.setReceivedDamageLastTurn(false);
    		}
    		
    		// If a health modification has occurred during this frame
	    	if (!healthCompo.getHealthChangeMap().isEmpty()) {
	    		
				GridPositionComponent gridPos = Mappers.gridPositionComponent.get(entity);
	
				for (Entry<HealthChangeEnum, String> entry : healthCompo.getHealthChangeMap().entrySet()) {
					HealthChangeEnum healthChange = entry.getKey();
					String displayValue = entry.getValue();
		    		switch(healthChange) {
		    		case HIT_INTERRUPT:
			    		healthCompo.setReceivedDamageLastTurn(true);
		    		case HIT:	    			
						room.entityFactory.createDamageDisplayer(displayValue, gridPos, healthChange,
								offsetTimes.size() * -20, room);
		
	    				// Alert the enemy if the player attacked it or if the enemy attacked the played when it was close enough
		    			alertEnemy(entity, healthCompo);
	
		    			break;
		    		case RESISTANT:
		    		case HEALED:
		    		case ARMOR:
						room.entityFactory.createDamageDisplayer(displayValue, gridPos, healthChange,
								offsetTimes.size() * -20, room);
		
		    			break;
		    		default:
		    		}
		    		
		    		offsetTimes.add(elapsedTime + 0.5f);
				}

	    		healthCompo.clearModified();
	    	}
    	
    	
	    	// Handle death
	    	if (healthCompo.getHp() <= 0) {
				//Entity is dead
				
				PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
				if (playerComponent != null) {
					
					AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(entity);
					if (alterationReceiverComponent != null) {
						alterationReceiverComponent.onDeath(entity, healthCompo.getAttacker(), room);
					}
					
					if (healthCompo.getHp() <= 0) {
						// Death of the player!
						gameScreen.state = GameScreen.GAME_OVER;
					}
					
				} else {
					// Death of any other entity than the player
					
					LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(entity);
					if (lootRewardComponent != null && lootRewardComponent.getDrop() != null) {
						// Drop reward
						dropItem(entity, lootRewardComponent);
					}
					
					EnemyComponent enemyComponent = Mappers.enemyComponent.get(entity);
					if (enemyComponent != null) {
						Journal.addEntry(enemyComponent.getType().title() + " died");
						// An enemy died
						enemyComponent.onDeath(entity, healthCompo.getAttacker(), room);
					}
					
		    		// Get XP
		    		if (healthCompo.getAttacker() != null) {
						ExperienceComponent expCompo = getExperienceComponent(healthCompo.getAttacker());
						ExpRewardComponent expRewardCompo = Mappers.expRewardComponent.get(entity);
						if (expCompo != null && expRewardCompo != null) {
							expCompo.earnXp(expRewardCompo.getExpGain());
						}
		    		}
					
					
					// Alteration events
					AlterationReceiverComponent deadEntityAlterationReceiverCompo = Mappers.alterationReceiverComponent.get(entity);
					if (deadEntityAlterationReceiverCompo != null) {
						deadEntityAlterationReceiverCompo.onDeath(entity, healthCompo.getAttacker(), room);
					}
					AlterationReceiverComponent attackerAlterationReceiverCompo = null;
					if (healthCompo.getAttacker() != null) {
						attackerAlterationReceiverCompo = Mappers.alterationReceiverComponent.get(healthCompo.getAttacker());
						if (attackerAlterationReceiverCompo != null) {
							attackerAlterationReceiverCompo.onKill(healthCompo.getAttacker(), entity, room);
						}
					}
					
					StatusReceiverComponent deadEntityStatusReceiverCompo = Mappers.statusReceiverComponent.get(entity);
					if (deadEntityStatusReceiverCompo != null) {
						for (Status status : deadEntityStatusReceiverCompo.getStatuses()) {
							status.onDeath(entity, room);
						}
					}
					
					room.removeEnemy(entity);					
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
