package com.boontaran.supercat.level;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 10/21/15.
 */
public class Bone extends Entity implements Pool.Poolable {
    public static final int REMOVE = 1; //event
    public int dir; //left or right

    //damage to take when hit enemy
    private float damage = 2;

    public Bone() {
        //set image
        Image img = SuperCat.createImage("bone");
        setImage(img);

        //raduis of collision
        setRadius(7);

        //full restitution
        restitution = 1;

        reset();
    }
    public float getDamage() {
        return damage;
    }

    //out screen, remove this
    @Override
    public void onSkipUpdate(float delta) {
        fireEvent(REMOVE);
    }

    //on hit wall, remove this
    @Override
    protected void hitWall(Entity ent) {
        if(getX() < ent.getX()) {
            dir = 3;
        }
        else if(getX() > ent.getX()) {
            dir = 1;
        }
        fireEvent(REMOVE);
    }

    //hit an object, remove this
    public void hitObject(Entity object) {
        if(getX() < object.getX()) {
            dir = 3;
        }
        else if(getX() > object.getX()) {
            dir = 1;
        }
        fireEvent(REMOVE);
    }

    //reset when put back to object pool
    @Override
    public void reset() {
        setV(0,0);
        dir = -1;
    }
}
