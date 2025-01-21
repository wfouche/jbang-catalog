
///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.asciidoctor:asciidoctorj:3.0.0
//DEPS org.asciidoctor:asciidoctorj-diagram:2.3.1

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;

import java.io.File;

public class asciidoc {

    public static void main(String[] args) {
        Asciidoctor asciidoctor = Asciidoctor.Factory.create(); 
        asciidoctor.requireLibrary("asciidoctor-diagram");
        asciidoctor.convertFile(                                
                new File(args[0]),
                Options.builder()                               
                        .toFile(true)
                        .safe(SafeMode.UNSAFE)
                        .build());
    }
}
