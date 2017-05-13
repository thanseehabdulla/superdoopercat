package com.boontaran.android.ads;

import android.app.Activity;
import com.badlogic.gdx.Gdx;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AdmobAds extends FullAds {
	protected static final String TAG = "AdmobAds";
	private InterstitialAd mInterstitialAd;
    private String testDevice = null;

    public AdmobAds(Activity activity, String id) {
        this(activity,id,null);
    }
	public AdmobAds(Activity activity, String id, String testDevice) {
		super(activity);

        this.testDevice = testDevice;
		mInterstitialAd = new InterstitialAd(activity);
        mInterstitialAd.setAdUnitId(id);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Gdx.app.log(TAG, "onAdClosed ");
                if(listener != null) {
                    listener.onClosed();
                }
                if(autoLoad) {
                    load();
                }
            }

			@Override
			public void onAdFailedToLoad(int errorCode) {
				Gdx.app.log(TAG, "onAdFailedToLoad "+errorCode);
                if(listener !=null) {
                    listener.onFailed();
                }
			}
			@Override
			public void onAdLoaded() {
				Gdx.app.log(TAG, "onAdLoaded ");
			}

        });


        
	}

    @Override
    public boolean isLoaded() {
        if(!enabled) return false;
        return mInterstitialAd.isLoaded();
    }

    @Override
	public void load() {
        if(!enabled) return;
        Gdx.app.log(TAG, "load ");


		AdRequest adRequest;
        if(testDevice !=null) {
            adRequest = new AdRequest.Builder()
                    .addTestDevice(testDevice)
                    .build();
        } else {
            adRequest = new AdRequest.Builder().build();
        }
        mInterstitialAd.loadAd(adRequest);


	}



	@Override
	public void show() {
        if(!enabled) return;
		Gdx.app.log(TAG, "show.. ");

		if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
        	Gdx.app.log(TAG, ".. not yet ready ");
        }
	}

}
