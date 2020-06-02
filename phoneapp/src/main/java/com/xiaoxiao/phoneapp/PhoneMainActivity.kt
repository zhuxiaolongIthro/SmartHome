package com.xiaoxiao.phoneapp

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.xiaoxiao.baselibrary.ble.BleClientService
import com.xiaoxiao.baselibrary.ble.IBleService
import com.xiaoxiao.baselibrary.ble.IBleServiceCallback
import com.xiaoxiao.baselibrary.wlan.IWlanP2pServiceAidl
import com.xiaoxiao.baselibrary.wlan.WLANService

class PhoneMainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
