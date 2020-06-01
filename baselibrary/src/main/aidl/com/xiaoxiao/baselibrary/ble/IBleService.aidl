// IBleService.aidl
package com.xiaoxiao.baselibrary.ble;
import com.xiaoxiao.baselibrary.ble.IBleServiceCallback;
// Declare any non-default types here with import statements

interface IBleService {
    void registCallback(IBleServiceCallback callback);

    /*启动ble 外围状态 开始发送广播*/
    void startAdvertise();
    /*在一定情况下 停止作为外围服务的广播 如链接数达到限制*/
    void stopAdvertise();

    /*自己作为 中心设备 开始扫描 外围设备
    指定延迟时间 ，指定扫描周期  ，指定扫描目标uuid、devicename、mac
    扫描结果在 ServiceCallback中返回
    */
    void scanLeSensors(long delay,long period,String keyWord);
    /**
    * 指定目标 进行链接操作
    链接状态 通过ServiceCallback 中返回
    */
    void connectToSensor(String keyWord);
    void disconnectFromSensor(String keyWord);


    /*信息传输*/
    void sendMessage(String msg);

}
