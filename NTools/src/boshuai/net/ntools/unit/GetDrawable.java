package boshuai.net.ntools.unit;

import boshuai.net.ntools.R;

/**
 * Created by Administrator on 2017/1/10.
 */

public class GetDrawable
{
    public static int getDrawableByExtendName(String extName)
    {
        extName = extName.toLowerCase().trim();
        if(extName.equals("txt"))
        {
            return R.drawable.file_type_txt;
        }
        else if(extName.equals("zip") || extName.equals("rar") || extName.equals("gz"))
        {
            return R.drawable.file_type_rar;
        }
        else if(extName.equals("xls") || extName.equals("xlsx"))
        {
            return R.drawable.file_type_xls;
        }
        else if(extName.equals("doc") || extName.equals("docx"))
        {
            return R.drawable.file_type_word;
        }
        else if(extName.equals("mp3") || extName.equals("ape") || extName.equals("oog") )
        {
            return R.drawable.file_type_music;
        }
        else if(extName.equals("mp4") || extName.equals("avi") || extName.equals("wmv"))
        {
            return R.drawable.file_type_video;
        }
        else if(extName.equals("pdf"))
        {
            return R.drawable.file_type_pdf;
        }
        else if(extName.equals("ppt") || extName.equals("pptx"))
        {
            return R.drawable.file_type_ppt;
        }
        else if(extName.equals("exe"))
        {
            return R.drawable.file_type_exe;
        }
        else if(extName.equals("dll"))
        {
            return R.drawable.file_type_dll;
        }
        else if(extName.equals("sdcard"))
        {
            return R.drawable.file_type_sdcard;
        }
        else if(extName.equals("hdisk"))
        {
            return R.drawable.file_type_hdisk;
        }
        else if(extName.equals("dir"))
        {
            return R.drawable.file_type_dir;
        }
        else if(extName.equals("jpg") || extName.equals("png") || extName.equals("bmp") || extName.equals("gif"))
        {
            return R.drawable.file_type_image;
        }

        return R.drawable.file_type_unknow;
    }
}
