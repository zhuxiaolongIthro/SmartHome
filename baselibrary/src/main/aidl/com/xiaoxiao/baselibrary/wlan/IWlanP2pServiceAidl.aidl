// IWlanP2pServiceAidl.aidl
package com.xiaoxiao.baselibrary.wlan;
import com.xiaoxiao.baselibrary.wlan.IWlanP2pServiceCallback;
// Declare any non-default types here with import statements

interface IWlanP2pServiceAidl {
   void registCallback(IWlanP2pServiceCallback callback);


   //开始 发现附近的peers 设备
   void startDiscoverPeers(long duration);
   //停止发现
   void stopDiscoverPeers();
   //执行连接
   void connectToPeer(String deviceName);
   //断开连接
   void disconnectFromPeer(String deviceName);
   //发送消息
   void sendMessage(String msg);

}
