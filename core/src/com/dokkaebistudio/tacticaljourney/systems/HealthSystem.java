package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.components.display.DamageDisplayComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class HealthSystem extends IteratingSystem implements RoomSystem {
	    
	public GameScreen gameScreen;
	public Stage stage;
	
	/** The current room. */
    private Room room;    

    public HealthSystem(GameScreen gameScreen, Room r, Stage s) {
        super(Family.one(HealthComponent.class, DamageDisplayComponent.class).get());
		this.priority = 21;

		this.gameScreen = gameScreen;
        this.room = r;
        this.stage = s;
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
							gridPos.coord(), false, 0, room);
	
	    			break;
	    		case HEALED:
					room.entityFactory.createDamageDisplayer(String.valueOf(healthCompo.getHealthRecoveredAtCurrentFrame()), 
							gridPos.coord(), true, 0, room);
	
	    			break;
	    		default:
	    		}

	    		healthCompo.setReceivedDamageLastTurn(true);
	    		healthCompo.clearModified();
	    	}
    	
    	
	    	// Handle death
	    	if (healthCompo.getHp() <= 0) {
				//Entity is dead
				
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
					room.removeEnemy(entity);
					//TODO: play death animation
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

    
    
	//********************************
	// Private methods
	
	private ExperienceComponent getExperienceComponent(Entity attacker) {
		ExperienceComponent result = null;
		result = Mappers.experienceComponent.get(attacker);
		if (result == null) {
			ParentEntityComponent parentEntityComponent = Mappers.parentEntityComponent.get(attacker);
			if (parentEntityComponent != null) {
				Entity parent = parentEntityComponent.getParent();
				result = getExperienceComponent(parent);
			}
		}
		
		return result;
	}
}
