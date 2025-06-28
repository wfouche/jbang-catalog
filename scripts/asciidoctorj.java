///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.asciidoctor:asciidoctorj:3.0.0
//DEPS org.asciidoctor:asciidoctorj-api:3.0.0
//DEPS org.asciidoctor:asciidoctorj-cli:3.0.0
//DEPS org.asciidoctor:asciidoctorj-epub3:2.1.3
//DEPS org.asciidoctor:asciidoctorj-diagram:3.0.1
//DEPS org.asciidoctor:asciidoctorj-diagram-plantuml:1.2025.3
//DEPS org.asciidoctor:asciidoctorj-pdf:2.3.19
//DEPS org.asciidoctor:asciidoctorj-revealjs:5.1.0
//DEPS com.beust:jcommander:1.82
//DEPS org.jruby:jruby-complete:9.4.13.0

import java.io.IOException;

public class asciidoctorj {

    public static void main(String[] args) throws IOException {
        org.asciidoctor.cli.jruby.AsciidoctorInvoker.main(args);
    }
}