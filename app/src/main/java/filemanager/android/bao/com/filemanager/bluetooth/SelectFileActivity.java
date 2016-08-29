package filemanager.android.bao.com.filemanager.bluetooth;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.io.File;

import filemanager.android.bao.com.filemanager.FileFragment;
import filemanager.android.bao.com.filemanager.R;

/**
 * Created by baobao on 16-3-22.
 */

public class SelectFileActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);


        FragmentManager manager = getSupportFragmentManager();
//        manager.beginTransaction().add()
    }

}
