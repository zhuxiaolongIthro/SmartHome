package com.xiaoxiao.phoneapp.demo

import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.xiaoxiao.baselibrary.bluetooth.ClassicBtServerService
import com.xiaoxiao.baselibrary.bluetooth.IBtServerAidl
import com.xiaoxiao.baselibrary.bluetooth.IClassicBtServiceAidl
import com.xiaoxiao.baselibrary.bluetooth.IClassicServiceCallback
import com.xiaoxiao.phoneapp.R

class ServerBtActivity : AppCompatActivity() {
    lateinit var iBinder: IBtServerAidl

    private val callback =object :IClassicServiceCallback{
        override fun onFileSended(state: Int) {
            Log.i("ServerBtActivity","onFileSended :")
        }

        override fun onAdversitiseStateChange(state: Int) {
            Log.i("ServerBtActivity","onAdversitiseStateChange :")
        }

        override fun onReceiveMessage(msg: String?) {
            Log.i("ServerBtActivity","onReceiveMessage :")
        }

        override fun onDiscoverFinished(devicesJson: MutableList<BluetoothDevice>?) {
            Log.i("ServerBtActivity","onDiscoverFinished :")
        }

        override fun onDiscoverServerStateChange(state: Int) {
            Log.i("ServerBtActivity","onDiscoverServerStateChange :")
        }

        override fun onConnectStateChanged(state: Int, mac: String?) {
            Log.i("ServerBtActivity","onConnectStateChanged :")
        }

        override fun onMessageSended() {
            Log.i("ServerBtActivity","onMessageSended :")
        }

        override fun asBinder(): IBinder? {
         Log.i("ServerBtActivity","asBinder :")
            return null
        }

        override fun onReceiveFile(tempPath: String?) {
            Log.i("ServerBtActivity","onReceiveFile :")
        }

    }


    val conn = object :ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i("ServerBtActivity","onServiceDisconnected :")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i("ServerBtActivity","onServiceConnected :")
            if (service != null) {
                iBinder=IBtServerAidl.Stub.asInterface(service)
                iBinder.registCallabck(callback)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_bt)
        val intent = Intent(this, ClassicBtServerService::class.java)
        bindService(intent,conn, Service.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        unbindService(conn)
        super.onDestroy()
    }
}