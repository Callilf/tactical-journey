/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.util;

import com.badlogic.ashley.core.ComponentMapper;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.DoorComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.ExplosiveComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.LevelUpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.ParentRoomComponent;
import com.dokkaebistudio.tacticaljourney.components.SlowMovementComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.DamageDisplayComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.display.VisualEffectComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.SkillComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WheelComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WheelModifierComponent;

/**
 * The util class containing all ComponentMappers.
 * @author Callil
 *
 */
public final class Mappers {

	public final static ComponentMapper<PlayerComponent> playerComponent = ComponentMapper.getFor(PlayerComponent.class);
	public final static ComponentMapper<EnemyComponent> enemyComponent = ComponentMapper.getFor(EnemyComponent.class);

	public final static ComponentMapper<SpriteComponent> spriteComponent = ComponentMapper.getFor(SpriteComponent.class);
	public final static ComponentMapper<AnimationComponent> animationComponent = ComponentMapper.getFor(AnimationComponent.class);
	public final static ComponentMapper<StateComponent> stateComponent = ComponentMapper.getFor(StateComponent.class);
	public final static ComponentMapper<VisualEffectComponent> visualEffectComponent = ComponentMapper.getFor(VisualEffectComponent.class);

	
	public final static ComponentMapper<ParentEntityComponent> parentEntityComponent = ComponentMapper.getFor(ParentEntityComponent.class);
	public final static ComponentMapper<ParentRoomComponent> parentRoomComponent = ComponentMapper.getFor(ParentRoomComponent.class);
	public final static ComponentMapper<GridPositionComponent> gridPositionComponent = ComponentMapper.getFor(GridPositionComponent.class);
	public final static ComponentMapper<TileComponent> tileComponent = ComponentMapper.getFor(TileComponent.class);
	public final static ComponentMapper<DoorComponent> doorComponent = ComponentMapper.getFor(DoorComponent.class);
	
	public final static ComponentMapper<MoveComponent> moveComponent = ComponentMapper.getFor(MoveComponent.class);
	public final static ComponentMapper<AttackComponent> attackComponent = ComponentMapper.getFor(AttackComponent.class);
	public final static ComponentMapper<SkillComponent> skillComponent = ComponentMapper.getFor(SkillComponent.class);
	public final static ComponentMapper<AmmoCarrierComponent> ammoCarrierComponent = ComponentMapper.getFor(AmmoCarrierComponent.class);
	public final static ComponentMapper<ExperienceComponent> experienceComponent = ComponentMapper.getFor(ExperienceComponent.class);


	public final static ComponentMapper<ItemComponent> itemComponent = ComponentMapper.getFor(ItemComponent.class);

	public final static ComponentMapper<HealthComponent> healthComponent = ComponentMapper.getFor(HealthComponent.class);
	public final static ComponentMapper<ExpRewardComponent> expRewardComponent = ComponentMapper.getFor(ExpRewardComponent.class);
	public final static ComponentMapper<LevelUpRewardComponent> levelUpRewardComponent = ComponentMapper.getFor(LevelUpRewardComponent.class);


	public final static ComponentMapper<TextComponent> textComponent = ComponentMapper.getFor(TextComponent.class);
	public final static ComponentMapper<DamageDisplayComponent> damageDisplayCompoM = ComponentMapper.getFor(DamageDisplayComponent.class);
	
	public final static ComponentMapper<WheelModifierComponent> wheelModifierComponentMapper = ComponentMapper.getFor(WheelModifierComponent.class);
	public final static ComponentMapper<WheelComponent> wheelComponentMapper = ComponentMapper.getFor(WheelComponent.class);

	
	public final static ComponentMapper<SolidComponent> solidComponent = ComponentMapper.getFor(SolidComponent.class);
	public final static ComponentMapper<SlowMovementComponent> slowMoveComponent = ComponentMapper.getFor(SlowMovementComponent.class);
	public final static ComponentMapper<ExplosiveComponent> explosiveComponent = ComponentMapper.getFor(ExplosiveComponent.class);
	public final static ComponentMapper<DestructibleComponent> destructibleComponent = ComponentMapper.getFor(DestructibleComponent.class);

	
	
	/** No constructor. */
	private Mappers() {}
	
}
