/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings.shinobi;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.ActionsUtil;
import com.dokkaebistudio.tacticaljourney.util.AnimatedImage;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Shinobi retaliation: throw kunai to attacker.
 * @author Callil
 *
 */
public class BlessingHangeki extends Blessing {
	
	private int chanceToProc = 33;
	private Map<Integer, Boolean> activationForRoom = new HashMap<>();

	@Override
	public String title() {
		return "Hangeki";
	}
	
	@Override
	public String description() {
		return "Upon receiving the first attack in a room, chance to retaliate by thowing a kunai that deals 7 damages at the enemy.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_hangeki;
	}
	
	@Override
	public Integer getCurrentProcChance(Entity user) {
		return chanceToProc;
	}
	
	@Override
	public void onReceive(Entity entity) {
		Room room = Mappers.gridPositionComponent.get(entity).room;
		if (!activationForRoom.containsKey(room.getIndex())) {
			activationForRoom.put(room.getIndex(), false);
		}
	}
	

	@Override
	public void onRoomVisited(Entity entity, Room room) {
		if (!activationForRoom.containsKey(room.getIndex())) {
			activationForRoom.put(room.getIndex(), false);
		}
	}
	
	@Override
	public boolean onReceiveAttack(final Entity user, final Entity attacker, final Room room) {
		if (!activationForRoom.containsKey(room.getIndex())) {
			activationForRoom.put(room.getIndex(), true);
		}
		
		if (!activationForRoom.get(room.getIndex())) {
			// Switch to activated even if it doesn't proc, so that it won't proc on nother attack in the same room.
			activationForRoom.put(room.getIndex(), true);
			
			float randomValue = RandomSingleton.getNextChanceWithKarma();
			if (randomValue < getCurrentProcChance(user)) {

				Journal.addEntry("Hangeki activated by throwing a retaliation kunaï.");
				AlterationSystem.addAlterationProc(this);
				
				// Throw the kunai
				GridPositionComponent userPos = Mappers.gridPositionComponent.get(user);
				GridPositionComponent attackerPos = Mappers.gridPositionComponent.get(attacker);
				
				final Image kunai = new Image(Assets.projectile_kunai.getRegion());
				Vector2 playerPixelPos = TileUtil.convertGridPosIntoPixelPos(userPos.coord());
				kunai.setPosition(playerPixelPos.x, playerPixelPos.y);
				
				Vector2 targetPosInPixel = TileUtil.convertGridPosIntoPixelPos(attackerPos.coord());
				kunai.setOrigin(Align.center);

				// Orient towards attacker
				double degrees = Math.atan2(
						targetPosInPixel.y - playerPixelPos.y,
					    targetPosInPixel.x - playerPixelPos.x
					) * 180.0d / Math.PI;
				kunai.setRotation((float) degrees);
				
				double distance = Math.hypot(playerPixelPos.x-targetPosInPixel.x, playerPixelPos.y-targetPosInPixel.y);
				double nbTiles = Math.ceil(distance / GameScreen.GRID_SIZE);
				float duration = (float) (nbTiles * 0.1f);

				Action removeImageAction = new Action() {
					@Override
					public boolean act(float delta) {
						AttackComponent ac = null;
						PlayerComponent playerComponent = Mappers.playerComponent.get(user);
						if (playerComponent != null) {
							ac = Mappers.attackComponent.get(playerComponent.getSkillThrow());
						}
						
						room.attackManager.applyDamage(user, attacker, 8, DamageType.NORMAL, 
								ac);

						kunai.remove();
						return true;
					}
				};
				
				ActionsUtil.move(kunai, targetPosInPixel, duration, removeImageAction);
				GameScreen.fxStage.addActor(kunai);

				
			}
		}
		
		// Did nothing, proceed with the attack
		return true;
	}

	
	@Override
	public void onFloorVisited(Entity entity, Floor floor, Room room) {
		// Changed floor, clear the activation map to save memory
		this.activationForRoom.clear();
	}
	
	//*********
	// Utils
	
	
	/**
	 * Smoke effect when evading.
	 * @param gridPos the tile pos
	 */
	private void createSmokeEffect(Vector2 gridPos) {
		final AnimatedImage smokeAnim = new AnimatedImage(AnimationSingleton.getInstance().explosion, false);
		Action smokeAnimFinishAction = new Action(){
		  @Override
		  public boolean act(float delta){
			smokeAnim.remove();
		    return true;
		  }
		};
		smokeAnim.setFinishAction(smokeAnimFinishAction);
		PoolableVector2 pixelPos = TileUtil.convertGridPosIntoPixelPos(gridPos);
		smokeAnim.setPosition(pixelPos.x, pixelPos.y);
		pixelPos.free();
		GameScreen.fxStage.addActor(smokeAnim);
	}

}
