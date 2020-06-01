package com.xiaoxiao.phoneapp

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.xiaoxiao.baselibrary.ble.BleClientService
import com.xiaoxiao.baselibrary.ble.IBleService
import com.xiaoxiao.baselibrary.ble.IBleServiceCallback

class PhoneMainActivity : AppCompatActivity() {


    lateinit var mBinder : IBleService

    val mCallback:IBleServiceCallback =ClientServiceCallback()

    private val bleConn = object :ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mBinder = IBleService.Stub.asInterface(service)
            mBinder.registCallback(mCallback)
        }
    }


    lateinit var asServerBtn:Button
    lateinit var scanBtn:Button
    lateinit var sendBtn:Button
    lateinit var inputEt:EditText
    lateinit var resultTv:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, BleClientService::class.java)
        bindService(intent,bleConn,Service.BIND_AUTO_CREATE)


        asServerBtn = findViewById(R.id.as_server)
        scanBtn = findViewById(R.id.scan_btn)
        sendBtn = findViewById(R.id.send_msg)
        inputEt = findViewById(R.id.input)
        resultTv = findViewById(R.id.result_tv)


        asServerBtn.setOnClickListener {
            mBinder.startAdvertise()
        }
        scanBtn.setOnClickListener {
            mBinder.stopAdvertise()
            mBinder.scanLeSensors(0,10,"")
        }

        sendBtn.setOnClickListener {
            val msg = inputEt.text.toString()
            mBinder.sendMessage(msg)
        }

    }

    override fun onDestroy() {
        unbindService(bleConn)
        super.onDestroy()
    }
}
