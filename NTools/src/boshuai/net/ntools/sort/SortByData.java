package boshuai.net.ntools.sort;

import java.io.File;
import java.util.Comparator;

/**
 * Created by pc on 2017/1/30 0030.
 */

public class SortByData implements Comparator
{
    private int sortType;
    public SortByData(int sortType)
    {
        if(sortType>0)
        {
            this.sortType = 1;
        }
        else if(sortType<0)
        {
            this.sortType = -1;
        }
        else
        {
            this.sortType = 0;
        }

    }
    @Override
    public int compare(Object o, Object t1)
    {
        int result = 0;
        File file1 = new File(o.toString());
        File file2 = new File(t1.toString());

        long date1 = file1.lastModified();

        long date2 = file2.lastModified();
        if(date1==date2)
        {
            result =  0;
        }
        else if(date1>date2)
        {
            result =  -1;
        }
        else
        {
            result =  1;
        }
        result = sortType*result;

        return result;
    }
}
