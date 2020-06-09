package com.xiaoxiao.baselibrary.bluetooth

import android.app.Service
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import com.xiaoxiao.baselibrary.base.BaseService
import java.util.*
import kotlin.collections.HashMap

/**
 * 作为服务端蓝牙角色
 * 角色运行模式以 广播以及等待链接为主
 * */
class ClassicBtServerService : ClassicBluetoothService() {
    private val iBinder = object : IBtServerAidl.Stub() {
        override fun startAdversitise() {
            Log.i("ClassicBtServerService", "startAdversitise :")
            /*  val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
              intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,3600)
              startActivity(intent)*/
        }

        override fun registCallabck(callback: IClassicServiceCallback?) {
            mCallback = callback
        }

        override fun sendMessage(msg: String?) {
            Log.i("ClassicBluetoothService", "sendMessage :")
            if (TextUtils.isEmpty(msg)) {
                return
            }
            for ((key, connnect) in conenctedSocketList) {
                connnect.sendMsg(msg!!)
            }
        }

        override fun sendFile(path: String?) {
            Log.i("ClassicBluetoothService", "sendFile :")
        }

        override fun disconnect() {
            Log.i("ClassicBluetoothService", "disconnect :")
            for ((key, connection) in conenctedSocketList) {
                connection.disconnect()
            }
            conenctedSocketList.clear()
        }
    }
    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            runOnWorkThread {
                when (intent?.action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {//蓝牙硬件状态改变
                        Log.i("ClassicBluetoothService", "onReceive 蓝牙改变")
                        when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                            BluetoothAdapter.STATE_ON -> {
                                //蓝牙启动 开启监听连接接入
                                waitingClient()
                            }

                            BluetoothAdapter.STATE_OFF -> {
                                //蓝牙关闭 停止监听连接接入
                                release()
                            }
                            BluetoothAdapter.STATE_TURNING_ON -> { }
                            BluetoothAdapter.STATE_TURNING_OFF -> { }
                        }
                    }
                    BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED -> {
                        Log.i("ClassicBluetoothService", "onReceive :链接状态改变")
                        val device =
                            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        when (intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1)) {
                            BluetoothAdapter.STATE_CONNECTED -> {
                                Log.i(
                                    "ClassicBluetoothService",
                                    "${device.name} ${device.address} 已连接"
                                )
                            }
                            BluetoothAdapter.STATE_CONNECTING -> {
                                Log.i(
                                    "ClassicBluetoothService",
                                    "${device.name} ${device.address} 正在连接"
                                )
                            }
                            BluetoothAdapter.STATE_DISCONNECTED -> {
                                Log.i(
                                    "ClassicBluetoothService",
                                    "${device.name} ${device.address} 已断开"
                                )
                            }
                            BluetoothAdapter.STATE_DISCONNECTING -> {
                                Log.i(
                                    "ClassicBluetoothService",
                                    "${device.name} ${device.address} 正在断开"
                                )

                            }

                        }
                    }
                    BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                        val device =
                            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        Log.i("ClassicBtServerService", "onReceive : 绑定状态变化")

                        when (intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)) {
                            BluetoothDevice.BOND_BONDED -> {//已绑定
                            }
                            BluetoothDevice.BOND_BONDING -> {// 等待绑定完成
                            }
                            BluetoothDevice.BOND_NONE -> {// 解绑
                            }
                        }
                    }

                }
            }
        }
    }

    private val conenctedSocketList = HashMap<String, BluetoothConnection>()
    private lateinit var serverSocket: BluetoothServerSocket


    override fun onCreate() {
        super.onCreate()
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val intentFilter = IntentFilter()
        for (action in actions) {
            intentFilter.addAction(action)
        }
        registerReceiver(bluetoothReceiver, intentFilter)

        serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
            "name",
            UUID.fromString("f74829c3-e67e-4bae-9241-95887a7205cd")
        )
        waitingClient()
    }

    private fun waitingClient() {
        val bluetoothConnection = BluetoothConnection(bluetoothServerSocket = serverSocket)
        bluetoothConnection.waitingClient { socket ->
            conenctedSocketList[socket.remoteDevice.address] = bluetoothConnection
            Log.i("ClassicBtServerService","waitingClient : 建立连接完成！！")
        }
    }
    private fun release(){
        for ((key,connection) in conenctedSocketList) {
            connection.disconnect()
        }
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
        release()
        super.onDestroy()
    }
}
