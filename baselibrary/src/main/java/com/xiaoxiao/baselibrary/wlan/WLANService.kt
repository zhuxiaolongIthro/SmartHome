package com.xiaoxiao.baselibrary.wlan

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.net.wifi.p2p.*
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import com.xiaoxiao.baselibrary.base.BaseService
import java.net.ServerSocket
import java.net.Socket

class WLANService : BaseService() {

    val filterActionList = arrayListOf<String>(
        WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION,
        WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION,
        WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION,
        WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION,
        WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION
    )

    private val discoveredPeerDeviceList = ArrayList<WifiP2pDevice>()

    lateinit var mCallback: IWlanP2pServiceCallback

    private var isDiscovering = false

    private val iBinder: IBinder = object : IWlanP2pServiceAidl.Stub() {
        override fun registCallback(callback: IWlanP2pServiceCallback?) {
            Log.i("WLANService", "registCallback :")
            if (callback != null) {
                mCallback = callback
            }
        }

        /**
         * @param duration 扫描持续时间
         *
         * */
        override fun startDiscoverPeers(duration: Long) {
            Log.i("WLANService", "startDiscoverPeers :")
            mWifiP2pManager.discoverPeers(mChannel,object :WifiP2pManager.ActionListener{
                override fun onSuccess() {
                    isDiscovering = true
                    Log.i("WLANService"," discover peers onSuccess :")
                    mainHandler.postDelayed({
                        stopDiscoverPeers()
                    },duration)
                }

                override fun onFailure(reason: Int) {
                    Log.i("WLANService","discover peers onFailure : $reason")
                }

            })
        }

        /**
         * 停止 discover 操作
         * */
        override fun stopDiscoverPeers() {
            Log.i("WLANService", "stopDiscoverPeers :")
            isDiscovering = false
            mWifiP2pManager.stopPeerDiscovery(mChannel,object :WifiP2pManager.ActionListener{
                override fun onSuccess() {
                    Log.i("WLANService","stop discover peers onSuccess :")
                }

                override fun onFailure(reason: Int) {
                    Log.i("WLANService","stop discover peers onFailure :")
                }

            })
        }

        /**
         * @param deviceName 断开连接的设备名称
         * */
        override fun disconnectFromPeer(deviceName: String?) {
            Log.i("WLANService", "disconnectFromPeer :")
        }

        /**
         * 向已连接的peerDevice 发送消息
         * */
        override fun sendMessage(msg: String?) {
            Log.i("WLANService", "sendMessage : $msg")
            if (msg == null) {
                return
            }

            runOnWorkThread {
                val write = mClientSocket?.getOutputStream()
                write?.write(msg.toByteArray(Charsets.UTF_8))
                write?.flush()
            }
        }

        /**
         * @param deviceName 要建立连接的设备名称
         * */
        override fun connectToPeer(deviceName: String?) {
            Log.i("WLANService", "connectToPeer :")
        }

    }
    lateinit var mWifiManager:WifiManager
    lateinit var mWifiP2pManager: WifiP2pManager
    lateinit var mChannel: WifiP2pManager.Channel

    private val p2pReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("WLANService", "onReceive : $intent")
            runOnWorkThread {//交给线程池 进行异步处理
                when (intent?.action) {
                    WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                        val p2pDevice =
                            intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                        Log.i("WLANService", "onReceive :${p2pDevice}")
                    }
                    WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                        val intExtra = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                        when (intExtra) {
                            WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                                Log.i("WLANService", "onReceive : p2p enable")
                            }
                            WifiP2pManager.WIFI_P2P_STATE_DISABLED -> {
                                Log.i("WLANService", "onReceive : p2p disable")
                            }
                        }

                    }
                    WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION -> {
                        val intExtra = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1)
                        when (intExtra) {
                            WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED -> {
                                Log.i("WLANService", "onReceive : p2p discover 开始")
                            }
                            WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED -> {
                                Log.i("WLANService", "onReceive : p2p discover 停止")
                            }
                        }

                    }
                    WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                        //检查当前 discover 状态 如果是停止状态 不进行列表更新
                        if(!isDiscovering)return@runOnWorkThread
                        val peerList =
                            intent.getParcelableExtra<WifiP2pDeviceList>(WifiP2pManager.EXTRA_P2P_DEVICE_LIST)
                        Log.i("WLANService", "onReceive : peers 改变  $peerList")
                        discoveredPeerDeviceList.addAll(peerList.deviceList)

                        //如果有 目标device 执行连接操作
                        var targetDevice :WifiP2pDevice?=null
                        discoveredPeerDeviceList.forEach { wifiDevice->
                            if (wifiDevice.deviceName.equals("OO")) {
                                targetDevice = wifiDevice
                                return@forEach
                            }
                        }
                        if (targetDevice == null) {
                            return@runOnWorkThread
                        }

                        val wifiP2pInfo = WifiP2pConfig()
                        wifiP2pInfo.deviceAddress=targetDevice?.deviceAddress
                        mWifiP2pManager.connect(mChannel,wifiP2pInfo,object :WifiP2pManager.ActionListener{
                            override fun onSuccess() {
                                Log.i("WLANService","connect p2p device onSuccess :")
                                buildSocket()
                            }

                            override fun onFailure(reason: Int) {
                                Log.i("WLANService","connect p2p device onFailure :")
                            }
                        })
                    }
                    WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                        Log.i("WLANService", "onReceive : p2p 连接状态发生改变")
                        val p2pInfo =
                            intent.getParcelableExtra<WifiP2pInfo>(WifiP2pManager.EXTRA_WIFI_P2P_INFO)
                        val p2pGroup =
                            intent.getParcelableExtra<WifiP2pGroup>(WifiP2pManager.EXTRA_WIFI_P2P_GROUP)
                        val networkInfo =
                            intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)

                        Log.i("WLANService", "onReceive : p2piInfor ${p2pInfo}")
                        Log.i("WLANService", "onReceive : p2pGroup ${p2pGroup}")
                        Log.i("WLANService", "onReceive : netWorkInfo ${networkInfo}")
                    }
                }
            }
        }
    }

    private val workThread = HandlerThread("workThread").apply { start() }
    private val workHandler = Handler(workThread.looper)
    private  var mServerSocket: ServerSocket?=null
    private  var mClientSocket:Socket?=null
    private fun buildSocket(){

        workHandler.post {
            Log.i("WLANService","buildSocket :等待socket连接")
            mServerSocket = ServerSocket()
            mClientSocket=mServerSocket?.accept()
            Log.i("WLANService","buildSocket : 完成连接 等待发送消息")
        }
    }


    //region 生命周期
    override fun onCreate() {
        super.onCreate()
        Log.i("WLANService", "onCreate :")
        //初始化需要资源
        mWifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        mWifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        mChannel = mWifiP2pManager.initialize(this, workThread.looper) {
            Log.i("WLANService", "initialize channeldisconnected callback :")
        }
        val intentFilter = IntentFilter()
        for (action in filterActionList) intentFilter.addAction(action)
        registerReceiver(p2pReceiver, intentFilter)
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i("WLANService", "onBind :")
        return iBinder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.i("WLANService", "onRebind :")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i("WLANService", "onUnbind :")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.i("WLANService", "onDestroy :")
        mWifiP2pManager.cancelConnect(mChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.i("WLANService", "cancelConnecton Success :")
            }

            override fun onFailure(reason: Int) {
                Log.i("WLANService", "cancelConnect onFailure :")
            }
        })
        mChannel.close()
        unregisterReceiver(p2pReceiver)
        super.onDestroy()

    }
//endregion
}
