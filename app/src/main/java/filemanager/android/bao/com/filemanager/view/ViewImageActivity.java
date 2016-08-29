package filemanager.android.bao.com.filemanager.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import filemanager.android.bao.com.filemanager.FileOperationTask;
import filemanager.android.bao.com.filemanager.R;

/**实现可以浏览文件
 * 浏览给定目录下的所有图片
 * Created by baobao on 16-3-24.
 */
public class ViewImageActivity extends FragmentActivity {

    public static final String FILE = "ViewImageActivity.FILE";
    private ViewPager viewPager;
    private List<String> files;
    private String file;
    private File parentFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_viewimage);
        Intent intent = getIntent();
        file = intent.getStringExtra(FILE);
        System.out.println("ViewImageActivity----->"+file);
        files = new ArrayList<>();
        if (file == null){
        }else {
            File f = new File(file);
            parentFile = f.getParentFile();
            String[] list = parentFile.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    if(filename.endsWith(".jpg") || filename.endsWith(".png")|| filename.endsWith(".jpeg")
                            || filename.endsWith(".jpe")|| filename.endsWith(".bmp")|filename.endsWith(".gif")){
                        return true;
                    }
                    return false;
                }
            });
            files.add(f.getName());
            for (String s:list){
                if (!files.contains(s)){
                    files.add(s);
                }
            }
        }
        viewPager = (ViewPager)findViewById(R.id.viewpager_viewimage);
        viewPager.setAdapter(new ImageViewAdapter());
        ViewImageActivity.this.setTitle(files.get(0));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                    ViewImageActivity.this.setTitle(files.get(viewPager.getCurrentItem()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_picture:
                break;
        }

        return true;
    }

    private class ImageViewAdapter extends FragmentStatePagerAdapter{

        public ImageViewAdapter(){
            super(getSupportFragmentManager());
        }


        @Override
        public Fragment getItem(int position) {
            ViewImageFragment fragment = new ViewImageFragment(parentFile+"/"+files.get(position));
            return fragment;
        }

        @Override
        public int getCount() {
            return files.size();
        }

    }
}
