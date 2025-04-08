package pointfeev.server

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        try {
            Server.port = Integer.parseInt(args[0])
        } catch (e: NumberFormatException) {
            println("ERROR: Invalid port number \"%s\"".format(args[0]))
            exitProcess(-1)
        }
    }
    Runtime.getRuntime().addShutdownHook(Thread { Server.shutdown() })
    Server.start()
}