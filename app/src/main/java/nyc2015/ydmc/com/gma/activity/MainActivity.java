package nyc2015.ydmc.com.gma.activity;

import nyc2015.ydmc.com.gma.activity.helper.AdViewHolder;
import nyc2015.ydmc.com.gma.activity.helper.MainActivitySwipeHandler;
import nyc2015.ydmc.com.gma.ads.NativeAdFetcher;
import nyc2015.ydmc.com.gma.constants.FlurryConstants;
import nyc2015.ydmc.com.gma.constants.SharedPreferenceConstants;
import nyc2015.ydmc.com.gma.data.ApplicationData;
import nyc2015.ydmc.com.gma.input.gesture.OnSwipeTouchListener;
import nyc2015.ydmc.com.gma.input.gesture.SwipeGestureHandler;
import nyc2015.ydmc.com.gma.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.flurry.android.ads.FlurryAdInterstitialListener;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeAsset;
import com.flurry.android.ads.FlurryAdNativeListener;
import com.google.android.gms.gcm.Task;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import nyc2015.ydmc.com.gma.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity implements SwipeGestureHandler {

    NativeAdFetcher nativeAdFetcher = new NativeAdFetcher();

    private FlurryAdNative mFlurryAdNative = null;

    private static final String kLogTag = MainActivity.class.getName();

    View adLayout;

    AdViewHolder adViewHolder = new AdViewHolder();

    private OnSwipeTouchListener swipeGestureListener;

    SharedPreferences sharedPreferences;

    private List<String> savedAds;

    public static final String AD_ASSET_HEADLINE = "headline";
    public static final String AD_ASSET_SUMMARY = "summary";
    public static final String AD_ASSET_SOURCE = "source";
    public static final String AD_ASSET_SEC_HQ_BRANDING_LOGO = "secHqBrandingLogo";
    public static final String AD_ASSET_SEC_HQ_IMAGE = "secHqImage";

    private Button button;

    FlurryAdNativeListener nativeListener = new FlurryAdNativeListener() {

        //This method will be called when the ad has been received from the server
        @Override
        public void onFetched(FlurryAdNative adNative) {
            parseAssets(adNative);
            if (swipeGestureListener == null) {
                swipeGestureListener = new OnSwipeTouchListener(MainActivity.this);
                if (adViewHolder.getAdImage() != null) {
                    adViewHolder.getAdImage().setOnTouchListener(swipeGestureListener);
                }
            }
        }

        //This method will be called when there is failure with fetch, render or click.
        @Override
        public void onError(FlurryAdNative adNative, FlurryAdErrorType adErrorType, int errorCode) {
            if (adErrorType.equals(FlurryAdErrorType.FETCH)) {
                Log.i(kLogTag, "onFetchFailed " + errorCode);
                // you can deploy internal logic to determine whether to  fetch another ad here or not.
                // do not fetch more than x times in a row
            }
        }

        @Override
        public void onShowFullscreen(FlurryAdNative adNative) {
            Log.i(kLogTag, "onShowFullscreen ");
        }

        //This method will be called when the user dismisses the current Ad
        @Override
        public void onCloseFullscreen(FlurryAdNative adNative) {
            Log.i(kLogTag, "onCloseFullscreen ");
        }

        //This method will be called when the user has clicked on the ad.
        @Override
        public void onClicked(FlurryAdNative adNative) {
            Log.i(kLogTag, "onClicked ");
        }

        @Override
        public void onImpressionLogged(FlurryAdNative flurryAdNative) {

        }

        //This method will be called when the user is leaving the application after following events associated with the current Ad .
        @Override
        public void onAppExit(FlurryAdNative adNative) {
            Log.i(kLogTag, "onAppExit ");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sharedPreferences = getApplicationContext().getSharedPreferences(SharedPreferenceConstants.SHARED_PREF_FILENAME, Activity.MODE_PRIVATE);

        //load saved ads
        savedAds = SharedPreferenceConstants.loadSavedAdsFromSharedPreferences(sharedPreferences);

        // final View controlsView = findViewById(R.id.fullscreen_content_controls);
        adLayout = (LinearLayout) findViewById(R.id.adContainerLayout);

        //if your application suports the device rotation, use the application context
        //when creating the native ad objects to prevent the object from
        //being destroyed with the Activity

        initFlurry();

        adViewHolder.setAdImage((ImageView) findViewById(R.id.mainImage));
        // adViewHolder.setAdSummary((TextView) findViewById(R.id.mainText));
        adViewHolder.setAdTitle((TextView) findViewById(R.id.newsTitle));
        adViewHolder.setAdSummary((TextView) findViewById(R.id.newsSummary));

    }


    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, FlurryConstants.FLURRY_APP_KEY);
        // fetch and prepare ad for this ad space. won’t render one yet
        mFlurryAdNative.fetchAd();
    }

    public void onStop() {
        FlurryAgent.onEndSession(this);
        //do NOT call mFlurryAdInterstitial.destroy() here.
        //it will destroy the object prematurely and prevent certain listener callbacks form fireing
        super.onStop();
    }

    public void onDestroy() {

        mFlurryAdNative.destroy();
        super.onDestroy();
    }

    public void parseAssets(FlurryAdNative adNative) {
        loadAdInView(adViewHolder, adNative);

    }

    public void loadAdInView(AdViewHolder viewHolder, FlurryAdNative adNative) {
        try {

            FlurryAdNativeAsset adHeadlineNativeAsset = adNative.getAsset(AD_ASSET_HEADLINE);
            FlurryAdNativeAsset adSummaryNativeAsset = adNative.getAsset(AD_ASSET_SUMMARY);
            FlurryAdNativeAsset adPublhiserNativeAsset = adNative.getAsset(AD_ASSET_SOURCE);
            FlurryAdNativeAsset adBrandingImageNativeAsset = adNative.getAsset(AD_ASSET_SEC_HQ_BRANDING_LOGO);
            FlurryAdNativeAsset adImageNativeAsset = adNative.getAsset(AD_ASSET_SEC_HQ_IMAGE);

            if (adHeadlineNativeAsset != null) {
                if (viewHolder.getAdTitle() != null) {
                    if (viewHolder.getAdTitle().getVisibility() == View.INVISIBLE) {
                        viewHolder.getAdTitle().setVisibility(View.VISIBLE);
                    }
                }
                adHeadlineNativeAsset.loadAssetIntoView(viewHolder.getAdTitle());
            } else {
                viewHolder.getAdTitle().setVisibility(View.INVISIBLE);
            }
            if (adSummaryNativeAsset != null) {
                if (viewHolder.getAdSummary() != null) {
                    if (viewHolder.getAdSummary().getVisibility() == View.INVISIBLE) {
                        viewHolder.getAdSummary().setVisibility(View.VISIBLE);
                    }
                }
                adSummaryNativeAsset.loadAssetIntoView(viewHolder.getAdSummary());
            } else {
                viewHolder.getAdSummary().setVisibility(View.INVISIBLE);
            }

            if (adPublhiserNativeAsset != null) {
                if (viewHolder.getPublisher() != null) {
                    if (viewHolder.getPublisher().getVisibility() == View.INVISIBLE) {
                        viewHolder.getPublisher().setVisibility(View.VISIBLE);

                    }
                }
                adPublhiserNativeAsset.loadAssetIntoView(viewHolder.getPublisher());
            } else {
                viewHolder.getPublisher().setVisibility(View.INVISIBLE);
            }
            if (adBrandingImageNativeAsset != null) {
                if (viewHolder.getSponsoredImage() != null) {
                    if (viewHolder.getSponsoredImage().getVisibility() == View.INVISIBLE) {
                        viewHolder.getSponsoredImage().setVisibility(View.VISIBLE);
                    }
                }
                adBrandingImageNativeAsset.loadAssetIntoView(viewHolder.getSponsoredImage());
            } else {
                viewHolder.getSponsoredImage().setVisibility(View.INVISIBLE);
            }


            if (adImageNativeAsset != null) {
                if (viewHolder.getAdImage() != null) {
                    if (viewHolder.getAdImage().getVisibility() == View.INVISIBLE) {
                        viewHolder.getAdImage().setVisibility(View.VISIBLE);
                    }
                }
                adImageNativeAsset.loadAssetIntoView(viewHolder.getAdImage());
            } else {
                viewHolder.getAdImage().setVisibility(View.INVISIBLE);
            }


        } catch (Exception e) {
            Log.i(MainActivity.class.getName(), "Exception in fetching an Ad", e);

        }
    }

    @Override
    public void swipedRight() {
        //adViewHolder.getAdSummary().performClick();//.performLongClick();//.performClick();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.save_overlay_title);
        builder.setTitle(R.string.save_overlay_title);
        this.savedAds = SharedPreferenceConstants.loadSavedAdsFromSharedPreferences(this.getSharedPreferences(SharedPreferenceConstants.SHARED_PREF_FILENAME, Activity.MODE_PRIVATE));
        if ((this.savedAds != null) && (this.savedAds.size() > 0)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String tmpMessage : this.savedAds) {
                stringBuilder.append(tmpMessage + "\n");
            }
            builder.setMessage(stringBuilder);
        } else {
            builder.setMessage("unable to get saved ads");
        }
        builder.setCancelable(true);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
        );
        builder.create();
        builder.show();
    }

    @Override
    public void swipedDown() {
        ApplicationData.getInstance().getDisguardedAds().add(this.adViewHolder.getAdTitle().getText().toString());
        Toast.makeText(getBaseContext(), "Ad Disguarded " + this.adViewHolder.getAdTitle().getText().toString(), Toast.LENGTH_LONG).show();
        this.handleAdNeedsToGo();

    }

    @Override
    public void swipedUp() {
        ApplicationData.getInstance().getSavedAds().add(this.adViewHolder.getAdTitle().getText().toString());
        ;
        Toast.makeText(getBaseContext(), "Ad Saved " + this.adViewHolder.getAdTitle().getText().toString(), Toast.LENGTH_LONG).show();
        SharedPreferenceConstants.flushSharedPreferences(sharedPreferences, ApplicationData.getInstance().getSavedAds());
        this.handleAdNeedsToGo();
    }

    @Override
    public void swipedLeft() {

        adViewHolder.hideAd();
        this.handleAdNeedsToGo();
    }


    private void handleAdNeedsToGo() {

        MainActivity.this.mFlurryAdNative.destroy();
        final Handler timerHandler = new Handler();
        Runnable timerRunnable = new Runnable() {

            @Override
            public void run() {
                initFlurry();
                MainActivity.this.mFlurryAdNative.fetchAd();

            }
        };
        timerHandler.postDelayed(timerRunnable, 2000);
    }


    private void initFlurry() {

        mFlurryAdNative = new FlurryAdNative(this, FlurryConstants.FLURRY_STREAM_AD_CAMPAIGN);
        // allow us to get callbacks for ad events
        mFlurryAdNative.setListener(nativeListener);
        //required to support ad tracking
        mFlurryAdNative.setTrackingView(adLayout);
    }

    public void addListenerOnButton() {

        button = (Button) findViewById(R.id.savedAdsButton);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.save_overlay_title);
                if ((MainActivity.this.savedAds != null) && (MainActivity.this.savedAds.size() > 0)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String tmpMessage : MainActivity.this.savedAds) {
                        stringBuilder.append(tmpMessage + "/n");
                    }
                    builder.setMessage(stringBuilder);
                } else {
                    builder.setMessage("unable to get saved ads");
                }
                builder.create();
            }

        });
    }

}

