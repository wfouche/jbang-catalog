///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.github.wfouche.tulip:tulip-runtime:2.1.7
//JAVA 21
//KOTLIN 2.0.21

import io.github.wfouche.tulip.api.TulipApi.NUM_ACTIONS
import io.github.wfouche.tulip.api.TulipApi.VERSION
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

const val appName: String = "tulip-cli"
const val appVersion: String = "__JBANG_SNAPSHOT_ID__/__JBANG_SNAPSHOT_TIMESTAMP__"

private fun displayAppInfo() {
    var version: String = appVersion
    if (appVersion.contains("JBANG_SNAPSHOT_ID")) {
        version = "0/2025-04-03T21:58:57"
    }
    println(appName + "/" + version + "/" + VERSION)
}

fun writeToFile(path: String, content: String, append: Boolean) {
    try {
        FileWriter(path, append).use { fileWriter ->
            fileWriter.write(content)
        }
    } catch (e: IOException) {
        // exception handling ...
    }
}

var benchmarkConfig: String = """
{
    "actions": {
        "description": "Spring RestClient Benchmark [__TULIP_LANG__]",
        "output_filename": "benchmark_output.json",
        "report_filename": "benchmark_report.html",
        "user_class": "io.tulip.__TULIP_LANG__HttpUser",
        "user_params": {
            "url": "__URL__",
            "connectTimeoutMillis": 500,
            "readTimeoutMillis": 2000,
            "debug": true
        },
        "user_actions": {
            "1": "GET:posts",
            "2": "GET:comments",
            "3": "GET:todos"
        }
    },
    "workflows": {
        "api-user": {
            "-": {
                "1": 0.40,
                "3": 0.60
            },
            "1": {
                "2": 1.0
            },
            "2": {
                "-": 1.0
            },
            "3": {
                "-": 1.0
            }
        }
    },
    "benchmarks": {
        "onStart": {
            "save_stats": false,
            "scenario_actions": [ {"id": 0} ]
        },
         "REST1": {
            "enabled": true,
            "aps_rate": __AVG_APS__,
            "scenario_actions": [
                {
                    "id": 1
                }
            ],
            "time": {
                "pre_warmup_duration": 30,
                "warmup_duration": 10,
                "benchmark_duration": 30,
                "benchmark_iterations": 3
            }
        },
        "REST2": {
            "enabled": true,
            "aps_rate": __AVG_APS__,
            "scenario_actions": [
                {
                    "id": 1, "weight": 10
                },
                {
                    "id": 2, "weight": 40
                },
                {
                    "id": 3, "weight": 50
                }
            ],
            "time": {
                "pre_warmup_duration": 30,
                "warmup_duration": 10,
                "benchmark_duration": 30,
                "benchmark_iterations": 3
            }
        },
        "REST3": {
            "enabled": true,
            "aps_rate": __AVG_APS__,
            "scenario_workflow": "api-user",
            "time": {
                "pre_warmup_duration": 30,
                "warmup_duration": 10,
                "benchmark_duration": 30,
                "benchmark_iterations": 3
            }
        },
        "REST3.max": {
            "enabled": true,
            "aps_rate": 0.0,
            "scenario_workflow": "api-user",
            "time": {
                "pre_warmup_duration": 30,
                "warmup_duration": 10,
                "benchmark_duration": 30,
                "benchmark_iterations": 3
            }
        },
        "onStop": {
            "save_stats": false,
            "scenario_actions": [ {"id": __ONSTOP_ID__} ]
        }
    },
    "contexts": {
        "Context-1": {
            "enabled": true,
            "num_users": 128,
            "num_threads": 8
        }
    }
}
""".trimIndent()

val javaApp: String = """
    ///usr/bin/env jbang "${'$'}0" "${'$'}@" ; exit ${'$'}?
    //DEPS io.github.wfouche.tulip:tulip-runtime:__TULIP_VERSION__
    //DEPS org.springframework.boot:spring-boot-starter-web:3.4.4
    //DEPS org.slf4j:slf4j-api:2.0.17
    //DEPS ch.qos.logback:logback-core:1.5.18
    //DEPS ch.qos.logback:logback-classic:1.5.18
    //SOURCES JavaHttpUser.java
    //JAVA 21
    //PREVIEW
    //RUNTIME_OPTIONS __TULIP_JAVA_OPTIONS__
    //COMPILE_OPTIONS -g
    
    package io.tulip;
    
    import io.github.wfouche.tulip.api.TulipApi;
    
    public class App {
       public static void main(String[] args) {
          TulipApi.runTulip("benchmark_config.json");
       }
    }
""".trimIndent()

val kotlinApp: String = """
    ///usr/bin/env jbang "${'$'}0" "${'$'}@" ; exit ${'$'}?
    //DEPS io.github.wfouche.tulip:tulip-runtime:__TULIP_VERSION__
    //DEPS org.springframework.boot:spring-boot-starter-web:3.4.4
    //DEPS org.slf4j:slf4j-api:2.0.17
    //DEPS ch.qos.logback:logback-core:1.5.18
    //DEPS ch.qos.logback:logback-classic:1.5.18
    //SOURCES KotlinHttpUser.kt
    //JAVA 21
    //KOTLIN 2.0.21
    //RUNTIME_OPTIONS __TULIP_JAVA_OPTIONS__
    //COMPILE_OPTIONS -progressive
    
    package io.tulip
    
    import io.github.wfouche.tulip.api.TulipApi
    
    class App() {    
        companion object {
            @JvmStatic
            fun main(args: Array<String>) {
                TulipApi.runTulip("benchmark_config.json")
            }
        }
    }
""".trimIndent()

val groovyApp: String = """
    ///usr/bin/env jbang "${'$'}0" "${'$'}@" ; exit ${'$'}?
    //DEPS io.github.wfouche.tulip:tulip-runtime:__TULIP_VERSION__
    //DEPS org.springframework.boot:spring-boot-starter-web:3.4.4
    //DEPS org.slf4j:slf4j-api:2.0.17
    //DEPS ch.qos.logback:logback-core:1.5.18
    //DEPS ch.qos.logback:logback-classic:1.5.18
    //SOURCES GroovyHttpUser.groovy
    //JAVA 21
    //GROOVY 4.0.26
    //RUNTIME_OPTIONS __TULIP_JAVA_OPTIONS__
    //COMPILE_OPTIONS --tolerance=5

    package io.tulip
    
    import io.github.wfouche.tulip.api.TulipApi
    
    class App {
        static void main(String[] args) {
            TulipApi.runTulip("benchmark_config.json")
        }
    }
""".trimIndent()

val scalaApp: String = """
    //> using jvm 21
    //> using dep io.github.wfouche.tulip:tulip-runtime:__TULIP_VERSION__
    //> using dep org.springframework.boot:spring-boot-starter-web:3.4.4
    //> using dep org.slf4j:slf4j-api:2.0.17
    //> using dep ch.qos.logback:logback-core:1.5.18
    //> using dep ch.qos.logback:logback-classic:1.5.18
    //> using javaOpt __TULIP_JAVA_OPTIONS__
    //> using repositories m2local
    
    // https://yadukrishnan.live/developing-java-applications-with-scala-cli
    // https://www.baeldung.com/scala/scala-cli-intro
    
    package io.tulip
    
    import io.github.wfouche.tulip.api.TulipApi
    
    object App {
      def main(args: Array[String]): Unit = {
        TulipApi.runTulip("benchmark_config.json")
      }
    }
""".trimIndent()

val javaUser: String = """
    package io.tulip;

    import io.github.wfouche.tulip.user.HttpUser;
    import java.util.concurrent.ThreadLocalRandom;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    public class JavaHttpUser extends HttpUser {

        public JavaHttpUser(int userId, int threadId) {
            super(userId, threadId);
        }

        public boolean onStart() {
            // Initialize the shared RestClient object only once
            if (getUserId() == 0) {
                logger.info("Java");
                super.onStart();
            }
            return true;
        }

        // Action 1: GET /posts/{id}
        public boolean action1() {
            int id = ThreadLocalRandom.current().nextInt(100)+1;
            return !http_GET("/posts/{id}", id).isEmpty();
        }

        // Action 2: GET /comments/{id}
        public boolean action2() {
            int id = ThreadLocalRandom.current().nextInt(500)+1;
            return !http_GET("/comments/{id}", id).isEmpty();
        }

        // Action 3: GET /todos/{id}
        public boolean action3() {
            int id = ThreadLocalRandom.current().nextInt(200)+1;
            return !http_GET("/todos/{id}", id).isEmpty();
        }

        public boolean onStop() {
            return true;
        }
        
        // Logger
        private static final Logger logger = LoggerFactory.getLogger(JavaHttpUser.class);

    }    
""".trimIndent()

val kotlinUser: String = """
    package io.tulip
    
    import io.github.wfouche.tulip.user.HttpUser
    import java.util.concurrent.ThreadLocalRandom
    import org.slf4j.Logger
    import org.slf4j.LoggerFactory
    
    class KotlinHttpUser(userId: Int, threadId: Int) : HttpUser(userId, threadId) {
    
        // Action 0
        override fun onStart(): Boolean {
            // Initialize the shared RestClient object only once
            if (userId == 0) {
                logger.info("Kotlin")
                super.onStart()
            }
            return true
        }
    
        // Action 1: GET /posts/{id}
        override fun action1(): Boolean {
            val id: Int = ThreadLocalRandom.current().nextInt(100)+1
            return !http_GET("/posts/{id}", id).isEmpty()
        }
    
        // Action 2: GET /comments/{id}
        override fun action2(): Boolean {
            val id: Int = ThreadLocalRandom.current().nextInt(500)+1
            return !http_GET("/comments/{id}", id).isEmpty()
        }
    
        // Action 3: GET /todos/{id}
        override fun action3(): Boolean {
            val id: Int = ThreadLocalRandom.current().nextInt(200)+1
            return !http_GET("/todos/{id}", id).isEmpty()
        }
    
        override fun onStop(): Boolean {
            return true
        }
            
        // RestClient object
        companion object {
            private val logger = LoggerFactory.getLogger(KotlinHttpUser::class.java)
        }
    }
""".trimIndent()

val groovyUser = """
    package io.tulip
    
    import io.github.wfouche.tulip.user.HttpUser
    import java.util.concurrent.ThreadLocalRandom
    import org.slf4j.Logger
    import org.slf4j.LoggerFactory
    
    class GroovyHttpUser extends HttpUser {
    
        GroovyHttpUser(int userId, int threadId) {
            super(userId, threadId)
        }
    
        boolean onStart() {
            // Initialize the shared RestClient object only once
            if (userId == 0) {
                logger.info("Groovy")
                super.onStart()
            }
            return true
        }
    
        // Action 1: GET /posts/{id}
        boolean action1() {
            int id = ThreadLocalRandom.current().nextInt(100) + 1
            return !http_GET("/posts/{id}", id).isEmpty()            
        }
    
        // Action 2: GET /comments/{id}
        boolean action2() {
            int id = ThreadLocalRandom.current().nextInt(500) + 1
            return !http_GET("/comments/{id}", id).isEmpty()
        }
    
        // Action 3: GET /todos/{id}
        boolean action3() {
            int id = ThreadLocalRandom.current().nextInt(200) + 1
            return !http_GET("/todos/{id}", id).isEmpty()
        }
    
        boolean onStop() {
            return true
        }

        // Logger
        static Logger logger = LoggerFactory.getLogger(GroovyHttpUser.class)
    
    }    
""".trimIndent()

val scalaUser: String = """
    package io.tulip
    
    import io.github.wfouche.tulip.user.HttpUser
    import java.util.concurrent.ThreadLocalRandom
    import org.slf4j.Logger
    import org.slf4j.LoggerFactory
    
    class ScalaHttpUser(userId: Int, threadId: Int) extends HttpUser(userId, threadId) {
    
      override def onStart(): Boolean = {
        // Initialize the shared RestClient object only once
        if (getUserId == 0) {
          logger.info("Scala")
          super.onStart()
        }
        true
      }
    
      // Action 1: GET /posts/{id}
      override def action1(): Boolean = {
        val id = ThreadLocalRandom.current().nextInt(100) + 1
        !http_GET("/posts/{id}", id).isEmpty()
      }
    
      // Action 2: GET /comments/{id}
      override def action2(): Boolean = {
        val id = ThreadLocalRandom.current().nextInt(500) + 1
        !http_GET("/comments/{id}", id).isEmpty()
      }
    
      // Action 3: GET /todos/{id}
      override def action3(): Boolean = {
        val id = ThreadLocalRandom.current().nextInt(200) + 1
        !http_GET("/todos/{id}", id).isEmpty()
      }
    
      override def onStop(): Boolean = true
      
    }
    
    // Logger
    val logger: Logger = LoggerFactory.getLogger(classOf[ScalaHttpUser])    
""".trimIndent()

val runBenchShJava: String = """
    #!/bin/bash
    rm -f benchmark_report.html
    #export JBANG_JAVA_OPTIONS="__TULIP_JAVA_OPTIONS__"
    jbang run io/tulip/App.java
    echo ""
    #w3m -dump -cols 205 benchmark_report.html
    lynx -dump -width 205 benchmark_report.html
    #jbang run asciidoc@wfouche benchmark_config.adoc
    #jbang export fatjar io/tulip/App.java
    
""".trimIndent()

val runBenchCmdJava: String = """
    if exist benchmark_report.html del benchmark_report.html
    REM JBANG_JAVA_OPTIONS=__TULIP_JAVA_OPTIONS__
    call jbang run io\tulip\App.java
    @echo off
    echo.
    REM w3m.exe -dump -cols 205 benchmark_report.html
    REM lynx.exe -dump -width 205 benchmark_report.html
    start benchmark_report.html
    REM jbang run asciidoc@wfouche benchmark_config.adoc
    REM start benchmark_config.html
    REM jbang export fatjar io\tulip\App.java
    
""".trimIndent()

val runBenchShKotlin: String = """
    #!/bin/bash
    rm -f benchmark_report.html
    #export JBANG_JAVA_OPTIONS="__TULIP_JAVA_OPTIONS__"
    jbang run io/tulip/App.kt
    echo ""
    #w3m -dump -cols 205 benchmark_report.html
    lynx -dump -width 205 benchmark_report.html
    #jbang run asciidoc@wfouche benchmark_config.adoc
    #jbang export fatjar io/tulip/App.kt
    
""".trimIndent()

val runBenchCmdKotlin: String = """
    if exist benchmark_report.html del benchmark_report.html
    REM JBANG_JAVA_OPTIONS=__TULIP_JAVA_OPTIONS__
    call jbang run io\tulip\App.kt
    @echo off
    echo.
    REM call w3m.exe -dump -cols 205 benchmark_report.html
    REM lynx.exe -dump -width 205 benchmark_report.html
    start benchmark_report.html
    REM jbang run asciidoc@wfouche benchmark_config.adoc
    REM start benchmark_config.html
    REM jbang export fatjar io\tulip\App.kt
    
""".trimIndent()

val runBenchShGroovy: String = """
    #!/bin/bash
    rm -f benchmark_report.html
    #export JBANG_JAVA_OPTIONS="__TULIP_JAVA_OPTIONS__"
    jbang run io/tulip/App.groovy
    echo ""
    #w3m -dump -cols 205 benchmark_report.html
    lynx -dump -width 205 benchmark_report.html
    #jbang run asciidoc@wfouche benchmark_config.adoc
    #jbang export fatjar io/tulip/App.groovy
    
""".trimIndent()

val runBenchCmdGroovy: String = """
    if exist benchmark_report.html del benchmark_report.html
    REM JBANG_JAVA_OPTIONS=__TULIP_JAVA_OPTIONS__
    call jbang run io\tulip\App.groovy
    @echo off
    echo.
    REM call w3m.exe -dump -cols 205 benchmark_report.html
    REM lynx.exe -dump -width 205 benchmark_report.html
    start benchmark_report.html
    REM jbang run asciidoc@wfouche benchmark_config.adoc
    REM start benchmark_config.html
    REM jbang export fatjar io\tulip\App.groovy
    
""".trimIndent()

val runBenchShScala: String = """
    #!/bin/bash
    rm -f benchmark_report.html
    scala-cli io/tulip/App.scala io/tulip/ScalaHttpUser.scala
    echo ""
    #w3m -dump -cols 205 benchmark_report.html
    lynx -dump -width 205 benchmark_report.html
    #jbang run asciidoc@wfouche benchmark_config.adoc
    
""".trimIndent()

val runBenchCmdScala: String = """
    if exist benchmark_report.html del benchmark_report.html
    scala-cli io\tulip\App.scala io\tulip\HttpUser.scala
    @echo off
    echo.
    REM call w3m.exe -dump -cols 205 benchmark_report.html
    REM lynx.exe -dump -width 205 benchmark_report.html
    start benchmark_report.html
    REM jbang asciidoc@wfouche benchmark_config.adoc
    REM start benchmark_config.html
    
""".trimIndent()

val runBenchShJython: String = """
    #!/bin/bash
    rm -f benchmark_report.html
    #export JBANG_JAVA_OPTIONS="__TULIP_JAVA_OPTIONS__"
    jbang run Jython.java benchmark.py
    echo ""
    #w3m -dump -cols 205 benchmark_report.html
    lynx -dump -width 205 benchmark_report.html
    #jbang run asciidoc@wfouche benchmark_config.adoc
    
""".trimIndent()

val runBenchCmdJython: String = """
    if exist benchmark_report.html del benchmark_report.html
    REM JBANG_JAVA_OPTIONS=__TULIP_JAVA_OPTIONS__
    call jbang run Jython.java benchmark.py
    @echo off
    echo.
    REM call w3m.exe -dump -cols 205 benchmark_report.html
    REM lynx.exe -dump -width 205 benchmark_report.html
    start benchmark_report.html
    REM jbang run asciidoc@wfouche benchmark_config.adoc
    REM start benchmark_config.html
    
""".trimIndent()

val JythonJava: String = """
    ///usr/bin/env jbang "${'$'}0" "${'$'}@" ; exit ${'$'}?
    
    //DEPS org.python:jython-standalone:2.7.4
    //DEPS io.github.wfouche.tulip:tulip-runtime:__TULIP_VERSION__
    //DEPS org.springframework.boot:spring-boot-starter-web:3.4.4
    //DEPS org.slf4j:slf4j-api:2.0.17
    //DEPS ch.qos.logback:logback-core:1.5.18
    //DEPS ch.qos.logback:logback-classic:1.5.18
    //JAVA 21
    //RUNTIME_OPTIONS __TULIP_JAVA_OPTIONS__
        
    import org.python.util.jython;
    
    public class Jython {
        public static void main(String[] args) {
            jython.main(args);
        }
    }

""".trimIndent()

val JythonBenchmark: String = """
    from __future__ import print_function
    
    import io.github.wfouche.tulip.user.HttpUser as HttpUser
    import io.github.wfouche.tulip.api.TulipUserFactory as TulipUserFactory
    import io.github.wfouche.tulip.api.TulipApi as TulipApi
    import java.util.concurrent.ThreadLocalRandom as ThreadLocalRandom
        
    class JythonHttpUser(HttpUser):
    
        def __init__(self, userId, threadId):
            HttpUser.__init__(self, userId, threadId)
    
        def onStart(self):
            if self.userId == 0:
                print("Jython")
                HttpUser.onStart(self)
            return True
    
        def action1(self):
            id = ThreadLocalRandom.current().nextInt(100) + 1
            return len(self.http_GET("/posts/{id}", id)) > 0
    
        def action2(self):
            id = ThreadLocalRandom.current().nextInt(500) + 1
            return len(self.http_GET("/comments/{id}", id)) > 0
    
        def action3(self):
            id = ThreadLocalRandom.current().nextInt(200) + 1
            return len(self.http_GET("/todos/{id}", id)) > 0
    
        def onStop(self):
            return True
    
    class UserFactory(TulipUserFactory):
    
        def getUser(self, userId, className, threadId):
            return JythonHttpUser(userId, threadId)
    
    TulipApi.runTulip("benchmark_config.json", UserFactory())

""".trimIndent()

fun main(args: Array<String>) {

    displayAppInfo()

    val osid: String = "${NUM_ACTIONS-1}"
    var lang: String = "Java"
    var protocol: String = "http"
    var url: String = "http://jsonplaceholder.typicode.com/posts/1"
    val TULIP_JAVA_OPTIONS: String =
        if (java.lang.System.getenv("TULIP_JAVA_OPTIONS") != null) java.lang.System.getenv("TULIP_JAVA_OPTIONS") else "-server -Xms2g -Xmx2g -XX:+UseZGC -XX:+ZGenerational"

    var command: String = ""
    if (args.size > 0) {
        command = args.get(0)
    }

    if (command != "init") {
        java.lang.System.exit(0)
    }

    if (args.size > 1) {
        lang = args.get(1)
    }
    val list: java.util.ArrayList<String> = java.util.ArrayList<String>()
    list.add("Java")
    list.add("Kotlin")
    list.add("Groovy")
    list.add("Scala")
    list.add("Jython")
    if (list.contains(lang)) {
        lang = lang
    } else {
        lang = "Java"
    }

    if (lang == "Scala") {
        println("\nCreating a " + lang + " benchmark with Scala-CLI support")
    } else {
        println("\nCreating a " + lang + " benchmark with JBang support")
    }

    var avgAPS: String = "10.0"
    if (args.size > 2) {
        avgAPS = args.get(2)
    }

    if (args.size > 3) {
        url = args.get(3)
    }

    var version: String = VERSION
    if (args.size > 4) {
        version = args.get(4)
    }

    val path: String = "io/tulip/"
    if (lang == "Java") {
        Files.createDirectories(Paths.get(path))
        writeToFile(
            "benchmark_config.json",
            benchmarkConfig.trimStart()
                .replace("__TULIP_LANG__", lang)
                .replace("__AVG_APS__", avgAPS)
                .replace("__URL__", url)
                .replace("__ONSTOP_ID__", osid), false
        )
        writeToFile(
            path + "App.java",
            javaApp
                .replace("__TULIP_VERSION__", version)
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS),
            false
        )
        writeToFile(
            path + "JavaHttpUser.java",
            javaUser,
            false
        )
        writeToFile(
            "run_bench.sh",
            runBenchShJava
                .replace("__TULIP_VERSION__", version)
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS),
            false
        )
        if (java.lang.System.getProperty("os.name").lowercase().contains("windows")) {
            // pass
        } else {
            try {
                val cmdArray: Array<String> = arrayOf("chmod", "u+x", "run_bench.sh")
                java.lang.Runtime.getRuntime().exec(cmdArray)
            } catch (e: IOException) {
                // pass
            }
        }
        writeToFile(
            "run_bench.cmd",
            runBenchCmdJava
                .replace("__TULIP_VERSION__", version)
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS), false
        )
    }

    if (lang == "Kotlin") {
        Files.createDirectories(Paths.get(path))
        writeToFile(
            "benchmark_config.json",
            benchmarkConfig.trimStart()
                .replace("__TULIP_LANG__", lang)
                .replace("__AVG_APS__", avgAPS)
                .replace("__URL__", url)
                .replace("__ONSTOP_ID__", osid), false
        )
        writeToFile(
            path + "App.kt",
            kotlinApp
                .replace("__TULIP_VERSION__", version)
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS),
            false
        )
        writeToFile(
            path + "KotlinHttpUser.kt",
            kotlinUser,
            false
        )
        writeToFile(
            "run_bench.sh",
            runBenchShKotlin
                .replace("__TULIP_VERSION__", version)
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS), false
        )
        if (java.lang.System.getProperty("os.name").lowercase().contains("windows")) {
            // pass
        } else {
            try {
                val cmdArray: Array<String> = arrayOf("chmod", "u+x", "run_bench.sh")
                java.lang.Runtime.getRuntime().exec(cmdArray)
            } catch (e: IOException) {
                // pass
            }
        }
        writeToFile(
            "run_bench.cmd",
            runBenchCmdKotlin
                .replace("__TULIP_VERSION__", version)
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS), false
        )
    }

    if (lang == "Groovy") {
        Files.createDirectories(Paths.get(path))
        writeToFile(
            "benchmark_config.json",
            benchmarkConfig.trimStart()
                .replace("__TULIP_LANG__", lang)
                .replace("__AVG_APS__", avgAPS)
                .replace("__URL__", url)
                .replace("__ONSTOP_ID__", osid), false
        )
        writeToFile(
            path + "App.groovy",
            groovyApp
                .replace("__TULIP_VERSION__", version)
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS),
            false
        )
        writeToFile(
            path + "GroovyHttpUser.groovy",
            groovyUser,
            false
        )
        writeToFile(
            "run_bench.sh",
            runBenchShGroovy
                .replace("__TULIP_VERSION__", version)
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS), false
        )
        if (java.lang.System.getProperty("os.name").lowercase().contains("windows")) {
            // pass
        } else {
            try {
                val cmdArray: Array<String> = arrayOf("chmod", "u+x", "run_bench.sh")
                java.lang.Runtime.getRuntime().exec(cmdArray)
            } catch (e: IOException) {
                // pass
            }
        }
        writeToFile(
            "run_bench.cmd",
            runBenchCmdGroovy
                .replace("__TULIP_VERSION__", version)
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS), false
        )
    }

    if (lang == "Scala") {
        Files.createDirectories(Paths.get(path))
        writeToFile(
            "benchmark_config.json",
            benchmarkConfig.trimStart()
                .replace("__TULIP_LANG__", lang)
                .replace("__AVG_APS__", avgAPS)
                .replace("__URL__", url)
                .replace("__ONSTOP_ID__", osid), false
        )
        writeToFile(
            path + "App.scala",
            scalaApp
                .replace("__TULIP_VERSION__", version)
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS), false
        )
        writeToFile(
            path + "ScalaHttpUser.scala",
            scalaUser,
            false
        )
        writeToFile(
            "run_bench.sh",
            runBenchShScala
                .replace("__TULIP_VERSION__", version),
            false
        )
        if (java.lang.System.getProperty("os.name").lowercase().contains("windows")) {
            // pass
        } else {
            try {
                val cmdArray: Array<String> = arrayOf("chmod", "u+x", "run_bench.sh")
                java.lang.Runtime.getRuntime().exec(cmdArray)
            } catch (e: IOException) {
                // pass
            }
        }
        writeToFile(
            "run_bench.cmd",
            runBenchCmdScala
                .replace("__TULIP_VERSION__", version),
            false
        )
    }

    if (lang == "Jython") {

        writeToFile(
            "benchmark_config.json",
            benchmarkConfig.trimStart()
                .replace("__TULIP_LANG__", lang)
                .replace("__AVG_APS__", avgAPS)
                .replace("__URL__", url)
                .replace("__ONSTOP_ID__", osid), false
        )

        writeToFile(
            "Jython.java",
            JythonJava
                .replace("__TULIP_VERSION__", version)
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS),
            false
        )

        writeToFile(
            "benchmark.py",
            JythonBenchmark,
            false
        )

        writeToFile(
            "run_bench.sh",
            runBenchShJython
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS), false
        )
        if (java.lang.System.getProperty("os.name").lowercase().contains("windows")) {
            // pass
        } else {
            try {
                val cmdArray: Array<String> = arrayOf("chmod", "u+x", "run_bench.sh")
                java.lang.Runtime.getRuntime().exec(cmdArray)
            } catch (e: IOException) {
                // pass
            }
        }
        writeToFile(
            "run_bench.cmd",
            runBenchCmdJython
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS), false
        )

    }

}
