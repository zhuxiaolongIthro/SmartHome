package com.xiaoxiao.homecenter

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.xiaoxiao.baselibrary.ble.BleCenterService
import com.xiaoxiao.baselibrary.ble.IBleService
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

    private val wifip2pCoon = object :ServiceConnection{
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

        val wifiIntent = Intent(this,WLANService::class.java)
        bindService(wifiIntent,wifip2pCoon,Service.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        unbindService(bleConn)
        unbindService(peripheralConn)
        unbindService(wifip2pCoon)
        super.onDestroy()
    }
}
