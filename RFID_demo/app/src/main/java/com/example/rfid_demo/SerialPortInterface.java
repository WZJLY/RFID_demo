package com.example.rfid_demo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android_serialport_api.SerialPort;


//new serial port
public class SerialPortInterface extends AppCompatActivity {
    FileOutputStream mOutputStream = null;
    FileInputStream mInputStream = null;
    SerialPort sp;
    Context context;

    public SerialPortInterface(Context context, String serialPortID) {
        this.context =context;
        try {
            sp = new SerialPort(new File(serialPortID), 9600, 0);
            mOutputStream = (FileOutputStream) sp.getOutputStream();
            mInputStream = (FileInputStream) sp.getInputStream();
            Toast.makeText(context,"串口连接成功", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(context,"串口连接失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(context,"串口连接失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



    /**
     * 发送数据
     *RFID读取数据
     * return 异常
     *
     */

public int RFID_send(int mode,byte[] sendData)
{
    if(mOutputStream == null){
        return -1;
    }
        try {
            mOutputStream.write(sendData);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

    return 1;
}


    /**
     *异或检验
     * return 校验值
     *
     */
public byte Make_BCC(byte data[],int start,int sum)
{
        byte temp =data[start];
        for(;start<sum;start++)
        {
            temp^=data[start+1];
        }
        return temp;
}

    /**
     *读取RFID
     * return 读取值
     *
     */

    public String RFID_read(){

            String Read = "";
            try {
                Thread.sleep(100);
                if (mInputStream.available() > 0) {
                    if (mInputStream != null) {
                        byte[] buffer = new byte[256];
                        int size = mInputStream.read(buffer);
                        if (size > 0) {
                            if (buffer[8] == 0x00) {
                                if (buffer[6] == 0x09 && buffer[7] == 0x06) {
                                    Read = "写入成功";
                                    Log.d("read", Read);
                                    return Read;
                                }
                                if (buffer[6] == 0x06 && buffer[7] == 0x01) {
                                    Read = "蜂鸣器";
                                    Log.d("read", Read);
                                    return Read;
                                }
                                if (buffer[6] == 0x08 && buffer[7] == 0x06) {

                                    Log.d("read", "OK");
                                    byte[] read = new byte[16];
                                    for (int j = 0; j < 16; j++) {
                                        read[j] = buffer[j + 9];
                                    }
                                    String sendString = new String(read, "UTF-8");
                                    Read = sendString;
                                    return Read;
                                    //将数据位读取出来
                                }
                            } else {

                                Read = "操作失败";
                                Log.d("read", Read);
                                return Read;
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return Read;
    }




}