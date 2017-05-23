package boshuai.net.ntools.image.photoview.scrollerproxy;

/**
 * Created by Administrator on 2017/1/14.
 */


import android.annotation.TargetApi;
import android.content.Context;

@TargetApi(14)
public class IcsScroller extends GingerScroller {

    public IcsScroller(Context context) {
        super(context);
    }

    @Override
    public boolean computeScrollOffset() {
        return mScroller.computeScrollOffset();
    }

}
