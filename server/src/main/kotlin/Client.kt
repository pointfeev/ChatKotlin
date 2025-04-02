package pointfeev.server

import pointfeev.shared.ClientState
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.system.exitProcess

class Client(val socket: Socket) {
    var state: ClientState = ClientState.INITIALIZING

    var input: InputStream? = null
    var output: OutputStream? = null

    companion object {
        var lastClientId: Int = 0
    }

    var clientId: Int = ++lastClientId

    override fun toString(): String {
        return "Client #%s".format(clientId)
    }

    val thread: Thread

    init {
        thread = Thread {
            if (!connect()) {
                println("WARNING: %s failed to connect: %s".format(this, socket.inetAddress.hostAddress))
                return@Thread
            }

            while (receive()) {
                Thread.yield()
            }
            disconnect()
        }
        thread.start()
    }

    fun connect(): Boolean {
        state = ClientState.CONNECTING

        try {
            input = socket.getInputStream()
            output = socket.getOutputStream()
        } catch (e: Exception) {
            disconnect()
            return false
        }

        // TODO: create synchronized Server.connect function and do this shit there (if that's even a thing in Kotlin)
        println("%s connected: %s".format(this, socket.inetAddress.hostAddress))

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

        try {
            socket.close()
        } catch (e: IOException) {
            // ignore
        }

        // TODO: create synchronized Server.disconnect function and do this shit there (if that's even a thing in Kotlin)
        println("%s disconnected: %s".format(this, socket.inetAddress.hostAddress))

        state = ClientState.DISCONNECTED
    }

    fun receive(): Boolean {
        try {
            val nextByte: Int = input!!.read()
            if (nextByte == -1) {
                return false
            }

            // TODO: create synchronized Server.receive function and do this shit there (if that's even a thing in Kotlin)
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
            if (state !== ClientState.DISCONNECTING) {
                println("ERROR: Failed to send message to %s: %s".format(this, bytes.contentToString()))
                exitProcess(-1)
            }
        }
    }
}