///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS io.github.wfouche.tulip:tulip-runtime:2.1.9
//JAVA 21

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TulipCli {

    static String appName = "tulip-cli";
    static String appVersion = "__JBANG_SNAPSHOT_ID__/__JBANG_SNAPSHOT_TIMESTAMP__";

    static void displayAppInfo() {
        String version = appVersion;
        if (appVersion.contains("JBANG_SNAPSHOT_ID")) {
            version = "0/2025-08-09T22:12:21";
        }
        System.err.println(appName + "/" + version + "/" + io.github.wfouche.tulip.api.TulipApi.VERSION);
    }

    static String osid = String.valueOf(io.github.wfouche.tulip.api.TulipApi.NUM_ACTIONS-1).toString();
    static String lang = "Java";
    static String url = "http://jsonplaceholder.typicode.com";
    static String TULIP_JAVA_OPTIONS = "-server -Xms2g -Xmx2g -XX:+UseZGC -XX:+ZGenerational";
    static String avgAPS = "10.0";
    static String version = io.github.wfouche.tulip.api.TulipApi.VERSION;
    static String path = "io/tulip/";

    static void writeToFile(String path, String content, Boolean append) {
        try {
            FileWriter fw = new FileWriter(path, append);
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void chmod() throws IOException, InterruptedException {
        if (!java.lang.System.getProperty("os.name").toLowerCase().contains("windows")) {
            List<String> cmd = new LinkedList<String>();
            cmd.add("chmod");
            cmd.add("+x");
            cmd.add("run_bench.sh");
            cmd.add("view_report.sh");
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.start().waitFor();
        }
    }

    static String benchmarkConfig = """
    {
        "actions": {
            "description": "jsonplaceholder.typicode.com [__TULIP_LANG__]",
            "output_filename": "benchmark_output.json",
            "report_filename": "benchmark_report.html",
            "user_class": "io.tulip.__TULIP_LANG__HttpUser",
            "user_params": {
                "url": "__URL__",
                "httpVersion": "HTTP_1_1",
                "connectTimeoutMillis": 5000,
                "readTimeoutMillis": 10000,
                "debug": true
            },
            "user_actions": {
                "1": "GET:posts",
                "2": "GET:comments",
                "3": "GET:todos"
            }
        },
        "workflows": {
            "ApiUser": {
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
                "scenario_workflow": "ApiUser",
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
                "scenario_workflow": "ApiUser",
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
    """.stripIndent();

    static String javaApp = """
    ///usr/bin/env jbang "$0" "$@" ; exit $?
    //DEPS io.github.wfouche.tulip:tulip-runtime:__TULIP_VERSION__
    //DEPS org.slf4j:slf4j-api:2.0.17
    //DEPS ch.qos.logback:logback-core:1.5.18
    //DEPS ch.qos.logback:logback-classic:1.5.18
    //DEPS org.springframework.boot:spring-boot-starter-web:3.5.5
    //SOURCES JavaHttpUser.java
    //JAVA 21
    //PREVIEW
    //RUNTIME_OPTIONS __TULIP_JAVA_OPTIONS__
    //COMPILE_OPTIONS -g
    //FILES ../../benchmark_config.json
    
    package io.tulip;
    
    import io.github.wfouche.tulip.api.TulipApi;
    
    public class App {
       public static void main(String[] args) {
          TulipApi.runTulip("benchmark_config.json");
       }
    }
    """.stripIndent();

    static String javaUser = """
    package io.tulip;

    import io.github.wfouche.tulip.user.HttpUser;
    import java.util.concurrent.ThreadLocalRandom;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    public class JavaHttpUser extends HttpUser {

        private ThreadLocalRandom random = ThreadLocalRandom.current();
    
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
            int id = random.nextInt(100)+1;
            return !http_GET("/posts/{id}", id).isEmpty();
        }

        // Action 2: GET /comments/{id}
        public boolean action2() {
            int id = random.nextInt(500)+1;
            return !http_GET("/comments/{id}", id).isEmpty();
        }

        // Action 3: GET /todos/{id}
        public boolean action3() {
            int id = random.nextInt(200)+1;
            return !http_GET("/todos/{id}", id).isEmpty();
        }
        
        public boolean onStop() {
            return true;
        }
    
        public Logger logger() {
            return logger;
        }
    
        // Logger
        private static final Logger logger = LoggerFactory.getLogger(JavaHttpUser.class);

    }
    """.stripIndent();

    static String runBenchShJava = """
    #!/bin/bash
    rm -f benchmark_report.html
    #export JBANG_JAVA_OPTIONS="__TULIP_JAVA_OPTIONS__"
    jbang run io/tulip/App.java
    echo ""
    #w3m -dump -cols 205 benchmark_report.html
    lynx -dump -width 205 benchmark_report.html
    #jbang run asciidoc@wfouche benchmark_config.adoc
    #jbang export fatjar io/tulip/App.java
    """.stripIndent();

    static String runBenchCmdJava = """
    if exist benchmark_report.html del benchmark_report.html
    REM JBANG_JAVA_OPTIONS=__TULIP_JAVA_OPTIONS__
    call jbang run io\\tulip\\App.java
    @echo off 
    echo.
    REM w3m.exe -dump -cols 205 benchmark_report.html
    REM lynx.exe -dump -width 205 benchmark_report.html
    start benchmark_report.html
    REM jbang run asciidoc@wfouche benchmark_config.adoc
    REM start benchmark_config.html
    REM jbang export fatjar io\\tulip\\App.java
    """.stripIndent();

    static String cleanCmd = """
    rd/q/s io
    del view*.cmd
    del view*.sh
    del run*.cmd
    del run*.sh
    del benchmark_*.*
    del .sdkmanrc
    rd/q/s .asciidoctor
    del wfd0.svg
    """.stripIndent();

    static String sdkmanConfig = """
    # Java
    java=21.0.8-tem
    
    # Gradle
    gradle=8.14.3
    
    # JBang
    jbang=0.129.0
    
    # Scala
    scalacli=1.8.5
    
    # MCS
    mcs=0.7.6
    
    # Maven
    maven=3.9.11
    
    # VisualVM
    visualvm=2.2
    """.stripIndent();

    static String viewBenchReportSh = """
    firefox benchmark_report.html
    """.stripIndent();

    static String viewBenchReportCmd = """
    start benchmark_report.html
    """.stripIndent();

    static void generateJavaApp() throws IOException, InterruptedException {
        Files.createDirectories(Paths.get(path));
        writeToFile(
            "benchmark_config.json",
            benchmarkConfig
                .replace("__TULIP_LANG__", lang)
                .replace("__AVG_APS__", avgAPS)
                .replace("__URL__", url)
                .replace("__ONSTOP_ID__", osid),
            false
        );

        writeToFile(
            path + "App.java",
            javaApp
                .replace("__TULIP_VERSION__", version)
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS),
            false
        );

        writeToFile(
            path + "JavaHttpUser.java",
            javaUser,
            false
        );

        writeToFile(
            "run_bench.sh",
            runBenchShJava
                .replace("__TULIP_VERSION__", version)
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS),
            false
        );

        writeToFile(
            "run_bench.cmd",
            runBenchCmdJava
                .replace("__TULIP_VERSION__", version)
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS), false
        );

        writeToFile(".sdkmanrc", sdkmanConfig, false);
        writeToFile("view_report.sh", viewBenchReportSh, false);
        writeToFile("view_report.cmd", viewBenchReportCmd, false);
        writeToFile("clean.cmd", cleanCmd, false);

        chmod();

    }

    static String kotlinApp = """
    ///usr/bin/env jbang "$0" "$@" ; exit $?
    //DEPS io.github.wfouche.tulip:tulip-runtime:__TULIP_VERSION__
    //DEPS org.slf4j:slf4j-api:2.0.17
    //DEPS ch.qos.logback:logback-core:1.5.18
    //DEPS ch.qos.logback:logback-classic:1.5.18
    //DEPS org.springframework.boot:spring-boot-starter-web:3.5.5
    //SOURCES KotlinHttpUser.kt
    //JAVA 21
    //KOTLIN 2.1.21
    //RUNTIME_OPTIONS __TULIP_JAVA_OPTIONS__
    //COMPILE_OPTIONS -progressive
    //FILES ../../benchmark_config.json

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
    """.stripIndent();

    static String kotlinUser = """
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
    
        override fun logger(): Logger {
            return logger
        }
    
        // RestClient object
        companion object {
            private val logger = LoggerFactory.getLogger(KotlinHttpUser::class.java)
        }
    }
    """.stripIndent();

    static String runBenchShKotlin = """
    #!/bin/bash
    rm -f benchmark_report.html
    #export JBANG_JAVA_OPTIONS="__TULIP_JAVA_OPTIONS__"
    jbang run io/tulip/App.kt
    echo ""
    #w3m -dump -cols 205 benchmark_report.html
    lynx -dump -width 205 benchmark_report.html
    #jbang run asciidoc@wfouche benchmark_config.adoc
    #jbang export fatjar io/tulip/App.kt
    """.stripIndent();

    static String runBenchCmdKotlin = """
    if exist benchmark_report.html del benchmark_report.html
    REM JBANG_JAVA_OPTIONS=__TULIP_JAVA_OPTIONS__
    call jbang run io\\tulip\\App.kt
    @echo off
    echo.
    REM call w3m.exe -dump -cols 205 benchmark_report.html
    REM lynx.exe -dump -width 205 benchmark_report.html
    start benchmark_report.html
    REM jbang run asciidoc@wfouche benchmark_config.adoc
    REM start benchmark_config.html
    REM jbang export fatjar io\\tulip\\App.kt
    """.stripIndent();

    static void generateKotlinApp() throws IOException, InterruptedException {
        Files.createDirectories(Paths.get(path));

        writeToFile(
                "benchmark_config.json",
                benchmarkConfig
                        .replace("__TULIP_LANG__", lang)
                        .replace("__AVG_APS__", avgAPS)
                        .replace("__URL__", url)
                        .replace("__ONSTOP_ID__", osid),
                false
        );

        writeToFile(
                path + "App.kt",
                kotlinApp
                        .replace("__TULIP_VERSION__", version)
                        .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS),
                false
        );

        writeToFile(
                path + "KotlinHttpUser.kt",
                kotlinUser,
                false
        );

        writeToFile(
                "run_bench.sh",
                runBenchShKotlin
                        .replace("__TULIP_VERSION__", version)
                        .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS),
                false
        );

        writeToFile(
                "run_bench.cmd",
                runBenchCmdKotlin
                        .replace("__TULIP_VERSION__", version)
                        .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS), false
        );

        writeToFile(".sdkmanrc", sdkmanConfig, false);
        writeToFile("view_report.sh", viewBenchReportSh, false);
        writeToFile("view_report.cmd", viewBenchReportCmd, false);
        writeToFile("clean.cmd", cleanCmd, false);

        chmod();
    }

    static String groovyApp = """
    ///usr/bin/env jbang "$0" "$@" ; exit $?
    //DEPS io.github.wfouche.tulip:tulip-runtime:__TULIP_VERSION__
    //DEPS org.slf4j:slf4j-api:2.0.17
    //DEPS ch.qos.logback:logback-core:1.5.18
    //DEPS ch.qos.logback:logback-classic:1.5.18
    //DEPS org.springframework.boot:spring-boot-starter-web:3.5.5
    //SOURCES GroovyHttpUser.groovy
    //JAVA 21
    //GROOVY 4.0.28
    //RUNTIME_OPTIONS __TULIP_JAVA_OPTIONS__
    //COMPILE_OPTIONS --tolerance=5
    //FILES ../../benchmark_config.json

    package io.tulip

    import io.github.wfouche.tulip.api.TulipApi

    class App {
        static void main(String[] args) {
            TulipApi.runTulip("benchmark_config.json")
        }
    }
    """.stripIndent();

    static String groovyUser = """
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
    
        Logger logger() {
            return logger
        }

        // Logger
        static Logger logger = LoggerFactory.getLogger(GroovyHttpUser.class)
    
    }
    """.stripIndent();

    static String runBenchShGroovy = """
    #!/bin/bash
    rm -f benchmark_report.html
    #export JBANG_JAVA_OPTIONS="__TULIP_JAVA_OPTIONS__"
    jbang run io/tulip/App.groovy
    echo ""
    #w3m -dump -cols 205 benchmark_report.html
    lynx -dump -width 205 benchmark_report.html
    #jbang run asciidoc@wfouche benchmark_config.adoc
    #jbang export fatjar io/tulip/App.groovy
    """.stripIndent();

    static String runBenchCmdGroovy = """
    REM
    REM JBang / Groovy / Tulip is not supported on Windows
    REM Try running the benchmark on Linux or macOS
    REM
    REM if exist benchmark_report.html del benchmark_report.html
    REM JBANG_JAVA_OPTIONS=__TULIP_JAVA_OPTIONS__
    REM call jbang run io\\tulip\\App.groovy
    REM @echo off
    REM echo.
    REM call w3m.exe -dump -cols 205 benchmark_report.html
    REM lynx.exe -dump -width 205 benchmark_report.html
    REM start benchmark_report.html
    REM jbang run asciidoc@wfouche benchmark_config.adoc
    REM start benchmark_config.html
    REM jbang export fatjar io\\tulip\\App.groovy
    """.stripIndent();

    static void generateGroovyApp() throws IOException, InterruptedException {
        Files.createDirectories(Paths.get(path));

        writeToFile(
                "benchmark_config.json",
                benchmarkConfig
                        .replace("__TULIP_LANG__", lang)
                        .replace("__AVG_APS__", avgAPS)
                        .replace("__URL__", url)
                        .replace("__ONSTOP_ID__", osid),
                false
        );

        writeToFile(
                path + "App.groovy",
                groovyApp
                        .replace("__TULIP_VERSION__", version)
                        .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS),
                false
        );

        writeToFile(
                path + "GroovyHttpUser.groovy",
                groovyUser,
                false
        );

        writeToFile(
                "run_bench.sh",
                runBenchShGroovy
                        .replace("__TULIP_VERSION__", version)
                        .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS),
                false
        );

        writeToFile(
                "run_bench.cmd",
                runBenchCmdGroovy
                        .replace("__TULIP_VERSION__", version)
                        .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS), false
        );

        writeToFile(".sdkmanrc", sdkmanConfig, false);
        writeToFile("view_report.sh", viewBenchReportSh, false);
        writeToFile("view_report.cmd", viewBenchReportCmd, false);
        writeToFile("clean.cmd", cleanCmd, false);

        chmod();
    }

    static String scalaApp = """
    //> using jvm 21
    //> using dep io.github.wfouche.tulip:tulip-runtime:__TULIP_VERSION__
    //> using dep org.slf4j:slf4j-api:2.0.17
    //> using dep ch.qos.logback:logback-core:1.5.18
    //> using dep ch.qos.logback:logback-classic:1.5.18
    //> using dep org.springframework.boot:spring-boot-starter-web:3.5.5
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
    """.stripIndent();

    static String scalaUser = """
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
    
      override def logger(): Logger = {
        return loggerz
      }

    }

    // Logger
    val loggerz: Logger = LoggerFactory.getLogger(classOf[ScalaHttpUser])
    """.stripIndent();

    static String runBenchShScala = """
    #!/bin/bash
    rm -f benchmark_report.html
    scala-cli io/tulip/App.scala io/tulip/ScalaHttpUser.scala
    echo ""
    #w3m -dump -cols 205 benchmark_report.html
    lynx -dump -width 205 benchmark_report.html
    #jbang run asciidoc@wfouche benchmark_config.adoc
    """.stripIndent();

    static String runBenchCmdScala = """
    if exist benchmark_report.html del benchmark_report.html
    scala-cli io\\tulip\\App.scala io\\tulip\\ScalaHttpUser.scala
    @echo off
    echo.
    REM call w3m.exe -dump -cols 205 benchmark_report.html
    REM lynx.exe -dump -width 205 benchmark_report.html
    start benchmark_report.html
    REM jbang asciidoc@wfouche benchmark_config.adoc
    REM start benchmark_config.html
    """.stripIndent();

    static void generateScalaApp() throws IOException, InterruptedException {
        Files.createDirectories(Paths.get(path));

        writeToFile(
                "benchmark_config.json",
                benchmarkConfig
                        .replace("__TULIP_LANG__", lang)
                        .replace("__AVG_APS__", avgAPS)
                        .replace("__URL__", url)
                        .replace("__ONSTOP_ID__", osid),
                false
        );

        writeToFile(
                path + "App.scala",
                scalaApp
                        .replace("__TULIP_VERSION__", version)
                        .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS),
                false
        );

        writeToFile(
                path + "ScalaHttpUser.scala",
                scalaUser,
                false
        );

        writeToFile(
                "run_bench.sh",
                runBenchShScala
                        .replace("__TULIP_VERSION__", version)
                        .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS),
                false
        );

        writeToFile(
                "run_bench.cmd",
                runBenchCmdScala
                        .replace("__TULIP_VERSION__", version)
                        .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS), false
        );

        writeToFile(".sdkmanrc", sdkmanConfig, false);
        writeToFile("view_report.sh", viewBenchReportSh, false);
        writeToFile("view_report.cmd", viewBenchReportCmd, false);

        chmod();
    }

    static String JythonJava = """
    ///usr/bin/env jbang "$0" "$@" ; exit $?
    
    //DEPS org.python:jython-standalone:2.7.4
    //DEPS io.github.wfouche.tulip:tulip-runtime:__TULIP_VERSION__
    //DEPS org.slf4j:slf4j-api:2.0.17
    //DEPS ch.qos.logback:logback-core:1.5.18
    //DEPS ch.qos.logback:logback-classic:1.5.18
    //DEPS org.springframework.boot:spring-boot-starter-web:3.5.5
    //JAVA 21
    //RUNTIME_OPTIONS __TULIP_JAVA_OPTIONS__
    
    import org.python.util.jython;
    
    public class Jython {
        public static void main(String[] args) {
            jython.main(args);
        }
    }
    """.stripIndent();

    static String JythonBenchmark = """
    from __future__ import print_function

    # /// jbang
    # requires-jython = "2.7.4"
    # requires-java = "21"
    # dependencies = [
    #   "io.github.wfouche.tulip:tulip-runtime:__TULIP_VERSION__",
    #   "org.slf4j:slf4j-api:2.0.17",
    #   "ch.qos.logback:logback-core:1.5.18",
    #   "ch.qos.logback:logback-classic:1.5.18",
    #   "org.springframework.boot:spring-boot-starter-web:3.5.5"
    # ]
    # runtime-options = [
    #   "-server", "-Xms2g", "-Xmx2g", "-XX:+UseZGC", "-XX:+ZGenerational"
    # ]
    # ///

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

    """.stripIndent();

    static String runBenchShJython = """
    #!/bin/bash
    rm -f benchmark_report.html
    #export JBANG_JAVA_OPTIONS="__TULIP_JAVA_OPTIONS__"
    #jbang run Jython.java benchmark.py
    jbang run jython-cli@jython benchmark.py
    echo ""
    #w3m -dump -cols 205 benchmark_report.html
    lynx -dump -width 205 benchmark_report.html
    #jbang run asciidoc@wfouche benchmark_config.adoc
    """.stripIndent();

    static String runBenchCmdJython = """
    if exist benchmark_report.html del benchmark_report.html
    REM JBANG_JAVA_OPTIONS=__TULIP_JAVA_OPTIONS__
    REM call jbang run Jython.java benchmark.py
    call jbang run jython-cli@jython benchmark.py
    @echo off
    echo.
    REM call w3m.exe -dump -cols 205 benchmark_report.html
    REM lynx.exe -dump -width 205 benchmark_report.html
    start benchmark_report.html
    REM jbang run asciidoc@wfouche benchmark_config.adoc
    REM start benchmark_config.html
    """.stripIndent();

    static void generateJythonApp() throws IOException, InterruptedException {
        //Files.createDirectories(Paths.get(path));

        writeToFile(
                "benchmark_config.json",
                benchmarkConfig
                        .replace("__TULIP_LANG__", lang)
                        .replace("__AVG_APS__", avgAPS)
                        .replace("__URL__", url)
                        .replace("__ONSTOP_ID__", osid),
                false
        );

//        writeToFile(
//                "Jython.java",
//                JythonJava
//                        .replace("__TULIP_VERSION__", version)
//                        .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS),
//                false
//        );

        writeToFile(
                "benchmark.py",
                JythonBenchmark
                        .replace("__TULIP_VERSION__", version)
                        .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS),
                false
        );

        writeToFile(
                "run_bench.sh",
                runBenchShJython
                        .replace("__TULIP_VERSION__", version)
                        .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS),
                false
        );

        writeToFile(
                "run_bench.cmd",
                runBenchCmdJython
                        .replace("__TULIP_VERSION__", version)
                        .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS), false
        );

        writeToFile(".sdkmanrc", sdkmanConfig, false);
        writeToFile("view_report.sh", viewBenchReportSh, false);
        writeToFile("view_report.cmd", viewBenchReportCmd, false);

        chmod();
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        displayAppInfo();

        if (java.lang.System.getenv("TULIP_JAVA_OPTIONS") != null) {
            TULIP_JAVA_OPTIONS = java.lang.System.getenv("TULIP_JAVA_OPTIONS");
        }

        String command = "";
        if (args.length > 0) {
            command = args[0];
        }

        if (!command.equals("init")) {
            java.lang.System.exit(0);
        }

        if (args.length > 1) {
            lang = args[1];
        }

        List<String> list = new LinkedList<String>();
        list.add("Java");
        list.add("Kotlin");
        list.add("Groovy");
        list.add("Scala");
        list.add("Jython");
        if (!list.contains(lang)) {
            lang = "Java";
        }

        if (lang.equals("Scala")) {
            System.out.println("\nCreating a " + lang + " benchmark with Scala-CLI support");
        } else {
            System.out.println("\nCreating a " + lang + " benchmark with JBang support");
        }

        if (args.length > 2) {
            avgAPS = args[2];
        }
        
        if (args.length > 3) {
            url = args[3];
        }
        
        if (args.length > 4) {
            version = args[4];
        }

        if (lang.equals("Java")) {
            generateJavaApp();
        }

        if (lang.equals("Kotlin")) {
            generateKotlinApp();
        }

        if (lang.equals("Groovy")) {
            generateGroovyApp();
        }

        if (lang.equals("Scala")) {
            generateScalaApp();
        }

        if (lang.equals("Jython")) {
            generateJythonApp();
        }
    }
}