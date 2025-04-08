package pointfeev.client

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        Client.host = args[0]
    }
    if (args.size >= 2) {
        try {
            Client.port = Integer.parseInt(args[1])
        } catch (e: NumberFormatException) {
            println("ERROR: Invalid port number \"%s\"".format(args[1]))
            exitProcess(-1)
        }
    }
    Runtime.getRuntime().addShutdownHook(Thread { Client.disconnect() })
    Client.start()
}