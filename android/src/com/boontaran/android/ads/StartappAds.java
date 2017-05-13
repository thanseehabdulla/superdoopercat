package com.boontaran.android.ads;

import android.app.Activity;
import com.badlogic.gdx.Gdx;
import com.startapp.android.publish.*;

/**
 * Created by arifbs on 12/1/15.
 */
public class StartappAds extends FullAds {
    private static final String TAG = "StartappAds";
    private StartAppAd startAppAd;

    public StartappAds(Activity activity,String appId) {
        super(activity);

        //init sdk
        StartAppSDK.init(activity, appId, true);
        startAppAd = new StartAppAd(activity);

    }

    @Override
    public boolean isLoaded() {
        return startAppAd.isReady();
    }

    @Override
    public void load() {
        Gdx.app.log(TAG , "load ..");
        if(!startAppAd.isNetworkAvailable()) {
            Gdx.app.log(TAG, "..no network");
            if(listener !=null) {
                listener.onFailed();
            }
            return;
        }
        startAppAd.loadAd(loadListener);
    }
    private AdEventListener loadListener = new AdEventListener() {
        @Override
        public void onReceiveAd(Ad ad) {
            Gdx.app.log(TAG, "onReceiveAd "+ad.toString());
            if(listener !=null) {
                listener.onCached();
            }
        }
        @Override
        public void onFailedToReceiveAd(Ad ad) {
            Gdx.app.log(TAG, "onFailedToReceiveAd "+ad.toString());
            if(listener !=null) {
                listener.onFailed();
            }
        }
    };

    @Override
    public void show() {
        if(startAppAd.isReady()) {
            startAppAd.showAd(displayListener);
        }
    }
    private AdDisplayListener displayListener = new AdDisplayListener() {
        @Override
        public void adHidden(Ad ad) {
            Gdx.app.log(TAG, "adHidden");
            if(listener !=null) {
                listener.onClosed();
            }
            if(autoLoad) {
                load();
            }
        }
        @Override
        public void adDisplayed(Ad ad) {
            Gdx.app.log(TAG, "adDisplayed");
        }
        @Override
        public void adClicked(Ad ad) {
            Gdx.app.log(TAG, "adClicked");

            if(autoLoad) {
                load();
            }
        }
        @Override
        public void adNotDisplayed(Ad ad) {
            Gdx.app.log(TAG, "adNotDisplayed");
            if(listener !=null) {
                listener.onFailed();
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        startAppAd.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        startAppAd.onResume();
    }

}
