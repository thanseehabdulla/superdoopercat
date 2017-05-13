package com.boontaran.android.iap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import com.badlogic.gdx.Gdx;
import com.boontaran.android.iap.util.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arifbs on 9/6/15.
 */
public class IapManager {
    private Activity activity;
    private static final String TAG = "IapManager";
    private IabHelper mHelper;
    private List<String> skuList;
    private ProgressDialog consumeDialog;
    private String base64EncodedPublicKey;
    private Listener listener;

    public IapManager(Activity activity,String[] skus,String base64EncodedPublicKey,Listener listener) {
        this.activity = activity;
        this.listener = listener;
        this.base64EncodedPublicKey = base64EncodedPublicKey;
        skuList = new ArrayList<String>();

        init(skus);
    }
    private void init(String[] skus) {
        for(int i=0;i<skus.length;i++) {
            skuList.add(skus[i]);
        }
        mHelper = new IabHelper(activity, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Gdx.app.log(TAG, "Problem setting up In-app Billing: " + result);
                } else {
                    Gdx.app.log(TAG, " setting up In-app Billing: success ");
                    queryItems();
                }
            }
        });
    }
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return mHelper.handleActivityResult(requestCode, resultCode, data);
    }
    public void destroy() {
        mHelper.dispose();
    }


    private void queryItems() {
        Gdx.app.log(TAG, " queryItems : ");
        for(String sku : skuList) {
            Gdx.app.log(TAG, " SKU : "+sku);
        }
        mHelper.queryInventoryAsync(true, skuList, mQueryFinishedListener);
    }
    private IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {

        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Gdx.app.log(TAG, "mQueryFinishedListener");

            if (result.isFailure()) {
                Gdx.app.log(TAG, "mQueryFinishedListener ERROR");
                listener.onQueryFailed(result.getMessage());
                return;
            }

            int numSku = inventory.getSkus().size();
            Gdx.app.log(TAG, "num sku = "+numSku);

            SkuDetails[] details=null;
            if(numSku > 0) {
                details = new SkuDetails[inventory.getSkus().size()];
                int i=0;
                for(SkuDetails d : inventory.getSkus().values()) {
                    details[i] = d;
                    i++;
                }
            } else {
                details = new SkuDetails[0];
            }


            listener.onQuerySuccess(inventory, details);
        }

    };

    public void purchase(String sku) {
        mHelper.launchPurchaseFlow(activity, sku, 10001, mPurchaseFinishedListener, "bGoa+V7g/yqDXvwwwwwwwbPiQJo4pf9RzJ");
    }
    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                listener.onPurchaseFailed(purchase,result.getMessage());
                Gdx.app.log(TAG, "Error purchasing: " + result);
                return;
            }
            listener.onPurchaseSuccess(purchase);
        }
    };

    public void consume(Purchase purchase) {
        Gdx.app.log(TAG, "consume..."+purchase.getSku());
        consumeDialog = ProgressDialog.show(activity, "","Please wait...", true);
        mHelper.consumeAsync(purchase,mConsumeFinishedListener);
    }
    private IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (result.isSuccess()) {
                listener.onConsumeSuccess(purchase);
            } else {
                Gdx.app.log(TAG,"consume error : " + result.getMessage());
                listener.onConsumeFailed(purchase,result.getMessage());
            }
            consumeDialog.dismiss();
        }
    };


    public static interface Listener {
        void onQueryFailed(String message);
        void onQuerySuccess(Inventory inventory, SkuDetails[] skuDetails);
        void onPurchaseSuccess(Purchase purchase);
        void onPurchaseFailed(Purchase purchase, String message);
        void onConsumeSuccess(Purchase purchase);
        void onConsumeFailed(Purchase purchase, String message);
    }
}
