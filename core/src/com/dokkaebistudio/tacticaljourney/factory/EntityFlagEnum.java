package com.dokkaebistudio.tacticaljourney.factory;

public enum EntityFlagEnum {

	PLAYER(0),
	SHOPKEEPER(1),

	ENEMY_SPIDER(2),
	ENEMY_SPIDER_WEB(3),
	ENEMY_SPIDER_VENOM(4),
	ENEMY_SCORPION(5),
	
	TILE(100),
	WALL(101),
	MUD(102),
	DOOR(103),
	EXIT(104),
	WALL_DESTROYED(105),
	SHOP_ITEM_BACKGROUND(106),

	
	END_TURN_BUTTON(500),
	MOVABLE_TILE(501),
	ATTACK_TILE(502),
	DESTINATION_TILE(502),
	WAYPOINT(503),
	TEXT(504),
	TEXT_ON_TILE(505),
	DAMAGE_DISPLAYER(506),
	EXP_DISPLAYER(507),
	SKILL1_BUTTON(508),
	SKILL2_BUTTON(509),
	DIALOG_POPIN(210),

	
	OK_BUTTON(510),
	LEVEL_UP_REWARD_BUTTON(511),
	LVL_UP_BACKGROUND(512),
	
	ARROW_NB(550),
	BOMB_NB(551),
	
	BOMB(600),
	EXPLOSION_EFFECT(601),

	
	REMAINS_BONES(990),
	REMAINS_SATCHEL(991),
	
	ITEM_TUTORIAL_PAGE(1000),
	ITEM_HEALTH_UP(1001),
	ITEM_MONEY(1002),
	ITEM_ARROWS(1003),
	ITEM_BOMBS(1004),
	ITEM_ARMOR_UP(1005),
	ITEM_ARMOR_PIECE(1006);


	
	private int flag;
	
	private EntityFlagEnum(int flag) {
		this.flag = flag;
	}
	
	

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
}
