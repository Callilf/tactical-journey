package com.dokkaebistudio.tacticaljourney.systems;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.ai.movements.TileSearchUtil;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.SkillComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.skills.SkillSelectionEnum;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class PlayerMoveSystem extends IteratingSystem implements RoomSystem {

	/** The movement handler. */
	private final MovementHandler movementHandler;

	/** The current room. */
	private Room room;

	/** The highlighted enemy. */
	private Entity enemyHighlighted;

	public PlayerMoveSystem(Room room) {
		super(Family.all(PlayerComponent.class, GridPositionComponent.class).get());
		this.room = room;
		this.movementHandler = new MovementHandler(room.engine);
	}

	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}

	@Override
	protected void processEntity(Entity moverEntity, float deltaTime) {
		MoveComponent moveCompo = Mappers.moveComponent.get(moverEntity);
		AttackComponent attackCompo = Mappers.attackComponent.get(moverEntity);
		GridPositionComponent moverCurrentPos = Mappers.gridPositionComponent.get(moverEntity);
		PlayerComponent playerCompo = Mappers.playerComponent.get(moverEntity);

		if (!room.state.isPlayerTurn()) {
			return;
		}

		// Check if the end turn button has been pushed
		handleEndTurnButton(moveCompo, attackCompo, playerCompo);

		switch (room.state) {

		case PLAYER_TURN_INIT:
			if (room.hasEnemies()) {
				moveCompo.moveRemaining = moveCompo.moveSpeed;
				moveCompo.freeMove = false;
			} else {
				moveCompo.moveRemaining = 30;
				moveCompo.freeMove = true;
			}
			room.state = RoomState.PLAYER_COMPUTE_MOVABLE_TILES;

		case PLAYER_COMPUTE_MOVABLE_TILES:
			// clear the movable tile
			moveCompo.clearMovableTiles();
			if (attackCompo != null)
				attackCompo.clearAttackableTiles();

			// Build the movable tiles list
			TileSearchUtil.buildMoveTilesSet(moverEntity, room);
			if (attackCompo != null)
				TileSearchUtil.buildAttackTilesSet(moverEntity, room, true);

			if (!room.hasEnemies()) {
				moveCompo.hideMovableTiles();
			}

			room.state = RoomState.ENEMY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER;
			break;

		case PLAYER_MOVE_TILES_DISPLAYED:
			// When clicking on a moveTile, display it as the destination
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;

				boolean selected = selectDestinationTile(moveCompo, x, y, moverCurrentPos);
				if (selected) {
					room.state = RoomState.PLAYER_MOVE_DESTINATION_SELECTED;
				}
			}

			// When right clicking on an ennemy, display it's possible movement
			handleRightClickOnEnemies(moverEntity);

			handleSkillSelection(moverEntity, room);
			break;

		case PLAYER_MOVE_DESTINATION_SELECTED:
			// Either click on confirm to move or click on another tile to change the
			// destination
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;

				SpriteComponent selectedTileSprite = Mappers.spriteComponent.get(moveCompo.getSelectedTile());
				SpriteComponent playerSprite = Mappers.spriteComponent.get(moverEntity);

				if (selectedTileSprite.containsPoint(x, y)) {
					// Confirm movement is we click on the selected tile again

					// Initiate movement
					movementHandler.initiateMovement(moverEntity);

					room.state = RoomState.PLAYER_MOVING;
				} else if (playerSprite.containsPoint(x, y)) {
					// Cancel movement is we click on the character
					moveCompo.clearSelectedTile();
					room.state = RoomState.PLAYER_MOVE_TILES_DISPLAYED;
				} else {
					// No confirmation, check if another tile has been selected
					selectDestinationTile(moveCompo, x, y, moverCurrentPos);
					room.state = RoomState.PLAYER_MOVE_DESTINATION_SELECTED;
				}

			}
			
			handleSkillSelection(moverEntity, room);

			break;

		case PLAYER_MOVING:
			TransformComponent transfoCompo = Mappers.transfoComponent.get(moverEntity);
			moveCompo.selectCurrentMoveDestinationTile();

			// Do the movement on screen
			Boolean movementFinished = movementHandler.performRealMovement(moverEntity, room);
			if (movementFinished == null)
				return;
			else if (movementFinished)
				room.state = RoomState.PLAYER_END_MOVEMENT;

			break;

		case PLAYER_END_MOVEMENT:
			movementHandler.finishRealMovement(moverEntity);

			// Compute the cost of this move
			if (room.hasEnemies()) {
				int cost = computeCostOfMovement(moveCompo);
				moveCompo.moveRemaining = moveCompo.moveRemaining - cost;
			}

			room.state = RoomState.PLAYER_COMPUTE_MOVABLE_TILES;
			break;

		default:
			break;

		}
	}

	/**
	 * Holding right click on an enemy displays it's possible movements and attacks.
	 */
	private void handleRightClickOnEnemies(Entity player) {
		if (InputSingleton.getInstance().rightClickJustPressed) {
			Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
			int x = (int) touchPoint.x;
			int y = (int) touchPoint.y;

			Vector2 gridPos = TileUtil.convertPixelPosIntoGridPos(new Vector2(x, y));
			Entity attackableEntity = TileUtil.getAttackableEntityOnTile(gridPos, room);
			if (attackableEntity != null) {
				// There is an enemy at this location, display it's tiles
				MoveComponent enemyMoveCompo = Mappers.moveComponent.get(attackableEntity);
				if (enemyMoveCompo != null) {
					enemyHighlighted = attackableEntity;
					enemyMoveCompo.showMovableTiles();
				}
				AttackComponent enemyAttackCompo = Mappers.attackComponent.get(attackableEntity);
				if (enemyAttackCompo != null) {
					enemyHighlighted = attackableEntity;
					enemyAttackCompo.showAttackableTiles();
				}
				// Hide the player's tiles
				MoveComponent playerMoveComponent = Mappers.moveComponent.get(player);
				if (playerMoveComponent != null) {
					playerMoveComponent.hideMovableTiles();
				}
				AttackComponent playerAttackComponent = Mappers.attackComponent.get(player);
				if (playerAttackComponent != null) {
					playerAttackComponent.hideAttackableTiles();
				}
			}
		} else if (InputSingleton.getInstance().rightClickJustReleased && enemyHighlighted != null) {
			// Released right click

			// hide the enemy tiles
			MoveComponent enemyMoveCompo = Mappers.moveComponent.get(enemyHighlighted);
			if (enemyMoveCompo != null) {
				enemyMoveCompo.hideMovableTiles();
			}
			AttackComponent enemyAttackCompo = Mappers.attackComponent.get(enemyHighlighted);
			if (enemyAttackCompo != null) {
				enemyAttackCompo.hideAttackableTiles();
			}
			enemyHighlighted = null;

			// Display the player's ones
			// Hide the player's tiles
			MoveComponent playerMoveComponent = Mappers.moveComponent.get(player);
			if (playerMoveComponent != null) {
				playerMoveComponent.showMovableTiles();
			}
			AttackComponent playerAttackComponent = Mappers.attackComponent.get(player);
			if (playerAttackComponent != null) {
				playerAttackComponent.showAttackableTiles();
			}
		}
	}

	/**
	 * Handle the selection of a skill.
	 * 
	 * @param rs        the current room state
	 * @param moveCompo the move component
	 */
	public static void handleSkillSelection(Entity player, Room room) {
		SkillSelectionEnum skillSelected = null;
		PlayerComponent playerCompo = Mappers.playerComponent.get(player);
		MoveComponent moveCompo = Mappers.moveComponent.get(player);

		// Check the click on a skill button
		if (InputSingleton.getInstance().leftClickJustPressed) {
			Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
			int x = (int) touchPoint.x;
			int y = (int) touchPoint.y;

			// If click on a skill button, make it look pushed but don't to any thing else.
			// The real action is on click release.
			SpriteComponent skill1SpriteComponent = Mappers.spriteComponent.get(playerCompo.getSkill1Button());
			if (skill1SpriteComponent.containsPoint(x, y)) {
				pushSkillButton(playerCompo.getSkill1(), skill1SpriteComponent);
			}

			SpriteComponent skill2SpriteComponent = Mappers.spriteComponent.get(playerCompo.getSkill2Button());
			if (skill2SpriteComponent.containsPoint(x, y)) {
				pushSkillButton(playerCompo.getSkill2(), skill2SpriteComponent);
			}
		}
		if (InputSingleton.getInstance().leftClickJustReleased) {
			Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
			int x = (int) touchPoint.x;
			int y = (int) touchPoint.y;

			// If release on the button, restore the original texture and end the turn.
			SpriteComponent skill1SpriteComponent = Mappers.spriteComponent.get(playerCompo.getSkill1Button());
			SkillSelectionEnum sse = releaseSkillButton(playerCompo.getSkill1(), playerCompo.getSkill1Button(),
					playerCompo, skill1SpriteComponent.containsPoint(x, y));
			if (sse != null)
				skillSelected = sse;

			SpriteComponent skill2SpriteComponent = Mappers.spriteComponent.get(playerCompo.getSkill2Button());
			sse = releaseSkillButton(playerCompo.getSkill2(), playerCompo.getSkill2Button(), playerCompo,
					skill2SpriteComponent.containsPoint(x, y));
			if (sse != null)
				skillSelected = sse;

		}

		if (InputSingleton.getInstance().skill1JustPressed) {
			// If skill hotkey pressed, make the skill button look pushed.
			SpriteComponent skill1SpriteComponent = Mappers.spriteComponent.get(playerCompo.getSkill1Button());
			pushSkillButton(playerCompo.getSkill1(), skill1SpriteComponent);
		}
		if (InputSingleton.getInstance().skill1JustReleased) {
			// If skill hotkey released, restore the button texture and activate/deactivate
			// the skill.
			skillSelected = releaseSkillButton(playerCompo.getSkill1(), playerCompo.getSkill1Button(), playerCompo,
					true);
		}

		if (InputSingleton.getInstance().skill2JustPressed) {
			// If skill 2 hotkey pressed, make the skill button look pushed.
			SpriteComponent skill2SpriteComponent = Mappers.spriteComponent.get(playerCompo.getSkill2Button());
			pushSkillButton(playerCompo.getSkill2(), skill2SpriteComponent);
		}
		if (InputSingleton.getInstance().skill2JustReleased) {
			// If skill 2 hotkey released, restore the button texture and
			// acticate/deactivate the skill.
			skillSelected = releaseSkillButton(playerCompo.getSkill2(), playerCompo.getSkill2Button(), playerCompo,
					true);
		}

		if (skillSelected != null) {
			switch (skillSelected) {
			case SKILL_SELECTED:
			case NEW_SKILL_SELECTED:
				room.state = RoomState.PLAYER_TARGETING_START;
				break;

			case NO_SKILL_SELECTED:
				room.state = RoomState.PLAYER_TARGETING_STOP;
				break;
			}
		}
	}

	/**
	 * Show the button as pushed.
	 * 
	 * @param skillEntity the clicked skill entity (not the button skill entity !!)
	 * @param skillButtonSpriteComponent the skill sprite component
	 */
	private static void pushSkillButton(Entity skillEntity, SpriteComponent skillButtonSpriteComponent) {
		SkillComponent skillComponent = Mappers.skillComponent.get(skillEntity);
		skillButtonSpriteComponent
				.setSprite(new Sprite(Assets.getTexture(skillComponent.getType().getBtnPushedTexture())));
	}

	/**
	 * Release a skill button.
	 * 
	 * @param skillEntity       the skill entity (not the button entity)
	 * @param skillButtonEntity the skill button entity
	 * @param playerCompo       the player compo
	 * @param setActive         whether this button is being activated or not
	 * @return true if the button is being activated, false if not
	 */
	private static SkillSelectionEnum releaseSkillButton(Entity skillEntity, Entity skillButtonEntity,
			PlayerComponent playerCompo, boolean setActive) {
		SpriteComponent skill1SpriteComponent = Mappers.spriteComponent.get(skillButtonEntity);
		SkillComponent skill1Component = Mappers.skillComponent.get(skillEntity);
		skill1SpriteComponent.setSprite(new Sprite(Assets.getTexture(skill1Component.getType().getBtnTexture())));
		if (setActive) {
			if (playerCompo.getActiveSkill() == skillEntity) {
				// Unselect the current skill
				return SkillSelectionEnum.NO_SKILL_SELECTED;
			} else {
				playerCompo.setActiveSkill(skillEntity);
				if (playerCompo.getActiveSkill() == null) {
					return SkillSelectionEnum.SKILL_SELECTED;
				} else {
					return SkillSelectionEnum.NEW_SKILL_SELECTED;
				}
			}
		}
		return null;
	}

	/**
	 * Handle the end turn button.
	 * 
	 * @param moveCompo   the move component
	 * @param attackCompo the attack component
	 * @param playerCompo the player component
	 */
	private void handleEndTurnButton(MoveComponent moveCompo, AttackComponent attackCompo,
			PlayerComponent playerCompo) {
		if (room.state.canEndTurn()) {
			if (InputSingleton.getInstance().leftClickJustPressed) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;

				// If click on the endTurnButton, make it look pushed but don't to any thing
				// else.
				// The real action is on click release.
				SpriteComponent spriteComponent = Mappers.spriteComponent.get(playerCompo.getEndTurnButton());
				if (spriteComponent.containsPoint(x, y)) {
					spriteComponent.setSprite(new Sprite(Assets.getTexture(Assets.btn_end_turn_pushed)));
				}
			}
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;

				// If release on the endTurnButton, restore the original texture and end the
				// turn.
				SpriteComponent spriteComponent = Mappers.spriteComponent.get(playerCompo.getEndTurnButton());
				spriteComponent.setSprite(new Sprite(Assets.getTexture(Assets.btn_end_turn)));
				if (spriteComponent.containsPoint(x, y)) {
					moveCompo.clearMovableTiles();
					attackCompo.clearAttackableTiles();
					room.turnManager.endPlayerTurn();
				}
			}
			if (InputSingleton.getInstance().spaceJustPressed) {
				// If space pressed, make the endTurnButton look pushed.
				SpriteComponent spriteComponent = Mappers.spriteComponent.get(playerCompo.getEndTurnButton());
				spriteComponent.setSprite(new Sprite(Assets.getTexture(Assets.btn_end_turn_pushed)));
			}
			if (InputSingleton.getInstance().spaceJustReleased) {
				// If space released, restore the button texture and end the turn.
				SpriteComponent spriteComponent = Mappers.spriteComponent.get(playerCompo.getEndTurnButton());
				spriteComponent.setSprite(new Sprite(Assets.getTexture(Assets.btn_end_turn)));
				moveCompo.clearMovableTiles();
				attackCompo.clearAttackableTiles();
				room.turnManager.endPlayerTurn();
			}
		}
	}

	/**
	 * Return the cost of movement
	 * 
	 * @param moveCompo the moveComponent
	 * @return the cost of movement
	 */
	private int computeCostOfMovement(MoveComponent moveCompo) {
		int cost = 0;
		for (Entity wp : moveCompo.getWayPoints()) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(wp);
			cost = cost + getCostOfTileAtPos(gridPositionComponent.coord);
		}
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(moveCompo.getSelectedTile());
		cost = cost + getCostOfTileAtPos(gridPositionComponent.coord);
		return cost;
	}

	/**
	 * Return the cost of move to a given tile.
	 * 
	 * @param pos the position of the tile.
	 * @return the cost
	 */
	private int getCostOfTileAtPos(Vector2 pos) {
		Entity tileEntity = room.getTileAtGridPosition(pos);
		TileComponent tileComponent = Mappers.tileComponent.get(tileEntity);
		return tileComponent.type.getMoveConsumed();
	}

	/**
	 * Set the destination of the movement.
	 * 
	 * @param moveCompo       the moveComponent
	 * @param x               the abscissa of the destination
	 * @param y               the ordinate of the destination
	 * @param moverCurrentPos the current position of the mover
	 */
	private boolean selectDestinationTile(MoveComponent moveCompo, int x, int y,
			GridPositionComponent moverCurrentPos) {
		for (Entity tile : moveCompo.movableTiles) {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(tile);
			GridPositionComponent destinationPos = Mappers.gridPositionComponent.get(tile);

			if (destinationPos.coord.equals(moverCurrentPos.coord)) {
				// Cannot move to the tile we already are
				continue;
			}

			if (spriteComponent.containsPoint(x, y)) {
				// Clicked on this tile !!
				// Create an entity to show that this tile is selected as the destination
				Entity destinationTileEntity = room.entityFactory.createDestinationTile(destinationPos.coord);
				moveCompo.setSelectedTile(destinationTileEntity);

				// Display the way to go to this point
				List<Entity> waypoints = TileSearchUtil.buildWaypointList(moveCompo, moverCurrentPos, destinationPos,
						room);
				moveCompo.setWayPoints(waypoints);

				return true;
			}

		}
		return false;
	}

}
