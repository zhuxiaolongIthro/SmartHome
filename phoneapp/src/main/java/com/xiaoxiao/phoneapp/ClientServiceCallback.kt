package com.xiaoxiao.phoneapp

import android.util.Log
import com.xiaoxiao.baselibrary.ble.IBleServiceCallback


/**
 * 客户端 链接逻辑处理
 * */
class ClientServiceCallback: IBleServiceCallback.Stub() {
    val TAG ="ClientServiceCallback"
    /**
     * 作为外围被连接
     * */
    override fun onConnectedAsPeripheral(keyword: String?) {
        Log.i(TAG,"onConnectedAsPeripheral :")
    }
    /**
     * 开始 低功耗传感器设备的扫描
     * */
    override fun onLeScanningStarted() {
        Log.i(TAG,"onLeScanningStarted :")

    }

    /**
     * 作为 客户端去链接了外围设备
     * */
    override fun connectToSensor(keyword: String?) {
        Log.i(TAG,"connectToSensor :")
    }

    override fun onLeScanningStoped() {
        Log.i(TAG,"onLeScanningStoped :")
    }

    override fun waitingConnectAsPeripheral() {
        Log.i(TAG,"waitingConnectAsPeripheral :")
    }

    override fun onLeDeviceFounded(keyword: String?) {
        Log.i(TAG,"onLeDeviceFounded :")
    }

    override fun bluetoothAdapterdisabled() {
        Log.i(TAG,"bluetoothAdapterdisabled :")
    }

    override fun disconnectedAsPeripheral(keyword: String?) {
        Log.i(TAG,"disconnectedAsPeripheral :")
    }

    override fun adverstiseStoped() {
        Log.i(TAG,"adverstiseStoped :")
    }

    override fun disconnectFromSensor(keyword: String?) {
        Log.i(TAG,"disconnectFromSensor :")
    }

    override fun onReceiveMessage(msg: String?) {
        Log.i(TAG,"onReceiveMessage :")
    }

    override fun bluetoothAdapterEnabled() {
        Log.i(TAG,"bluetoothAdapterEnabled :")
    }

    override fun adverstiseStarted() {
        Log.i(TAG,"adverstiseStarted :")
    }
}