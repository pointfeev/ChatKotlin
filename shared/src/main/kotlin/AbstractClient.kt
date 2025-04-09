package pointfeev.shared

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset

abstract class AbstractClient(var socket: Socket? = null) {
    private var input: InputStream? = null
    private var output: OutputStream? = null

    companion object {
        val charset: Charset = Charsets.UTF_8
    }

    var state: ClientState = ClientState.INITIALIZING

    fun connect(): Boolean {
        if (state == ClientState.CONNECTING) {
            return false
        }
        state = ClientState.CONNECTING

        try {
            input = socket!!.getInputStream()
            output = socket!!.getOutputStream()
        } catch (e: Exception) {
            onConnectFailure()
            disconnect()
            return false
        }
        if (!onConnect()) {
            onConnectFailure()
            disconnect()
            return false
        }
        return true
    }

    abstract fun onConnectFailure()
    abstract fun onConnect(): Boolean

    fun listen() {
        if (state == ClientState.LISTENING) {
            return
        }
        state = ClientState.LISTENING

        while (receive()) {
            Thread.sleep(200)
        }
        disconnect()
    }

    fun disconnect() {
        if (state == ClientState.DISCONNECTING) {
            return
        }
        state = ClientState.DISCONNECTING

        if (input != null) {
            try {
                input!!.close()
            } catch (e: IOException) {
                // ignore
            }
            input = null
        }
        if (output != null) {
            try {
                output!!.close()
            } catch (e: IOException) {
                // ignore
            }
            output = null
        }

        if (socket == null) {
            return
        }
        try {
            socket!!.close()
        } catch (e: IOException) {
            // ignore
        }
        onDisconnect()
        socket = null
    }

    abstract fun onDisconnect()

    fun receive(): Boolean {
        try {
            val bytes: MutableList<Byte> = mutableListOf()
            while (true) {
                val nextByte: Int = input!!.read()
                if (nextByte == -1) {
                    return false
                }
                if (nextByte == 0) {
                    break
                }
                bytes.add(nextByte.toByte())
            }
            return onReceive(bytes.toByteArray())
        } catch (e: IOException) {
            // ignore
        }
        return false
    }

    abstract fun onReceive(bytes: ByteArray): Boolean

    fun send(bytes: ByteArray) {
        if (state != ClientState.LISTENING) {
            return
        }

        try {
            output!!.write(bytes + 0)
        } catch (e: IOException) {
            onSendFailure(bytes)
            disconnect()
        }
    }

    fun send(byte: Byte) {
        send(byteArrayOf(byte))
    }

    fun send(string: String) {
        send(string.toByteArray(charset))
    }

    abstract fun onSendFailure(bytes: ByteArray)
}