package com.dokkaebistudio.tacticaljourney.systems;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.display.DamageDisplayComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AllyComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.singletons.GameTimeSingleton;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.LootUtil;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

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
						
		    			break;
		    		case HEALED:						
		    		case RESISTANT:
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
						
						String killerString = "Unknown";
						if (healthCompo.getAttacker() != null) {
							InspectableComponent inspectableComponent = Mappers.inspectableComponent.get(healthCompo.getAttacker());
							if (inspectableComponent != null) {
								killerString = inspectableComponent.getTitle();
							}
						}
						gameScreen.killerStr = killerString;
					}
					
			    	healthCompo.setAttacker(null);
			    	
				} else {
					Journal.addEntry(Mappers.inspectableComponent.get(entity).getTitle() + " died");

					// Death of any other entity than the player
					GridPositionComponent entityPos = Mappers.gridPositionComponent.get(entity);
					if (entityPos.room == room) {
						// Do not handle entities outside the current room
					
						// Drop reward
						LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(entity);
						LootUtil.dropItem(entity, lootRewardComponent, room);


						EnemyComponent enemyComponent = Mappers.enemyComponent.get(entity);
						if (enemyComponent != null) {
							// An enemy died
							for(Entity ally : room.getAllies()) {
								AIComponent aiComponent = Mappers.aiComponent.get(ally);
								if (aiComponent != null && aiComponent.getTarget() == entity) {
									aiComponent.onLoseTarget(ally, room);
								}
							}
						}
						
						AllyComponent allyComponent = Mappers.allyComponent.get(entity);
						if (allyComponent != null) {
							// An ally died
							for(Entity enemy : room.getEnemies()) {
								AIComponent aiComponent = Mappers.aiComponent.get(enemy);
								if (aiComponent != null && aiComponent.getTarget() == entity) {
									aiComponent.onLoseTarget(enemy, room);
								}
							}
						}
						
						// On death event
						Mappers.aiComponent.get(entity).onDeath(entity, healthCompo.getAttacker(), room);
						
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
							
							AIComponent attackerAIComponent = Mappers.aiComponent.get(healthCompo.getAttacker());
							if (attackerAIComponent != null) {
								attackerAIComponent.onKill(healthCompo.getAttacker(), entity, room);
							}
						}
						
						StatusReceiverComponent deadEntityStatusReceiverCompo = Mappers.statusReceiverComponent.get(entity);
						if (deadEntityStatusReceiverCompo != null) {
							for (Status status : deadEntityStatusReceiverCompo.getStatuses()) {
								status.onDeath(entity, room);
							}
						}
						
						VFXUtil.createDeathEffect(entity);
						
						if (enemyComponent != null) {
							room.removeEnemy(entity);
						} else {
							room.removeAlly(entity);
						}
						
						// If it was the player's turn, recompute movable tiles after the entity has been removed
						if (room.getState().canEndTurn()) {
							room.setNextState(RoomState.PLAYER_COMPUTE_MOVABLE_TILES);
						}
					
				    	healthCompo.setAttacker(null);

					}
					
				}
			
	    	}

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
