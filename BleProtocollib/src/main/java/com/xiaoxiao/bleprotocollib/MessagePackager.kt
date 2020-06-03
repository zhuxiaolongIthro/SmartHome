package com.xiaoxiao.bleprotocollib

import com.sun.xml.internal.ws.util.ByteArrayBuffer
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.reflect.Array
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

@Suppress("UNREACHABLE_CODE")
public class MessagePackager(val maxLengh: Int = 20) : IPackager {

    companion object {
        val defaultChartset = Charsets.UTF_8

        val BLE_MAX_PACKAGE_SIZE = 20 //ble 的最长数据长度

        val TYPE_MASK = 0x03

        val DATA_HEAD_START = 0xFD.toByte() //分包开始
        val DATA_HEAD_DATA = 0xFC.toByte()//正常数据
        val DATA_HEAD_END = 0xFE.toByte()// 分包结束
    }
    /**
     * 如果分包  结构应该如何？
     *
     * 最长为 20 个字节   如果是分包数据
     * 第一个字节 为当前分包索引号 其余 18 个字节为数据内容
     * 最后一个字节 作为结束标识？？？
     * 定义数据帧
     * 第一个字节 8bit  111111 高6位预留 00 低2位 代表该帧 类型  00-起始帧  01-普通数据帧  10-结束帧 11-没有分包的数据
     *  第二个字节 8bit ....
     *
     * */


    /**
     * 判断信息是否需要被分包
     * */
    override fun needSplit(msg: String): Boolean {
        val byteArray = msg.toByteArray(defaultChartset)

        return byteArray.size >= maxLengh
    }

    /**
     * 执行分包
     * */
    override fun splitMessage(longMsg: String): ArrayList<ByteArray> {
        val result = ArrayList<ByteArray>()
        val toByteArray = longMsg.toByteArray(defaultChartset)
        val byteArrayInputStream = ByteArrayInputStream(toByteArray)

        val tempByteArray = ByteArray(maxLengh - 1)
        while (byteArrayInputStream.read(tempByteArray) != -1) {
            val byteArray = ByteArray(maxLengh)
            byteArray[0] = DATA_HEAD_DATA
            tempByteArray.copyInto(
                destination = byteArray,
                destinationOffset = 1,
                startIndex = 0,
                endIndex = tempByteArray.size - 1
            )
            result.add(byteArray)
        }
        result[0][0] = DATA_HEAD_START
        result[result.size - 1][0] = DATA_HEAD_END
        return result
    }

    /**
     * 判断接受到的数据是否需要进行 打包处理
     * */
    override fun needPackage(byteArray: ByteArray): Boolean {
        when (TYPE_MASK.and(byteArray[0].toInt())) {
            DATA_HEAD_START.toInt(),
            DATA_HEAD_DATA.toInt(),
            DATA_HEAD_END.toInt() -> {
                return true
            }

            else -> {
                return false
            }
        }
    }

    override fun packageMessage(packages: ArrayList<ByteArray>): ByteArray {
        TODO("Not yet implemented")
    }
}
