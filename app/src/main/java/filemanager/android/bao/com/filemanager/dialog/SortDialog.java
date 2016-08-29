package filemanager.android.bao.com.filemanager.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import filemanager.android.bao.com.filemanager.R;
import filemanager.android.bao.com.filemanager.StoregeFragment;

/**
 * Created by baobao on 16-4-19.
 */
public class SortDialog extends Dialog implements View.OnClickListener {
    private StoregeFragment fragment;
    private Button normal;
    private Button type1;
    private Button type2;
    private Button type3;
    public SortDialog(Context context,StoregeFragment fragment) {
        super(context);
        this.fragment = fragment;
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.sort_popumenu,null);
        setContentView(view);
        setTitle("排序更方便定位文件哦");
        normal = (Button)view.findViewById(R.id.sort_normal);
        type1 = (Button)view.findViewById(R.id.sort_type1);
        type2 =(Button)view.findViewById(R.id.sort_type2);
        type3 =(Button)view.findViewById(R.id.sort_type3);
        setColor(fragment.getType());
        normal.setOnClickListener(this);
        type1.setOnClickListener(this);
        type2.setOnClickListener(this);
        type3.setOnClickListener(this);
    }

    private void setColor(int n){
        switch (n){
            case 0:
                normal.setTextColor(Color.RED);
                type1.setTextColor(Color.GRAY);
                type2.setTextColor(Color.GRAY);
                type3.setTextColor(Color.GRAY);
                break;
            case 1:
                normal.setTextColor(Color.GRAY);
                type1.setTextColor(Color.RED);
                type2.setTextColor(Color.GRAY);
                type3.setTextColor(Color.GRAY);
                break;
            case 2:
                normal.setTextColor(Color.GRAY);
                type1.setTextColor(Color.GRAY);
                type2.setTextColor(Color.RED);
                type3.setTextColor(Color.GRAY);
                break;
            case 3:
                normal.setTextColor(Color.GRAY);
                type1.setTextColor(Color.GRAY);
                type2.setTextColor(Color.GRAY);
                type3.setTextColor(Color.RED);
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sort_normal:
                fragment.setType(StoregeFragment.NORMAL);
                fragment.addFragment(fragment.getCurrentPath());
                SortDialog.this.dismiss();
                break;
            case R.id.sort_type1:
                fragment.setType(StoregeFragment.TYPE1);
                fragment.addFragment(fragment.getCurrentPath());
                SortDialog.this.dismiss();
                break;
            case R.id.sort_type2:
                fragment.setType(StoregeFragment.TYPE2);
                fragment.addFragment(fragment.getCurrentPath());
                SortDialog.this.dismiss();
                break;
            case R.id.sort_type3:
                fragment.setType(StoregeFragment.TYPE3);
                fragment.addFragment(fragment.getCurrentPath());
                SortDialog.this.dismiss();
                break;
        }
    }
}
