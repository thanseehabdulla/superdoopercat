package com.boontaran.supercat.level.ui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 11/19/15.
 */
public class PanelItem extends Group {
    private final Image bg;
    protected Image icon;

    public PanelItem() {
        setHeight(44);
        setTransform(false);

        //bg
        NinePatch patch = new NinePatch(SuperCat.getRegion("panel_item_bg"),6,6,6,6);
        bg = new Image(patch);
        addActor(bg);


    }

    //resize bg based on size setup
    @Override
    protected void sizeChanged() {
        if(bg != null) bg.setSize(getWidth(),getHeight());

    }

    //the icon
    protected void setIcon(String reg) {
        icon = SuperCat.createImage(reg);
        addActor(icon);
        icon.setX(7);
        icon.setY((getHeight()-icon.getHeight())/2);
    }
}
