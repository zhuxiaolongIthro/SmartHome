package com.xiaoxiao.baselibrary.config

import android.content.Context
import androidx.collection.ArrayMap
import java.io.InputStreamReader

class ConfigReader {
    companion object {
        //  命令码与命令json串 映射表
        val commandTable = ArrayMap<Int, Command>()
    }

//    /**
//     * 从xml 资源文件
//     * */
//    @Throws(Resources.NotFoundException::class)
//    fun readFromRes(context: Context,resId:Int){
//        val parser = context.resources.getXml(resId)
//        trivelXml(parser)
//    }


//    fun trivelXml(parser:XmlResourceParser){
//        loop@while (true){
//            when(parser.eventType){
//                XmlResourceParser.START_DOCUMENT->{
////                    Log.i("ConfigReader","readFromRes : start_document")
//                }
//                XmlResourceParser.START_TAG->{
//                    Log.i("ConfigReader","readFromRes : start_tag ${parser.name}")
//                }
//                XmlResourceParser.END_TAG->{
//                    Log.i("ConfigReader","readFromRes : end_tag ${parser.name}")
//                }
//                XmlResourceParser.END_DOCUMENT->{
////                    Log.i("ConfigReader","readFromRes : end_document")
//                    break@loop
//                }
//            }
//            parser.next()
//        }
//    }

    fun readFromAssets(context: Context, configFile: String) {
        val assetsManager = context.resources.assets
        val inputStream = assetsManager.open(configFile)
        val stringBuilder = StringBuilder()
        val isReader = InputStreamReader(inputStream)
        for (readLine in isReader.readLines().apply { inputStream.close() }) {
            stringBuilder.append(readLine)
        }
//        val gson = Gson()
//
//        val configBean = gson.fromJson<ConfigBean>(stringBuilder.toString(), ConfigBean::class.java)
//
//        configBean?.let { bean ->
//            for (command in bean.config.commands) {
//                commandTable.put(command.code, command)
//            }
//        }
    }


}