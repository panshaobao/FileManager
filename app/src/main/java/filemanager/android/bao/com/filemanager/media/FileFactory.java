package filemanager.android.bao.com.filemanager.media;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * Created by baobao on 16-3-26.
 */
public class FileFactory {

    //获得当前目录下所有.mp3文件
    public static File[] getFiles(String path){
        File file = new File(path);
        final File parentFile = file.getParentFile();
        File[] files = parentFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(".mp3")) {
                    return true;
                }

                return false;
            }
        });

        return files;
    }
}
