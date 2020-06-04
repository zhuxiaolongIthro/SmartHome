package com.xiaoxiao.baselibrary.bluetooth

import android.bluetooth.*
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
import java.util.*

/**
 * 首先选择要传输的文件，然后选择目标设备，执行连接，连接成功后开始发送文件，发送成功后断开连接
 * */
class ClassicBluetoothService : BaseService() {
    val DISCOVER = 0
    val CONNECT = 1
    val SENT = 2

    companion object{
        const val DEFAULT_DISCOVER_TIMEOUT=30
        const val SERVER_SOCKET_UUID = "f74829c3-e67e-4bae-9241-95887a7205cd"
    }


    var configFilePath :String?=null
    var clientDeviceMac:String?=null
    private val iBinder = object : IClassicBtServiceAidl.Stub() {
        override fun startAdversitise() {
            TODO("Not yet implemented")
        }

        override fun connectToService(mac: String?, uuid: String?) {
            TODO("Not yet implemented")
        }

        override fun registCallabck(callback: IClassicServiceCallback?) {
            mCallback = callback
        }

        override fun sendMessage(msg: String?) {
            TODO("Not yet implemented")
        }

        override fun sendFile(path: String?) {
            TODO("Not yet implemented")
        }

        override fun discoverServer(mac: String?, uuid: String?) {
            TODO("Not yet implemented")
        }

        override fun startListener() {
            TODO("Not yet implemented")
        }


    }

    var mCallback: IClassicServiceCallback? = null


    private val mReceiveThread = HandlerThread("receiveSocket").apply { start() }
    private val mSendThread = HandlerThread("sendSocket").apply { start() }


    private val receiveHandler = object :Handler(mReceiveThread.looper){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                CONNECT -> {//开始连接
                    //创建socket
                    mServiceBluetoothSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("文件", UUID.fromString(SERVER_SOCKET_UUID))
                    //等待客户端连接
                    mServiceBluetoothSocket.accept()
                }
                else -> {
                }
            }


        }
    }
    private val mSendHandler = object :Handler(mSendThread.looper){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }


    lateinit var mBluetoothAdapter: BluetoothAdapter
    lateinit var mBluetoothManager: BluetoothManager

    lateinit var mServiceBluetoothSocket:BluetoothServerSocket
    var clientSocket:BluetoothSocket?=null
    var targetDevice:BluetoothDevice?=null


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
                        receiveHandler.sendEmptyMessage(CONNECT)
                    }
                    BluetoothDevice.ACTION_FOUND -> {//发现设备
                        Log.i("ClassicBluetoothService","onReceive")
                        val foundedDevice =
                            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        if (foundedDevice.address.equals(clientDeviceMac)) {
                            targetDevice = foundedDevice
                        }
                    }
                }
            }
        }
    }
    private fun connectDevice(device: BluetoothDevice){
        val createInsecureRfcommSocketToServiceRecord =
            device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(SERVER_SOCKET_UUID))

    }


    private fun discoverDevice() {
        mBluetoothAdapter.startDiscovery()
        receiveHandler.postDelayed(Runnable {
            mBluetoothAdapter.cancelDiscovery()
        }, DEFAULT_DISCOVER_TIMEOUT*1000L)
    }


    override fun onCreate() {
        super.onCreate()
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
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