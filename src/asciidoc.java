// spotless:off
//DEPS org.asciidoctor:asciidoctorj:3.0.0
//DEPS org.asciidoctor:asciidoctorj-diagram:3.0.1
//DEPS org.asciidoctor:asciidoctorj-diagram-plantuml:1.2025.3
// spotless:on

import java.io.File;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;

public class asciidoc {

    public static void main(String[] args) {
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();
        asciidoctor.requireLibrary("asciidoctor-diagram");
        asciidoctor.convertFile(
                new File(args[0]), Options.builder().toFile(true).safe(SafeMode.UNSAFE).build());
    }
}
