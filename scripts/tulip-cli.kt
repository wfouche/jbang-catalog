///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.github.ajalt.clikt:clikt-jvm:5.0.2
//JAVA 21
//KOTLIN 2.1.10

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option

class KwrkCli : CliktCommand() {
    private val p_rate by option("--rate").default("5.0")
    private val p_threads by option("--threads").default("2")
    private val p_duration by option("--duration").default("30")
    private val p_repeat by option("--repeat").default("3")
    private val p_url by option("--url").default("http://jsonplaceholder.typicode.com/posts/1")
    override fun run() {
        var jsonc = ""

        jsonc = jsonc.replace("__P_RATE__", p_rate)
        jsonc = jsonc.replace("__P_THREADS__", p_threads)
        jsonc = jsonc.replace("__P_DURATION__", p_duration)
        jsonc = jsonc.replace("__P_REPEAT__", p_repeat)
        jsonc = jsonc.replace("__P_URL__", p_url)

        println("tulip-cli arguments:")
        println("  --rate ${p_rate}")
        println("  --threads ${p_threads}")
        println("  --duration ${p_duration}")
        println("  --repeat ${p_repeat}")
        println("  --url ${p_url}")
    }
}

fun main(args: Array<String>) = KwrkCli().main(args)
