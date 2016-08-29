package filemanager.android.bao.com.filemanager.bluetooth;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import filemanager.android.bao.com.filemanager.R;
import filemanager.android.bao.com.filemanager.RecentFile;
import filemanager.android.bao.com.filemanager.RecentFragment;

/**等待客户端连接
 * Created by baobao on 16-3-23.
 */
public class ServerActitivty extends Activity implements View.OnClickListener {
    //用于串口的UUID
    final static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private BluetoothAdapter adapter;
    private Button startServer;
    private TextView showMessage;
    boolean isConnected =false;
    private Button stopServer ;
    private ConnectedThread connectedThread;
    private ChatThread chatThread;
    private File file;


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    showMessage.setText("等待连接失败"+"\n"+"请用别的蓝牙设备连接本机蓝牙设备");
                    isConnected =false;
                    stopServer.setClickable(false);
                    break;
                case 1:
                    showMessage.setText("已有设备连接"+"\n"+"其它设备已可以接收本机文件");
                    isConnected =true;
                    stopServer.setClickable(true);
                    chatThread = connectedThread.getChatThread();
                    if (file!=null){
                        chatThread.sendFile(file);
                    }
                    break;
            }


        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (connectedThread != null){
            connectedThread.cancelServer();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        startServer = (Button)findViewById(R.id.start_server);
        startServer.setOnClickListener(this);
        showMessage = (TextView)findViewById(R.id.show_message);
        stopServer = (Button)findViewById(R.id.stop_server);
        stopServer.setOnClickListener(this);
        stopServer.setClickable(false);
        initActionBar();
        initBluetooth();
        Intent intent = getIntent();
        String path = intent.getStringExtra(RecentFragment.SEND_FILE);
        file = new File(path);
    }


    private void initActionBar(){
        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
    }

    private void initBluetooth(){
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null){
            Toast.makeText(this, "本地没有蓝牙设备", Toast.LENGTH_SHORT).show();
            return;
        }
        //启动蓝牙设备
        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            if (resultCode == RESULT_OK){

            }else {
                Toast.makeText(this,"请打开蓝牙设备",Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_server:
                connectedThread = new ConnectedThread(handler, adapter, null, "server");
                connectedThread.start();
                showMessage.setText("正在等待连接....");
                startServer.setClickable(false);
                stopServer.setClickable(true);
                break;
            case R.id.stop_server:
                connectedThread.cancelServer();
                showMessage.setText("已停止服务....");
                startServer.setClickable(true);
                break;
        }

    }
}
