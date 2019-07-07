/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import java.util.List;
import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffStunned;
import com.dokkaebistudio.tacticaljourney.util.AnimatedImage;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * A potion that bursts into flames when used or thrown.
 * @author Callil
 *
 */
public class ItemSmokebomb extends AbstractItem {

	public ItemSmokebomb() {
		super(ItemEnum.SMOKE_BOMB, Assets.smoke_bomb_item, false, true);
		setRecyclePrice(7);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_SMOKE_BOMB_DESCRIPTION;		
	}
	
	@Override
	public String getActionLabel() {
		return "Use";
	}
	
	@Override
	public boolean isStackable() {
		return true;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		GridPositionComponent userPos = Mappers.gridPositionComponent.get(user);
		
		createSmokeEffect(userPos.coord());
		
		// All enemies become unalerted
		for(Entity enemy : room.getEnemies()) {
			if (enemy == user) continue;
			Mappers.aiComponent.get(enemy).setAlerted(false, enemy, user);
		}
		
		// Adjacent enemies are stunned
		List<Tile> adjacentTiles = TileUtil.getAdjacentTiles(userPos.coord(), room);
		for (Tile adjacentTile : adjacentTiles) {
			Optional<Entity> stunnableEntity = TileUtil.getEntityWithComponentOnTile(adjacentTile.getGridPos(), StatusReceiverComponent.class, room);
			if (stunnableEntity.isPresent() && stunnableEntity.get() != user) {
				StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(stunnableEntity.get());
				if (statusReceiverComponent != null) {
					statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffStunned(1));
				}
			}
		}

		return true;
	}
	
	@Override
	public void onThrow(Vector2 thrownPosition, Entity thrower, Entity item, Room room) {
		super.onThrow(thrownPosition, thrower, item, room);
				
		createSmokeEffect(thrownPosition);
		room.removeEntity(item);

		Optional<Entity> stunnableEntity = TileUtil.getEntityWithComponentOnTile(thrownPosition, StatusReceiverComponent.class, room);
		if (stunnableEntity.isPresent()) {
			StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(stunnableEntity.get());
			if (statusReceiverComponent != null) {
				statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffStunned(2));
			}
		}
	}
	
	
	/**
	 * Smoke effect when evading.
	 * @param gridPos the tile pos
	 */
	private void createSmokeEffect(Vector2 gridPos) {
		final AnimatedImage smokeAnim = new AnimatedImage(AnimationSingleton.getInstance().smoke_bomb, false);
		Action smokeAnimFinishAction = new Action(){
		  @Override
		  public boolean act(float delta){
			smokeAnim.remove();
		    return true;
		  }
		};
		smokeAnim.setFinishAction(smokeAnimFinishAction);
		
		smokeAnim.setOrigin(Align.center);
		smokeAnim.addAction(Actions.scaleTo(1.5f, 1.5f));
		
		PoolableVector2 pixelPos = TileUtil.convertGridPosIntoPixelPos(gridPos);
		smokeAnim.setPosition(pixelPos.x + GameScreen.GRID_SIZE/2 - smokeAnim.getWidth()/2, pixelPos.y + GameScreen.GRID_SIZE/2 - smokeAnim.getHeight()/2);
		pixelPos.free();
		
		
		GameScreen.fxStage.addActor(smokeAnim);
	}
}
