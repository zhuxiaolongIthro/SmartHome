// IWlanP2pServiceCallback.aidl
package com.xiaoxiao.baselibrary.wlan;

// Declare any non-default types here with import statements

interface IWlanP2pServiceCallback {
    //完成一次发现 后获得的peers device 回调
    void onFoundPeerList();
    //连接回调
    void onPeerConnected(String deviceName,boolean success);
    //断开连接回调
    void onPeerDisConnected(String deviceName,boolean success);
    //收到消息
    void onReceiveMessage(String msg);
}
