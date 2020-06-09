package com.xiaoxiao.phoneapp.demo

import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import androidx.appcompat.app.AlertDialog
import com.xiaoxiao.baselibrary.bluetooth.*
import com.xiaoxiao.phoneapp.R

class BluetoothActivity : AppCompatActivity() {

    lateinit var iBinder : IBtClientAidl

    var mCallback =object :IClassicServiceCallback.Stub(){
        override fun onFileSended(state: Int) {
            Log.i("BluetoothActivity","onFileSended :")
        }

        override fun onAdversitiseStateChange(state: Int) {
            Log.i("BluetoothActivity","onAdversitiseStateChange :")
        }

        override fun onReceiveMessage(msg: String?) {
            Log.i("BluetoothActivity","onReceiveMessage :")
        }

        override fun onDiscoverFinished(devicesJson: MutableList<BluetoothDevice>?) {
            Log.i("BluetoothActivity","onDiscoverFinished : ${devicesJson}")
            if (devicesJson != null) {
                showDialog(devicesJson)
            }
        }

        override fun onDiscoverServerStateChange(state: Int) {
            Log.i("BluetoothActivity","onDiscoverServerStateChange :")
        }

        override fun onConnectStateChanged(state: Int, mac: String?) {
            Log.i("BluetoothActivity","onConnectStateChanged :")
        }

        override fun onMessageSended() {
            Log.i("BluetoothActivity","onMessageSended :")

        }

        override fun asBinder(): IBinder {
            Log.i("BluetoothActivity","asBinder :")
            return super.asBinder()
        }

        override fun onReceiveFile(tempPath: String?) {
            Log.i("BluetoothActivity","onReceiveFile :")
        }

    }



    fun  showDialog(deviceList:List<BluetoothDevice>){
        Log.i("BluetoothActivity","showDialog :")
        for (bluetoothDevice in deviceList) {
            Log.i("BluetoothActivity","showDialog : ${bluetoothDevice.name}")
        }
    }

    val btConn = object :ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service != null) {
                iBinder = IBtClientAidl.Stub.asInterface(service)
                iBinder.registCallabck(mCallback)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        val intent = Intent(this, ClassicBtClientService::class.java)
        bindService(intent,btConn,Service.BIND_AUTO_CREATE)

    }

    override fun onDestroy() {
        unbindService(btConn)
        super.onDestroy()
    }

    fun scan_bt(v: View?){
        iBinder.discoverServer("","")
    }
    fun connect_bt(v:View?){
        iBinder.connectToService("","")
    }
    fun disconnect_bt(v:View?){
        iBinder.disconnect()
    }
}