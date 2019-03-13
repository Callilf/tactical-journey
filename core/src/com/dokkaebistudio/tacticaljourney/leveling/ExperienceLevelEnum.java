/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.leveling;



/**
 * 
 * @author Callil
 *
 */
public enum ExperienceLevelEnum {

	LEVEL_1(5),
	LEVEL_2(10),
	LEVEL_3(25),
	LEVEL_4(50),
	LEVEL_5(75),
	LEVEL_6(100),
	LEVEL_7(150),
	LEVEL_8(200),
	LEVEL_9(250),
	LEVEL_10(300),
	LEVEL_11(350),
	LEVEL_12(400),
	LEVEL_13(4500),
	LEVEL_14(500),
	LEVEL_15(600),
	LEVEL_16(700),
	LEVEL_17(800),
	LEVEL_18(900),
	LEVEL_19(1000);
	
	/** The amount of xp needed to reach the next level. */
	private int xpToNextLevel;
	
	ExperienceLevelEnum(int xpToNextLevel) {
		this.xpToNextLevel = xpToNextLevel;
	}
	
	
	/**
	 * Return the {@link ExperienceLevelEnum} matching the given level.
	 * @param level the level
	 * @return the matching ExperienceLevelEnum
	 * @throws Exception 
	 */
	public static ExperienceLevelEnum get(int level) {
		if (level <= 0) System.out.println("Calling for tha ExperienceLevelEnum for the level = " + level);
		
		ExperienceLevelEnum[] allLevels = ExperienceLevelEnum.values();
		if (allLevels.length <= level) {
			return allLevels[allLevels.length - 1];
		} else {
			return ExperienceLevelEnum.valueOf("LEVEL_" + level);
		}
	}


	
	// Getters and setters
	
	public int getXpToNextLevel() {
		return xpToNextLevel;
	}


	public void setXpToNextLevel(int xpToNextLevel) {
		this.xpToNextLevel = xpToNextLevel;
	}
	
	
}
