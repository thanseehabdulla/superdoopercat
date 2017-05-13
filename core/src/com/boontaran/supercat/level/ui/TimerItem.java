package com.boontaran.supercat.level.ui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 12/22/15.
 */
public class TimerItem extends PanelItem {
    private Image timeBar,timeTrack;
    private float barMaxWidth;
    private boolean hasNoBonus = false;

    public TimerItem() {
        setWidth(130);
        setIcon("timer_icon");

        //track (borrowing the health track image)
        NinePatch patch = new NinePatch(SuperCat.getRegion("health_track"),3,3,3,3);
        timeTrack = new Image(patch);
        timeTrack.setWidth(86);
        addActor(timeTrack);
        timeTrack.setPosition(icon.getRight()+2 , 7);

        //resizeable bar
        NinePatch patch2 = new NinePatch(SuperCat.getRegion("health_bar"),2,2,2,2);
        timeBar = new Image(patch2);
        addActor(timeBar);
        timeBar.setPosition(timeTrack.getX() + 3f, timeTrack.getY()+2.5f);
        timeBar.setWidth(timeTrack.getWidth() - 3f*2f);
        barMaxWidth = timeBar.getWidth();
    }
    public void setTime(float ratio) {
        if(hasNoBonus) return;

        //resize bar
        timeBar.setWidth(ratio*barMaxWidth);
        if(timeBar.getWidth()<4) {
            timeBar.setVisible(false);
            noBonus();
        } else {
            timeBar.setVisible(true);
        }
    }
    private void noBonus() {
        hasNoBonus = true;
        timeBar.setVisible(false);
        timeTrack.setVisible(false);
    }
}
