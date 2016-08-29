package filemanager.android.bao.com.filemanager.media;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import filemanager.android.bao.com.filemanager.R;

/**显示播放列表
 * Created by baobao on 16-3-25.
 */
public class Mp3ListFragment extends android.support.v4.app.ListFragment{
    private List<String>listsString;
    private List<File>listsFile;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listsString = new ArrayList<>();
        listsFile = new ArrayList<>();
        String file = getActivity().getIntent().getStringExtra(PlayMusicFragment.MP3_PATH);
        File[] files = FileFactory.getFiles(file);
        for (File file1:files){
            listsFile.add(file1);
            listsString.add(file1.getName());
        }

        setListAdapter(new MP3ListAdapter<String>());
    }
    private ListView listView;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView= getListView();
    }

    private class MP3ListAdapter<String> extends ArrayAdapter{

        public MP3ListAdapter() {
            super(getActivity(),0,listsString);
        }

        @Override
        public int getCount() {
            return listsString.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_mp3list,null);
            }
            TextView textView = (TextView)convertView.findViewById(R.id.list_song_file);
            textView.setText((java.lang.String)getItem(position));
            return convertView;
        }

    }

}
