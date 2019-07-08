package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ashley.PublicEntity;
import com.dokkaebistudio.tacticaljourney.components.interfaces.MarkerInterface;
import com.dokkaebistudio.tacticaljourney.components.interfaces.MovableInterface;
import com.dokkaebistudio.tacticaljourney.dialog.AbstractDialogs;
import com.dokkaebistudio.tacticaljourney.dialog.Dialog;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Indicate that this entity can speak with the player.
 * @author Callil
 */
public class SpeakerComponent implements Component, Poolable, MovableInterface, MarkerInterface {

	private AbstractDialogs dialogs;
	private Image marker = new Image(Assets.speaker_marker.getRegion());
	private Vector2 markerPositionOffset;

	
	@Override
	public void reset() {
		markerPositionOffset = null;
	}
	

	@Override
	public void showMarker(Entity speaker) {
		if (marker != null) {
			if (markerPositionOffset == null) {
				markerPositionOffset = new Vector2(GameScreen.GRID_SIZE/2 - marker.getWidth()/2, Mappers.spriteComponent.get(speaker).getSprite().getHeight());
			}
			
			GameScreen.fxStage.addActor(marker);
			this.place(Mappers.gridPositionComponent.get(speaker).coord());
			marker.clearActions();
			marker.addAction(Actions.forever(Actions.sequence(
					Actions.moveBy(0, 20, 1f, Interpolation.pow2), 
					Actions.moveBy(0, -20, 1f, Interpolation.pow2))));
		}
	}


	@Override
	public void hideMarker() {
		marker.remove();		
	}

	@Override
	public void initiateMovement(Vector2 currentPos) {
		if (marker != null) {
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(currentPos);
			updatePosition(startPos);
			marker.setPosition(startPos.x, startPos.y);
		}
	}

	@Override
	public void performMovement(float xOffset, float yOffset) {
		if (marker != null) {
			marker.setPosition(marker.getX() + xOffset, marker.getY() + yOffset);
		}
	}

	@Override
	public void endMovement(Vector2 finalPos) {
		if (marker != null) {
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(finalPos);
			updatePosition(startPos);			
			marker.setPosition(startPos.x, startPos.y);
		}
	}

	@Override
	public void place(Vector2 tilePos) {
		if (marker != null) {
			Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(tilePos);
			updatePosition(startPos);
			marker.setPosition(startPos.x, startPos.y);
		}
	}


	private void updatePosition(Vector2 startPos) {
		if (markerPositionOffset == null) {
			startPos.x = startPos.x + GameScreen.GRID_SIZE/2 - marker.getWidth()/2;
			startPos.y = startPos.y + GameScreen.GRID_SIZE;
		} else {
			startPos.x = startPos.x + markerPositionOffset.x;
			startPos.y = startPos.y + markerPositionOffset.y;
		}
	}
	
	
	
	public Dialog getSpeech(Entity speakerEntity) {
		marker.setDrawable(new TextureRegionDrawable(Assets.speaker_marker_gray.getRegion()));
		return this.dialogs.getDialog((PublicEntity) speakerEntity);
	}
	
	public Dialog getSpeech(String tag) {
		marker.setDrawable(new TextureRegionDrawable(Assets.speaker_marker_gray.getRegion()));
		return this.dialogs.getDialog(tag);
	}
	
	public void setDialogs(AbstractDialogs dialogs) {
		this.dialogs = dialogs;
	}
	
	
	public static Serializer<SpeakerComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<SpeakerComponent>() {

			@Override
			public void write(Kryo kryo, Output output, SpeakerComponent object) {
				kryo.writeClassAndObject(output, object.dialogs);
				output.writeBoolean(object.marker == null);
			}

			@Override
			public SpeakerComponent read(Kryo kryo, Input input, Class<? extends SpeakerComponent> type) {
				SpeakerComponent compo = engine.createComponent(SpeakerComponent.class);
				compo.dialogs = (AbstractDialogs) kryo.readClassAndObject(input);
				
				boolean markerHidden = input.readBoolean();
				if (markerHidden) compo.marker = null;

				return compo;
			}
		
		};
	}


}
