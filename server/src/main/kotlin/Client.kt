package pointfeev.server

import pointfeev.shared.AbstractClient
import java.net.Socket

class Client(socket: Socket) : AbstractClient(socket) {
    private var name: String? = null

    override fun toString(): String {
        if (name != null) {
            return name!!
        }
        return socket!!.inetAddress.hostAddress
    }

    init {
        Thread {
            if (!connect()) {
                return@Thread
            }
            listen()
        }.start()
    }

    override fun onConnectFailure() {
        println("ERROR: %s failed to connect".format(this))
    }

    override fun onConnect(): Boolean {
        synchronized(Server.syncLock) {
            if (!Server.clients.add(this)) {
                return false
            }
        }

        println("%s connected".format(this))
        return true
    }

    override fun onDisconnect() {
        synchronized(Server.syncLock) {
            if (Server.clients.remove(this)) {
                println("%s disconnected".format(this))
            }
        }
    }

    override fun onReceive(string: String): Boolean {
        if (name == null) {
            println("%s set their name to %s".format(this, string))
            name = string
            return true
        }

        // TODO

        println("ERROR: Received unknown message from %s: %s".format(this, string))
        return false
    }

    override fun onSendFailure(string: String) {
        println("ERROR: Failed to send message to %s: %s".format(this, string))
    }
}