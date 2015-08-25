package nyc2015.ydmc.com.gma.application;

import android.app.Application;

import com.flurry.android.FlurryAgent;

import nyc2015.ydmc.com.gma.constants.FlurryConstants;

/**
 * Created by softvelopment on 8/25/15.
 */
public class FlurryfiedApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // configure Flurry
        FlurryAgent.setLogEnabled(false);

        // init Flurry
        FlurryAgent.init(this, FlurryConstants.FLURRY_APP_KEY);
    }
}
