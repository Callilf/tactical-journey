package com.dokkaebistudio.tacticaljourney.components.player;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.dokkaebistudio.tacticaljourney.wheel.Sector;
import com.dokkaebistudio.tacticaljourney.wheel.Sector.Hit;

/**
 * This component should be attached to weapons, as it defines the base
 * attack wheel to display (without modifiers from other sources).
 */
public class WheelComponent implements Component {

    public List<Sector> sectors = new LinkedList<Sector>();

    public void addSector(int range, Hit hit) {
        sectors.add(new Sector(range, hit));
    }

}
