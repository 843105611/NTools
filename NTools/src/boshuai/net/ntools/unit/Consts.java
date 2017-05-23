package boshuai.net.ntools.unit;

import java.net.Socket;
import java.util.IdentityHashMap;
import java.util.WeakHashMap;

import boshuai.net.ntools.media.MusicService;

/**
 * Created by Administrator on 2017/1/9.
 */

public class Consts
{
    public static int VIEW_TYPE_GRIDVIEW = 0;
    public static int VIEW_TYPE_LISTVIEW = 1;
    public static int VIEW_TYPE = VIEW_TYPE_GRIDVIEW;


    public static String CURRENT_TAB = "phone";

    /*
        手机U盘相关配置
     */
    public static String CURRENT_SD_DIR ="/";
    public static String PRESD_DIR = "";
    public static String SD_ROOT = "";

    /*
        电脑磁盘相关配置
     */
    public static String CurrentPC_Dir="/";
    public static String PC_ROOT = "";
    public static String PREPC_DIR = "/";


    public static String DIR_END_TOKEN = "FILE_DIR_SVR_END_TOKEN_33445566";


    /*
        是否显示隐藏文件
     */
    public static boolean IS_SHOW_HIDDEN_FILE = false;


    /*
        屏幕尺寸
     */
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;


//发送动作相关定义
    public static float IMAGE_MOVE_COUNT = 0.5F; //执行发送动作时，图片移动效果，移动的距离为0.5倍屏幕高度
    public static float IMAGE_MOVE_V_MIN = 0.1f; //竖直方向超过屏幕高度的0.1倍就执行发送动作
    public static float IMAGE_MOVE_H_MAX = 0.1f;   //水平方向超过屏幕宽度的0.1倍，无论竖直方向移动多少都不执行发送动作


    public static boolean IS_EXIT_WHEN_TOUCH_EDGE = true;  //是否退出当前Activity，当按图片显示页面左右边缘时


    /*
        编辑相关配置
     */
    public static String CurrentFile;
    public static boolean IS_EDIT = false;
    public static WeakHashMap<String , String> selectedPath;
    public static boolean IS_COPY_BE_CLICKED = false;


    public static int MENU_COPY = 3;
    public static int MENU_PASTE = 4;
    public static int MENU_DEL = 5;
    public static int MENU_CANCEL = 6;


    public static MusicService mService;

    /*
        电脑连接相关配置
     */
    public static boolean IS_COMPUTER_CONNECTED = false;

    public static int HEART_TEST_SECOND = 5;
    public static Socket sockCommand;
    public static Socket sockDir;
    public static Socket sockFile;
    public static Socket sockHeart;


    public static String ip="192.168.43.116";
    public static int PORT_COMMAND = 3344;
    public static int PORT_DIR = 3345;
    public static int PORT_FILE = 3346;
    public static int PORT_HEART = 3347;

    public static String CURRENT_IMAGE = "";





    /*
        文件显示排序相关
     */
    public static final int SORT_TYPE_BY_FILENAME= 0;
    public static final int SORT_TYPE_BY_DATE = 1;
    public static int CURRENT_SORT_TYPE = SORT_TYPE_BY_FILENAME;
    public static int CURRENT_SORT_TYPE_CHANGE = -1;

}
