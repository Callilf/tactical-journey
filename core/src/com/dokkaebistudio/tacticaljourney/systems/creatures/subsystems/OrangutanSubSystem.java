package com.dokkaebistudio.tacticaljourney.systems.creatures.subsystems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackSkill;
import com.dokkaebistudio.tacticaljourney.creature.enemies.EnemyOrangutan;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomCreatureState;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.systems.creatures.CreatureSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class OrangutanSubSystem extends CreatureSubSystem {
	
	public static final Vector2 LEFT_CLONE_TILE = new Vector2(3, 6);
	public static final Vector2 RIGHT_CLONE_TILE = new Vector2(19, 6);
	public static final Vector2 UP_CLONE_TILE = new Vector2(11, 10);
	public static final Vector2 DOWN_CLONE_TILE = new Vector2(11, 2);
	
	private EnemyOrangutan enemyOrangutan;
	
	private AttackSkill meleeSkill;
	private AttackSkill rangeSkill;
	private AttackSkill throwSkill;
	
	@Override
	public boolean update(final CreatureSystem creatureSystem, final Entity enemy, final Room room) {
		if (enemyOrangutan == null) {
			enemyOrangutan = (EnemyOrangutan) Mappers.aiComponent.get(enemy).getType();
			for (AttackSkill as : Mappers.attackComponent.get(enemy).getSkills()) {
				switch(as.getAttackType()) {
				case MELEE:
					meleeSkill = as;
					break;
				case RANGE:
					rangeSkill = as;
					break;
				case THROW:
					throwSkill = as;
					break;
					default:
				}
			}
		}
		final Vector2 playerPos = Mappers.gridPositionComponent.get(GameScreen.player).coord();
		
		switch(room.getCreatureState()) {
		
		case TURN_INIT:
			
			
			break;

    	case MOVE_TILES_DISPLAYED:

    		if (enemyOrangutan.isSleeping()) {
        		for(Tile t : Mappers.attackComponent.get(enemy).allAttackableTiles) {
        			if (t.getGridPos().equals(playerPos)) {
        				// Sees the player
        				enemyOrangutan.setSleeping(false);
        				break;
        			}
        		}
    		}
    			
    		if (enemyOrangutan.isSleeping()) {
    			room.setCreatureState(RoomCreatureState.END_MOVEMENT);
    			return true;
    		}
    		
    		
    		break;
    		

    	case ATTACK:
    		
    		break;
    		
    	case ATTACK_FINISH:
    		if (!enemyOrangutan.isSleeping()) {
    			Mappers.stateComponent.get(enemy).set(StatesEnum.STANDING);
    		}
			break;
			
    	default:
    	}
		
		return false;
	}

	

}
