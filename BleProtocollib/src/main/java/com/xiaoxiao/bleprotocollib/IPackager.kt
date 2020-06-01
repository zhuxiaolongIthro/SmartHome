package com.xiaoxiao.bleprotocollib
/**
 * ble 信息 打包/解包
 * 由于 ble 信息长度的限制，需要对较长的数据进行分包处理
 * */
interface IPackager {
    fun needSplit(msg:String):Boolean

    fun splitMessage(longMsg:String):ArrayList<ByteArray>

    fun needPackage():Boolean

    fun packageMessage(packages:ArrayList<ByteArray>):ByteArray

}