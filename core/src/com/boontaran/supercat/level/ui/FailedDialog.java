package com.boontaran.supercat.level.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.boontaran.MessageEvent;
import com.boontaran.supercat.SuperCat;
import com.boontaran.ui.NButton;

/**
 * Created by arifbs on 11/20/15.
 */
public class FailedDialog extends Dialog {
    public static final int RETRY = 1;
    public static final int QUIT = 2;

    public FailedDialog(float w, float h) {
        super(w, h);
        setTitle("out/failed");

        //quit button & listener
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

        //retry button & listener
        NButton retry = new NButton(SuperCat.getRegion("out/retry_btn"));
        addActor(retry);
        retry.setPosition(contentX + contentWidth - retry.getWidth(), contentY);
        retry.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fire(new MessageEvent(RETRY));
                SuperCat.media.playSound("button.mp3");
            }
        });

    }
}
