package nyc2015.ydmc.com.gma.activity;

import nyc2015.ydmc.com.gma.activity.helper.AdViewHolder;
import nyc2015.ydmc.com.gma.activity.helper.MainActivitySwipeHandler;
import nyc2015.ydmc.com.gma.ads.NativeAdFetcher;
import nyc2015.ydmc.com.gma.constants.FlurryConstants;
import nyc2015.ydmc.com.gma.input.gesture.OnSwipeTouchListener;
import nyc2015.ydmc.com.gma.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.flurry.android.ads.FlurryAdInterstitialListener;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeAsset;
import com.flurry.android.ads.FlurryAdNativeListener;

import java.util.List;

import nyc2015.ydmc.com.gma.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity {

    NativeAdFetcher nativeAdFetcher = new NativeAdFetcher();

    private FlurryAdNative mFlurryAdNative = null;

    private static final String kLogTag= MainActivity.class.getName();

    View adLayout;

    AdViewHolder adViewHolder = new AdViewHolder();

    private OnSwipeTouchListener swipeGestureListener;

    private MainActivitySwipeHandler swipeHandler;


    public static final String AD_ASSET_HEADLINE = "headline";
    public static final String AD_ASSET_SUMMARY = "summary";
    public static final String AD_ASSET_SOURCE = "source";
    public static final String AD_ASSET_SEC_HQ_BRANDING_LOGO = "secHqBrandingLogo";
    public static final String AD_ASSET_SEC_HQ_IMAGE = "secHqImage";

    FlurryAdNativeListener nativeListener = new FlurryAdNativeListener() {

        //This method will be called when the ad has been received from the server
        @Override
        public void onFetched(FlurryAdNative adNative) {
           parseAssets(adNative);
            swipeHandler =new MainActivitySwipeHandler(MainActivity.this.getApplicationContext());
            swipeGestureListener = new OnSwipeTouchListener(MainActivity.this, swipeHandler);
            if(adViewHolder.getAdImage() != null)
            {
                adViewHolder.getAdImage().setOnTouchListener(swipeGestureListener);
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
            Log.i(kLogTag, "onCloseFullscreen " );
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
            Log.i(kLogTag, "onAppExit ");}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

       // final View controlsView = findViewById(R.id.fullscreen_content_controls);
         adLayout = (LinearLayout)findViewById(R.id.adContainerLayout);

        //if your application suports the device rotation, use the application context
        //when creating the native ad objects to prevent the object from
        //being destroyed with the Activity

        mFlurryAdNative = new FlurryAdNative(this, FlurryConstants.FLURRY_STREAM_AD_CAMPAIGN);
        // allow us to get callbacks for ad events
        mFlurryAdNative.setListener(nativeListener);
        //required to support ad tracking
        mFlurryAdNative.setTrackingView(adLayout);
        //nativeAdFetcher.prefetchAds(adLayout.getContext());

        adViewHolder.setAdImage((ImageView)findViewById(R.id.mainImage));
       // adViewHolder.setAdSummary((TextView) findViewById(R.id.mainText));
        adViewHolder.setAdTitle((TextView)findViewById(R.id.newsTitle));
        adViewHolder.setAdSummary((TextView)findViewById(R.id.newsSummary));

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

    public void parseAssets(FlurryAdNative adNative)
    {
      loadAdInView(adViewHolder,adNative);

    }

    public void loadAdInView(AdViewHolder viewHolder, FlurryAdNative adNative) {
        try {

            FlurryAdNativeAsset adHeadlineNativeAsset = adNative.getAsset(AD_ASSET_HEADLINE);
            FlurryAdNativeAsset adSummaryNativeAsset = adNative.getAsset(AD_ASSET_SUMMARY);
            FlurryAdNativeAsset adPublhiserNativeAsset = adNative.getAsset(AD_ASSET_SOURCE);
            FlurryAdNativeAsset adBrandingImageNativeAsset = adNative.getAsset(AD_ASSET_SEC_HQ_BRANDING_LOGO);
            FlurryAdNativeAsset adImageNativeAsset = adNative.getAsset(AD_ASSET_SEC_HQ_IMAGE);

            if (adHeadlineNativeAsset != null) {
                adHeadlineNativeAsset.loadAssetIntoView(viewHolder.getAdTitle());
            } else {
                viewHolder.getAdTitle().setVisibility(View.GONE);
            }
            if (adSummaryNativeAsset != null) {
                adSummaryNativeAsset.loadAssetIntoView(viewHolder.getAdSummary());
            } else {
                viewHolder.getAdSummary().setVisibility(View.GONE);
            }

            if (adPublhiserNativeAsset != null) {
                adPublhiserNativeAsset.loadAssetIntoView(viewHolder.getPublisher());
            } else {
                viewHolder.getPublisher().setVisibility(View.GONE);
            }

            if (adBrandingImageNativeAsset != null) {
                adBrandingImageNativeAsset.loadAssetIntoView(viewHolder.getSponsoredImage());
            } else
            {
                viewHolder.getSponsoredImage().setVisibility(View.GONE);
            }


            if (adImageNativeAsset != null) {
                adImageNativeAsset.loadAssetIntoView(viewHolder.getAdImage());
            } else
            {
                viewHolder.getAdImage().setVisibility(View.GONE);
            }


        } catch (Exception e) {
            Log.i(MainActivity.class.getName(), "Exception in fetching an Ad", e);

        }
    }
}

