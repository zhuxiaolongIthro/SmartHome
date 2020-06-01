package com.xiaoxiao.baselibrary.cloud

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * 云服务接口 通过广域互联网 传输信息
 * */
class CloudApiService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}
