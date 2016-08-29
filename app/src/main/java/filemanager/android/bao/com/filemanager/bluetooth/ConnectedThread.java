package filemanager.android.bao.com.filemanager.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

/**与别的设备进行连接，为了堵塞UI线程，在子线程里进行连接
 * Created by baobao on 16-3-22.
 */
public class ConnectedThread extends Thread{
    //用于串口的UUID
    final static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    //设置本机是服务器还是客户端，服务器用 server 客户端用 client
    //默认是服务器
    private String mode = "server";

    private BluetoothServerSocket serverSocket ;

    private BluetoothSocket socket;

    private BluetoothAdapter adapter;

    private BluetoothDevice device ;
    private ChatThread chatThread;

    private Handler handler;

    //参数device 是用来创建客户端的
    public ConnectedThread(Handler handler,BluetoothAdapter adapter,BluetoothDevice device,String mode){
        this.mode = mode ;
        this.adapter = adapter ;
        this.device = device ;
        this.handler = handler;
        if ("server".equals(mode)){
            //作为服务器
            try {
                serverSocket = adapter.listenUsingInsecureRfcommWithServiceRecord(adapter.getName(), UUID.fromString(SPP_UUID));
            } catch (IOException e) {
            }
        }else{
            //作为客户端
            try {
                socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void run(){
        if ("server".equals(mode)){
            //作为服务器
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                //告诉activity没有设备连接上
                handler.sendEmptyMessage(0);
            }

            if (socket != null) {
                //已经连接上，在这里进行文件传输操作
                chatThread = new ChatThread(socket);
                handler.sendEmptyMessage(1);
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }else {
            //作为客户端
            try {
                socket.connect();
            } catch (IOException e) {
               //不能连接服务器
                handler.sendEmptyMessage(0);
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            //已经连接上服务器
            chatThread = new ChatThread(socket);
            handler.sendEmptyMessage(1);

        }
    }

    //取消服务器
    public void cancelServer() {
        try {
            serverSocket.close();
        } catch (IOException e) { }
    }

    //取消客户端
    public void cancelClient() {
        try {
            socket.close();
        } catch (IOException e) { }
    }


    public BluetoothSocket getSocket() {
        return socket;
    }

    public ChatThread getChatThread(){
        return chatThread;
    }
}
