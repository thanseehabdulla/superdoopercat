package com.boontaran.supercat.level.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.boontaran.games.Util;
import com.boontaran.supercat.SuperCat;

/**
 * Created by arifbs on 11/19/15.
 *
 * collected coins counter
 */
public class CoinItem extends PanelItem {
    private Label label;
    private int num=-1;

    public CoinItem() {
        setWidth(130);
        setIcon("coin_icon");

        //the label
        label = Util.createLabel("x 999", SuperCat.font32,new Color(0xffffffff));
        addActor(label);
        label.setX(icon.getRight() + 24);
        label.setY((getHeight()-label.getHeight())/2);
        label.setY(6);

        //init
        setNumCoins(0);
    }
    public void setNumCoins(int num) {
        if(this.num == num) return;
        this.num = num;

        String str = "x "+num;
        label.setText(str);
    }
}
