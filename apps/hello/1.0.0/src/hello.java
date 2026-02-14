///usr/bin/env jbang "$0" "$@" ; exit $?

public class hello {

    public static void main(String... args) {
        System.out.println("Hello World!");
        System.out.println(System.getProperty("jbang.app.version"));
    }

}
