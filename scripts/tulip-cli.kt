///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.github.wfouche.tulip:tulip-runtime:2.1.6
//JAVA 21
//KOTLIN 2.0.21

import io.github.wfouche.tulip.api.TulipApi.VERSION
import io.github.wfouche.tulip.api.TulipApi.NUM_ACTIONS
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

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
        "user_class": "io.tulip.HttpUser",
        "user_params": {
            "protocol": "__PROTOCOL__",
            "host": "__HOST__",
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
    //DEPS org.springframework.boot:spring-boot-starter-web:3.4.3
    //DEPS org.slf4j:slf4j-api:2.0.17
    //DEPS ch.qos.logback:logback-core:1.5.18
    //DEPS ch.qos.logback:logback-classic:1.5.18
    //SOURCES HttpUser.java
    //JAVA 21
    
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
    //DEPS org.springframework.boot:spring-boot-starter-web:3.4.3
    //DEPS org.slf4j:slf4j-api:2.0.17
    //DEPS ch.qos.logback:logback-core:1.5.18
    //DEPS ch.qos.logback:logback-classic:1.5.18
    //SOURCES HttpUser.kt
    //JAVA 21
    //KOTLIN 2.0.21
    
    package io.tulip
    
    import io.github.wfouche.tulip.api.TulipApi
    
    fun main(args: Array<String>) {
        TulipApi.runTulip("benchmark_config.json")
    }
""".trimIndent()

val groovyApp: String = """
    ///usr/bin/env jbang "${'$'}0" "${'$'}@" ; exit ${'$'}?
    //DEPS io.github.wfouche.tulip:tulip-runtime:__TULIP_VERSION__
    //DEPS org.springframework.boot:spring-boot-starter-web:3.4.3
    //DEPS org.slf4j:slf4j-api:2.0.17
    //DEPS ch.qos.logback:logback-core:1.5.18
    //DEPS ch.qos.logback:logback-classic:1.5.18
    //SOURCES HttpUser.groovy
    //JAVA 21
    //GROOVY 4.0.26
    
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
    //> using dep org.springframework.boot:spring-boot-starter-web:3.4.3
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

    import io.github.wfouche.tulip.api.*;
    import java.util.concurrent.ThreadLocalRandom;
    import org.springframework.web.client.RestClient;
    import org.springframework.web.client.RestClientException;
    import org.springframework.http.client.SimpleClientHttpRequestFactory;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    public class HttpUser extends TulipUser {

        public HttpUser(int userId, int threadId) {
            super(userId, threadId);
        }

        public boolean onStart() {
            // Initialize the shared RestClient object only once
            if (getUserId() == 0) {
                logger.info("Java");
                logger.info("Initializing static data");
                var connectTimeout = Integer.valueOf(getUserParamValue("connectTimeoutMillis"));
                var readTimeout = Integer.valueOf(getUserParamValue("readTimeoutMillis"));
                var factory = new SimpleClientHttpRequestFactory();
                factory.setConnectTimeout(connectTimeout);
                factory.setReadTimeout(readTimeout);
                var url = getUserParamValue("protocol") + "://" + getUserParamValue("host");
                client = RestClient.builder()
                    .requestFactory(factory)
                    .baseUrl(url)
                    .build();
                debug = Boolean.valueOf(getUserParamValue("debug"));
                logger.info("debug = " + debug);
                if (debug) {
                    logger.info(url);
                }
            }
            return true;
        }

        // Action 1: GET /posts/{id}
        public boolean action1() {
            int id = ThreadLocalRandom.current().nextInt(100)+1;
            return serviceCall("/posts/{id}", id);
        }

        // Action 2: GET /comments/{id}
        public boolean action2() {
            int id = ThreadLocalRandom.current().nextInt(500)+1;
            return serviceCall("/comments/{id}", id);
        }

        // Action 3: GET /todos/{id}
        public boolean action3() {
            int id = ThreadLocalRandom.current().nextInt(200)+1;
            return serviceCall("/todos/{id}", id);
        }

        public boolean onStop() {
            return true;
        }

        private boolean serviceCall(String uri, int id) {
            boolean rc;
            try {
                String rsp = client.get()
                    .uri(uri, id)
                    .retrieve()
                    .body(String.class);
                rc = (rsp != null && rsp.length() > 2);
            } catch (RestClientException e) {
                rc = false;
            }
            return rc;
        }

        // RestClient object
        private static RestClient client;

        // Debug flag
        private static boolean debug = false;

        // Logger
        private static final Logger logger = LoggerFactory.getLogger(HttpUser.class);

    }    
""".trimIndent()

val kotlinUser: String = """
    package io.tulip
    
    import io.github.wfouche.tulip.api.*
    import java.util.concurrent.ThreadLocalRandom
    import org.springframework.web.client.RestClient
    import org.springframework.web.client.RestClientException
    import org.springframework.http.client.SimpleClientHttpRequestFactory
    import org.slf4j.Logger
    import org.slf4j.LoggerFactory
    
    class HttpUser(userId: Int, threadId: Int) : TulipUser(userId, threadId) {
    
        // Action 0
        override fun onStart(): Boolean {
            // Initialize the shared RestClient object only once
            if (userId == 0) {
                logger.info("Kotlin")
                logger.info("Initializing static data")
                val connectTimeout = getUserParamValue("connectTimeoutMillis").toInt()
                val readTimeout = getUserParamValue("readTimeoutMillis").toInt()
                val factory = SimpleClientHttpRequestFactory().apply {
                    setConnectTimeout(connectTimeout)
                    setReadTimeout(readTimeout)
                }
                val url = getUserParamValue("protocol") + "://" + getUserParamValue("host")
                client = RestClient.builder()
                    .requestFactory(factory)
                    .baseUrl(url)
                    .build()
                debug = getUserParamValue("debug").toBoolean()
                logger.info("debug = " + debug)
                if (debug) {
                    logger.info(url)
                }
            }
            return true
        }
    
        // Action 1: GET /posts/{id}
        override fun action1(): Boolean {
            val id: Int = ThreadLocalRandom.current().nextInt(100)+1
            return try {
                val rsp: String? = client.get()
                    .uri("/posts/{id}", id)
                    .retrieve()
                    .body(String::class.java)
                //Postcondition
                (rsp != null && rsp.length > 2)
            } catch (e: RestClientException) {
                false
            }
        }
    
        // Action 2: GET /comments/{id}
        override fun action2(): Boolean {
            val id: Int = ThreadLocalRandom.current().nextInt(500)+1
            return try {
                val rsp: String? = client.get()
                    .uri("/comments/{id}", id)
                    .retrieve()
                    .body(String::class.java)
                //Postcondition
                (rsp != null && rsp.length > 2)
            } catch (e: RestClientException) {
                false
            }
        }
    
        // Action 3: GET /todos/{id}
        override fun action3(): Boolean {
            val id: Int = ThreadLocalRandom.current().nextInt(200)+1
            return try {
                val rsp: String? = client.get()
                    .uri("/todos/{id}", id)
                    .retrieve()
                    .body(String::class.java)
                //Postcondition
                (rsp != null && rsp.length > 2)
            } catch (e: RestClientException) {
                false
            }
        }
    
        override fun onStop(): Boolean {
            return true
        }
    
        // RestClient object
        companion object {
            private lateinit var client: RestClient
            private var debug: Boolean = false
            private val logger = LoggerFactory.getLogger(HttpUser::class.java)
        }
    }
""".trimIndent()

val groovyUser = """
    package io.tulip
    
    import io.github.wfouche.tulip.api.*
    import org.springframework.web.client.RestClient
    import org.springframework.web.client.RestClientException
    import org.springframework.http.client.SimpleClientHttpRequestFactory
    import java.util.concurrent.ThreadLocalRandom
    import org.slf4j.Logger
    import org.slf4j.LoggerFactory
    
    class HttpUser extends TulipUser {
    
        HttpUser(int userId, int threadId) {
            super(userId, threadId)
        }
    
        boolean onStart() {
            // Initialize the shared RestClient object only once
            if (userId == 0) {
                logger.info("Groovy")
                logger.info("Initializing static data")
                def connectTimeout = getUserParamValue("connectTimeoutMillis") as Integer
                def readTimeout = getUserParamValue("readTimeoutMillis") as Integer
                def factory = new SimpleClientHttpRequestFactory()
                factory.setConnectTimeout(connectTimeout)
                factory.setReadTimeout(readTimeout)
                def url = getUserParamValue("protocol") + "://" + getUserParamValue("host")
                client = RestClient.builder()
                    .requestFactory(factory)
                    .baseUrl(url)
                    .build()
                def debug = Boolean.valueOf(getUserParamValue("debug"))
                logger.info("debug = " + debug)
                if (debug) {
                    logger.info(url)
                }
            }
            return true
        }
    
        // Action 1: GET /posts/{id}
        boolean action1() {
            boolean rc
            try {
                int id = ThreadLocalRandom.current().nextInt(100) + 1
                String rsp = client.get()
                    .uri("/posts/{id}", id)
                    .retrieve()
                    .body(String.class)
                rc = (rsp != null && rsp.length() > 2)
            } catch (RestClientException e) {
                rc = false
            }
            return rc
        }
    
        // Action 2: GET /comments/{id}
        boolean action2() {
            boolean rc
            try {
                int id = ThreadLocalRandom.current().nextInt(500) + 1
                String rsp = client.get()
                    .uri("/comments/{id}", id)
                    .retrieve()
                    .body(String.class)
                rc = (rsp != null && rsp.length() > 2)
            } catch (RestClientException e) {
                rc = false
            }
            return rc
        }
    
        // Action 3: GET /todos/{id}
        boolean action3() {
            boolean rc
            try {
                int id = ThreadLocalRandom.current().nextInt(200) + 1
                String rsp = client.get()
                    .uri("/todos/{id}", id)
                    .retrieve()
                    .body(String.class)
                rc = (rsp != null && rsp.length() > 2)
            } catch (RestClientException e) {
                rc = false
            }
            return rc
        }
    
        boolean onStop() {
            return true
        }
    
        // RestClient object
        static RestClient client
    
        // Debug flag
        static boolean debug = false
    
        // Logger
        static Logger logger = LoggerFactory.getLogger(HttpUser.class)
    
    }    
""".trimIndent()

val scalaUser: String = """
    package io.tulip
    
    import io.github.wfouche.tulip.api._
    import java.util.concurrent.ThreadLocalRandom
    import org.springframework.web.client.RestClient
    import org.springframework.web.client.RestClientException
    import org.springframework.http.client.SimpleClientHttpRequestFactory
    import org.slf4j.Logger
    import org.slf4j.LoggerFactory
    import scala.compiletime.uninitialized
    
    class HttpUser(userId: Int, threadId: Int) extends TulipUser(userId, threadId) {
    
      override def onStart(): Boolean = {
        // Initialize the shared RestClient object only once
        if (getUserId == 0) {
          logger.info("Scala")
          logger.info("Initializing static data")
          val connectTimeout = getUserParamValue("connectTimeoutMillis").toInt
          val readTimeout = getUserParamValue("readTimeoutMillis").toInt
          val factory = new SimpleClientHttpRequestFactory()
          factory.setConnectTimeout(connectTimeout)
          factory.setReadTimeout(readTimeout)
          val url = getUserParamValue("protocol") + "://" + getUserParamValue("host")
          client = RestClient.builder()
            .requestFactory(factory)
            .baseUrl(url)
            .build()
          debug = getUserParamValue("debug").toBoolean
          logger.info(s"debug = ${'$'}debug")
          if (debug) {
            logger.info(url)
          }
        }
        true
      }
    
      // Action 1: GET /posts/{id}
      override def action1(): Boolean = {
        try {
          val id = ThreadLocalRandom.current().nextInt(100) + 1
          val rsp = client.get()
            .uri("/posts/{id}", id)
            .retrieve()
            .body(classOf[String])
          rsp != null && rsp.length > 2
        } catch {
          case _: RestClientException => false
        }
      }
    
      // Action 2: GET /comments/{id}
      override def action2(): Boolean = {
        try {
          val id = ThreadLocalRandom.current().nextInt(500) + 1
          val rsp = client.get()
            .uri("/comments/{id}", id)
            .retrieve()
            .body(classOf[String])
          rsp != null && rsp.length > 2
        } catch {
          case _: RestClientException => false
        }
      }
    
      // Action 3: GET /todos/{id}
      override def action3(): Boolean = {
        try {
          val id = ThreadLocalRandom.current().nextInt(200) + 1
          val rsp = client.get()
            .uri("/todos/{id}", id)
            .retrieve()
            .body(classOf[String])
          rsp != null && rsp.length > 2
        } catch {
          case _: RestClientException => false
        }
      }
    
      override def onStop(): Boolean = true
    }
    
    // RestClient object
    var client: RestClient = uninitialized
    
    // Debug flag
    var debug: Boolean = false
    
    // Logger
    val logger: Logger = LoggerFactory.getLogger(classOf[HttpUser])    
""".trimIndent()

val runBenchShJava: String = """
    #!/bin/bash
    rm -f benchmark_report.html
    export JBANG_JAVA_OPTIONS="__TULIP_JAVA_OPTIONS__"
    jbang run io/tulip/App.java
    echo ""
    #w3m -dump -cols 205 benchmark_report.html
    lynx -dump -width 205 benchmark_report.html
    #jbang run asciidoc@wfouche benchmark_config.adoc
    #jbang export fatjar io/tulip/App.java
    
""".trimIndent()

val runBenchCmdJava: String = """
    if exist benchmark_report.html del benchmark_report.html
    set JBANG_JAVA_OPTIONS=__TULIP_JAVA_OPTIONS__
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
    export JBANG_JAVA_OPTIONS="__TULIP_JAVA_OPTIONS__"
    jbang run io/tulip/App.kt
    echo ""
    #w3m -dump -cols 205 benchmark_report.html
    lynx -dump -width 205 benchmark_report.html
    #jbang run asciidoc@wfouche benchmark_config.adoc
    #jbang export fatjar io/tulip/App.kt
    
""".trimIndent()

val runBenchCmdKotlin: String = """
    if exist benchmark_report.html del benchmark_report.html
    set JBANG_JAVA_OPTIONS=__TULIP_JAVA_OPTIONS__
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
    export JBANG_JAVA_OPTIONS="__TULIP_JAVA_OPTIONS__"
    jbang run io/tulip/App.groovy
    echo ""
    #w3m -dump -cols 205 benchmark_report.html
    lynx -dump -width 205 benchmark_report.html
    #jbang run asciidoc@wfouche benchmark_config.adoc
    #jbang export fatjar io/tulip/App.groovy
    
""".trimIndent()

val runBenchCmdGroovy: String = """
    if exist benchmark_report.html del benchmark_report.html
    set JBANG_JAVA_OPTIONS=__TULIP_JAVA_OPTIONS__
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
    scala-cli io/tulip/App.scala io/tulip/HttpUser.scala
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
    export JBANG_JAVA_OPTIONS="__TULIP_JAVA_OPTIONS__"
    jbang run Jython.java benchmark.py
    echo ""
    #w3m -dump -cols 205 benchmark_report.html
    lynx -dump -width 205 benchmark_report.html
    #jbang run asciidoc@wfouche benchmark_config.adoc
    
""".trimIndent()

val runBenchCmdJython: String = """
    if exist benchmark_report.html del benchmark_report.html
    set JBANG_JAVA_OPTIONS=__TULIP_JAVA_OPTIONS__
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
    //DEPS org.springframework.boot:spring-boot-starter-web:3.4.3
    //DEPS org.slf4j:slf4j-api:2.0.17
    //DEPS ch.qos.logback:logback-core:1.5.18
    //DEPS ch.qos.logback:logback-classic:1.5.18
    //JAVA 21
    
    import org.python.util.jython;
    
    public class Jython {
        public static void main(String[] args) {
            jython.main(args);
        }
    }

""".trimIndent()

val JythonBenchmark: String = """
    from __future__ import print_function
    
    import io.github.wfouche.tulip.api.TulipUser as TulipUser
    import io.github.wfouche.tulip.api.TulipUserFactory as TulipUserFactory
    import io.github.wfouche.tulip.api.TulipApi as TulipApi
    import org.springframework.web.client.RestClient as RestClient
    import org.springframework.web.client.RestClientException as RestClientException
    import org.springframework.http.client.SimpleClientHttpRequestFactory as SimpleClientHttpRequestFactory
    import java.util.concurrent.ThreadLocalRandom as ThreadLocalRandom
    import java.lang.String
    
    client = None
    
    class HttpUser(TulipUser):
    
        def __init__(self, userId, threadId):
            TulipUser.__init__(self, userId, threadId)
    
        def onStart(self):
            if self.userId == 0:
                print("Jython")
                print("Initializing static data")
                connectTimeout = int(self.getUserParamValue("connectTimeoutMillis"))
                readTimeout = int(self.getUserParamValue("readTimeoutMillis"))
                factory = SimpleClientHttpRequestFactory()
                factory.setConnectTimeout(connectTimeout)
                factory.setReadTimeout(readTimeout)
                url = self.getUserParamValue("protocol") + "://" + self.getUserParamValue("host")
                global client
                client = RestClient.builder().requestFactory(factory).baseUrl(url).build()
            return True
    
        def action1(self):
            rc = False
            try:
                id = ThreadLocalRandom.current().nextInt(100) + 1
                rsp = client.get().uri("/posts/{id}", id).retrieve().body(java.lang.String)
                rc = (rsp is not None and len(rsp) > 2)
            except RestClientException:
                pass
            return rc
    
        def action2(self):
            rc = False
            try:
                id = ThreadLocalRandom.current().nextInt(500) + 1
                rsp = client.get().uri("/comments/{id}", id).retrieve().body(java.lang.String)
                rc = (rsp is not None and len(rsp) > 2)
            except RestClientException:
                pass
            return rc
    
        def action3(self):
            rc = False
            try:
                id = ThreadLocalRandom.current().nextInt(200) + 1
                rsp = client.get().uri("/todos/{id}", id).retrieve().body(java.lang.String)
                rc = (rsp is not None and len(rsp) > 2)
            except RestClientException:
                pass
            return rc
    
        def onStop(self):
            return True
    
    class UserFactory(TulipUserFactory):
    
        def getUser(self, userId, className, threadId):
            return HttpUser(userId, threadId)
    
    TulipApi.runTulip("benchmark_config.json", UserFactory())

""".trimIndent()

fun main(args: Array<String>) {

    val osid: String = "${NUM_ACTIONS-1}"
    var lang: String = "Java"
    var protocol: String = "http"
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
        println("tulip-cli ($VERSION): creating a " + lang + " benchmark with Scala-CLI support")
    } else {
        println("tulip-cli ($VERSION): creating a " + lang + " benchmark with JBang support")
    }

    var avgAPS: String = "10.0"
    if (args.size > 2) {
        avgAPS = args.get(2)
    }

    if (args.size > 3) {
        protocol = args.get(3)
    }

    var host: String = "jsonplaceholder.typicode.com"
    if (args.size > 4) {
        host = args.get(4)
    }

    var version: String = VERSION
    if (args.size > 5) {
        version = args.get(5)
    }

    val path: String = "io/tulip/"
    if (lang == "Java") {
        Files.createDirectories(Paths.get(path))
        writeToFile(
            "benchmark_config.json",
            benchmarkConfig.trimStart()
                .replace("__TULIP_LANG__", lang)
                .replace("__AVG_APS__", avgAPS)
                .replace("__HOST__", host)
                .replace("__PROTOCOL__", protocol)
                .replace("__ONSTOP_ID__", osid), false
        )
        writeToFile(
            path + "App.java",
            javaApp
                .replace("__TULIP_VERSION__", version),
            false
        )
        writeToFile(
            path + "HttpUser.java",
            javaUser,
            false
        )
        writeToFile(
            "run_bench.sh",
            runBenchShJava
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
                .replace("__HOST__", host)
                .replace("__PROTOCOL__", protocol)
                .replace("__ONSTOP_ID__", osid), false
        )
        writeToFile(
            path + "App.kt",
            kotlinApp
                .replace("__TULIP_VERSION__", version),
            false
        )
        writeToFile(
            path + "HttpUser.kt",
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
                .replace("__HOST__", host)
                .replace("__PROTOCOL__", protocol)
                .replace("__ONSTOP_ID__", osid), false
        )
        writeToFile(
            path + "App.groovy",
            groovyApp
                .replace("__TULIP_VERSION__", version),
            false
        )
        writeToFile(
            path + "HttpUser.groovy",
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
                .replace("__HOST__", host)
                .replace("__PROTOCOL__", protocol)
                .replace("__ONSTOP_ID__", osid), false
        )
        writeToFile(
            path + "App.scala",
            scalaApp
                .replace("__TULIP_VERSION__", version)
                .replace("__TULIP_JAVA_OPTIONS__", TULIP_JAVA_OPTIONS), false
        )
        writeToFile(
            path + "HttpUser.scala",
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
                .replace("__HOST__", host)
                .replace("__PROTOCOL__", protocol)
                .replace("__ONSTOP_ID__", osid), false
        )

        writeToFile(
            "Jython.java",
            JythonJava
                .replace("__TULIP_VERSION__", version),
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
