package filemanager.android.bao.com.filemanager;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**单例，用来存储最近打开的文件
 * Created by baobao on 16-3-15.
 */
public class RecentFileLab {

    private List<RecentFile> recentFiles;

    private Context context;


    //一直存在的单例对象
    private static RecentFileLab recentFileLab;

    private RecentFileLab(Context c){
        context = c;
        recentFiles= new ArrayList<RecentFile>();
    }


    public static RecentFileLab newInstance(Context c){
        if (recentFileLab == null){
            recentFileLab = new RecentFileLab(c);
        }

        return recentFileLab;
    }

    public void addFile(RecentFile recentFile){
        recentFiles.add(recentFile);
    }

    public void deletedFile(int index){
        recentFiles.remove(index);
    }

    public void deletedAll(){
        recentFiles = new ArrayList<RecentFile>();
    }

    public List<RecentFile> getRecentFiles(){
        return recentFiles;
    }

    public void setRecentFiles(List<RecentFile> list){
        recentFiles = list;
    }


    //保存浏览的文件名
    public void writeToFile(String fileName){
        System.out.println("****** 最近浏览数据保存中 ******");
        JSONArray array = new JSONArray();
        Writer writer =null;
        for (RecentFile file:recentFiles){
            array.put(file.toJSON());
        }
        //删除原来的记录
        boolean ok = context.deleteFile(fileName);

        if (ok){
            System.out.println("****** 旧rencent.json已删除 ******");
        }
        //写入新记录
        try {
            writer = new OutputStreamWriter(context.openFileOutput(fileName,Context.MODE_PRIVATE));
            writer.write(array.toString());
        }catch (Exception e){
            System.out.println("写入recentFile.json出错。。。。。");
        }finally {
            if (writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //在应用的整个生命周期里只加载一次，即第一次启动应用程序时加载,
    //在别的时候调用也不会加载，这样确保不让recentFiles出错
    public void loadRecentFiles(String fileName){

        if (recentFiles.size() == 0){
            BufferedReader reader=null;
//        List<RecentFile>list = new ArrayList<RecentFile>();

            try {
                reader = new BufferedReader(new InputStreamReader(context.openFileInput(fileName)));
                StringBuffer jsonString = new StringBuffer();
                String line = null;
                while ((line= reader.readLine())!=null){
                    jsonString.append(line);
                }

                JSONArray array = (JSONArray)new JSONTokener(jsonString.toString()).nextValue();

                for(int i = 0;i<array.length();i++){
                    recentFiles.add(new RecentFile(array.getJSONObject(i)));
                }


            }catch (Exception e){

            }finally {
                if (reader!=null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }


}
