// IClassicServiceCallback.aidl
package com.xiaoxiao.baselibrary.bluetooth;

// Declare any non-default types here with import statements

interface IClassicServiceCallback {
    /*连接状态改变*/
   void onConnectStateChanged(int state,String mac);
   /*文件发送状态改变*/
   void onFileSended(int state);

}
