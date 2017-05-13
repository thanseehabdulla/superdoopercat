package com.boontaran.supercat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.boontaran.games.GdxGame;
import com.boontaran.games.Media;
import com.boontaran.games.StageGame;
import com.boontaran.supercat.level.Level;
import com.boontaran.supercat.level.SubLevel;
import com.boontaran.supercat.screens.Intro;
import com.boontaran.supercat.screens.LevelList;
import com.boontaran.ui.NButton;


public class SuperCat extends GdxGame {
    private static final String TAG = "SuperCat";

    public static final int EXIT_APP = 0;
    public static final int READY = 1;
    public static final int SHOW_ADS = 2;
    public static final int OPEN_MARKET = 3;
    public static final int REMOVE_ADS = 4;


    //asset manager, handling images, fonts, and audio files
    private AssetManager assetManager;
    private boolean loadAssets;

    //hold reference to loaded images (as texture atlas)
    private static TextureAtlas atlas;

    //game data
    public static Data data;


    //bitmap fonts
    public static BitmapFont font32,font64,font144;

    public static Media media;

    //screens
    private Intro intro;
    private LevelList levelList;
    private Level level;
    private SubLevel subLevel;

    public static TextureAtlas getAtlas() {
        return atlas;
    }

    private boolean profileGL = false;
    private boolean logFPS = false;

    private FPSLogger fpsLogger;

    public static boolean tester;

    private static Callback callback;

    public SuperCat(Callback callback) {
        //game dimension
        super(800,480);

        SuperCat.callback = callback;
    }

    public static void trackPage(String name) {
        SuperCat.callback.trackPage(name);
    }

    public static void trackEvent(String label) {
        SuperCat.callback.trackEvent(label);
    }


    @Override
    public void create() {

        Gdx.input.setCatchBackKey(true);
        //Gdx.app.setLogLevel(Application.LOG_NONE);


        if(profileGL)
            GLProfiler.enable();

        if(logFPS)
            fpsLogger = new FPSLogger();

        //data
        data = new Data();




        //show 'please_wait.png' while assets are being loaded
        setScreen(new Preload());

        //create the manager
        assetManager=new AssetManager();

        //queue items

        // IMAGES
        assetManager.load("images/pack.atlas", TextureAtlas.class);


        // FONTS
        BitmapFontLoader.BitmapFontParameter param = new BitmapFontLoader.BitmapFontParameter();
        param.atlasName = "images/pack.atlas";

        /*
        assetManager.load("fonts/font32.fnt", BitmapFont.class, param);
        assetManager.load("fonts/font64.fnt", BitmapFont.class, param);
        assetManager.load("fonts/font144.fnt", BitmapFont.class, param);
        */

        //sounds
        assetManager.load("sounds/button.mp3", Sound.class);
        assetManager.load("sounds/brick.mp3", Sound.class);
        assetManager.load("sounds/coin.mp3", Sound.class);
        assetManager.load("sounds/dumdum.mp3", Sound.class);
        assetManager.load("sounds/hit.mp3", Sound.class);
        assetManager.load("sounds/jump.mp3", Sound.class);
        assetManager.load("sounds/level_completed.mp3", Sound.class);
        assetManager.load("sounds/level_completed2.mp3", Sound.class);
        assetManager.load("sounds/missile.mp3", Sound.class);
        assetManager.load("sounds/missile_explode.mp3", Sound.class);
        assetManager.load("sounds/mystery_box.mp3", Sound.class);
        assetManager.load("sounds/special_item.mp3", Sound.class);
        assetManager.load("sounds/throw_weapon.mp3", Sound.class);
        assetManager.load("sounds/weapon_hit.mp3", Sound.class);
        assetManager.load("sounds/hit_ground.mp3", Sound.class);

        assetManager.load("musics/music1.ogg", Music.class);
        assetManager.load("musics/music2.ogg", Music.class);
        assetManager.load("musics/music3.ogg", Music.class);
        assetManager.load("musics/music4.ogg", Music.class);

        loadAssets = true;
        //see render() method to run the asset loading

    }

    //called once all assets are loaded
    private void onAssetsLoaded() {
        if(SuperCat.data.isMuted()) {
            SuperCat.media.setMute(true);
        } else {
            SuperCat.media.setMute(false);
        }

        callback.sendMessage(READY);

        if(SuperCat.tester) {
            //showLevel(-1);
        } else {
            //showLevel(1);
            showIntro();
        }
    }

    //create and show main game scren
    private void showIntro() {
        intro = new Intro();
        intro.setCallback(new StageGame.Callback() {
            @Override
            public void call(int code, int value) {
                //back from intro
                if (code == Intro.ON_BACK) {
                    callback.sendMessage(EXIT_APP);
                }

                //play btn clicked, hide intro and show level list
                else if (code == Intro.ON_PLAY) {
                    hideIntro();
                    showLevelList();
                }

                else if (code == Intro.ON_REMOVE_ADS) {
                    callback.sendMessage(REMOVE_ADS);
                }
            }

        });
        setScreen(intro);
        trackPage("intro");

        SuperCat.media.playMusic("music1.ogg");
    }
    private void hideIntro() {
        intro.dispose();
        intro = null;
    }

    //create and show level select screen
    private void showLevelList() {
    	
    	
        levelList = new LevelList();
        levelList.setCallback(new StageGame.Callback() {
            @Override
            public void call(int code, int value) {

                //back, hide this screen and show intro
                if (code == LevelList.ON_BACK) {
                    hideLevelList();
                    showIntro();
                } else if (code == LevelList.ON_SELECT) {
                    int levelId = levelList.getSelectedId();
                    hideLevelList();
                    showLevel(levelId);
                } else if(code == LevelList.ON_RATE_BUTTON) {
                    callback.sendMessage(OPEN_MARKET);
                }
            }


        });
        setScreen(levelList);
        trackPage("level_list");

        SuperCat.media.playMusic("music1.ogg");
    }
    private void hideLevelList() {
        levelList.dispose();
        levelList=null;

    }

    private int lastLevelId;

    private void showLevel(int id) {
        showLevel(id,0);
    }
    private void showLevel(int id,float delay) {
        lastLevelId = id;

        //the default requirement of stars
        float minCoins = 0.75f;
        float minEnemies = 0.75f;
        float bonusTime = 120;

        //modify based on level id
        if(id <=4) {
            bonusTime = 120;
        }
        else {
            bonusTime = 150;
            minCoins = 0.85f;
            minEnemies = 0.85f;
        }

        //create the level, pass the id
        level = new Level(id);
        if(delay != 0) level.setDelay(delay);

        //set the requirement
        level.setScoreRule(minCoins,minEnemies,bonusTime);

        //monitor the state of Level
        level.setCallback(new StageGame.Callback() {
            @Override
            public void call(int code, int value) {
                //start sub level
                if (code == Level.START_SUB_LEVEL) {
                    SuperCat.media.playSound("dumdum.mp3");
                    showSubLevel(level.getSubLevel());
                }
                //quit level, fail or success
                else if (code == Level.QUIT) {
                	if(SuperCat.tester) {
                		Gdx.app.exit();
                		return;
                	}

                    hideLevel();
                    showLevelList();

                    //write the data
                    SuperCat.data.flush();

                    //notify to show ads
                    callback.sendMessage(SHOW_ADS);
                }
                //retry after failed
                else if(code == Level.RETRY) {
                    //show ads first
                    callback.sendMessage(SHOW_ADS);
                    hideLevel();
                    showLevel(lastLevelId,0.5f);
                }

                //next level
                else if(code == Level.NEXT_LEVEL) {
                	if(SuperCat.tester) {
                		Gdx.app.exit();
                		return;
                	}

                    //write data
                    SuperCat.data.flush();

                    //show ads first
                    callback.sendMessage(SHOW_ADS);

                    hideLevel();

                    //if at the last level of worlds, back to level list
                    if(lastLevelId % (Settings.COLS*Settings.ROWS) == 0) {
                        showLevelList();
                    } else {
                        showLevel(lastLevelId+1,0.5f);
                    }
                }
            }


        });

        //show to screen
        setScreen(level);

        //tracking
        trackPage("level_"+id);

        //stop the intro music
        SuperCat.media.stopMusic("music1.ogg");
    }
    private void hideLevel() {
        level.dispose();
        level = null;
    }


    private void showSubLevel(SubLevel sub) {
        //identical to showLevel() above

        subLevel = sub;
        setScreen(subLevel);
        subLevel.setCallback(new StageGame.Callback() {
            @Override
            public void call(int code, int value) {
                if(code == subLevel.EXIT) {
                    level.setLevelData(subLevel.getLevelData());
                    level.backFromSubLevel();
                    setScreen(level);
                    SuperCat.media.playSound("dumdum.mp3");
                }
                else if(code == Level.RETRY) {
                    callback.sendMessage(SHOW_ADS);
                    hideLevel();
                    showLevel(lastLevelId);
                }
                else if(code == Level.QUIT) {
                    hideLevel();
                    showLevelList();
                }
            }

        });
    }

    //write data on every app pause
    @Override
    public void pause() {
        super.pause();
        if(!SuperCat.tester) {
            data.flush();
        }
    }



    @Override
    public void render() {
        super.render();

        if(loadAssets) {
            if(assetManager.update()) {  //assets has completed
                loadAssets=false;
                atlas = assetManager.get("images/pack.atlas", TextureAtlas.class);

                //create fonts
                createFonts();

                //create the media to handle sound & music
                media = new Media(assetManager,"sounds","musics");

                //notify
                onAssetsLoaded();
            }
        } else {
            media.update(Gdx.graphics.getDeltaTime());
        }

        if(profileGL) {
            Gdx.app.log(TAG, "----------------");
            Gdx.app.log(TAG, "draw calls: " + GLProfiler.drawCalls);
            Gdx.app.log(TAG, "texture bindings: " + GLProfiler.textureBindings);
            GLProfiler.reset();
        }

        if(logFPS) {
            fpsLogger.log();
        }
    }

    private void createFonts() {
        //create the font generetor
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/gooddog.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        //filtering
        parameter.genMipMaps = true;
        parameter.minFilter = Texture.TextureFilter.MipMapLinearNearest;
        parameter.magFilter = Texture.TextureFilter.Linear;

        //create various fonts

        parameter.size = 32;
        parameter.borderColor = new Color(0x000000ff);
        parameter.borderWidth = 1.5f;
        font32 = generator.generateFont(parameter);

        parameter.size = 64;
        parameter.borderColor = new Color(0x000000ff);
        parameter.borderWidth = 1.5f;
        font64 = generator.generateFont(parameter);

        parameter.size = 144;
        parameter.borderWidth = 0;
        parameter.characters = "0123456789";
        font144 = generator.generateFont(parameter);

        //finished
        generator.dispose();
    }

    //shortcut methods to create image and button
    public static Image createImage(String regionName) {
        return new Image(getRegion(regionName));
    }
    public static TextureRegion getRegion(String regionName) {
        return atlas.findRegion(regionName);
    }
    public static NButton createButton(String regionName) {
        return new NButton(getRegion(regionName));
    }


    public void setConfig(boolean useIAP) {
        Intro.useIAP = useIAP;
    }
}
