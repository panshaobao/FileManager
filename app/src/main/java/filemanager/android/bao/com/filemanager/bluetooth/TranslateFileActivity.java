package filemanager.android.bao.com.filemanager.bluetooth;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import filemanager.android.bao.com.filemanager.R;
import filemanager.android.bao.com.filemanager.RecentFragment;

/**传输文件，采用蓝牙通信模式
 * Created by baobao on 16-3-21.
 */
public class TranslateFileActivity extends Activity implements View.OnClickListener {

    private BluetoothAdapter adapter;
    private String path;

    //蓝牙的开启状态
    private boolean isOn =true;

    private ConnectedThread connectedThread;
    private ChatThread chatThread;

    private List<BluetoothDevice> devices;
    private TextView show_connected;
    private Button cancel_connected;

    private List<BluetoothDevice> devicesPaired;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    show_connected.setText("连接失败");
                    break;
                case 1:
                    chatThread = connectedThread.getChatThread();
                    chatThread.start();
                    show_connected.setText("连接成功");
                    cancel_connected.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        pairedList = (ListView)findViewById(R.id.paired_list);
        devicesList = (ListView)findViewById(R.id.devices_list);
        show_connected = (TextView)findViewById(R.id.show_connected);
        cancel_connected = (Button)findViewById(R.id.cancel_connected);
        cancel_connected.setOnClickListener(this);
        initBluetooth();
        initActionBar();
        //扫描设备
        adapter.startDiscovery();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, intentFilter);

        Intent intent = getIntent();
        path =intent.getStringExtra(RecentFragment.SEND_FILE);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPaired();
        adapter.startDiscovery();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectedThread != null){
            connectedThread.cancelClient();
            show_connected.setText("当前没有连接设备");
        }
        if (adapter != null){
            adapter.cancelDiscovery();
        }
        try{
            unregisterReceiver(mReceiver);
        }catch (Exception e){}

        cancel_connected.setVisibility(View.GONE);
        show_connected.setText("已断开连接");

    }

    private ListView pairedList;
    private ListView devicesList;

    private void getPaired(){
        devicesPaired = new ArrayList<>();
        if (isOn){
            Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
            if (pairedDevices.size()>0) {
                for (BluetoothDevice device : pairedDevices) {
                    devicesPaired.add(device);
                }
            }
        }

        ArrayList<String> data1 = new ArrayList<>();
        for (BluetoothDevice s: devicesPaired){
            data1.add(s.getName()+"\n"+s.getAddress());
        }


        pairedList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,data1));

        pairedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                connect(devicesPaired.get(i));
            }
        });


    }

    private void connect(final BluetoothDevice device){
        new AlertDialog.Builder(TranslateFileActivity.this)
                .setPositiveButton("接收文件", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        connectedThread = new ConnectedThread(handler,adapter,device,"client");
                        connectedThread.start();
                        show_connected.setText("正在连接....");
                    }
                })
                .setMessage("连接此设备接收文件")
                .show();
    }

    private void initBluetooth(){
        devices = new ArrayList<BluetoothDevice>();
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null){
            Toast.makeText(this,"本地没有蓝牙设备",Toast.LENGTH_SHORT).show();
            return;
        }
        //启动蓝牙设备
        if (!adapter.isEnabled()) {
            isOn = false ;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.translate_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.refresh:
                devices = new ArrayList<>();
                adapter.cancelDiscovery();
                adapter.startDiscovery();
                break;
            case R.id.operation_info:
                new AlertDialog.Builder(this)
                        .setMessage("接收到的文件存放到外部存储器的mybluetooth目录下")
                        .setPositiveButton("我已了解",null)
                        .show();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 当发现设备的时候
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 获得所发现的设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                System.out.println("发现新设备" + device.getName() + "---" + device.getAddress());
                if (!devices.contains(device)){
                    devices.add(device);
                }
                final ArrayList<String> data3 = new ArrayList<>();
                for (BluetoothDevice d:devices){
                    data3.add(d.getName() + "\n" + d.getAddress());
                }
                devicesList.setAdapter(new ArrayAdapter<String>(TranslateFileActivity.this,
                        android.R.layout.simple_list_item_1, data3));

//                ((ArrayAdapter)devicesList.getAdapter()).notifyDataSetChanged();

                devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        BluetoothDevice device1 = devices.get(i);
                        if (BluetoothDevice.BOND_NONE == device1.getBondState()) {
                            //还没配对好，进行配对
                            Toast.makeText(TranslateFileActivity.this,"进行配对",Toast.LENGTH_SHORT).show();
                            Method createBondMethod = null;
                            try {
                                createBondMethod = BluetoothDevice.class.getMethod("createBond");
                                createBondMethod.invoke(device1);
                            } catch (Exception e) {
                               Toast.makeText(TranslateFileActivity.this,"配对失败",Toast.LENGTH_SHORT).show();
                            }

                        } else if (BluetoothDevice.BOND_BONDED == device1.getBondState()) {
                            //已经配对好,进行连接
                            connect(device1);

                        } else if (BluetoothDevice.BOND_BONDING == device1.getBondState()) {
                            //正在配对

                            Toast.makeText(TranslateFileActivity.this,"正在配对，请稍等",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
            //得到已经配对的蓝牙设备
            getPaired();
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0){
            if (!(resultCode == RESULT_OK)){
                isOn = false ;
                Toast.makeText(this,"请打开蓝牙设备",Toast.LENGTH_SHORT).show();
            }else {
                isOn = true;
            }
        }
    }

    private void initActionBar(){
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    //初始化系统状态栏，设置为透明
    private void initStateBar() {
        if (android.os.Build.VERSION.SDK_INT > 18) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_connected:
                connectedThread.cancelClient();
                show_connected.setText("当前没有连接设备");
                cancel_connected.setVisibility(View.GONE);
                break;
        }
    }
}
