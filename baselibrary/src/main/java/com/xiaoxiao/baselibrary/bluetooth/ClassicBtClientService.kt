package com.xiaoxiao.baselibrary.bluetooth

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import com.xiaoxiao.baselibrary.base.BaseService
/**
 * 作为服务端蓝牙角色
 * */
class ClassicBtClientService : ClassicBluetoothService() {
    private val iBinder = object : IBtClientAidl.Stub() {
        override fun connectToService(mac: String?, uuid: String?) {
            Log.i("ClassicBluetoothService","connectToService : $mac")

            runOnWorkThread {
                for ((key_mac, device) in serviceList) {
                    if (TextUtils.equals(key_mac,mac)) {
                        val bluetoothConnection = BluetoothConnection()
                        conenctList.put(key_mac,bluetoothConnection)
                        bluetoothConnection.connectToServer(device)
                        bluetoothConnection.startListenerMsg {msg: String? ->
                            Log.i("ClassicBluetoothService","connectToService : 收消息回调 $msg")
                        }
                        break
                    }
                }
            }
        }

        override fun registCallabck(callback: IClassicServiceCallback?) {
            mCallback = callback
        }

        override fun sendMessage(msg: String?) {
            Log.i("ClassicBluetoothService","sendMessage :")
            if (TextUtils.isEmpty(msg)) {
                return
            }
            for ((key, connect) in conenctList) {
                connect.sendMsg(msg!!)
            }
        }

        override fun sendFile(path: String?) {
            Log.i("ClassicBluetoothService","sendFile :")
        }

        override fun discoverServer(mac: String?, uuid: String?) {
            Log.i("ClassicBluetoothService","discoverServer :")
            mBluetoothAdapter.startDiscovery()
            mainHandler.postDelayed({
                mBluetoothAdapter.cancelDiscovery()
            },10000)
        }
        override fun disconnect() {
            Log.i("ClassicBluetoothService","disconnect :")
        }
    }

    private val serviceList = HashMap<String, BluetoothDevice>()

    private val conenctList = HashMap<String,BluetoothConnection>()

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            runOnWorkThread {
                when (intent?.action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {//蓝牙硬件状态改变
                        Log.i("ClassicBluetoothService","onReceive 蓝牙改变")
                        when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,-1)) {
                            BluetoothAdapter.STATE_ON -> { }
                            BluetoothAdapter.STATE_TURNING_ON -> { }
                            BluetoothAdapter.STATE_OFF -> { }
                            BluetoothAdapter.STATE_TURNING_OFF -> { }
                        }
                    }
                    BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED->{
                        Log.i("ClassicBluetoothService","onReceive :链接状态改变")
                        val device =
                            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        when (intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,-1)) {
                            BluetoothAdapter.STATE_CONNECTED -> {
                                Log.i("ClassicBluetoothService","${device.name} ${device.address} 已连接")
                            }
                            BluetoothAdapter.STATE_CONNECTING -> {
                                Log.i("ClassicBluetoothService","${device.name} ${device.address} 正在连接")
                            }
                            BluetoothAdapter.STATE_DISCONNECTED -> {
                                Log.i("ClassicBluetoothService","${device.name} ${device.address} 已断开")
                            }
                            BluetoothAdapter.STATE_DISCONNECTING -> {
                                Log.i("ClassicBluetoothService","${device.name} ${device.address} 正在断开")
                            }
                        }
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {//蓝牙发现行为 开始
                        Log.i("ClassicBluetoothService","onReceive 发现开始")
                        serviceList.clear()//清空
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {//蓝牙发现行为 结束
                        Log.i("ClassicBluetoothService","onReceive 发现结束")
                        val callbackList = ArrayList<BluetoothDevice>()
                        for ((key,device) in serviceList) {
                            if(!TextUtils.isEmpty(key)){
                                callbackList.add(device)
                            }
                        }
                        mCallback?.onDiscoverFinished(callbackList)
                    }
                    BluetoothDevice.ACTION_FOUND -> {//发现设备
                        Log.i("ClassicBluetoothService","onReceive 发现设备")
                        synchronized(serviceList){
                            val foundedDevice =
                                intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                            serviceList[foundedDevice.address]=foundedDevice
                        }
                    }
                }
            }
        }
    }
    override fun onCreate() {
        super.onCreate()
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val intentFilter = IntentFilter()
        for (action in actions) {
            intentFilter.addAction(action)
        }
        registerReceiver(bluetoothReceiver, intentFilter)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return iBinder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        unregisterReceiver(bluetoothReceiver)
        super.onDestroy()
    }
}
