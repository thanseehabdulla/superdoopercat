package com.boontaran.supercat.level;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.boontaran.MessageEvent;
import com.boontaran.games.platformerLib.Entity;


/**
 * Created by arifbs on 11/11/15.
 */
public abstract class Bullet extends Entity implements Poolable {
    //events
    public static final int EXPLODE = 1;
    public static final int REMOVE = 2;

    //is bullet owned by enemy?
    public boolean enemySide;

    //damage to takes
    protected float damage = 1;

    public Bullet() {
        restitution = 1;
        friction = 0;
    }

    public float getDamage() {
        return damage;
    }

    //explode when hitting wall
    @Override
    protected void hitWall(Entity ent) {
        fireEvent(EXPLODE);
    }

    //explode when hitting something
    public void hitObject(Entity ent) {
        fireEvent(EXPLODE);
    }

    //out screen, remove this
    @Override
    public void onSkipUpdate(float delta) {
        fireEvent(REMOVE);
    }

    @Override
    public void reset() {
        setV(0,0);
    }
}
