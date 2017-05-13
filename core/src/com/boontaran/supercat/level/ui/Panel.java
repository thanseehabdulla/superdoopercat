package com.boontaran.supercat.level.ui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 11/17/15.
 */
public class Panel extends Group {
    //contents
    private final LiveItem health;
    private final BoneItem bone;
    private final CoinItem coin;
    private final TimerItem timer;

    public Panel(float w) {
        setSize(w, 52);
        setTransform(false);

        //create contents & positions
        health = new LiveItem();
        addActor(health);
        health.setX(8);

        bone = new BoneItem();
        addActor(bone);
        bone.setX(health.getRight() + 16);

        coin = new CoinItem();
        addActor(coin);
        coin.setX(bone.getRight() + 16);

        timer = new TimerItem();
        addActor(timer);
        timer.setX(coin.getRight() + 16);

    }

    //set the data
    public void setHealth(float ratio) {
        health.setHealth(ratio);
    }
    public void setBones(int num) {
        bone.setNumBones(num);
    }
    public void setCoins(int num) {
        coin.setNumCoins(num);
    }
    public void setTime(float ratio) {
        timer.setTime(ratio);
    }

}
