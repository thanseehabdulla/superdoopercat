package com.boontaran.supercat.level.ui;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 11/17/15.
 *
 * abstract dialog
 */
public abstract  class Dialog extends Group {
    private final Image bg;
    protected float contentX,contentY,contentWidth,contentHeight;

    public Dialog(float w,float h) {
        setTransform(false);
        setSize(w, h);

        //black background
        NinePatch patch = new NinePatch(SuperCat.getRegion("out/black"),4,4,4,4);
        Image blackBg = new Image(patch);
        blackBg.setSize(w, h);
        addActor(blackBg);

        //the dialog background
        NinePatch patch2 = new NinePatch(SuperCat.getRegion("out/dialog_bg"),55,55,55,55);
        bg = new Image(patch2);
        addActor(bg);
        setBgSize(610, 408);

    }

    //set dialog size
    protected void setBgSize(float w, float h) {
        bg.setSize(w,h);
        bg.setX((getWidth() - bg.getWidth()) / 2);
        bg.setY((getHeight() - bg.getHeight()) / 2);


        contentX = bg.getX() + 73;
        contentY = bg.getY() + 52;
        contentWidth = bg.getWidth() - 2*73;
        contentHeight = bg.getHeight() - 52 - 128;
    }

    //put image as title
    protected void setTitle(String region) {
        Image title = SuperCat.createImage(region);
        addActor(title);
        title.setY(bg.getTop() - 50 - title.getHeight());
        title.setX((getWidth()-title.getWidth())/2);
    }
}
