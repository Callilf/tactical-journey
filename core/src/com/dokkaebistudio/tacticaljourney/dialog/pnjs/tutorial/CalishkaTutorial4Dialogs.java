package com.dokkaebistudio.tacticaljourney.dialog.pnjs.tutorial;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ces.components.TutorialComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.ces.entity.PublicEntity;
import com.dokkaebistudio.tacticaljourney.dialog.AbstractDialogs;
import com.dokkaebistudio.tacticaljourney.dialog.DialogBuilder;
import com.dokkaebistudio.tacticaljourney.dialog.DialogCondition;
import com.dokkaebistudio.tacticaljourney.dialog.DialogEffect;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

public class CalishkaTutorial4Dialogs extends AbstractDialogs {

	
	public CalishkaTutorial4Dialogs() {

		// Basic dialog
		
		this.addDialog(new DialogBuilder()
				.addText("Now that you've learnt the basics of close range combat, let's discuss the other ways you have to defeat enemies.")
				.addText("Beside your melee skill, you also have 3 other skills that can be useful depending on the situations.")
				.addText("The first one is the [YELLOW]Bow[] skill, which allows you to fire arrows at enemies from a distance. It won't deal as much damage as the melee attack "
						+ "but might prove useful in luring enemies and dealing damages without receiving any attacks.")				
				.addText("To attack with the bow, [ORANGE]click[] on the bow skill at the bottom of the screen and then select the tile where you want to shot.")
				.addText("Of course you need arrows first, so here, take these arrows and try killing that spider with your bow!")
				.setRepeat(false)
				.setEffect(new DialogEffect() {
					public void play(Entity speaker, Room room) {
						VFXUtil.createSmokeEffect(new Vector2(11, 6));
						Entity arrows = room.entityFactory.itemFactory.createItemArrows(room, new Vector2(11, 6));
						Mappers.itemComponent.get(arrows).setQuantity(10);
						
						VFXUtil.createSmokeEffect(new Vector2(11, 10));
						Entity spider = room.entityFactory.enemyFactory.spiderFactory.createSpider(room, new Vector2(11, 10));
						spider.remove(LootRewardComponent.class);
						Mappers.healthComponent.get(spider).setHp(5);
						Mappers.aiComponent.get(spider).setTurnOver(true);
						
						room.turnManager.endPlayerTurn();
					}
				})
				.build());
		this.addDialog(new DialogBuilder()
				.addText("To attack with the bow, [ORANGE]click[] on the bow skill at the bottom of the screen and then select the tile where you want to shot. Try killing the spider!")
				.setRepeat(true)
				.build());
		
		
		// After killing the spider
		
		DialogCondition killedSpiderCondition = new DialogCondition() {
			@Override
			public boolean test(PublicEntity e) {
				TutorialComponent tc = Mappers.tutorialComponent.get(e);
				return tc.isGoal1Reached() && !tc.isGoal2Reached();			
			}
		};
		
		
		this.addDialog(new DialogBuilder()
				.addText("Well done! As you could see, the attack wheel of the bow is different than the one of the melee attack. Actually, each weapon has its own "
						+ "wheel, so you will have to adapt to them.")
				.addText("Also as you noticed, walking upon the arrows did not picked up the arrows. Instead, it displayed a popup asking whether you want to grab them or not.")
				.addText("This is because there was an enemy in the room, so picking up an item would end you turn and start the enemy turn. So basically, in a cleared room "
						+ "you can pickup items just by walking upon them, but in rooms with enemies, this popup will appear.")
				.addText("Okay now let's talk about [YELLOW]bombs[]. Bombs are very powerful, but kind of hard to handle.")
				.addText("You can throw bombs the same way you shot arrows, by [ORANGE]clicking[] on the bomb skill and then [ORANGE]clicking[] on the tile where you want to throw the bomb.")
				.addText("However, the bomb will not detonate right away, instead it will remain on the tile and [YELLOW]explode after 2 turns[]")
				.addText("The explosion deals 20 damages and can destroy many things like light walls, objects or bushes.")
				.addText("Here are some bombs, use them to reach and kill this spider!")
				.setRepeat(false)
				.setCondition(killedSpiderCondition)
				.setActivateMarker(true)
				.setEffect(new DialogEffect() {
					public void play(Entity speaker, Room room) {
						VFXUtil.createSmokeEffect(new Vector2(11, 6));
						Entity bombs = room.entityFactory.itemFactory.createItemBombs(room, new Vector2(11, 6));
						Mappers.itemComponent.get(bombs).setQuantity(5);
						
						VFXUtil.createSmokeEffect(new Vector2(11, 2));
						Entity spider = room.entityFactory.enemyFactory.spiderFactory.createSpider(room, new Vector2(11, 2));
						spider.remove(LootRewardComponent.class);
						Mappers.healthComponent.get(spider).setHp(5);
						Mappers.aiComponent.get(spider).setTurnOver(true);
						
						room.turnManager.endPlayerTurn();
					}
				})
				.build());
		this.addDialog(new DialogBuilder()
				.addText("[ORANGE]Click[] on the bomb skill and then [ORANGE]clicking[] on the tile where you want to throw the bomb. Use a bomb to break the walls and kill the spider.")
				.setRepeat(true)
				.setCondition(killedSpiderCondition)
				.build());
		
		// After killing the second spider
		
		DialogCondition secondSpiderKilledCondition = new DialogCondition() {
			@Override
			public boolean test(PublicEntity e) {
				TutorialComponent tc = Mappers.tutorialComponent.get(e);
				return tc.isGoal2Reached() && !tc.isGoal3Reached();			
			}
		};
		
		this.addDialog(new DialogBuilder()
				.addText("Well done, you're getting good at killing spiders!")
				.addText("Arrows and bombs are a great way to defeat powerful foes without losing too much health, but be careful for you cannot carry a lot of them.")
				.addText("Basically you can only carry a maximum of 10 arrows and 5 bombs. But fortunately, the dungeon contains some ways to increase these quantities.")
				.addText("Finally, the last skill that will help you fight monsters is the [YELLOW]throw[]. You already learnt how to throw items previously, and you should practice "
						+ "it because some items are very powerful when thrown.")
				.addText("You now have enough knowledge about combat to stand a chance in the dungeon.")
				.addText("So about the dungeon itself, it is made of multiple floors. The [YELLOW]Universal Cure[] is in the deepest floor, so you will have to go through all floors to "
						+ "get it.")
				.addText("There are two kinds of floors, basic floors and boss floors. In basic floors, to go to the next floor you will have to find the [YELLOW]stairway[] "
						+ "and the associated [YELLOW]key[].")
				.addText("Remember this: [YELLOW]you don't have to clear the entire floor to go to the next![] As long as you have the key, you can go to the next floor.")
				.addText("Boss floors, however, are very small and require you to beat the boss to proceed to the next floor.")
				.addText("You now have all the information you need to get started. Grab the key, open the stairway and enter the real dungeon!")
				.setRepeat(false)
				.setCondition(secondSpiderKilledCondition)
				.setActivateMarker(true)
				.setEffect(new DialogEffect() {
					public void play(Entity speaker, Room room) {
						VFXUtil.createSmokeEffect(new Vector2(11, 6));
						room.entityFactory.itemFactory.createItemKey(room, new Vector2(11, 6));
					}
				})
				.build());
		
		this.addDialog(new DialogBuilder()
				.addText("Good luck! Literally.")
				.setRepeat(false)
				.setCondition(secondSpiderKilledCondition)
				.build());

	}

}
