package pointfeev.client

import pointfeev.shared.ClientState
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.system.exitProcess

class Client {
    var host: String = "127.0.0.1"
    var port: Int = 9876

    var state: ClientState = ClientState.INITIALIZING

    var socket: Socket? = null
    var input: InputStream? = null
    var output: OutputStream? = null

    fun start() {
        println("Connecting to server at %s:%d...".format(host, port))
        if (!connect()) {
            println("ERROR: Failed to connect to server")
            exitProcess(-1)
        }

        println("Connected to server")
        while (receive()) {
            Thread.yield()
        }
        disconnect()
    }

    fun connect(): Boolean {
        state = ClientState.CONNECTING

        try {
            socket = Socket(host, port)
            input = socket!!.getInputStream()
            output = socket!!.getOutputStream()
        } catch (e: Exception) {
            disconnect()
            return false
        }

        state = ClientState.CONNECTED
        return true
    }

    fun disconnect() {
        if (state == ClientState.DISCONNECTING || state == ClientState.DISCONNECTED) {
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

        println("Disconnected from server")

        socket = null

        state = ClientState.DISCONNECTED
    }

    fun receive(): Boolean {
        try {
            val nextByte: Int = input!!.read()
            if (nextByte == -1) {
                return false
            }

            println("Received message from server: %s".format(nextByte.toString()))

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
            if (state !== ClientState.DISCONNECTING) {
                println("ERROR: Failed to send message to server: %s".format(bytes.contentToString()))
                exitProcess(-1)
            }
        }
    }
}