package com.xiaoxiao.baselibrary.wifip2p

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import com.xiaoxiao.baselibrary.base.BaseService

class WifiP2pService : BaseService() {

    lateinit var mWifip2pManager:WifiP2pManager

    lateinit var mWifiManager:WifiManager

    val iBinder = object :IWifiP2pServiceAidl.Stub(){
        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String?
        ) {
            Log.i("WifiP2pService","basicTypes")
        }
    }

    val handlerThread = HandlerThread("p2pWork").apply {
        start()
    }

    val wifiActionListener =object :WifiP2pManager.ActionListener{
        override fun onSuccess() {
            Log.i("WifiP2pService","onSuccess")
        }

        override fun onFailure(reason: Int) {
            Log.i("WifiP2pService","onFailure")
        }
    }



    override fun onCreate() {
        super.onCreate()
        Log.i("WifiP2pService","onCreate")
        mWifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        Log.i("WifiP2pService","p2p ${mWifiManager.isP2pSupported}")
        Log.i("WifiP2pService","wifi ${mWifiManager.isWifiEnabled}")
        Log.i("WifiP2pService","state ${mWifiManager.wifiState==WifiManager.WIFI_STATE_ENABLED}")


//        mWifip2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
//        mWifip2pManager.initialize(this,handlerThread.looper) {
//            Log.i("WifiP2pService","listener")
//        }
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i("WifiP2pService","onBind")
        return iBinder
    }

    override fun onRebind(intent: Intent?) {
        Log.i("WifiP2pService","onRebind")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i("WifiP2pService","onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.i("WifiP2pService","onDestroy")
        super.onDestroy()
    }
}
