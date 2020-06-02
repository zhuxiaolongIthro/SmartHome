package com.xiaoxiao.homecenter.drivers

import android.os.Handler
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.userdriver.pio.GpioDriver
import java.lang.IllegalArgumentException

/**
 * 纯输入 硬件
 * */
class AM312_Driver(out: String)
    : GpioDriver {
    val peripheralManager = PeripheralManager.getInstance()
    val outGpio :Gpio
    init {
        outGpio = peripheralManager.openGpio(out)
        outGpio.setEdgeTriggerType(Gpio.EDGE_BOTH)//挑拨触发
        outGpio.setDirection(Gpio.DIRECTION_IN)// 输入
        outGpio.setActiveType(Gpio.ACTIVE_HIGH)//高电平有效
    }
    override fun setValue(p0: Boolean) {
        throw IllegalArgumentException("当前输入设备不支持设置 value")
    }

    override fun setDirection(p0: Int) {
        Log.i("AM312_Driver", "setDirection")
    }

    override fun open() {
        Log.i("AM312_Driver", "open")
    }

    override fun setActiveType(p0: Int) {
        Log.i("AM312_Driver", "setActiveType")
    }

    override fun registerGpioCallback(p0: Handler?, p1: GpioCallback?) {
        Log.i("AM312_Driver", "registerGpioCallback")
    }

    override fun setEdgeTriggerType(p0: Int) {
        Log.i("AM312_Driver", "setEdgeTriggerType")
    }

    override fun unregisterGpioCallback(p0: GpioCallback?) {
        Log.i("AM312_Driver", "unregisterGpioCallback")
    }

    override fun getValue(): Boolean {
        Log.i("AM312_Driver", "getValue")
        return outGpio.value
    }

    override fun close() {
        Log.i("AM312_Driver", "close")
    }
}