/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.BlockExplosionComponent;
import com.dokkaebistudio.tacticaljourney.components.ChasmComponent;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.DialogComponent;
import com.dokkaebistudio.tacticaljourney.components.DoorComponent;
import com.dokkaebistudio.tacticaljourney.components.ExplosiveComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.DamageDisplayComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.SkillComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WheelComponent;
import com.dokkaebistudio.tacticaljourney.components.transition.ExitComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.enums.AnimationsEnum;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.enums.TileEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.destructibles.AmmoCrateItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.destructibles.VaseItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.destructibles.WallItemPool;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.skills.SkillEnum;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.vfx.AttackAnimation;
import com.dokkaebistudio.tacticaljourney.wheel.Sector.Hit;

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
	
	/** The enemy factory. */
	public ItemFactory itemFactory;
	
	/** The creep factory. */
	public CreepFactory creepFactory;
	
	/** The lootable factory. */
	public LootableFactory lootableFactory;

	
	
	/** The factory for visual effects. */
	public EffectFactory effectFactory;
	
	/**
	 * Constructor.
	 * @param e the engine
	 */
	public EntityFactory(PooledEngine e) {
		this.engine = e;
		this.playerFactory = new PlayerFactory(e, this);
		this.enemyFactory = new EnemyFactory(e, this);
		this.itemFactory = new ItemFactory(e, this);
		this.creepFactory = new CreepFactory(e, this);
		this.lootableFactory = new LootableFactory(e, this);
		this.effectFactory = new EffectFactory( e, this);
	}

	
	/**
	 * Create a tile with a given type at the given position
	 * @param pos the position
	 * @param type the type
	 * @return the tile entity
	 */
	public Entity createTerrain(Room room, Vector2 pos, TileEnum type) {
		Entity result = null;
		switch (type) {
			case WALL:
				result = this.createWall(room, pos);
				break;
			case GROUND:
				break;
			case PIT:
				result = this.createChasm(room, pos);
				break;
			case MUD:
				result = this.creepFactory.createMud(room, pos);
				break;
		}
		return result;
	}
	
	public Entity createWall(Room room, Vector2 pos) {
		Entity wallEntity = engine.createEntity();
		wallEntity.flags = EntityFlagEnum.WALL.getFlag();

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(wallEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.WALL;
    	wallEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	Sprite s = new Sprite(Assets.wall);
    	spriteCompo.setSprite(s);
    	wallEntity.add(spriteCompo);
    	
    	SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
    	wallEntity.add(solidComponent);    	
    	
    	BlockExplosionComponent blockExplosionComponent = engine.createComponent(BlockExplosionComponent.class);
		wallEntity.add(blockExplosionComponent);
    	
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	destructibleCompo.setDestroyedTexture(Assets.wall_destroyed);
    	wallEntity.add(destructibleCompo);
    	
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(new WallItemPool());
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.COMMON, 100);
		dropRate.add(ItemPoolRarity.RARE, 0);
		lootRewardCompo.setDropRate(dropRate);
		wallEntity.add(lootRewardCompo);
				
    	
		engine.addEntity(wallEntity);

    	return wallEntity;
	}	
	
	public Entity createChasm(Room room, Vector2 pos) {
		Entity chasmEntity = engine.createEntity();
		chasmEntity.flags = EntityFlagEnum.CHASM.getFlag();

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(chasmEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.TILE;
    	chasmEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	Sprite s = new Sprite(Assets.tile_pit);
    	spriteCompo.setSprite(s);
    	chasmEntity.add(spriteCompo);
    	
    	ChasmComponent chasmCompo = engine.createComponent(ChasmComponent.class);
    	chasmEntity.add(chasmCompo);   
    	
		engine.addEntity(chasmEntity);

    	return chasmEntity;
	}	
	
	public Entity createDoor(Room room, Vector2 pos, Room targetedRoom) {
		Entity doorEntity = engine.createEntity();
		doorEntity.flags = EntityFlagEnum.DOOR.getFlag();

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(doorEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.DOOR;
    	doorEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	Sprite s = new Sprite(Assets.door_opened);
    	spriteCompo.setSprite(s);
    	doorEntity.add(spriteCompo);
    	
    	DoorComponent doorCompo = engine.createComponent(DoorComponent.class);
    	doorCompo.setTargetedRoom(targetedRoom);
    	doorCompo.setOpened( true);
    	doorEntity.add(doorCompo);
    	
		engine.addEntity(doorEntity);

    	return doorEntity;
	}
	
	public Entity createExit(Room room, Vector2 pos, boolean opened) {
		Entity exitEntity = engine.createEntity();
		exitEntity.flags = EntityFlagEnum.REMAINS_BONES.getFlag();

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(exitEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.EXIT;
    	exitEntity.add(movableTilePos);
    	
    	// Remove creeps on this tile
    	Set<Entity> creeps = room.getEntitiesAtPositionWithComponent(pos, CreepComponent.class);
    	for (Entity e : creeps) {
    		room.removeEntity(e);
    	}
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	Sprite s = new Sprite(opened ? Assets.exit_opened : Assets.exit_closed);
    	spriteCompo.setSprite(s);
    	exitEntity.add(spriteCompo);
    	
    	ExitComponent exitCompo = engine.createComponent(ExitComponent.class);
    	exitCompo.setOpened(opened);
    	exitEntity.add(exitCompo);

		engine.addEntity(exitEntity);

    	return exitEntity;
	}
	
	public Entity createMovableTile(Vector2 pos, Room room) {
		Entity movableTileEntity = engine.createEntity();
		movableTileEntity.flags = EntityFlagEnum.MOVABLE_TILE.getFlag();

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(movableTileEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.MOVABLE_TILE;
    	movableTileEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(new Sprite(Assets.tile_movable));
    	movableTileEntity.add(spriteCompo);
    	
		try {
			engine.addEntity(movableTileEntity);
		} catch(Exception e) {
			// Enter here if an entity was removed, reset in the pool and re added in the engine during the same update.
			System.out.println("movableTile already in the engine.");
		}
    	return movableTileEntity;
	}
	
	public Entity createAttackableTile(Vector2 pos, Room room, boolean explosion) {
		Entity attackableTileEntity = engine.createEntity();
		attackableTileEntity.flags = EntityFlagEnum.ATTACK_TILE.getFlag();

    	GridPositionComponent attackableTilePos = engine.createComponent(GridPositionComponent.class);
    	attackableTilePos.coord(attackableTileEntity, pos, room);
    	attackableTilePos.zIndex = ZIndexConstants.ATTACKABLE_TILE;
    	attackableTileEntity.add(attackableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(new Sprite(explosion ? Assets.tile_explosion : Assets.tile_attackable));
    	attackableTileEntity.add(spriteCompo);
    	
		engine.addEntity(attackableTileEntity);
    	return attackableTileEntity;
	}
	
	
	/**
	 * Create the red cross that indicates the destination tile of the player.
	 * @param pos the position
	 * @return the destination tile entity
	 */
	public Entity createDestinationTile(Vector2 pos, Room room) {
		Entity redCross = engine.createEntity();
		redCross.flags = EntityFlagEnum.DESTINATION_TILE.getFlag();

		GridPositionComponent selectedTilePos = engine.createComponent(GridPositionComponent.class);
		selectedTilePos.coord(redCross, pos, room);
    	selectedTilePos.zIndex = ZIndexConstants.DESTINATION_TILE;
    	redCross.add(selectedTilePos);
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(new Sprite(Assets.tile_movable_selected));
    	redCross.add(spriteCompo);
    	
		engine.addEntity(redCross);
    	return redCross;
	}
	
	
	/**
	 * Create a waypoint.
	 * @param pos the position
	 * @return the waypoint entity
	 */
	public Entity createWaypoint(Vector2 pos, Room room) {
		Entity waypoint = engine.createEntity();
		waypoint.flags = EntityFlagEnum.WAYPOINT.getFlag();

		GridPositionComponent waypointPos = engine.createComponent(GridPositionComponent.class);
		waypointPos.coord(waypoint, pos, room);
    	waypointPos.zIndex = ZIndexConstants.WAYPOINT;
    	waypoint.add(waypointPos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(new Sprite(Assets.tile_movable_waypoint));
    	waypoint.add(spriteCompo);
    	
		engine.addEntity(waypoint);
    	return waypoint;
	}
	
	
	
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
		
		room.addEntity(sprite);

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
	public Entity createSpriteOnTile(Vector2 tilePos, int zIndex, AtlasRegion texture, EntityFlagEnum flag, Room room) {
		Entity sprite = engine.createEntity();
		sprite.flags = flag.getFlag();
		
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(new Sprite(texture));
		sprite.add(spriteCompo);
		
		GridPositionComponent gridPos = engine.createComponent(GridPositionComponent.class);
		gridPos.coord(sprite, tilePos, room);
		gridPos.zIndex = zIndex;
		sprite.add(gridPos);
		
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
			room.addEntity(textTest);
		} else {
			engine.addEntity(textTest);
		}
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
		if (room != null) {
			gridPositionComponent.coord(textTest, tilePos, room);
		} else {
			gridPositionComponent.coord(tilePos);
		}
		gridPositionComponent.zIndex = zIndex;
		textTest.add(gridPositionComponent);
		
		TextComponent tc = new TextComponent(Assets.smallFont);
		tc.setText(text);
		textTest.add(tc);
		
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
	public Entity createDamageDisplayer(String damage, GridPositionComponent gridPosCompo, HealthChangeEnum healthChange, float offsetY, Room room) {
		Entity display = engine.createEntity();
		display.flags = EntityFlagEnum.DAMAGE_DISPLAYER.getFlag();

		PoolableVector2 initialPos = null;
		if (gridPosCompo.hasAbsolutePos()) {
			initialPos = PoolableVector2.create(gridPosCompo.getAbsolutePos());
		} else {
			initialPos = TileUtil.convertGridPosIntoPixelPos(gridPosCompo.coord());
		}
		initialPos.add(GameScreen.GRID_SIZE/2, GameScreen.GRID_SIZE + offsetY);
		
		DamageDisplayComponent displayCompo = engine.createComponent(DamageDisplayComponent.class);
		displayCompo.setInitialPosition(initialPos);
		display.add(displayCompo);
		
		GridPositionComponent transfoCompo = engine.createComponent(GridPositionComponent.class);
		transfoCompo.absolutePos(initialPos.x, initialPos.y);
		transfoCompo.zIndex = ZIndexConstants.DAMAGE_DISPLAYER;
		display.add(transfoCompo);
		
		TextComponent textCompo = engine.createComponent(TextComponent.class);
		textCompo.setFont(Assets.font);
		
		String color = "";
		switch(healthChange) {
		case HEALED:
			color = "[GREEN]";
			break;
		case HIT:
		case HIT_INTERRUPT:
			color = "[RED]";
			break;
		case ARMOR:
			color = "[BLUE]";
			break;
		case RESISTANT:
			color = "[BLACK]";
			break;
			default:
				color = "[WHITE]";

		}
		textCompo.setText(color + damage);
		display.add(textCompo);
		
		initialPos.free();
		
		room.addEntity(display);
		return display;
	}
	
	/**
	 * Create an experience displayer.
	 * @param exp the amount of xp earned
	 * @param initialPos the initial position
	 * @return the exp displayer entity
	 */
	public Entity createExpDisplayer(int exp, Vector2 gridPos, float offsetY, Room room) {
		Entity display = engine.createEntity();
		display.flags = EntityFlagEnum.EXP_DISPLAYER.getFlag();

		PoolableVector2 initialPos = TileUtil.convertGridPosIntoPixelPos(gridPos);
		initialPos.add(0, GameScreen.GRID_SIZE + offsetY);
		
		DamageDisplayComponent displayCompo = engine.createComponent(DamageDisplayComponent.class);
		displayCompo.setInitialPosition(initialPos);
		display.add(displayCompo);
		 
		GridPositionComponent transfoCompo = engine.createComponent(GridPositionComponent.class);
		transfoCompo.absolutePos(initialPos.x, initialPos.y);
		transfoCompo.zIndex = ZIndexConstants.EXP_DISPLAYER;
		display.add(transfoCompo);
		
		TextComponent textCompo = engine.createComponent(TextComponent.class);
		textCompo.setFont(Assets.font);
		textCompo.setText("Exp+" + exp);
		display.add(textCompo);
		
		initialPos.free();
		
		room.addEntity(display);
		return display;
	}
	
	
	/**
	 * Create a bomb that explodes after x turns.
	 * @param room the parent room
	 * @param tilePos the position in tiles
	 * @param parent the parent entity of the bomb
	 * @return the entity created
	 */
	public Entity createBomb(Room room, Vector2 tilePos, Entity parentEntity, int radius, int turnsToExplode, int damage) {
		Entity bomb = engine.createEntity();
		bomb.flags = EntityFlagEnum.BOMB.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		bomb.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(bomb, tilePos, room);
		gridPosition.zIndex = ZIndexConstants.BOMB;
		bomb.add(gridPosition);
		
		ExplosiveComponent explosionCompo = engine.createComponent(ExplosiveComponent.class);
		explosionCompo.room = room;
		explosionCompo.setRadius(radius);
		explosionCompo.setTurnsToExplode(turnsToExplode);
		explosionCompo.setDamage(damage);
		bomb.add(explosionCompo);
		
		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.animations.put(StatesEnum.EXPLODING_IN_SEVERAL_TURNS.getState(), AnimationsEnum.BOMB_SLOW.getAnimation());
		animationCompo.animations.put(StatesEnum.EXPLODING_THIS_TURN.getState(), AnimationsEnum.BOMB_FAST.getAnimation());
		bomb.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(explosionCompo.getTurnsToExplode() > 0 ? StatesEnum.EXPLODING_IN_SEVERAL_TURNS.getState() : StatesEnum.EXPLODING_THIS_TURN.getState());
		bomb.add(stateCompo);
		
		ParentEntityComponent parentCompo = engine.createComponent(ParentEntityComponent.class);
		parentCompo.setParent(parentEntity);
		bomb.add(parentCompo);
		
		engine.addEntity(bomb);
		
		return bomb;
	}
	
	
	public Entity createSkill(Room room, Entity parent, SkillEnum type, int skillNumber) {
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
		skillMoveComponent.room = room;
		skillMoveComponent.setMoveSpeed(0);
		skillMoveComponent.setMoveRemaining(0);
		skillEntity.add(skillMoveComponent);
		
		AttackComponent parentAttackCompo = Mappers.attackComponent.get(parent);
		
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		attackComponent.room = room;
		attackComponent.setAttackType(type.getAttackType());
		attackComponent.setRangeMin(type.getRangeMin());
		attackComponent.setRangeMax(type.getRangeMax());
		attackComponent.setStrength(type.getStrength());
		attackComponent.setAmmoType(type.getAmmosType());
		attackComponent.setAmmosUsedPerAttack(type.getNbOfAmmosPerAttack());
		attackComponent.setSkillNumber(skillNumber);
		attackComponent.setParentAttackCompo(parentAttackCompo);
		skillEntity.add(attackComponent);
		
		if (type == SkillEnum.BOMB) {
			attackComponent.setStrengthDifferential(false);
			attackComponent.setBombRadius(2);
			attackComponent.setBombTurnsToExplode(2);
		}
		
		// Wheel
		WheelComponent baseWheelComponent = null;

		switch(skillNumber) {
		case 1:
			playerComponent.setSkillMelee(skillEntity);
			
			baseWheelComponent = engine.createComponent(WheelComponent.class);
			baseWheelComponent.addSector(75, Hit.HIT);
			baseWheelComponent.addSector(10, Hit.MISS);
			baseWheelComponent.addSector(10, Hit.CRITICAL);
			baseWheelComponent.addSector(10, Hit.MISS);
			baseWheelComponent.addSector(75, Hit.HIT);
			baseWheelComponent.addSector(20, Hit.GRAZE);
			baseWheelComponent.addSector(140, Hit.MISS);
			baseWheelComponent.addSector(20, Hit.GRAZE);
			skillEntity.add(baseWheelComponent);
			
			AttackAnimation attackAnimation = new AttackAnimation(
					new Animation<>(0.03f, Assets.slash_animation), 
					new Animation<>(0.03f, Assets.slash_critical_animation), true);
			attackComponent.setAttackAnimation(attackAnimation);
			break;
		case 2:
			playerComponent.setSkillRange(skillEntity);
			
			baseWheelComponent = engine.createComponent(WheelComponent.class);
			baseWheelComponent.addSector(45, Hit.HIT);
			baseWheelComponent.addSector(5, Hit.CRITICAL);
			baseWheelComponent.addSector(50, Hit.MISS);
			baseWheelComponent.addSector(20, Hit.GRAZE);
			
			baseWheelComponent.addSector(45, Hit.HIT);
			baseWheelComponent.addSector(5, Hit.CRITICAL);
			baseWheelComponent.addSector(50, Hit.MISS);
			baseWheelComponent.addSector(20, Hit.GRAZE);
			
			baseWheelComponent.addSector(45, Hit.HIT);
			baseWheelComponent.addSector(5, Hit.CRITICAL);
			baseWheelComponent.addSector(50, Hit.MISS);
			baseWheelComponent.addSector(20, Hit.GRAZE);
			skillEntity.add(baseWheelComponent);

			attackAnimation = new AttackAnimation(
					new Animation<>(0.1f, Assets.arrow), 
					new Animation<>(0.1f, Assets.arrow), true);
			attackComponent.setAttackAnimation(attackAnimation);

			break;
		case 3:
			playerComponent.setSkillBomb(skillEntity);
			
			attackAnimation = new AttackAnimation(
					new Animation<>(0.2f, Assets.bomb_animation), 
					new Animation<>(0.2f, Assets.bomb_animation), false);
			attackComponent.setAttackAnimation(attackAnimation);

			break;
		case 4:
			playerComponent.setSkillThrow(skillEntity);
			
			attackComponent.setAttackAnimation(
					new AttackAnimation(null, null, false));

			break;
			
			default:
				break;
		}
		
		return skillEntity;
	}
	
	
	/**
	 * Create a vase.
	 * @param pos the position
	 * @param moveSpeed the speed
	 * @return the enemy entity
	 */
	public Entity createVase(Room room, Vector2 pos) {
		Entity vaseEntity = engine.createEntity();
		vaseEntity.flags = EntityFlagEnum.DESTRUCTIBLE_VASE.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		int nextInt = RandomSingleton.getInstance().getSeededRandom().nextInt(2);
		spriteCompo.setSprite(new Sprite(nextInt == 0 ? Assets.destructible_vase : Assets.destructible_vase_big));
		vaseEntity.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(vaseEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.DESTRUCTIBLE;
		vaseEntity.add(gridPosition);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		vaseEntity.add(solidComponent);
		
		DestructibleComponent destructibleComponent = engine.createComponent(DestructibleComponent.class);
		destructibleComponent.setDestroyedTexture(nextInt == 0 ? Assets.destructible_vase_destroyed : Assets.destructible_vase_big_destroyed);
		vaseEntity.add(destructibleComponent);
				
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(new VaseItemPool());
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.COMMON, 50);
		dropRate.add(ItemPoolRarity.RARE, 1);
		lootRewardCompo.setDropRate(dropRate);
		vaseEntity.add(lootRewardCompo);
				
		engine.addEntity(vaseEntity);
		return vaseEntity;
	}
	
	/**
	 * Create an ammo crate.
	 * @param pos the position
	 * @param moveSpeed the speed
	 * @return the enemy entity
	 */
	public Entity createAmmoCrate(Room room, Vector2 pos) {
		Entity crateEntity = engine.createEntity();
		crateEntity.flags = EntityFlagEnum.DESTRUCTIBLE_AMMO_CRATE.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		int nextInt = RandomSingleton.getInstance().getSeededRandom().nextInt(2);
		spriteCompo.setSprite(new Sprite(Assets.destructible_ammo_crate));
		crateEntity.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(crateEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.DESTRUCTIBLE;
		crateEntity.add(gridPosition);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		crateEntity.add(solidComponent);
		
		DestructibleComponent destructibleComponent = engine.createComponent(DestructibleComponent.class);
		destructibleComponent.setDestroyedTexture(Assets.destructible_ammo_crate_destroyed);
		destructibleComponent.setDestroyableWithWeapon(true);
		crateEntity.add(destructibleComponent);
				
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(new AmmoCrateItemPool());
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.COMMON, 80);
		dropRate.add(ItemPoolRarity.RARE, 5);
		lootRewardCompo.setDropRate(dropRate);
		crateEntity.add(lootRewardCompo);
				
		engine.addEntity(crateEntity);
		return crateEntity;
	}
	
	
	/**
	 * Create a dialog popin
	 * @param pos the position
	 * @return the dialog
	 */
	public Entity createDialogPopin(String text, Vector2 pos, float duration, Room room) {
		Entity dialogEntity = engine.createEntity();
		dialogEntity.flags = EntityFlagEnum.DIALOG_POPIN.getFlag();

		DialogComponent dialogCompo = engine.createComponent(DialogComponent.class);
		dialogCompo.setPos(pos);
		dialogCompo.setDuration(duration);
		dialogCompo.setText(text);
		dialogEntity.add(dialogCompo);
		
		room.setDialog(dialogEntity);

    	return dialogEntity;
	}
	
	
}
