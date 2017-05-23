package boshuai.net.ntools.ui;

import java.util.ArrayList;
import java.util.Stack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import boshuai.net.ntools.R;
import boshuai.net.ntools.adapter.DirListAdapter;
import boshuai.net.ntools.media.MediaPlayActivity;
import boshuai.net.ntools.unit.Consts;
import boshuai.net.ntools.unit.FileUnit;
import boshuai.net.ntools.unit.Msg;
import boshuai.net.ntools.unit.ScrollPosInfo;
import boshuai.net.ntools.unit.UrlsInfo;

public class TabPhoneActivity extends Activity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener
{
    AbsListView mAbsListView;
    public static ArrayList<String> mDataList;
    TextView mTextViewCurrentDir;
    DirListAdapter myAdapter;
    static int mScrolledPosition;
    static Stack<ScrollPosInfo> mStackScrollPos;
    public static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mStackScrollPos = new Stack<ScrollPosInfo>();
        mDataList = new ArrayList<String>();
        changeDataList();
        mHandler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                if (msg.what == Msg.MSG_CHANGE_VIEW_TYPE)
                {
                    saveCurrentScrollPosition(Consts.CURRENT_SD_DIR);
                    changeDataList();
                }
                if (msg.what == Msg.MSG_CHANGE_MENU_STATE)
                {
                    saveCurrentScrollPosition(Consts.CURRENT_SD_DIR);
                    changeDataList();
                }
                if (msg.what == Msg.MSG_SORT_CHANGE)
                {
                    saveCurrentScrollPosition(Consts.CURRENT_SD_DIR);
                    changeDataList();
                }
            }
        };
    }

    private void getData()
    {
        mDataList.clear();
        FileUnit.getFileList("phone", Consts.CURRENT_SD_DIR);
    }



    private void changeDataList()
    {
        getData();
        myAdapter = new DirListAdapter(getApplicationContext(), mDataList);

        if (Consts.VIEW_TYPE == Consts.VIEW_TYPE_GRIDVIEW)
        {
            setContentView(R.layout.layout_gridview);
            mAbsListView = (GridView) findViewById(R.id.listView);
        } else
        {
            setContentView(R.layout.layout_listview);
            mAbsListView = (ListView) findViewById(R.id.listView);
        }

        if (mAbsListView != null)
        {
            mAbsListView.setAdapter(myAdapter);
            mAbsListView.setOnItemClickListener(this);
            mAbsListView.setOnScrollListener(this);
            setScrolledPosition();
        }
        mTextViewCurrentDir = (TextView) findViewById(R.id.textViewCurrentDir);
        if (mTextViewCurrentDir != null)
        {
            mTextViewCurrentDir.setText(Consts.CURRENT_SD_DIR);
        }
    }


    @Override
    public void onBackPressed()
    {
        if (Consts.IS_EDIT && !Consts.IS_COPY_BE_CLICKED)
        {
            Consts.selectedPath.clear();
            Consts.IS_COPY_BE_CLICKED = false;
            Consts.IS_EDIT = false;
            Message msg = new Message();
            msg.what = Msg.MSG_CHANGE_MENU_STATE;
            MainActivity.handler.sendMessage(msg);
            saveCurrentScrollPosition(Consts.CURRENT_SD_DIR);
            changeDataList();
            return;
        }

        String pre_dir = FileUnit.getPreDir(Consts.CURRENT_SD_DIR);
        if (pre_dir == null || pre_dir.equals(""))
        {
            super.onBackPressed();
            return;
        }
        Consts.CURRENT_SD_DIR = pre_dir;
        if (mTextViewCurrentDir != null)
        {
            mTextViewCurrentDir.setText(Consts.CURRENT_SD_DIR);
        }
        changeDataList();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        String path = mDataList.get(position);
        if (FileUnit.isDir(path))
        {
            Consts.CURRENT_SD_DIR = path;
            if (mTextViewCurrentDir != null)
            {
                mTextViewCurrentDir.setText(Consts.CURRENT_SD_DIR);
            }
            saveCurrentScrollPosition(FileUnit.getPreDir(Consts.CURRENT_SD_DIR));
            getData();
            myAdapter.notifyDataSetChanged();
        } else
        {
            Intent intent;
            String extName = FileUnit.getExtName(path);
            Consts.CurrentFile = path;
            if (extName != null)
            {
                if (extName.equals("jpg") || extName.equals("png") || extName.equals("gif") || extName.equals("bmp"))
                {

                    UrlsInfo urlsInfo = getImageListInCurrentDir(path);
                    intent = new Intent(this, ImageShowActivity.class);
                    intent.putExtra(ImageShowActivity.EXTRA_IMAGE_URLS, urlsInfo.urls);
                    intent.putExtra(ImageShowActivity.EXTRA_IMAGE_INDEX, urlsInfo.pos);
                    startActivity(intent);
                } else if (extName.equals("mp3") || extName.equals("ape") || extName.equals("flac"))
                {
                    intent = new Intent(this, MediaPlayActivity.class);
                    intent.putExtra("music_info", path);
                    startActivity(intent);
                }
            }
        }
    }


    private UrlsInfo getImageListInCurrentDir(String path)
    {
        int pos = 0, count = 0;
        String dir = FileUnit.getPreDir(path);
        getData();
        if (mDataList == null || mDataList.size() <= 0)
            return null;
        String[] urlsTemp = new String[mDataList.size()];
        for (int i = count = 0; i < mDataList.size(); i++)
        {
            String extName = FileUnit.getExtName(mDataList.get(i));
            if (extName.equals("jpg") || extName.equals("png") || extName.equals("gif") || extName.equals("bmp"))
            {
                urlsTemp[count++] = mDataList.get(i);
            }
        }
        String[] urls = new String[count];
        for (int i = 0; i < count; i++)
        {
            if (path.equals(urlsTemp[i]))
            {
                pos = i;
            }
            urls[i] = "file://" + urlsTemp[i];
        }
        UrlsInfo urlsInfo = new UrlsInfo();
        urlsInfo.urls = urls;
        urlsInfo.pos = pos;
        return urlsInfo;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {//不滚动时保存当前滚动的位置
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
        {
            mScrolledPosition = view.getFirstVisiblePosition();
            //	mScrolledPosition = getScrollY(view);
            Log.v("BOSHUAI", "scrolled position:" + mScrolledPosition);
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
    }

    private int getScrollY(AbsListView listView)
    {
        View v = listView.getChildAt(0);
        if (v == null)
            return 0;
        int firstVisiblePos = listView.getFirstVisiblePosition();
        int top = v.getTop();
        return -top + firstVisiblePos * v.getHeight();
    }

    private void setScrolledPosition()
    {
        if (mStackScrollPos != null && !mStackScrollPos.isEmpty())
        {
            ScrollPosInfo spi = mStackScrollPos.pop();
            if (spi != null)
            {
                Integer _scrollPos = spi.pos;
                if (_scrollPos != null && _scrollPos >= 0)
                {
                    Log.v("BOSHUAI", "scrolllistBy current scrollPos:" + _scrollPos);
                    mAbsListView.setSelection(_scrollPos);
                } else
                {
                    Log.v("BOSHUAI", "in setScrollPosition, _scrollPos is null or _scrollPos <0 " + "  " + _scrollPos + " map:" + spi.toString());
                }
            } else
            {
                Log.v("BOSHUAI", "in setScrollPosition,map is null");
            }
        } else
        {
            Log.v("BOSHUAI", "in setScrollPosition, mScroll is null or mStackScrollPos is empty");
        }
    }

    private void saveCurrentScrollPosition(String currentDir)
    {
        ScrollPosInfo spi = new ScrollPosInfo();
        spi.dir = currentDir;
        spi.pos = mScrolledPosition;

        mStackScrollPos.push(spi);
        Log.v("BOSHUAI", "saveCurrentScrollPos:" + mScrolledPosition + " currentDir:" + currentDir + " mStackScrollPos:" + mStackScrollPos);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.v("BOSHUAI", "Phone activity onDestroy()");
    }
}
