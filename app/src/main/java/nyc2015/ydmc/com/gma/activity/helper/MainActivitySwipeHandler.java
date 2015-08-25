package nyc2015.ydmc.com.gma.activity.helper;

import android.content.Context;
import android.widget.Toast;

import java.util.ConcurrentModificationException;

import nyc2015.ydmc.com.gma.input.gesture.SwipeGestureHandler;

/**
 * Created by softvelopment on 8/25/15.
 */
public class MainActivitySwipeHandler implements SwipeGestureHandler {
    private Context context;

    public MainActivitySwipeHandler(Context context)
    {
        this.context = context;
    }

    @Override
    public void swipedRight() {
        Toast.makeText(context, "swipped right", Toast.LENGTH_LONG);
    }

    @Override
    public void swipedDown() {

        Toast.makeText(context, "swipped down", Toast.LENGTH_LONG);
    }

    @Override
    public void swipedUp() {

        Toast.makeText(context, "swipped up", Toast.LENGTH_LONG);
    }

    @Override
    public void swipedLeft() {

        Toast.makeText(context, "swipped left", Toast.LENGTH_LONG);
    }
}
