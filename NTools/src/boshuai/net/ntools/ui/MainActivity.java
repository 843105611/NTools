package boshuai.net.ntools.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import boshuai.net.ntools.R;
import boshuai.net.ntools.unit.Consts;
import boshuai.net.ntools.unit.FileUnit;
import boshuai.net.ntools.unit.Msg;
import boshuai.net.ntools.unit.Uri2Path;

public class MainActivity extends  TabActivity  implements  View.OnClickListener
{
	int CHECKED_COLOR = Color.BLACK;
	int CHECKED_BG_COLOR = Color.LTGRAY;
	TabHost tabHost;
	RadioGroup radioGroup;
	RelativeLayout bottom_layout;
	RadioButton rbPhone, rbComputer;
	LinearLayout linearLayoutFragment;
	private Menu mMenu = null;
	public static Handler handler;

    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
		bottom_layout = (RelativeLayout) findViewById(R.id.layout_bottom);
		linearLayoutFragment = (LinearLayout)findViewById(R.id.linearLayoutFragment);

		tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec("phone").setIndicator("phone").setContent(new Intent(this, TabPhoneActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("computer").setIndicator("computer").setContent(new Intent(this, TabComputerActivity.class)));

		radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
		rbPhone = (RadioButton)findViewById(R.id.radio_phone);
		rbComputer = (RadioButton)findViewById(R.id.radio_computer);

		rbPhone.setOnClickListener(this);
		rbComputer.setOnClickListener(this);

		rbPhone.setTextColor(CHECKED_COLOR);
		rbPhone.setBackgroundColor(CHECKED_BG_COLOR);
		tabHost.setCurrentTabByTag("phone");



		if(Consts.IS_COMPUTER_CONNECTED==false)
		{
			removeComputerView();
		}
		share();
		handler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				switch(msg.what)
				{
					case Msg.MSG_CHANGE_MENU_STATE:
						Log.v("BOSHUAI", "change menu state now");
						onCreateOptionsMenu(mMenu);
						break;

					case Msg.MSG_COMPUTER_CONNECTED:
						Log.v("BOSHUAI", "COMPUTER is connected");
						addComputerView();
						break;

					case Msg.MSG_COMPUTER_DISCONNECTED:
						removeComputerView();
						break;
				}
			};
		};
    }

	private void removeComputerView()
	{
		if(linearLayoutFragment!=null && bottom_layout!=null)
		{
			linearLayoutFragment.removeView(bottom_layout);
		}
	}

	private void addComputerView()
	{
		if(linearLayoutFragment!=null && bottom_layout!=null)
		{
			linearLayoutFragment.addView(bottom_layout);
		}
	}


	private void setMenuState(MenuItem menuItem, boolean enable, int showType)
	{
		if(menuItem!=null)
		{
			menuItem.setEnabled(enable);
			menuItem.setShowAsAction(showType);
		}
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
		MenuItem menuCopy, menuPaste, menuDel, menuCancel;
		if(mMenu==null)
		{
			mMenu = menu;
		}
		else
		{
			mMenu.clear();
		}
        // Inflate the menu; this adds items to the action bar if it is present.
		if(Consts.IS_EDIT)
		{
			getMenuInflater().inflate(R.menu.main_edit, menu);
			menuCancel = menu.getItem(Consts.MENU_CANCEL);
			menuDel = menu.getItem(Consts.MENU_DEL);
			menuCopy = menu.getItem(Consts.MENU_COPY);
			menuPaste = menu.getItem(Consts.MENU_PASTE);

			setMenuState(menuCancel, true, MenuItem.SHOW_AS_ACTION_ALWAYS);
			setMenuState(menuCopy, false, MenuItem.SHOW_AS_ACTION_ALWAYS);
			setMenuState(menuPaste, false, MenuItem.SHOW_AS_ACTION_ALWAYS);
			setMenuState(menuDel, false, MenuItem.SHOW_AS_ACTION_ALWAYS);

			if(Consts.selectedPath.size()>0)
			{
				setMenuState(menuCopy, true, MenuItem.SHOW_AS_ACTION_ALWAYS);
				setMenuState(menuDel, true,  MenuItem.SHOW_AS_ACTION_ALWAYS);
				if(Consts.IS_COPY_BE_CLICKED)
				{
					setMenuState(menuPaste, true, MenuItem.SHOW_AS_ACTION_ALWAYS);
					setMenuState(menuDel, false, MenuItem.SHOW_AS_ACTION_ALWAYS);
					setMenuState(menuCopy, false, MenuItem.SHOW_AS_ACTION_ALWAYS);
				}
			}
			else
			{
				setMenuState(menuCopy, false, MenuItem.SHOW_AS_ACTION_ALWAYS);
				setMenuState(menuPaste, false, MenuItem.SHOW_AS_ACTION_ALWAYS);
				setMenuState(menuDel, false, MenuItem.SHOW_AS_ACTION_ALWAYS);
			}
		}
		else
		{
			getMenuInflater().inflate(R.menu.main, menu);
		}
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Message msg;
		int itemID = item.getItemId();

		switch(itemID)
		{
			case R.id.action_control_pc:
			{
				Intent intent = new Intent(this, ControlComputerActivity.class);
				startActivity(intent);
			}
				break;
			
			case R.id.action_settings:
				break;

			case R.id.action_change_view:
				if(Consts.VIEW_TYPE==Consts.VIEW_TYPE_GRIDVIEW)
				{
					Consts.VIEW_TYPE = Consts.VIEW_TYPE_LISTVIEW;
				}
				else
				{
					Consts.VIEW_TYPE = Consts.VIEW_TYPE_GRIDVIEW;
				}
				msg = new Message();
				msg.what = Msg.MSG_CHANGE_VIEW_TYPE;
				if(Consts.CURRENT_TAB.equals("phone"))
				{
					TabPhoneActivity.mHandler.sendMessage(msg);
				}
				else if(Consts.CURRENT_TAB.equals("computer"))
				{
					TabComputerActivity.handler.sendMessage(msg);
				}
				onCreateOptionsMenu(mMenu);
				break;

			case R.id.action_edit:
			{
				Consts.IS_EDIT = true;
				msg = new Message();
				msg.what = Msg.MSG_CHANGE_MENU_STATE;
				if(Consts.CURRENT_TAB.equals("phone"))
				{
					TabPhoneActivity.mHandler.sendMessage(msg);
				}
				else if(Consts.CURRENT_TAB.equals("computer"))
				{
					TabComputerActivity.handler.sendMessage(msg);
				}
			}
			break;

			case R.id.action_sort_by_date:
			{
				Consts.CURRENT_SORT_TYPE = Consts.SORT_TYPE_BY_DATE;
				if(Consts.CURRENT_SORT_TYPE_CHANGE==1)
				{
					Consts.CURRENT_SORT_TYPE_CHANGE = -1;
				}
				else
				{
					Consts.CURRENT_SORT_TYPE_CHANGE  = 1;
				}
				msg = new Message();
				msg.what = Msg.MSG_SORT_CHANGE;
				if(Consts.CURRENT_TAB.equals("phone"))
				{
					TabPhoneActivity.mHandler.sendMessage(msg);
				}
				else if(Consts.CURRENT_TAB.equals("computer"))
				{
					TabComputerActivity.handler.sendMessage(msg);
				}
			}
			break;

			case R.id.action_sort_by_filename:
			{
				Consts.CURRENT_SORT_TYPE = Consts.SORT_TYPE_BY_FILENAME;
				if(Consts.CURRENT_SORT_TYPE_CHANGE==1)
				{
					Consts.CURRENT_SORT_TYPE_CHANGE = -1;
				}
				else
				{
					Consts.CURRENT_SORT_TYPE_CHANGE  = 1;
				}
				msg = new Message();
				msg.what = Msg.MSG_SORT_CHANGE;
				if(Consts.CURRENT_TAB.equals("phone"))
				{
					TabPhoneActivity.mHandler.sendMessage(msg);
				}
				else if(Consts.CURRENT_TAB.equals("computer"))
				{
					TabComputerActivity.handler.sendMessage(msg);
				}
			}
			break;

			case R.id.action_copy:
			{
				Consts.IS_COPY_BE_CLICKED = true;
				onCreateOptionsMenu(mMenu);

				//取消复制按钮功能
				MenuItem menuItem = mMenu.getItem(Consts.MENU_COPY);
				menuItem.setEnabled(false);

				msg = new Message();
				msg.what = Msg.MSG_CHANGE_VIEW_TYPE;
				if(Consts.CURRENT_TAB.equals("phone"))
				{
					TabPhoneActivity.mHandler.sendMessage(msg);
				}
				else if(Consts.CURRENT_TAB.equals("computer"))
				{
					TabComputerActivity.handler.sendMessage(msg);
				}
			}
			break;

			case R.id.action_paste:
			{
				//TODO 执行粘贴操作

				if(Consts.selectedPath==null)
				{
					Log.v("BOSHUAI", "selectPath is null");
				}
				else
				{
					for(String value:Consts.selectedPath.values())
					{
						Log.v("BOSHUAI", "PATH:" + value);
						String dir = Consts.CURRENT_SD_DIR;
						String destFileName = dir + "/" + FileUnit.getFileName(value);
						FileUnit.copyFile(value, destFileName);
						msg = new Message();
						msg.what = Msg.MSG_CHANGE_VIEW_TYPE;
						if(Consts.CURRENT_TAB.equals("phone"))
						{
							TabPhoneActivity.mHandler.sendMessage(msg);
						}
						else if(Consts.CURRENT_TAB.equals("computer"))
						{
							TabComputerActivity.handler.sendMessage(msg);
						}
					}
				}

				Consts.selectedPath.clear();
				//重置IS_COPY_BE_CLICKED 状态
				Consts.IS_EDIT = false;
				Consts.IS_COPY_BE_CLICKED = false;
				onCreateOptionsMenu(mMenu);
			}
			break;

			case R.id.action_del:
			{
				//TODO 执行删除操作

				if(Consts.selectedPath==null)
				{
					Log.v("BOSHUAI", "selectPath is null");
				}
				else
				{
					for(String value:Consts.selectedPath.values())
					{
						Log.v("BOSHUAI", "PATH:" + value);
						FileUnit.deleteFile(value);
						msg = new Message();
						msg.what = Msg.MSG_CHANGE_VIEW_TYPE;
						if(Consts.CURRENT_TAB.equals("phone"))
						{
							TabPhoneActivity.mHandler.sendMessage(msg);
						}
						else if(Consts.CURRENT_TAB.equals("computer"))
						{
							TabComputerActivity.handler.sendMessage(msg);
						}
					}
				}

				Consts.selectedPath.clear();
				//重置IS_COPY_BE_CLICKED 状态
				Consts.IS_EDIT = false;
				Consts.IS_COPY_BE_CLICKED = false;
				onCreateOptionsMenu(mMenu);

				msg = new Message();
				msg.what = Msg.MSG_CHANGE_VIEW_TYPE;
				if(Consts.CURRENT_TAB.equals("phone"))
				{
					TabPhoneActivity.mHandler.sendMessage(msg);
				}
				else if(Consts.CURRENT_TAB.equals("computer"))
				{
					TabComputerActivity.handler.sendMessage(msg);
				}
			}
			break;

			case R.id.action_cancel:
			{
				Consts.selectedPath.clear();
				Consts.IS_EDIT = false;
				Consts.IS_COPY_BE_CLICKED = false;
				onCreateOptionsMenu(mMenu);

				msg = new Message();
				msg.what = Msg.MSG_CHANGE_MENU_STATE;
				if(Consts.CURRENT_TAB.equals("phone"))
				{
					TabPhoneActivity.mHandler.sendMessage(msg);
				}
				else if(Consts.CURRENT_TAB.equals("computer"))
				{
					TabComputerActivity.handler.sendMessage(msg);
				}
			}
			break;

			default:
				onCreateOptionsMenu(mMenu);
				msg = new Message();
				msg.what = Msg.MSG_CHANGE_MENU_STATE;
				if(Consts.CURRENT_TAB.equals("phone"))
				{
					TabPhoneActivity.mHandler.sendMessage(msg);
				}
				else if(Consts.CURRENT_TAB.equals("computer"))
				{
					TabComputerActivity.handler.sendMessage(msg);
				}
				break;

		}
		Log.v("BOSHUAI", "option menu clicked");

		return super.onOptionsItemSelected(item);
	}



	@Override
	public void onOptionsMenuClosed(Menu menu)
	{
		Log.v("BOSHUAI", "optionsMenuClosed");
		onCreateOptionsMenu(mMenu);
		super.onOptionsMenuClosed(menu);
	}

	private void setRadioBack(int checkedId)
    {
    	switch(checkedId)
    	{
    	case R.id.radio_phone:
			Consts.CURRENT_TAB = "phone";
    		rbPhone.setTextColor(CHECKED_COLOR);
    		rbPhone.setBackgroundColor(CHECKED_BG_COLOR);
    		rbComputer.setTextColor(Color.BLACK);
    		rbComputer.setBackgroundColor(Color.WHITE);
    		break;

    	case R.id.radio_computer:
			Consts.CURRENT_TAB = "computer";
    		rbPhone.setBackgroundColor(Color.WHITE);
    		rbComputer.setTextColor(CHECKED_COLOR);
    		rbComputer.setBackgroundColor(CHECKED_BG_COLOR);
    		break;
    	}
    }

	@Override
	protected void onResume()
	{
		super.onResume();
		if(Consts.CURRENT_TAB.equals("phone"))
		{
			setRadioBack(R.id.radio_phone);
			tabHost.setCurrentTabByTag("phone");
		}
		else
		{
			setRadioBack(R.id.radio_computer);
			tabHost.setCurrentTabByTag("computer");
		}
	}

	public String getRealPathFromURI(Uri contentUri)
	{
		String res = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
		if(cursor.moveToFirst())
		{;
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			res = cursor.getString(column_index);
		}
		cursor.close();
		return res;
	}

	private void share()
	{
		Intent intent = getIntent();
		if(intent!=null)
		{
			String action = intent.getAction();
			String type = intent.getType();

			if(action.equals("android.intent.action.SEND"))
			{
				Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
				String path = Uri2Path.getPathByUri4kitkat(this,uri);
			}
			else if(action.equals("android.intent.action.SEND_MULTIPLE"))
			{
				ArrayList<Uri> list = intent.getParcelableArrayListExtra(intent.EXTRA_STREAM);
				for(int i=0; list!=null && i<list.size(); i++)
				{
					String path = Uri2Path.getPathByUri4kitkat(this, list.get(i));
				}
			}
		}
	}

	@Override
	public void onClick(View view)
	{
		if(view==rbComputer)
		{
			setRadioBack(R.id.radio_computer);
			tabHost.setCurrentTabByTag("computer");
		}
		else if(view==rbPhone)
		{
			setRadioBack(R.id.radio_phone);
			tabHost.setCurrentTabByTag("phone");
		}
	}
}
