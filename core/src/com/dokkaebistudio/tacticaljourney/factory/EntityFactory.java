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
import com.dokkaebistudio.tacticaljourney.components.DoorComponent;
import com.dokkaebistudio.tacticaljourney.components.ParentRoomComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent.TileEnum;
import com.dokkaebistudio.tacticaljourney.components.display.DamageDisplayComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.SkillComponent;
import com.dokkaebistudio.tacticaljourney.components.transition.ExitComponent;
import com.dokkaebistudio.tacticaljourney.constants.PositionConstants;
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
	
	// textures are stored so we don't fetch them from the atlas each time (atlas.findRegion is SLOW)
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
		this.playerFactory = new PlayerFactory(e, this);
		this.enemyFactory = new EnemyFactory(e, this);
		
		wallTexture = Assets.getTexture(Assets.tile_wall);
		groundTexture = Assets.getTexture(Assets.tile_ground);
		pitTexture = Assets.getTexture(Assets.tile_pit);
		mudTexture = Assets.getTexture(Assets.tile_mud);
		healthUpTexture = Assets.getTexture(Assets.health_up_item);
	}


	
	/**
	 * Create the end turn button.
	 * @param pos the position
	 * @return the end turn button entity
	 */
	public Entity createEndTurnButton() {
		Entity endTurnButton = engine.createEntity();
		endTurnButton.flags = EntityFlagEnum.END_TURN_BUTTON.getFlag();

		
		TransformComponent transfoCompo = engine.createComponent(TransformComponent.class);
		transfoCompo.pos.set(PositionConstants.POS_END_TURN_BTN, PositionConstants.Z_END_TURN_BTN);
		endTurnButton.add(transfoCompo);
		    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(new Sprite(Assets.getTexture(Assets.btn_end_turn)));
    	endTurnButton.add(spriteCompo);
    	
    	engine.addEntity(endTurnButton);
    	return endTurnButton;
	}
	
	
	/**
	 * Create the end turn button.
	 * @param pos the position
	 * @return the end turn button entity
	 */
	public Entity createSkillButton(SkillEnum skill, Vector2 pos) {
		Entity skillButton = engine.createEntity();
		skillButton.flags = EntityFlagEnum.SKILL1_BUTTON.getFlag();

		
		TransformComponent transfoCompo = engine.createComponent(TransformComponent.class);
		transfoCompo.pos.set(pos.x, pos.y, PositionConstants.Z_SKILL_BTN);
		skillButton.add(transfoCompo);
		    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(new Sprite(Assets.getTexture(skill.getBtnTexture())));
    	skillButton.add(spriteCompo);
    	
    	engine.addEntity(skillButton);
    	return skillButton;
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
		
		ParentRoomComponent parentRoomComponent = engine.createComponent(ParentRoomComponent.class);
		parentRoomComponent.setParentRoom(room);
		tileEntity.add(parentRoomComponent);

		engine.addEntity(tileEntity);

		return tileEntity;
	}
	
	
	public Entity createDoor(Room room, Vector2 pos, Room targetedRoom) {
		Entity doorEntity = engine.createEntity();
		doorEntity.flags = EntityFlagEnum.DOOR.getFlag();

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord.set(pos);
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
	
	public Entity createExit(Room r, Vector2 pos) {
		Entity exitEntity = engine.createEntity();
		exitEntity.flags = EntityFlagEnum.DOOR.getFlag();

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord.set(pos);
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
		parentRoomComponent.setParentRoom(r);
		exitEntity.add(parentRoomComponent);
    	    	
		engine.addEntity(exitEntity);

    	return exitEntity;
	}
	
	public Entity createMovableTile(Vector2 pos) {
		Entity movableTileEntity = engine.createEntity();
		movableTileEntity.flags = EntityFlagEnum.MOVABLE_TILE.getFlag();

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
		attackableTileEntity.flags = EntityFlagEnum.ATTACK_TILE.getFlag();

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
		redCross.flags = EntityFlagEnum.DESTINATION_TILE.getFlag();

		GridPositionComponent selectedTilePos = engine.createComponent(GridPositionComponent.class);
    	selectedTilePos.coord.set(pos);
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
    	waypointPos.coord.set(pos);
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
		
		TransformComponent transfoCompo = engine.createComponent(TransformComponent.class);
		if (pos != null) {
			transfoCompo.pos.set(pos);
		}
		sprite.add(transfoCompo);
		
		if (room != null) {
			ParentRoomComponent parentRoomComponent = engine.createComponent(ParentRoomComponent.class);
			parentRoomComponent.setParentRoom(room);
			sprite.add(parentRoomComponent);
		}
		
		engine.addEntity(sprite);
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

		TransformComponent transfoCompo = new TransformComponent();
		if (pos != null) {
			transfoCompo.pos.set(pos);
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
		gridPositionComponent.coord.set(tilePos);
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
		
		TransformComponent transfoCompo = engine.createComponent(TransformComponent.class);
		transfoCompo.pos.set(initialPos, PositionConstants.Z_DAMAGE_DISPLAYER);
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
		
		TransformComponent transfoCompo = engine.createComponent(TransformComponent.class);
		transfoCompo.pos.set(initialPos, PositionConstants.Z_EXP_DISPLAYER);
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
		gridPosition.coord.set(tilePos);
		gridPosition.zIndex = 6;
		healthUp.add(gridPosition);
		
		ItemComponent itemCompo = engine.createComponent(ItemComponent.class);
		itemCompo.setItemType(ItemEnum.CONSUMABLE_HEALTH_UP);
		healthUp.add(itemCompo);

		ParentRoomComponent parentRoomComponent = engine.createComponent(ParentRoomComponent.class);
		parentRoomComponent.setParentRoom(room);
		healthUp.add(parentRoomComponent);
		
		engine.addEntity(healthUp);
		
		return healthUp;
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
		skillPosCompo.coord.set(parentPos.coord);
		skillEntity.add(skillPosCompo);
		
		MoveComponent skillMoveComponent = engine.createComponent(MoveComponent.class);
		skillMoveComponent.engine = engine;
		skillMoveComponent.moveSpeed = 0;
		skillMoveComponent.moveRemaining = 0;
		skillEntity.add(skillMoveComponent);
		
		AttackComponent parentAttackCompo = Mappers.attackComponent.get(parent);
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.engine = engine;
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
			default:
				break;
		}
		
		return skillEntity;
	}
}
