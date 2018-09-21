/**
 * 
 */
package com.dokkaebistudio.tacticaljourney;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.components.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.TextureComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent.TileEnum;
import com.dokkaebistudio.tacticaljourney.components.TransformComponent;
import com.dokkaebistudio.tacticaljourney.components.WheelComponent;

/**
 * Factory used to create presets of entities.
 * @author Callil
 *
 */
public final class EntityFactory {
	
	/** The gdx pooled engine. */
	public PooledEngine engine;
	
	// textures are stored so we don't fetch them from the atlas each time (atlas.findRegion is SLOW)
	private TextureAtlas.AtlasRegion playerTexture;
	private TextureAtlas.AtlasRegion wallTexture;
	private TextureAtlas.AtlasRegion pitTexture;
	private TextureAtlas.AtlasRegion mudTexture;
	private TextureAtlas.AtlasRegion groundTexture;


	/**
	 * Constructor.
	 * @param e the engine
	 */
	public EntityFactory(PooledEngine e) {
		this.engine = e;
		
		playerTexture = Assets.getTexture(Assets.player);
		wallTexture = Assets.getTexture(Assets.tile_wall);
		groundTexture = Assets.getTexture(Assets.tile_ground);
		pitTexture = Assets.getTexture(Assets.tile_pit);
		mudTexture = Assets.getTexture(Assets.tile_mud);
	}


	/**
	 * Create the player.
	 * @param pos the position
	 * @param moveSpeed the speed
	 * @return the player entity
	 */
	public Entity createPlayer(Vector2 pos, int moveSpeed) {
		Entity playerEntity = engine.createEntity();

		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		WheelComponent baseWheelComponent = engine.createComponent(WheelComponent.class);

		baseWheelComponent.addSector(15, WheelComponent.Hit.MISS);
		baseWheelComponent.addSector(5, WheelComponent.Hit.GRAZE);
		baseWheelComponent.addSector(10, WheelComponent.Hit.HIT);
		baseWheelComponent.addSector(2, WheelComponent.Hit.CRITICAL);
		baseWheelComponent.addSector(10, WheelComponent.Hit.HIT);
		baseWheelComponent.addSector(5, WheelComponent.Hit.GRAZE);

		gridPosition.coord.set(pos); // default position

		texture.region = this.playerTexture;

		playerEntity.add(position);
		playerEntity.add(texture);
		playerEntity.add(gridPosition);
		playerEntity.add(baseWheelComponent);
		// he's the player !
		PlayerComponent playerComponent = engine.createComponent(PlayerComponent.class);
		playerComponent.engine = this.engine;
		playerComponent.moveSpeed = moveSpeed;
		playerEntity.add(playerComponent);

		engine.addEntity(playerEntity);
		return playerEntity;
	}
	
	
	/**
	 * Create a tile with a given type at the given position
	 * @param pos the position
	 * @param type the type
	 * @return the tile entity
	 */
	public Entity createTile(Vector2 pos, TileEnum type) {
		Entity tileEntity = engine.createEntity();
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		TileComponent tile = engine.createComponent(TileComponent.class);

		tile.type = type;
		switch (type) {
			case WALL:
				texture.region = wallTexture;
				break;
			case GROUND:
				texture.region = groundTexture;
				break;
			case PIT:
				texture.region = pitTexture;
				break;
			case MUD:
				texture.region = mudTexture;
				break;
		}

		gridPosition.coord.set(pos);

		tileEntity.add(position);
		tileEntity.add(texture);
		tileEntity.add(gridPosition);
		tileEntity.add(tile);

		engine.addEntity(tileEntity);
		return tileEntity;
	}
	
	
	
	//TODO: blue tiles
	public Entity createMovableTile(Vector2 pos) {
		Entity movableTileEntity = engine.createEntity();
    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord.set(pos);
    	movableTileEntity.add(movableTilePos);
    	
    	TextureComponent texture = engine.createComponent(TextureComponent.class);
    	texture.region = Assets.getTexture(Assets.tile_movable);
    	movableTileEntity.add(texture);
    	
    	engine.addEntity(movableTileEntity);
    	return movableTileEntity;
	}
	
	
	/**
	 * Create the red cross that indicates the destination tile of the player.
	 * @param pos the position
	 * @return the destination tile entity
	 */
	public Entity createDestinationTile(Vector2 pos) {
		Entity redCross = engine.createEntity();
		
		GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord.set(pos);
    	redCross.add(movableTilePos);
    	TextureComponent texture = engine.createComponent(TextureComponent.class);
    	texture.region = Assets.getTexture(Assets.tile_movable_selected);
    	redCross.add(texture);
    	
    	engine.addEntity(redCross);
    	return redCross;
	}
	
}
