package boshuai.net.ntools.sort;

import java.util.Comparator;

import boshuai.net.ntools.unit.FileUnit;

/**
 * Created by Administrator on 2017/2/3.
 */

public class SortByType implements Comparator
{
    @Override
    public int compare(Object o, Object t1)
    {
        String fileName1 = (String)o;
        String fileName2 = (String)t1;

        if(FileUnit.isDir(fileName1) && FileUnit.isDir(fileName2))
        {
            return 0;
        }
        else if(FileUnit.isDir(fileName1) && !FileUnit.isDir(fileName2))
        {
            return -1;
        }
        else if(!FileUnit.isDir(fileName1) && FileUnit.isDir(fileName2))
        {
            return 1;
        }

        return 0;
    }
}
