package com.xiaoxiao.baselibrary.bluetooth

import android.os.Handler
import android.os.HandlerThread

class ReceiveThread:HandlerThread("receiveThread") {
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

    fun startListening(){
        handler.post {
            while (true){




            }
        }
    }
}