package pointfeev.server

import pointfeev.shared.ClientState
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class Client(private val socket: Socket) {
    private var input: InputStream? = null
    private var output: OutputStream? = null

    var state: ClientState = ClientState.INITIALIZING

    companion object {
        var lastId: Int = 0
    }

    val id: Int = ++lastId

    override fun toString(): String {
        return "Client #%s".format(id)
    }

    init {
        Thread {
            if (!connect()) {
                println("WARNING: %s failed to connect: %s".format(this, socket.inetAddress.hostAddress))
                return@Thread
            }

            while (receive()) {
                Thread.yield()
            }
            disconnect()
        }.start()
    }

    fun connect(): Boolean {
        if (state == ClientState.CONNECTING || state == ClientState.CONNECTED) {
            return false
        }
        state = ClientState.CONNECTING

        try {
            input = socket.getInputStream()
            output = socket.getOutputStream()
        } catch (e: Exception) {
            println("ERROR: %s failed to connect: %s".format(this, socket.inetAddress.hostAddress))
            disconnect()
            return false
        }

        println("%s connected: %s".format(this, socket.inetAddress.hostAddress))

        state = ClientState.CONNECTED
        return true
    }

    fun disconnect() {
        if (state == ClientState.DISCONNECTING || state == ClientState.DISCONNECTED) {
            return
        }
        val wasConnected: Boolean = state == ClientState.CONNECTED
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

        try {
            socket.close()
        } catch (e: IOException) {
            // ignore
        }

        if (wasConnected) {
            println("%s disconnected: %s".format(this, socket.inetAddress.hostAddress))
        }

        state = ClientState.DISCONNECTED
    }

    fun receive(): Boolean {
        try {
            val nextByte: Int = input!!.read()
            if (nextByte == -1) {
                return false
            }

            println("Received message from %s: %s".format(this, nextByte.toString()))

            return true
        } catch (e: IOException) {
            // ignore
        }
        return false
    }

    fun send(bytes: ByteArray) {
        try {
            output!!.write(bytes)
        } catch (e: IOException) {
            if (state != ClientState.CONNECTED) {
                return
            }

            println("ERROR: Failed to send message to %s: %s".format(this, bytes.contentToString()))
            disconnect()
        }
    }
}