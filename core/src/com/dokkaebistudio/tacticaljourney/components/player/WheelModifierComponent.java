package com.dokkaebistudio.tacticaljourney.components.player;

import com.badlogic.ashley.core.Component;

/**
 * This component doesn't define a wheel, but it modifies an existing wheel.
 * It can change some sectors, change their range, etc.
 */
public class WheelModifierComponent implements Component {
    public float criticalSectorMultiplier = 1f;
    public float missSectorMultiplier = 1f;
    public float hitSectorMultiplier = 1f;
    public float grazeSectorMultiplier = 1f;

    public boolean removeCriticalSectors = false;
    // TODO add some more boolean or multipliers

}
