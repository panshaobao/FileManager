package filemanager.android.bao.com.filemanager;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.text.Collator;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by baobao on 16-4-18.
 */
public class ListFileFragment extends ListFragment {
    //当前所在目录
    private String path;
    //当前目录下的所有文件
    private static List<String> files;
    private StoregeFragment sfragment;

    public ListFileFragment() {

    }

    public void setPath(String path){
        this.path= path;
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

        Log.d("TAG", String.valueOf(files.size()));
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

    public class MyAdapter extends ArrayAdapter{
        private List<String> data = getFiles();
        public MyAdapter(List<String> data){
            super(getActivity(),0,data);
        }
        public int getCount() {
            return data.size();
        }

        public void refresh(List<String> list){
            data = list;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String path = (String)getItem(position);
            File file = new File(path);
            if (convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.listfilefragment_list_item,null);
            }
            ImageView fileImage = (ImageView)convertView.findViewById(R.id.list_file_image);
            TextView fileName =(TextView)convertView.findViewById(R.id.list_file_name);
            TextView fileSize =(TextView)convertView.findViewById(R.id.list_file_size);
            TextView fileTime=(TextView)convertView.findViewById(R.id.list_file_time);
            ImageView dirImage =(ImageView)convertView.findViewById(R.id.dir);
            //设置文件最后修改的时间
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(file.lastModified());
            fileTime.setText(df.format(date.getTime()));
            //设置文件名
            fileName.setText(file.getName());
            //设置文件的图标
            if (file.isDirectory()){
                fileImage.setImageResource(R.drawable.folder);
                String[] list = file.list();
                fileSize.setText(list.length+"项");
                dirImage.setVisibility(View.VISIBLE);
            }else {
                double size = file.length()/1024.0;
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                if (size>=1000){
                    size=size/1024.0;
                    fileSize.setText(decimalFormat.format(size)+"MB");
                }else {
                    fileSize.setText(decimalFormat.format(size)+"KB");
                }
                dirImage.setVisibility(View.GONE);
                if(path.endsWith(".jpg") || path.endsWith(".png")|| path.endsWith(".jpeg")
                        || path.endsWith(".jpe")|| path.endsWith(".bmp")|path.endsWith(".gif")){
                    new BitmapWorkerTask(fileImage).execute(path);
                }else if (path.endsWith(".mp3")){
                    fileImage.setImageResource(R.drawable.file_icon_mp3);
                }else if (path.endsWith(".pdf")){
                    fileImage.setImageResource(R.drawable.file_icon_pdf);
                }else if (path.endsWith(".mp4") || path.endsWith(".3gp") || path.endsWith(".wav")){
                    fileImage.setImageResource(R.drawable.file_icon_wav);
                } else{
                    fileImage.setImageResource(R.drawable.file_icon_txt);
                }
            }

            return convertView;
        }
    }
    MyAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        path=getArguments().getString(LISTFILEFRAGMENT_PATH);
        adapter= new MyAdapter(getFiles());
        sfragment.myAdapter = adapter;
        setListAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemClickListener(sfragment.getItemClickListener());
        getListView().setOnItemLongClickListener(sfragment.getItemLongClickListener());
    }

    public static String LISTFILEFRAGMENT_PATH ="listfilefragment_path";
    //接收文件目录路径，用来显示当前目下的文件内容
    public static ListFileFragment  newInstance(String path,StoregeFragment sfragment){
        Bundle bundle = new Bundle();
        bundle.putString(LISTFILEFRAGMENT_PATH, path);
        ListFileFragment fragment = new ListFileFragment();
        fragment.sfragment = sfragment;
//        fragment.setPath(path);
        fragment.setArguments(bundle);
        return fragment;
    }

}
