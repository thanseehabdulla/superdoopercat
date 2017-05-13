package com.boontaran.supercat.level;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 10/21/15.
 *
 * a splash animation when a coin is collected
 */
public class CoinTrail extends Entity implements Pool.Poolable {
    public static final int REMOVE = 1;
    private float time;

    public CoinTrail() {
        //image
        Image img = SuperCat.createImage("coin_trail");
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

        //remove then time's up
        if(time <0) {
            fireEvent(REMOVE);
        }
    }

    //out screen , remove this
    @Override
    public void onSkipUpdate(float delta) {
        fireEvent(REMOVE);
    }

    @Override
    public void reset() {
        time = (float) (0.5f + Math.random()*0.5f);
        time2 = time;
        setRotation((float) (Math.random()*360));
    }
}
