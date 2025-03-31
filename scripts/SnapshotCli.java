///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS com.google.code.gson:gson:2.12.1

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Arrays;
import java.io.IOException;
import java.io.FileReader;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class SnapshotCli {

    private static String appName = "snapshot-cli";
    private static String appVersion = "__JBANG_SNAPSHOT_ID__/__JBANG_SNAPSHOT_TIMESTAMP__";

    private static void displayAppInfo() {
        String version = appVersion;
        if (appVersion.contains("JBANG_SNAPSHOT_ID")) {
            version = "0";
        }
        System.out.println(appName + "/" + version);
    }

    private static String getScriptname(String alias) {
        String catalog = "jbang-catalog.json";
        System.out.println("\nJBang catalog   : " + catalog);
        System.out.println("Script alias    : " + alias);
        String scriptRef = null;
        try {
            FileReader reader = new FileReader(catalog);
            Gson gson = new Gson();
            JsonObject jb = gson.fromJson(reader, JsonObject.class);
            scriptRef =  jb.getAsJsonObject("aliases").getAsJsonObject(alias).get("script-ref").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scriptRef;
    }

    private static final String indexFileText = "{\n    \"description\": \"__DESC__\",\n    \"timestamp\": \"__TIMESTAMP__\"\n}\n";

    public static void sha1HashFile(String filepath, String outputFilepath) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] fileBytes = Files.readAllBytes(Paths.get(filepath));
            sha1.update(fileBytes);
            String hashValue = bytesToHex(sha1.digest());
            try (BufferedWriter outF = new BufferedWriter(new FileWriter(outputFilepath))) {
                outF.write(hashValue);
            }
            System.out.println("    " + filepath + " sha1 " + outputFilepath);
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found at " + filepath);
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static List<String> getSources(String scriptFilename) {
        List<String> sources = new LinkedList<>();
        try (BufferedReader file = new BufferedReader(new FileReader(scriptFilename))) {
            String line;
            String token = "//SOURCES ";
            while ((line = file.readLine()) != null) {
                if (line.length() > token.length() && line.startsWith(token)) {
                    sources.addAll(Arrays.asList(line.split("\\s+")).subList(1, line.split("\\s+").length));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sources;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        displayAppInfo();
        String mainScriptFilename = getScriptname(args[0]);
        String srcDir = new File(mainScriptFilename).getParent();
        String description = args[1];
        String dateTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
        System.out.println("Source script   : " + mainScriptFilename);
        System.out.println("Description     : " + description);

        if (description.equals("--")) {
            System.out.println("\nExiting without snapshotting file " + mainScriptFilename);
            System.exit(0);
        }
        String mainSnapshotDirname = mainScriptFilename.substring(0, mainScriptFilename.lastIndexOf('.'));

        // Create the snapshot folder if required
        File snapshotDir = new File(mainSnapshotDirname);
        if (!snapshotDir.isDirectory()) {
            snapshotDir.mkdir();
        }

        // Calculate the next snapshotId to be used
        File[] dirList = snapshotDir.listFiles();
        Set<Integer> intSet = new HashSet<>();
        if (dirList != null) {
            for (File dirname : dirList) {
                intSet.add(Integer.parseInt(dirname.getName()));
            }
        }
        int snapshotId = intSet.isEmpty() ? 1 : Collections.max(intSet) + 1;

        // Create the snapshot folder
        String destDir = mainSnapshotDirname + "/" + snapshotId;
        System.out.println("\nSnapshot folder:");
        System.out.println("   " + destDir);
        new File(destDir).mkdir();

        // Determine source files
        List<String> srcFiles = new LinkedList<>();
        srcFiles.add(mainScriptFilename);
        boolean sourcesError = false;
        List<String> sources = getSources(mainScriptFilename);
        if (sources.size() > 0) {
            System.out.println("\n//SOURCES:");
            for (String file : sources) {
                if (file.indexOf(File.separator) != -1) {
                    sourcesError = true;
                }
                System.out.println("   " + file);
            }
        }
        if (sourcesError) {
            System.out.println("\nError: //SOURCES");
            System.out.println("   All files in sources list should be in the same folder as main script file.");
            System.out.println("   Exiting.");
            System.exit(1);
        }

        // Display sourced files
        System.out.println("\nSource files:");
        System.out.println("   " + mainScriptFilename);
        for (String file : sources) {
            String filename;
            if (srcDir != null && srcDir.length() > 0) {
                filename = Paths.get(srcDir, file).toString();
            } else {
                filename = file;
            }
            srcFiles.add(filename);
            System.out.println("   " + filename);
        }

        // Copy files
        System.out.println("\nSnapshot started:");
        for (String srcFile : srcFiles) {
            System.out.println("    " + srcFile + " copy " + destDir);
            try {
                Files.copy(Paths.get(srcFile), Paths.get(destDir, Paths.get(srcFile).getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String sha1DestFile = destDir + '/' + new File(srcFile).getName() + ".sha1";
            sha1HashFile(srcFile, sha1DestFile);
        }

        String srcFile = Paths.get(destDir, "00index.json").toString();
        String dstFile = srcFile + ".sha1";
        System.out.println("    " + srcFile + " create");
        try (BufferedWriter idxFile = new BufferedWriter(new FileWriter(srcFile))) {
            idxFile.write(indexFileText.replace("__DESC__", description).replace("__TIMESTAMP__", dateTimestamp));
        } catch (IOException e) {
            e.printStackTrace();
        }
        sha1HashFile(srcFile, dstFile);

        System.out.println("\nSnapshot files:");
        String indexFilename = "";
        File destDir_fh = new File(destDir);
        File[] destDirList = destDir_fh.listFiles();
        Arrays.sort(destDirList);
        for (File dirname : destDirList) {
            String filename = Paths.get(destDir, dirname.getName()).toString();
            if (filename.endsWith("00index.json")) {
                indexFilename = filename;
            }
            System.out.println("   " + filename);
        }

        // Display index file
        System.out.println("\nSnapshot index file:");
        System.out.println("   " + indexFilename);
        try (BufferedReader file = new BufferedReader(new FileReader(indexFilename))) {
            String line;
            while ((line = file.readLine()) != null) {
                System.out.println("   " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update snapshotted main script file, replace __JBANG_SNAPSHOT_VERSION_INFO__
        String scriptFilename = destDir + '/' + new File(mainScriptFilename).getName();
        List<String> lines = new LinkedList<>();
        try (BufferedReader file = new BufferedReader(new FileReader(scriptFilename))) {
            String line;
            boolean tokensReplaced = false;
            String token1 = "__JBANG_SNAPSHOT_ID__";
            String token2 = "__JBANG_SNAPSHOT_TIMESTAMP__";
            while ((line = file.readLine()) != null) {
                if (line.length() > token1.length() && line.contains(token1)) {
                    if (!tokensReplaced) {
                        line = line.replace(token1, snapshotId + "");
                        line = line.replace(token2, dateTimestamp);
                        tokensReplaced = true;
                    }
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFilename))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine(); // Add a newline character after each line
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        System.out.println("\nSnapshot done:");
        System.out.println(
                "   " + mainScriptFilename
                + " --> " +
                Paths.get(destDir, Paths.get(mainScriptFilename).getFileName().toString()));
    }

}
