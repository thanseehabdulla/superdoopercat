package com.boontaran.supercat.level;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 11/13/15.
 */
public class MissileLauncher extends Entity {
    private Level level;
    private float missileTime = (float) (5 * Math.random());

    public MissileLauncher(Level level) {
        this.level = level;

        //no interaction
        setNoCollision(true);
        setNoLandCollision(true);
        noGravity = true;

        //the display
        Image image = SuperCat.createImage("missile_launcher");
        setImage(image);
        setSize(image.getWidth(), image.getHeight());

        edgeUpdateLimRatio = 1.25f;
    }

    @Override
    public void update(float delta) {
        missileTime -= delta;
        if(missileTime <=0) {
            missileTime = (float) (4 + 3 * Math.random());  //next missile launch

            //launch
            if(!level.getHero().hasDied()) {

                //get missile
                Missile missile;
                missile = level.getMissile();
                //position
                missile.setPosition(getX(), getY());
                level.addMissile(this, missile);
                missile.setRotation(90);
                missile.setVY(600);

                //start it
                missile.start();

            }
        }
    }
}
