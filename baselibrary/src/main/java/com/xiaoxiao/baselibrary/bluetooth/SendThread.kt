package com.xiaoxiao.baselibrary.bluetooth

import android.os.HandlerThread
import java.util.logging.Handler

class SendThread:HandlerThread("sendThread") {

    init {
        start()
    }


    fun sendMessage(msg:String?){




    }
}