package nyc2015.ydmc.com.gma.activity.helper;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.ads.FlurryAdNative;

/**
 * Created by softvelopment on 8/25/15.
 */
public class AdViewHolder {
    private ImageView adImage;
    private TextView adTitle;
    private TextView adSummary;
    private TextView publisher;
    private ImageView sponsoredImage;
    private FlurryAdNative adNative;

    public ImageView getAdImage() {
        return adImage;
    }

    public void setAdImage(ImageView adImage) {
        this.adImage = adImage;
    }

    public TextView getAdTitle() {
        return adTitle;
    }

    public void setAdTitle(TextView adTitle) {
        this.adTitle = adTitle;
    }

    public TextView getAdSummary() {
        return adSummary;
    }

    public void setAdSummary(TextView adSummary) {
        this.adSummary = adSummary;
    }

    public TextView getPublisher() {
        return publisher;
    }

    public void setPublisher(TextView publisher) {
        this.publisher = publisher;
    }

    public ImageView getSponsoredImage() {
        return sponsoredImage;
    }

    public void setSponsoredImage(ImageView sponsoredImage) {
        this.sponsoredImage = sponsoredImage;
    }

    public FlurryAdNative getAdNative() {
        return adNative;
    }

    public void setAdNative(FlurryAdNative adNative) {
        this.adNative = adNative;
    }

    public void hideAd()
    {
        if(adImage != null) {
            adImage.setVisibility(View.INVISIBLE);
        }
        if(adTitle != null) {
            adTitle.setVisibility(View.INVISIBLE);
        }
        if(adSummary != null) {
            adSummary.setVisibility(View.INVISIBLE);
        }
        if(publisher != null) {
            publisher.setVisibility(View.INVISIBLE);
        }
        if(sponsoredImage != null) {
            sponsoredImage.setVisibility(View.INVISIBLE);
        }
        if(adNative != null) {
            adNative.destroy();
        }
    }
}
