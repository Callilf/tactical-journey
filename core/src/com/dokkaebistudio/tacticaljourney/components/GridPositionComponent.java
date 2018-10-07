package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class GridPositionComponent implements Component {
	
    public final Vector2 coord = new Vector2(0,0);
    
    public int zIndex = 0;
}
