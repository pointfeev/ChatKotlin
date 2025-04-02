package pointfeev.client

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val client = Client()
    if (args.isNotEmpty()) {
        client.host = args[0]
    }
    if (args.size >= 2) {
        try {
            client.port = Integer.parseInt(args[0])
        } catch (e: NumberFormatException) {
            println("ERROR: Invalid port number \"%s\"".format(args[0]))
            exitProcess(-1)
        }
    }
    Runtime.getRuntime().addShutdownHook(Thread { client.disconnect() })
    client.start()
}