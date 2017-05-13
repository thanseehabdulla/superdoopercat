package com.boontaran.android.ads;

import android.app.Activity;
import com.badlogic.gdx.Gdx;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Model.CBError.CBImpressionError;

import java.util.ArrayList;
import java.util.List;

public class ChartboostAds extends FullAds {
	private static final String TAG = "ChartboostAds";
    private String location;
	private boolean hasStarted = false;
    private boolean loadBeforeStart = false;

	private static Delegate delegate;

	public ChartboostAds(Activity activity, String id, String signature,String location) {
		super(activity);
		setLocation(location);

		Chartboost.startWithAppId(activity, id, signature);

        if(delegate == null) {
            delegate = new Delegate();
            Chartboost.setDelegate(delegate);
        }
	    Chartboost.onCreate(activity);
	}
    public void setLocation(String location) {
        this.location = location;
    }
    public String getLocation() {
        return location;
    }
    public boolean isAutoLoad() {
        return autoLoad;
    }
    @Override
    public void setListener(Listener listener) {
        super.setListener(listener);
        delegate.addListener(this);
    }
    public Listener getListener() {
        return listener;
    }

	public boolean isLoaded() {
		return Chartboost.hasInterstitial(location);
	};
	
	@Override
	public void load() {
		Gdx.app.log(TAG, location+" load");
		if(!hasStarted) {
			Gdx.app.log(TAG, location+" not yet started");
            loadBeforeStart = true;
            onStart();

			return;
		}
		
		if(!Chartboost.hasInterstitial(location)) {
			Chartboost.cacheInterstitial(location);
		}
	}

	@Override
	public void show() {
		Gdx.app.log(TAG, location+" show..");
		if(Chartboost.hasInterstitial(location)) {
			Chartboost.showInterstitial(location);
		} else {
			Gdx.app.log(TAG, location+" .. not ready");
		}
	}



	@Override
	public void onStart() {
		Chartboost.onStart(activity);
		hasStarted=true;

        if(loadBeforeStart) {
            loadBeforeStart = false;
            load();
        }
		else if(autoLoad) {
			load();
		}


	}

	@Override
	public void onResume() {
		Chartboost.onResume(activity);
	}

	@Override
	public void onPause() {
		Chartboost.onPause(activity);
	}

	@Override
	public void onStop() {
		Chartboost.onStop(activity);
	}

	@Override
	public void onDestroy() {
        delegate = null;
		Chartboost.onDestroy(activity);
	}

	@Override
	public boolean onBackPressed() {
		return Chartboost.onBackPressed();
	}
	
	


    private static class Delegate extends ChartboostDelegate {
        private List<ChartboostAds> adsList = new ArrayList<ChartboostAds>();

        public void addListener(ChartboostAds ads) {
            adsList.add(ads);
        }
        private ChartboostAds getAds(String location) {
            int i;
            int num = adsList.size();

            ChartboostAds ads = null;
            for(i=0;i<num;i++) {
                ads = adsList.get(i);
                if(ads.getLocation().equals(location)) {
                    break;
                }
            }

            return ads;
        }

        @Override
        public void didCacheInterstitial(String location) {
            ChartboostAds ads = getAds(location);
            if(ads != null) {
                Gdx.app.log(TAG, location+" cached");
                if(ads.getListener() != null) {
                    ads.getListener().onCached();
                }
            }
        }

        @Override
        public void didCloseInterstitial(String location) {
            ChartboostAds ads = getAds(location);
            if(ads != null) {
                Gdx.app.log(TAG, location+" close");
                if(ads.getListener() != null) {
                    ads.getListener().onClosed();
                }
                if(ads.isAutoLoad()) {
                    ads.load();
                }
            }
        }

        @Override
        public void didDismissInterstitial(String location) {
            ChartboostAds ads = getAds(location);
            if(ads != null) {
                Gdx.app.log(TAG, location+" dissmis");
                if(ads.getListener() != null) {
                    ads.getListener().onClosed();
                }
                if(ads.isAutoLoad()) {
                    ads.load();
                }
            }
        }

        @Override
        public void didFailToLoadInterstitial(String location,CBImpressionError error) {
            ChartboostAds ads = getAds(location);
            if(ads != null) {
                Gdx.app.log(TAG, location+" failed "+error.toString());
                if(ads.getListener() != null) {
                    ads.getListener().onFailed();
                }
            }
        }
    }
}
