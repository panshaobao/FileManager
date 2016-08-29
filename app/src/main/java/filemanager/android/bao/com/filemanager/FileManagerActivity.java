package filemanager.android.bao.com.filemanager;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.DataOutputStream;

import filemanager.android.bao.com.filemanager.bluetooth.TranslateFileActivity;

//程序的入口类
public class FileManagerActivity extends FragmentActivity {

    //滚动视图
    private ViewPager viewPager =null;
    private MyAdapter myAdapter;
    private PagerTitleStrip pagerTitleStrip;


    public void	onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_file_manager);

        //初始化系统状态栏
        initStateBar();
        //初始化ViewPager
        initViewPager();

        initDrawLayoutLeft();

    }

    private TextView translateFile;
    //初始化隐藏面板的控件
    private void initDrawLayoutLeft(){
        translateFile = (TextView)findViewById(R.id.translate_file);
        translateFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("******** 启动文件传输 *********");
                //启动文件传输界面
                Intent intent = new Intent(FileManagerActivity.this,TranslateFileActivity.class);
                startActivity(intent);

            }
        });
    }

    private void initViewPager(){

        pagerTitleStrip =(PagerTitleStrip)findViewById(R.id.pager_title_strip);

        viewPager=(ViewPager)findViewById(R.id.pager);
        myAdapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }


    //ViewPager的适配器
    private class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                //第一个页面显示
                return new RecentFragment(FileManagerActivity.this);
            }else if (position == 1){
                //第二个页面
                return new StoregeFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //这个方法会返回每个ViewPager页面的标题
            if(position == 0){
                return "最近打开";
            }else if (position == 1){
                return "存储";
            }

            return "";
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){

            new AlertDialog.Builder(this)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FileManagerActivity.this.finish();
                            System.exit(0);
                        }
                    })
                    .setMessage("退出程序？")
                    .setNegativeButton("取消",null)
                    .show();

            return true;
        }

        return false;
    }

    //初始化系统状态栏，设置为透明
    private void initStateBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setLogo(null);
        actionBar.setIcon(null);
        actionBar.setTitle(null);
    }


}
