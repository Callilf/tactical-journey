package com.dokkaebistudio.tacticaljourney.rendering.service;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;

public class PopinService {

	private static PopinService instance;
	
	
    //*****************************
    // STYLES
    
	private LabelStyle hudStyle;
	private LabelStyle smallTextStyle;
    private TextButtonStyle buttonStyle;
    private TextButtonStyle checkedButtonStyle;
    
    private TextFieldStyle textFieldStyle;
	
    
    private PopinService() {
		hudStyle = new LabelStyle(SceneAssets.font.getFont(), Color.WHITE);
		smallTextStyle = new LabelStyle(SceneAssets.smallFont.getFont(), Color.WHITE);

		
		Drawable npBtnUp = new NinePatchDrawable(SceneAssets.buttonNinePatch);
		Drawable npBtnDown = new NinePatchDrawable(SceneAssets.buttonPressedNinePatch);
		Drawable npBtnDisabled = new NinePatchDrawable(SceneAssets.buttonDisabledNinePatch);
		buttonStyle = new TextButtonStyle(npBtnUp, npBtnDown, null, SceneAssets.font.getFont());
		buttonStyle.disabled = npBtnDisabled;
		
		Drawable npCheckedBtnUp = new NinePatchDrawable(SceneAssets.buttonNinePatch);
		Drawable npCheckedBtnDown = new NinePatchDrawable(SceneAssets.buttonPressedNinePatch);
		Drawable npCheckedBtnDisabled = new NinePatchDrawable(SceneAssets.buttonDisabledNinePatch);
		checkedButtonStyle = new TextButtonStyle(npCheckedBtnUp, npCheckedBtnDown, npCheckedBtnDown, SceneAssets.font.getFont());
		checkedButtonStyle.disabled = npCheckedBtnDisabled;

		
		textFieldStyle = new TextFieldStyle();
		textFieldStyle.background = new NinePatchDrawable(SceneAssets.popinNinePatch);
		textFieldStyle.font = SceneAssets.font.getFont();
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.cursor = new TextureRegionDrawable(SceneAssets.textfield_cursor.getRegion());
		textFieldStyle.cursor.setMinWidth(1f);
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
    
    public static TextFieldStyle textFieldStyle() {
    	if (instance == null) instance = new PopinService();
		return instance.textFieldStyle;
	}
    
    public static void dispose() {
    	instance = null;
    }
}

