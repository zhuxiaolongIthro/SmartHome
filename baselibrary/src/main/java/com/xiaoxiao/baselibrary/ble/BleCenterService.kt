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
import com.google.android.things.bluetooth.BluetoothProfile
import com.xiaoxiao.baselibrary.base.BaseService
import java.util.*
import kotlin.collections.ArrayList

/**
 * 核心功能：
 * 1、作为外围设备 发起广播等待链接
 * 2、作为中心设备 扫描外围蓝牙传感器设备，并发起链接
 *
 * */
class BleCenterService : BaseService(), BluetoothAdapter.LeScanCallback {

    companion object{
        const val SERVICE_TRANSFER_MSG_UUID=""
        const val CHARTRANSFER_MSG_NOTIFY_UUID=""
        const val CHARTRANSFER_MSG_WRITE_UUID=""
    }

    private lateinit var mCallback: IBleServiceCallback


    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mLeAdvertiser: BluetoothLeAdvertiser

    private lateinit var mBluetoothManager: BluetoothManager

    private lateinit var mBleGattServer :BluetoothGattServer

    /**
     * 广播接收器
     * */
    private val bluetoothStateReceiver = object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {

        }

    }

    private val mGattServerCallback = object :BluetoothGattServerCallback(){
        override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
            super.onNotificationSent(device, status)
        }

        override fun onExecuteWrite(device: BluetoothDevice?, requestId: Int, execute: Boolean) {
            super.onExecuteWrite(device, requestId, execute)
        }

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
        }

        override fun onCharacteristicReadRequest(
            device: BluetoothDevice?,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
        }

        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
        }

        override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
            super.onServiceAdded(status, service)
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


    /**
     * 广播 状态回调
     * */
    private val mAdvertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {//成功开启设备广播
            super.onStartSuccess(settingsInEffect)
        }

        override fun onStartFailure(errorCode: Int) {//开启设备广播失败
            super.onStartFailure(errorCode)
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
            callback?.let {
                mCallback = it
            }
        }

        /**
         * 开始广播
         * */
        override fun startAdvertise() {
            //TODO 检查广播是否已经开启
            mBluetoothAdapter
                .bluetoothLeAdvertiser
                .startAdvertising(buildAdvertiseSettings(),buildAdvertiseData(),mAdvertiseCallback)
        }

        /**
         * 关闭广播
         * */
        override fun stopAdvertise() {
            mBluetoothAdapter
                .bluetoothLeAdvertiser
                .stopAdvertising(mAdvertiseCallback)
        }

        /**
         * 断开外围传感器的链接
         * */
        override fun disconnectFromSensor(keyWord: String?) {
            TODO("Not yet implemented")
        }

        /**
         * 发起对外围传感器的链接
         * */
        override fun connectToSensor(keyWord: String?) {
            TODO("Not yet implemented")
        }

        /**
         * 开始扫描外围BLE 传感器
         * @param delay 延迟开始
         * @param period 扫描周期
         * @param keyWord 扫描关键字
         * */
        override fun scanLeSensors(delay: Long, period: Long, keyWord: String?) {
            TODO("Not yet implemented")
        }

        /**
         * 向传感器发送消息
         * */
        override fun sendMessage(msg: String?) {
            TODO("Not yet implemented")
        }
    }

    private fun buildAdvertiseSettings(): AdvertiseSettings {
        val builder = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)//有电源 不需要考虑电力损耗 采用低延迟广播
            .setConnectable(true)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
        return builder.build()
    }
    private fun buildAdvertiseData():AdvertiseData{
        val builder = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .setIncludeTxPowerLevel(true)
        return builder.build()
    }

    private fun buildServices():BluetoothGattService{
        val service = BluetoothGattService(
            UUID.fromString(SERVICE_TRANSFER_MSG_UUID),
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )
        val noticyCharacteristic = BluetoothGattCharacteristic(
            UUID.fromString(CHARTRANSFER_MSG_NOTIFY_UUID),
            BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        )

        val writeCharacteristic = BluetoothGattCharacteristic(
            UUID.fromString(CHARTRANSFER_MSG_WRITE_UUID),
            BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )

        service.addCharacteristic(noticyCharacteristic)
        service.addCharacteristic(writeCharacteristic)
        return service
    }

    //region service生命周期
    /**
     * 生命周期
     * */
    override fun onCreate() {
        super.onCreate()
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBleGattServer = mBluetoothManager.openGattServer(this,mGattServerCallback)
        mBleGattServer.addService(buildServices())
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)//硬件状态
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)//配对状态改变
        registerReceiver(bluetoothStateReceiver,intentFilter)
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onDestroy() {
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
