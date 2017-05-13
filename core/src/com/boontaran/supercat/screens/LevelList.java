package com.boontaran.supercat.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.boontaran.games.Delayer;
import com.boontaran.games.StageGame;
import com.boontaran.supercat.Settings;
import com.boontaran.supercat.SuperCat;
import com.boontaran.ui.NButton;

import java.util.logging.Level;

/**
 * Created by arifbs on 10/19/15.
 */
public class LevelList extends StageGame {
    //events
    public static final int ON_BACK = 1;
    public static final int ON_SELECT = 2;
    public static final int ON_RATE_BUTTON = 3;

    //selected icon id
    private int selectedId;
    //icons container
    private Group container;
    //num of pages, representing num of worlds
    private int numPages;
    //store the last viewed page
    private static int lastPageShown=1;

    //nav button
    private NButton nextBtn,prevBtn;

    //backgrounds..
    private Array<Image> bgImages;


    @Override
    protected void create() {
        enableBackgroundBlending=true;

        //create the container
        container = new Group();
        container.setTransform(false);
        addChild(container);

        //create next page button
        nextBtn = new NButton(SuperCat.getRegion("out/next_world_btn"));
        addOverlayChild(nextBtn);
        nextBtn.setX(getWidth()-nextBtn.getWidth()-5);
        centerActorY(nextBtn);

        //what to do when clicked
        nextBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showPage(++lastPageShown);
                SuperCat.media.playSound("button.mp3");
            }

        });

        //create the prev page button
        TextureRegion reg = new TextureRegion(SuperCat.getRegion("out/next_world_btn"));
        reg.flip(true,false);
        prevBtn = new NButton(reg);
        addOverlayChild(prevBtn);
        prevBtn.setX(5);
        centerActorY(prevBtn);

        //when clicked
        prevBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showPage(--lastPageShown);
                SuperCat.media.playSound("button.mp3");
            }
        });


        bgImages = new Array<Image>();

        //create pages / worlds based on settings
        int i;
        for(i=0;i<Settings.WORLDS;i++) {
            createWorld(i+1);
        }

        //use coming soon screen?
        if(Settings.SHOW_COMING_SOON) {
            createComingSoon();
        }

        //the size
        container.setSize(numPages*getWidth(),getHeight());

        //show the last viewed page
        showPage(lastPageShown,false);

    }

    @Override
    protected void update(float delta) {
        super.update(delta);
        background.act();
    }

    private void showPage(int id) {
        showPage(id,true);
    }

    //show a page
    private void showPage(int id,boolean withAnimation) {
        //showing it by shift the container postition
        if(!withAnimation) {
            container.setX(-(id-1)*getWidth());
        } else {
            container.clearActions();
            container.addAction(Actions.moveTo(-(id-1)*getWidth() , 0, 0.5f, Interpolation.exp5));
        }

        //change the background also
        changeBG(withAnimation);

        //store the last viewed
        lastPageShown = id;

        //show or hide nav buttons based on the current page
        prevBtn.setVisible(true);
        nextBtn.setVisible(true);

        if(id == 1) {
            prevBtn.setVisible(false);  //fist page, hide prev button
        }
        if(id == numPages) {
            nextBtn.setVisible(false); //last page hide next button
        }

        //only one page, hide all nav button
        if(numPages==1) {
            prevBtn.setVisible(false);
            nextBtn.setVisible(false);
        }
    }

    private void changeBG(boolean withAnimation) {
        if(!withAnimation) {
            clearBackground();
            addBackground(bgImages.get(lastPageShown - 1), true, false);
            return;
        }

        //get the current bg
        final Image curBg = (Image) background.getActors().get(0);
        //create the new bg
        Image newBg = bgImages.get(lastPageShown-1);

        //add the new bg
        addBackground(newBg,true,false);
        newBg.setColor(1,1,1,0);
        newBg.addAction(Actions.alpha(1,0.4f));

        //fade out the old bg
        delayCall(0.4f, new Delayer.Listener(){
            @Override
            public void onDelay() {
                removeBackground(curBg);
            }
        });

    }

    private int lastLevelId=1;
    private void createWorld(int id) {
        numPages++;

        //get the custom bg
        if(Settings.PAGE_BACKGROUNDS != null) {
            String bg = Settings.PAGE_BACKGROUNDS.get(id);

            if(bg != null && !bg.isEmpty()) {
                bgImages.add(SuperCat.createImage("bg/"+bg));
            } else {
                //not defined, use default
                bgImages.add(SuperCat.createImage("bg/level_list_bg"));
            }
        } else {
            //use default
            bgImages.add(SuperCat.createImage("bg/level_list_bg"));
        }




        //create table containing icons..
        Table table = new Table();


        table.defaults().pad(12);
        int col,row;
        int progress = SuperCat.data.getProgress();
        int levelId=lastLevelId;

        //put into the cell
        for(row=0; row< Settings.ROWS; row++) {
            for(col=0;col<Settings.COLS;col++) {
                LevelIcon icon = new LevelIcon(levelId);

                //set listener, call when clicked
                icon.addListener(iconListener);
                table.add(icon);

                //debug, unlock all
                if(levelId <= progress ||Settings.UNLOCK_ALL) {
                    icon.setLock(false);
                }
                levelId++;
            }
            table.row();
        }
        lastLevelId = levelId;

        //finish the table, put on container
        table.pack();
        container.addActor(table);
        table.setX((id-1)*getWidth());
        table.moveBy((getWidth()-table.getWidth())/2,(getHeight()-table.getHeight())/2);
    }
    private void createComingSoon() {
        numPages++;

        //check the bg, if doesn't exist, use default level list bg
        if(SuperCat.getRegion("bg/level_list_bg_coming_soon") != null) {
            bgImages.add(SuperCat.createImage("bg/level_list_bg_coming_soon"));
        } else {
            bgImages.add(SuperCat.createImage("bg/level_list_bg"));
        }

        //add the image
        Image img = SuperCat.createImage("out/coming_soon");
        container.addActor(img);
        img.setX((numPages-1)*getWidth());
        img.moveBy((getWidth()-img.getWidth())/2,0);
        img.setY(getHeight()-img.getHeight()-80);

        //add rate button
        NButton rateBtn = new NButton(SuperCat.getRegion("out/rate_btn"));
        rateBtn.setX((numPages-1)*getWidth() + (getWidth()-rateBtn.getWidth())/2);
        rateBtn.setY(25);
        container.addActor(rateBtn);

        //when clicked
        rateBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                call(ON_RATE_BUTTON);
            }
        });
    }
    public int getSelectedId(){
        return selectedId;
    }

    //called when an icon is clicked
    private ClickListener iconListener = new ClickListener(){
        @Override
        public void clicked(InputEvent event, float x, float y) {
            //get the clicked icon
            LevelIcon icon = (LevelIcon) event.getTarget();

            //set the id from icon
            selectedId = icon.id;

            //send message that an icon is clicked
            call(ON_SELECT);

            //play a sound
            SuperCat.media.playSound("button.mp3");
        }
    };

    //monitor on back key pressed
    @Override
    public boolean keyDown(int keycode) {
        if(keycode== Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            call(ON_BACK);
            SuperCat.media.playSound("button.mp3");
            return true;
        }
        return super.keyDown(keycode);
    }
}
