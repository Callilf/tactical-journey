package com.dokkaebistudio.tacticaljourney.rendering.service;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.dokkaebistudio.tacticaljourney.Assets;

public class PopinService {

	private static PopinService instance;
	
	
    //*****************************
    // STYLES
    
	private LabelStyle hudStyle;
	private LabelStyle smallTextStyle;
    private TextButtonStyle bigButtonStyle;
    private TextButtonStyle smallButtonStyle;
    private TextButtonStyle smallButtonCheckedStyle;
	
    
    private PopinService() {
		hudStyle = new LabelStyle(Assets.font, Color.WHITE);
		smallTextStyle = new LabelStyle(Assets.smallFont, Color.WHITE);

		Drawable btnUp = new SpriteDrawable(new Sprite(Assets.popin_big_btn_up));
		Drawable btnDown = new SpriteDrawable(new Sprite(Assets.popin_big_btn_down));
		Sprite disableSprite = new Sprite(Assets.popin_big_btn_up);
		disableSprite.setAlpha(0.5f);
		Drawable btnDisabled = new SpriteDrawable(disableSprite);
		bigButtonStyle = new TextButtonStyle(btnUp, btnDown, null, Assets.font);
		bigButtonStyle.disabled = btnDisabled;

		Drawable sbtnUp = new SpriteDrawable(new Sprite(Assets.popin_small_btn_up));
		Drawable sbtnDown = new SpriteDrawable(new Sprite(Assets.popin_small_btn_down));
		Sprite sdisableSprite = new Sprite(Assets.popin_small_btn_up);
		sdisableSprite.setAlpha(0.5f);
		Drawable sbtnDisabled = new SpriteDrawable(sdisableSprite);
		smallButtonStyle = new TextButtonStyle(sbtnUp, sbtnDown, null, Assets.font);
		smallButtonStyle.disabled = sbtnDisabled;	
		
		smallButtonCheckedStyle = new TextButtonStyle(sbtnUp, sbtnDown, sbtnDown, Assets.font);
		smallButtonCheckedStyle.disabled = sbtnDisabled;	
    }
    
    
    public static LabelStyle hudStyle() {
    	if (instance == null) instance = new PopinService();
    	return instance.hudStyle;
    }
    
    public static LabelStyle smallTextStyle() {
    	if (instance == null) instance = new PopinService();
    	return instance.smallTextStyle;
    }
    
    public static TextButtonStyle bigButtonStyle() {
    	if (instance == null) instance = new PopinService();
    	return instance.bigButtonStyle;
    }
    
    public static TextButtonStyle smallButtonStyle() {
    	if (instance == null) instance = new PopinService();
    	return instance.smallButtonStyle;
    }
    
    public static TextButtonStyle smallButtonCheckedStyle() {
    	if (instance == null) instance = new PopinService();
    	return instance.smallButtonCheckedStyle;
    }
    
    public static void dispose() {
    	instance = null;
    }
}

