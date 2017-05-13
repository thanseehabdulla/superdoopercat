package com.boontaran.supercat.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.supercat.SuperCat;
import com.boontaran.ui.NButton;

/**
 * Created by arifbs on 12/18/15.
 */
public class Sign extends Entity {
    public static final int READ = 1;

    private String text;
    private float touchHeroTime;
    private NButton button;

    public Sign(String text) {
        setTouchable(Touchable.enabled);
        this.text = text;
        setSize(64,64);
        setNoLandCollision(true);
        noGravity = true;

        //the display
        Image img = SuperCat.createImage("sign");
        addActor(img);
        img.setX(-img.getWidth()/2);
        img.setY(-img.getHeight()/2-7);
        img.setTouchable(Touchable.disabled);

        //button to read
        button = new NButton(SuperCat.getRegion("read_btn"));
        addActor(button);
        button.setX(-button.getWidth()/2);
        button.setY(60);

        //button click listener
        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fireEvent(READ);
            }
        });
        button.setVisible(false);

        //capture event to set the target
        addCaptureListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                event.setTarget(Sign.this);
                return true;
            }
        });

    }
    public String getText(){
        return text;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        //calculate time, hide the button when time's up
        if(touchHeroTime>0) {
            touchHeroTime-=delta;

            if(touchHeroTime < 0) {
                button.setVisible(false);
            }
        }
    }

    //hero is touching this
    public void touchHero() {
        touchHeroTime=0.1f;
        button.setVisible(true);
    }
}
