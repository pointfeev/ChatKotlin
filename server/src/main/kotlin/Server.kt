package pointfeev.server

import java.io.IOException
import java.net.ServerSocket

object Server {
    var port: Int = 9876

    private var serverSocket: ServerSocket? = null

    val syncLock: Any = Any()
    val clients: MutableSet<Client> = mutableSetOf()

    fun start() {
        serverSocket = ServerSocket(port)
        println("Server started on port %d".format(port))
        while (serverSocket != null) {
            try {
                Client(serverSocket!!.accept())
            } catch (e: IOException) {
                // ignore
            }
        }
        shutdown()
    }

    fun shutdown() {
        if (serverSocket == null) {
            return
        }
        try {
            serverSocket!!.close()
        } catch (e: IOException) {
            // ignore
        }
        serverSocket = null
        println("Server stopped")
    }
}