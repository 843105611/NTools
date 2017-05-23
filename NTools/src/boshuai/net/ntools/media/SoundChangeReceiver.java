package boshuai.net.ntools.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import boshuai.net.ntools.unit.Consts;

/**
 * Created by pc on 2017/1/21 0021.
 */

public class SoundChangeReceiver extends BroadcastReceiver
{
    MusicService musicService;

    public SoundChangeReceiver()
    {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
     String name = intent.getAction().toString();
        Log.i("BOSHUAI", "接收到:"+name);

        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))
        {
            Log.v("BOSHUAI", "out going call");
        }

        if(Consts.mService!=null)
        {
            Consts.mService.pause();
            Log.v("BOSHUAI", "pause music");
        }
        else
        {
            Log.v("BOSHUAI", "musicService is null  02");
        }
      }

}
