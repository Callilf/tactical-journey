package com.dokkaebistudio.tacticaljourney.creature.enemies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.creature.Creature;
import com.dokkaebistudio.tacticaljourney.creeps.CreepBanana;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffStunned;
import com.dokkaebistudio.tacticaljourney.systems.creatures.subsystems.OrangutanSubSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.util.actions.ActionsUtil;

public class EnemyOrangutan extends Creature {
	
	private boolean sleeping = true;
	private RandomXS128 random;
	
	public EnemyOrangutan() {}
	
	public EnemyOrangutan(RandomXS128 random) {
		this.random = random;
	}

	@Override
	public String title() {
		return "Orangutan alpha male";
	}
	
	
	@Override
	public void onRoomVisited(Entity enemy, Room room) {
		// Place the enemy so that it is far enough from the player
		GridPositionComponent playerPos = Mappers.gridPositionComponent.get(GameScreen.player);
		if (playerPos.coord().x < 11) {
			MovementHandler.placeEntity(enemy, OrangutanSubSystem.RIGHT_CLONE_TILE, room);
		} else if (playerPos.coord().x > 11) {
			MovementHandler.placeEntity(enemy, OrangutanSubSystem.LEFT_CLONE_TILE, room);
		} else if (playerPos.coord().y < 6) {
			MovementHandler.placeEntity(enemy, OrangutanSubSystem.UP_CLONE_TILE, room);
		} else if (playerPos.coord().y > 6) {
			MovementHandler.placeEntity(enemy, OrangutanSubSystem.DOWN_CLONE_TILE, room);
		}
		
		// Orient the sprite towards the player
		Mappers.spriteComponent.get(enemy).orientSprite(enemy, playerPos.coord());
		
		List<Tile> freeTiles = new ArrayList<>();
		List<Tile> allTiles = TileUtil.getAllTiles(room);
		allTiles.stream().filter(tile -> tile.isWalkable()).forEachOrdered(freeTiles::add);
		
		Collections.shuffle(freeTiles, random);
		
		for (int i=0 ; i<10 ; i++) {
			room.entityFactory.creepFactory.createBananaPeel(room, freeTiles.get(i).getGridPos(), enemy);
		}
	}
	

	@Override
	public void onStartTurn(Entity creature, Room room) {
		
		// Wake up if an ally slips on a banana
		AIComponent aiComponent = Mappers.aiComponent.get(creature);
		if (sleeping || !aiComponent.isAlerted()) {
			for (Entity ally : room.getAllies()) {
				StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(ally);
				if (statusReceiverComponent != null && statusReceiverComponent.hasStatus(StatusDebuffStunned.class)) {
					sleeping = false;
					aiComponent.setAlerted(true, creature, ally);
				}
			}
		}
		
	}
	
	@Override
	public void onReceiveDamage(int damage, Entity creature, Entity attacker, Room room) {
		if (damage > 0) {
			// When taking damage, drops 3 banana peels on close tiles that doesn't already have a banana peel
			GridPositionComponent orangutanPos = Mappers.gridPositionComponent.get(creature);
			List<Tile> tilesAtProximity = TileUtil.getTilesAtProximity(orangutanPos.coord(), 3, room);
			Collections.shuffle(tilesAtProximity, RandomSingleton.getInstance().getUnseededRandom());
			
			tilesAtProximity.parallelStream()
				.filter(t -> t.isWalkable() && !hasBanana(t, room))
				.limit(3)
				.forEachOrdered(t -> throwBananas(orangutanPos.coord(), t, room));
		}
	}
	
	
	
	public static int numRabbits(int[] answers) {
        
		// Handle exceptional cases
		if (answers == null || answers.length == 0) {
			return 0;
		}
		
		// Build a map that tells for each answer how many times it has been given
		Map<Integer, Integer> map = new HashMap<>();
		for(int answer : answers) {
			map.put(answer, map.containsKey(answer) ? map.get(answer) + 1 : 1);
		}
		
		// count
		int total = 0;
		for(Entry<Integer, Integer> entry : map.entrySet()) {
			if (entry.getKey() == 0) {
				total += entry.getValue();
			} else {
				double sum = entry.getValue().intValue();
				double answer = entry.getKey().intValue() + 1;
				total += ((int) Math.ceil(sum / answer)) * (entry.getKey()+1); 
			}
		}
		
		return total;
    }
	
	public static void main(String[] args) {
		System.out.println(numRabbits(new int[] {10, 10, 10}));
	}
	
	
	
	//*******************
	// Utils
	
	private void throwBananas(Vector2 orangutanPos, Tile targetedTile, Room room) {
		Image attackImage = new Image(Assets.creep_banana.getRegion());
		Vector2 playerPixelPos = TileUtil.convertGridPosIntoPixelPos(orangutanPos);
		attackImage.setPosition(playerPixelPos.x, playerPixelPos.y);
		
		Action finishAction = new Action() {
			  @Override
			  public boolean act(float delta){
				  attackImage.remove();
				  room.entityFactory.creepFactory.createBananaPeel(room, targetedTile.getGridPos(), null);
			      return true;
			  }
		};
		
		Vector2 targetPosInPixel = targetedTile.getAbsolutePos();
		attackImage.setOrigin(Align.center);

		double distance = Math.hypot(playerPixelPos.x-targetPosInPixel.x, playerPixelPos.y-targetPosInPixel.y);
		double nbTiles = Math.ceil(distance / GameScreen.GRID_SIZE);
		float duration = (float) (nbTiles * 0.1f);
		
		float rotation = (float) (nbTiles * 90);
		ActionsUtil.moveAndRotate(attackImage, targetPosInPixel, rotation, duration, finishAction);
	
		GameScreen.fxStage.addActor(attackImage);
	}
	
	
	
	public boolean hasBanana(Tile t, Room room) {
		Optional<Entity> bananaOpt = TileUtil.getEntityWithComponentOnTile(t.getGridPos(), CreepComponent.class, room);
		if (bananaOpt.isPresent()) {
			if (Mappers.creepComponent.get(bananaOpt.get()).getType() instanceof CreepBanana) {
				return true;
			}
		}
		
		return false;
	}
	
	
	
	// Getters and Setters	
	
	@Override
	public void onAlerted(Entity enemy, Entity target, Room room) {
		sleeping = false;
	}


	public boolean isSleeping() {
		return sleeping;
	}



	public void setSleeping(boolean sleeping) {
		this.sleeping = sleeping;
	}


	public RandomXS128 getRandom() {
		return random;
	}


	public void setRandom(RandomXS128 random) {
		this.random = random;
	}
}
