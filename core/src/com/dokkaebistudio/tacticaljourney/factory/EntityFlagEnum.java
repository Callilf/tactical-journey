package com.dokkaebistudio.tacticaljourney.factory;

public enum EntityFlagEnum {

	PLAYER(0),
	SHOPKEEPER(1),
	GODESS_STATUE(2),


	ENEMY_SPIDER(30),
	ENEMY_SPIDER_WEB(31),
	ENEMY_SPIDER_VENOM(32),
	ENEMY_SCORPION(33),
	ENEMY_STINGER(34),
	ENEMY_PANGOLIN_BABY(35),
	ENEMY_PANGOLIN_MOTHER(36),

	
	TILE(100),
	WALL(101),
	CHASM(102),
	DOOR(103),
	EXIT(104),
	DESTROYED_SPRITE(105),
	SHOP_ITEM_BACKGROUND(106),
	
	CREEP_MUD(150),
	CREEP_WEB(151),
	CREEP_FIRE(152),
	CREEP_POISON(153),

	
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

	DESTRUCTIBLE_VASE(900),
	
	REMAINS_BONES(990),
	REMAINS_SATCHEL(991),
	
	ITEM_TUTORIAL_PAGE(1000),
	ITEM_HEALTH_UP(1001),
	ITEM_MONEY(1002),
	ITEM_KEY(1003),
	ITEM_ARROWS(1004),
	ITEM_BOMBS(1005),
	ITEM_ARMOR_UP(1006),
	ITEM_ARMOR_PIECE(1007),
	ITEM_FIRE_POTION(1008),
	ITEM_WEB_SACK(1009),
	ITEM_VENOM_GLAND(1010),
	ITEM_REGEN_POTION(1011),
	ITEM_WING_POTION(1012),
	
	
	
	ITEM_TOTEM_OF_KALAMAZOO(1101),
	ITEM_FATA_MORGANA(1102),
	ITEM_MITHRIDATIUM(1103),
	ITEM_NURSE_EYE_PATCH(1104);


	
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
