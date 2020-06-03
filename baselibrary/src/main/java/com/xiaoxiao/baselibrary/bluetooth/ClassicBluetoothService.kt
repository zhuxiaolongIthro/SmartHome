package com.xiaoxiao.baselibrary.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Message
import android.util.Log
import com.xiaoxiao.baselibrary.base.BaseService
import java.io.File
/**
 * 首先选择要传输的文件，然后选择目标设备，执行连接，连接成功后开始发送文件，发送成功后断开连接
 * */
class ClassicBluetoothService : BaseService() {
    val DISCOVER = 0
    val CONNECT = 1
    val SENT = 2

    companion object{
        const val DEFAULT_DISCOVER_TIMEOUT=30
    }


    var configFilePath :String?=null
    var clientDeviceMac:String?=null
    private val iBinder = object : IClassicBtServiceAidl.Stub() {
        override fun sendFile(path: String?, mac: String?) {
            val file = File(path)
            if (file.exists().not()) {
                mCallback?.onFileSended(0)//文件不存在
            }
            configFilePath = path
            clientDeviceMac = mac
            discoverDevice()
        }

        override fun registCallabck(callback: IClassicServiceCallback?) {
            mCallback = callback
        }

        override fun connectToDevice(mac: String?) {

        }

    }

    var mCallback: IClassicServiceCallback? = null


    private val mWorkThread = HandlerThread("bluetoothwork").apply { start() }
    private val mWorkHandler = object : Handler(mWorkThread.looper) {


        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)


        }
    }

    lateinit var mBluetoothAdapter: BluetoothAdapter

    lateinit var mServiceBluetoothSocket:BluetoothServerSocket
    var clientSocket:BluetoothSocket?=null


    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            runOnWorkThread {
                when (intent?.action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {//蓝牙硬件状态改变
                        Log.i("ClassicBluetoothService","onReceive")
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {//蓝牙发现行为 开始
                        Log.i("ClassicBluetoothService","onReceive")
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {//蓝牙发现行为 结束
                        Log.i("ClassicBluetoothService","onReceive")
                    }
                    BluetoothDevice.ACTION_FOUND -> {//发现设备
                        Log.i("ClassicBluetoothService","onReceive")
                        val foundedDevice =
                            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        if (foundedDevice.address.equals(clientDeviceMac)) {

                        }
                    }
                }
            }

        }

    }


    private fun discoverDevice() {
        mBluetoothAdapter.startDiscovery()
        mWorkHandler.postDelayed(Runnable {
            mBluetoothAdapter.cancelDiscovery()
        }, DEFAULT_DISCOVER_TIMEOUT*1000L)
    }


    override fun onCreate() {
        super.onCreate()
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
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