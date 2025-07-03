///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS org.asciidoctor:asciidoctorj:3.0.0
//DEPS org.asciidoctor:asciidoctorj-api:3.0.0
//DEPS org.asciidoctor:asciidoctorj-cli:3.0.0
//DEPS org.asciidoctor:asciidoctorj-epub3:2.1.3
//DEPS org.asciidoctor:asciidoctorj-diagram:3.0.1
//DEPS org.asciidoctor:asciidoctorj-diagram-batik:1.17
//DEPS org.asciidoctor:asciidoctorj-diagram-ditaamini:1.0.3
//DEPS org.asciidoctor:asciidoctorj-diagram-plantuml:1.2025.3
//DEPS org.asciidoctor:asciidoctorj-diagram-jsyntrax:1.38.2
//DEPS org.asciidoctor:asciidoctorj-pdf:2.3.19
//DEPS org.asciidoctor:asciidoctorj-revealjs:5.2.0
//DEPS com.beust:jcommander:1.82
//DEPS org.jruby:jruby-complete:9.4.8.0

import java.io.IOException;

// Check dependency updates
// $ jbang export maven asciidoctorj.java
// $ cd asciidoctorj
// $ ./mvnw versions:display-dependency-updates
// [INFO] Scanning for projects...
// [INFO]
// [INFO] ------------------< org.example.project:asciidoctorj >------------------
// [INFO] Building asciidoctorj 999-SNAPSHOT
// [INFO]   from pom.xml
// [INFO] --------------------------------[ jar ]---------------------------------
// [INFO]
// [INFO] --- versions:2.18.0:display-dependency-updates (default-cli) @ asciidoctorj ---
// [INFO] The following dependencies in Dependencies have newer versions:
// [INFO]   org.asciidoctor:asciidoctorj-epub3 .................... 2.1.3 -> 2.2.0
// [INFO]   org.jruby:jruby-complete ......................... 9.4.8.0 -> 10.0.0.1
// [INFO]
// [INFO] ------------------------------------------------------------------------
// [INFO] BUILD SUCCESS
// [INFO] ------------------------------------------------------------------------
// [INFO] Total time:  1.961 s
// [INFO] Finished at: 2025-06-28T15:49:01+02:00
// [INFO] ------------------------------------------------------------------------

public class asciidoctorj {

    public static void main(String[] args) throws IOException {
        org.asciidoctor.cli.jruby.AsciidoctorInvoker.main(args);
    }
}