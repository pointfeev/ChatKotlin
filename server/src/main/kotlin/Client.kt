package pointfeev.server

import pointfeev.shared.AbstractClient
import java.net.Socket

class Client(socket: Socket) : AbstractClient(socket) {
    private var alias: String? = null

    override fun toString(): String {
        if (alias != null) {
            return alias!!
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

    override fun onReceive(bytes: ByteArray): Boolean {
        val string: String = bytes.toString(charset)

        if (alias == null) {
            synchronized(Server.syncLock) {
                for (client: Client in Server.clients) {
                    if (client.alias == string) {
                        println("%s attempted to use an already used alias: %s".format(this, string))
                        send(1)
                        return true
                    }
                }
            }

            println("%s set their alias to %s".format(this, string))
            alias = string
            return true
        }

        synchronized(Server.syncLock) {
            for (client: Client in Server.clients) {
                if (client != this) {
                    client.send("[%s] %s".format(alias, string))
                }
            }
        }
        println("%s sent a message: %s".format(this, string))
        return true
    }

    override fun onSendFailure(bytes: ByteArray) {
        println("ERROR: Failed to send message to %s: %s".format(this, bytes.contentToString()))
    }
}