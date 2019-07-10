package com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.ces.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AllyComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.NamedSystem;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.singletons.GameTimeSingleton;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.LootUtil;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

public class HealthSystem extends NamedSystem {
	    
	public GameScreen gameScreen;
	public Stage fxStage;
	
    /** The entities with an health component of the current room. */
    private List<Entity> allEntitiesOfCurrentRoom = new ArrayList<>();
	
    private List<Float> offsetTimes = new ArrayList<>();

    public HealthSystem(GameScreen gameScreen, Room r, Stage s) {
		this.priority = 22;
		this.gameScreen = gameScreen;
        this.room = r;
        this.fxStage = s;
    }

    @Override
	public void performUpdate(float deltaTime) {
    	fillEntitiesOfCurrentRoom();
    	
    	float elapsedTime = GameTimeSingleton.getInstance().getElapsedTime();
    	offsetTimes.removeIf(f -> f < elapsedTime);
    	
    	for (Entity entity : allEntitiesOfCurrentRoom) {

    		HealthComponent healthCompo = Mappers.healthComponent.get(entity);
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
		    		case HEALED:						
		    		case RESISTANT:
		    		case ARMOR:
		    		case KNOCKBACK:
		    			VFXUtil.createDamageDisplayer(displayValue, gridPos.coord(), 
		    					healthChange,offsetTimes.size() * -20, room);
		
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
				if (entity == GameScreen.player) {
					handlePlayerDeath(healthCompo);
				} else {
					handleEntityDeath(entity, healthCompo);
				}
			
	    	}
	
    	}
    }

    /**
     * An ally or an enemy's hp reached 0.
     * @param entity the entity that has 0 hp
     * @param healthCompo the health component of this entity
     */
	private void handleEntityDeath(Entity entity, HealthComponent healthCompo) {
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

	
    /**
     * The player's hp reached 0.
     * @param entity the player that has 0 hp
     * @param healthCompo the health component of this entity
     */
	private void handlePlayerDeath(HealthComponent healthCompo) {
		AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(GameScreen.player);
		if (alterationReceiverComponent != null) {
			alterationReceiverComponent.onDeath(GameScreen.player, healthCompo.getAttacker(), room);
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
			gameScreen.killerName = killerString;
		}
		
		healthCompo.setAttacker(null);
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
	
	
	private void fillEntitiesOfCurrentRoom() {
		allEntitiesOfCurrentRoom.clear();
		for (Entity e : room.getAllEntities()) {
			if (Mappers.healthComponent.has(e)) allEntitiesOfCurrentRoom.add(e);
		}
	}
	
}
