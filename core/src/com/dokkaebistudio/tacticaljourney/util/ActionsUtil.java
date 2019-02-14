package com.dokkaebistudio.tacticaljourney.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public final class ActionsUtil {

	public static void move(Actor actor, Vector2 moveLocation, float duration, Action finishAction) {
		actor.addAction(Actions.sequence(Actions.moveTo(moveLocation.x, moveLocation.y, duration), finishAction));
	}

	public static void moveAndRotate(Actor actor, Vector2 moveLocation, float rotation, float duration, Action finishAction) {
		actor.addAction(Actions.sequence(
				Actions.parallel(Actions.moveTo(moveLocation.x, moveLocation.y, duration),Actions.rotateBy(rotation, duration)), 
				finishAction));
	}
}
