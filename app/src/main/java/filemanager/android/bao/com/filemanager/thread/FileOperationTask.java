package filemanager.android.bao.com.filemanager.thread;

import android.os.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import android.os.Process;

/**
 * Created by baobao on 16-4-20.
 */
public class FileOperationTask extends Thread{
    //操作类型
    public static final String CUT = "CUT";
    public static final String COPY = "COPY";
    public static final String DELETE ="DELETE";
    //operation值为:"CUT","COPY","DELETE"
    private String operation;
    Handler handler;
    String fileName;
    String currentPath;
    public FileOperationTask(Handler handler,String currentPath,String fileName,String operation){
        this.operation = operation;
        this.handler = handler;
        this.fileName = fileName;
        this.currentPath =currentPath;
//        this.setPriority(10);
    }

    @Override
    public void run() {
        Process.setThreadPriority(Thread.MAX_PRIORITY);
        boolean isOK=false;
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
        if (isOK){
            handler.sendEmptyMessage(1);
        }else {
            handler.sendEmptyMessage(0);
        }

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
                    in.transferTo(0,in.size(),out);
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
                    in.transferTo(0,in.size(),out);
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
            }
        }

        return true;
    }

}
