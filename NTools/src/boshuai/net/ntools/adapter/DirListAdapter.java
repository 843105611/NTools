package boshuai.net.ntools.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import boshuai.net.ntools.R;
import boshuai.net.ntools.ui.MainActivity;
import boshuai.net.ntools.unit.ComputerFileInfo;
import boshuai.net.ntools.unit.Consts;
import boshuai.net.ntools.unit.FileUnit;
import boshuai.net.ntools.unit.Msg;
import boshuai.net.ntools.unit.ViewHolder;

/**
 * Created by Administrator on 2017/1/14.
 */
//ggggg

public class DirListAdapter extends BaseAdapter
{
    private ArrayList<String> fileList;
    private LayoutInflater mInflater;
    private Context mContext;



    public DirListAdapter(Context context, ArrayList<String> fileList)
    {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.fileList = fileList;
    }
    @Override
    public int getCount()
    {
        return fileList==null?0 : fileList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final ViewHolder holder;
        final String filePath = fileList.get(position);
        if(convertView==null)
        {
            holder = new ViewHolder();
            if(Consts.VIEW_TYPE==Consts.VIEW_TYPE_GRIDVIEW)
            {
                convertView = mInflater.inflate(R.layout.item_gridlist, null);
            }
            else
            {
                convertView = mInflater.inflate(R.layout.item_listview, null);
            }
            holder.name = (TextView)convertView.findViewById(R.id.textView_item);
            holder.image = (ImageView)convertView.findViewById(R.id.imageView_item);
            holder.selected = (CheckBox)convertView.findViewById(R.id.checkbox_item);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

//处理编辑逻辑
        if(Consts.IS_EDIT)
        {
            if(Consts.IS_COPY_BE_CLICKED==false)
            {//当复制按钮按下时，要准备粘贴，将可选状态取消
                holder.selected.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.selected.setVisibility(View.INVISIBLE);
            }
            holder.selected.setOnClickListener(new View.OnClickListener()
            {
                String filePath = fileList.get(position);
                @Override
                public void onClick(View v)
                {
                    if(holder.selected.isChecked())
                    {
                        Log.v("BOSHUAI", "path:" + fileList.get(position) + " be selected");
                        if(!Consts.selectedPath.containsKey(filePath))
                        {
                            Consts.selectedPath.put(filePath, filePath);
                        }
                    }
                    else
                    {
                        if(Consts.selectedPath.containsKey(filePath))
                        {
                            Consts.selectedPath.remove(filePath);
                        }
                    }
                    //改变编辑菜单状态
                    changeMenuState();
                }
            });

            String filePathTemp = Consts.selectedPath.get(filePath);
            if(filePathTemp!=null)
            {
                holder.selected.setChecked(true);
            }
            else
            {
                holder.selected.setChecked(false);
            }
            //改变编辑菜单状态
            changeMenuState();
        }
        else  //变为不可编辑状态
        {
            holder.selected.setVisibility(View.INVISIBLE);
        }
//编辑逻辑到此结束



        String fileName =  null;
        Object uri = null;

        if(Consts.CURRENT_TAB.equals("phone"))
        {
            fileName = FileUnit.getFileName(filePath);
            holder.name.setText(fileName);

            uri = getPhoneUri(filePath);
            if (uri != null)
            {
                Glide.with(mContext).load(uri).thumbnail(0.1f).into(holder.image);
            }
        }
        else if(Consts.CURRENT_TAB.equals("computer"))
        {
            Log.v("BOSHUAI", "In DirListAdapter filePath:" + filePath);
            ComputerFileInfo cfi = ComputerFileInfo.getComputerFileInfo(filePath);
            if(cfi!=null)
            {
                fileName = cfi.fileName;
                holder.name.setText(fileName);

                uri = getComputerUri(cfi.type, cfi.extName);
                if (uri != null)
                {
                    Glide.with(mContext).load(uri).thumbnail(0.1f).into(holder.image);
                }
            }
        }
        return convertView;
    }




    private void doWithEdit()
    {


    }


    private void changeMenuState()
    {
        Message msg = new Message();
        msg.what = Msg.MSG_CHANGE_MENU_STATE;
        MainActivity.handler.sendMessage(msg);
        Log.v("BOSHUAI", "changeMenuState on DirListAdapter");
    }





    private Object getPhoneUri(String filePath)
    {
        Object uri = null;
        String extName = FileUnit.getExtName(filePath);

        if(FileUnit.getType(filePath).equals("dir"))
        {
            if (FileUnit.getPreDir(filePath).equals(FileUnit.SD_ROOT) && FileUnit.getType(filePath).equals("dir"))
            {
                uri = R.drawable.file_type_sdcard;
            }
            else
            {
                uri =  R.drawable.file_type_dir;
            }
        }
        else
        {
            if (extName.equals("txt"))
            {
                uri =   R.drawable.file_type_txt;
            } else if (extName.equals("zip") || extName.equals("rar") || extName.equals("gz"))
            {
                uri =  R.drawable.file_type_rar;
            } else if (extName.equals("xls") || extName.equals("xlsx"))
            {
                uri = R.drawable.file_type_xls;
            } else if (extName.equals("doc") || extName.equals("docx"))
            {
                uri =  R.drawable.file_type_word;
            } else if (extName.equals("mp3") || extName.equals("ape") || extName.equals("oog") || extName.equals("flac"))
            {
                uri = R.drawable.file_type_music;
            } else if (extName.equals("mp4") || extName.equals("avi") || extName.equals("wmv") || extName.equals("rmvb") || extName.equals("rm") || extName.equals("mkv"))
            {
                uri =   R.drawable.file_type_video;
            } else if (extName.equals("pdf"))
            {
                uri = R.drawable.file_type_pdf;
            } else if (extName.equals("ppt") || extName.equals("pptx"))
            {
                uri =  R.drawable.file_type_ppt;
            } else if (extName.equals("exe"))
            {
                uri = R.drawable.file_type_exe;
            } else if (extName.equals("dll"))
            {
                uri =  R.drawable.file_type_dll;
            } else if (extName.equals("sdcard"))
            {
                uri =  R.drawable.file_type_sdcard;
            } else if (extName.equals("hdisk"))
            {
                uri =  R.drawable.file_type_hdisk;
            } else if (extName.equals("dir"))
            {
                uri = R.drawable.file_type_dir;
            } else if (extName.equals("jpg") || extName.equals("png") || extName.equals("bmp") || extName.equals("gif"))
            {
                //  uri = "file://" + filePath;
                uri = Uri.parse("file://" + filePath);
            }
            else if(extName.equals("apk"))
            {
                uri = R.drawable.file_type_apk;
            }
            else
            {
                uri = R.drawable.file_type_unknow;
            }
        }
        return uri;
    }



    private Object getComputerUri(String type, String extName)
    {
        Object uri = null;


        if(type.equals("h"))
        {
            uri = R.drawable.file_type_hdisk;


        }
        else if(type.equals("d"))
        {
            uri =  R.drawable.file_type_dir;
        }
        else
        {
            if (extName.equals("txt"))
            {
                uri =   R.drawable.file_type_txt;
            } else if (extName.equals("zip") || extName.equals("rar") || extName.equals("gz"))
            {
                uri =  R.drawable.file_type_rar;
            } else if (extName.equals("xls") || extName.equals("xlsx"))
            {
                uri = R.drawable.file_type_xls;
            } else if (extName.equals("doc") || extName.equals("docx"))
            {
                uri =  R.drawable.file_type_word;
            } else if (extName.equals("mp3") || extName.equals("ape") || extName.equals("oog")  || extName.equals("flac"))
            {
                uri = R.drawable.file_type_music;
            } else if (extName.equals("mp4") || extName.equals("avi") || extName.equals("wmv") || extName.equals("rmvb") || extName.equals("rm") || extName.equals("mkv"))
            {
                uri =   R.drawable.file_type_video;
            } else if (extName.equals("pdf"))
            {
                uri = R.drawable.file_type_pdf;
            } else if (extName.equals("ppt") || extName.equals("pptx"))
            {
                uri =  R.drawable.file_type_ppt;
            } else if (extName.equals("exe"))
            {
                uri = R.drawable.file_type_exe;
            } else if (extName.equals("dll"))
            {
                uri =  R.drawable.file_type_dll;
            } else if (extName.equals("sdcard"))
            {
                uri =  R.drawable.file_type_sdcard;
            } else if (extName.equals("hdisk"))
            {
                uri =  R.drawable.file_type_hdisk;
            } else if (extName.equals("dir"))
            {
                uri = R.drawable.file_type_dir;
            } else if (extName.equals("jpg") || extName.equals("png") || extName.equals("bmp") || extName.equals("gif"))
            {
                //  uri = "file://" + filePath;
                uri = R.drawable.file_type_image;
            }
            else if(extName.equals("apk"))
            {
                uri = R.drawable.file_type_apk;
            }
            else
            {
                uri = R.drawable.file_type_unknow;
            }
        }
        return uri;
    }

}
