package com.xiaoxiao.phoneapp

import com.xiaoxiao.baselibrary.wlan.IWlanP2pServiceCallback

class WlanServiceCallback:IWlanP2pServiceCallback.Stub() {
    override fun onFoundPeerList() {
    }

    override fun onPeerDisConnected(deviceName: String?, success: Boolean) {
    }

    override fun onReceiveMessage(msg: String?) {
    }

    override fun onPeerConnected(deviceName: String?, success: Boolean) {
    }
}