package com.dokkaebistudio.tacticaljourney.rendering.service;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.dokkaebistudio.tacticaljourney.Assets;

public class PopinService {

	private static PopinService instance;
	
	
    //*****************************
    // STYLES
    
	private LabelStyle hudStyle;
	private LabelStyle smallTextStyle;
    private TextButtonStyle buttonStyle;
    private TextButtonStyle checkedButtonStyle;
    private TextButtonStyle bigButtonStyle;
    private TextButtonStyle smallButtonStyle;
    private TextButtonStyle smallButtonCheckedStyle;
	
    
    private PopinService() {
		hudStyle = new LabelStyle(Assets.font.getFont(), Color.WHITE);
		smallTextStyle = new LabelStyle(Assets.smallFont.getFont(), Color.WHITE);

		
		Drawable npBtnUp = new NinePatchDrawable(Assets.buttonNinePatch);
		Drawable npBtnDown = new NinePatchDrawable(Assets.buttonPressedNinePatch);
		Drawable npBtnDisabled = new NinePatchDrawable(Assets.buttonDisabledNinePatch);
		buttonStyle = new TextButtonStyle(npBtnUp, npBtnDown, null, Assets.font.getFont());
		buttonStyle.disabled = npBtnDisabled;
		
		Drawable npCheckedBtnUp = new NinePatchDrawable(Assets.buttonNinePatch);
		Drawable npCheckedBtnDown = new NinePatchDrawable(Assets.buttonPressedNinePatch);
		Drawable npCheckedBtnDisabled = new NinePatchDrawable(Assets.buttonDisabledNinePatch);
		checkedButtonStyle = new TextButtonStyle(npCheckedBtnUp, npCheckedBtnDown, npCheckedBtnDown, Assets.font.getFont());
		checkedButtonStyle.disabled = npCheckedBtnDisabled;
		
		Drawable btnUp = new SpriteDrawable(new Sprite(Assets.popin_big_btn_up.getRegion()));
		Drawable btnDown = new SpriteDrawable(new Sprite(Assets.popin_big_btn_down.getRegion()));
		Sprite disableSprite = new Sprite(Assets.popin_big_btn_up.getRegion());
		disableSprite.setAlpha(0.5f);
		Drawable btnDisabled = new SpriteDrawable(disableSprite);
		bigButtonStyle = new TextButtonStyle(btnUp, btnDown, null, Assets.font.getFont());
		bigButtonStyle.disabled = btnDisabled;

		Drawable sbtnUp = new SpriteDrawable(new Sprite(Assets.popin_small_btn_up.getRegion()));
		Drawable sbtnDown = new SpriteDrawable(new Sprite(Assets.popin_small_btn_down.getRegion()));
		Sprite sdisableSprite = new Sprite(Assets.popin_small_btn_up.getRegion());
		sdisableSprite.setAlpha(0.5f);
		Drawable sbtnDisabled = new SpriteDrawable(sdisableSprite);
		smallButtonStyle = new TextButtonStyle(sbtnUp, sbtnDown, null, Assets.font.getFont());
		smallButtonStyle.disabled = sbtnDisabled;	
		
		smallButtonCheckedStyle = new TextButtonStyle(sbtnUp, sbtnDown, sbtnDown, Assets.font.getFont());
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
    
    public static TextButtonStyle buttonStyle() {
    	if (instance == null) instance = new PopinService();
    	return instance.buttonStyle;
    }
    
    public static TextButtonStyle checkedButtonStyle() {
    	if (instance == null) instance = new PopinService();
    	return instance.checkedButtonStyle;
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

