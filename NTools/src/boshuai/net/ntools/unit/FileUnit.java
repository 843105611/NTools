package boshuai.net.ntools.unit;

import android.os.Environment;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import boshuai.net.ntools.sort.SortByData;
import boshuai.net.ntools.sort.SortByFileName;
import boshuai.net.ntools.sort.SortByType;
import boshuai.net.ntools.ui.TabComputerActivity;
import boshuai.net.ntools.ui.TabPhoneActivity;

/**
 * Created by Administrator on 2017/1/10.
 */

public class FileUnit
{

    public static String SD_ROOT;

    private static String getNowTime()
    {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        int msec = c.get(Calendar.MILLISECOND);

        return "" + year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second + ":" + msec;
    }

    private static String getRandomFileName()
    {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        int msec = c.get(Calendar.MILLISECOND);
        return "file_test_" + year +"-" + month +"-" + date + "-" + hour + "-" + minute + "-" + second + "-" + msec;
    }

    /**
     * 获取内置SD卡路径
     * @return
     */
    public static String getInnerSDCardPath() {
        String sd_path =  Environment.getExternalStorageDirectory().getPath();
        if(sd_path!=null)
        {
            SD_ROOT = sd_path.substring(0, sd_path.lastIndexOf("/"));
        }
        return sd_path;
    }

    /**
     * 获取外置SD卡路径
     * @return  应该就一条记录或空
     */
    public static ArrayList<String> getExtSDCardPath()
    {
        String innerSdcardPath = getInnerSDCardPath();
        ArrayList<String> lResult = new ArrayList<String>();
        if(innerSdcardPath!=null)
        {
            lResult.add(innerSdcardPath);
        }

        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                line.toLowerCase();
                if ((line.contains("storage") || line.contains("sdcard") || line.contains("otg") ))
                {
                    String [] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory())
                    {
                        boolean b;
                        String testFileName = path + "/." + getRandomFileName();
                        File fTest = new File(testFileName);

                        try
                        {
                            b = fTest.createNewFile();
                            b = fTest.delete();
                        }
                        catch(IOException e)
                        {
                            b = false;
                        }


                        if(b && !path.equals(innerSdcardPath))
                        {
                            lResult.add(path);
                        }

                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }

        return lResult;
    }



    public static void log(String path, String content)
    {
        content = getNowTime() + ">>" + content + "\r\n";
        try {
            // 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(path, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            //将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.writeBytes(content);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileNameForNumber(String fileName)
    {
        if(fileName==null)
            return null;

        String dir = getPreDir(fileName);
        String extName = getExtName(fileName);
        String baseName = getFileBaseName(fileName);

        int i = 1;
        while(true)
        {
            String fileName2 = dir + "/" + baseName + "(" + i + ")." + extName;
            File file = new File(fileName2);
            if(file.exists()==false)
            {
                return fileName2;
            }
            i++;
        }

    }

    public static boolean deleteFile(String fileName)
    {
        if(fileName==null)
            return false;

        File file = new File(fileName);
        return file.delete();
    }

    public static boolean copyFile(String localFile, String destFile)
    {
        File file1, file2;
        if(isFile(localFile)==false)
            return false;

        String dir = getPreDir(destFile);
        File file = new File(dir);
        file.mkdirs();

        if(isDir(dir)==false)
            return false;

        if(localFile.equals(destFile))
        {
            destFile = getFileNameForNumber(destFile);
        }

        file1 = new File(localFile);
        file2 = new File(destFile);

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try
        {
            fis = new FileInputStream(file1);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }

        try
        {
            fos = new FileOutputStream(file2);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            try
            {
                fis.close();
            } catch (IOException e1)
            {
                e1.printStackTrace();
            }
            return false;
        }

        int readed = 0;
        byte [] buffRead = new byte[1024*1024];

        while(true)
        {
            try
            {
                readed = fis.read(buffRead);
                if(readed<=0)
                    break;

                fos.write(buffRead, 0, readed);
            } catch (IOException e)
            {
                e.printStackTrace();
                break;
            }
        }


        try
        {
            fis.close();
            fos.close();
        }
        catch(Exception e)
        {
            return false;
        }

        return true;
    }


    public static long getFileSize(String filePath)
    {
        File file = new File(filePath);
        if(file.exists() && file.isFile())
        {
            return file.length();
        }
        return -1;
    }

    public static String getFileName(String filePath)
    {
        if(filePath==null)
            return "";
        filePath = filePath.trim();
        if(filePath.contains("/"))
        {
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
            return fileName;
        }
        return null;
    }

    public static String getFileBaseName(String filePath)
    {
        if(filePath==null)
            return "";

        String fileName = FileUnit.getFileName(filePath);
        if(fileName!=null && fileName.length()>=1)
        {
            int i = fileName.indexOf('.');
            int leg = fileName.length();
            return (i > 0 ? (i + 1) == leg ? " " : fileName.substring(0, i) : " ");
        }

        return "";
    }

    public static String getExtName(String filePath)
    {
        String extName;
        if(filePath==null || filePath.indexOf(".")<0)
        {
            return "";
        }
        extName = filePath.substring(filePath.lastIndexOf(".")+1, filePath.length());
        if(extName!=null)
        {
            extName = extName.toLowerCase().trim();
            return extName;
        }
        return "";
    }

    public static String getType(String filePath)
    {
        File file = new File(filePath);

        if(file!=null && file.exists())
        {
            if(file.isFile())
                return "file";
            if(file.isDirectory())
                return "dir";
        }

        return "";
    }

    public static void getFileList(String type, final String dir)
    {
        if(dir==null)
        {
            return ;
        }

        if(type.equals("phone"))
        {
            if (dir.equals(SD_ROOT))
            {
                ArrayList<String> list =  FileUnit.getExtSDCardPath();
                for(int i=0; list!=null && i<list.size(); i++)
                {
                    TabPhoneActivity.mDataList.add(list.get(i));
                }
                return;
            }

            File file = new File(dir);
            File[] tempList = file.listFiles();

            for (int i = 0; tempList != null && i < tempList.length; i++)
            {
                String filePath = tempList[i].getAbsolutePath();
                if (filePath != null)
                {
                    filePath = filePath.trim();
                    String fileName = FileUnit.getFileName(filePath);
                    if (fileName != null && fileName.indexOf(".") == 0)
                    {
                        if (Consts.IS_SHOW_HIDDEN_FILE)
                        {
                            TabPhoneActivity.mDataList.add(filePath);
                        }
                    } else
                    {
                        TabPhoneActivity.mDataList.add(filePath);
                    }
                }
            }
            if(Consts.CURRENT_SORT_TYPE==Consts.SORT_TYPE_BY_FILENAME)
            {
                Collections.sort(TabPhoneActivity.mDataList, new SortByFileName(Consts.CURRENT_SORT_TYPE_CHANGE));
            }
            else if(Consts.CURRENT_SORT_TYPE==Consts.SORT_TYPE_BY_DATE)
            {
                Collections.sort(TabPhoneActivity.mDataList, new SortByData(Consts.CURRENT_SORT_TYPE_CHANGE));
            }
            Collections.sort(TabPhoneActivity.mDataList, new SortByType());
        }
        else if(type.equals("computer"))
        {

            Thread thread = new Thread(new Runnable()
            {//TODO 澶勭悊鑾峰彇computer鐩綍
                @Override
                public void run()
                {
                    if(Consts.sockDir!=null)
                    {
                        int countSended = NetLib.sendString(Consts.sockDir, dir);
                        if(countSended>0)
                        {
                            String temp = null;
                            while(true)
                            {
                                temp = NetLib.recvString(Consts.sockDir);
                                if(temp.equals(Consts.DIR_END_TOKEN))
                                {
                                    break;
                                }
                                if(temp!=null)
                                {
                                    String tempList0[] = temp.split(":");
                                    if (tempList0 != null && tempList0.length > 0)
                                    {
                                        String currentDir = tempList0[0];
                                        Consts.CurrentPC_Dir = currentDir;
                                        String tempList[] = tempList0[1].split("\\|");
                                        for (int i = 0; tempList != null && i < tempList.length; i++)
                                        {
                                            if (TabComputerActivity.mDataList.contains(tempList[i]) == false)
                                                TabComputerActivity.mDataList.add(tempList[i]);
                                        }
                                    }
                                }
                                if(TabComputerActivity.mDataList.size()>0)
                                {
                                    Message msg = new Message();
                                    msg.what = Msg.MSG_COMPUTER_DIR_LIST_READY;
                                    TabComputerActivity.handler.sendMessage(msg);
                                }
                            }
                        }
                        else
                        {
                        }
                    }
                }
            });
            thread.start();

        }
        return ;
    }

    public static boolean isDir(String path)
    {
        if(path==null)
            return false;

        File file = new File(path);
        return file.isDirectory();
    }

    public static boolean isFile(String path)
    {
        if(path==null)
            return false;
        File file = new File(path);
        return file.isFile();
    }

    public static String getPreDir(String currentDir)
    {
        if(currentDir==null)
            return null;

        if(currentDir.contains("/"))
        {
            return currentDir.substring(0, currentDir.lastIndexOf("/"));
        }
        return null;
    }

    public static String getPreDir2(String currentDir)
    {
        if(currentDir==null)
            return null;

        if(currentDir.length()==2)
        {
            return "/";
        }


        if(currentDir.contains("/"))
        {
            return currentDir.substring(0, currentDir.lastIndexOf("/"));
        }
        return null;
    }

}
