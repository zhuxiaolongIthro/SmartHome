package com.xiaoxiao.baselibrary.ble

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.google.android.things.bluetooth.BluetoothProfile
import com.xiaoxiao.baselibrary.base.BaseService
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

/**
 * 核心功能：
 * 1、作为外围设备 发起广播等待链接
 * 2、作为中心设备 扫描外围蓝牙传感器设备，并发起链接
 * 因为可以认为没有操作界面 所以自动开启 服务模式
 * */
class BleService : BaseService(), BluetoothAdapter.LeScanCallback {

    companion object {
        const val SERVICE_TRANSFER_MSG_UUID = "3518d48c-ea83-495c-922d-273a0d4e21ca"
        const val CHARTRANSFER_MSG_NOTIFY_UUID = "3518d48a-ea83-495c-922d-273a0d4e21cb"
        const val CHARTRANSFER_MSG_WRITE_UUID = "3518d48b-ea83-495c-922d-273a0d4e21cc"
    }

    private lateinit var mCallback: IBleServiceCallback


    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mLeAdvertiser: BluetoothLeAdvertiser

    private lateinit var mBluetoothManager: BluetoothManager

    private lateinit var mBleGattServer: BluetoothGattServer

    private lateinit var mGattService :BluetoothGattService
    private lateinit var mNoticyCharacteristic:BluetoothGattCharacteristic
    private lateinit var mWriteCharacteristic: BluetoothGattCharacteristic
//
//    private var timer = Timer()
//    private val timerTask =object :TimerTask(){
//        override fun run() {
//            Log.i("BleCenterService","timer run")
//            for (device in connectedClientList) {
//
//                val service = mBleGattServer.getService(UUID.fromString(SERVICE_TRANSFER_MSG_UUID))
//                val characteristic =
//                    service.getCharacteristic(UUID.fromString(CHARTRANSFER_MSG_NOTIFY_UUID))
//                characteristic.value = "你也好".toByteArray()
//                mBleGattServer.notifyCharacteristicChanged(device,characteristic,true)
//            }
//
//        }
//    }

    /**
     * 广播接收器
     * */
    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

        }

    }
    private val mGattServerCallback = object : BluetoothGattServerCallback() {

        /**
         * 已经发送了通知
         * */
        override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
            super.onNotificationSent(device, status)
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.i("BleCenterService", "onNotificationSent success")
                }
                BluetoothGatt.GATT_FAILURE->{
                    Log.i("BleCenterService", "onNotificationSent failure")
                }
            }
        }
        /**
         * 队列执行的 write 操作
         * */
        override fun onExecuteWrite(device: BluetoothDevice?, requestId: Int, execute: Boolean) {
            super.onExecuteWrite(device, requestId, execute)
            Log.i("BleCenterService", "onExecuteWrite")
        }

        /**
         * 写入信息
         * */
        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic?,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            super.onCharacteristicWriteRequest(
                device,
                requestId,
                characteristic,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )
            Log.i("BleService","onCharacteristicWriteRequest requestId:$requestId offset $offset")
            mBleGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, characteristic?.value?.size?:0, value)
            if (value != null) {
                val string = String(value, Charset.defaultCharset())
                Log.i("BleCenterService", "onCharacteristicWriteRequest  $string")
            } else {
                Log.i("BleCenterService", "onCharacteristicWriteRequest empty value")
            }

        }
        /**
         * 读取信息
         * */
        override fun onCharacteristicReadRequest(
            device: BluetoothDevice?,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
            Log.i("BleCenterService", "onCharacteristicReadRequest")
            mBleGattServer.sendResponse(device,requestId,BluetoothGatt.GATT_SUCCESS,characteristic?.value?.size?:0,characteristic?.value)
        }

        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            Log.i("BleCenterService", "onConnectionStateChange")
            when (newState) {
                android.bluetooth.BluetoothProfile.STATE_CONNECTED -> {//设备连接
                    Log.i("BleCenterService","onConnectionStateChange 已连接")
                    if (device!=null) {
                        connectedClientList.add(device)
//                        timer = Timer()
//                        timer.schedule(timerTask,2000,2000)
                    }
                }
                android.bluetooth.BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.i("BleCenterService","onConnectionStateChange 已断开")
                    if (device != null) {
                        val iterator = connectedClientList.iterator()
                        while (iterator.hasNext()) {
                            val connectDevice = iterator.next()
                            if (connectDevice.address.equals(device.address)) {
                                iterator.remove()
                            }
                        }
//                        timer.cancel()
                    }
                }
                else -> {
                }
            }
        }

        override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
            super.onServiceAdded(status, service)
            Log.i("BleCenterService", "onServiceAdded")
        }
    }

    /**
     * 已配对的设备集合
     * */
    private val bondedDeviceList = ArrayList<BluetoothDevice>()

    /**
     *  扫描到的设备集合
     * */
    private val scanedDeviceList = ArrayList<BluetoothDevice>()
    private val connectedClientList = ArrayList<BluetoothDevice>()

    /**
     * 广播 状态回调
     * */
    private val mAdvertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {//成功开启设备广播
            super.onStartSuccess(settingsInEffect)
            Log.i("BleCenterService", "onStartSuccess")
        }

        override fun onStartFailure(errorCode: Int) {//开启设备广播失败
            super.onStartFailure(errorCode)
            Log.i("BleCenterService", "onStartFailure")
        }
    }

    /**
     * 低功耗外围设备扫描回调
     *
     * */
    override fun onLeScan(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray?) {

    }


    private val mBinder: IBinder = object : IBleService.Stub() {
        override fun registCallback(callback: IBleServiceCallback?) {
            Log.i("BleCenterService", "registCallback")
            callback?.let {
                mCallback = it
            }
        }

        /**
         * 开始广播
         * */
        override fun startAdvertise() {
            Log.i("BleCenterService", "startAdvertise")
            mBluetoothAdapter
                .bluetoothLeAdvertiser
                .startAdvertising(
                    buildAdvertiseSettings(),
                    buildAdvertiseData(),
                    mAdvertiseCallback
                )
        }

        /**
         * 关闭广播
         * */
        override fun stopAdvertise() {
            Log.i("BleCenterService", "stopAdvertise")
            mBluetoothAdapter
                .bluetoothLeAdvertiser
                .stopAdvertising(mAdvertiseCallback)
        }

        /**
         * 断开外围传感器的链接
         * */
        override fun disconnectFromSensor(keyWord: String?) {
            Log.i("BleCenterService", "disconnectFromSensor")
        }

        /**
         * 发起对外围传感器的链接
         * */
        override fun connectToSensor(keyWord: String?) {
            Log.i("BleCenterService", "connectToSensor")
        }

        /**
         * 开始扫描外围BLE 传感器
         * @param delay 延迟开始
         * @param period 扫描周期
         * @param keyWord 扫描关键字
         * */
        override fun scanLeSensors(delay: Long, period: Long, keyWord: String?) {
            Log.i("BleCenterService", "scanLeSensors")
        }

        /**
         * 向传感器发送消息
         * */
        override fun sendMessage(msg: String?) {
            Log.i("BleCenterService", "sendMessage")
        }
    }

    private fun selfStopAdvertise() {

    }

    private fun selfStartAdvertise() {

    }

    private fun buildAdvertiseSettings(): AdvertiseSettings {
        val builder = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)//有电源 不需要考虑电力损耗 采用低延迟广播
            .setConnectable(true)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
        return builder.build()
    }

    private fun buildAdvertiseData(): AdvertiseData {
        val builder = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .setIncludeTxPowerLevel(true)
        return builder.build()
    }

    private fun buildServices(): BluetoothGattService {
        mBleGattServer.clearServices()//清空原有的services
        mGattService = BluetoothGattService(//基本通信服务
            UUID.fromString(SERVICE_TRANSFER_MSG_UUID),
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )
        mNoticyCharacteristic = BluetoothGattCharacteristic(//基本订阅 特征，客户端通过这个获取订阅的消息
            UUID.fromString(CHARTRANSFER_MSG_NOTIFY_UUID),
            BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        )

        mWriteCharacteristic = BluetoothGattCharacteristic(// 基本写入 特征，客户端通过这个向center 写入命令
            UUID.fromString(CHARTRANSFER_MSG_WRITE_UUID),
            BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )

        mGattService.addCharacteristic(mNoticyCharacteristic)
        mGattService.addCharacteristic(mWriteCharacteristic)
        return mGattService
    }

    //region service生命周期
    /**
     * 生命周期
     * */
    override fun onCreate() {
        super.onCreate()
        Log.i("BleCenterService", "onCreate")
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBleGattServer = mBluetoothManager.openGattServer(this, mGattServerCallback)
        mBleGattServer.addService(buildServices())
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)//硬件状态
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)//配对状态改变
        registerReceiver(bluetoothStateReceiver, intentFilter)

    }

    override fun onBind(intent: Intent): IBinder {
        Log.i("BleCenterService", "onBind")
        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i("BleCenterService", "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        Log.i("BleCenterService", "onRebind")
        super.onRebind(intent)
    }

    override fun onDestroy() {
        Log.i("BleCenterService", "onDestroy")
        unregisterReceiver(bluetoothStateReceiver)
        for (connectedDevice in mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT)) {
            mBleGattServer.cancelConnection(connectedDevice)
        }
        mBleGattServer.clearServices()
        mBleGattServer.close()
        super.onDestroy()
    }
    //endregion
}
