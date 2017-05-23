package boshuai.net.ntools.image.photoview.gestures;

/**
 * Created by Administrator on 2017/1/14.
 */

import android.view.MotionEvent;

public interface GestureDetector {

    public boolean onTouchEvent(MotionEvent ev);

    public boolean isScaling();

    public void setOnGestureListener(OnGestureListener listener);

}
