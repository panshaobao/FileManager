package filemanager.android.bao.com.filemanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import filemanager.android.bao.com.filemanager.bluetooth.ServerActitivty;
import filemanager.android.bao.com.filemanager.bluetooth.TranslateFileActivity;
import filemanager.android.bao.com.filemanager.media.MediaActivity;
import filemanager.android.bao.com.filemanager.media.PlayMusicFragment;
import filemanager.android.bao.com.filemanager.view.ViewImageActivity;

/**
 * 显示最近打开的文件
 * Created by baobao on 16-3-9.
 */
public class RecentFragment extends ListFragment {
    //文件名
    public static final String fileName="recent_file.json";
    public static final String SEND_FILE = "send_file";

    public static final int RECENTFRAGMENT_SENDFILE_REQUESTCODE=0;
    //
    private RecentFileLab recentFileLab;

    private FileAdapter adapter;

    public RecentFragment(){}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println("***** 调用onAttach() *******");
        recentFileLab=RecentFileLab.newInstance(getActivity());
        recentFileLab.loadRecentFiles(fileName);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //打开文件
        RecentFile recentFile =(RecentFile) adapter.getItem(position);
        File f = new File(recentFile.getPath());
        String path = f.getAbsolutePath();
        if(path.endsWith(".jpg") || path.endsWith(".png")|| path.endsWith(".jpeg")
                || path.endsWith(".jpe")|| path.endsWith(".bmp")|path.endsWith(".gif")){

            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            intent.putExtra(ViewImageActivity.FILE,path);
            startActivity(intent);

        }else if (path.endsWith(".mp3")) {
            Intent intent = new Intent(getActivity(), MediaActivity.class);
            intent.putExtra(PlayMusicFragment.MP3_PATH,path);
            startActivity(intent);
        }else {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setType("text/plain");
            intent.setData(Uri.fromFile(f.getAbsoluteFile()));
            Intent intent1;
            intent1 = Intent.createChooser(intent,"打开文件");
            startActivity(intent1);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //保持实例对象
        this.setRetainInstance(true);
        adapter = new FileAdapter(recentFileLab.getRecentFiles());
        setListAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ListView listView = getListView();
        registerForContextMenu(listView);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            getActivity().getMenuInflater().inflate(R.menu.recent_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.delete_recentfile:
                recentFileLab.deletedFile(info.position);
                recentFileLab.writeToFile(fileName);    //保存数据到文件
                adapter.notifyDataSetChanged();
                break;
            case R.id.delete_all:
                recentFileLab.deletedAll();
                recentFileLab.writeToFile(fileName);     //保存数据到文件
                adapter.notifyDataSetChanged();
                break;
            case R.id.send_file:
                Intent intent = new Intent(getActivity(), ServerActitivty.class);
                intent.putExtra(SEND_FILE,((RecentFile)adapter.getItem(info.position)).getPath());
                startActivityForResult(intent, RECENTFRAGMENT_SENDFILE_REQUESTCODE);
                break;
        }

        return true;
    }

    //每次回到这个界面，更新adapter的数据
    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public RecentFragment(Context context){
        super();

    }

    private class FileAdapter<RecentFile> extends ArrayAdapter<RecentFile>{

        public FileAdapter(List<RecentFile>list) {
            super(getActivity(), 0, list);

        }

        @Override
        public int getCount() {
            return recentFileLab.getRecentFiles().size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            if (convertView == null ){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_recentfragment_list,null);
            }
            ImageView imageView= (ImageView)convertView.findViewById(R.id.image_rencent);


            TextView textView =(TextView)convertView.findViewById(R.id.file_name_recent);
            filemanager.android.bao.com.filemanager.RecentFile file=
                    (filemanager.android.bao.com.filemanager.RecentFile) getItem(position);
            //设置标题
            textView.setText(file.toString());
            //设置图标

            if(file.getPath().endsWith(".jpg") || file.getPath().endsWith(".png")
                    || file.getPath().endsWith(".jpeg")
                    || file.getPath().endsWith(".jpe")||
                    file.getPath().endsWith(".bmp")|file.getPath().endsWith(".gif")){
                new BitmapWorkerTask(imageView).execute(file.getPath());
            }else {
                imageView.setImageResource(file.getId());
                System.out.println(".....直接加载图片.......");
            }

            return convertView;
        }
    }


}
