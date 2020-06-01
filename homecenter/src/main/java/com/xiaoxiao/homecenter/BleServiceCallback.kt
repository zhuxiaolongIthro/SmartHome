package com.xiaoxiao.homecenter

import android.os.IBinder
import android.util.Log
import com.xiaoxiao.baselibrary.ble.IBleServiceCallback

class BleServiceCallback:IBleServiceCallback.Stub() {

    override fun onConnectedAsPeripheral(keyword: String?) {
        Log.i("BleServiceCallback","onConnectedAsPeripheral")
    }

    override fun disconnectFromSensor(keyword: String?) {
    Log.i("BleServiceCallback","disconnectFromSensor")
    }

    override fun onReceiveMessage(msg: String?) {
        Log.i("BleServiceCallback","onReceiveMessage")
    }

    override fun onLeScanningStarted() {
        Log.i("BleServiceCallback","onLeScanningStarted")
    }

    override fun connectToSensor(keyword: String?) {
        Log.i("BleServiceCallback","connectToSensor")
    }

    override fun onLeScanningStoped() {
        Log.i("BleServiceCallback","onLeScanningStoped")
    }

    override fun bluetoothAdapterEnabled() {
        Log.i("BleServiceCallback","bluetoothAdapterEnabled")
    }

    override fun waitingConnectAsPeripheral() {
        Log.i("BleServiceCallback","waitingConnectAsPeripheral")
    }

    override fun onLeDeviceFounded(keyword: String?) {
        Log.i("BleServiceCallback","onLeDeviceFounded")
    }

    override fun bluetoothAdapterdisabled() {
        Log.i("BleServiceCallback","bluetoothAdapterdisabled")
    }

    override fun adverstiseStarted() {
        Log.i("BleServiceCallback","adverstiseStarted")
    }

    override fun disconnectedAsPeripheral(keyword: String?) {
        Log.i("BleServiceCallback","disconnectedAsPeripheral")
    }

    override fun adverstiseStoped() {
        Log.i("BleServiceCallback","adverstiseStoped")
    }

}