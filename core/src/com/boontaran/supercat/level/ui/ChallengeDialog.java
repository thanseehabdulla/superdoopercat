package com.boontaran.supercat.level.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.boontaran.MessageEvent;
import com.boontaran.games.Util;
import com.boontaran.supercat.SuperCat;
import com.boontaran.ui.NButton;

/**
 * Created by arifbs on 12/23/15.
 */
public class ChallengeDialog extends Dialog {
    public static final int ACCEPT = 1;

    public ChallengeDialog(float w, float h,int levelId,int coinsPct,int enemiesPct,int sec) {
        super(w, h);
        setBgSize(610, 480);

        setTitle("out/challenge");

        //'accept' button
        NButton acceptBtn = new NButton(SuperCat.getRegion("out/accept_btn"));
        addActor(acceptBtn);
        acceptBtn.setPosition(contentX + (contentWidth-acceptBtn.getWidth())/2, contentY);
        acceptBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fire(new MessageEvent(ACCEPT));
                SuperCat.media.playSound("button.mp3");
            }
        });


        //table to show data
        Table table = new Table();

        table.defaults().left();
        table.defaults().padLeft(20).padRight(20).padBottom(4).padTop(0);

        addActor(table);
        table.align(Align.left);

        //add table content
        table.add(SuperCat.createImage("out/completed_coin"));
        Label coinLabel = Util.createLabel(coinsPct+" %", SuperCat.font32, new Color(0xffffffff));
        table.add(coinLabel);

        Image tickCoin;

        //if already get the achievement, tick it on
        if(SuperCat.data.isStar(levelId,1)) {
            tickCoin  = SuperCat.createImage("out/tick_on");
            coinLabel.setText("");
        } else {
            tickCoin  = SuperCat.createImage("out/tick_off");
        }
        table.add(tickCoin);



        table.row();

        table.add(SuperCat.createImage("out/completed_enemy"));
        Label enemyLabel = Util.createLabel(enemiesPct+" %" , SuperCat.font32, new Color(0xffffffff));
        table.add(enemyLabel);

        Image tickEnemy;
        //if already get the achievement, tick it on
        if(SuperCat.data.isStar(levelId,2)) {
            tickEnemy = SuperCat.createImage("out/tick_on");
            enemyLabel.setText("");
        } else {
            tickEnemy = SuperCat.createImage("out/tick_off");
        }

        table.add(tickEnemy);

        table.row();

        table.add(SuperCat.createImage("out/completed_timer"));
        Label timerLabel = Util.createLabel(sec+" sec" , SuperCat.font32, new Color(0xffffffff));
        table.add(timerLabel);
        Image tickTimer;

        //if already get the achievement, tick it on
        if(SuperCat.data.isStar(levelId,3)) {
            tickTimer = SuperCat.createImage("out/tick_on");
            timerLabel.setText("");
        } else {
            tickTimer = SuperCat.createImage("out/tick_off");
        }


        table.add(tickTimer);
        table.pack();

        table.setX(contentX + (contentWidth-table.getWidth())/2);
        table.setY(contentY + 130);

    }
}
