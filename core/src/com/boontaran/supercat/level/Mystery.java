package com.boontaran.supercat.level;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.boontaran.games.platformerLib.Entity;
import com.boontaran.games.platformerLib.World;
import com.boontaran.supercat.SuperCat;
import com.boontaran.supercat.level.enemies.Enemy;
import com.boontaran.supercat.level.enemies.Enemy1;
import com.boontaran.supercat.level.enemies.Enemy2;

/**
 * Created by arifbs on 10/21/15.
 */
public class Mystery extends Entity {
    //images
    private Image normal,touched,expired;
    private boolean hasExpired=false;
    private float blinkTime;

    //content
    private Bones bones;
    private Enemy enemy;
    private Array<Coin> coins;


    public Mystery() {
        setSize(Level.GRID, Level.GRID);

        //image states
        normal = SuperCat.createImage("mystery");
        touched = SuperCat.createImage("mystery_touched");
        expired = SuperCat.createImage("mystery_expired");

        setImage(normal);
    }

    //add coins
    public void addCoin(int count) {
        coins = new Array<Coin>();
        while(count-- >0) {
            coins.add(new Coin());
        }
    }
    //add bullet
    public void addBullet(int num) {
        bones= new Bones(num);
    }

    //add enemy
    public void addEnemy(int type) {
        if(type == 1) {
            enemy = new Enemy1((Level) world);
        } else {
            enemy = new Enemy2((Level) world);
        }
    }


    @Override
    public void update(float delta) {
        if(blinkTime > 0) {  //blink after hero touch
            blinkTime -= delta;
            if(blinkTime <=0) {
                if(hasExpired) { //expired
                    setImage(expired);
                } else {
                    setImage(normal);
                }
            }
        }
    }

    public void touched() {
        if(hasExpired) return;

        //change display
        setImage(touched);
        SuperCat.media.playSound("mystery_box.mp3");
        if(!hasExpired)blinkTime =0.1f;

    }

    //get a coin
    public Coin getCoin() {
        Coin coin = null;

        if(coins != null && coins.size>0) {
            coin = coins.removeIndex(0);
            if (coins.size == 0) {  //out of coin, set expired
                setExpired();
            }
        }
        return coin;
    }

    //get bones
    public Bones getBones() {
        if(bones==null) return null;
        setExpired();

        return bones;
    }

    //get enemy
    public Enemy getEnemy(){
        if(enemy == null) return null;
        setExpired();
        return enemy;
    }

    //set expired display
    private void setExpired() {
        setImage(expired);
        hasExpired = true;
    }

    public boolean hasExpired() {
        return hasExpired;
    }
}
