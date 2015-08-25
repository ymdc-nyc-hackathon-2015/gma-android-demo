package nyc2015.ydmc.com.gma.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by softvelopment on 8/25/15.
 */
public class ApplicationData {
    private static ApplicationData ourInstance = new ApplicationData();

    public synchronized static ApplicationData getInstance() {
        return ourInstance;
    }

    private List<String> disguardedAds = new ArrayList<>();
    private List<String> savedAds = new ArrayList<>();

    private ApplicationData() {
    }

    public List<String> getDisguardedAds() {
        return disguardedAds;
    }

    public void setDisguardedAds(List<String> disguardedAds) {
        this.disguardedAds = disguardedAds;
    }

    public List<String> getSavedAds() {
        return savedAds;
    }

    public void setSavedAds(List<String> savedAds) {
        this.savedAds = savedAds;
    }
}
