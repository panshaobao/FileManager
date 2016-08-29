package filemanager.android.bao.com.filemanager;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import filemanager.android.bao.com.filemanager.dialog.StaticDialog;

/**复制文件，剪切，删除文件的操作
 * 只能复制，剪切文件，不能复制，剪切目录
 * Created by baobao on 16-3-18.
 */
public class FileOperationTask extends AsyncTask<String,Integer,Boolean>{
    //操作类型
    public static final String CUT = "CUT";
    public static final String COPY = "COPY";
    public static final String DELETE ="DELETE";

    private String operation;
    private StoregeFragment fragment;
    StaticDialog dialog;
    //operation值为:"CUT","COPY","DELETE"
    public FileOperationTask(StoregeFragment fragment , String operation){
        this.operation = operation;
        this.fragment = fragment;
        dialog = new StaticDialog(fragment.getActivity());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(String...strings) {
        //第一个参数是复制到哪里的目录的路径
        //第二个参数是所要复制的文件的路径
        boolean isOK = false;
        String currentPath = strings[0];
        String fileName = strings[1];

        switch (operation){
            case COPY:
                isOK = copy(currentPath,fileName);
                break;
            case CUT:
                isOK =cut(currentPath,fileName);
                break;
            case DELETE:
                isOK = delete(fileName);
                break;
        }

        return isOK;
    }

    @Override
    protected void onPostExecute(Boolean isOk) {
        switch (operation){
            case CUT:
                if (isOk){
                    Toast.makeText(fragment.getActivity(),"剪切成功",Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(fragment.getActivity(),"剪切失败",Toast.LENGTH_SHORT).show();
                }
                break;
            case COPY:
                if (isOk){
                    Toast.makeText(fragment.getActivity(),"复制成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(fragment.getActivity(),"复制失败",Toast.LENGTH_SHORT).show();
                }
                break;
            case DELETE:
                if (isOk){
                    Toast.makeText(fragment.getActivity(),"删除成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(fragment.getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
                }
                break;

        }
        dialog.setCanDissmiss(true);
        dialog.dismiss();
        fragment.addFragment(fragment.getCurrentPath());
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

    }

    //删除文件
    private boolean delete(String fileName){
        File file = new File(fileName);
        return file.delete();
    }

    //复制选中的文件
    private boolean copy(String currentPath,String fileName){
        File file = new File(fileName);
        String name = file.getName();
        System.out.println(name);
        System.out.println(currentPath+name);
        File newFile = new File(currentPath+"/"+name);
        if (file.isDirectory()){
            return false;
        }else {
            try {
                if (newFile.exists()){
                    return false;
                }
                newFile.createNewFile();
            } catch (IOException e) {
                return false;
            }
            FileInputStream inputStream =null;
            FileOutputStream outputStream =null;
            FileChannel in=null;
            FileChannel out =null;
            try {
                inputStream= new FileInputStream(file);
                outputStream = new FileOutputStream(newFile);
                in = inputStream.getChannel();
                out =outputStream.getChannel();
                int data;
                try {
                    in.transferTo(0, in.size(), out);
                } catch (IOException e) {
                    e.printStackTrace();
                    newFile.delete();
                    return false;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                newFile.delete();
                return false;
            }finally {
                if (inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (in!=null){
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
               if (out!=null){
                   try {
                       out.close();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }

            }
        }

        return true;
    }

    //剪切选中的文件
    private boolean cut(String currentPath,String fileName){
        File file = new File(fileName);
        String name = file.getName();
        System.out.println(name);
        System.out.println(currentPath+name);
        File newFile = new File(currentPath+"/"+name);
        if (file.isDirectory()){
            return false;
        }else {
            try {
                if (newFile.exists()){
                    return false;
                }
                newFile.createNewFile();
            } catch (IOException e) {
                return false;
            }
            FileInputStream inputStream =null;
            FileOutputStream outputStream =null;
            FileChannel in = null;
            FileChannel out =null;
            try {
                inputStream= new FileInputStream(file);
                outputStream = new FileOutputStream(newFile);
                in = inputStream.getChannel();
                out = outputStream.getChannel();
                try {
                    in.transferTo(0, in.size(), out);
                    file.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                    newFile.delete();
                    return false;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                newFile.delete();
                return false;
            }finally {
                if (inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (in!=null){
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out!=null){
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        return true;
    }

}
