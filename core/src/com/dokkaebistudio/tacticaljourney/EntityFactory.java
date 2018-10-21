/**
 * 
 */
package com.dokkaebistudio.tacticaljourney;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent.EnemyMoveStrategy;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent.TileEnum;
import com.dokkaebistudio.tacticaljourney.components.WheelComponent;
import com.dokkaebistudio.tacticaljourney.components.display.DamageDisplayComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.items.ItemEnum;
import com.dokkaebistudio.tacticaljourney.systems.display.RenderingSystem;

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
	private TextureAtlas.AtlasRegion spiderTexture;
	private TextureAtlas.AtlasRegion scorpionTexture;
	private TextureAtlas.AtlasRegion wallTexture;
	private TextureAtlas.AtlasRegion pitTexture;
	private TextureAtlas.AtlasRegion mudTexture;
	private TextureAtlas.AtlasRegion groundTexture;
	
	private TextureAtlas.AtlasRegion healthUpTexture;


	/**
	 * Constructor.
	 * @param e the engine
	 */
	public EntityFactory(PooledEngine e) {
		this.engine = e;
		
		playerTexture = Assets.getTexture(Assets.player);
		spiderTexture = Assets.getTexture(Assets.enemy_spider);
		scorpionTexture = Assets.getTexture(Assets.enemy_scorpion);
		wallTexture = Assets.getTexture(Assets.tile_wall);
		groundTexture = Assets.getTexture(Assets.tile_ground);
		pitTexture = Assets.getTexture(Assets.tile_pit);
		mudTexture = Assets.getTexture(Assets.tile_mud);
		healthUpTexture = Assets.getTexture(Assets.health_up_item);
	}


	/**
	 * Create the player.
	 * @param pos the position
	 * @param moveSpeed the speed
	 * @return the player entity
	 */
	public Entity createPlayer(Vector2 pos, int moveSpeed) {
		Entity playerEntity = engine.createEntity();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(new Sprite(this.playerTexture));
		playerEntity.add(spriteCompo);
		
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord.set(pos);
		gridPosition.zIndex = 10;
		playerEntity.add(gridPosition);
		
		WheelComponent baseWheelComponent = engine.createComponent(WheelComponent.class);
		baseWheelComponent.addSector(75, WheelComponent.Hit.HIT);
		baseWheelComponent.addSector(10, WheelComponent.Hit.MISS);
		baseWheelComponent.addSector(10, WheelComponent.Hit.CRITICAL);
		baseWheelComponent.addSector(10, WheelComponent.Hit.MISS);
		baseWheelComponent.addSector(75, WheelComponent.Hit.HIT);
		baseWheelComponent.addSector(20, WheelComponent.Hit.GRAZE);
		baseWheelComponent.addSector(140, WheelComponent.Hit.MISS);
		baseWheelComponent.addSector(20, WheelComponent.Hit.GRAZE);
		playerEntity.add(baseWheelComponent);
		
		// he's the player !
		PlayerComponent playerComponent = engine.createComponent(PlayerComponent.class);
		playerComponent.engine = this.engine;
		playerComponent.setEndTurnButton(createEndTurnButton(new Vector2(0.0f, 0.0f)));
		playerEntity.add(playerComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.engine = this.engine;
		moveComponent.moveSpeed = moveSpeed;
		playerEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.engine = this.engine;
		attackComponent.setRangeMax(1);
		attackComponent.setStrength(4);
		playerEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		playerEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.engine = engine;
		healthComponent.setMaxHp(100);
		healthComponent.setHp(100);
		healthComponent.setHpDisplayer(this.createTextOnTile(pos, String.valueOf(healthComponent.getHp()), 100));
		playerEntity.add(healthComponent);

		engine.addEntity(playerEntity);
		return playerEntity;
	}
	
	
	/**
	 * Create the end turn button.
	 * @param pos the position
	 * @return the end turn button entity
	 */
	public Entity createEndTurnButton(Vector2 pos) {
		Entity endTurnButton = engine.createEntity();
		
		TransformComponent transfoCompo = engine.createComponent(TransformComponent.class);
		transfoCompo.pos.set(pos.x, pos.y, 10);
		endTurnButton.add(transfoCompo);
		    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(new Sprite(Assets.getTexture(Assets.btn_end_turn)));
    	endTurnButton.add(spriteCompo);
    	
    	engine.addEntity(endTurnButton);
    	return endTurnButton;
	}
	
	
	/**
	 * Create a tile with a given type at the given position
	 * @param pos the position
	 * @param type the type
	 * @return the tile entity
	 */
	public Entity createTile(Vector2 pos, TileEnum type) {
		Entity tileEntity = engine.createEntity();
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		TileComponent tile = engine.createComponent(TileComponent.class);

		tile.type = type;
		switch (type) {
			case WALL:
				spriteCompo.setSprite(new Sprite(wallTexture));
				break;
			case GROUND:
				spriteCompo.setSprite(new Sprite(groundTexture));
				break;
			case PIT:
				spriteCompo.setSprite(new Sprite(pitTexture));
				break;
			case MUD:
				spriteCompo.setSprite(new Sprite(mudTexture));
				break;
		}

		gridPosition.coord.set(pos);
		gridPosition.zIndex = 1;

		tileEntity.add(spriteCompo);
		tileEntity.add(gridPosition);
		tileEntity.add(tile);

		engine.addEntity(tileEntity);
		return tileEntity;
	}
	
	
	
	public Entity createMovableTile(Vector2 pos) {
		Entity movableTileEntity = engine.createEntity();
    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord.set(pos);
    	movableTilePos.zIndex = 2;
    	movableTileEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(new Sprite(Assets.getTexture(Assets.tile_movable)));
    	movableTileEntity.add(spriteCompo);
    	
    	engine.addEntity(movableTileEntity);
    	return movableTileEntity;
	}
	
	public Entity createAttackableTile(Vector2 pos) {
		Entity attackableTileEntity = engine.createEntity();
    	GridPositionComponent attackableTilePos = engine.createComponent(GridPositionComponent.class);
    	attackableTilePos.coord.set(pos);
    	attackableTilePos.zIndex = 2;
    	attackableTileEntity.add(attackableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(new Sprite(Assets.getTexture(Assets.tile_attackable)));
    	attackableTileEntity.add(spriteCompo);
    	
    	engine.addEntity(attackableTileEntity);
    	return attackableTileEntity;
	}
	
	
	/**
	 * Create the red cross that indicates the destination tile of the player.
	 * @param pos the position
	 * @return the destination tile entity
	 */
	public Entity createDestinationTile(Vector2 pos) {
		Entity redCross = engine.createEntity();
		
		GridPositionComponent selectedTilePos = engine.createComponent(GridPositionComponent.class);
    	selectedTilePos.coord.set(pos);
    	selectedTilePos.zIndex = 2;
    	redCross.add(selectedTilePos);
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(new Sprite(Assets.getTexture(Assets.tile_movable_selected)));
    	redCross.add(spriteCompo);
    	
    	engine.addEntity(redCross);
    	return redCross;
	}
	
	
	/**
	 * Create a waypoint.
	 * @param pos the position
	 * @return the waypoint entity
	 */
	public Entity createWaypoint(Vector2 pos) {
		Entity confirmButton = engine.createEntity();
		
		GridPositionComponent waypointPos = engine.createComponent(GridPositionComponent.class);
    	waypointPos.coord.set(pos);
    	waypointPos.zIndex = 2;
    	confirmButton.add(waypointPos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(new Sprite(Assets.getTexture(Assets.tile_movable_waypoint)));
    	confirmButton.add(spriteCompo);
    	
    	engine.addEntity(confirmButton);
    	return confirmButton;
	}
	
//	/**
//	 * Create the movement confirmation button.
//	 * @param pos the position
//	 * @return the confirmation button entity
//	 */
//	public Entity createMoveConfirmationButton(Vector2 pos) {
//		Entity confirmButton = engine.createEntity();
//		
//		GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
//    	movableTilePos.coord.set(pos);
//    	confirmButton.add(movableTilePos);
//    	
//    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
//    	spriteCompo.setSprite(new Sprite(Assets.getTexture(Assets.btn_move_confirmation)));
//    	confirmButton.add(spriteCompo);
//    	
//    	engine.addEntity(confirmButton);
//    	return confirmButton;
//	}
	
	

	/**
	 * Create an enemy.
	 * @param pos the position
	 * @param moveSpeed the speed
	 * @return the enemy entity
	 */
	public Entity createSpider(Vector2 pos, int speed) {
		Entity enemyEntity = engine.createEntity();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(new Sprite(this.spiderTexture));
		enemyEntity.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord.set(pos);
		gridPosition.zIndex = 9;
		enemyEntity.add(gridPosition);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.engine = this.engine;
		enemyComponent.setMoveStrategy(EnemyMoveStrategy.MOVE_RANDOMLY);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.engine = this.engine;
		moveComponent.moveSpeed = speed;
		enemyEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.engine = this.engine;
		attackComponent.setRangeMax(1);
		attackComponent.setStrength(5);
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.engine = engine;
		healthComponent.setMaxHp(10);
		healthComponent.setHp(10);
		healthComponent.setHpDisplayer(this.createTextOnTile(pos, String.valueOf(healthComponent.getHp()), 100));
		enemyEntity.add(healthComponent);

		engine.addEntity(enemyEntity);
		return enemyEntity;
	}
	
	/**
	 * Create a scorpion.
	 * @param pos the position
	 * @param moveSpeed the speed
	 * @return the enemy entity
	 */
	public Entity createScorpion(Vector2 pos, int speed) {
		Entity enemyEntity = engine.createEntity();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(new Sprite(this.scorpionTexture));
		enemyEntity.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord.set(pos);
		gridPosition.zIndex = 9;
		enemyEntity.add(gridPosition);
		
		EnemyComponent enemyComponent = engine.createComponent(EnemyComponent.class);
		enemyComponent.engine = this.engine;
		enemyComponent.setMoveStrategy(EnemyMoveStrategy.MOVE_TOWARD_PLAYER);
		enemyEntity.add(enemyComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.engine = this.engine;
		moveComponent.moveSpeed = speed;
		enemyEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.engine = this.engine;
		attackComponent.setRangeMax(1);
		attackComponent.setStrength(10);
		enemyEntity.add(attackComponent);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		enemyEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.engine = engine;
		healthComponent.setMaxHp(15);
		healthComponent.setHp(15);
		healthComponent.setHpDisplayer(this.createTextOnTile(pos, String.valueOf(healthComponent.getHp()), 100));
		enemyEntity.add(healthComponent);

		engine.addEntity(enemyEntity);
		return enemyEntity;
	}
	
	/**
	 * Create a text that will be displayed on screen.
	 * @param pos the position of the text
	 * @return the text entity
	 */
	public Entity createText(Vector3 pos, String text) {
		Entity textTest = engine.createEntity();
		TransformComponent transfoCompo = new TransformComponent();
		transfoCompo.pos.set(pos);
		textTest.add(transfoCompo);
		
		TextComponent tc = new TextComponent(Assets.font);
		tc.setText(text);
		textTest.add(tc);
		engine.addEntity(textTest);
		return textTest;
	}
	
	/**
	 * Create a text that will be displayed on a tile.
	 * @param pos the position of the text in tile position
	 * @return the text entity
	 */
	public Entity createTextOnTile(Vector2 tilePos, String text, int zIndex) {
		Entity textTest = engine.createEntity();
		GridPositionComponent gridPositionComponent = new GridPositionComponent();
		gridPositionComponent.coord.set(tilePos);
		gridPositionComponent.zIndex = zIndex;
		textTest.add(gridPositionComponent);
		
		TextComponent tc = new TextComponent(Assets.font);
		tc.setText(text);
		textTest.add(tc);
		engine.addEntity(textTest);
		return textTest;
	}
	
	/**
	 * Create a damage displayer.
	 * @param damage the damage to display
	 * @param initialPos the initial position
	 * @return the damage displayer entity
	 */
	public Entity createDamageDisplayer(String damage, Vector2 gridPos, boolean heal) {
		Entity display = engine.createEntity();
		
		Vector2 initialPos = RenderingSystem.convertGridPosIntoPixelPos(gridPos);
		initialPos.add(GameScreen.GRID_SIZE/2, GameScreen.GRID_SIZE);
		
		DamageDisplayComponent displayCompo = engine.createComponent(DamageDisplayComponent.class);
		displayCompo.setInitialPosition(initialPos);
		display.add(displayCompo);
		
		TransformComponent transfoCompo = engine.createComponent(TransformComponent.class);
		transfoCompo.pos.set(initialPos, 100);
		display.add(transfoCompo);
		
		TextComponent textCompo = engine.createComponent(TextComponent.class);
		if (heal) {
			textCompo.setFont(Assets.greenFont);
		} else {
			textCompo.setFont(Assets.redFont);
		}
		textCompo.setText(damage);
		display.add(textCompo);
		
		engine.addEntity(display);
		return display;
	}
	
	
	/**
	 * Create a health up item that is consumed when picked up.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemHealthUp(Vector2 tilePos) {
		Entity healthUp = engine.createEntity();
		
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(new Sprite(this.healthUpTexture));
		healthUp.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord.set(tilePos);
		gridPosition.zIndex = 6;
		healthUp.add(gridPosition);
		
		ItemComponent itemCompo = engine.createComponent(ItemComponent.class);
		itemCompo.setItemType(ItemEnum.CONSUMABLE_HEALTH_UP);
		healthUp.add(itemCompo);
		
		engine.addEntity(healthUp);
		return healthUp;
	}
	
}
