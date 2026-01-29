/// usr/bin/env jbang "$0" "$@ view" ; exit $?

public class ShowVersion {
    public static void main(String[] args) {
        // Retrieve the property set by JBang
        String jbangVersion = System.getProperty("jbang.app.version");

        if (jbangVersion != null) {
            System.out.println("JBang Version: " + jbangVersion);
        } else {
            System.out.println("Property 'jbang.app.version' is not set.");
            System.out.println(
                    "Note: This property is typically available when running via the JBang CLI.");
        }
    }
}
