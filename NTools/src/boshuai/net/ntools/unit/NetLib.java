package boshuai.net.ntools.unit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;


/**
 * Created by pc on 2017/1/23 0023.
 */

public class NetLib
{
    public static  Socket connect(String ip, int port)
    {
        Socket socket;
        try
        {
            socket = new Socket(ip, port);
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        return  socket;
    }

    public static int writeBytes(OutputStream os, byte[] buff)
    {
        int count = 0;
//        for(int i=0; i<buff.length; i++)
//        {
//            try
//            {
//                os.write(buff, i, 1);
//          //      os.write
//                count++;
//            } catch (IOException e)
//            {
//                e.printStackTrace();
//                return -1;
//            }
//        }
//        return count;
        try
        {
            os.write(buff);
            os.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return buff.length;
    }

    public static int sendString(Socket s, String str)
    {
        int countSended = 0;
        try
        {
            OutputStream os = s.getOutputStream();
            byte[] buff = str.getBytes();
            if(buff!=null)
            {
                int length = buff.length;
                byte[] byteLength = NetLib.int2byte(length);
                countSended = writeBytes(os, byteLength);
                if(countSended>=byteLength.length)
                {
                    countSended =writeBytes(os, buff);
                    return countSended;
                }
                else
                {
                    return -1;
                }

            }
            return -1;
        } catch (IOException e)
        {
            e.printStackTrace();
            return -2;
        }
    }

    public static byte[] recvBytes(InputStream is, int length)
    {
        byte[] buff = new byte[length];
       int i, recved;
        for(i=recved=0; recved<length; )
        {
            try
            {
                i = is.read(buff, recved, length-recved);
                if(i>0)
                {
                    recved += i;
                }
            } catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        return buff;
    }

    public static String recvString(Socket s)
    {
        try
        {
            InputStream is = s.getInputStream();
            byte[] buffLength = recvBytes(is, 4);
            if(buffLength==null)
                return null;

            int length = byte2int(buffLength);

            if(length>0)
            {
                byte[] buff = new byte[length];
                int recved;
                for(int i = recved=0; recved<length;)
                {
                   i = is.read(buff, recved, length-recved);
                    if(i>0)
                    {
                        recved += i;
                    }
                }
                String str = new String(buff, "GB2312");
                return str;

            }
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public static boolean senfFile(Socket s, String locFilePath, String destFilePath)
    {
        OutputStream os = null;
        try
        {
            os = s.getOutputStream();
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        File file = new File(locFilePath);
        try
        {
            FileInputStream fis = new FileInputStream(file);
            int fileSize = (int)file.length();
            if(fileSize<=0)
            {
                try
                {
                    fis.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                return false;
            }




            byte fileSizeByte[] = int2byte(fileSize);
            writeBytes(os, fileSizeByte);

            int lenReaded = 0;
            byte[] buff = new byte[2048];

            while(true)
            {
                try
                {
                    lenReaded = fis.read(buff);
                    if(lenReaded>0)
                    {
                        os.write(buff, 0, lenReaded);
                        os.flush();
                    }
                    else
                    {
                        break;
                    }

                } catch (IOException e)
                {
                    e.printStackTrace();
                    try
                    {
                        fis.close();
                    } catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }

            try
            {
                fis.close();
              //  os.close();
            } catch (IOException e)
            {
                e.printStackTrace();
                return false;
            }
            return true;
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
    }


    public static byte[] int2byte(int res)
    {
        byte[] targets = new byte[4];

        targets[0] = (byte)(res & 0xff);//最低位
        targets[1] = (byte)((res >> 8) & 0xff); //次低位
        targets[2] = (byte)((res>>16)&0xff);//次高位
        targets[3] = (byte)(res>>24);//最高位
        return targets;
    }

    public static int byte2int(byte[] res)
    {
        int targets = (res[0] &0xff) | ((res[1]<<8)&0xff00) | ((res[2]<<24)>>8) | (res[3]<<24);

        return targets;
    }

}
