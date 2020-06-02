package com.xiaoxiao.phoneapp.demo

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.xiaoxiao.baselibrary.ble.IBleService
import com.xiaoxiao.baselibrary.ble.IBleServiceCallback
import com.xiaoxiao.baselibrary.wlan.IWlanP2pServiceAidl
import com.xiaoxiao.baselibrary.wlan.WLANService
import com.xiaoxiao.phoneapp.ClientServiceCallback
import com.xiaoxiao.phoneapp.R
import com.xiaoxiao.phoneapp.WlanServiceCallback

class WlanActivity : AppCompatActivity() {

    lateinit var mWlanBinder : IWlanP2pServiceAidl


    val mWlanServiceCallback = WlanServiceCallback()

    private val wifiConn = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i("WlanActivity","onServiceDisconnected :")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i("WlanActivity","onServiceConnected :")
            mWlanBinder = IWlanP2pServiceAidl.Stub.asInterface(service)
            mWlanBinder.registCallback(mWlanServiceCallback)
            mWlanBinder.startDiscoverPeers(10000)
        }

    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("WlanActivity","onCreate :")
        setContentView(R.layout.activity_wlan)
    }

    override fun onStart() {
        Log.i("WlanActivity","onStart :")
        super.onStart()
        val intent = Intent(this, WLANService::class.java)
        bindService(intent,wifiConn,Service.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        Log.i("WlanActivity","onStop :")
        super.onStop()
        unbindService(wifiConn)
    }

    override fun onDestroy() {
        Log.i("WlanActivity","onDestroy :")
        super.onDestroy()
    }
}