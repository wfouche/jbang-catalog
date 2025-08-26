/// usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS org.tomlj:tomlj:1.1.1
//DEPS org.apache.commons:commons-lang3:3.18.0

import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;
import org.apache.commons.lang3.SystemUtils;

public class JavaCli {

  List<String> deps = new ArrayList<>();
  List<String> ropts = new ArrayList<>();
  String javaVersion = getJvmMajorVersion();
  boolean debug = false;
  StringBuilder tomlText = new StringBuilder();
  TomlParseResult tpr = null;

  static String getJvmMajorVersion() {
      String version = System.getProperty("java.version");
      if (version.startsWith("1.")) {
          version = version.substring(2);
      }
      return version.replaceAll("(\\d+).+", "$1");
  }

  void initEnvironment(String[] args) throws IOException {
    // Check that that Java 8 (1.8) or higher is used
    if (Integer.parseInt(javaVersion) < 8) {
      System.err.println("java-cli: error, Java 8 or higher is required");
      System.exit(1);
    }

    // Determine the Java script filename (if specified)
    String scriptFilename = "";
    for (String arg : args) {
      if (arg.endsWith(".java")) {
        scriptFilename = arg;
        break;
      }
    }

    // Extract TOML data as a String (if present)
    if (!scriptFilename.isEmpty()) {
      List<String> lines = Files.readAllLines(Paths.get(scriptFilename));
      boolean found = false;
      for (String line : lines) {
        if (found && !line.startsWith("// ")) {
          found = false;
          tomlText = new StringBuilder();
        }
        if (!found && line.startsWith("// ### jbang")) {
          found = true;
        } else if (found && line.startsWith("// ###")) {
          break;
        } else if (found && line.startsWith("// ")) {
          if (tomlText.length() > 0) {
            tomlText.append("\n");
          }
          tomlText.append(line.substring(3));
        }
      }
    }

    // Parse the TOML data
    if (tomlText.length() > 0) {
      tpr = Toml.parse(tomlText.toString());
    }

    // Process the TOML data
    if (tpr != null) {

      String keyName = "";
      String osFamily = "";
      String osArch = SystemUtils.OS_ARCH;
      if (SystemUtils.IS_OS_LINUX) {
        osFamily = "linux";
      } else if (SystemUtils.IS_OS_WINDOWS) {
        osFamily = "windows";
      } else if (SystemUtils.IS_OS_MAC) {
        osFamily = "macos";
      }
      //osFamily = "linux";
      //osArch = "aarch64";

      // deps
      for (Object e : tpr.getArrayOrEmpty("deps").toList()) {
        String dep = (String) e;
        deps.add(dep);
      }

      // platform.<linux,macos,windows>.<aarch64,x86_64,...>.deps
      keyName = "platform." + osFamily + "." + osArch + ".deps";
      for (Object e : tpr.getArrayOrEmpty(keyName).toList()) {
        String dep = (String) e;
        deps.add(dep);
      }


      // platform.<linux,macos,windows>.runtimeOptions
      keyName = "platform." + osFamily + ".runtimeOptions";
      for (Object e : tpr.getArrayOrEmpty(keyName).toList()) {
        String ropt = (String) e;
        ropts.add(ropt);
      }

      // runtimeOptions
      keyName = "runtimeOptions";
      for (Object e : tpr.getArrayOrEmpty(keyName).toList()) {
        String ropt = (String) e;
        ropts.add(ropt);
      }

      // debug
      if (tpr.isBoolean("debug")) {
        debug = Boolean.TRUE.equals(tpr.getBoolean("debug"));
      }
      if (debug) {
        System.err.println("[java-cli] Java version: " + javaVersion);
        System.err.println("[java-cli] OS family: " + osFamily);
        System.err.println("[java-cli] OS architecture: " + osArch);
        System.err.println("[java-cli] Deps: " + deps.toString());
        System.err.println("[java-cli] Runtime options: " + ropts.toString());
      }
    }
  }

  void runProcess(String[] args) throws IOException, InterruptedException {
    // Construct the JBang command to be executed
    List<String> cmd = new LinkedList<>();

    String ext = System.getProperty("os.name").toLowerCase().startsWith("win") ? ".cmd" : "";
    cmd.add("jbang" + ext);

    cmd.add("run");

    cmd.add("--java");
    cmd.add(javaVersion);

    for (String ropt : ropts) {
      cmd.add("--runtime-option");
      cmd.add(ropt);
    }

    for (String dep : deps) {
      cmd.add("--deps");
      cmd.add(dep);
    }

    Collections.addAll(cmd, args);

    if (false && debug) {
      System.err.println("[java-cli] " + cmd.toString());
    }

    // Execute the JBang command
    ProcessBuilder pb = new ProcessBuilder(cmd);
    pb.inheritIO();
    pb.start().waitFor();
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    JavaCli javaCli = new JavaCli();
    javaCli.initEnvironment(args);
    javaCli.runProcess(args);
  }
}
