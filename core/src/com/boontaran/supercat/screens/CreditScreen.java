package com.boontaran.supercat.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.boontaran.games.GdxGame;
import com.boontaran.supercat.SuperCat;
import com.boontaran.supercat.level.ui.Dialog;

/**
 * Created by arifbs on 4/18/16.
 */
public class CreditScreen extends Dialog {
    public CreditScreen() {
        super(GdxGame.getWidth()   , GdxGame.getHeight());

        //showing the image of credits
        Image content = SuperCat.createImage("out/credit_screen");
        addActor(content);

        //center it
        content.setX((getWidth()-content.getWidth())/2);
        content.setY((getHeight()-content.getHeight())/2);

        //remove on any screen touch
        addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                remove();
            }
        });
    }
}
