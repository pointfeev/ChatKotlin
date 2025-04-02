package pointfeev.server

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val server = Server()
    if (args.isNotEmpty()) {
        try {
            server.port = Integer.parseInt(args[0])
        } catch (e: NumberFormatException) {
            println("ERROR: Invalid port number \"%s\"".format(args[0]))
            exitProcess(-1)
        }
    }
    Runtime.getRuntime().addShutdownHook(Thread { server.shutdown() })
    server.start()
}