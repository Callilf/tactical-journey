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
public final class EnemyFactory {
	
	/** The gdx pooled engine. */
	public PooledEngine engine;
	
	/** the entity factory. */
	public EntityFactory entityFactory;
	
	// textures are stored so we don't fetch them from the atlas each time (atlas.findRegion is SLOW)
	private TextureAtlas.AtlasRegion spiderTexture;
	private TextureAtlas.AtlasRegion scorpionTexture;

	/**
	 * Constructor.
	 * @param e the engine
	 */
	public EnemyFactory(PooledEngine e, EntityFactory ef) {
		this.engine = e;
		this.entityFactory = ef;
		
		spiderTexture = Assets.getTexture(Assets.enemy_spider);
		scorpionTexture = Assets.getTexture(Assets.enemy_scorpion);
	}
	

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
		healthComponent.setHpDisplayer(this.entityFactory.createTextOnTile(pos, String.valueOf(healthComponent.getHp()), 100));
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
		healthComponent.setHpDisplayer(this.entityFactory.createTextOnTile(pos, String.valueOf(healthComponent.getHp()), 100));
		enemyEntity.add(healthComponent);

		engine.addEntity(enemyEntity);
		return enemyEntity;
	}
	
}
