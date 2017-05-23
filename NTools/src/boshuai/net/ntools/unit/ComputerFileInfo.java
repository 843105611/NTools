package boshuai.net.ntools.unit;

import android.util.Log;

/**
 * Created by pc on 2017/1/27 0027.
 */

public class ComputerFileInfo
{
    public String fileName;
    public String extName;
    public String type;
    public long fileSize;

    public static  ComputerFileInfo getComputerFileInfo(String filePath)
    {
        ComputerFileInfo cfi = new ComputerFileInfo();
        if(filePath==null || filePath.length()<=0)
        {
            return null;
        }

        String[] temp = filePath.split("\\?");
        if(temp!=null && temp.length>=3)
        {
            cfi.fileName = temp[0];
            cfi.type = temp[1];
            cfi.extName = "";
            if(cfi.type.equals("f"))
            {
                if(cfi.fileName.contains("."))
                {
                    cfi.extName = cfi.fileName.substring(cfi.fileName.lastIndexOf(".")+1, cfi.fileName.length());
                    Log.v("BOSHUAI", "extendName:" + cfi.extName);
                }
            }
            cfi.fileSize = Long.parseLong(temp[2]);
            Log.v("BOSHUAI", "cfi.fileName:" + cfi.fileName + " type:" + cfi.type + " fileSize:" + cfi.fileSize);
            return  cfi;
        }

        return null;
    }

}