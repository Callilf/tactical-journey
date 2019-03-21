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
import com.dokkaebistudio.tacticaljourney.components.DialogComponent;
import com.dokkaebistudio.tacticaljourney.dialog.Dialog;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class DialogSystem extends EntitySystem implements RoomSystem {	
	
	private Room room;
	
	public DialogSystem(Room r) {
		this.priority = 13;

		this.room = r;
	}
	
	
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
    

    @Override
	public void update(float deltaTime) {
    	Entity currentDialog = room.getDialog();
    	
    	// Dialog creation
    	if (room.getRequestedDialog() != null) {
    		Dialog requestedDialog = room.getRequestedDialog();
    		if (requestedDialog.isForceDisplay() || currentDialog == null) {
    			
    			if (currentDialog != null) {
    				// Remove the existing dialog
    		    	DialogComponent dialogComponent = Mappers.dialogComponent.get(currentDialog);
        			room.removeDialog();
    			}
    			
				currentDialog = room.entityFactory.createDialogPopin(requestedDialog.getSpeaker(),
						requestedDialog.getText(), requestedDialog.getDuration(), room);
    		}
    		room.clearRequestedDialog();
    	}
    	
    }
}
