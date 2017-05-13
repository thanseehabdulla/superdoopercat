package com.boontaran.supercat.level;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 10/21/15.
 *
 * dust when hero stomp the ground
 */
public class Dust extends Entity implements Pool.Poolable {
    public static final int REMOVE = 1;
    private float time;

    public Dust() {
        //the image
        Image img = SuperCat.createImage("dust");
        setImage(img);

        //no interaction
        setNoCollision(true);
        setNoLandCollision(true);
        noGravity=true;

        reset();
    }

    private float time2;
    @Override
    public void update(float delta) {
        //fade out based on time
        time -= delta;
        setColor(1,1,1,time/time2);

        //remove
        if(time <0) {
            fireEvent(REMOVE);
        }
    }

    //out screen, remove this
    @Override
    public void onSkipUpdate(float delta) {
        fireEvent(REMOVE);
    }

    //set random value when putting back to object pool
    @Override
    public void reset() {
        time = (float) (0.75f + Math.random()*0.75f);
        time2 = time;
        setRotation((float) (Math.random()*360));
    }
}
