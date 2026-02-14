//JAVA 17+
//REPOS central=https://repo1.maven.org/maven2,apache-snapshot=https://repository.apache.org/content/groups/snapshots/
//DEPS org.apache.camel:camel-bom:${camel.jbang.version:4.17.0}@pom
//DEPS org.apache.camel:camel-jbang-core:${camel.jbang.version:4.17.0}
//DEPS org.apache.camel.kamelets:camel-kamelets:${camel-kamelets.version:4.17.0}

package main;

import org.apache.camel.dsl.jbang.core.commands.CamelJBangMain;

/**
 * Main to run CamelJBang
 */
public class CamelJBang {

    public static void main(String... args) {
        CamelJBangMain.run(args);
    }
}
