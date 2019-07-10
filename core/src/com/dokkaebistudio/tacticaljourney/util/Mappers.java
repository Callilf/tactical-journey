/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.util;

import com.badlogic.ashley.core.ComponentMapper;
import com.dokkaebistudio.tacticaljourney.ces.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.ChasmComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.DialogComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.EnemySpawnerComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.ExplosiveComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.FlammableComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.FlyComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.GravityComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.HumanoidComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.PanelComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.SpeakerComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.KnockbackComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.TutorialComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.WormholeComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.creep.CreepEmitterComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.creep.CreepImmunityComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.CalishkaComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.ChaliceComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.RecyclingMachineComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.SewingMachineComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.ShopKeeperComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.SoulbenderComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.neutrals.StatueComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.orbs.OrbCarrierComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.orbs.OrbComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AllyComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.SkillComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.WheelComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.WheelModifierComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.transition.DoorComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.transition.ExitComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.transition.SecretDoorComponent;

/**
 * The util class containing all ComponentMappers.
 * @author Callil
 *
 */
public final class Mappers {

	public final static ComponentMapper<PlayerComponent> playerComponent = ComponentMapper.getFor(PlayerComponent.class);
	public final static ComponentMapper<AllyComponent> allyComponent = ComponentMapper.getFor(AllyComponent.class);
	public final static ComponentMapper<EnemyComponent> enemyComponent = ComponentMapper.getFor(EnemyComponent.class);
	public final static ComponentMapper<AIComponent> aiComponent = ComponentMapper.getFor(AIComponent.class);
	
	public final static ComponentMapper<CalishkaComponent> calishkaComponent = ComponentMapper.getFor(CalishkaComponent.class);
	public final static ComponentMapper<ShopKeeperComponent> shopKeeperComponent = ComponentMapper.getFor(ShopKeeperComponent.class);
	public final static ComponentMapper<SoulbenderComponent> soulbenderComponent = ComponentMapper.getFor(SoulbenderComponent.class);
	public final static ComponentMapper<StatueComponent> statueComponent = ComponentMapper.getFor(StatueComponent.class);
	public final static ComponentMapper<ChaliceComponent> chaliceComponent = ComponentMapper.getFor(ChaliceComponent.class);
	public final static ComponentMapper<SewingMachineComponent> sewingMachineComponent = ComponentMapper.getFor(SewingMachineComponent.class);
	public final static ComponentMapper<RecyclingMachineComponent> recyclingMachineComponent = ComponentMapper.getFor(RecyclingMachineComponent.class);

	public final static ComponentMapper<HumanoidComponent> humanoidComponent = ComponentMapper.getFor(HumanoidComponent.class);

	public final static ComponentMapper<SpriteComponent> spriteComponent = ComponentMapper.getFor(SpriteComponent.class);
	public final static ComponentMapper<AnimationComponent> animationComponent = ComponentMapper.getFor(AnimationComponent.class);
	public final static ComponentMapper<StateComponent> stateComponent = ComponentMapper.getFor(StateComponent.class);
	
	public final static ComponentMapper<ParentEntityComponent> parentEntityComponent = ComponentMapper.getFor(ParentEntityComponent.class);
	public final static ComponentMapper<GridPositionComponent> gridPositionComponent = ComponentMapper.getFor(GridPositionComponent.class);
	public final static ComponentMapper<DoorComponent> doorComponent = ComponentMapper.getFor(DoorComponent.class);
	public final static ComponentMapper<SecretDoorComponent> secretDoorComponent = ComponentMapper.getFor(SecretDoorComponent.class);
	public final static ComponentMapper<ExitComponent> exitComponent = ComponentMapper.getFor(ExitComponent.class);
	public final static ComponentMapper<PanelComponent> panelComponent = ComponentMapper.getFor(PanelComponent.class);
	public final static ComponentMapper<WormholeComponent> wormholeComponent = ComponentMapper.getFor(WormholeComponent.class);
	
	
	public final static ComponentMapper<MoveComponent> moveComponent = ComponentMapper.getFor(MoveComponent.class);
	public final static ComponentMapper<AttackComponent> attackComponent = ComponentMapper.getFor(AttackComponent.class);
	public final static ComponentMapper<SkillComponent> skillComponent = ComponentMapper.getFor(SkillComponent.class);
	public final static ComponentMapper<WalletComponent> walletComponent = ComponentMapper.getFor(WalletComponent.class);
	public final static ComponentMapper<AmmoCarrierComponent> ammoCarrierComponent = ComponentMapper.getFor(AmmoCarrierComponent.class);
	public final static ComponentMapper<InventoryComponent> inventoryComponent = ComponentMapper.getFor(InventoryComponent.class);
	public final static ComponentMapper<AlterationReceiverComponent> alterationReceiverComponent = ComponentMapper.getFor(AlterationReceiverComponent.class);
	public final static ComponentMapper<StatusReceiverComponent> statusReceiverComponent = ComponentMapper.getFor(StatusReceiverComponent.class);

	
	public final static ComponentMapper<ExperienceComponent> experienceComponent = ComponentMapper.getFor(ExperienceComponent.class);

	public final static ComponentMapper<LootableComponent> lootableComponent = ComponentMapper.getFor(LootableComponent.class);
	public final static ComponentMapper<ItemComponent> itemComponent = ComponentMapper.getFor(ItemComponent.class);

	public final static ComponentMapper<HealthComponent> healthComponent = ComponentMapper.getFor(HealthComponent.class);
	public final static ComponentMapper<ExpRewardComponent> expRewardComponent = ComponentMapper.getFor(ExpRewardComponent.class);
	public final static ComponentMapper<LootRewardComponent> lootRewardComponent = ComponentMapper.getFor(LootRewardComponent.class);
	public final static ComponentMapper<EnemySpawnerComponent> enemySpawnerComponent = ComponentMapper.getFor(EnemySpawnerComponent.class);

	
	public final static ComponentMapper<TextComponent> textComponent = ComponentMapper.getFor(TextComponent.class);
	public final static ComponentMapper<DialogComponent> dialogComponent = ComponentMapper.getFor(DialogComponent.class);
	public final static ComponentMapper<SpeakerComponent> speakerComponent = ComponentMapper.getFor(SpeakerComponent.class);

	public final static ComponentMapper<WheelModifierComponent> wheelModifierComponent = ComponentMapper.getFor(WheelModifierComponent.class);
	public final static ComponentMapper<WheelComponent> wheelComponentMapper = ComponentMapper.getFor(WheelComponent.class);
	
	public final static ComponentMapper<InspectableComponent> inspectableComponent = ComponentMapper.getFor(InspectableComponent.class);
	
	public final static ComponentMapper<SolidComponent> solidComponent = ComponentMapper.getFor(SolidComponent.class);
	public final static ComponentMapper<GravityComponent> gravityComponent = ComponentMapper.getFor(GravityComponent.class);
	public final static ComponentMapper<ChasmComponent> chasmComponent = ComponentMapper.getFor(ChasmComponent.class);
	public final static ComponentMapper<FlyComponent> flyComponent = ComponentMapper.getFor(FlyComponent.class);

	public final static ComponentMapper<CreepComponent> creepComponent = ComponentMapper.getFor(CreepComponent.class);
	public final static ComponentMapper<CreepEmitterComponent> creepEmitterComponent = ComponentMapper.getFor(CreepEmitterComponent.class);
	public final static ComponentMapper<CreepImmunityComponent> creepImmunityComponent = ComponentMapper.getFor(CreepImmunityComponent.class);
	
	public final static ComponentMapper<OrbComponent> orbComponent = ComponentMapper.getFor(OrbComponent.class);
	public final static ComponentMapper<OrbCarrierComponent> orbCarrierComponent = ComponentMapper.getFor(OrbCarrierComponent.class);
	
	public final static ComponentMapper<FlammableComponent> flammableComponent = ComponentMapper.getFor(FlammableComponent.class);
	public final static ComponentMapper<ExplosiveComponent> explosiveComponent = ComponentMapper.getFor(ExplosiveComponent.class);
	public final static ComponentMapper<DestructibleComponent> destructibleComponent = ComponentMapper.getFor(DestructibleComponent.class);
	
	public final static ComponentMapper<KnockbackComponent> knockbackComponent = ComponentMapper.getFor(KnockbackComponent.class);

	
	
	public final static ComponentMapper<TutorialComponent> tutorialComponent = ComponentMapper.getFor(TutorialComponent.class);

	
	
	/** No constructor. */
	private Mappers() {}
	
}
