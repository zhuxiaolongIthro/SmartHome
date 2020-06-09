// IBtServerAidl.aidl
package com.xiaoxiao.baselibrary.bluetooth;
import com.xiaoxiao.baselibrary.bluetooth.IClassicServiceCallback;
// Declare any non-default types here with import statements
/**
*蓝牙 服务端 方法
*/
interface IBtServerAidl {
void registCallabck(IClassicServiceCallback callback);
    /*作为服务端 发送广播 并等待链接的能力 一般是 AndroitThings 系统作为服务端等待被手机链接*/
    //开始发送广播
    void startAdversitise();
    //开始等待链接
    void disconnect();

    /*通用功能 在建立连接后 不在区分服务端 客户端角色*/
    /**
    *向已连接的设备 发送文件
    */
    void sendFile(String path);
    /**
    *相已连接的设备发送 信息
    */
    void sendMessage(String msg);
}
