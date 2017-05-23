package boshuai.net.ntools.image.photoview.gestures;

/**
 * Created by Administrator on 2017/1/14.
 */

public interface OnGestureListener {

    public void onDrag(float dx, float dy);

    public void onFling(float startX, float startY, float velocityX,
                        float velocityY);

    public void onScale(float scaleFactor, float focusX, float focusY);

}