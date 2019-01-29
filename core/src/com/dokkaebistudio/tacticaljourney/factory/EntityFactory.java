/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.DoorComponent;
import com.dokkaebistudio.tacticaljourney.components.ExplosiveComponent;
import com.dokkaebistudio.tacticaljourney.components.ParentRoomComponent;
import com.dokkaebistudio.tacticaljourney.components.SlowMovementComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent.TileEnum;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.DamageDisplayComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.SkillComponent;
import com.dokkaebistudio.tacticaljourney.components.transition.ExitComponent;
import com.dokkaebistudio.tacticaljourney.constants.PositionConstants;
import com.dokkaebistudio.tacticaljourney.enums.AnimationsEnum;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.items.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.skills.SkillEnum;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Factory used to create presets of entities.
 * @author Callil
 *
 */
public final class EntityFactory {
	
	/** The gdx pooled engine. */
	public PooledEngine engine;
	
	/** The player factory. */
	public PlayerFactory playerFactory;

	/** The enemy factory. */
	public EnemyFactory enemyFactory;
	
	/** The factory for visual effects. */
	public EffectFactory effectFactory;
	
	// textures are stored so we don't fetch them from the atlas each time (atlas.findRegion is SLOW)
	private TextureAtlas.AtlasRegion wallTexture;
	private TextureAtlas.AtlasRegion pitTexture;
	private TextureAtlas.AtlasRegion mudTexture;
	private TextureAtlas.AtlasRegion groundTexture;
	
	private TextureAtlas.AtlasRegion healthUpTexture;
	private TextureAtlas.AtlasRegion bombTexture;


	/**
	 * Constructor.
	 * @param e the engine
	 */
	public EntityFactory(PooledEngine e) {
		this.engine = e;
		this.playerFactory = new PlayerFactory(e, this);
		this.enemyFactory = new EnemyFactory(e, this);
		this.effectFactory = new EffectFactory( e, this);
		
		wallTexture = Assets.getTexture(Assets.wall);
		groundTexture = Assets.getTexture(Assets.tile_ground);
		pitTexture = Assets.getTexture(Assets.tile_pit);
		mudTexture = Assets.getTexture(Assets.tile_mud);
		healthUpTexture = Assets.getTexture(Assets.health_up_item);
		bombTexture = Assets.getTexture(Assets.bomb_item);
	}

	
	/**
	 * Create a tile with a given type at the given position
	 * @param pos the position
	 * @param type the type
	 * @return the tile entity
	 */
	public Entity createTile(Room room, Vector2 pos, TileEnum type) {
		Entity tileEntity = engine.createEntity();
		tileEntity.flags = EntityFlagEnum.TILE.getFlag();
		
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		TileComponent tile = engine.createComponent(TileComponent.class);

		tile.type = type;
		switch (type) {
			case WALL:
				this.createWall(room, pos);
				spriteCompo.setSprite(new Sprite(groundTexture));
				tile.type = TileEnum.GROUND;
				break;
			case GROUND:
				spriteCompo.setSprite(new Sprite(groundTexture));
				break;
			case PIT:
				spriteCompo.setSprite(new Sprite(pitTexture));
				break;
			case MUD:
				this.createMud(room, pos);
				spriteCompo.setSprite(new Sprite(groundTexture));
				tile.type = TileEnum.GROUND;
				break;
		}

		gridPosition.coord(tileEntity, pos, room);
		gridPosition.zIndex = 1;

		tileEntity.add(spriteCompo);
		tileEntity.add(gridPosition);
		tileEntity.add(tile);
		
		ParentRoomComponent parentRoomComponent = engine.createComponent(ParentRoomComponent.class);
		parentRoomComponent.setParentRoom(room);
		tileEntity.add(parentRoomComponent);

		engine.addEntity(tileEntity);

		return tileEntity;
	}
	
	public Entity createWall(Room room, Vector2 pos) {
		Entity wallEntity = engine.createEntity();
		wallEntity.flags = EntityFlagEnum.WALL.getFlag();

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(wallEntity, pos, room);
    	movableTilePos.zIndex = 2;
    	wallEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	Sprite s = new Sprite(Assets.getTexture(Assets.wall));
    	spriteCompo.setSprite(s);
    	wallEntity.add(spriteCompo);
    	
    	SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
    	wallEntity.add(solidComponent);    	
    	
		ParentRoomComponent parentRoomComponent = engine.createComponent(ParentRoomComponent.class);
		parentRoomComponent.setParentRoom(room);
		wallEntity.add(parentRoomComponent);
		
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	destructibleCompo.setDestroyedTexture(Assets.getTexture(Assets.wall_destroyed));
    	wallEntity.add(destructibleCompo);
    	
		engine.addEntity(wallEntity);

    	return wallEntity;
	}
	
	public Entity createMud(Room room, Vector2 pos) {
		Entity mudEntity = engine.createEntity();
		mudEntity.flags = EntityFlagEnum.MUD.getFlag();

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(mudEntity, pos, room);
    	movableTilePos.zIndex = 2;
    	mudEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	Sprite s = new Sprite(Assets.getTexture(Assets.tile_mud));
    	spriteCompo.setSprite(s);
    	mudEntity.add(spriteCompo);
    	    	
		ParentRoomComponent parentRoomComponent = engine.createComponent(ParentRoomComponent.class);
		parentRoomComponent.setParentRoom(room);
		mudEntity.add(parentRoomComponent);
		
		SlowMovementComponent slowMovementCompo = engine.createComponent(SlowMovementComponent.class);
		slowMovementCompo.setMovementConsumed(1);
    	mudEntity.add(slowMovementCompo);
    	
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
		mudEntity.add(destructibleCompo);
    	
		engine.addEntity(mudEntity);

    	return mudEntity;
	}

	
	
	public Entity createDoor(Room room, Vector2 pos, Room targetedRoom) {
		Entity doorEntity = engine.createEntity();
		doorEntity.flags = EntityFlagEnum.DOOR.getFlag();

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(doorEntity, pos, room);
    	movableTilePos.zIndex = 2;
    	doorEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	Sprite s = targetedRoom == null ? new Sprite(Assets.getTexture(Assets.door_closed)) : new Sprite(Assets.getTexture(Assets.door_opened));
    	spriteCompo.setSprite(s);
    	doorEntity.add(spriteCompo);
    	
    	DoorComponent doorCompo = engine.createComponent(DoorComponent.class);
    	doorCompo.setTargetedRoom(targetedRoom);
    	doorCompo.setOpened(targetedRoom == null ? false : true);
    	doorEntity.add(doorCompo);
    	
		ParentRoomComponent parentRoomComponent = engine.createComponent(ParentRoomComponent.class);
		parentRoomComponent.setParentRoom(room);
		doorEntity.add(parentRoomComponent);
    	
		engine.addEntity(doorEntity);

    	return doorEntity;
	}
	
	public Entity createExit(Room room, Vector2 pos) {
		Entity exitEntity = engine.createEntity();
		exitEntity.flags = EntityFlagEnum.DOOR.getFlag();

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(exitEntity, pos, room);
    	movableTilePos.zIndex = 2;
    	exitEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	Sprite s = new Sprite(Assets.getTexture(Assets.exit));
    	spriteCompo.setSprite(s);
    	exitEntity.add(spriteCompo);
    	
    	ExitComponent exitCompo = engine.createComponent(ExitComponent.class);
    	exitCompo.setOpened(true);
    	exitEntity.add(exitCompo);
    	
		ParentRoomComponent parentRoomComponent = engine.createComponent(ParentRoomComponent.class);
		parentRoomComponent.setParentRoom(room);
		exitEntity.add(parentRoomComponent);
    	    	
		engine.addEntity(exitEntity);

    	return exitEntity;
	}
	
	public Entity createMovableTile(Vector2 pos) {
		Entity movableTileEntity = engine.createEntity();
		movableTileEntity.flags = EntityFlagEnum.MOVABLE_TILE.getFlag();

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(pos);
    	movableTilePos.zIndex = 2;
    	movableTileEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(new Sprite(Assets.getTexture(Assets.tile_movable)));
    	movableTileEntity.add(spriteCompo);
    	
		try {
			engine.addEntity(movableTileEntity);
		} catch(Exception e) {
			// Enter here if an entity was removed, reset in the pool and re added in the engine during the same update.
			System.out.println("movableTile already in the engine.");
		}
    	return movableTileEntity;
	}
	
	public Entity createAttackableTile(Vector2 pos) {
		Entity attackableTileEntity = engine.createEntity();
		attackableTileEntity.flags = EntityFlagEnum.ATTACK_TILE.getFlag();

    	GridPositionComponent attackableTilePos = engine.createComponent(GridPositionComponent.class);
    	attackableTilePos.coord(pos);
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
		redCross.flags = EntityFlagEnum.DESTINATION_TILE.getFlag();

		GridPositionComponent selectedTilePos = engine.createComponent(GridPositionComponent.class);
    	selectedTilePos.coord(pos);
    	selectedTilePos.zIndex = 100;
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
		Entity waypoint = engine.createEntity();
		waypoint.flags = EntityFlagEnum.WAYPOINT.getFlag();

		GridPositionComponent waypointPos = engine.createComponent(GridPositionComponent.class);
    	waypointPos.coord(pos);
    	waypointPos.zIndex = 100;
    	waypoint.add(waypointPos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(new Sprite(Assets.getTexture(Assets.tile_movable_waypoint)));
    	waypoint.add(spriteCompo);
    	
		engine.addEntity(waypoint);
    	return waypoint;
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
//    	return confirmButton;
//	}
	
	
	/**
	 * Create a simple sprite entity.
	 * @param texture the texture
	 * @param flag the flag of the entity (for debugging purpose)
	 * @return the sprite entity
	 */
	public Entity createSprite(AtlasRegion texture, EntityFlagEnum flag) {
		return createSprite(null, texture, flag, null);
	}
	
	/**
	 * Create a simple sprite entity.
	 * @param pos the position
	 * @param texture the texture
	 * @param flag the flag of the entity (for debugging purpose)
	 * @return the sprite entity
	 */
	public Entity createSprite(Vector3 pos, AtlasRegion texture, EntityFlagEnum flag) {
		return createSprite(pos, texture, flag, null);
	}
	
	/**
	 * Create a simple sprite entity.
	 * @param pos the position
	 * @param texture the texture
	 * @param flag the flag of the entity (for debugging purpose)
	 * @param room the parent room
	 * @return the sprite entity
	 */
	public Entity createSprite(Vector3 pos, AtlasRegion texture, EntityFlagEnum flag, Room room) {
		Entity sprite = engine.createEntity();
		sprite.flags = flag.getFlag();
		
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(new Sprite(texture));
		sprite.add(spriteCompo);
		
		GridPositionComponent transfoCompo = engine.createComponent(GridPositionComponent.class);
		if (pos != null) {
			transfoCompo.absolutePos(pos.x, pos.y);
			transfoCompo.zIndex = (int) pos.z;
		}
		sprite.add(transfoCompo);
		
		if (room != null) {
			ParentRoomComponent parentRoomComponent = engine.createComponent(ParentRoomComponent.class);
			parentRoomComponent.setParentRoom(room);
			sprite.add(parentRoomComponent);
			
			room.addEntity(sprite);
		} else {
			engine.addEntity(sprite);
		}
		return sprite;
	}
	
	/**
	 * Create a simple sprite entity.
	 * @param pos the position
	 * @param texture the texture
	 * @param flag the flag of the entity (for debugging purpose)
	 * @param room the parent room
	 * @return the sprite entity
	 */
	public Entity createSpriteOnTile(Vector2 tilePos, AtlasRegion texture, EntityFlagEnum flag, Room room) {
		Entity sprite = engine.createEntity();
		sprite.flags = flag.getFlag();
		
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(new Sprite(texture));
		sprite.add(spriteCompo);
		
		GridPositionComponent gridPos = engine.createComponent(GridPositionComponent.class);
		gridPos.coord(sprite, tilePos, room);
		gridPos.zIndex = 2;
		sprite.add(gridPos);

		ParentRoomComponent parentRoomComponent = engine.createComponent(ParentRoomComponent.class);
		parentRoomComponent.setParentRoom(room);
		sprite.add(parentRoomComponent);
		
		room.addEntity(sprite);
		return sprite;
	}
	
	
	
	/**
	 * Create a text that will be displayed on screen.
	 * @return the text entity
	 */
	public Entity createText() {
		return createText(null, null, null);
	}
	
	/**
	 * Create a text that will be displayed on screen.
	 * @param text the text to display
	 * @return the text entity
	 */
	public Entity createText( String text) {
		return createText(null, text, null);
	}
	
	/**
	 * Create a text that will be displayed on screen.
	 * @param pos the positon of the text
	 * @param text the text to display
	 * @return the text entity
	 */
	public Entity createText(Vector3 pos, String text) {
		return createText(pos, text, null);
	}
	
	/**
	 * Create a text that will be displayed on screen.
	 * @param pos the position of the text
	 * @param text the text to display
	 * @param room the parent room of this text
	 * @return the text entity
	 */
	public Entity createText(Vector3 pos, String text, Room room) {
		return createText(pos, text, Assets.font, room);
	}
	
	/**
	 * Create a text that will be displayed on screen.
	 * @param pos the position of the text
	 * @param text the text to display
	 * @param room the parent room of this text
	 * @return the text entity
	 */
	public Entity createText(Vector3 pos, String text, BitmapFont font, Room room) {
		Entity textTest = engine.createEntity();
		textTest.flags = EntityFlagEnum.TEXT.getFlag();

		GridPositionComponent transfoCompo = engine.createComponent(GridPositionComponent.class);
		if (pos != null) {
			transfoCompo.absolutePos(pos.x, pos.y);
			transfoCompo.zIndex = (int) pos.z;
		}
		textTest.add(transfoCompo);
		
		TextComponent tc = new TextComponent(font);
		if (text != null) {
			tc.setText(text);
		}
		textTest.add(tc);
		
		if (room != null) {
			ParentRoomComponent parentRoomComponent = engine.createComponent(ParentRoomComponent.class);
			parentRoomComponent.setParentRoom(room);
			textTest.add(parentRoomComponent);
		}
		
		engine.addEntity(textTest);
		return textTest;
	}
	
	
	/**
	 * Create a text that will be displayed on a tile.
	 * @param pos the position of the text in tile position
	 * @return the text entity
	 */
	public Entity createTextOnTile(Vector2 tilePos, String text, int zIndex) {
		return createTextOnTile(tilePos, text, zIndex, null);
	}
	
	/**
	 * Create a text that will be displayed on a tile.
	 * @param pos the position of the text in tile position
	 * @return the text entity
	 */
	public Entity createTextOnTile(Vector2 tilePos, String text, int zIndex, Room room) {
		Entity textTest = engine.createEntity();
		textTest.flags = EntityFlagEnum.TEXT_ON_TILE.getFlag();

		GridPositionComponent gridPositionComponent = new GridPositionComponent();
		gridPositionComponent.coord(tilePos);
		gridPositionComponent.zIndex = zIndex;
		textTest.add(gridPositionComponent);
		
		TextComponent tc = new TextComponent(Assets.font);
		tc.setText(text);
		textTest.add(tc);
		
		if (room != null) {
			ParentRoomComponent parentRoomComponent = engine.createComponent(ParentRoomComponent.class);
			parentRoomComponent.setParentRoom(room);
			textTest.add(parentRoomComponent);
		}
		
		engine.addEntity(textTest);
		
		return textTest;
	}
	
	/**
	 * Create a damage displayer.
	 * @param damage the damage to display
	 * @param gridPos the grid position
	 * @param heal whether the amount is a healing amount or damage amount (changes the color of the text)
	 * @return the damage displayer entity
	 */
	public Entity createDamageDisplayer(String damage, Vector2 gridPos, boolean heal) {
		Entity display = engine.createEntity();
		display.flags = EntityFlagEnum.DAMAGE_DISPLAYER.getFlag();

		Vector2 initialPos = TileUtil.convertGridPosIntoPixelPos(gridPos);
		initialPos.add(GameScreen.GRID_SIZE/2, GameScreen.GRID_SIZE);
		
		DamageDisplayComponent displayCompo = engine.createComponent(DamageDisplayComponent.class);
		displayCompo.setInitialPosition(initialPos);
		display.add(displayCompo);
		
		GridPositionComponent transfoCompo = engine.createComponent(GridPositionComponent.class);
		transfoCompo.absolutePos(initialPos.x, initialPos.y);
		transfoCompo.zIndex = (int) PositionConstants.Z_DAMAGE_DISPLAYER;
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
	 * Create an experience displayer.
	 * @param exp the amount of xp earned
	 * @param initialPos the initial position
	 * @return the exp displayer entity
	 */
	public Entity createExpDisplayer(int exp, Vector2 gridPos) {
		Entity display = engine.createEntity();
		display.flags = EntityFlagEnum.EXP_DISPLAYER.getFlag();

		Vector2 initialPos = TileUtil.convertGridPosIntoPixelPos(gridPos);
		initialPos.add(GameScreen.GRID_SIZE/2, GameScreen.GRID_SIZE);
		
		DamageDisplayComponent displayCompo = engine.createComponent(DamageDisplayComponent.class);
		displayCompo.setInitialPosition(initialPos);
		display.add(displayCompo);
		 
		GridPositionComponent transfoCompo = engine.createComponent(GridPositionComponent.class);
		transfoCompo.absolutePos(initialPos.x, initialPos.y);
		transfoCompo.zIndex = (int) PositionConstants.Z_EXP_DISPLAYER;
		display.add(transfoCompo);
		
		TextComponent textCompo = engine.createComponent(TextComponent.class);
		textCompo.setFont(Assets.font);
		textCompo.setText("Exp+" + exp);
		display.add(textCompo);
		
		engine.addEntity(display);
		return display;
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
		spriteCompo.setSprite(new Sprite(this.healthUpTexture));
		healthUp.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(healthUp, tilePos, room);
		gridPosition.zIndex = 6;
		healthUp.add(gridPosition);
		
		ItemComponent itemCompo = engine.createComponent(ItemComponent.class);
		itemCompo.setItemType(ItemEnum.CONSUMABLE_HEALTH_UP);
		healthUp.add(itemCompo);

		ParentRoomComponent parentRoomComponent = engine.createComponent(ParentRoomComponent.class);
		parentRoomComponent.setParentRoom(room);
		healthUp.add(parentRoomComponent);
		
		engine.addEntity(healthUp);
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	healthUp.add(destructibleCompo);
		
		return healthUp;
	}
	
	/**
	 * Create a bomb that explodes after x turns.
	 * @param room the parent room
	 * @param tilePos the position in tiles
	 * @param parent the parent entity of the bomb
	 * @return the entity created
	 */
	public Entity createBomb(Room room, Vector2 tilePos, Entity parentEntity) {
		Entity bomb = engine.createEntity();
		bomb.flags = EntityFlagEnum.BOMB.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		bomb.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(bomb, tilePos, room);
		gridPosition.zIndex = 6;
		bomb.add(gridPosition);
		
		ExplosiveComponent explosionCompo = engine.createComponent(ExplosiveComponent.class);
		explosionCompo.engine = engine;
		explosionCompo.setRadius(2);
		explosionCompo.setTurnsToExplode(2);
		explosionCompo.setDamage(20);
		bomb.add(explosionCompo);
		
		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.animations.put(StatesEnum.EXPLODING_IN_SEVERAL_TURNS.getState(), AnimationsEnum.BOMB_SLOW.getAnimation());
		animationCompo.animations.put(StatesEnum.EXPLODING_THIS_TURN.getState(), AnimationsEnum.BOMB_FAST.getAnimation());
		bomb.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(explosionCompo.getTurnsToExplode() > 0 ? StatesEnum.EXPLODING_IN_SEVERAL_TURNS.getState() : StatesEnum.EXPLODING_THIS_TURN.getState());
		bomb.add(stateCompo);

		ParentRoomComponent parentRoomComponent = engine.createComponent(ParentRoomComponent.class);
		parentRoomComponent.setParentRoom(room);
		bomb.add(parentRoomComponent);
		
		ParentEntityComponent parentCompo = engine.createComponent(ParentEntityComponent.class);
		parentCompo.setParent(parentEntity);
		bomb.add(parentCompo);
		
		engine.addEntity(bomb);
		
		return bomb;
	}
	
	
	public Entity createSkill(Entity parent, SkillEnum type, int skillNumber) {
		GridPositionComponent parentPos = Mappers.gridPositionComponent.get(parent);
		PlayerComponent playerComponent = Mappers.playerComponent.get(parent);
		
		Entity skillEntity = engine.createEntity();
		
		SkillComponent skillCompo = engine.createComponent(SkillComponent.class);
		skillCompo.setParentEntity(parent);
		skillCompo.setSkillNumber(skillNumber);
		skillCompo.setType(type);
		skillEntity.add(skillCompo);
		
		GridPositionComponent skillPosCompo = engine.createComponent(GridPositionComponent.class);
		skillPosCompo.coord(parentPos.coord());
		skillEntity.add(skillPosCompo);
		
		MoveComponent skillMoveComponent = engine.createComponent(MoveComponent.class);
		skillMoveComponent.engine = engine;
		skillMoveComponent.moveSpeed = 0;
		skillMoveComponent.moveRemaining = 0;
		skillEntity.add(skillMoveComponent);
		
		AttackComponent parentAttackCompo = Mappers.attackComponent.get(parent);
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.engine = engine;
		attackComponent.setAttackType(type.getAttackType());
		attackComponent.setRangeMin(type.getRangeMin());
		attackComponent.setRangeMax(type.getRangeMax());
		attackComponent.setStrength(type.getStrength());
		attackComponent.setAmmoType(type.getAmmosType());
		attackComponent.setAmmosUsedPerAttack(type.getNbOfAmmosPerAttack());
		attackComponent.setSkillNumber(skillNumber);
		attackComponent.setParentAttackCompo(parentAttackCompo);
		skillEntity.add(attackComponent);
		

		switch(skillNumber) {
		case 1:
			playerComponent.setSkillMelee(skillEntity);
			break;
		case 2:
			playerComponent.setSkillRange(skillEntity);
			break;
		case 3:
			playerComponent.setSkillBomb(skillEntity);
			break;
			
			default:
				break;
		}
		
		return skillEntity;
	}
}
