/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.BlockExplosionComponent;
import com.dokkaebistudio.tacticaljourney.components.BlockVisibilityComponent;
import com.dokkaebistudio.tacticaljourney.components.ChasmComponent;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.DialogComponent;
import com.dokkaebistudio.tacticaljourney.components.ExplosiveComponent;
import com.dokkaebistudio.tacticaljourney.components.FlyComponent;
import com.dokkaebistudio.tacticaljourney.components.GravityComponent;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.components.PanelComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.WormholeComponent;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackSkill;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.RecyclingMachineComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.SkillComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WheelComponent;
import com.dokkaebistudio.tacticaljourney.components.transition.DoorComponent;
import com.dokkaebistudio.tacticaljourney.components.transition.ExitComponent;
import com.dokkaebistudio.tacticaljourney.components.transition.SecretDoorComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.enums.TileEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.skills.SkillEnum;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
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
	
	/** The destructible factory. */
	public DestructibleFactory destructibleFactory;
	
	/** The creep factory. */
	public CreepFactory creepFactory;
	
	/** The lootable factory. */
	public LootableFactory lootableFactory;

	/** The orb factory. */
	public OrbFactory orbFactory;
		
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
		this.orbFactory = new OrbFactory(e, this);
		this.destructibleFactory = new DestructibleFactory(e, this);
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
			case H_WALL:
				result = this.createHeavyWall(room, pos);
				break;
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
			case BUSH:
				result = this.creepFactory.createBush(room, pos, false);
				break;
			case CLOVER_BUSH:
				result = this.creepFactory.createBush(room, pos, true);
				break;
			case VINES_BUSH:
				result = this.creepFactory.createVinesBush(room, pos);
				break;
		}
		return result;
	}
	
	
	public Entity createHeavyWall(Room room, Vector2 pos) {
		Entity wallEntity = engine.createEntity();
		wallEntity.flags = EntityFlagEnum.HEAVY_WALL.getFlag();
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.HEAVY_WALL_TITLE);
		inspect.setDescription(Descriptions.HEAVY_WALL_DESCRIPTION);
		wallEntity.add(inspect);

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(wallEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.WALL;
    	wallEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(Assets.heavy_wall);
    	wallEntity.add(spriteCompo);
    	
    	SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
    	wallEntity.add(solidComponent);    	
    	
    	BlockVisibilityComponent blockVisibilityComponent = engine.createComponent(BlockVisibilityComponent.class);
    	wallEntity.add(blockVisibilityComponent);
    	
    	BlockExplosionComponent blockExplosionComponent = engine.createComponent(BlockExplosionComponent.class);
		wallEntity.add(blockExplosionComponent);				
    	
		engine.addEntity(wallEntity);

    	return wallEntity;
	}	
	
	public Entity createWall(Room room, Vector2 pos) {
		Entity wallEntity = engine.createEntity();
		wallEntity.flags = EntityFlagEnum.WALL.getFlag();
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.WALL_TITLE);
		inspect.setDescription(Descriptions.WALL_DESCRIPTION);
		wallEntity.add(inspect);

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(wallEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.WALL;
    	wallEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(Assets.wall);
    	wallEntity.add(spriteCompo);
    	
    	SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
    	wallEntity.add(solidComponent);
    	
    	BlockVisibilityComponent blockVisibilityComponent = engine.createComponent(BlockVisibilityComponent.class);
    	wallEntity.add(blockVisibilityComponent);
    	
    	BlockExplosionComponent blockExplosionComponent = engine.createComponent(BlockExplosionComponent.class);
		wallEntity.add(blockExplosionComponent);
    	
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	destructibleCompo.setDestroyedTexture(Assets.wall_destroyed);
    	wallEntity.add(destructibleCompo);
		
		GravityComponent gravityCompo = engine.createComponent(GravityComponent.class);
		wallEntity.add(gravityCompo);
    	
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().wall);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 2f);
		dropRate.add(ItemPoolRarity.COMMON, 90f);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		wallEntity.add(lootRewardCompo);
				
    	
		engine.addEntity(wallEntity);

    	return wallEntity;
	}	

	
	public Entity createChasm(Room room, Vector2 pos) {
		Entity chasmEntity = engine.createEntity();
		chasmEntity.flags = EntityFlagEnum.CHASM.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.CHASM_TITLE);
		inspect.setDescription(Descriptions.CHASM_TITLE);
		chasmEntity.add(inspect);

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(chasmEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.TILE;
    	chasmEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(Assets.tile_pit);
    	chasmEntity.add(spriteCompo);
    	
    	ChasmComponent chasmCompo = engine.createComponent(ChasmComponent.class);
    	chasmEntity.add(chasmCompo);   
    	
		engine.addEntity(chasmEntity);

    	return chasmEntity;
	}	
	
	public Entity createDoor(Room room, Vector2 pos, Room targetedRoom) {
		Entity doorEntity = engine.createEntity();
		doorEntity.flags = EntityFlagEnum.DOOR.getFlag();
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.DOOR_TITLE);
		inspect.setDescription(Descriptions.DOOR_DESCRIPTION);
		doorEntity.add(inspect);

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(doorEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.DOOR;
    	doorEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(Assets.door_opened);
    	doorEntity.add(spriteCompo);
    	
    	DoorComponent doorCompo = engine.createComponent(DoorComponent.class);
    	doorCompo.setTargetedRoom(targetedRoom);
    	doorCompo.setOpened( true);
    	doorEntity.add(doorCompo);
    	
		engine.addEntity(doorEntity);

    	return doorEntity;
	}
	
	public Entity createSecretDoor(Room room, Vector2 pos) {
		Entity secretDoorEntity = engine.createEntity();
		secretDoorEntity.flags = EntityFlagEnum.SECRET_DOOR.getFlag();
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.SECRET_DOOR_TITLE);
		inspect.setDescription(Descriptions.SECRET_DOOR_DESCRIPTION);
		secretDoorEntity.add(inspect);

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(secretDoorEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.DOOR;
    	secretDoorEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(Assets.secret_door_closed);
    	secretDoorEntity.add(spriteCompo);
    	
    	SecretDoorComponent secretDoorCompo = engine.createComponent(SecretDoorComponent.class);
    	secretDoorCompo.setOpened( false);
    	secretDoorEntity.add(secretDoorCompo);
    	
		engine.addEntity(secretDoorEntity);

    	return secretDoorEntity;
	}
	
	public Entity createExit(Room room, Vector2 pos, boolean opened) {
		Entity exitEntity = engine.createEntity();
		exitEntity.flags = EntityFlagEnum.EXIT.getFlag();
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.EXIT_TITLE);
		inspect.setDescription(Descriptions.EXIT_DESCRIPTION);
		exitEntity.add(inspect);

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
    	spriteCompo.setSprite(opened ? Assets.exit_opened : Assets.exit_closed);
    	exitEntity.add(spriteCompo);
    	
    	ExitComponent exitCompo = engine.createComponent(ExitComponent.class);
    	exitCompo.setOpened(opened);
    	exitEntity.add(exitCompo);

		engine.addEntity(exitEntity);

    	return exitEntity;
	}
	
	
	public Entity createWoodenPanel(Room room, Vector2 pos) {
		Entity exitEntity = engine.createEntity();
		exitEntity.flags = EntityFlagEnum.WOODEN_PANEL.getFlag();
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.WOODEN_PANEL_TITLE);
		inspect.setDescription(Descriptions.WOODEN_PANEL_DESCRIPTION);
		exitEntity.add(inspect);

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(exitEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.ENEMY;
    	exitEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(Assets.wooden_panel);
    	exitEntity.add(spriteCompo);
    	
    	PanelComponent panelComponent = engine.createComponent(PanelComponent.class);
    	panelComponent.init();
    	exitEntity.add(panelComponent);
    	
		GravityComponent gravityCompo = engine.createComponent(GravityComponent.class);
		exitEntity.add(gravityCompo);
    	
		engine.addEntity(exitEntity);

    	return exitEntity;
	}
	
	public Entity createWallGate(Room room, Vector2 pos, boolean opened) {
		Entity exitEntity = engine.createEntity();
		exitEntity.flags = EntityFlagEnum.WALL_GATE.getFlag();
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(opened ? Descriptions.WALL_GATE_OPENED_TITLE : Descriptions.WALL_GATE_CLOSED_TITLE);
		inspect.setDescription(opened ? Descriptions.WALL_GATE_OPENED_DESCRIPTION : Descriptions.WALL_GATE_CLOSED_DESCRIPTION);
		exitEntity.add(inspect);

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(exitEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.WALL;
    	exitEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(opened ? Assets.wall_gate_opened : Assets.wall_gate_closed);
    	exitEntity.add(spriteCompo);
    	
    	if (!opened) {
    		SolidComponent solidCompo = engine.createComponent(SolidComponent.class);
    		exitEntity.add(solidCompo);
    		BlockVisibilityComponent blockVisibilityCompo = engine.createComponent(BlockVisibilityComponent.class);
    		exitEntity.add(blockVisibilityCompo);
    		BlockExplosionComponent blockExploCompo = engine.createComponent(BlockExplosionComponent.class);
    		exitEntity.add(blockExploCompo);
    	}
    	
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
    	spriteCompo.setSprite(Assets.tile_movable);
    	movableTileEntity.add(spriteCompo);
    	
    	movableTileEntity.add(engine.createComponent(FlyComponent.class));
    	
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
    	spriteCompo.setSprite(explosion ? Assets.tile_explosion : Assets.tile_attackable);
    	attackableTileEntity.add(spriteCompo);
    	
    	attackableTileEntity.add(engine.createComponent(FlyComponent.class));

    	
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
    	spriteCompo.setSprite(Assets.tile_movable_selected);
    	redCross.add(spriteCompo);
    	
    	redCross.add(engine.createComponent(FlyComponent.class));

    	
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
    	spriteCompo.setSprite(Assets.tile_movable_waypoint);
    	waypoint.add(spriteCompo);
    	
    	waypoint.add(engine.createComponent(FlyComponent.class));

    	
		engine.addEntity(waypoint);
    	return waypoint;
	}
	
	
	
	/**
	 * Create a simple sprite entity.
	 * @param texture the texture
	 * @param flag the flag of the entity (for debugging purpose)
	 * @return the sprite entity
	 */
	public Entity createSprite(RegionDescriptor texture, EntityFlagEnum flag) {
		return createSprite(null, texture, flag, null);
	}
	
	/**
	 * Create a simple sprite entity.
	 * @param pos the position
	 * @param texture the texture
	 * @param flag the flag of the entity (for debugging purpose)
	 * @return the sprite entity
	 */
	public Entity createSprite(Vector3 pos, RegionDescriptor texture, EntityFlagEnum flag) {
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
	public Entity createSprite(Vector3 pos, RegionDescriptor texture, EntityFlagEnum flag, Room room) {
		Entity sprite = engine.createEntity();
		sprite.flags = flag.getFlag();
		
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(texture);
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
	public Entity createSpriteOnTile(Vector2 tilePos, int zIndex, RegionDescriptor texture, EntityFlagEnum flag, Room room) {
		Entity sprite = engine.createEntity();
		sprite.flags = flag.getFlag();
		
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(texture);
		sprite.add(spriteCompo);
		
		GridPositionComponent gridPos = engine.createComponent(GridPositionComponent.class);
		gridPos.coord(sprite, tilePos, room);
		gridPos.zIndex = zIndex;
		sprite.add(gridPos);
		
		room.addEntity(sprite);
		return sprite;
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
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.BOMB_TITLE);
		inspect.setDescription(Descriptions.BOMB_DESCRIPTION);
		bomb.add(inspect);


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
		animationCompo.addAnimation(StatesEnum.EXPLODING_IN_SEVERAL_TURNS, AnimationSingleton.getInstance().bomb_slow);
		animationCompo.addAnimation(StatesEnum.EXPLODING_THIS_TURN, AnimationSingleton.getInstance().bomb_fast);
		bomb.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(explosionCompo.getTurnsToExplode() > 0 ? StatesEnum.EXPLODING_IN_SEVERAL_TURNS : StatesEnum.EXPLODING_THIS_TURN);
		bomb.add(stateCompo);
		
		ParentEntityComponent parentCompo = engine.createComponent(ParentEntityComponent.class);
		parentCompo.setParent(parentEntity);
		bomb.add(parentCompo);
		
		GravityComponent gravityCompo = engine.createComponent(GravityComponent.class);
		bomb.add(gravityCompo);
		
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
		skillMoveComponent.setMoveSpeed(0);
		skillMoveComponent.setMoveRemaining(0);
		skillEntity.add(skillMoveComponent);
		
		AttackComponent attackComponent = engine.createComponent(AttackComponent.class);
		
		AttackSkill as = new AttackSkill();
		as.setRangeMin(type.getRangeMin());
		as.setRangeMax(type.getRangeMax());
		as.setStrength(type.getStrength());
		as.setAttackType(type.getAttackType());
		attackComponent.getSkills().add(as);
		
		attackComponent.setAmmoType(type.getAmmosType());
		attackComponent.setAmmosUsedPerAttack(type.getNbOfAmmosPerAttack());
		attackComponent.setSkillNumber(skillNumber);
		attackComponent.setParentEntity(parent);
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
			
			AttackAnimation attackAnimation = new AttackAnimation(AnimationSingleton.getInstance().attack_slash, 
					AnimationSingleton.getInstance().attack_slash_critical, true);
			as.setAttackAnimation(attackAnimation);
			attackComponent.setAccuracy(1);
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

			attackAnimation = new AttackAnimation(AnimationSingleton.getInstance().arrow, 
					AnimationSingleton.getInstance().arrow, true);
			as.setAttackAnimation(attackAnimation);
			attackComponent.setAccuracy(1);
			attackComponent.setStrengthDifferential(true);

			break;
		case 3:
			playerComponent.setSkillBomb(skillEntity);
			
			attackAnimation = new AttackAnimation(AnimationSingleton.getInstance().bomb, 
					AnimationSingleton.getInstance().bomb, false);
			as.setAttackAnimation(attackAnimation);

			break;
		case 4:
			playerComponent.setSkillThrow(skillEntity);
			
			as.setAttackAnimation(
					new AttackAnimation(null, null, false));

			break;
			
			default:
				break;
		}
		
		return skillEntity;
	}
	
	
	/**
	 * Create a dialog popin
	 * @param pos the position
	 * @return the dialog
	 */
	public Entity createDialogPopin(String speaker, String text, float duration, Room room) {
		Entity dialogEntity = engine.createEntity();
		dialogEntity.flags = EntityFlagEnum.DIALOG_POPIN.getFlag();

		DialogComponent dialogCompo = engine.createComponent(DialogComponent.class);
		dialogCompo.setRoom(room);
		dialogCompo.setSpeaker(speaker);
		dialogCompo.setDuration(duration);
		dialogCompo.setText(text);
		dialogEntity.add(dialogCompo);
		
		room.setDialog(dialogEntity);

    	return dialogEntity;
	}
	
	/**
	 * Create a wormhole.
	 * @param room the parent room
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createWormhole(Room room, Vector2 tilePos, Vector2 destination) {
		Entity wormhole = engine.createEntity();
		wormhole.flags = EntityFlagEnum.WORMHOLE.getFlag();
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.WORMHOLE_TITLE);
		inspect.setDescription(Descriptions.WORMHOLE_DESCRIPTION);
		wormhole.add(inspect);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(wormhole, tilePos, room);
		gridPosition.zIndex = ZIndexConstants.WORMHOLE;
		wormhole.add(gridPosition);
		
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		wormhole.add(spriteCompo);

		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.addAnimation(StatesEnum.PORTAL, AnimationSingleton.getInstance().portal);
		wormhole.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(null);
		wormhole.add(stateCompo);		
		
		WormholeComponent wormholeCompo = engine.createComponent(WormholeComponent.class);
		wormholeCompo.setDestination(destination);
		wormhole.add(wormholeCompo);
		
		engine.addEntity(wormhole);
		
		return wormhole;
	}
	
	
	public Entity createRecyclingMachine(Room room, Vector2 tilePos) {
		Entity recycler = engine.createEntity();
		recycler.flags = EntityFlagEnum.RECYCLER.getFlag();
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.RECYCLER_TITLE);
		inspect.setDescription(Descriptions.RECYCLER_DESCRIPTION);
		recycler.add(inspect);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(recycler, tilePos, room);
		gridPosition.zIndex = ZIndexConstants.STATUE;
		recycler.add(gridPosition);
		
		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(Assets.recycling_machine);
		recycler.add(spriteCompo);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		recycler.add(solidComponent);
		
		BlockVisibilityComponent blockVisibilityCompo = engine.createComponent(BlockVisibilityComponent.class);
		recycler.add(blockVisibilityCompo);
		
		RecyclingMachineComponent recyclingCompo = engine.createComponent(RecyclingMachineComponent.class);
		recycler.add(recyclingCompo);
		
		GravityComponent gravityCompo = engine.createComponent(GravityComponent.class);
		recycler.add(gravityCompo);
		
		engine.addEntity(recycler);
		
		return recycler;
	}
	
}
