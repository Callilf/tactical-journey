package com.dokkaebistudio.tacticaljourney.ces.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.ces.components.interfaces.MarkerInterface;
import com.dokkaebistudio.tacticaljourney.ces.components.interfaces.MovableInterface;
import com.dokkaebistudio.tacticaljourney.ces.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class PanelComponent implements Component, Poolable, MovableInterface, MarkerInterface, RoomSystem {
	
	public static final int MAX_TURN = 9999;

	/** The room.*/
	public Room room;
	
	/** The displayer that shows the the alerted state. */
	private Container<Label> textContainer = new Container<>();
	private Label text = new Label("0", PopinService.hudStyle());

	public void init() {
		textContainer.setActor(text);
		textContainer.setTransform(true);
		textContainer.setOrigin(Align.center);
		textContainer.addAction(Actions.scaleTo(1.2f, 1.2f));
		textContainer.addAction(Actions.rotateTo(-7f));
		textContainer.pack();
	}
	
	public void updateText(int turns, int turnThreshold) {	
		String color = turns <= turnThreshold ? "[WHITE]" : "[LIGHT_GRAY]";
		text.setText(turns <= MAX_TURN ? color + turns : color + MAX_TURN);
	}
	
	
	
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}
	
	@Override
	public void reset() {
		if (text != null) {
			text.setText("0");
			textContainer.remove();		
		}
	}
	
	
	@Override
	public void showMarker(Entity ally) {		
		GameScreen.fxStage.addActor(textContainer);
		this.place(Mappers.gridPositionComponent.get(ally).coord());
	}
	
	@Override
	public void hideMarker() {
		textContainer.remove();
	}
	
	
	
	//**************************************
	// Movement

	@Override
	public void initiateMovement(Vector2 currentPos) {
		if (textContainer != null) {
			text.layout();
			textContainer.pack();
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(currentPos);
			startPos.x = startPos.x + GameScreen.GRID_SIZE/2 - textContainer.getWidth()/2 - 4;
			startPos.y = startPos.y + GameScreen.GRID_SIZE/2 - 2;
			textContainer.setPosition(startPos.x, startPos.y);
		}
	}



	@Override
	public void performMovement(float xOffset, float yOffset) {
		if (textContainer != null) {
			textContainer.setPosition(textContainer.getX() + xOffset, textContainer.getY() + yOffset);
		}
	}



	@Override
	public void endMovement(Vector2 finalPos) {
		if (textContainer != null) {
			text.layout();
			textContainer.pack();
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(finalPos);
			startPos.x = startPos.x + GameScreen.GRID_SIZE/2 - textContainer.getWidth()/2 - 4;
			startPos.y = startPos.y + GameScreen.GRID_SIZE/2 - 2;
			textContainer.setPosition(startPos.x, startPos.y);
		}
	}

	@Override
	public void place(Vector2 tilePos) {
		if (text != null) {
			text.layout();
			textContainer.pack();
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(tilePos);
			startPos.x = startPos.x + GameScreen.GRID_SIZE/2 - textContainer.getWidth()/2 - 4;
			startPos.y = startPos.y + GameScreen.GRID_SIZE/2 - 2;
			textContainer.setPosition(startPos.x, startPos.y);
		}
	}
		
	

	
	
	
	// Getters and Setters

	
	public Label getText() {
		return text;
	}

	
	
	public static Serializer<PanelComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<PanelComponent>() {

			@Override
			public void write(Kryo kryo, Output output, PanelComponent object) {
				output.writeString(object.text.getText().toString());
			}

			@Override
			public PanelComponent read(Kryo kryo, Input input, Class<? extends PanelComponent> type) {
				PanelComponent compo = engine.createComponent(PanelComponent.class);
				compo.init();
				compo.text.setText(input.readString());
				return compo;
			}
		
		};
	}


}
