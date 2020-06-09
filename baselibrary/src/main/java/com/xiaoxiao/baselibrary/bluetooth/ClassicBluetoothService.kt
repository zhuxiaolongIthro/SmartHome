package com.xiaoxiao.baselibrary.bluetooth

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
 * 首先选择要传输的文件，然后选择目标设备，执行连接，连接成功后开始发送文件，发送成功后断开连接
 * */
abstract class ClassicBluetoothService : BaseService() {
    var mCallback: IClassicServiceCallback? = null

    lateinit var mBluetoothAdapter: BluetoothAdapter
    lateinit var mBluetoothManager: BluetoothManager

    private val serviceList = HashMap<String,BluetoothDevice>()

    private val conenctList = HashMap<String,BluetoothConnection>()


    protected val actions = arrayListOf<String>(
        BluetoothAdapter.ACTION_STATE_CHANGED,
        BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED,
        BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE,
        BluetoothAdapter.ACTION_DISCOVERY_STARTED,
        BluetoothAdapter.ACTION_DISCOVERY_FINISHED,
        BluetoothDevice.ACTION_BOND_STATE_CHANGED,
        BluetoothDevice.ACTION_FOUND
    )

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            runOnWorkThread {
                when (intent?.action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {//蓝牙硬件状态改变
                        Log.i("ClassicBluetoothService","onReceive 蓝牙改变")
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
    }

}