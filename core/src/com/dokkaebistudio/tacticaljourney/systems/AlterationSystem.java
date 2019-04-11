/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.dokkaebistudio.tacticaljourney.systems;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.alterations.Alteration;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.components.BlockVisibilityComponent;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.StatueComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent.AlterationActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent.PlayerActionEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class AlterationSystem extends EntitySystem implements RoomSystem {	
	
	private Stage fxStage;
	private Room room;
	private Entity player;
	private AlterationReceiverComponent playerAlterationReceiverCompo;
	
	private List<Entity> statues = new ArrayList<>();
	
	public static List<Alteration> alterationProcImages = new ArrayList<>();
	public Table alterationProcTable;

	public AlterationSystem(Entity player, Room r, Stage stage) {
		this.priority = 12;

		this.fxStage = stage;
		this.player = player;
		this.room = r;
		
		this.alterationProcTable = new Table();
//		this.alterationProcTable.setDebug(true);
		this.alterationProcTable.left().top();
		this.alterationProcTable.setPosition(10, 1000);
		this.fxStage.addActor(this.alterationProcTable);
	}
	
	
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
    
	@Override
	public void update(float deltaTime) {
		
		if (playerAlterationReceiverCompo == null) {
			playerAlterationReceiverCompo = Mappers.alterationReceiverComponent.get(player);
		}
		
		if (room.getState().canEndTurn()) {
			
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;
				PoolableVector2 tempPos = TileUtil.convertPixelPosIntoGridPos(x, y);
				Entity statue = TileUtil.getEntityWithComponentOnTile(tempPos, StatueComponent.class, room);
				
				if (statue != null) {
					StatueComponent statueComponent = Mappers.statueComponent.get(statue);
					DestructibleComponent destructibleComponent = Mappers.destructibleComponent.get(statue);
					GridPositionComponent playerPosition = Mappers.gridPositionComponent.get(player);

					if (!destructibleComponent.isDestroyed()) {
						int distanceFromStatue = TileUtil.getDistanceBetweenTiles(playerPosition.coord(), tempPos);
						if (distanceFromStatue == 1) {
							//Close from statue, display popin
							if (statueComponent.isHasBlessing()) {
								Mappers.playerComponent.get(player).requestAction(PlayerActionEnum.PRAY, statue);
							} else {
								room.setRequestedDialog("You", "The statue looks cold.");
//								room.entityFactory.createDialogPopin("The statue looks cold.", playerPosition.getWorldPos(), 2f, room);
							}
						} else {
							if (statueComponent.isHasBlessing()) {
								room.setRequestedDialog("You","A benevolent aura emanates from this statue.");
							} else {
								room.setRequestedDialog("You","The statue looks cold.");
							}
						}
						
					} else {
						room.setRequestedDialog("You","That was probably a bad idea.");
					}
					
				}
				
				tempPos.free();
				
			}
			
		} 
		
		
		fillStatues();
		for (Entity statue : statues) {
			StatueComponent statueComponent = Mappers.statueComponent.get(statue);
			if (statueComponent.wasJustDestroyed()) {
				statueComponent.setJustDestroyed(false);

				// Deliver the curse
				if (statueComponent.getCurseToGive() != null) {
					playerAlterationReceiverCompo.requestAction(AlterationActionEnum.RECEIVE_CURSE, statueComponent.getCurseToGive());
					statueComponent.setCurseToGive(null);
				}
				
				DestructibleComponent destructibleComponent = Mappers.destructibleComponent.get(statue);
				if (!destructibleComponent.isRemove()) {
					statue.remove(BlockVisibilityComponent.class);
					destructibleComponent.setRemove(true);
					destructibleComponent.setDestroyedTexture(null);
				}
			}
		}
		
		
		
		if (!playerAlterationReceiverCompo.getCurrentActions().isEmpty()) {
			int addCount = 0;
			int removeCount = 0;
			
			for (int i = 0 ; i < playerAlterationReceiverCompo.getCurrentActions().size() ; i++) {
				AlterationActionEnum currentAction = playerAlterationReceiverCompo.getCurrentActions().get(i);
				Alteration currentAlteration = playerAlterationReceiverCompo.getCurrentAlterations().get(i);
				switch (currentAction) {
				case RECEIVE_BLESSING:
					Blessing blessing = (Blessing) currentAlteration;
					playerAlterationReceiverCompo.addBlessing(player, blessing, fxStage, addCount);
					addCount ++;
					
					Journal.addEntry("[GREEN]Received blessing: " + blessing.title());
					break;
				case REMOVE_BLESSING:
					blessing = (Blessing) currentAlteration;
					playerAlterationReceiverCompo.removeBlessing(player, blessing, fxStage, removeCount);
					removeCount ++;
					
					Journal.addEntry("[SCARLET]Lost blessing: " + blessing.title());
					break;
				case RECEIVE_CURSE:
					Curse curse = (Curse) currentAlteration;
					playerAlterationReceiverCompo.addCurse(player, curse, fxStage, addCount);
					addCount ++;

					Journal.addEntry("[PURPLE]Received curse: " + curse.title());
					break;
				case REMOVE_CURSE:
					curse = (Curse) currentAlteration;
					playerAlterationReceiverCompo.removeCurse(player, curse, fxStage, removeCount);
					removeCount ++;
					
					Journal.addEntry("[GREEN]Lifted curse: " + curse.title());
					break;
				
				}
			}
			
			playerAlterationReceiverCompo.clearCurrentAction();
		}

		
		// Display alteration images on proc
		for (int i=0 ; i < alterationProcImages.size() ; i++) {
			Alteration alteration = alterationProcImages.get(i);
			setActivateAnimation(alteration, fxStage);
		}
		alterationProcImages.clear();
		
	}



	/**
	 * Fills the list of statues of the room. Empty list if no statues.
	 * @param room the room
	 */
	private void fillStatues() {
		statues.clear();
		for (Entity e : room.getNeutrals()) {
			if (Mappers.statueComponent.has(e)) {
				statues.add(e);
			}
		}
	}
	
	
	//**********************************
	// Alteration proc animations
	
	
	public static void addAlterationProc(Alteration alteration) {
		alterationProcImages.add(alteration);
	}

	
	private void setActivateAnimation(Alteration alteration, Stage fxStage) {
		int index = alterationProcImages.indexOf(alteration);
		final Table t = new Table();
//		t.setDebug(true);
		
		Image alterationImg = new Image(alteration.texture().getRegion());
		t.add(alterationImg).fill().expand().width(80).height(80).padRight(10);

		Label alterationName = new Label(alteration.title(), PopinService.smallTextStyle());
		alterationName.setWrap(true);
		t.add(alterationName).width(300);
		
		t.pack();
		alterationProcTable.add(t);
		alterationProcTable.row();
		
		Action removeImageAction = new Action(){
			  @Override
			  public boolean act(float delta){
				  t.remove();
				  return true;
			  }
		};
	
		alterationImg.setOrigin(Align.center);
		ScaleToAction init = Actions.scaleTo(0, 0);
		DelayAction delayInit = Actions.delay(index * 0.5f);
		ScaleToAction appear = Actions.scaleTo(1, 1, 1f, Interpolation.elasticOut);
		DelayAction delay = Actions.delay(3f);
		AlphaAction disappearAlpha = Actions.alpha(0f, 1f);
		alterationImg.addAction(Actions.sequence(init, delayInit, appear, delay, disappearAlpha, removeImageAction));
		
		
		AlphaAction lblinit = Actions.alpha(0);
		DelayAction lbldelayInit = Actions.delay(index * 0.5f);
		AlphaAction lblappear = Actions.alpha(1, 1);
		DelayAction lbldelay = Actions.delay(3f);
		AlphaAction lbldisappearAlpha = Actions.alpha(0f, 1f);
		alterationName.addAction(Actions.sequence(lblinit, lbldelayInit, lblappear, lbldelay, lbldisappearAlpha));
	}
	
}
