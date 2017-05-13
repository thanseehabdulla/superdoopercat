package com.boontaran.supercat;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by arifbs on 12/27/15.
 */
public class SoundBtn extends Group {
    private Image on,off;
    public SoundBtn() {
        setTransform(false);

        //the images of button
        on = SuperCat.createImage("out/sound_on");
        off = SuperCat.createImage("out/sound_off");
        setSize(on.getWidth(),on.getHeight());

        addActor(on);
        addActor(off);

        //set the image based on state
        if(SuperCat.data.isMuted()) {
            on.setVisible(false);
            off.setVisible(true);
        } else {
            on.setVisible(true);
            off.setVisible(false);
        }

        addCaptureListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                event.setTarget(SoundBtn.this);
                return true;
            }
        });

        //listen when clicked
        addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(SuperCat.data.isMuted()) {
                    setMute(false);
                    SuperCat.data.setMute(false);
                } else {
                    setMute(true);
                    SuperCat.data.setMute(true);
                }
            }
        });

    }

    //set the image based on state
    private void setMute(boolean mute) {
        if(mute) {
            on.setVisible(false);
            off.setVisible(true);
        } else {
            on.setVisible(true);
            off.setVisible(false);
        }
    }
}
