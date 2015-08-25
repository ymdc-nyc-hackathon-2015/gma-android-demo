package nyc2015.ydmc.com.gma.constants;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by softvelopment on 8/25/15.
 */
public class SharedPreferenceConstants {
    public static final String SHARED_PREF_FILENAME="gma-shared-pref";
    public static final String SAVED_ADS_LIST = "saved_ads_list";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void flushSharedPreferences(SharedPreferences prefs, Object... data)
    {
        if(data.length >0 ) {
            Set<String> set = new HashSet<String>();
             set.addAll(loadSavedAdsFromSharedPreferences( prefs));
            SharedPreferences.Editor editor = prefs.edit();
            for(int i=0; i<data.length;i++)
            {
                if(data[i] instanceof List)
                {
                    List castData= (List<String>)data[i];
                    set.addAll(castData);
                    editor.putStringSet(SAVED_ADS_LIST, set);
                }
            }
            editor.commit();
        }

    }
    public static List loadSavedAdsFromSharedPreferences(SharedPreferences prefs)
    {
        List<String> resultList = new ArrayList<>();
        Set<String> set = prefs.getStringSet(SAVED_ADS_LIST, null);
        if(set != null)
        {
         resultList.addAll(set);
        }
        return resultList;
    }
}
