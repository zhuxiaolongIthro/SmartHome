// IClassicBtServiceAidl.aidl
package com.xiaoxiao.baselibrary.bluetooth;
import com.xiaoxiao.baselibrary.bluetooth.IClassicServiceCallback;
// Declare any non-default types here with import statements

interface IClassicBtServiceAidl {
    void registCallabck(IClassicServiceCallback callback);

    /**
    * ble 通信时传递的 mac 地址
    */
    void connectToDevice(String mac);

    /**
    *向目标设备 发送文件
    */
    void sendFile(String path,String deviceMac);
}
