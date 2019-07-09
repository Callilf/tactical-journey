package com.dokkaebistudio.tacticaljourney.creature.enemies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.ces.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.creatures.subsystems.OrangutanSubSystem;
import com.dokkaebistudio.tacticaljourney.creature.Creature;
import com.dokkaebistudio.tacticaljourney.creeps.CreepBanana;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemBanana;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffStunned;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.util.actions.ActionsUtil;

public class EnemyOrangutan extends Creature {
	
	private boolean sleeping = true;
	private RandomXS128 random;
	private List<Entity> bananas = new ArrayList<>();
	private boolean goingForBanana;
	
	private int damageReceived;
	
	public EnemyOrangutan() {}
	
	public EnemyOrangutan(RandomXS128 random) {
		this.random = random;
	}

	@Override
	public String title() {
		return Descriptions.ENEMY_ORANGUTAN_ALPHA_TITLE;
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
		
		
		// Place banana peels
		List<Tile> freeTiles = new ArrayList<>();
		List<Tile> allTiles = TileUtil.getAllTiles(room);
		allTiles.stream().filter(tile -> tile.isWalkable()).forEachOrdered(freeTiles::add);
		
		Collections.shuffle(freeTiles, random);
		
		Iterator<Tile> iterator = freeTiles.iterator();
		int i=0;
		while (iterator.hasNext() && i < 10) {
			Tile next = iterator.next();
			room.entityFactory.creepFactory.createBananaPeel(room, next.getGridPos(), enemy);
			iterator.remove();
			i++;
		}

		// Place bananas
		i=0;
		while (iterator.hasNext() && i < 4) {
			Tile next = iterator.next();
			Entity banana = room.entityFactory.itemFactory.createItem(ItemEnum.BANANA, room, next.getGridPos());
			bananas.add(banana);
			iterator.remove();
			i++;
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
		
		// Check if a banana disappeared
		// 1) First find all bananas in the room
		List<Entity> foundBananas = new ArrayList<>();
		List<Tile> allTiles = TileUtil.getAllTiles(room);
		for (Tile tile : allTiles) {
			if (!tile.isWalkable()) continue;
			
			Set<Entity> items = TileUtil.getEntitiesWithComponentOnTile(tile.getGridPos(), ItemComponent.class, room);
			for (Entity item : items) {
				ItemComponent itemCompo = Mappers.itemComponent.get(item);
				if (itemCompo != null && itemCompo.getItemType() instanceof ItemBanana) {
					// Found a banana
					foundBananas.add(item);
				}
			}
		}
		// 2) Compare the bananas found to the bananas found at the previous turn to see if a banana disappeared
		for (Entity banana : this.bananas) {
			if (!foundBananas.contains(banana)) {
				// This banana disappeared
				sleeping = false;
				aiComponent.setAlerted(true, creature, GameScreen.player);
				HealthComponent healthComponent = Mappers.healthComponent.get(creature);
				healthComponent.healthChangeMap.put(HealthChangeEnum.HIT, "@$%&@%$!!!");
			}
		}
		this.bananas.clear();
		this.bananas.addAll(foundBananas);
		

		
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
		
		damageReceived += damage;
		
		if (damageReceived >= 15) {
			damageReceived = 0;
			goingForBanana = true;
		}
	}
	

	
	//*******************
	// Utils
	
	public static void throwBananas(Vector2 orangutanPos, Tile targetedTile, Room room) {
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
	
	
	
	public static boolean hasBanana(Tile t, Room room) {
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

	public boolean isGoingForBanana() {
		return goingForBanana;
	}

	public void setGoingForBanana(boolean goingForBanana) {
		this.goingForBanana = goingForBanana;
	}
	
	public List<Entity> getBananas() {
		return bananas;
	}
}
