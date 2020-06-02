package com.xiaoxiao.baselibrary.base

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import java.util.concurrent.Executors

abstract class BaseService : Service() {
    companion object{
        val threadPool = Executors.newFixedThreadPool(10)
    }
    val mainHandler = Handler(Looper.getMainLooper())


    @MainThread
    fun runOnMainThread(task:()->Unit){
        mainHandler.post {
            task.invoke()
        }
    }
    @MainThread
    fun runOnMainThread(runnable: Runnable){
        mainHandler.post(runnable)
    }
    fun runOnWorkThread(task:()->Unit){
        threadPool.submit(Runnable {
            Log.e("BaseService","runOnWorkThread : ${Thread.currentThread().name}")
            task.invoke()
        })
    }
    fun runOnWorkThread(runnable: Runnable){
        threadPool.submit(runnable)
    }

}
