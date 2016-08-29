package filemanager.android.bao.com.filemanager.media;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import filemanager.android.bao.com.filemanager.R;


/**播放音频
 * Created by baobao on 16-3-25.
 */
public class MediaActivity extends FragmentActivity {

    private ViewPager viewPager;
    public static PlayMusicFragment playMusicFragment= new PlayMusicFragment();
    public static Mp3ListFragment mp3ListFragment= new Mp3ListFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        viewPager = (ViewPager)findViewById(R.id.viewpager_media);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0){
                    return playMusicFragment;
                }else {
                    return mp3ListFragment;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
    }


}
