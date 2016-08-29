package filemanager.android.bao.com.filemanager.media;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import filemanager.android.bao.com.filemanager.R;

/**
 * Created by baobao on 16-3-25.
 */
public class MediaService extends Service  {

    private MediaPlayer player;

    private File[] lists;
    private int curent = 0;

    private MyBindler bindler = new MyBindler();

    private boolean isFirst =true ;

    public class MyBindler extends Binder{
        public MediaService getService(){
            return MediaService.this;
        }
    }

    public void setIsFirst(boolean is){
       isFirst = is;
    }

    public File[] getLists(){
        return lists ;
    }

    public int getCurent(){
        return curent ;
    }

    public File getCurrentFile(){
        if (isFirst){
            return firstFile ;
        }
        return lists[curent];
    }

    public void next(){
        System.out.println(lists.length);
        System.out.println(curent);
        if (curent<(lists.length-1)){
            curent +=1;
            player.reset();
            player.setOnCompletionListener(MediaActivity.playMusicFragment);
            player.setOnPreparedListener(MediaActivity.playMusicFragment);
            player.setOnSeekCompleteListener(MediaActivity.playMusicFragment);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                player.setDataSource(getApplicationContext(), Uri.fromFile(lists[curent]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.prepareAsync();
        }else {
            Toast.makeText(getApplicationContext(),"后面没有歌曲啦",Toast.LENGTH_SHORT).show();
        }
    }

    public void pre(){
        System.out.println(lists.length);
        System.out.println(curent);
        if (curent>0){
            curent -=1;
            player.reset();
            player.setOnCompletionListener(MediaActivity.playMusicFragment);
            player.setOnPreparedListener(MediaActivity.playMusicFragment);
            player.setOnSeekCompleteListener(MediaActivity.playMusicFragment);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                player.setDataSource(getApplicationContext(), Uri.fromFile(lists[curent]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.prepareAsync();
        }else {
            Toast.makeText(getApplicationContext(),"前面没有歌曲啦",Toast.LENGTH_SHORT).show();
        }

    }

    public void stop(){
        player.stop();
    }

    public void start(){
        player.start();
    }

    public boolean pause(){
        if (player.isPlaying()){
            player.pause();
            return true;
        }
        return false;
    }

    private File firstFile;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("onBind");
        String file = intent.getStringExtra(PlayMusicFragment.MP3_PATH);
        lists = FileFactory.getFiles(file);
        firstFile = new File(file);
        Uri uri = Uri.fromFile(firstFile);
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnSeekCompleteListener(MediaActivity.playMusicFragment);
        try {
            player.setDataSource(getApplicationContext(),uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setOnCompletionListener(MediaActivity.playMusicFragment);
        player.setOnPreparedListener(MediaActivity.playMusicFragment);
        player.prepareAsync();

        return bindler;
    }

    private void initForground(){
        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(),0,
                new Intent(getApplicationContext(),MediaActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification= new Notification.Builder(getApplicationContext())
                .setContentTitle("myMusic")
                .setContentText("正在播放ing...")
                .setSmallIcon(R.drawable.list_icon_playing)
                .build();
        startForeground(200, notification);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onCreate");
        initForground();
    }

    public int getPositon(){
        return player.getCurrentPosition();
    }

    public int getDuration(){
        return player.getDuration();
    }

    public void seekto(int position){
        player.seekTo(position);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
        player.release();
        player = null ;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("onUnbind");
        MediaActivity.playMusicFragment.handler.removeCallbacks( MediaActivity.playMusicFragment.refreshRun);
        return super.onUnbind(intent);
    }


}
