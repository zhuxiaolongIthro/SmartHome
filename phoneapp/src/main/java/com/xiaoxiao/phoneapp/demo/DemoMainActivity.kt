package com.xiaoxiao.phoneapp.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.xiaoxiao.baselibrary.bluetooth.ClassicBluetoothService
import com.xiaoxiao.phoneapp.R

class DemoMainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_main)
    }


    fun bleService(v: View?){
        startActivity(Intent(this,BleActivity::class.java))
    }
    fun wlanService(v:View?){
        startActivity(Intent(this,WlanActivity::class.java))
    }
    fun config(v:View?){
        startActivity(Intent(this,ConfigReadActivity::class.java))
    }
    fun btService(v:View?){
        startActivity(Intent(this,ServerBtActivity::class.java))
    }
    fun btClient(v: View?){
        startActivity(Intent(this,BluetoothActivity::class.java))
    }
}