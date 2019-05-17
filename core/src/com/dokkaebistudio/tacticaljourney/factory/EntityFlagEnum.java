package com.dokkaebistudio.tacticaljourney.factory;

public enum EntityFlagEnum {

	PLAYER(1),
	SHOPKEEPER(2),
	GODDESS_STATUE(3),
	SOUL_BENDER(4),
	ALTAR(5),
	RECYCLER(6),
	


	ENEMY_SPIDER(30),
	ENEMY_SPIDER_WEB(31),
	ENEMY_SPIDER_VENOM(32),
	ENEMY_SCORPION(33),
	ENEMY_STINGER(34),
	ENEMY_PANGOLIN_BABY(35),
	ENEMY_PANGOLIN_MOTHER(36),
	
	ENEMY_TRIBESMEN_SPEAR(37),
	ENEMY_TRIBESMEN_SHIELD(38),
	ENEMY_TRIBESMEN_SCOUT(39),
	ENEMY_TRIBESMEN_SHAMAN(40),
	ENEMY_TRIBESMEN_TOTEM(41),
	
	ENEMY_SHINOBI(42),
	ENEMY_ORANGUTAN(43),
	
	ALLY_CLONE(70),

	TILE(100),
	WALL(101),
	CHASM(102),
	DOOR(103),
	EXIT(104),
	DESTROYED_SPRITE(105),
	SHOP_CARPET(106),
	HEAVY_WALL(107),
	BUSH(108),
	SECRET_DOOR(109),
	WOODEN_PANEL(110),
	WALL_GATE(111),

	LOOTABLE_BONES(140),
	LOOTABLE_SATCHEL(141),
	LOOTABLE_BELONGINGS(142),
	LOOTABLE_ORB_BAG(143),
	LOOTABLE_SPELLBOOK(144),
	
	CREEP_MUD(150),
	CREEP_WEB(151),
	CREEP_FIRE(152),
	CREEP_POISON(153),
	CREEP_BUSH(154),
	CREEP_VINES_BUSH(155),
	CREEP_BANANA(156),

	DIALOG_POPIN(210),
	
	ENERGY_ORB(400),
	FIRE_ORB(401),
	VEGETAL_ORB(402),
	POISON_ORB(403),
	DEATH_ORB(404),
	VOID(405),
	
	END_TURN_BUTTON(500),
	MOVABLE_TILE(501),
	ATTACK_TILE(502),
	DESTINATION_TILE(503),
	WAYPOINT(504),
	TEXT(505),
	TEXT_ON_TILE(506),
	DAMAGE_DISPLAYER(507),
	EXP_DISPLAYER(508),
	SKILL1_BUTTON(509),
	SKILL2_BUTTON(510),
	TEXT_QUANTITY_DISPLAYER(511),
	TEXT_PRICE_DISPLAYER(512),

	
	OK_BUTTON(513),
	LEVEL_UP_REWARD_BUTTON(514),
	LVL_UP_BACKGROUND(515),
	
	ARROW_NB(550),
	BOMB_NB(551),
	
	BOMB(600),
	EXPLOSION_EFFECT(601),
	WORMHOLE(602),

	DESTRUCTIBLE_VASE(900),
	DESTRUCTIBLE_AMMO_CRATE(901),
	
	
	UNIVERSAL_CURE(999),
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
	ITEM_PEBBLE(1011),
	ITEM_REGEN_POTION(1012),
	ITEM_WING_POTION(1013),
	ITEM_ORB_CONTAINER(1014),
	ITEM_WORMHOLE_SHARD(1015),
	ITEM_DIVINE_CATALYST(1016),
	ITEM_SHURIKEN(1017),
	ITEM_UNIVERSAL_CURE(1018),
	ITEM_SMOKEBOMB(1019),
	ITEM_LEATHER(1020),
	ITEM_CLOVER(1021),
	ITEM_PURITY_POTION(1022),

	ITEM_SCROLL(1099),

	ITEM_INFUSABLE(1100),


	ITEM_ORB(2000);

	
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
