// IBleServiceCallback.aidl
package com.xiaoxiao.baselibrary.ble;

// Declare any non-default types here with import statements

interface IBleServiceCallback {
    /*蓝牙状态*/
    void bluetoothAdapterEnabled();
    void bluetoothAdapterdisabled();
    /*作为外围设备 的广播状态*/
    void adverstiseStarted();
    void adverstiseStoped();
    /*扫描状态*/
    void onLeScanningStarted();
    void onLeDeviceFounded(String keyword);
    void onLeScanningStoped();
    /*设备链接状态*/
    void waitingConnectAsPeripheral();//作为外围设备等待被连接
    void onConnectedAsPeripheral(String keyword);//作为外围设备被成功连接
    void disconnectedAsPeripheral(String keyword);//作为外围设备 链接被断开

    void connectToSensor(String keyword);//作为中心设备 发起对外围传感器设备的链接
    void disconnectFromSensor(String keyword); //作为中心设备 断开与外围传感器的链接
    /*信息传输 */
    void onReceiveMessage(String msg);
}
