package filemanager.android.bao.com.filemanager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**显示最近文件的模型
 * Created by baobao on 16-3-15.
 */
public class RecentFile {
    public static final String PICTURE=".png";
    public static final String MP3=".mp3";
    public static final String PDF=".pdf";
    public static final String MP4=".mp4";
    public static final String TXT=".txt";
    private static final String RECENTFILE_ID = "recent_file_id";
    private static final String RECENTFILE_PATH = "recent_file_path";
    //文件路径
    private String path;
    //文件的后缀
    private String suffix;
    //
    private int id;

    public RecentFile(String path){
        this.path = path;

        if(path.endsWith(".jpg") || path.endsWith(".png")|| path.endsWith(".jpeg")
                || path.endsWith(".jpe")|| path.endsWith(".bmp")|path.endsWith(".gif")){
            suffix=PICTURE;
            id = R.drawable.file_icon_picture;
        }else if (path.endsWith(".mp3")){
            suffix=MP3;
            id = R.drawable.file_icon_mp3;
        }else if (path.endsWith(".pdf")){
            suffix=PDF;
            id=R.drawable.file_icon_pdf;
        }else if (path.endsWith(".mp4") || path.endsWith(".3gp") || path.endsWith(".wav")){
            suffix=MP4;
            id=R.drawable.file_icon_wav;
        }else{
            suffix=TXT;
            id=R.drawable.file_icon_txt;
        }

    }

    public RecentFile(JSONObject jsonObject) {
        try {
            path = jsonObject.getString(RECENTFILE_PATH);
            id = jsonObject.getInt(RECENTFILE_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setId(int id1){
        id=id1;
    }

    public int getId(){
        return id;
    }

    public String getPath(){
        return path;
    }

    public void setPath(String s){
        path = s;
    }

    public String getSuffix(){
        return suffix;
    }

    @Override
    public String toString() {
        File f = new File(path);
        return f.getName();
    }

    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RECENTFILE_ID, getId());
            jsonObject.put(RECENTFILE_PATH,getPath());
        } catch (JSONException e) {
            System.out.println("创建json出错");
        }

        return jsonObject;
    }
}
