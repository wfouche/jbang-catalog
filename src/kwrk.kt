// spotless:off
//DEPS com.github.ajalt.clikt:clikt-jvm:5.1.0
//DEPS io.github.wfouche.tulip:tulip-runtime:2.2.0
//JAVA 25
//KOTLIN 2.3.0
//RUNTIME_OPTIONS -XX:+IgnoreUnrecognizedVMOptions
//RUNTIME_OPTIONS --enable-native-access=ALL-UNNAMED
//RUNTIME_OPTIONS --sun-misc-unsafe-memory-access=allow
// spotless:on

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.int
import io.github.wfouche.tulip.api.TulipApi
import io.github.wfouche.tulip.user.HttpUser
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.management.ManagementFactory
import java.util.Locale
import org.slf4j.LoggerFactory

const val appName: String = "kwrk"
const val appVersion: String = "__JBANG_SNAPSHOT_ID__/__JBANG_SNAPSHOT_TIMESTAMP__"

private fun displayAppInfo() {
    var version: String = appVersion
    if (appVersion.contains("JBANG_SNAPSHOT_ID")) {
        version = "0/2025-08-06T14:52:50"
    }
    println(appName + "/" + version + "/" + TulipApi.VERSION)
}

val benchmarkConfig: String =
    """
    {
        "actions": {
            "description": "kwrk",
            "output_filename": "kwrk___P_RPT_SUFFIX___output.json",
            "report_filename": "kwrk___P_RPT_SUFFIX___report.html",
            "user_class": "KwrkHttpUser",
            "user_params": {
                "url": "__P_URL__",
                "httpVersion": "__P_HTTP_VERSION__",
                "httpHeader": "__P_HEADER__",
                "connectTimeoutMillis": 1000,
                "readTimeoutMillis": 10000,
                "jsonBody": "__JSON_BODY__"
            },
            "user_actions": {
                "1": "GET:url",
                "2": "POST:url"
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
                "aps_rate_step_change": __P_RATE_STEP_CHANGE__,
                "aps_rate_step_count" : __P_RATE_STEP_COUNT__,
                "worker_thread_queue_size": __P_QSIZE__,
                "scenario_actions": [
                    {
                        "id": __ACTION_ID__
                    }
                ],
                "time": {
                    "warmup_duration1": __P_WARMUP__,
                    "warmup_duration2": 30,
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
                "num_users": __P_CONNS__,
                "num_threads": __P_THREADS__
            }
        }
    }
    """
        .trimIndent()

val indexHtml: String =
    """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Benchmark Reports Dashboard</title>
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body, html {
                height: 100%;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                overflow: hidden;
            }

            .container {
                display: flex;
                height: 100vh;
            }

            /* Left Side Panel */
            .sidebar {
                width: 260px;
                background-color: #1a1a2e;
                color: white;
                padding: 20px 0;
                display: flex;
                flex-direction: column;
                border-right: 1px solid #333;
            }

            .sidebar h2 {
                font-size: 1.1rem;
                margin-bottom: 25px;
                padding: 0 20px;
                color: #4ecca3;
                text-transform: uppercase;
                letter-spacing: 1px;
            }

            .sidebar ul {
                list-style: none;
            }

            .sidebar li {
                margin: 4px 0;
            }

            .sidebar a {
                color: #94a3b8;
                text-decoration: none;
                display: block;
                padding: 12px 20px;
                transition: all 0.2s ease;
            }

            .sidebar a:hover {
                background-color: #16213e;
                color: #fff;
            }

            /* Style for the currently active link */
            .sidebar a.active {
                background-color: #4ecca3;
                color: #1a1a2e;
                font-weight: bold;
            }

            /* Right Side Display Panel */
            .content-panel {
                flex-grow: 1;
                background-color: #ffffff;
            }

            iframe {
                width: 100%;
                height: 100%;
                border: none;
            }
        </style>
    </head>
    <body>

        <div class="container">
            <nav class="sidebar" id="menu">
                <h2>Benchmark Reports</h2>
                <ul>
                    <li><a href="__REPORT_FILENAME__" target="reportFrame" class="active">kwrk - __RPT_ALIAS__</a></li>
                </ul>
            </nav>

            <main class="content-panel">
                <iframe name="reportFrame" src="__REPORT_FILENAME__"></iframe>
            </main>
        </div>

        <script>
            // Optional JavaScript to highlight the clicked link
            const links = document.querySelectorAll('#menu a');
            
            links.forEach(link => {
                link.addEventListener('click', function() {
                    // Remove 'active' class from all links
                    links.forEach(l => l.classList.remove('active'));
                    // Add 'active' class to the clicked link
                    this.classList.add('active');
                });
            });
        </script>

    </body>
    </html>
    """
        .trimIndent()

fun writeToFile(path: String, content: String, append: Boolean) {
    try {
        FileWriter(path, append).use { fileWriter -> fileWriter.write(content) }
    } catch (e: IOException) {
        // exception handling ...
    }
}

class KwrkHttpUser() : HttpUser() {

    override fun onStart(): Boolean {
        if (userId == 0) {
            super.onStart()
            jsonBody = getUserParamValue("jsonBody").replace("\\", "")
        }
        return true
    }

    // Curl commands: https://gist.github.com/hnnazm/ac6f986d45556e52334fb7fd2689d9be

    // Action 1: GET ${url}
    override fun action1(): Boolean {
        return httpGet(getUrlPath()).isSuccessful()
    }

    // Action 2: POST ${url}
    override fun action2(): Boolean {
        return httpPost(jsonBody, getUrlPath()).isSuccessful()
    }

    // Action 100
    override fun onStop(): Boolean {
        return true
    }

    // RestClient object
    companion object {
        private val logger = LoggerFactory.getLogger(KwrkHttpUser::class.java)
        private lateinit var http_header_key: String
        private lateinit var http_header_val: String
        private lateinit var jsonBody: String
    }
}

class KwrkCli : CliktCommand() {
    private val p_debug by option("--debug").default("false")
    private val p_version by option("--version").default("HTTP_1_1")
    private val p_rate by option("--rate").double().default(5.0)
    private val p_rate_step_change by option("--rateStepChange").double().default(0.0)
    private val p_rate_step_count by option("--rateStepCount").int().default(1)
    private val p_qsize by option("--qsize").int().default(0)
    private val p_conns by option("--conns").int().default(1)
    private val p_threads by option("--threads").int().default(0)
    private val p_warmup by option("--warmup").int().default(5)
    private val p_duration by option("--duration").int().default(30)
    private val p_iterations by option("--iterations").int().default(3)
    private val p_header by option("--header").default("User-Agent: kwrk")
    private val p_method by option("--method").default("GET")
    private val p_url by option("--url").default("http://jsonplaceholder.typicode.com/posts/1")
    private val p_rpt_suffix by option("--name").default("test")
    private val p_json_body by
        option("--jsonBody").default("{\"title\": \"foo\", \"body\": \"bar\", \"userId\": 1}")

    override fun run() {
        displayAppInfo()
        println("")

        var json = benchmarkConfig

        json = json.replace("__P_HTTP_VERSION__", p_version)
        json = json.replace("__P_RATE__", p_rate.toString())
        json = json.replace("__P_RATE_STEP_CHANGE__", p_rate_step_change.toString())
        json = json.replace("__P_RATE_STEP_COUNT__", p_rate_step_count.toString())
        json = json.replace("__P_QSIZE__", p_qsize.toString())
        json = json.replace("__P_THREADS__", p_threads.toString())
        json = json.replace("__P_CONNS__", p_conns.toString())
        json = json.replace("__P_DURATION__", p_duration.toString())
        json = json.replace("__P_ITERATIONS__", p_iterations.toString())
        json = json.replace("__P_URL__", p_url)
        json = json.replace("__P_HEADER__", p_header)
        json = json.replace("__P_RPT_SUFFIX__", p_rpt_suffix)
        json = json.replace("__ACTION_ID__", "${if (p_method.uppercase() == "GET") 1 else 2}")
        json = json.replace("__JSON_BODY__", p_json_body.replace("\"", "\\\""))

        var warmup = p_warmup
        if (p_rate < 1.0) {
            if (p_rate != 0.0) {
                warmup = 0
            }
        }
        json = json.replace("__P_WARMUP__", warmup.toString())

        if (p_url == "--") {
            println("url: not defined, please specify a value using the --url option")
            System.exit(1)
        } else {
            println("  kwrk options:")
            println("    --rate ${p_rate}")
            println("    --rateStepChange ${p_rate_step_change}")
            println("    --rateStepCount ${p_rate_step_count}")
            println("    --conns ${p_conns}")
            if (p_threads != 0) {
                println("    --threads ${p_threads}")
            }
            println("    --warmup ${p_warmup}")
            println("    --duration ${p_duration}")
            println("    --iterations ${p_iterations}")
            println("    --http ${p_version}")
            println("    --header ${p_header}")
            println("    --method ${p_method}")
            println("    --url ${p_url}")
            if (p_method.uppercase() == "POST") {
                println("    --jsonBody ${p_json_body}")
            }
            println("    --name ${p_rpt_suffix}")
        }
        //        println("")
        //        println("  java options:")
        val runtimeMxBean = ManagementFactory.getRuntimeMXBean()
        val jvmArgs = runtimeMxBean.getInputArguments()
        //        for (arg in jvmArgs) {
        //            println("    $arg")
        //        }
        if (p_debug == "true") {
            println("")
            println(json)
        }
        println("")

        // TulipApi.runTulip(json)
        val configFilename = "kwrk_${p_rpt_suffix}_config.json"
        writeToFile(configFilename, json, false)
        val outputFilename = TulipApi.runTulip(configFilename)
        TulipApi.generateReport(outputFilename)

        val indexFilename = "kwrk_${p_rpt_suffix}_index.html"
        writeToFile(
            indexFilename,
            indexHtml
                .replace("__REPORT_FILENAME__", "kwrk_${p_rpt_suffix}_report.html")
                .replace("__RPT_ALIAS__", "${p_rpt_suffix}"),
            false,
        )

        val old_lines: List<String> = File("kwrk_${p_rpt_suffix}_report.html").readLines()
        val new_lines: MutableList<String> = mutableListOf()
        var i = 0
        for (line in old_lines) {
            if (i == old_lines.size - 2) {
                break
            }
            if (line.startsWith("</style>")) {
                // new_lines.add("th:nth-child(n+14) {background-color: #D3D3D3;}")
                // new_lines.add("td:nth-child(n+14) {background-color: #D3D3D3;}")
            }
            new_lines.add(line)
            i += 1
        }
        // println(old_lines.size)
        // println(new_lines.size)
        new_lines.add("<h3>Benchmark Options</h3>")
        new_lines.add("<table style=\"width:40%\">")

        new_lines.add("<tr>")
        new_lines.add("  <th>name</th>")
        new_lines.add("  <th>value</th>")
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>method</th>")
        new_lines.add("  <td>__P_METHOD__</th>".replace("__P_METHOD__", p_method))
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>url</th>")
        new_lines.add("  <td>__P_URL__</th>".replace("__P_URL__", p_url))
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>version</th>")
        new_lines.add("  <td>__P_HTTP__</th>".replace("__P_HTTP__", p_version))
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>header</th>")
        new_lines.add("  <td>__P_HEADER__</th>".replace("__P_HEADER__", p_header))
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
        new_lines.add("  <td>warmup</th>")
        new_lines.add("  <td>__P_WARMUP__ seconds</th>".replace("__P_WARMUP__", warmup.toString()))
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>duration</th>")
        new_lines.add(
            "  <td>__P_DURATION__ seconds</th>".replace("__P_DURATION__", p_duration.toString())
        )
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>iterations</th>")
        new_lines.add(
            "  <td>__P_ITERATIONS__</th>".replace("__P_ITERATIONS__", p_iterations.toString())
        )
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
        val memory_used_jvm: String = "%.3f GB".format(Locale.US, (tm - fm) / gb1)
        val free_memory_jvm: String = "%.3f GB".format(Locale.US, fm / gb1)
        val total_memory_jvm: String = "%.3f GB".format(Locale.US, tm / gb1)
        val maximum_memory_jvm: String = "%.3f GB".format(Locale.US, mm / gb1)

        new_lines.add("<tr>")
        new_lines.add("  <td>jvm_memory_used</td>")
        new_lines.add("  <td>${memory_used_jvm}</td>")
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>jvm_free_memory</td>")
        new_lines.add("  <td>${free_memory_jvm}</td>")
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>jvm_total_memory</td>")
        new_lines.add("  <td>${total_memory_jvm}</td>")
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>jvm_maximum_memory</td>")
        new_lines.add("  <td>${maximum_memory_jvm}</td>")
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>java.vendor</th>")
        new_lines.add("  <td>${System.getProperty("java.vendor")}</td>")
        new_lines.add("</tr>")

        new_lines.add("<tr>")
        new_lines.add("  <td>java.runtime.version</td>")
        new_lines.add("  <td>${System.getProperty("java.runtime.version")}</td>")
        new_lines.add("</tr>")

        var java_options: String = ""
        for (arg in jvmArgs) {
            if (java_options.length == 0) {
                java_options += arg
            } else {
                java_options += " " + arg
            }
        }

        new_lines.add("<tr>")
        new_lines.add("  <td>java.runtime.options</td>")
        new_lines.add("  <td>${java_options}</td>")
        new_lines.add("</tr>")

        new_lines.add("")
        new_lines.add("</body>")
        new_lines.add("</html>")

        val br: BufferedWriter = BufferedWriter(FileWriter("kwrk_${p_rpt_suffix}_report.html"))
        for (str in new_lines) {
            br.write(str + java.lang.System.lineSeparator())
        }
        br.close()
    }
}

fun main(args: Array<String>) = KwrkCli().main(args)
