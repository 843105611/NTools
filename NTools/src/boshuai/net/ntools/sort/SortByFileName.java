package boshuai.net.ntools.sort;

import java.util.Comparator;

/**
 * Created by pc on 2017/1/30 0030.
 */

public class SortByFileName implements Comparator
{
    private int sortType = 0;

    public SortByFileName(int sortType)
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
        String name1 = o.toString();
        String name2 = t1.toString();

        if(name1.compareTo(name2)>0)
        {
            result =  1;
        }
        else if(name1.compareTo(name2)<0)
        {
            result =  -1;
        }
        else
        {
            result = 0;
        }

        return result*sortType;
    }
}
