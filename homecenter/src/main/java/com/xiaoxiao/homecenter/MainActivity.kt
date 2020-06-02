package com.xiaoxiao.homecenter

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.xiaoxiao.baselibrary.ble.BleCenterService
import com.xiaoxiao.baselibrary.ble.IBleService
import com.xiaoxiao.baselibrary.wlan.IWlanP2pServiceAidl
import com.xiaoxiao.baselibrary.wlan.WLANService
import com.xiaoxiao.homecenter.services.PeripheralService

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the PeripheralManager
 * For example, the snippet below will open a GPIO pin and set it to HIGH:
 *
 * val manager = PeripheralManager.getInstance()
 * val gpio = manager.openGpio("BCM6").apply {
 *     setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * }
 * gpio.value = true
 *
 * You can find additional examples on GitHub: https://github.com/androidthings
 */
class MainActivity : AppCompatActivity() {


    lateinit var iBinder:IBleService

    val mCallback =BleServiceCallback()

    private val bleConn = object :ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            iBinder.stopAdvertise()

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            iBinder = IBleService.Stub.asInterface(service)
            iBinder.registCallback(mCallback)
            iBinder.startAdvertise()
        }
    }

    private val peripheralConn = object :ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, BleCenterService::class.java)

        bindService(intent,bleConn,Service.BIND_AUTO_CREATE)

        val sensorIntent = Intent(this,PeripheralService::class.java)
        bindService(sensorIntent,peripheralConn,Service.BIND_AUTO_CREATE)

        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager


        if (!wifiManager.isP2pSupported) {
            return
        }
    }

    override fun onDestroy() {
        unbindService(bleConn)
        unbindService(peripheralConn)
        super.onDestroy()
    }
}
