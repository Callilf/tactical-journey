package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * This component should be attached to weapons, as it defines the base
 * attack wheel to display (without modifiers from other sources).
 */
public class WheelComponent implements Component {

    List<Sector> sectors = new LinkedList<Sector>();

    public class Sector {
        /** Range must be > 1. */
        public int range;
        public Hit hit;

        public Sector(int range, Hit hit) {
            this.range = range;
            this.hit = hit;
        }

    }

    public enum Hit {
        HIT, MISS, GRAZE, CRITICAL;
    }
}
