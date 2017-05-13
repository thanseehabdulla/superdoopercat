package com.boontaran.supercat.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.boontaran.games.StageGame;
import com.boontaran.supercat.SoundBtn;
import com.boontaran.supercat.SuperCat;
import com.boontaran.ui.NButton;

/**
 * Created by arifbs on 10/19/15.
 */
public class Intro extends StageGame {
    //events
    public static  final int ON_PLAY = 1;
    public static  final int ON_BACK = 2;
    public static  final int ON_REMOVE_ADS = 3;

    //indicate if show the iap button or not
    public static boolean useIAP;

    @Override
    protected void create() {
        //the background
        Image bg = SuperCat.createImage("bg/intro_bg");
        addBackground(bg,true,false);

        //title
        Image title = SuperCat.createImage("out/title");
        addChild(title);
        //set position
        centerActorX(title);
        title.setY(getHeight()-title.getHeight()-20);

        //play button
        NButton playBtn = SuperCat.createButton("out/play_btn");
        addChild(playBtn);
        centerActorX(playBtn);
        playBtn.setY(80);
        playBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                call(ON_PLAY);
                SuperCat.media.playSound("button.mp3");
            }
        });

        //credits button
        NButton creditBtn = SuperCat.createButton("out/credits_btn");
        addChild(creditBtn);
        centerActorX(creditBtn);
        creditBtn.setY(4);
        creditBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showCreditScreen();
            }
        });

        //mute button
        SoundBtn soundBtn = new SoundBtn();
        addChild(soundBtn);
        soundBtn.setPosition(
                getWidth()-soundBtn.getWidth()-12,
                getHeight()-soundBtn.getHeight()-42);

        //iap button
        if(!SuperCat.data.isRemoveAds() && useIAP) {
            NButton removeAdsBtn = SuperCat.createButton("out/remove_ads_btn");
            removeAdsBtn.setPosition(
                    getWidth() - removeAdsBtn.getWidth() - 12,
                    40);
            addChild(removeAdsBtn);

            //send message to initiate purchase
            removeAdsBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    call(ON_REMOVE_ADS);
                }
            });
        }
    }

    //create screen and show it
    private void showCreditScreen() {
        CreditScreen creditScreen = new CreditScreen();
        addChild(creditScreen);
    }

    //listen on back key
    @Override
    public boolean keyDown(int keycode) {
        if(keycode== Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            call(ON_BACK);
            return true;
        }
        return super.keyDown(keycode);
    }
}
