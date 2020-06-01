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
import com.xiaoxiao.baselibrary.base.BaseService
import com.xiaoxiao.homecenter.IPeripheralServiceCallback

/**
 *  硬件设备服务
 *
 * */
class PeripheralService : BaseService() {

    lateinit var peripheralManager:PeripheralManager
    lateinit var userDriverManager:UserDriverManager




    lateinit var mCallback:IPeripheralServiceCallback


    val iBinder:IBinder = object :IPeripheralServiceAIDL.Stub(){
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

    val mGpioCallback = object :GpioCallback{
        override fun onGpioEdge(gpio: Gpio?): Boolean {
            Log.i("PeripheralService","onGpioEdge ${gpio?.name} ${gpio?.value}")
            return false
        }

        override fun onGpioError(gpio: Gpio?, error: Int) {
//            super.onGpioError(gpio, error)
            Log.i("PeripheralService","onGpioError ${gpio?.name} ${gpio?.value} error $error")
        }
    }

    val gpioList=ArrayList<Gpio>()

    override fun onCreate() {
        super.onCreate()
        peripheralManager = PeripheralManager.getInstance()
        userDriverManager = UserDriverManager.getInstance()

        for (gpioName in peripheralManager.gpioList) {
//            Log.i("PeripheralService","gpio : $gpioName")
            val openedGpio = peripheralManager.openGpio(gpioName)
            openedGpio.setActiveType(Gpio.ACTIVE_LOW)//设置为高电平 有效
            openedGpio.registerGpioCallback(listenerHandler,mGpioCallback)
            gpioList.add(openedGpio)
        }

        for (gpio in gpioList) {
            Log.i("PeripheralService","gpio value  ${gpio.name} : ${gpio.value}")
        }

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
