package boshuai.net.ntools.media;


import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MusicService extends Service
{
    MediaPlayer m;
    AudioManager am;

    public class Localbinder extends Binder
    {
        MusicService getService()
        {
            return MusicService.this;
        }
    }
    Localbinder mbinder = new Localbinder();

    public IBinder onBind(Intent intent)
    {
        return mbinder;
    }



    @Override
    public void onCreate()
    {
        m = new MediaPlayer();
        m.setOnCompletionListener(listener);
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        super.onCreate();
    }
    OnCompletionListener listener = new OnCompletionListener()
    {
        String BROADCAST ="com.myAction.receiver";
        public void onCompletion(MediaPlayer mp)
        {
            Intent intent =new Intent();
            intent.setAction(BROADCAST);
            intent.putExtra("key", "stop");
            MusicService.this.sendBroadcast(intent);
        }

    };
    public void setPath(String path)
    {

        try {
            if(m==null)
            {
                m = new MediaPlayer();
            }
            if(m.isPlaying())
            {
                m.stop();
            }

            m.reset();
            m.setDataSource(path);
            m.prepare();
            m.start();
        } catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public int getsize()
    {
        return m.getDuration();
    }
    public int getlocation()
    {
        return m.getCurrentPosition();
    }
    public void seekto(int position)
    {

        m.seekTo(position);
    }
    public void start()
    {

        if(m.isPlaying())
        {

        }else
        {
            m.start();
        }

    }
    public void stop()
    {
        m.stop();
    }
    public void pause()
    {
        m.pause();
    }
    public void destory()
    {
        m.release();
        m=null;
    }

    public void setVolume(int volume)
    {
        ///am.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        if(am!=null)
        {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        }
    }

    public int getVolume()
    {
        if(am!=null)
        {
            return am.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    public int getMaxVolume()
    {
        if(am!=null)
        {
            return am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

}