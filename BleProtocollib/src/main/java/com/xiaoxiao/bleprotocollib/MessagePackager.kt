package com.xiaoxiao.bleprotocollib

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

@Suppress("UNREACHABLE_CODE")
public class MessagePackager(val maxLengh:Int=20):IPackager {

    companion object{
        val defaultChartset = Charsets.UTF_8
    }
    /**
     * 如果分包  结构应该如何？
     *
     * 最长为 20 个字节   如果是分包数据
     * 第一个字节 为当前分包索引号 其余 18 个字节为数据内容
     * 最后一个字节 作为结束标识？？？
     *
     *
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
//
//        val byteArrayOutputStream = ByteArrayOutputStream()
//
//        val toByteArray = longMsg.toByteArray(defaultChartset)
//
//        byteArrayOutputStream.write(toByteArray)
//
        return result
    }

    override fun needPackage(): Boolean {
        TODO("Not yet implemented")
    }

    override fun packageMessage(packages: ArrayList<ByteArray>): ByteArray {
        TODO("Not yet implemented")
    }
}
