///usr/bin/env jbang "$0" "$@" ; exit $?
        
public class Command {
        
    private static String appName = "command";
    private static String appVersion = "1/2025-04-01T19:19:41";
        
    private static void displayAppInfo() {
        String version = appVersion;
        if (appVersion.contains("JBANG_SNAPSHOT_ID")) {
            version = "0";
        } 
        System.out.println(appName + "/" + version);
    }   

    public static void main(String[] args) {
	displayAppInfo();

	// ... rest of the program
    }

}
