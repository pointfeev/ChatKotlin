package pointfeev.client

import pointfeev.shared.AbstractClient
import java.io.IOException
import java.net.Socket
import kotlin.system.exitProcess

object Client : AbstractClient() {
    var host: String = "127.0.0.1"
    var port: Int = 9876

    fun start() {
        println("Connecting to server at %s:%d...".format(host, port))
        try {
            socket = Socket(host, port)
        } catch (e: IOException) {
            onConnectFailure()
            disconnect()
            return
        }
        if (!connect()) {
            return
        }
        Thread {
            listen()
        }.start()
    }

    override fun onConnectFailure() {
        println("ERROR: Failed to connect to server")
        exitProcess(-1)
    }

    override fun onConnect(): Boolean {
        println("Connected to server")
        return true
    }

    override fun onDisconnect() {
        println("Disconnected from server")
    }

    override fun onReceive(string: String): Boolean {
        println("Received message from server: %s".format(string))
        return true
    }

    override fun onSendFailure(string: String) {
        println("ERROR: Failed to send message to server: %s".format(string))
        exitProcess(-1)
    }
}