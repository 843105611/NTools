package boshuai.net.ntools.media;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import boshuai.net.ntools.R;
import boshuai.net.ntools.unit.Consts;
import boshuai.net.ntools.unit.FileUnit;
import boshuai.net.ntools.unit.Msg;

/**
 * Created by Administrator on 2017/1/20.
 */

public class MediaPlayActivity extends Activity implements View.OnClickListener
{
    static boolean mIsPause = false;
    MusicService mService;
    ListView mListview;
    Button mBtnPre, mBtnNext, mBtnPlayPause;
    TextView mTextViewMusicInfo, mTextViewMusicCurrentTime, mTextViewMusicTotalTime;
    SeekBar mSeekBar, mSeekVolume;
    Intent mIntentMusicService;
    Handler handler;
    static String musicPath, oldMusicPath;
    static int mPosition = 0;
    static boolean mIsExitActivity;
    SoundChangeReceiver soundChangeReceiver;


    private boolean mResumeAfterCall = false;
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener()
    {
        @Override
        public void onCallStateChanged(int state, String incomingNumber)
        {
            if (state == TelephonyManager.CALL_STATE_RINGING)
            {
                if (mService != null)
                {
                       mService.pause();
                }
            }
            else if (state == TelephonyManager.CALL_STATE_OFFHOOK)
            {
                if (mService != null)
                {
                    mService.pause();
                }
            }
            else if (state == TelephonyManager.CALL_STATE_IDLE)
            {
                if (mService != null  && mIsPause==false)
                {
                    mService.start();

                }
            }

        }
    };

           @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_play);


        TelephonyManager tmgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tmgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);


        mTextViewMusicInfo = (TextView)findViewById(R.id.tv_music_info);
        mTextViewMusicCurrentTime = (TextView)findViewById(R.id.tv_music_current_time);
        mTextViewMusicTotalTime = (TextView)findViewById(R.id.tv_music_total_time);


        mSeekBar = (SeekBar)findViewById(R.id.seekBarMusic);
        mSeekBar.setOnSeekBarChangeListener(seeklistener);

        mSeekVolume = (VerticalSeekBar)findViewById(R.id.seekBar_music_volume);

        mSeekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                mService.setVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        mBtnPre = (Button)findViewById(R.id.btn_pre_music);
        mBtnPre.setOnClickListener(this);

        mBtnPlayPause = (Button)findViewById(R.id.btn_play_pause);
        mBtnPlayPause.setOnClickListener(this);;

        mBtnNext = (Button)findViewById(R.id.btn_next_muisic);
        mBtnNext.setOnClickListener(this);

        mListview = (ListView)findViewById(R.id.music_listview);

        mIntentMusicService = new Intent(this, MusicService.class);
        startService(mIntentMusicService);
        bindService(mIntentMusicService, sconnection, Context.BIND_AUTO_CREATE);

        musicPath = getIntent().getStringExtra("music_info");
        Log.v("BOSHUAI", "musicPath:" + musicPath);



        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                switch(msg.what){
                    case Msg.MUSIC_SERVICE_IS_READY:
                        startPlay();
                        break;

                    case Msg.MUSIC_CHANGE_POSITION:
                        autoChangeProcess();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    private void autoChangeProcess()
    {
        mSeekBar.setProgress(mPosition);
        setCurrentTime(mPosition, mTextViewMusicCurrentTime);
    }

    @Override
    public void onBackPressed()
    {
        mIsExitActivity = true;
        super.onBackPressed();
    }

    class ThreadPlay extends  Thread
    {
        @Override
        public void run()
        {
            while(true)
            {
                if(mIsExitActivity)
                    break;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mPosition = mService.getlocation();
             //   Log.v("BOSHUAI", "now postition:" + mPosition);
                Message msg = new Message();
                msg.what = Msg.MUSIC_CHANGE_POSITION;
                handler.sendMessage(msg);
                super.run();
            }
            Log.v("BOSHUAI", "ThreadPlay is exit");
        }
    }



    @Override
    public void onClick(View v)
    {
        if(mService==null)
        {
            return;
        }

        if(v==mBtnPlayPause)
        {
            if(mBtnPlayPause.getText().toString().trim().equals("暂停"))
            {
                mService.pause();
                mIsPause = true;
                mBtnPlayPause.setText("播放");
            }
            else
            {
                mService.start();
                mIsPause = false;
                mBtnPlayPause.setText("暂停");
            }
        }
    }

    private void setCurrentTime(int position, TextView tv)
    {
        int hour, minute, second;
        hour = position /1000/3600;
        minute = position/1000/60;
        second = (position /1000) % 60;
        String currentTime = "";
        if(hour>0)
        {
            currentTime = String.format("%02d:%02d:%02d", hour, minute, second);
        }
        else
        {
            currentTime = String.format("%02d:%02d", minute, second);
        }
        tv.setText(currentTime);
    }

    private void setMusicInfo()
    {
        String totalTime;
        int length = mService.getsize();
        mSeekBar.setMax(length);
        Log.v("BOSHUAI", "music length: " + length);

        setCurrentTime(length, mTextViewMusicTotalTime);
        setCurrentTime(0, mTextViewMusicCurrentTime);

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try
        {
            mmr.setDataSource(musicPath);
            String author = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

            String info;
            if(artist!=null && title!=null)
            {
                info = artist + " -- " + title;
            }
            else if(author!=null && title!=null)
            {
                info = author + " -- " + title;
            }
            else
            {
                info = FileUnit.getFileName(musicPath);
            }
            mTextViewMusicInfo.setText(info);

        }
        catch (Exception e)
        {

        }

    }

    private void startPlay()
    {
        if(mService!=null)
        {
            mIsExitActivity = false;
            oldMusicPath = musicPath;

            mService.setPath(musicPath);
            mService.start();
            Thread thread = new ThreadPlay();
            thread.start();
            setMusicInfo();

            int maxVolume = mService.getMaxVolume();
            Log.v("BOSHUAI", "max volume: " + maxVolume);
            mSeekVolume.setMax(maxVolume);
            mService.setVolume(maxVolume/2);
            mSeekVolume.setProgress(maxVolume);
        }
    }


    SeekBar.OnSeekBarChangeListener seeklistener = new SeekBar.OnSeekBarChangeListener()
    {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            if(!fromUser) return;
            if(mService!=null)
                mService.seekto(progress);
        }

        public void onStartTrackingTouch(SeekBar seekBar)
        {
            // TODO Auto-generated method stub
        }

        public void onStopTrackingTouch(SeekBar seekBar)
        {
            // TODO Auto-generated method stub
        }
    };

    private ServiceConnection sconnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            Log.v("BOSHUAI", "now is connecting service");
            mService = ((MusicService.Localbinder)service).getService();
            if(mService!=null)
            {
              //  soundChangeReceiver = new SoundChangeReceiver(mService);
                Consts.mService = mService;
                if(musicPath.equals(oldMusicPath))
                {
                    if(mPosition>0)
                    {
                        mIsExitActivity = false;
                        Thread thread = new ThreadPlay();
                        thread.start();
                        setMusicInfo();
                    }
                        return;
                }

                Message msg = new Message();
                msg.what = Msg.MUSIC_SERVICE_IS_READY;
                handler.sendMessage(msg);
            }
        }
        public void onServiceDisconnected(ComponentName name)
        {
        }
    };
}
