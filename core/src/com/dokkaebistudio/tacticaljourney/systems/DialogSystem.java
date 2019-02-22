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

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.DialogComponent;
import com.dokkaebistudio.tacticaljourney.dialog.Dialog;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableLabel;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableTable;
import com.dokkaebistudio.tacticaljourney.rendering.poolables.PoolableTextureRegionDrawable;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class DialogSystem extends EntitySystem implements RoomSystem {	
	
	private Room room;
	private Stage stage;
	
	private Entity currentDialog;
	
	public DialogSystem(Room r, Stage s) {
		this.priority = 13;

		this.room = r;
		this.stage = s;
	}
	
	
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
    

    @Override
	public void update(float deltaTime) {
    	currentDialog = room.getDialog();
    	
    	// Dialog creation
    	if (room.getRequestedDialog() != null) {
    		Dialog requestedDialog = room.getRequestedDialog();
    		if (requestedDialog.isForceDisplay() || currentDialog == null) {
    			
    			if (currentDialog != null) {
    				// Remove the existing dialog
    		    	DialogComponent dialogComponent = Mappers.dialogComponent.get(currentDialog);
        			dialogComponent.getTable().remove();
        			room.removeDialog();
    			}
    			
				currentDialog = room.entityFactory.createDialogPopin(requestedDialog.getText(),
						requestedDialog.getPos(), requestedDialog.getDuration(), room);
    		}
    		room.clearRequestedDialog();
    	}
    	
    	if (currentDialog == null) return;
    	
    	
    	
    	// Dialog duration management
    	DialogComponent dialogComponent = Mappers.dialogComponent.get(currentDialog);
    	if (dialogComponent.getCurrentDuration() == 0) {
    		
    		Table t = PoolableTable.create();
    		PoolableTextureRegionDrawable background = PoolableTextureRegionDrawable.create(Assets.dialog_background);
    		t.setBackground(background);
    		
    		Label label = PoolableLabel.create(dialogComponent.getText(), PopinService.hudStyle());
    		label.setAlignment(Align.center);
    		Cell<Label> labelCell = t.add(label);
    		
    		t.pack();
    		
    		if (label.getPrefWidth() > 400) {
        		label.setWrap(true);
    			labelCell.width(600).pad(30, 10, 30, 10);
    		} else {
    			labelCell.width(300).pad(30, 10, 30, 10);
    		}
    		t.pack();
    		
    		t.setPosition(dialogComponent.getPos().x - t.getWidth()/2 + GameScreen.GRID_SIZE/2, dialogComponent.getPos().y + GameScreen.GRID_SIZE);
    		
    		stage.addActor(t);
    		dialogComponent.setTable(t);
    		
    	} else {
    		
    		if (dialogComponent.getCurrentDuration() >= dialogComponent.getDuration()) {
    			dialogComponent.getTable().remove();
    			room.removeDialog();
    			return;
    		}
    		
    	}
		dialogComponent.setCurrentDuration(dialogComponent.getCurrentDuration() + deltaTime);

    }
}
