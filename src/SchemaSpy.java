///usr/bin/env jbang "$0" "$@" ; exit $?

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SchemaSpy {

    private static String getDatbaseType(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-t")) {
                return args[i + 1];
            }
        }
        throw new IllegalArgumentException("Database type not specified. Use -t <databaseType>");
    }

    private static Map<String, List<String>> setupDrivers() {
        Map<String, List<String>> drivers = new HashMap<>();
        //https://mariadb.com/kb/en/mariadb-connector-j/
        drivers.put("mariadb", List.of("org.mariadb.jdbc:mariadb-java-client:RELEASE"));
        //https://dev.mysql.com/doc/connector-j/8.0/en/
        drivers.put("mysql", List.of("mysql:mysql-connector-java:RELEASE"));
        //https://jdbc.postgresql.org/documentation/head/connect.html
        drivers.put("pgsql11", List.of("org.postgresql:postgresql:RELEASE"));
        //https://docs.oracle.com/en/database/oracle/oracle-database/19/jjdbc/JDBC-driver-connection-url-syntax.html#GUID-0A7E1701-2CEC-4608-A498-2D72AEB4013B
        drivers.put("oracle", List.of("com.oracle.database.jdbc:ojdbc10:RELEASE"));
        //https://docs.microsoft.com/en-us/sql/connect/jdbc/microsoft-jdbc-driver-for-sql-server?view=sql-server-ver15
        drivers.put("sqlserver", List.of("com.microsoft.sqlserver:mssql-jdbc:RELEASE"));
        //https://help.sap.com/viewer/0eec0d68141541d1b07893a39944924e/2.0.02/en-US/109397c2206a4ab2a5386d494f4cf75e.html
        drivers.put("sap", List.of("com.sapcloud.db.jdbc:ngdbc:RELEASE"));
        //https://www.ibm.com/docs/en/informix-servers/14.10?topic=SSGU8G_14.1.0/com.ibm.jdbc_pg.doc/ids_jdbc_501.htm
        drivers.put("informix", List.of("com.ibm.informix:jdbc:RELEASE"));
        //https://www.firebirdsql.org/file/documentation/drivers_documentation/java/3.0.7/firebird-classic-server.html
        drivers.put("firebird", List.of("org.firebirdsql.jdbc:jaybird:RELEASE"));
        drivers.put("firebirdsql", List.of("org.firebirdsql.jdbc:jaybird:RELEASE"));
        //https://hsqldb.org/doc/2.0/guide/dbproperties-chapt.html
        drivers.put("hsqldb", List.of("org.hsqldb:hsqldb:RELEASE"));
        //https://www.h2database.com/html/features.html#database_url
        drivers.put("h2", List.of("com.h2database:h2:RELEASE"));
        //https://db.apache.org/derby/docs/10.8/devguide/cdevdvlp17453.html
        drivers.put("derby", List.of("org.apache.derby:derby:RELEASE"));
        drivers.put("sqlite", List.of("org.xerial:sqlite-jdbc:RELEASE", "org.slf4j:slf4j-simple:1.7.36"));
        return drivers;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        List<String> cmd = new LinkedList<>();
        Map<String, List<String>> drivers = setupDrivers();
        String databaseType = getDatbaseType(args);
        if (!drivers.containsKey(databaseType)) {
            throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        }
        List<String> deps = drivers.get(databaseType);

        boolean windows = System.getProperty("os.name").toLowerCase().startsWith("win");
        cmd.add("jbang" + (windows ? ".cmd" : ""));
        cmd.add("run");

        //cmd.add("--java");
        //cmd.add("21");

        for (String dep: deps) {
            cmd.add("--deps");
            cmd.add(dep);
        }

        cmd.add("-m");
        cmd.add("org.schemaspy.Main");

        cmd.add("org.schemaspy:schemaspy:RELEASE");

        for (String arg : args) {
            cmd.add(arg);
        }

        System.out.println("Running command: " + String.join(" ", cmd));

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.inheritIO();
        pb.start().waitFor();
    }

}
