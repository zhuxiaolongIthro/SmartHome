package com.xiaoxiao.phoneapp.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.xiaoxiao.baselibrary.config.ConfigReader
import com.xiaoxiao.phoneapp.R

class ConfigReadActivity : AppCompatActivity() {

    val configReader = ConfigReader()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_read)


//        configReader.readFromRes(this,R.xml.config)
        configReader.readFromAssets(this,"config.json")
    }
}