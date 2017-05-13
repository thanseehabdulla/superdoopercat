package com.boontaran.android.ads;

import android.app.Activity;
import com.badlogic.gdx.Gdx;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.purplebrain.adbuddiz.sdk.AdBuddizDelegate;
import com.purplebrain.adbuddiz.sdk.AdBuddizError;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by arifbs on 3/30/16.
 */
public class AdBuddizAds extends FullAds {
    private static final String TAG = "AdBuddizAds";
    //private static Delegate delegate;
    private int adId;
    private static int sid;

    public AdBuddizAds(Activity activity, String id,boolean testMode) {
        super(activity);
        sid++;
        adId = sid;

        AdBuddiz.setPublisherKey(id);
        AdBuddiz.setDelegate(null);

        if(testMode) {
            AdBuddiz.setTestModeActive();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AdBuddiz.onDestroy();
    }

    @Override
    public void setListener(final Listener listener) {
        super.setListener(listener);

        AdBuddiz.setDelegate(new AdBuddizDelegate() {
            @Override
            public void didCacheAd() {
                listener.onCached();
            }

            @Override
            public void didShowAd() {

            }

            @Override
            public void didFailToShowAd(AdBuddizError adBuddizError) {
                listener.onFailed();
            }

            @Override
            public void didClick() {

            }

            @Override
            public void didHideAd() {
                listener.onClosed();
            }
        });
    }

    @Override
    public void load() {
        Gdx.app.log(TAG , "load");
        AdBuddiz.cacheAds(activity);
    }

    @Override
    public void show() {
        Gdx.app.log(TAG , "show");
        AdBuddiz.showAd(activity);
    }

    @Override
    public boolean isLoaded() {
        if(!super.isLoaded()) {
            return false;
        }
        return AdBuddiz.isReadyToShowAd(activity);
    }

    //

    /*
    private class Delegate implements AdBuddizDelegate {
        private Map<Integer, AdBuddizAds> adList = new HashMap<Integer, AdBuddizAds>();

        public void setListener(AdBuddizAds ads) {
            adList.put(ads.adId, ads);
        }
        public AdBuddizAds getAds(int id) {
            return adList.get(id);
        }
        @Override
        public void didCacheAd() {
            AdBuddizAds ads = getAds();
        }

        @Override
        public void didShowAd() {

        }

        @Override
        public void didFailToShowAd(AdBuddizError adBuddizError) {

        }

        @Override
        public void didClick() {

        }

        @Override
        public void didHideAd() {

        }
    };

    */
}
