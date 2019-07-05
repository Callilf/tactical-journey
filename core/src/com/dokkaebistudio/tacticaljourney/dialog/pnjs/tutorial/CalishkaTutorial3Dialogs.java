package com.dokkaebistudio.tacticaljourney.dialog.pnjs.tutorial;

import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ashley.PublicEntity;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.TutorialComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.CalishkaComponent;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.EnemyTypeEnum;
import com.dokkaebistudio.tacticaljourney.dialog.AbstractDialogs;
import com.dokkaebistudio.tacticaljourney.dialog.Dialog;
import com.dokkaebistudio.tacticaljourney.dialog.DialogBuilder;
import com.dokkaebistudio.tacticaljourney.dialog.DialogCondition;
import com.dokkaebistudio.tacticaljourney.dialog.DialogEffect;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

public class CalishkaTutorial3Dialogs extends AbstractDialogs {
	
	@Override
	protected void setSpeaker(Dialog d) {
		d.setSpeaker(Descriptions.CALISHKA_TITLE);
	}
	
	@SuppressWarnings("unchecked")
	public CalishkaTutorial3Dialogs() {

		// Basic dialog
		
		this.addDialog(new DialogBuilder()
				.addText("It's time to talk about how combat works in here.")
				.addText("Did you see these blue tiles on the ground? They are here because this room contains at least one enemy, which means that you cannot move freely.")
				.addText("After you play your turn, all the enemies of the rooms will play their turns too!")
				.addText("Try to spend some turns to see what the spider does. To spend turns you can either [ORANGE]click[] on the 'END TURN' button on the button left of the screen, "
						+ "or just [ORANGE]click[] on your current tile.")
				
				.setRepeat(false)
				.build());
		this.addDialog(new DialogBuilder()
				.addText("To spend turns you can either [ORANGE]click[] on the 'END TURN' button on the button left of the screen, "
						+ "or just [ORANGE]click[] on your current tile. Spend a few turn to see how the spider moves.")
				.setRepeat(true)
				.build());
		
		// After spending 3 turns
		
		DialogCondition spent3Turns = new DialogCondition() {
			@Override
			public boolean test(PublicEntity e) {
				TutorialComponent tc = Mappers.tutorialComponent.get(e);
				return tc.isGoal1Reached() && !tc.isGoal2Reached();			
			}
		};
		
		this.addDialog(new DialogBuilder()
				.addText("The only way for you to be able to move freely in a room is to get rid of all the enemies. A room with no enemy is called a [GREEN]cleared room[].")
				.addText("Clearing a room will grant you money, and might activate passive abilities like the [GREEN]blessing[] I granted you.")
				.addText("Let's clear this room!")
				.addText("In order to do so, I'll teach you the basics of combat. To fight an enemy, you just have to go on an adjacent tile, and then [ORANGE]click[] on the enemy!")
				.addText("When you click, the [RED]Attack Wheel[] will appear. The attack wheel is used to compute the damages of your attack based on your strength.")
				.addText("When you [ORANGE]click[] on the screen while the wheel is turning, the arrow will stop. The color designated by the arrow will be used to compute the damages.")
				.addText("[BLACK]Black[] is a miss, which deal no damage")
				.addText("[GRAY]Gray[] is a graze, which deal a bit less damages that your strength")
				.addText("[GREEN]Green[] is a hit, which deal damages equal to your strength, which is currently 5")
				.addText("[RED]Red[] is a critical, which deal damages equal to twice your strength")
				.addText("Now go and attack this spider to understand how this works!")
				.setRepeat(false)
				.setCondition(spent3Turns)
				.setEffect(new DialogEffect() {
					public void play(Room room) {
						VFXUtil.createSmokeEffect(new Vector2(17, 6));
						Optional<Entity> wall = TileUtil.getEntityWithComponentOnTile(new Vector2(17, 6), SolidComponent.class, room);
						if (wall.isPresent()) {
							room.removeEntity(wall.get());
						}
						
						room.setNextState(RoomState.PLAYER_COMPUTE_MOVABLE_TILES);
					}
				})
				.build());
		
		this.addDialog(new DialogBuilder()
				.addText("Try killing the spider by going at close range and [ORANGE]clicking[] on it.")
				.setRepeat(true)
				.setCondition(spent3Turns)
				.build());
		
		
		// After killing the spider
		
		DialogCondition killedSpiderCondition = new DialogCondition() {
			@Override
			public boolean test(PublicEntity e) {
				TutorialComponent tc = Mappers.tutorialComponent.get(e);
				return tc.isGoal2Reached() && !tc.isGoal3Reached();			
			}
		};
		
		
		this.addDialog(new DialogBuilder()
				.addText("Congratulations, you killed your first enemy!")
				.addText("You cleared the room and can now move freely, the blue tiles are gone.")
				.addText("You also gained some gold coins that you can see at the top of the screen! Gold coins can be used for various purpose in the dungeon, but I'll let you discover "
						+ "this by yourself.")
				.addText("Note that you also gained some experience. You can see it at the bottom of the screen. If you earn enough experience, you can reach the next level, which "
						+ "will allow you to improve your stats or earn items.")
				.addText("Let's come back to the combat. You just defeated the spider, but you went attacking it quite blindly. Fortunately for you it was a pretty weark enemy.")
				.addText("You can actually gain a bit of information about the enemy and it's possible moves. The first way to do that is to use your Inspect skill, which can be activated "
						+ "by [ORANGE]clicking[] on the inspect button at the right of the inventory, and then [ORANGE]clicking[] on the thing you want to inspect.")
				.addText("Try to inspect this new enemy!")
				.setRepeat(false)
				.setCondition(killedSpiderCondition)
				.setEffect(new DialogEffect() {
					public void play(Room room) {
						VFXUtil.createSmokeEffect(new Vector2(17, 6));
						room.entityFactory.createWall(room, new Vector2(17, 6));
						
						VFXUtil.createSmokeEffect(new Vector2(8, 6));
						Entity stinger = room.entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.STINGER, room, new Vector2(8, 6));
						stinger.remove(LootRewardComponent.class);
						
						room.setNextState(RoomState.PLAYER_COMPUTE_MOVABLE_TILES);
					}
				})
				.build());
		this.addDialog(new DialogBuilder()
				.addText("To inspect an enemy, [ORANGE]click[] on the inspect button at the right of the inventory, and then [ORANGE]click[] on the enemy you want to inspect")
				.setRepeat(true)
				.setCondition(killedSpiderCondition)
				.build());
		
		// After inspecting stinger
		
		DialogCondition stingerInspectedCondition = new DialogCondition() {
			@Override
			public boolean test(PublicEntity e) {
				TutorialComponent tc = Mappers.tutorialComponent.get(e);
				return tc.isGoal3Reached() && !tc.isGoal4Reached();			
			}
		};
		
		this.addDialog(new DialogBuilder()
				.addText("Good. This inspection skill is really important for newcomers like you. You can inspect anything, not just enemies. You can inspect vegetation, "
						+ "items, objects, walls, and so on. So keep in mind to use this anytime you see something unusual!")
				.addText("The second important way to get information on an enemy is by analyzing its possible movements and attacks. To do so, just hold [ORANGE]right click[] on an "
						+ "enemy. The blue tiles are its possible movements, and the red tiles are where it cannot move but can attack.")
				.addText("Try displaying the possible movements of the enemy!")
				.setRepeat(false)
				.setCondition(stingerInspectedCondition)
				.build());
		
		
		// After displaying movements of stinger
		
		DialogCondition stingerMovesDisplayedCondition = new DialogCondition() {
			@Override
			public boolean test(PublicEntity e) {
				return Mappers.tutorialComponent.get(e).isGoal4Reached();
			}
		};
		
		this.addDialog(new DialogBuilder()
				.addText("Great. Didn't you see something weird?")
				.addText("Yes indeed, this enemy can charge horizontally and vertically! So avoid ending your turn on the same line or you will be badly hurt.")
				.addText("Go on and kill this enemy. I'll be waiting for you in the next room for the last part of this training, which will center on your bow and bombs.")
				.setRepeat(false)
				.setCondition(stingerMovesDisplayedCondition)
				.setEffect(new DialogEffect() {
					public void play(Room room) {
						room.openDoors();
						
						VFXUtil.createSmokeEffect(new Vector2(17, 6));
						Optional<Entity> wall = TileUtil.getEntityWithComponentOnTile(new Vector2(17, 6), SolidComponent.class, room);
						if (wall.isPresent()) {
							room.removeEntity(wall.get());
						}
						
						VFXUtil.createSmokeEffect(new Vector2(20, 7));
						Optional<Entity> calishka = TileUtil.getEntityWithComponentOnTile(new Vector2(20, 7), CalishkaComponent.class, room);
						room.removeEntity(calishka.get());
						
						MoveComponent playerMoveCompo = Mappers.moveComponent.get(GameScreen.player);
						playerMoveCompo.setMoveRemaining(playerMoveCompo.getMoveSpeed());
						room.setNextState(RoomState.PLAYER_COMPUTE_MOVABLE_TILES);
					}
				})
				.build());

	}

}
