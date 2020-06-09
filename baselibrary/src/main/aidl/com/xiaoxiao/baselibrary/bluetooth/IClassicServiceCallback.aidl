// IClassicServiceCallback.aidl
package com.xiaoxiao.baselibrary.bluetooth;
import android.bluetooth.BluetoothDevice;

// Declare any non-default types here with import statements

interface IClassicServiceCallback {
    /*连接状态改变 是否有设备链接*/
   void onConnectStateChanged(int state,String mac);
   /*广播状态改变*/
   void onAdversitiseStateChange(int state);
   /*发现设备状态改变*/
   void onDiscoverServerStateChange(int state);
   /*获取发现列表*/
   void onDiscoverFinished(in List<BluetoothDevice> devicesJson);
   /*文件发送状态改变*/
   void onFileSended(int state);
   /*信息发送状态*/
   void onMessageSended();
   /*收到文件*/
   void onReceiveFile(String tempPath);
   /*收到信息*/
   void onReceiveMessage(String msg);
}
