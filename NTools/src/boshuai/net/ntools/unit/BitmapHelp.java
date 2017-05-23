package boshuai.net.ntools.unit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/1/14.
 */

public class BitmapHelp
{

    /**
     * 将文件生成位图
     * @param path
     * @return
     * @throws IOException
     */
    public static  Bitmap getImageDrawable(String path)
    {

        if(path.contains("file://"))
        {
            path = path.substring("file://".length(), path.length());
        }

        //打开文件
        File file = new File(path);
        if(!file.exists())
        {
            return null;
        }

//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        byte[] bt = new byte[BUFFER_SIZE];

        //得到文件的输入流
        InputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e)
        {

            e.printStackTrace();
            return null;
        }

//        //将文件读出到输出流中
//        int readLength = in.read(bt);
//        while (readLength != -1) {
//            outStream.write(bt, 0, readLength);
//            readLength = in.read(bt);
//        }
//
//        //转换成byte 后 再格式化成位图
//        byte[] mDataList = outStream.toByteArray();

        BitmapFactory.Options opts=new BitmapFactory.Options();
        opts.inTempStorage = new byte[1000 * 1024];
        opts.inPreferredConfig = Bitmap.Config.RGB_565;

        opts.inPurgeable = true;

        opts.inSampleSize = 2;
//6.设置解码位图的尺寸信息
        opts.inInputShareable = true;
//7.解码位图
        Bitmap bitmap =BitmapFactory.decodeStream(in, null, opts);
//8.显示位图
        //    preview.setImageBitmap(bitmap);

        //      Bitmap bitmap = BitmapFactory.decodeByteArray(mDataList, 0, mDataList.length);// 生成位图


        return bitmap;
    }

}
