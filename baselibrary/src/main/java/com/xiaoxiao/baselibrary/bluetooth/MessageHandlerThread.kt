package com.xiaoxiao.baselibrary.bluetooth

import android.os.Handler
import android.os.HandlerThread

class MessageHandlerThread(name: String?) : HandlerThread(name) {
    private val handler : Handler
    init {
        start()
        handler = Handler(looper)
    }


    fun post(f:()->Unit){
        handler.post {
            f.invoke()
        }
    }
}