package boshuai.net.ntools.unit;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by pc on 2017/1/26 0026.
 */

public class CharConvert
{
    public static String TranEncode2GBK(String str)
    {
        String strEncode = CharConvert.getEncoding(str);
        try
        {
            String temp = new String(str.getBytes(strEncode), "GB2312");
            return temp;
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String TranEncode2UTF8(String str)
    {
        String strEncode = CharConvert.getEncoding(str);
        try
        {
            String temp = new String(str.getBytes(strEncode), "UTF-8");
            return temp;
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isGB2312(char c)
    {
        Character ch = new Character(c);
        String sCh = ch.toString();

        byte[] bb = new byte[0];
        try
        {
            bb = sCh.getBytes("gb2312");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return false;
        }
        if(bb.length>1)
        {
            return true;
        }

        return false;
    }

    public static String getEncoding(String str)
    {
        String encode = "GB2312";
        String s;
        try
        {
            if(str.equals(new String(str.getBytes(encode), encode)))
            {
                s = encode;
                return s;
            }
        }
        catch (Exception e)
        {
        }

        encode = "ISO-8859-1";
        try
        {
            if(str.equals(new String(str.getBytes(encode), encode)))
            {
                s = encode;
                return s;
            }
        }
        catch (Exception e)
        {
        }


        encode = "UTF-8";
        try
        {
            if(str.equals(new String(str.getBytes(encode), encode)))
            {
                s = encode;
                return s;
            }
        }
        catch (Exception e)
        {
        }

        encode = "GBK";
        try
        {
            if(str.equals(new String(str.getBytes(encode), encode)))
            {
                s = encode;
                return s;
            }
        }
        catch (Exception e)
        {
        }

        return "";



    }

}
