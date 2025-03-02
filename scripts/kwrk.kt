///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.github.ajalt.clikt:clikt-jvm:5.0.1
//DEPS io.github.wfouche.tulip:tulip-runtime:2.1.5
//DEPS org.springframework.boot:spring-boot-starter-web:3.4.3
//DEPS org.slf4j:slf4j-api:2.0.16
//DEPS ch.qos.logback:logback-core:1.5.16
//DEPS ch.qos.logback:logback-classic:1.5.16
//JAVA 21
//KOTLIN 2.0.21

import io.github.wfouche.tulip.api.TulipApi
import io.github.wfouche.tulip.api.TulipUser
import java.util.concurrent.ThreadLocalRandom
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.double

import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter

import java.util.Locale

val benchmarkConfig:String = """
{
    "actions": {
        "description": "kwrk",
        "output_filename": "kwrk_output.json",
        "report_filename": "kwrk_report.html",
        "user_class": "HttpUser",
        "user_params": {
            "url": "__P_URL__",
            "httpHeader": "__P_HEADER__",
            "connectTimeoutMillis": 500,
            "readTimeoutMillis": 2000
        },
        "user_actions": {
            "1": "GET:url"
        }
    },
    "benchmarks": {
        "onStart": {
            "save_stats": false,
            "scenario_actions": [ {"id": 0} ]
        },
         "HTTP": {
            "enabled": true,
            "aps_rate": __P_RATE__,
            "worker_thread_queue_size": __P_QSIZE__,
            "scenario_actions": [
                {
                    "id": 1
                }
            ],
            "time": {
                "pre_warmup_duration": __P_WARMUP__,
                "warmup_duration": __P_WARMUP__,
                "benchmark_duration": __P_DURATION__,
                "benchmark_iterations": __P_ITERATIONS__
            }
        },
        "onStop": {
            "enabled": false,
            "save_stats": false,
            "scenario_actions": [ {"id": 100} ]
        }
    },
    "contexts": {
        "Context-1": {
            "enabled": true,
            "num_users": __P_THREADS__,
            "num_threads": __P_THREADS__
        }
    }
}
""".trim()

class HttpUser(userId: Int, threadId: Int) : TulipUser(userId, threadId) {

    // Action 0
    override fun onStart(): Boolean {
        // Initialize the shared RestClient object only once
        if (userId == 0) {
            val connectTimeout = getUserParamValue("connectTimeoutMillis").toInt()
            val readTimeout = getUserParamValue("readTimeoutMillis").toInt()
            val factory = SimpleClientHttpRequestFactory().apply {
                setConnectTimeout(connectTimeout)
                setReadTimeout(readTimeout)
            }
            restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl(getUserParamValue("url"))
                .build()
            val h: String = getUserParamValue("httpHeader")
            val L = h.split(":")
            http_header_key = L[0].trim()
            http_header_val = L[1].trim()
        }
        return true
    }

    // Action 1: GET ${url}
    override fun action1(): Boolean {
        return try {
            val rsp: String? = restClient.get()
                .uri("")
                .header(http_header_key, http_header_val)
                .retrieve()
                .body(String::class.java)
            //Postcondition
            (rsp != null && rsp.length > 0)
        } catch (e: RestClientException) {
            false
        }
    }

    // Action 100
    override fun onStop(): Boolean {
        return true
    }

    // RestClient object
    companion object {
        private lateinit var restClient: RestClient
        private val logger = LoggerFactory.getLogger(HttpUser::class.java)
        private lateinit var http_header_key: String
        private lateinit var http_header_val: String
    }
}

class KwrkCli : CliktCommand() {
    private val p_debug by option("--debug").default("false")
    private val p_rate by option("--rate").double().default(5.0)
    private val p_qsize by option("--qsize").int().default(0)
    private val p_threads by option("--threads").int().default(2)
    private val p_warmup by option("--warmup").int().default(15)
    private val p_duration by option("--duration").int().default(30)
    private val p_iterations by option("--iterations").int().default(3)
    private val p_header by option("--header").default("User-Agent: kwrk")
    private val p_url by option("--url").default("--")

    override fun run() {
        var json = benchmarkConfig

        json = json.replace("__P_RATE__", p_rate.toString())
        json = json.replace("__P_QSIZE__", p_qsize.toString())
        json = json.replace("__P_THREADS__", p_threads.toString())
        json = json.replace("__P_DURATION__", p_duration.toString())
        json = json.replace("__P_ITERATIONS__", p_iterations.toString())
        json = json.replace("__P_URL__", p_url)
        json = json.replace("__P_HEADER__", p_header)

        var warmup = p_warmup
        if (p_rate <= 1.0) {
            if (p_rate != 0.0) {
                warmup = 0
            }
        }
        json = json.replace("__P_WARMUP__", warmup.toString())

        if (p_url == "--") {
            println("url: not defined, please specify a value using the --url option")
            System.exit(1)
        } else {
            println("kwrk options:")
            println("  --rate ${p_rate}")
            println("  --threads ${p_threads}")
            println("  --duration ${p_duration}")
            println("  --iterations ${p_iterations}")
            println("  --header ${p_header}")
            println("  --url ${p_url}")
        }

        if (p_debug == "true") {
            println("")
            println(json)
            println("")
        }

        TulipApi.runTulip(json)

        val old_lines: List<String> = File("kwrk_report.html").readLines()
        val new_lines: MutableList<String> = mutableListOf()
        var i = 0
        for (line in old_lines) {
            if (i == old_lines.size-2) {
                break
            }
            if (line.startsWith("</style>")) {
                new_lines.add("th:nth-child(n+14) {background-color: #D3D3D3;}")
                new_lines.add("td:nth-child(n+14) {background-color: #D3D3D3;}")
            }
            new_lines.add(line)
            i += 1
        }
        //println(old_lines.size)
        //println(new_lines.size)
        new_lines.add("<h3>Benchmark Options</h3>")
        new_lines.add("<table style=\"width:40%\">")

        new_lines.add("<tr>")
        new_lines.add("  <th>name</th>")
        new_lines.add("  <th>value</th>")
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>url</th>")
        new_lines.add("  <td>__P_URL__</th>".replace("__P_URL__", p_url.toString()))
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>header</th>")
        new_lines.add("  <td>__P_HEADER__</th>".replace("__P_HEADER__", p_header.toString()))
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>rate</th>")
        new_lines.add("  <td>__P_RATE__</th>".replace("__P_RATE__", p_rate.toString()))
        new_lines.add("</tr>")

        if (p_qsize != 0) {
            new_lines.add("<tr>")
            new_lines.add("  <td>qsize</th>")
            new_lines.add("  <td>__P_QSIZE__</th>".replace("__P_QSIZE__", p_qsize.toString()))
            new_lines.add("</tr>")
        }

        new_lines.add("<tr>")
        new_lines.add("  <td>threads</th>")
        new_lines.add("  <td>__P_THREADS__</th>".replace("__P_THREADS__", p_threads.toString()))
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>duration</th>")
        new_lines.add("  <td>__P_DURATION__ seconds</th>".replace("__P_DURATION__", p_duration.toString()))
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>iterations</th>")
        new_lines.add("  <td>__P_ITERATIONS__</th>".replace("__P_ITERATIONS__", p_iterations.toString()))
        new_lines.add("</tr>")
        new_lines.add("</table>")

        new_lines.add("<h3>Java Options</h3>")
        new_lines.add("<table style=\"width:40%\">")

        new_lines.add("<tr>")
        new_lines.add("  <th>name</th>")
        new_lines.add("  <th>value</th>")
        new_lines.add("</tr>")

        val rt = Runtime.getRuntime()
        val fm = rt.freeMemory()
        val tm = rt.totalMemory()
        val mm = rt.maxMemory()

        val gb1 = 1073741824.0
        val memory_used_jvm: String = "%.3f GB".format(Locale.US, (tm - fm)/gb1)
        val free_memory_jvm: String = "%.3f GB".format(Locale.US, fm/gb1)
        val total_memory_jvm: String = "%.3f GB".format(Locale.US, tm/gb1)
        val maximum_memory_jvm: String = "%.3f GB".format(Locale.US, mm/gb1)

        new_lines.add("<tr>")
        new_lines.add("  <td>jvm_memory_used</th>")
        new_lines.add("  <td>${memory_used_jvm}</th>")
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>jvm_free_memory</th>")
        new_lines.add("  <td>${free_memory_jvm}</th>")
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>jvm_total_memory</th>")
        new_lines.add("  <td>${total_memory_jvm}</th>")
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>jvm_maximum_memory</th>")
        new_lines.add("  <td>${maximum_memory_jvm}</th>")
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>java.vendor</th>")
        new_lines.add("  <td>${System.getProperty("java.vendor")}</th>")
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>java.runtime.version</th>")
        new_lines.add("  <td>${System.getProperty("java.runtime.version")}</th>")
        new_lines.add("</tr>")

        val env: String? = System.getenv("JBANG_JAVA_OPTIONS")
        val java_options: String
        if (env != null) {
            java_options = env
        } else {
            java_options = ""
        }
        new_lines.add("<tr>")
        new_lines.add("  <td>JBANG_JAVA_OPTIONS</th>")
        new_lines.add("  <td>${java_options}</th>")
        new_lines.add("</tr>")

        new_lines.add("</table>")
        new_lines.add("</body>")
        new_lines.add("</html>")

        val br: BufferedWriter = BufferedWriter(FileWriter("kwrk_report.html"))
        for (str in new_lines) {
            br.write(str + java.lang.System.lineSeparator())
        }
        br.close()
    }
}

fun main(args: Array<String>) = KwrkCli().main(args)