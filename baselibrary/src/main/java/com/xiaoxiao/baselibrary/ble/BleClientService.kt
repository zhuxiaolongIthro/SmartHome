package com.xiaoxiao.baselibrary.ble

import android.bluetooth.*
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.xiaoxiao.baselibrary.base.BaseService
import java.util.*
import kotlin.collections.ArrayList

/**
 * 手机端的 蓝牙通信服务
 * */
class BleClientService : BaseService() {
    val TAG ="BleClientService"
    companion object {
        const val SERVICE_TRANSFER_MSG_UUID = "3518d48c-ea83-495c-922d-273a0d4e21ca"
        const val CHARTRANSFER_MSG_NOTIFY_UUID = "3518d48c-ea83-495c-922d-273a0d4e21ca"
        const val CHARTRANSFER_MSG_WRITE_UUID = "3518d48c-ea83-495c-922d-273a0d4e21ca"
    }

    private lateinit var mCallback: IBleServiceCallback


    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mLeAdvertiser: BluetoothLeAdvertiser

    private lateinit var mBluetoothManager: BluetoothManager

    private lateinit var mBleGattServer: BluetoothGattServer

    /**
     * 广播接收器
     * */
    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(TAG,"onReceive :")
        }

    }

    private val mGattCallback = object :BluetoothGattCallback(){
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            Log.i(TAG,"onConnectionStateChange :")
        }
    }

    private val mGattServerCallback = object : BluetoothGattServerCallback() {
        override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
            super.onNotificationSent(device, status)
            Log.i(TAG,"onNotificationSent :")
        }

        override fun onExecuteWrite(device: BluetoothDevice?, requestId: Int, execute: Boolean) {
            super.onExecuteWrite(device, requestId, execute)
            Log.i(TAG,"onExecuteWrite :")
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
            Log.i(TAG,"onCharacteristicWriteRequest :")
        }

        override fun onCharacteristicReadRequest(
            device: BluetoothDevice?,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
            Log.i(TAG,"onCharacteristicReadRequest :")
        }

        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            Log.i(TAG,"onConnectionStateChange :")
        }

        override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
            super.onServiceAdded(status, service)
            Log.i(TAG,"onServiceAdded :")
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
     * 已连接的ble设备
     * */
    private val connectedGattDeviceList = ArrayList<BluetoothGatt>()

    /**
     * 广播 状态回调
     * */
    private val mAdvertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {//成功开启设备广播
            super.onStartSuccess(settingsInEffect)
            Log.i(TAG,"advertise onStartSuccess :")
        }

        override fun onStartFailure(errorCode: Int) {//开启设备广播失败
            super.onStartFailure(errorCode)
            Log.i(TAG," advertise onStartFailure : errorCode $errorCode")
        }
    }

    /**
     * 低功耗蓝牙扫描回调
     * */
    private val mLeScanCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.i(TAG,"leScanCallback onScanFailed :$errorCode")
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            Log.i(TAG,"leScanCallack onScanResult :${result?.device?.name}")
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            Log.i(TAG,"onBatchScanResults :")
        }
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
            mBluetoothAdapter
                .bluetoothLeAdvertiser
                .stopAdvertising(mAdvertiseCallback)
        }

        /**
         * 断开外围传感器的链接
         * */
        override fun disconnectFromSensor(keyWord: String?) {
            for (gatt in connectedGattDeviceList) {

            }
            val device = findTargetDevice(keyWord)
        }

        /**
         * 发起对外围传感器的链接
         * */
        override fun connectToSensor(keyWord: String?) {
            val device = findTargetDevice(keyWord)
            device?.run {
                val connectGatt = connectGatt(this@BleClientService, true, mGattCallback)
                connectedGattDeviceList.add(connectGatt)
                connectGatt
            }
        }

        /**
         * 开始扫描外围BLE 传感器
         * @param delay 延迟开始
         * @param period 扫描周期
         * @param keyWord 扫描关键字
         * */
        override fun scanLeSensors(delay: Long, period: Long, keyWord: String?) {
            mBluetoothAdapter.bluetoothLeScanner.startScan(mLeScanCallback)
        }

        /**
         * 向传感器发送消息
         * */
        override fun sendMessage(msg: String?) {
            TODO("Not yet implemented")
        }
    }

    private fun findTargetDevice(keyWord: String?): BluetoothDevice? {
        for (bluetoothDevice in scanedDeviceList) {

            var isKeyWordUuid = false
            bluetoothDevice.uuids?.forEach { parcelUuid ->
                isKeyWordUuid = parcelUuid.uuid.toString().equals(keyWord)
                if (isKeyWordUuid) {
                    return@forEach
                }
            }

            if (bluetoothDevice.name.equals(keyWord)
                || bluetoothDevice.address.equals(keyWord)
                || isKeyWordUuid) {
                return bluetoothDevice
            }
        }
        return null
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
        mBleGattServer = mBluetoothManager.openGattServer(this, mGattServerCallback)
        mBleGattServer.addService(buildServices())
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)//硬件状态
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)//配对状态改变
        registerReceiver(bluetoothStateReceiver, intentFilter)
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
//        for (connectedDevice in mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT)) {
//            mBleGattServer.cancelConnection(connectedDevice)
//        }
        mBleGattServer.clearServices()
        mBleGattServer.close()
        super.onDestroy()
    }
    //endregion
}
