package com.dokkaebistudio.tacticaljourney.util.actions;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class ActionMoveCircular extends TemporalAction {
   private Vector2 start;
   private float r1;
   private float r2;
   private float startAngle;
   private boolean angleSet;
   private float direction;
   private boolean toRotate;
   
   public static ActionMoveCircular actionCircle(float x, float y, float r, float direction, boolean rotate, float duration) 
   {
      return ActionMoveCircular.actionEllipse(x,y,r,r,direction,rotate,duration,Interpolation.linear);
   }
   
   public static ActionMoveCircular actionCircle(float x, float y, float r, float direction, boolean rotate, float duration, Interpolation interpolation)
   {
      return ActionMoveCircular.actionEllipse(x,y,r,r,direction,rotate,duration,Interpolation.linear);
   }
   
   public static ActionMoveCircular actionEllipse(float x, float y, float r1, float r2, float direction, boolean rotate, float duration)
   {
      return actionEllipse(x,y,r1,r2,direction,rotate,duration,Interpolation.linear);
   }
   
   // x, y - center point
   // r1, r2 - radiuses
   // direction - 1 clockwise, -1 anticlockwise, other values - for experiments
   // rotate- to rotate actor or not to rotate the actor
   // duration, interpolation - as in other actions   
   public static ActionMoveCircular actionEllipse(float x, float y, float r1, float r2, 
         float direction, boolean rotate, float duration, Interpolation interpolation) 
   {
      ActionMoveCircular action = new ActionMoveCircular();
      action.setR(r1,r2);
      action.setDuration(duration);
      action.setPosition(x,y);
      action.angleSet = false;
      action.setDirection(direction);
      action.setRotate(rotate);
      action.setInterpolation(interpolation);
      return action;
   }
   
   protected void setRotate(boolean rotate)
   {
      toRotate = rotate;
   }
   
   protected void setDirection(float dir)
   {
      direction = -dir;
   }
   
   public void setPosition(float x, float y)
   {
      start = new Vector2(x, y);
   }
   public Vector2 getPosition() {
	   return start;
   }
   
   
   protected void setStartAngle(float angle)
   {
      startAngle = angle;
   }

   protected void begin () 
   {
      
   }

   protected void update (float percent) 
   {      
      if(!angleSet)
      {
         // calculate angle between start point and actor position
         startAngle = (float)Math.toRadians(new Vector2(actor.getX(), actor.getY()).sub(start).angle());
         angleSet = true;
      }
      
      float angle = (float)(Math.PI*2*(direction*percent/1f)) + startAngle;
      actor.setPosition(start.x + r1*(float)Math.cos(angle), start.y + r2*(float)Math.sin(angle));
      if(toRotate)
         actor.setRotation((float)Math.toDegrees(angle)-90);
   }
   
   public void setR (float r1, float r2) 
   {
      this.r1=r1;
      this.r2=r2;
   }
}