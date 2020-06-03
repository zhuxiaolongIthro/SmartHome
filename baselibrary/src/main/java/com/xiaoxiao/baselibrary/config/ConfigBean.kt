package com.xiaoxiao.baselibrary.config

/**
 * 配置文件
 * */
data class ConfigBean(
    var config: Config
)

data class Config(
    var commands: List<Command>
)

data class Command(
    var body: Body? = null,
    var code: Int? = null
)

data class Body(
    var action: Int? = null,
    var params: ArrayList<String>? = null
)