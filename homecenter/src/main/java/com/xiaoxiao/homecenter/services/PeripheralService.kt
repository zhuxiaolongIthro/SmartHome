package com.xiaoxiao.homecenter.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.userdriver.UserDriverManager
import com.google.android.things.userdriver.pio.GpioDriver
import com.google.android.things.userdriver.sensor.UserSensor
import com.xiaoxiao.baselibrary.base.BaseService
import com.xiaoxiao.homecenter.IPeripheralServiceCallback
import java.util.*
import kotlin.collections.ArrayList

/**
 *  硬件设备服务
 *
 * */
class PeripheralService : BaseService() {

    lateinit var peripheralManager: PeripheralManager
    lateinit var userDriverManager: UserDriverManager


    lateinit var mCallback: IPeripheralServiceCallback


    val iBinder: IBinder = object : IPeripheralServiceAIDL.Stub() {
        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String?
        ) {

        }

    }

    val handlerThread = HandlerThread("listenerThread").apply { start() }

    val listenerHandler = Handler(handlerThread.looper)

    val mGpioCallback = object : GpioCallback {
        override fun onGpioEdge(gpio: Gpio?): Boolean {
            Log.i("PeripheralService", "onGpioEdge ${gpio?.name} ${gpio?.value}")
            if (gpio?.name.equals("BCM26")) {
                if (gpio?.value == true) {
                    openLed()
                }else{
                    closeLed()
                }
            }
            return true
        }

        override fun onGpioError(gpio: Gpio?, error: Int) {
//            super.onGpioError(gpio, error)

            Log.i("PeripheralService", "onGpioError ${gpio?.name} ${gpio?.value} error $error")
        }
    }

    val gpioList = ArrayList<Gpio>()

    private fun openLed() {
        Log.i("PeripheralService","openLed ")
        LED_OUTPUT.value = true
    }

    private fun closeLed() {
        Log.i("PeripheralService","closeLed")
        LED_OUTPUT.value = false
    }

    lateinit var AM312_INPUT: Gpio
    lateinit var LED_OUTPUT: Gpio

    override fun onCreate() {
        super.onCreate()
        peripheralManager = PeripheralManager.getInstance()
        userDriverManager = UserDriverManager.getInstance()

        AM312_INPUT = peripheralManager.openGpio("BCM26")
        AM312_INPUT.setActiveType(Gpio.ACTIVE_HIGH)//设置为高电平 有效
        AM312_INPUT.setDirection(Gpio.DIRECTION_IN)
        AM312_INPUT.setEdgeTriggerType(Gpio.EDGE_BOTH)
        AM312_INPUT.registerGpioCallback(listenerHandler, mGpioCallback)


        LED_OUTPUT = peripheralManager.openGpio("BCM13")
        LED_OUTPUT.setActiveType(Gpio.ACTIVE_HIGH)//设置为高电平 有效
        LED_OUTPUT.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)

    }

    override fun onBind(intent: Intent): IBinder {
        return iBinder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
