package filemanager.android.bao.com.filemanager.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import filemanager.android.bao.com.filemanager.R;

/**播放界面
 * Created by baobao on 16-3-25.
 */
public class PlayMusicFragment extends Fragment implements View.OnClickListener,MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener{
    public static final String MP3_PATH="mpa3_path";
    private Intent intent ;
    private  MediaService mediaService;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("onServiceConnected");
            mediaService = ((MediaService.MyBindler)service).getService();
            if (mediaService == null){
                System.out.println("没有值");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("onServiceDisconnected");
            mediaService = null;
        }
    };

    private ImageButton playStop;
    private SeekBar seekBar;
    private TextView currentDuration;
    private TextView totallDuration;
    private TextView songName ;
    private ImageButton ctrol;
    private ImageButton pre ;
    private ImageButton next ;

    private boolean isDrag =false ;
    private boolean isPaused = false ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_playmusic_media,null);
        playStop = (ImageButton)view.findViewById(R.id.play_stop);
        playStop.setOnClickListener(this);
        seekBar = (SeekBar)view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isDrag){
                    mediaService.seekto(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDrag =true ;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDrag =false ;
            }
        });
        currentDuration = (TextView)view.findViewById(R.id.curent_duration);
        totallDuration = (TextView)view.findViewById(R.id.totall_duration);
        songName = (TextView)view.findViewById(R.id.song_name);
        ctrol = (ImageButton)view.findViewById(R.id.control);
        ctrol.setOnClickListener(this);
        pre = (ImageButton)view.findViewById(R.id.pre);
        pre.setOnClickListener(this);
        next = (ImageButton)view.findViewById(R.id.next);
        next.setOnClickListener(this);
        return view ;
    }

    private boolean isBind = false ;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent(getActivity(),MediaService.class);
        String file = getActivity().getIntent().getStringExtra(MP3_PATH);
        intent.putExtra(MP3_PATH, file);
        if (getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE))
            isBind = true;
        else isBind = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_stop:
                if (isPaused){
                    //重新播放
                    isPaused = false ;
                    mediaService.start();
                    handler.post(refreshRun);
                    playStop.setImageResource(R.drawable.btn__player);
                }else {
                    //暂停
                    isPaused = true ;
                    mediaService.pause();
                    handler.removeCallbacks(refreshRun);
                    playStop.setImageResource(R.drawable.btn_stop);
                }
                break;
            case R.id.pre:
                mediaService.setIsFirst(false);
                mediaService.pre();
                break;
            case R.id.next:
                mediaService.setIsFirst(false);
                mediaService.next();
                break;
            case R.id.control:

                break;
        }
    }

     Runnable refreshRun = new Runnable() {
        @Override
        public void run() {
            //分
            double currentFen = ((double)mediaService.getPositon())/1000/60;
            //秒的十位
            double currentMiaoShi = (currentFen -(int)currentFen)*60/10;
            //秒的个位
            double currentMiaoGe =  (currentFen -(int)currentFen)*60%10;
            currentDuration.setText(String.valueOf((int)currentFen)+":"+String.valueOf((int)currentMiaoShi)+String.valueOf((int)currentMiaoGe));
            seekBar.setProgress(mediaService.getPositon());
            handler.postDelayed(refreshRun, 200);
            System.out.println("Run.......");
        }
    };

     Handler handler = new Handler();

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaService.setIsFirst(false);
        //当播放完一首歌的时候会调用这个方法
        //可以在这个设置播放下一首
        handler.removeCallbacks(refreshRun);
        mediaService.next();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //准备好后会调用这个方法
        songName.setText(mediaService.getCurrentFile().getName());
        //分
        double totalFen = ((double)mediaService.getDuration())/1000/60;
        //秒的十位
        double totalMiaoShi = (totalFen - (int) totalFen)*60/10;
        //秒的个位
        double totalMiaoGe = (totalFen - (int) totalFen)*60%10;

        seekBar.setMax(mediaService.getDuration());
        seekBar.setProgress(mediaService.getPositon());
        totallDuration.setText(String.valueOf((int)totalFen)+":"+String.valueOf((int)totalMiaoShi)+String.valueOf((int)totalMiaoGe));
        mp.start();
        handler.post(refreshRun);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        seekBar.setProgress(mp.getCurrentPosition());
        //分
        double currentFen = mediaService.getPositon()/1000/60;
        //秒的十位
        double currentMiaoShi = (currentFen-(int)currentFen)*60/10;
        //秒的个位
        double currentMiaoGe =  (currentFen-(int)currentFen)*60%10;
        currentDuration.setText(String.valueOf(currentFen)+":"+String.valueOf(currentMiaoShi)+String.valueOf(currentMiaoGe));
    }


}
