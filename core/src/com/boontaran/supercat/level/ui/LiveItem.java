package com.boontaran.supercat.level.ui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 11/19/15.
 */
public class LiveItem extends PanelItem {
    private Image healthBar;
    private float barMaxWidth;

    public LiveItem() {
        setWidth(144);
        setIcon("health_icon");

        //track
        NinePatch patch = new NinePatch(SuperCat.getRegion("health_track"),3,3,3,3);
        Image healthTrack = new Image(patch);
        healthTrack.setWidth(86);
        addActor(healthTrack);
        healthTrack.setPosition(icon.getRight()+2 , 7);

        //resizeable bar
        NinePatch patch2 = new NinePatch(SuperCat.getRegion("health_bar"),2,2,2,2);
        healthBar = new Image(patch2);
        addActor(healthBar);
        healthBar.setPosition(healthTrack.getX() + 3f, healthTrack.getY()+2.5f);
        healthBar.setWidth(healthTrack.getWidth() - 3f*2f);
        barMaxWidth = healthBar.getWidth();
    }

    //set display
    public void setHealth(float ratio) {
        //resize the bar
        healthBar.setWidth(ratio*barMaxWidth);
        if(healthBar.getWidth()<4) {
            healthBar.setVisible(false);
        } else {
            healthBar.setVisible(true);
        }
    }

}
