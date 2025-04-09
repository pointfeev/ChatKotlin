package pointfeev.client

import pointfeev.shared.AbstractClient
import java.io.IOException
import java.net.Socket
import java.util.*
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

        val scanner = Scanner(System.`in`)
        while (socket != null) {
            send(scanner.nextLine())
        }
        disconnect()
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

    override fun onReceive(bytes: ByteArray): Boolean {
        if (bytes.size == 1 && bytes[0] == 1.toByte()) {
            println("That alias is already in use")
            return true
        }

        println(bytes.toString(charset))
        return true
    }

    override fun onSendFailure(bytes: ByteArray) {
        println("ERROR: Failed to send message to server: %s".format(bytes.contentToString()))
        exitProcess(-1)
    }
}