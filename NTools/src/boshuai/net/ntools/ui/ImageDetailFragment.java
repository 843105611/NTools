package boshuai.net.ntools.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import boshuai.net.ntools.R;
import boshuai.net.ntools.image.photoview.PhotoView;
import boshuai.net.ntools.image.photoview.PhotoViewAttacher;
import boshuai.net.ntools.unit.Consts;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Created by Administrator on 2017/1/14.
 */


public class ImageDetailFragment extends Fragment
{
    private String mImageUrl;
    private PhotoView mImageView;
    private PhotoViewAttacher mAttacher;
    private static Bitmap mBitmap;
    private static Drawable mDrawable;

    static boolean mIsFullScreen = false;


    public static ImageDetailFragment newInstance(String[] imageUrls, int position)
    {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        if(mBitmap!=null && mBitmap.isRecycled()==false)
        {
            mBitmap.recycle();;
            mBitmap = null;
        }
        args.putString("url", imageUrls[position]);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(mDrawable !=null)
        {
            mImageView.setImageDrawable(null);
            mDrawable = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
        Log.v("BOSHUAI", "mImagUrl:" + mImageUrl);
        Consts.CURRENT_IMAGE = mImageUrl.substring(7, mImageUrl.length());
        Log.v("BOSHUAI", "filePath:" + Consts.CURRENT_IMAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (PhotoView) v.findViewById(R.id.image);
        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener()
        {
            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2)
            {
                //TODO 按图片显示页面的边框是否退出当前Activity


               if(Consts.IS_EXIT_WHEN_TOUCH_EDGE)
               {
                   mIsFullScreen = !mIsFullScreen;
                   if(mIsFullScreen) {
                       // getActivity().finish();
                       WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                       params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                       getActivity().getWindow().setAttributes(params);
                       getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                   }
                   else
                   {
                       WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                       params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                       getActivity().getWindow().setAttributes(params);
                       getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                   }
               }
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);


        Glide.with(getActivity().getApplicationContext()).load(mImageUrl).fitCenter().into(new SimpleTarget<GlideDrawable>(Consts.SCREEN_WIDTH, Consts.SCREEN_HEIGHT)
        {
            @Override
            public void onResourceReady(GlideDrawable glideDrawable, GlideAnimation<? super GlideDrawable> glideAnimation)
            {
                mDrawable = glideDrawable;
                mImageView.setImageDrawable(glideDrawable);
            }
         });
    }


}