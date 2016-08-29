package filemanager.android.bao.com.filemanager.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import filemanager.android.bao.com.filemanager.R;

/**
 * Created by baobao on 16-3-24.
 */
public class ViewImageFragment extends Fragment {
    private static final String IMAGE_PATH = "ViewImageFragment.image_path";
    private ImageView imageView;
    private String path;
    public ViewImageFragment(String filePath){
        this.path = filePath;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_viewimage, null);
        imageView =(ImageView) view.findViewById(R.id.imageview_viewimage);
        int dw = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int dh = getActivity().getWindowManager().getDefaultDisplay().getHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path,options);
        int simpleSize =1;
        while (options.outWidth/simpleSize>dw || options.outHeight/simpleSize>dh){
            simpleSize*=2;
        }

        options.inSampleSize = simpleSize;
        options.inJustDecodeBounds= false;
        imageView.setImageBitmap( BitmapFactory.decodeFile(path,options));
        return view;
    }
}
