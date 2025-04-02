package pointfeev.server

import java.io.IOException
import java.net.ServerSocket

class Server {
    var port: Int = 9876

    var serverSocket: ServerSocket? = null

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