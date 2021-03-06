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

package com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems;

import com.dokkaebistudio.tacticaljourney.ces.systems.NamedSystem;
import com.dokkaebistudio.tacticaljourney.dialog.Dialog;
import com.dokkaebistudio.tacticaljourney.room.Room;

public class DialogSystem extends NamedSystem {	
	
	public DialogSystem(Room r) {
		this.priority = 13;
		this.room = r;
	}


    @Override
	public void performUpdate(float deltaTime) {
    	Dialog currentDialog = room.getDialog();
    	
    	// Dialog creation
    	if (room.getRequestedDialog() != null) {
    		Dialog requestedDialog = room.getRequestedDialog();
    		if (currentDialog == null) {
				room.setDialog(requestedDialog);
    		}
    		room.clearRequestedDialog();
    	}
    	
    }
}
