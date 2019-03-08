package com.dokkaebistudio.tacticaljourney.wheel;

public class Sector {
	

	public enum Hit {
	    HIT, MISS, GRAZE, CRITICAL, POISON;
	}


    /** Range must be > 1. */
    public int range;
    public Hit hit;

    public Sector(int range, Hit hit) {
        this.range = range;
        this.hit = hit;
    }

}
