package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.interfaces.MovableInterface;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyMoveStrategy;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.systems.enemies.EnemySubSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class AIComponent implements Component, Poolable, MovableInterface, RoomSystem {
	
	/** The room.*/
	public Room room;
	
	private EnemySubSystem subSystem;
	
	/** Whether this enemy's turn is over or not. */
	private boolean turnOver;
	
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
	
	/** The target this enemy is focusing on. */
	private Entity target;

	
	
	
	
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
		setAlerted(false, null, null);
		subSystem = null;
		turnOver = false;
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
			startPos.x = startPos.x + GameScreen.GRID_SIZE - textCompo.getWidth();
			startPos.y = startPos.y + textCompo.getHeight();
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
			startPos.x = startPos.x + GameScreen.GRID_SIZE - textCompo.getWidth();
			startPos.y = startPos.y + textCompo.getHeight();
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
			startPos.x = startPos.x + GameScreen.GRID_SIZE - textCompo.getWidth();
			startPos.y = startPos.y + textCompo.getHeight();
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

	public boolean isAlerted() {
		return alerted;
	}

	public void setAlerted(boolean alerted, Entity enemy, Entity target) {
		if (!this.alerted && alerted && enemy != null) {
			//First time alerted
			EnemyComponent enemyComponent = Mappers.enemyComponent.get(enemy);
			if (enemyComponent != null) {
				enemyComponent.getType().onAlerted(enemy, GameScreen.player, room);
			}
		}
		
		if (target != null) {
			this.target = target;
		}
		
		this.alerted = alerted;
		if (alertedDisplayer != null) {
			TextComponent textComponent = Mappers.textComponent.get(alertedDisplayer);
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(alertedDisplayer);
			if (textComponent != null && gridPositionComponent != null) {
				String color = "[WHITE]";
				if (this.target != GameScreen.player) {
					color = "[GREEN]";
				}
				
				textComponent.setText(this.alerted ? color + "!!" : "");
				
				this.place(gridPositionComponent.coord());
			}
		}
	}

	public Entity getAlertedDisplayer() {
		return alertedDisplayer;
	}

	public void setAlertedDisplayer(Entity alertedDisplayer) {
		this.alertedDisplayer = alertedDisplayer;
	}

	public EnemySubSystem getSubSystem() {
		return subSystem;
	}

	public void setSubSystem(EnemySubSystem subSystem) {
		this.subSystem = subSystem;
	}

	public boolean isTurnOver() {
		return turnOver;
	}

	public void setTurnOver(boolean turnOver) {
		this.turnOver = turnOver;
	}

	public Entity getTarget() {
		return target;
	}

	public void setTarget(Entity target) {
		this.target = target;
	}
	
	
	
	public static Serializer<AIComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<AIComponent>() {

			@Override
			public void write(Kryo kryo, Output output, AIComponent object) {
				
				kryo.writeClassAndObject(output, object.subSystem);
				
				output.writeBoolean(object.turnOver);

				output.writeString(object.basicMoveStrategy.name());
				output.writeString(object.alertedMoveStrategy.name());

				kryo.writeClassAndObject(output, object.alertedDisplayer);
				output.writeBoolean(object.alerted);
				kryo.writeClassAndObject(output, object.target);
			}

			@Override
			public AIComponent read(Kryo kryo, Input input, Class<AIComponent> type) {
				AIComponent compo = engine.createComponent(AIComponent.class);
				
				compo.subSystem = (EnemySubSystem) kryo.readClassAndObject(input);
				
				compo.turnOver = input.readBoolean();
				
				compo.basicMoveStrategy = EnemyMoveStrategy.valueOf(input.readString());
				compo.alertedMoveStrategy = EnemyMoveStrategy.valueOf(input.readString());
				
				compo.alertedDisplayer = (Entity) kryo.readClassAndObject(input);
				compo.setAlerted(input.readBoolean(), null, null);
				compo.target = (Entity) kryo.readClassAndObject(input);
//				engine.addEntity(compo.alertedDisplayer);
				
				return compo;
			}
		
		};
	}

}
