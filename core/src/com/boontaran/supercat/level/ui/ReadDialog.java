package com.boontaran.supercat.level.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.boontaran.MessageEvent;
import com.boontaran.games.Util;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 12/18/15.
 */

//dialog when reading a sign
public class ReadDialog extends Group {
    public static final int CLOSE  =1;

    public ReadDialog(float w,float h,String text) {
        setTransform(false);

        setSize(w, h);

        //black bg
        NinePatch patch = new NinePatch(SuperCat.getRegion("out/black"),4,4,4,4);
        Image blackBg = new Image(patch);
        blackBg.setSize(w, h);
        addActor(blackBg);

        //listen on any touch
        addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fire(new MessageEvent(CLOSE));
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        Group content = new Group();
        content.setTransform(false);

        //image of sign
        Image bg = SuperCat.createImage("out/sign_read");
        content.addActor(bg);
        content.setSize(bg.getWidth(),bg.getHeight());
        content.setX((getWidth()-content.getWidth())/2);
        content.setY(-2);

        //text of sign
        TextArea textArea = Util.createTextArea(540,340,text,SuperCat.font64,new Color(0xffffffff));
        content.addActor(textArea);
        textArea.setX(125);
        textArea.setY(77);
        textArea.setAlignment(Align.center);

        addActor(content);
    }
}
