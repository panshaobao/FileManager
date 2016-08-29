package filemanager.android.bao.com.filemanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 用来显示文件，采用Gridview来显示
 * Created by baobao on 16-3-10.
 */
public class FileFragment extends Fragment {

    public static final String FILEFRAGMENT_PATH="filefragent_path";
    public static final String CURREN_PATH_FILE="filefragmen.current.path.file";

    //当前所在目录
    private String path;
    //当前目录下的所有文件
    private static List<String> files;
    private GridView gridView;
    //文件名
    private TextView textView;
    private StoregeFragment sfragment;

    public FileFragment(){
        super();
    }

//    private PopupWindow window;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        path=getArguments().getString(FILEFRAGMENT_PATH);
    }

    //保存Fragment当前的状态
    @Override
    public void onPause() {
        super.onPause();
    }

    //恢复状态
    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();

    }




    //接收文件目录路径，用来显示当前目下的文件内容
    public static FileFragment  newInstance(String path,StoregeFragment sfragment){
        Bundle bundle = new Bundle();
        bundle.putString(FILEFRAGMENT_PATH, path);
        FileFragment fragment = new FileFragment();
        fragment.sfragment = sfragment;
        fragment.setArguments(bundle);
        return fragment;
    }
    public void setFiles(List<String> files){
        this.files=files;
    }


    public List<String> getFiles(){
        List<List<String>> lists = getFile(path);
        compareDirFiles= lists.get(0);
        compareFiles= lists.get(1);
        files =null;
        files = new ArrayList<>();
        switch (sfragment.getType()){
            case 0:
                Collections.sort(compareDirFiles, Collator.getInstance(Locale.CHINESE));
                Collections.sort(compareFiles, Collator.getInstance(Locale.CHINESE));
                files.addAll(compareDirFiles);
                files.addAll(compareFiles);
                break;
            case 1:
                Collections.sort(compareDirFiles, Collator.getInstance(Locale.CHINESE));
                Collections.sort(compareFiles, Collator.getInstance(Locale.CHINESE));
                files.addAll(compareFiles);
                files.addAll(compareDirFiles);
                break;
            case 2:
                Collections.sort(compareDirFiles, Collator.getInstance(Locale.ENGLISH));
                Collections.reverse(compareDirFiles);
                Collections.sort(compareFiles, Collator.getInstance(Locale.ENGLISH));
                Collections.reverse(compareFiles);
                files.addAll(compareDirFiles);
                files.addAll(compareFiles);
                break;
            case 3:
                Collections.sort(compareDirFiles, Collator.getInstance(Locale.ENGLISH));
                Collections.reverse(compareDirFiles);
                Collections.sort(compareFiles, Collator.getInstance(Locale.ENGLISH));
                Collections.reverse(compareFiles);
                files.addAll(compareFiles);
                files.addAll(compareDirFiles);
                break;
        }

        Log.d("TAG",String.valueOf(files.size()));
        return files;
    }

    //把目录和文件分别存储,方便实现排序
    private List<String>compareDirFiles = new ArrayList<>(),compareFiles = new ArrayList<>();

    //获得指定目录的文件
    private List<List<String>> getFile(String path){
        List<List<String>> lists = new ArrayList<>();
        List<String>cdif = new ArrayList<>();
        List<String>cfile = new ArrayList<>();
        File file = new File(path);
        File[] files = file.listFiles();
        if (files==null){
            System.out.println("读取文件夹出错");
        }else {
            if (sfragment.isHide){
                for (File file1:files){
                    //表示需要隐藏文件,所以判断如果文件是隐藏的,就不用显示出来
                    if (!file1.isHidden()){
                        String filePath = file1.getAbsolutePath();
                        if (file1.isDirectory()){
                            cdif.add(filePath);
                        }else {
                            cfile.add(filePath);
                        }
                    }

                }

            }else {
               for (File file1:files){
                   String filePath = file1.getAbsolutePath();
                   if (file1.isDirectory()){
                       cdif.add(filePath);
                   }else {
                       cfile.add(filePath);
                   }
               }
            }

        }
        lists.add(cdif);
        lists.add(cfile);

        System.out.println(compareDirFiles.size() + "------" + compareFiles.size());
        return lists;
    }

    private ImageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filefragment_gridview,null);
        gridView = (GridView)view.findViewById(R.id.gridview);

        adapter = new ImageAdapter(getFiles());
        sfragment.imageAdapter =adapter;
//        adapter.setNotifyOnChange(false);
        gridView.setAdapter(adapter);
//        gridView.setOnClickListener(sfragment.getClickListener());
        //设置点击监听器
        gridView.setOnItemClickListener(sfragment.getItemClickListener());
        //设置长按监听器
        gridView.setOnItemLongClickListener(sfragment.getItemLongClickListener());

        return view;

    }

    public ImageAdapter getAdapter(){
        return adapter;
    }

    //girdview的适配器

    public class ImageAdapter extends ArrayAdapter {
        private List<String>data=getFiles();
        public ImageAdapter(List<String>data) {
            super(getActivity(),0,data);
        }

        public int getCount() {
            return data.size();
        }

        public void refresh(List<String> list){
            data=list;
            notifyDataSetChanged();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.file_gridview_item,null);
            }
            ImageView imageView =(ImageView)convertView.findViewById(R.id.file_image);
            textView=(TextView)convertView.findViewById(R.id.file_name);
            System.out.println("调用createView..............");
            java.lang.String path = ( java.lang.String)getItem(position);
            File file = new File(path);
            if(path.endsWith(".jpg") || path.endsWith(".png")|| path.endsWith(".jpeg")
                    || path.endsWith(".jpe")|| path.endsWith(".bmp")|path.endsWith(".gif")){
                new BitmapWorkerTask(imageView).execute(path);
            }else if (path.endsWith(".mp3")){
                imageView.setImageResource(R.drawable.file_icon_mp3);
            }else if (path.endsWith(".pdf")){
                imageView.setImageResource(R.drawable.file_icon_pdf);
            }else if (path.endsWith(".mp4") || path.endsWith(".3gp") || path.endsWith(".wav")){
                imageView.setImageResource(R.drawable.file_icon_wav);
            }else if (file.isDirectory()){
                imageView.setImageResource(R.drawable.folder);
            }else{
                imageView.setImageResource(R.drawable.file_icon_txt);
            }

            textView.setText(file.getName());

            return convertView;
        }

    }
}

