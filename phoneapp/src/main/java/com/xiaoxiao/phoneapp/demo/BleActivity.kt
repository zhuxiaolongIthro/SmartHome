package com.xiaoxiao.phoneapp.demo

import android.content.ComponentName
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.xiaoxiao.baselibrary.ble.IBleService
import com.xiaoxiao.baselibrary.ble.IBleServiceCallback
import com.xiaoxiao.baselibrary.wlan.IWlanP2pServiceAidl
import com.xiaoxiao.phoneapp.ClientServiceCallback
import com.xiaoxiao.phoneapp.R
import com.xiaoxiao.phoneapp.WlanServiceCallback

class BleActivity : AppCompatActivity() {

    lateinit var mBleBinder : IBleService

    val mBleServiceCallback: IBleServiceCallback = ClientServiceCallback()

    private val bleConn = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mBleBinder = IBleService.Stub.asInterface(service)
            mBleBinder.registCallback(mBleServiceCallback)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ble)
    }
}