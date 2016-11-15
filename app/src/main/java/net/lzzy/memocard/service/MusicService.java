package net.lzzy.memocard.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import net.lzzy.memocard.R;

/**
 * Created by Administrator on 2016/6/2.
 * 播放音乐
 */
public class MusicService extends Service {
    private MediaPlayer player;


    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.ten);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopSelf();
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

   public class MusicBinder extends Binder {
        public void playMusic() {
            if (!player.isPlaying())
                player.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player.isPlaying())
            player.stop();
        player.release();
    }
}
