package com.xiaoxiao.baselibrary.bluetooth

import android.bluetooth.*
import android.os.Handler
import android.os.Looper
import java.io.*
import java.util.*

/**
 * socket 管理
 * */
typealias BluetoothConnectionCallback = () -> Unit
typealias MessageListener = (msg: String?) -> Unit
typealias SocketConnectCallback = (socket: BluetoothSocket) -> Unit

class BluetoothConnection(
    var bluetoothServerSocket: BluetoothServerSocket?=null,
    var remoteDevice: BluetoothDevice?=null,
    var autoReconnect: Boolean = false
) {
    enum class ConnectionState {
        WAITING, CONNECTING, CONNECTED, DISCONNECTED, DISCOVERING, UNDEFINE
    }

    companion object {
        const val DEFAULT_DISCOVER_TIMEOUT = 30
        const val SERVER_SOCKET_UUID = "f74829c3-e67e-4bae-9241-95887a7205cd"
    }

    lateinit var readStream: InputStream
    lateinit var writeStream: OutputStream

    lateinit var bufferReader: BufferedReader
    lateinit var bufferWriter: BufferedWriter

    var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    var state = ConnectionState.UNDEFINE

    val mainHandler = Handler(Looper.getMainLooper())

    lateinit var clientSocket: BluetoothSocket

    val receiveThread: ReceiveThread by lazy {
        ReceiveThread()
    }
    val sendThread: MessageHandlerThread by lazy {
        MessageHandlerThread("sendThread")
    }
    val messageHandlerThread: MessageHandlerThread by lazy {
        MessageHandlerThread("listenThread")
    }

    /**
     * 作为客户端去链接服务端
     * 通过discover 发现的远端device 作为服务端
     * */
    fun connectToServer(remoteDevice: BluetoothDevice) {
        receiveThread.post {
            clientSocket = remoteDevice.createInsecureRfcommSocketToServiceRecord(
                UUID.fromString(SERVER_SOCKET_UUID)
            )
            /*blocking*/
            clientSocket.connect() //主动链接服务端  服务端会在accept中获取
            /*链接成功*/
            buildSocketStreams(clientSocket)
        }
    }

    fun waitingClient(callback: SocketConnectCallback) {
        receiveThread.post {
            /*blocking*/
            clientSocket = bluetoothServerSocket?.accept()!!//来自 服务端的socket
            /*链接成功*/
            buildSocketStreams(clientSocket)
            callback.invoke(clientSocket)
        }
    }

    private fun buildSocketStreams(socket: BluetoothSocket) {
        readStream = socket.inputStream
        bufferReader = BufferedReader(InputStreamReader(readStream))
        writeStream = socket.outputStream
        bufferWriter = BufferedWriter(OutputStreamWriter(writeStream))
    }

    fun startListenerMsg(listener: MessageListener) {
        messageHandlerThread.post {
            while (true) {
                try {
                    //阻塞读取数据
                    val readLine = bufferReader.readLine()
                    //主线程回调数据通知
                    mainHandler.post { listener(readLine) }
                } catch (e: Exception) {
                    e.printStackTrace()//链接意外断开
                }
            }
        }
    }

    fun sendMsg(msg: String) {
        sendThread.post {
            bufferWriter.write(msg)
            bufferWriter.newLine()
            bufferWriter.flush()
        }
    }

    fun disconnect() {
        when (state) {
            ConnectionState.CONNECTED -> {//如果已经连接
                bufferReader.close()
                bufferWriter.flush()
                bufferWriter.close()
                clientSocket.close()
            }
            ConnectionState.UNDEFINE->{

            }
            ConnectionState.WAITING->{//等待接入
                receiveThread.quit()
                sendThread.quit()
                messageHandlerThread.quit()
            }
            ConnectionState.CONNECTING->{//正在连接中

            }

            ConnectionState.DISCONNECTED->{//已经断开

            }
        }

    }

}