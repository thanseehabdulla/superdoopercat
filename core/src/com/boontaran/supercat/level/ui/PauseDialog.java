package com.boontaran.supercat.level.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.boontaran.MessageEvent;
import com.boontaran.supercat.SoundBtn;
import com.boontaran.supercat.SuperCat;
import com.boontaran.ui.NButton;

/**
 * Created by arifbs on 11/17/15.
 */
public class PauseDialog extends Dialog {
    public static final int RESUME = 1;
    public static final int QUIT = 2;

    public PauseDialog(float w, float h) {
        super(w, h);
        setTitle("out/paused");

        //buttons and the listener
        NButton quit = new NButton(SuperCat.getRegion("out/quit_btn"));
        addActor(quit);
        quit.setPosition(contentX, contentY);

        quit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fire(new MessageEvent(QUIT));
                SuperCat.media.playSound("button.mp3");
            }
        });

        NButton resume = new NButton(SuperCat.getRegion("out/resume_btn"));
        addActor(resume);
        resume.setPosition(contentX + contentWidth - resume.getWidth(), contentY);
        resume.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fire(new MessageEvent(RESUME));
                SuperCat.media.playSound("button.mp3");
            }
        });

        //mute button
        SoundBtn soundBtn = new SoundBtn();
        addActor(soundBtn);
        soundBtn.setX(contentX + (contentWidth-soundBtn.getWidth())/2);
        soundBtn.setY(contentY + 120);
    }
}
