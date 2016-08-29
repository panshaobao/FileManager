package filemanager.android.bao.com.filemanager.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Environment;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**文件传输
 * 发送文件，先发送文件名，再发送文件本身
 * 接收文件，先接收到文件名，再接收文件本身
 * Created by baobao on 16-3-22.
 */
public class ChatThread extends Thread{

    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private FileInputStream fileInputStream;
    private FileOutputStream fileOutputStream;

    private static String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static File f = new File(filePath+"/mybluetooth");

    public ChatThread(BluetoothSocket socket){
        this.socket = socket ;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            dataInputStream = new DataInputStream(inputStream);
            dataOutputStream = new DataOutputStream(outputStream);

        } catch (IOException e) {

        }
    }

    public void run(){
        System.out.println("********* 接收线程已工作 *******");
        if (f.exists()){
            if (!f.isDirectory()){
                f.mkdir();
                System.out.println("*********** 创建mybluetooth目录 ***********");
            }
        }else {
            f.mkdir();
            System.out.println("*********** 创建mybluetooth目录 ***********");
        }

        recieveFile();
    }

    private String getFileName(){
        String fileName=null;
        try {
            fileName = dataInputStream.readUTF();
            System.out.println(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public void sendFileName(File file){
        try {
            dataOutputStream.writeUTF(file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(File file){

        new SendThread(file).start();
    }

    public void recieveFile(){
        //接收文件
        File file ;
        file = new File(f,getFileName());
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("***** 创建文件"+file.getName());
                try {
                    fileOutputStream = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    System.out.println("***** 文件"+ file.getName()+"没发现" );
                }
            } catch (IOException e) {
                System.out.println("***** 创建文件失败");
            }

        }

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("***** 文件"+ file.getName()+"没发现" );
        }
        try {
            int n;
            while ((n = inputStream.read()) != -1) {
                fileOutputStream.write(n);
                System.out.print(n);
            }
        } catch (IOException e) {
            System.out.println("***** 接收文件失败");
        }



    }

    //完成发送文件
    private class SendThread extends Thread{

        private File file;

        public SendThread(File file){
            this.file = file;
        }

        public void run(){
//            发送文件
            sendFileName(file);

            try {
                fileInputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int n;
            try {
                while((n=fileInputStream.read()) != -1){
                    outputStream.write(n);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public void cancel(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
