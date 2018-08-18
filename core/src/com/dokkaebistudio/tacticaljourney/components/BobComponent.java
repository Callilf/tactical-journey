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

package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;

public class BobComponent implements Component {
	public static final int STATE_JUMP = 0;
	public static final int STATE_FALL = 1;
	public static final int STATE_HIT = 2;
	public static final float JUMP_VELOCITY = 11;
	public static final float MOVE_VELOCITY = 20;
	public static final float WIDTH = 0.8f;
	public static final float HEIGHT = 0.8f;
	
	public float heightSoFar = 0.0f;
}
