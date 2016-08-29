package filemanager.android.bao.com.filemanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;

import filemanager.android.bao.com.filemanager.dialog.SortDialog;
import filemanager.android.bao.com.filemanager.dialog.StaticDialog;
import filemanager.android.bao.com.filemanager.media.MediaActivity;
import filemanager.android.bao.com.filemanager.media.PlayMusicFragment;
import filemanager.android.bao.com.filemanager.view.ViewImageActivity;
/**
 * 查看手机的外部存储器
 * Created by baobao on 16-3-9.
 */
public class StoregeFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static int NORMAL=0;//默认的排序,目录排在前面,文件排在后面,有中文字符的排在前面,按A~Z顺序排列
    public static int TYPE1=1;//文件排在前面,目录排在后面,有中文字符的排在前面,按A~Z顺序排列
    public static int TYPE2=2;//目录排在前面,文件排在后面,有中文的排在前面,按Z~A顺序排列
    public static int TYPE3=3;//文件排在前面,目录排在后面,有中文的排在前面,按Z~A顺序排列

    private int type=0;

    public void setType(int type){
        this.type=type;
    }

    public int getType(){
        return this.type;
    }

    //显示外部存储器
    private ListView listView;

    public static final int CUT = 10;
    public static final int COPY = 11;
//    public static final int RENAME = 12;
    public static final int DELETE = 13;

    //被管理的filefragment的适配器
    public FileFragment.ImageAdapter imageAdapter;

    public ListFileFragment.MyAdapter myAdapter;

    //代表当前的操作是什么
    public int operation=0;

    //用来显示文件的fragment
    private FileFragment fileFragment;
    private AdapterView.OnItemClickListener listener;

    //确定，取消文件操作面板，默认下不可见
    private LinearLayout sureCancelLayout;

    public StoregeFragment(){
        super();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (isList){
//            myAdapter.notifyDataSetChanged();
//        }else {
//            imageAdapter.notifyDataSetChanged();
//        }
        addFragment(currentPath);
    }

    //在这里管理filefragment的点击事件
    public AdapterView.OnItemClickListener getItemClickListener() {
        listener = new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String path = (String)adapterView.getItemAtPosition(i);
                File file = new File(path);
                if(file.isDirectory()){
                    //是目录就进入目录
                    addFragment(path);
                    System.out.println(path);
                    System.out.println(currentPath);
                }else {
                    //记录浏览文件
                    RecentFileLab.newInstance(getActivity()).addFile(new RecentFile(path));
                    RecentFileLab.newInstance(getActivity()).writeToFile(RecentFragment.fileName);
                    //是文件就打开文件
                    if(path.endsWith(".jpg") || path.endsWith(".png")|| path.endsWith(".jpeg")
                            || path.endsWith(".jpe")|| path.endsWith(".bmp")|path.endsWith(".gif")){

                        Intent intent = new Intent(getActivity(), ViewImageActivity.class);
                        intent.putExtra(ViewImageActivity.FILE,path);
                        startActivityForResult(intent, 0);
                    }else if (path.endsWith(".mp3")) {
                        Intent intent = new Intent(getActivity(), MediaActivity.class);
                        intent.putExtra(PlayMusicFragment.MP3_PATH,path);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setType("text/plain");
                        intent.setData(Uri.fromFile(file));
                        Intent intent1;
                        intent1 = Intent.createChooser(intent,"打开文件");
                        startActivity(intent1);
                    }

                }
            }
        };


        return listener;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //当前filefragment所在路径
    private String currentPath;

    public String getCurrentPath(){
        return currentPath;
    }

    //当前所操作的文件
    private String fileName;

    private ListFileFragment listFileFragment;

    //判断是列表显示还是网格显示,false表示列表显示
    public boolean isList =true;
    //用来更新不同目录下文件的显示
    //参数为文件的目录
    public void addFragment(String path){
        currentPath = path;
        FragmentManager fm = getFragmentManager();
        if (isList){
           listFileFragment = ListFileFragment.newInstance(path,this);
            fm.beginTransaction()
                    .replace(R.id.filefragment_container,listFileFragment)
                    .commit();
        }else {
//            fileFragment = (FileFragment)fm.findFragmentById(R.id.filefragment_container);
            fileFragment = FileFragment.newInstance(path,this);
            fm.beginTransaction()
                    .replace(R.id.filefragment_container, fileFragment)
                    .commit();
        }


    }
    //表示当前是否隐藏文件
    public boolean isHide=true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }


    //默认打开的目录
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        List<String> files = getExStorage();
        if (files.size()>0){
            isList=true;
            addFragment(files.get(0));
        }

    }

//    private PopupWindow window;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.storage_fragment, null);
        sureCancelLayout = (LinearLayout)view.findViewById(R.id.sure_cancel);
        Button sureButton =(Button) view.findViewById(R.id.sure);
        Button cancelButton = (Button)view.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sureCancelLayout.setVisibility(View.GONE);
            }
        });

        sureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sureCancelLayout.setVisibility(View.GONE);
                switch (operation) {
                    case COPY:
                        sureCancelLayout.setVisibility(View.GONE);
                        //添加data不然会程序崩溃
                        if (!isList){
                            imageAdapter.add(new String(currentPath + "/" + fileName));
                            imageAdapter.refresh(fileFragment.getFiles());
                        }else {
                            myAdapter.add(new String((currentPath + "/" + fileName)));
                            myAdapter.refresh(listFileFragment.getFiles());
                        }
                        new FileOperationTask(StoregeFragment.this,FileOperationTask.COPY).execute(currentPath,fileName);
                        break;
                    case CUT:
                        sureCancelLayout.setVisibility(View.GONE);
                        //添加data不然会程序崩溃
                        if (!isList){
                            imageAdapter.add(new String(currentPath + "/" + fileName));
                            imageAdapter.refresh(fileFragment.getFiles());
                        }else {
                            myAdapter.add(new String((currentPath + "/" + fileName)));
                            myAdapter.refresh(listFileFragment.getFiles());
                        }
                        new FileOperationTask(StoregeFragment.this,FileOperationTask.CUT).execute(currentPath, fileName);
                        break;
                    case DELETE:
                        sureCancelLayout.setVisibility(View.GONE);
                        if (!isList){
                            imageAdapter.remove(new String(currentPath + "/" + fileName));
                            imageAdapter.refresh(fileFragment.getFiles());
                        }else {
                            myAdapter.remove(new String((currentPath+"/"+fileName)));
                            myAdapter.refresh(listFileFragment.getFiles());
                        }
                        new FileOperationTask(StoregeFragment.this,FileOperationTask.DELETE).execute(null, fileName);
                        break;
                }

            }

        });

        listView =(ListView)view.findViewById(R.id.storage_list);
        //装载数据
        StorageAdapter adapter = new StorageAdapter(getExStorage());
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
        MenuItem item = menu.getItem(2);
        if (isList){
            item.setTitle("网格显示");
        }else {
            item.setTitle("列表显示");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.up_path:
                for (String path:getExStorage()){
                    if (currentPath.equals(path)){
                        return true;
                    }
                }
                String parent = new File(currentPath).getParent();
                addFragment(parent);
                break;
            case R.id.show_as_listview:
                if (isList){
                    //点击将改为方格显示
                    item.setTitle("列表显示");
                    isList = false;
                    addFragment(currentPath);
                }else {
                    //点击将改为列表显示
                    item.setTitle("网格显示");
                    isList = true;
                    addFragment(currentPath);
                }
                break;
            case R.id.hide:
                if (isHide){
                    item.setTitle("不显示隐藏文件");
                    isHide = false;
                    addFragment(currentPath);
                }else {
                    item.setTitle("显示隐藏文件");
                    isHide=true;
                    addFragment(currentPath);
                }
                break;
            case R.id.mkdir:
                mkdir();
                break;
            case R.id.sort:
                new SortDialog(getActivity(),this).show();

                break;

        }

        return true;
    }


    //点击存储设备时进行的操作
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int positon, long l) {
        String path=null;
        path=(String)adapterView.getItemAtPosition(positon);
        addFragment(path);
    }

    public AdapterView.OnItemLongClickListener getItemLongClickListener() {
        AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String path = (String)adapterView.getItemAtPosition(i);
                PopupMenu popupMenu = new PopupMenu(getActivity(),view);
                popupMenu.inflate(R.menu.popup_menu_filefragment);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.delete_popupmenu:
                                sureCancelLayout.setVisibility(View.VISIBLE);
                                operation = DELETE;
                                fileName = path;
                                break;
                            case R.id.cut_popupmenu:
                                operation = CUT;
                                sureCancelLayout.setVisibility(View.VISIBLE);
                                fileName = path;
                                break;
                            case R.id.copy_popupmenu:
                                operation = COPY;
                                sureCancelLayout.setVisibility(View.VISIBLE);
                                fileName = path;
                                break;
                            case R.id.rename_popupmenu:
                                View view1 = getActivity().getLayoutInflater().inflate(R.layout.rename, null);
                                final EditText editText = (EditText) view1.findViewById(R.id.rename_edittext);
                                new AlertDialog.Builder(getActivity())
//                                        .setIcon(R.drawable.icon)
                                        .setView(view1)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                File file = new File(path);
                                                if (!file.renameTo(new File(currentPath + "/" + editText.getText().toString()))) {
                                                    Toast.makeText(getActivity(), "重命名失败", Toast.LENGTH_SHORT).show();
                                                }
                                                addFragment(currentPath);
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })
                                        .setTitle("重命名").show();
                                break;
                        }

                        return true;
                    }
                });
                return true;
            }
        };
        return longClickListener;
    }

    private void mkdir(){
        View view2 = getActivity().getLayoutInflater().inflate(R.layout.rename, null);
        final EditText editText2 = (EditText) view2.findViewById(R.id.rename_edittext);

        new AlertDialog.Builder(getActivity())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = editText2.getText().toString();
                        if (name != null && !name.equals("")) {
                            File file = new File(currentPath + "/" + name);
                            if (file.mkdir()){
                                Toast.makeText(getActivity(),"创建成功",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getActivity(),"创建失败",Toast.LENGTH_SHORT).show();
                            }
                            addFragment(currentPath);
                        } else {
                            Toast.makeText(getActivity(),"文件名不能为空",Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setTitle("新建文件夹")
                .setView(view2)
                .show();
    }


    private class StorageAdapter extends ArrayAdapter<String>{

        public StorageAdapter(List<String> list){
            super(getActivity(), 0,list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           if (convertView == null){
               convertView = getActivity().getLayoutInflater().inflate(R.layout.storage_list_item,null);
          }
            ImageView imageView = (ImageView)convertView.findViewById(R.id.image_sd);
            TextView textView =(TextView)convertView.findViewById(R.id.storage_path);
            TextView showSpace = (TextView)convertView.findViewById(R.id.show_space);

            String apath = getItem(position);
            File file = new File(apath);
            if (apath.endsWith("0")){
                imageView.setImageResource(R.drawable.sd0);
            }else {

                imageView.setImageResource(R.drawable.sd1);
            }
            textView.setText(file.getName());
            ProgressBar progressBar=(ProgressBar)convertView.findViewById(R.id.storage_progressbar);
            StatFs statFs = new StatFs(file.getAbsolutePath());
            double total = statFs.getTotalBytes()/(1024d*1024d*1024d);
            double free = statFs.getFreeBytes()/(1024d*1024d*1024d);
            double hasUse=total-free;
            progressBar.setMax((int) total);
            progressBar.setSecondaryProgress((int) total);
            progressBar.setProgress((int) hasUse);
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            showSpace.setText(decimalFormat.format(hasUse)+"GB/"+decimalFormat.format(total)+"GB");
            return convertView;
        }

        @Override
        public int getCount() {
            return getExStorage().size();
        }
    }

    //获得外部存储器的路径
    public List<String> getExStorage(){
        List<String> list = new ArrayList<String>();
        //用反射机制读取外部sd卡的路径
        StorageManager sm = (StorageManager) this.getActivity().getSystemService(Context.STORAGE_SERVICE);
        try {

            String[] paths = (String [])sm.getClass().getMethod("getVolumePaths",null).invoke(sm,null);
            for (String path: paths){
                StatFs sf = new StatFs(path);
                if (sf.getBlockCountLong()*sf.getBlockSizeLong()>0)
                    list.add(path);
            }

        }catch (Exception e){
           if (list.size()==0){
               Toast.makeText(getActivity(),"没有外部存储器",Toast.LENGTH_SHORT).show();
           }
        }

        return list;
    }

}
