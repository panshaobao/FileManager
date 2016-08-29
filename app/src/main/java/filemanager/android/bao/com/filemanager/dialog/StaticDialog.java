package filemanager.android.bao.com.filemanager.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

/**静态对话框
 * Created by baobao on 16-4-20.
 */
public class StaticDialog extends ProgressDialog{
    boolean canDissmiss;
    public StaticDialog(Context context) {
        super(context);
        canDissmiss=false;
    }

    public void setCanDissmiss(boolean canDissmiss) {
        this.canDissmiss = canDissmiss;
    }

    @Override
    public void dismiss() {
        if (!canDissmiss){
            return;
        }else{
            super.dismiss();
            canDissmiss = false;
        }
    }
}
