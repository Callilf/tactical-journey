package com.dokkaebistudio.tacticaljourney.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.interfaces.MovableInterface;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is an ally, meaning that it won't attack the player
 * but will attack enemies. Also, enemies will attack it.
 * @author Callil
 *
 */
public class AllyComponent implements Component, Poolable, MovableInterface {

	private Image marker = new Image(Assets.ally_marker.getRegion());
	
	
	
	public void showMarker(Entity ally) {
		GameScreen.fxStage.addActor(marker);
		this.place(Mappers.gridPositionComponent.get(ally).coord());
	}
	
	public void hideMarker() {
		marker.remove();
	}
	
	
	
	
	@Override
	public void reset() {
		marker.remove();
	}
	
	
	//**************************************
	// Movement

	@Override
	public void initiateMovement(Vector2 currentPos) {
		Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(currentPos);
		startPos.x = startPos.x + GameScreen.GRID_SIZE - marker.getWidth();
		startPos.y = startPos.y + marker.getHeight();
		marker.setPosition(startPos.x, startPos.y);
	}

	@Override
	public void performMovement(float xOffset, float yOffset) {
		marker.setPosition(marker.getX() + xOffset, marker.getY() + yOffset);
	}

	@Override
	public void endMovement(Vector2 finalPos) {
		Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(finalPos);
		startPos.x = startPos.x + GameScreen.GRID_SIZE - marker.getWidth();
		startPos.y = startPos.y + marker.getHeight();
		marker.setPosition(startPos.x, startPos.y);
	}

	@Override
	public void place(Vector2 tilePos) {
		Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(tilePos);
		startPos.x = startPos.x + GameScreen.GRID_SIZE - marker.getWidth();
		startPos.y = startPos.y + marker.getHeight();
		marker.setPosition(startPos.x, startPos.y);
	}
	
	
	
	// Getters and Setters
	
	public Image getMarker() {
		return marker;
	}

	public void setMarker(Image marker) {
		this.marker = marker;
	}
	
	
	
	
	public static Serializer<AllyComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<AllyComponent>() {

			@Override
			public void write(Kryo kryo, Output output, AllyComponent object) {}

			@Override
			public AllyComponent read(Kryo kryo, Input input, Class<AllyComponent> type) {
				AllyComponent compo = engine.createComponent(AllyComponent.class);
				return compo;
			}
		
		};
	}





}
