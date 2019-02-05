/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.items.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * Factory used to create presets of entities.
 * @author Callil
 *
 */
public final class ItemFactory {
	
	/** The gdx pooled engine. */
	public PooledEngine engine;
	
	/** the entity factory. */
	public EntityFactory entityFactory;
	
	// textures are stored so we don't fetch them from the atlas each time (atlas.findRegion is SLOW)
	private TextureAtlas.AtlasRegion smallHealthPotionTexture;
	private TextureAtlas.AtlasRegion tutorialPageTexture;

	/**
	 * Constructor.
	 * @param e the engine
	 */
	public ItemFactory(PooledEngine e, EntityFactory ef) {
		this.engine = e;
		this.entityFactory = ef;
		
		smallHealthPotionTexture = Assets.getTexture(Assets.health_up_item);
		tutorialPageTexture = Assets.getTexture(Assets.tutorial_page_item	);
	}
	

	/**
	 * Create a health up item that is consumed when picked up.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemHealthUp(Room room, Vector2 tilePos) {
		Entity healthUp = engine.createEntity();
		healthUp.flags = EntityFlagEnum.ITEM_HEALTH_UP.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(new Sprite( this.smallHealthPotionTexture));
		healthUp.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		if (tilePos != null) gridPosition.coord(healthUp, tilePos, room);
		gridPosition.zIndex = 8;
		healthUp.add(gridPosition);
		
		ItemComponent itemCompo = engine.createComponent(ItemComponent.class);
		itemCompo.setItemType(ItemEnum.CONSUMABLE_HEALTH_UP);
		healthUp.add(itemCompo);
		
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	healthUp.add(destructibleCompo);
		
		engine.addEntity(healthUp);

		return healthUp;
	}
	
	/**
	 * Create a tutorial page.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemTutorialPage(int pageNumber, Room room, Vector2 tilePos) {
		Entity item = engine.createEntity();
		item.flags = EntityFlagEnum.ITEM_TUTORIAL_PAGE.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(new Sprite( this.tutorialPageTexture));
		item.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		if (tilePos != null) gridPosition.coord(item, tilePos, room);
		gridPosition.zIndex = 8;
		item.add(gridPosition);
		
		ItemComponent itemCompo = engine.createComponent(ItemComponent.class);
		itemCompo.setItemType(ItemEnum.valueOf("TUTORIAL_PAGE_" + pageNumber));
		item.add(itemCompo);
		
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	item.add(destructibleCompo);
		
		engine.addEntity(item);

		return item;
	}
	
}
