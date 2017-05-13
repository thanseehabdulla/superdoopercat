package com.boontaran.supercat.level.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.boontaran.MessageEvent;
import com.boontaran.games.Delayer;
import com.boontaran.games.Util;
import com.boontaran.supercat.SuperCat;
import com.boontaran.ui.NButton;


/**
 * Created by arifbs on 11/20/15.
 */
public class CompletedDialog extends Dialog {
    //events
    public static final int NEXT = 1;
    public static final int QUIT = 2;
    private final NButton quitBtn,nextBtn;

    //contents
    private final Table table;
    private Label coinLabel,enemyLabel,timerLabel;
    private Image tickCoin,tickEnemy,tickTimer;
    private Image starCoin,starEnemy,starTimer;

    //delayer
    private Delayer delayer = new Delayer(null);

    public CompletedDialog(float w, float h) {
        super(w, h);
        setBgSize(610, 480);

        //image title
        setTitle("out/completed");

        //buttons and listeners
        quitBtn = new NButton(SuperCat.getRegion("out/quit_btn"));
        addActor(quitBtn);
        quitBtn.setPosition(contentX, contentY);
        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fire(new MessageEvent(QUIT));
                SuperCat.media.playSound("button.mp3");
            }
        });
        quitBtn.setVisible(false);

        nextBtn = new NButton(SuperCat.getRegion("out/next_btn"));
        addActor(nextBtn);
        nextBtn.setPosition(contentX + contentWidth - nextBtn.getWidth(), contentY);
        nextBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fire(new MessageEvent(NEXT));
                SuperCat.media.playSound("button.mp3");
            }
        });
        nextBtn.setVisible(false);

        //create content
        table = new Table();
        table.defaults().left();
        table.defaults().padLeft(20).padRight(20).padBottom(4).padTop(0);

        addActor(table);
        table.align(Align.left);

        //put content
        table.add(SuperCat.createImage("out/completed_coin"));
        coinLabel = Util.createLabel("xxxxx" , SuperCat.font32, new Color(0xffffffff));
        table.add(coinLabel);
        tickCoin = SuperCat.createImage("out/tick_off");
        table.add(tickCoin);

        table.row();

        table.add(SuperCat.createImage("out/completed_enemy"));
        enemyLabel = Util.createLabel("xxxxx" , SuperCat.font32, new Color(0xffffffff));
        table.add(enemyLabel);
        tickEnemy = SuperCat.createImage("out/tick_off");
        table.add(tickEnemy);

        table.row();

        table.add(SuperCat.createImage("out/completed_timer"));
        timerLabel = Util.createLabel("xxxxxx" , SuperCat.font32, new Color(0xffffffff));
        table.add(timerLabel);
        tickTimer = SuperCat.createImage("out/tick_off");
        table.add(tickTimer);

        table.pack();

        table.setX(contentX + (contentWidth-table.getWidth())/2);
        table.setY(contentY + 100);

        starEnemy = SuperCat.createImage("out/star_off");
        addActor(starEnemy);
        starEnemy.setX(contentX + (contentWidth-starEnemy.getWidth())/2);
        starEnemy.setY(contentY+100);
        starEnemy.setVisible(false);

        starCoin = SuperCat.createImage("out/star_off");
        addActor(starCoin);
        starCoin.setX(starEnemy.getX() - starCoin.getWidth()-20);
        starCoin.setY(starEnemy.getY());
        starCoin.setVisible(false);

        starTimer = SuperCat.createImage("out/star_off");
        addActor(starTimer);
        starTimer.setX(starEnemy.getRight()+20);
        starTimer.setY(starEnemy.getY());
        starTimer.setVisible(false);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        //run delayer
        delayer.update(delta);
    }

    //put data
    public void setData(float coin, float enemies, float time) {
        int coinI = (int) (coin*10);
        String coinTxt = ((float)coinI)/10 +" %";
        coinLabel.setText(coinTxt);

        int enemyI = (int) (enemies*10);
        String enemyTxt = ((float)enemyI)/10+" %";
        enemyLabel.setText(enemyTxt);

        int timeI = (int)(time*10);
        String timeTxt = ((float)timeI)/10+" sec";
        timerLabel.setText(timeTxt);

        delayer.addDelay(1.3f, new Delayer.Listener() {
            @Override
            public void onDelay() {
                table.addAction(Actions.moveTo(table.getX() , contentY+150 , 0.3f,Interpolation.fade));
            }
        });
    }

    public void setTick1() {
        setTick(tickCoin);
    }
    public void setTick2() {
        setTick(tickEnemy);
    }
    public void setTick3() {
        setTick(tickTimer);

    }

    //set the 'tick's
    private void setTick(Image tick) {
        tick.setDrawable(new TextureRegionDrawable(SuperCat.getRegion("out/tick_on")));

        float ow = tick.getWidth();
        float oh = tick.getHeight();
        float w = tick.getWidth()*1.3f;
        float h = tick.getHeight()*1.3f;

        tick.setSize(w, h);
        tick.moveBy(-(w-ow)/2 , -(h-oh)/2);
        tick.addAction(Actions.sizeTo(ow,oh,0.3f, Interpolation.fade));
        tick.addAction(Actions.moveBy((w-ow)/2,(h-oh)/2,0.3f,Interpolation.fade));
    }

    private int starData;
    private boolean lastStar1,lastStar2,lastStar3;

    //set if get star or not
    public void showStars(int lastData,int newData) {
        starCoin.setVisible(true);
        starEnemy.setVisible(true);
        starTimer.setVisible(true);

        setPreviousStar(lastData);
        starData = newData;

        //animate with delays
        delayer.addDelay(0.2f, new Delayer.Listener() {
            @Override
            public void onDelay() {
                if(starData >= 100) {
                    if(!lastStar1) {
                        starCoin.setDrawable(new TextureRegionDrawable(SuperCat.getRegion("out/star_on")));
                        animateStar(starCoin);
                    }
                    starData = starData % 100;
                }
            }
        });

        delayer.addDelay(0.4f, new Delayer.Listener() {

            @Override
            public void onDelay() {
                if(starData >= 10) {
                    if(!lastStar2) {
                        starEnemy.setDrawable(new TextureRegionDrawable(SuperCat.getRegion("out/star_on")));
                        animateStar(starEnemy);
                    }
                    starData = starData % 10;
                }
            }
        });

        delayer.addDelay(0.6f, new Delayer.Listener() {

            @Override
            public void onDelay() {
                if(starData > 0) {
                    if(!lastStar3) {
                        starTimer.setDrawable(new TextureRegionDrawable(SuperCat.getRegion("out/star_on")));
                        animateStar(starTimer);
                    }
                }
            }
        });

    }

    //if already have stars form previous try
    private void setPreviousStar(int data) {
        if(data >= 100 ) {
            starCoin.setDrawable(new TextureRegionDrawable(SuperCat.getRegion("out/star_on")));
            data = data % 100;
            lastStar1 = true;
        }

        if(data >= 10 ) {
            starEnemy.setDrawable(new TextureRegionDrawable(SuperCat.getRegion("out/star_on")));
            data = data % 10;
            lastStar2 = true;
        }
        if(data > 0) {
            starTimer.setDrawable(new TextureRegionDrawable(SuperCat.getRegion("out/star_on")));
            lastStar3=true;
        }
    }

    //star showing animation, zoom effect
    private void animateStar(Image star) {
        float ow = star.getWidth();
        float oh = star.getHeight();
        float w = star.getWidth()*1.3f;
        float h = star.getHeight()*1.3f;

        star.setSize(w, h);
        star.moveBy(-(w-ow)/2 , -(h-oh)/2);
        star.addAction(Actions.sizeTo(ow,oh,0.3f, Interpolation.fade));
        star.addAction(Actions.moveBy((w-ow)/2,(h-oh)/2,0.3f,Interpolation.fade));
    }
    public void showBtn() {
        nextBtn.setVisible(true);
        quitBtn.setVisible(true);
    }
}
