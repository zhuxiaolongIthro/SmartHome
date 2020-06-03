# SmartHome

## BaseLibrary 基础功能库 包含通用的 service
    -  经典蓝牙 通过bluetoothSocket 进行数据传递
    -  BLE 通过notify 等短数据操作
    -  wlan 局域网内通信 采用socket 进行通信
    -  cloud 网络服务器 Http/Https mqtt等方式
    -  usb usb串口通信功能
    -  config 配置文件，与ble 协议 目的相同，通过配置文件设置两端统一的 命令表，达到缩减通信数据长度的目的
## BleProtocolLib 低功耗蓝牙 通信协议 处理通过BLE 传输数据
    - BLE 单次消息 20byte 长度限制 较长消息需要进行分包处理
## HomeCenter AndroidThings 上运行的APP
    - 作为核心服务运行，提供客户端接入，传感器驱动，控制功能具体实现
## PhoneApp 手机平板上运行的APP
    - 作为客户端运行，提供链接到AndroidThings 核心服务，并读取传感器数据，发送操作指令，数据、文件上传等功能
## WatchApp AndroidWear APP
    - 手表app功能与手机端类似，硬件限制 功能会有所删减
