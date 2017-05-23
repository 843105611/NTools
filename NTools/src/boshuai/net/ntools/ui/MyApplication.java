package boshuai.net.ntools.ui;

import android.app.Application;
import android.content.Context;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import java.util.WeakHashMap;

import boshuai.net.ntools.unit.Consts;
import boshuai.net.ntools.unit.FileUnit;
import boshuai.net.ntools.unit.Msg;
import boshuai.net.ntools.unit.NetLib;


/**
 * Created by Administrator on 2017/1/14.
 */

public class MyApplication extends Application
{

    @Override
    public void onCreate()
    {
        super.onCreate();

        //获取屏幕尺寸，并将其设置到全局中间量中
        WindowManager wm  = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        Consts.SCREEN_HEIGHT = dm.heightPixels;
        Consts.SCREEN_WIDTH= dm.widthPixels;

        //获取手机SD卡根目录
        FileUnit.getInnerSDCardPath();
        Consts.CURRENT_SD_DIR = FileUnit.SD_ROOT;
        Consts.selectedPath = new WeakHashMap<String, String>();

        connectSvr();
    }


    private boolean connectSvr()
    {
        Consts.sockCommand = null;
        Consts.sockDir = null;
        Consts.sockFile = null;

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.v("BOSHUAI", "thread running");

                Consts.sockCommand = NetLib.connect(Consts.ip, Consts.PORT_COMMAND);
                if(Consts.sockCommand!=null)
                {
//                    Log.v("BOSHUAI", "sockCommand connect success");
//                    NetLib.sendString(Consts.sockCommand, "get command");
//                    String str = NetLib.recvString(Consts.sockCommand);
//                    Log.v("BOSHUAI", "str:" + str);
                }

                Consts.sockDir = NetLib.connect(Consts.ip, Consts.PORT_DIR);
                if(Consts.sockDir!=null)
                {
//                    Log.v("BOSHUAI", "sockDir connect success");
//                    NetLib.sendString(Consts.sockDir, "get dir");
//                    String str2 = NetLib.recvString(Consts.sockDir);
//                    Log.v("BOSHUAI", "str:" + str2);
                }

                Consts.sockFile = NetLib.connect(Consts.ip, Consts.PORT_FILE);
                if(Consts.sockFile!=null)
                {
//                    Log.v("BOSHUAI", "sockFile connect success");
//                    NetLib.sendString(Consts.sockFile, "get file");
//                    String str3 = NetLib.recvString(Consts.sockFile);
//                    Log.v("BOSHUAI", "str:" + str3);
                }

                Consts.sockHeart = NetLib.connect(Consts.ip, Consts.PORT_HEART);

                while(true)
                {
                    //            Log.v("BOSHUAI", "heart testing...");
                    int lenSended=-1;
                    if(Consts.sockHeart!=null)
                    {
                        lenSended = NetLib.sendString(Consts.sockHeart, "get heart");
                    }
                    if(lenSended<0 )
                    {
                        // Log.v("BOSHUAI", "heart test failed");
                        Consts.sockHeart = null;
                        Consts.sockHeart = NetLib.connect(Consts.ip, Consts.PORT_HEART);
                        Consts.sockCommand = NetLib.connect(Consts.ip, Consts.PORT_COMMAND);
                        Consts.sockDir = NetLib.connect(Consts.ip, Consts.PORT_DIR);
                        Consts.sockFile = NetLib.connect(Consts.ip, Consts.PORT_FILE);
                        if((Consts.IS_COMPUTER_CONNECTED ==true))
                        {
                            //   Log.v("BOSHUAI", "Send heart test failed single");
                            if(MainActivity.handler!=null)
                            {
                                Message msg = new Message();
                                msg.what = Msg.MSG_COMPUTER_DISCONNECTED;
                                MainActivity.handler.sendMessage(msg);
                            }
                            Consts.IS_COMPUTER_CONNECTED = false;
                        }
                        try
                        {
                            Thread.sleep(1000*Consts.HEART_TEST_SECOND);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    else
                    {
                        //      Log.v("BOSHUAI", "heart test success lenRecved:" + lenSended);

                        if(Consts.IS_COMPUTER_CONNECTED == false)
                        {
                            String str4 = NetLib.recvString(Consts.sockHeart);
                            //      Log.v("BOSHUAI", "str:" + str4);

                            //       Log.v("BOSHUAI", "Send heart test success single");
                            if(MainActivity.handler!=null)
                            {
                                Message msg = new Message();
                                msg.what = Msg.MSG_COMPUTER_CONNECTED;
                                MainActivity.handler.sendMessage(msg);
                            }
                            Consts.IS_COMPUTER_CONNECTED = true;
                        }
                    }
                    try
                    {
                        Thread.sleep(1000*Consts.HEART_TEST_SECOND);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        return false;
    }

}
