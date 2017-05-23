package boshuai.net.ntools.unit;

/**
 * Created by Administrator on 2017/1/17.
 */

public class TextHelp {

    /*

	//	AseoZdpAseo.init(this, AseoZdpAseo.INSERT_TYPE);
	String memoInfo = getSystemAvaialbeMemorySize();
	Log.v("BOSHUAI", "memoInfo:" + memoInfo);

	long max = Runtime.getRuntime().maxMemory();
	Log.v("BOSHUAI", "max memeory:" + max/1024/1024);

	//获得系统可用内存信息
	private String getSystemAvaialbeMemorySize(){
		//获得MemoryInfo对象
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo() ;
		//获得系统可用内存，保存在MemoryInfo对象上
		ActivityManager mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		mActivityManager.getMemoryInfo(memoryInfo) ;
		long memSize = memoryInfo.availMem ;

		//字符类型转换
		String availMemStr = formateFileSize(memSize);

		return availMemStr ;
	}

	//调用系统函数，字符串转换 long -String KB/MB
	private String formateFileSize(long size){
		return Formatter.formatFileSize(MainActivity.this, size);
	}
     */
}
