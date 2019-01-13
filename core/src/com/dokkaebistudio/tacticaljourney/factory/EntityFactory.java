/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.DoorComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.ParentRoomComponent;
import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.SkillComponent;
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
import com.dokkaebistudio.tacticaljourney.components.transition.ExitComponent;
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
	
	/** The enemy factory. */
	public EnemyFactory enemyFactory;
	
	// textures are stored so we don't fetch them from the atlas each time (atlas.findRegion is SLOW)
	private TextureAtlas.AtlasRegion playerTexture;
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
		this.enemyFactory = new EnemyFactory(e, this);
		
		playerTexture = Assets.getTexture(Assets.player);
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
	public Entity createPlayer(Vector2 pos, int moveSpeed, Room room) {
		Entity playerEntity = engine.createEntity();
		playerEntity.flags = EntityFlagEnum.PLAYER.getFlag();

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
		playerComponent.setSkill1Button(createSkillButton(SkillEnum.SLASH, SkillEnum.SKILL_1_POSITION));
		playerComponent.setSkill2Button(createSkillButton(SkillEnum.BOW, SkillEnum.SKILL_2_POSITION));
		
		
		//TODO : refactor
		Entity indicator = engine.createEntity();
		SpriteComponent indicatorSpriteCompo = engine.createComponent(SpriteComponent.class);
		indicatorSpriteCompo.setSprite(new Sprite(Assets.getTexture(Assets.btn_skill_active)));
		indicator.add(indicatorSpriteCompo);
		TransformComponent indicatorTransfoCompo = engine.createComponent(TransformComponent.class);
		indicatorTransfoCompo.pos.set(0, 0, 11);
		indicator.add(indicatorTransfoCompo);
		playerComponent.setActiveSkillIndicator(indicator);
		engine.addEntity(indicator);
		playerEntity.add(playerComponent);
		
		MoveComponent moveComponent = engine.createComponent(MoveComponent.class);
		moveComponent.engine = this.engine;
		moveComponent.moveSpeed = moveSpeed;
		playerEntity.add(moveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.engine = this.engine;
		attackComponent.setRangeMax(1);
		attackComponent.setStrength(5);
		playerEntity.add(attackComponent);
		
		AmmoCarrierComponent ammoCarrierCompo = engine.createComponent(AmmoCarrierComponent.class);
		ammoCarrierCompo.setArrows(10);
		ammoCarrierCompo.setMaxArrows(10);
		Vector3 arrowDisplayerPos = new Vector3();
		arrowDisplayerPos.set(1200,1070, 100);
		Entity arrowsNbText = this.createText(arrowDisplayerPos, "Arrows: 10/10", null);
		ammoCarrierCompo.setArrowsDisplayer(arrowsNbText);
		ammoCarrierCompo.setBombs(0);
		ammoCarrierCompo.setMaxBombs(5);
		Vector3 bombDisplayerPos = new Vector3();
		bombDisplayerPos.set(1200,1040, 100);
		Entity bombsNbText = this.createText(bombDisplayerPos, "Bombs: 0/5", null);
		ammoCarrierCompo.setBombsDisplayer(bombsNbText);
		playerEntity.add(ammoCarrierCompo);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		playerEntity.add(solidComponent);
		
		HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
		healthComponent.engine = engine;
		healthComponent.setMaxHp(100);
		healthComponent.setHp(100);
		Entity hpText = this.createTextOnTile(pos, String.valueOf(healthComponent.getHp()), 100, null);
		healthComponent.setHpDisplayer(hpText);
		playerEntity.add(healthComponent);
		
		ParentRoomComponent parentRoomComponent = engine.createComponent(ParentRoomComponent.class);
		parentRoomComponent.setParentRoom(room);
		playerEntity.add(parentRoomComponent);
		
		this.createSkill(playerEntity, SkillEnum.SLASH, 1 );
		this.createSkill(playerEntity, SkillEnum.BOW, 2);

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
		endTurnButton.flags = EntityFlagEnum.END_TURN_BUTTON.getFlag();

		
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
	 * Create the end turn button.
	 * @param pos the position
	 * @return the end turn button entity
	 */
	public Entity createSkillButton(SkillEnum skill, Vector2 pos) {
		Entity skillButton = engine.createEntity();
		skillButton.flags = EntityFlagEnum.SKILL1_BUTTON.getFlag();

		
		TransformComponent transfoCompo = engine.createComponent(TransformComponent.class);
		transfoCompo.pos.set(pos.x, pos.y, 10);
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
	 * Create a text that will be displayed on screen.
	 * @param pos the position of the text
	 * @return the text entity
	 */
	public Entity createText(Vector3 pos, String text, Room room) {
		Entity textTest = engine.createEntity();
		textTest.flags = EntityFlagEnum.TEXT.getFlag();

		TransformComponent transfoCompo = new TransformComponent();
		transfoCompo.pos.set(pos);
		textTest.add(transfoCompo);
		
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
	 * @param initialPos the initial position
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
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.engine = engine;
		attackComponent.setRangeMin(type.getRangeMin());
		attackComponent.setRangeMax(type.getRangeMax());
		attackComponent.setStrength(type.getStrength());
		attackComponent.setAmmoType(type.getAmmosType());
		attackComponent.setAmmosUsedPerAttack(type.getNbOfAmmosPerAttack());
		attackComponent.setSkillNumber(skillNumber);
		
//		if (type.getAmmos() >= 0) {
//			Vector3 pos = new Vector3();
//			pos.set(1650,100, 100);
//			Entity ammoText = this.createText(pos, String.valueOf(type.getAmmos()), null);
//			attackComponent.setAmmoDisplayer(ammoText);
//		}
		
		skillEntity.add(attackComponent);
		

		switch(skillNumber) {
		case 1:
			playerComponent.setSkill1(skillEntity);
			break;
		case 2:
			playerComponent.setSkill2(skillEntity);
			break;
			default:
				break;
		}
		
		return skillEntity;
	}
}
