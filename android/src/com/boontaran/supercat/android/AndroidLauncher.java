package com.boontaran.supercat.android;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.boontaran.android.ads.*;
import com.boontaran.android.iap.IapManager;
import com.boontaran.android.iap.util.Inventory;
import com.boontaran.android.iap.util.Purchase;
import com.boontaran.android.iap.util.SkuDetails;
import com.boontaran.supercat.Callback;
import com.boontaran.supercat.SuperCat;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class AndroidLauncher extends AndroidApplication {
    //EDIT FROM HERE ...

    //SETTINGS ....
    //leave it blank or null if not using google analytics
    public static final String TRACKING_ID = "";

    public static final String MARKET_URL = "";

    //use IAP
    private boolean useIAP=false;
    private static final String SKU = "";
    private static final String ENCODED_PUBLIC_KEY = "";

    //find the method setupAds() below to set your ads..

    //STOP EDIT ...

    public static final String TAG = "AndroidLauncher";

    //google analytic
    private Tracker tracker;
    //intersitital ads
    private FullAds ads;
    //iap
    private IapManager iap;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        //not using these
		config.useAccelerometer=false;
        config.useCompass=false;


        SuperCat game = new SuperCat(new Callback() {
            @Override
            public void sendMessage(int message) {
                //message from game to android
                Gdx.app.log(TAG, "sendMessage : " + message);
                handler.sendEmptyMessage(message);
            }

            @Override
            public void trackEvent(String label) {
                Gdx.app.log(TAG, "trackEvent : " + label);

                //event send to google analytics
                if (tracker != null) {
                    tracker.send(new HitBuilders.EventBuilder().setCategory("game")
                            .setAction("game").setLabel(label).build());
                }
            }

            @Override
            public void trackPage(String name) {
                Gdx.app.log(TAG, "trackPage : " + name);

                //page tracking send to google analytics
                if(tracker != null) {
                    tracker.setScreenName(name);
                    tracker.send(new HitBuilders.AppViewBuilder().build());
                }
            }
        });

        //launch the game
        initialize(game, config);

        //define if using IAP or not
        game.setConfig(useIAP);

        //setup iap
        if(useIAP) {
            String[] skus = {SKU};
            iap = new IapManager(this, skus , ENCODED_PUBLIC_KEY, new IapManager.Listener() {
                @Override
                public void onQueryFailed(String message) {
                    Gdx.app.log(TAG, "IAP onQueryFailed : " + message);
                }

                @Override
                public void onQuerySuccess(Inventory inventory, SkuDetails[] skuDetails) {


                    Gdx.app.log(TAG, "IAP onQuerySuccess : "+skuDetails.length+" items");
                    for(SkuDetails detail : skuDetails) {
                        Gdx.app.log(TAG, "item : " + detail);
                    }

                    //user has purchased remove-ads
                    if (inventory.hasPurchase(SKU)) {
                        //update data
                        SuperCat.data.setRemoveAds();
                    }
                }

                @Override
                public void onPurchaseSuccess(Purchase purchase) {
                    Gdx.app.log(TAG, "IAP onPurchaseSuccess " + purchase.getSku());

                    //user purchase remove-ads, and it succe$$,
                    if (purchase.getSku().equals(SKU)) {
                        SuperCat.data.setRemoveAds(); //update data
                    }
                }

                @Override
                public void onPurchaseFailed(Purchase purchase, String message) {
                    Gdx.app.log(TAG, "IAP onPurchaseFailed " + message);
                }

                @Override
                public void onConsumeSuccess(Purchase purchase) {  }

                @Override
                public void onConsumeFailed(Purchase purchase, String message) {  }
            });


        }
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(iap != null) {
            iap.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupAds() {
        Gdx.app.log(TAG, "setup ads");

        // user has purchase remove ads, no need to setup
        if(SuperCat.data.isRemoveAds()) {
            Gdx.app.log(TAG, "user has 'removed ads'");
            return;
        }

        //enable ONE of this ads :
        //also enable the activity entry in manifest,xml
        //you can delete the unused jar in libs folder to reduce apk size

        //ads = new AdmobAds(this,"ADMOB_ID","TEST_DEVICE_ID");
        //ads = new ChartboostAds(this,CHARTBOOST_ID,CHARTBOOST_SIG, CBLocation.LOCATION_DEFAULT);
        //ads = new StartappAds(this,STARTAPP);
        //ads = new AdBuddizAds(this,ADBUDDIZ_ID,false);  --> activity, id, testmode



        //load the ads
        if(ads != null) ads.setAutoLoad(true);


        //tips: if you don't like my wrapper classes above, you can write the ads setup code here :p


    }


    private void showAds() {
        Gdx.app.log(TAG, "show ads ...");
        // user has purchase remove ads, no need to show
        if(SuperCat.data.isRemoveAds()) {
            Gdx.app.log(TAG, "user has 'removed ads'");
            return;
        }

        //showing the ads with checking first
        if(ads != null) {
            if(ads.isLoaded()) {
                ads.show();
            } else {
                Gdx.app.log(TAG, "not ready");
            }
        }

        //again...
        //tips: if you don't like my wrapper classes above, you can write the code to show ads here


    }


    //setup google analytics
    private void setupTracker() {
        if(TRACKING_ID.isEmpty() || TRACKING_ID==null) return;
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker(TRACKING_ID);
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
    }

    //exit app
    private void exitApp() {
        Gdx.app.exit();
    }



    //route app cycle to the ads
    @Override
     protected void onResume() {
        if(ads !=null) {
            ads.onResume();
        }
        super.onResume();
    }
    @Override
    protected void onPause() {
        if(ads !=null) {
            ads.onPause();
        }
        super.onPause();
    }
    @Override
    protected void onStop() {
        if(ads !=null) {
            ads.onStop();
        }
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        if(ads !=null) ads.onDestroy();
        if(iap != null) iap.destroy();

        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        if(ads !=null) {
            ads.onResume();
        }
        super.onBackPressed();
    }


    //handler to communicate ui thread <-> game thread

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int message = msg.what;

            //do something based on game's messages
            if(message == SuperCat.READY) {
                setupAds();
                setupTracker();
            }
            else if(message == SuperCat.EXIT_APP) {
                exitApp();
            }
            else if(message == SuperCat.SHOW_ADS) {
                showAds();
            }
            else if(message == SuperCat.OPEN_MARKET) {
                Gdx.net.openURI(MARKET_URL);
            }
            else if(message == SuperCat.REMOVE_ADS) {
                iap.purchase(SKU);
            }

        }
    };


}
