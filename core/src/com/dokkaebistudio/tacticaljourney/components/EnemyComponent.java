package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.interfaces.MovableInterface;
import com.dokkaebistudio.tacticaljourney.enemies.Enemy;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyMoveStrategy;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.systems.enemies.EnemySubSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class EnemyComponent implements Component, Poolable, MovableInterface, RoomSystem {
	
	/** The room.*/
	public Room room;
	
	/** The type of enemy. */
	private Enemy type;
	private EnemySubSystem subSystem;
	
	
	
	/** The faction of this enemy. Enemies from the same faction won't attack
	 * themselves and can have synergies. */
	private EnemyFactionEnum faction;
	
	/** The movement pattern of this enemy when not alerted. */
	private EnemyMoveStrategy basicMoveStrategy;
	
	/** The movement pattern of this enemy when alerted. */
	private EnemyMoveStrategy alertedMoveStrategy;

	
	
	//****************
	// Alerted state
	
	/** Whether the enemy is alerted, which means that he will chase the player. */
	private boolean alerted = false;
	
	/** The displayer that shows the the alerted state. */
	private Entity alertedDisplayer;

	
	
	
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}
	
	@Override
	public void reset() {
		if (alertedDisplayer != null) {
			room.removeEntity(alertedDisplayer);		
		}
		alertedDisplayer = null;
		setAlerted(false);
		type = null;
		subSystem = null;
	}
	
	
	
	
	//************************
	// Events
	
	public void onStartTurn(Entity enemy, Room room) {
		this.type.onStartTurn(enemy, room);
	}
	
	public void onEndTurn(Entity enemy, Room room) {
		this.type.onEndTurn(enemy, room);
	}
	
	
	
	public void onAttack(Entity enemy, Entity target, Room room) {
		this.type.onAttack(enemy, target, room);
	}
	
	public void onReceiveDamage(Entity enemy, Entity attacker, Room room) {
		this.type.onReceiveDamage(enemy, attacker, room);
	}
	
	public void onDeath(Entity enemy, Entity attacker, Room room) {
		this.type.onDeath(enemy, attacker, room);
	}
	
	
	
	
	
	
	
	//**************************************
	// Movement

	@Override
	public void initiateMovement(Vector2 currentPos) {
		
		if (alertedDisplayer != null) {
			TextComponent textCompo = Mappers.textComponent.get(alertedDisplayer);
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(alertedDisplayer);
			
			//Add the tranfo component to the entity to perform real movement on screen
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(currentPos);
			startPos.x = startPos.x + GameScreen.GRID_SIZE/2 - textCompo.getWidth()/2;
			startPos.y = startPos.y + GameScreen.GRID_SIZE;
			gridPositionComponent.absolutePos((int)startPos.x, (int)startPos.y);
			gridPositionComponent.coord(currentPos);
		}
	}



	@Override
	public void performMovement(float xOffset, float yOffset) {
		if (alertedDisplayer != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(alertedDisplayer);
			gridPositionComponent.absolutePos((int)(gridPositionComponent.getAbsolutePos().x + xOffset), 
					(int)(gridPositionComponent.getAbsolutePos().y + yOffset));
		}
	}



	@Override
	public void endMovement(Vector2 finalPos) {
		if (alertedDisplayer != null) {
			TextComponent textCompo = Mappers.textComponent.get(alertedDisplayer);
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(alertedDisplayer);
			
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(finalPos);
			startPos.x = startPos.x + GameScreen.GRID_SIZE/2 - textCompo.getWidth()/2;
			startPos.y = startPos.y + GameScreen.GRID_SIZE;
			gridPositionComponent.absolutePos((int)startPos.x, (int)startPos.y);
			gridPositionComponent.coord(finalPos);
		}
	}

	@Override
	public void place(Vector2 tilePos) {
		if (alertedDisplayer != null) {
			TextComponent textCompo = Mappers.textComponent.get(alertedDisplayer);
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(alertedDisplayer);
			
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(tilePos);
			startPos.x = startPos.x + GameScreen.GRID_SIZE/2 - textCompo.getWidth()/2;
			startPos.y = startPos.y + GameScreen.GRID_SIZE;
			gridPositionComponent.absolutePos((int)startPos.x, (int)startPos.y);
			gridPositionComponent.coord(tilePos);
		}
	}
	
	
	
	
	// Getters and Setters

	public EnemyMoveStrategy getBasicMoveStrategy() {
		return basicMoveStrategy;
	}

	public void setBasicMoveStrategy(EnemyMoveStrategy moveStrategy) {
		this.basicMoveStrategy = moveStrategy;
	}

	public EnemyMoveStrategy getAlertedMoveStrategy() {
		return alertedMoveStrategy != null ? alertedMoveStrategy : basicMoveStrategy;
	}

	public void setAlertedMoveStrategy(EnemyMoveStrategy alertedMoveStrategy) {
		this.alertedMoveStrategy = alertedMoveStrategy;
	}

	public EnemyFactionEnum getFaction() {
		return faction;
	}

	public void setFaction(EnemyFactionEnum faction) {
		this.faction = faction;
	}

	public boolean isAlerted() {
		return alerted;
	}

	public void setAlerted(boolean alerted) {
		this.alerted = alerted;
		if (alertedDisplayer != null) {
			TextComponent textComponent = Mappers.textComponent.get(alertedDisplayer);
			textComponent.setText(this.alerted ? "[WHITE]!!" : "");
			
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(alertedDisplayer);
			this.place(gridPositionComponent.coord());
		}
	}

	public Entity getAlertedDisplayer() {
		return alertedDisplayer;
	}

	public void setAlertedDisplayer(Entity alertedDisplayer) {
		this.alertedDisplayer = alertedDisplayer;
	}

	public Enemy getType() {
		return type;
	}

	public void setType(Enemy type) {
		this.type = type;
	}

	public EnemySubSystem getSubSystem() {
		return subSystem;
	}

	public void setSubSystem(EnemySubSystem subSystem) {
		this.subSystem = subSystem;
	}

}
