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
import boshuai.net.ntools.unit.ComputerFileInfo;
import boshuai.net.ntools.unit.Consts;
import boshuai.net.ntools.unit.FileUnit;
import boshuai.net.ntools.unit.Msg;

public class TabComputerActivity extends Activity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener
{
	GridView mGridView;
	ListView mListView;
	AbsListView mAbsListView;
	public static ArrayList<String> mDataList;
	TextView mTextViewCurrentDir;
	DirListAdapter myAdapter;
	static int scrolledPosition;
	static Stack<ScrollPosInfo> mScrollPos;
	public static Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mScrollPos = new Stack<ScrollPosInfo>();
		mDataList = new ArrayList<String>();
		changeDataList();
		handler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				if(msg.what==Msg.MSG_COMPUTER_DIR_LIST_READY)
				{
					Log.v("BOSHUAI", "computer dis list is ready");
					myAdapter.notifyDataSetChanged();
				}

				if (msg.what == Msg.MSG_CHANGE_VIEW_TYPE)
				{
					Log.v("BOSHUAI", "change view type now");
					changeDataList();
				}
				if(msg.what==Msg.MSG_CHANGE_MENU_STATE)
				{
					saveCurrentScrollPosition(Consts.CurrentPC_Dir);
					changeDataList();
				}
				if(msg.what==Msg.MSG_SORT_CHANGE)
				{
					changeDataList();
				}
			};
		};
	}

	private void getData()
	{
		mDataList.clear();
		FileUnit.getFileList("computer", Consts.CurrentPC_Dir);
	}

	private void setScrolledPosition()
	{
		if(mScrollPos!=null && !mScrollPos.isEmpty())
		{
			ScrollPosInfo spi = mScrollPos.pop();
			if(spi!=null)
			{
				Integer _scrollPos = spi.pos;
				if(_scrollPos!=null && _scrollPos>=0)
				{
					Log.v("BOSHUAI", "scrolllistBy current scrollPos:" + _scrollPos);
					mAbsListView.setSelection(_scrollPos);
				}
				else
				{
					Log.v("BOSHUAI", "in setScrollPosition, _scrollPos is null or _scrollPos <0 " +  "  " + _scrollPos + " map:" +spi.toString() );
				}
			}
			else
			{
				Log.v("BOSHUAI", "in setScrollPosition,map is null");
			}
		}
		else
		{
			Log.v("BOSHUAI", "in setScrollPosition, mScroll is null or mStackScrollPos is empty");
		}
	}

	private void changeDataList()
	{
		getData();
		myAdapter = new DirListAdapter(getApplicationContext(), mDataList);

		if(Consts.VIEW_TYPE==Consts.VIEW_TYPE_GRIDVIEW)
		{
			setContentView(R.layout.layout_gridview);
			mGridView = (GridView) findViewById(R.id.listView);
			mAbsListView = mGridView;
		}
		else
		{
			setContentView(R.layout.layout_listview);
			mListView = (ListView)findViewById(R.id.listView);
			mAbsListView = mListView;
		}

		if(mAbsListView !=null)
		{
			mAbsListView.setAdapter(myAdapter);
			mAbsListView.setOnItemClickListener(this);
			mAbsListView.setOnScrollListener(this);
			setScrolledPosition();
		}
		mTextViewCurrentDir = (TextView) findViewById(R.id.textViewCurrentDir);
		if(mTextViewCurrentDir !=null)
		{
			mTextViewCurrentDir.setText(Consts.CurrentPC_Dir);
		}
	}



	@Override
	public void onBackPressed()
	{
		if(Consts.IS_EDIT && !Consts.IS_COPY_BE_CLICKED)
		{
			Consts.selectedPath.clear();
			Consts.IS_COPY_BE_CLICKED = false;
			Consts.IS_EDIT = false;
			Message msg = new Message();
			msg.what = Msg.MSG_CHANGE_MENU_STATE;
			MainActivity.handler.sendMessage(msg);
			saveCurrentScrollPosition(Consts.CurrentPC_Dir);
			changeDataList();
			return;
		}

		String pre_dir = FileUnit.getPreDir2(Consts.CurrentPC_Dir);
		if(pre_dir==null || pre_dir.equals(""))
		{
			super.onBackPressed();
			return;
		}

		Consts.CurrentPC_Dir =pre_dir;
		if(mTextViewCurrentDir !=null)
		{
			mTextViewCurrentDir.setText(Consts.CurrentPC_Dir);
		}
		changeDataList();

		myAdapter.notifyDataSetChanged();
	}


	private void saveCurrentScrollPosition(String currentDir)
	{
		ScrollPosInfo spi = new ScrollPosInfo();
		spi.dir = currentDir;
		spi.pos = scrolledPosition;

		mScrollPos.push(spi);
		Log.v("BOSHUAI", "saveCurrentScrollPos:" + scrolledPosition + " currentDir:" + currentDir + " mStackScrollPos:" + mScrollPos);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{//不滚动时保存当前滚动的位置
		if(scrollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
		{
			scrolledPosition = view.getFirstVisiblePosition();
			//	mScrolledPosition = getScrollY(view);
			Log.v("BOSHUAI", "scrolled position:" + scrolledPosition );
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
	{
	}

	private int getScrollY(AbsListView listView)
	{
		View v = listView.getChildAt(0);
		if(v==null)
			return 0;
		int firstVisiblePos = listView.getFirstVisiblePosition();
		int top = v.getTop();
		return -top + firstVisiblePos*v.getHeight();
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		String path = mDataList.get(position);
		ComputerFileInfo cfi = ComputerFileInfo.getComputerFileInfo(path);
		Log.v("BOSHUAI", "path:" + path);
		if(cfi.type.equals("d") || cfi.type.equals("h"))
		{
			if(Consts.CurrentPC_Dir.equals("/"))
			{
				Consts.CurrentPC_Dir += cfi.fileName;
			}
			else
			{
				Consts.CurrentPC_Dir += "/" + cfi.fileName;
			}
			Log.v("BOSHUAI", "CurrentPC_dir:" + Consts.CurrentPC_Dir);
			if(mTextViewCurrentDir!=null)
			{
				mTextViewCurrentDir.setText(Consts.CurrentPC_Dir);
			}
			getData();
			Log.v("LIFULIN", "onItemClick getdata path:" + Consts.CurrentPC_Dir);
			myAdapter.notifyDataSetChanged();
		}
		else
		{
			Intent intent;
			Log.v("BOSHUAI", "open the file:" + path);
			String extName = FileUnit.getExtName(path);
			Consts.CurrentFile = path;
			if(extName!=null  )
			{
				if(extName.equals("jpg") || extName.equals("png") || extName.equals("gif") || extName.equals("bmp")) {

					UrlsInfo urlsInfo = getImageListInCurrentDir(path);
					intent = new Intent(this, ImageShowActivity.class);
					intent.putExtra(ImageShowActivity.EXTRA_IMAGE_URLS, urlsInfo.urls);
					intent.putExtra(ImageShowActivity.EXTRA_IMAGE_INDEX, urlsInfo.pos);
					startActivity(intent);
				}
				else if(extName.equals("mp3"))
				{
					intent = new Intent(this, MediaPlayActivity.class);
					intent.putExtra("music_info", path);
					startActivity(intent);
				}
			}
		}

	}



	class ScrollPosInfo
	{
		public String dir;
		public int pos;
	}

	class UrlsInfo
	{
		public String[] urls;
		public int pos;
	}

	private UrlsInfo getImageListInCurrentDir(String path)
	{
		int pos =0, count = 0;
		String dir = FileUnit.getPreDir2(path);
		getData();
		if(mDataList ==null || mDataList.size()<=0)
			return null;
		String[] urlsTemp = new String[mDataList.size()];

		for(int i = count=0; i< mDataList.size(); i++)
		{
			String extName = FileUnit.getExtName(mDataList.get(i));
			if(extName.equals("jpg") || extName.equals("png") || extName.equals("gif") || extName.equals("bmp"))
			{
				urlsTemp[count++] = mDataList.get(i);
			}
		}
		String[] urls = new String[count];
		for(int i=0;i<count; i++)
		{
			if(path.equals(urlsTemp[i]))
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


}
