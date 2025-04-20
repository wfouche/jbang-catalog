///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS dev.jbang:jash:0.0.1
//DEPS org.tomlj:tomlj:1.1.1
//JAVA 21

import java.io.*;
import java.nio.file.*;
import java.util.*;
import dev.jbang.jash.Jash;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

public class python_jvm {

    private static final String textJythonApp = """
            // import org.python.util.jython;
            import org.python.util.PythonInterpreter;
            import java.util.Base64;
            
            public class __CLASSNAME__ {
            
                public static String mainScriptTextBase64 = "__MAIN_SCRIPT__";
 
                public static void main(String... args) {
                    String mainScriptFilename = "__MAIN_SCRIPT_FILENAME__";
                    String mainScript = "";
                    String pythonArgsScript = "";
                    for (String arg: args) {
                        //System.out.println("Java: " + arg);
                        if (pythonArgsScript.length() == 0) {
                            if (!arg.equals(mainScriptFilename)) {
                                pythonArgsScript += "'" + mainScriptFilename + "', ";
                            }
                        } else {
                            pythonArgsScript += ", ";
                        }
                        pythonArgsScript += "'" + arg + "'";
                    }
                    if (pythonArgsScript.length() == 0) {
                        pythonArgsScript = "'" + mainScriptFilename + "'";
                    }
                    pythonArgsScript = "import sys; sys.argv = [" + pythonArgsScript + "]";
                    {
                        byte[] decodedBytes = Base64.getDecoder().decode(mainScriptTextBase64);
                        String text = new String(decodedBytes);
                        //System.out.println("===");
                        //System.out.println(text);
                        //System.out.println("===");
                        mainScript = text;
                    }
                    //System.out.println("args --> " + pythonArgsScript);
                    {
                        // run script
                        PythonInterpreter pyInterp = new PythonInterpreter();
                        // initialize args
                        pyInterp.exec(pythonArgsScript);
                        // run script
                        //pyInterp.exec("__name__=\\"\\"");
                        pyInterp.exec(mainScript);
                    }
                    //jython.main(args);
                }
            }            
            """;

    public static final String textGraalpyApp = """
            import org.graalvm.polyglot.*;
            import java.util.Base64;
            
            public class __CLASSNAME__ {
            
                public static String mainScriptTextBase64 = "__MAIN_SCRIPT__";
            
                public static void main(String... args) {
                    String mainScriptFilename = "__MAIN_SCRIPT_FILENAME__";
                    String mainScript = "";
                    String pythonArgsScript = "";
                    for (String arg: args) {
                        if (pythonArgsScript.length() == 0) {
                            if (!arg.equals(mainScriptFilename)) {
                                pythonArgsScript += "'" + mainScriptFilename + "', ";
                            }
                        } else {
                            pythonArgsScript += ", ";
                        }
                        pythonArgsScript += "'" + arg + "'";
                    }
                    if (pythonArgsScript.length() == 0) {
                        pythonArgsScript = "'" + mainScriptFilename + "'";
                    }
                    pythonArgsScript = "import sys; sys.argv = [" + pythonArgsScript + "]";
                    //System.out.println("argsL " + pythonArgsScript);
                    {
                        byte[] decodedBytes = Base64.getDecoder().decode(mainScriptTextBase64);
                        String text = new String(decodedBytes);
                        mainScript = text;
                    }
                    {
                        // run script
                        //PythonInterpreter pyInterp = new PythonInterpreter();
            
                        // initialize args
                        //pyInterp.exec(pythonArgsScript);
            
                        // run script
                        //pyInterp.exec("__name__=\\"\\"");
                        //pyInterp.exec(mainScript);
                        try (var context = Context.newBuilder().option("python.EmulateJython", "__EMJ__").allowAllAccess(__AAA__).build()) {
                            Source sourceArgs = Source.create("python", pythonArgsScript);
                            Source sourceMain = Source.create("python", mainScript);
                            Value result = context.eval(sourceArgs);
                            result = context.eval(sourceMain);
                            //System.out.println(context.eval("python", "'Hello Python!'").asString());
                            //System.out.println(context.eval("python", "1+1"));
                        }
                     }
                }
            }
            """;

    public static void main(String[] args) throws IOException {
        String scriptFilename = args[0];
        String javaClassname = new File(scriptFilename).getName().replace(".", "_");
        String javaFilename = javaClassname + ".java";
        List<String> deps = new ArrayList<>();
        String jythonVersion = "2.7.4";
        String graalpyVersion = "";
        String graalpyAllowAllAccess = "false";
        String graalpyEmulateJython = "false";
        String javaVersion = "21";
        String ls = System.lineSeparator();

        // Parse PEP723 data
        {
            StringBuffer tomlText = new StringBuffer("");
            {
                List<String> lines = Files.readAllLines(new File(scriptFilename).toPath());
                boolean found = false;
                for (String line: lines) {
                    if (line.startsWith("# /// jbang")) {
                        found = true;
                    }
                    else if (line.startsWith("# ///")) {
                        found = false;
                        break;
                    } else if (line.startsWith("# ")) {
                        if (found) {
                            if (tomlText.length() == 0) {
                                tomlText.append(line.substring(2));
                            } else {
                                tomlText.append(ls + line.substring(2));
                            }
                        }
                    }
                }
                //System.out.println("Java TOML scanner:");
                //System.out.println(tomlText.toString());
            }
            TomlParseResult tpr = Toml.parse(tomlText.toString());
            if (tpr.isString("requires-jython")) {
                jythonVersion = tpr.getString("requires-jython").substring(2);
            }
            if (tpr.isString("requires-graalpy")) {
                graalpyVersion = tpr.getString("requires-graalpy").substring(2);
                TomlTable graalpyTable = tpr.getTable("graalpy");
                if (graalpyTable != null) {
                    Boolean allowAllAccess = graalpyTable.getBoolean("allowAllAccess");
                    if (allowAllAccess != null && allowAllAccess.equals(Boolean.TRUE) ) {
                        graalpyAllowAllAccess = "true";
                    }
                    Boolean emulateJython = graalpyTable.getBoolean("emulateJython");
                    if (emulateJython != null && emulateJython.equals(Boolean.TRUE) ) {
                        graalpyEmulateJython = "true";
                    }
                }
            }
            if (tpr.isString("requires-java")) {
                javaVersion = tpr.getString("requires-java").substring(2);
            }
            for (Object e : tpr.getArrayOrEmpty("dependencies").toList()) {
                String dep = (String) e;
                deps.add(dep);
            }
        }

        String dep = "org.python:jython-standalone:" + jythonVersion;
        if (graalpyVersion.length() > 0) {
            dep = "org.graalvm.python:jbang:" + graalpyVersion;
        }
        deps.add(dep);

        byte[] data = Files.readAllBytes(Paths.get(scriptFilename));
        String scriptFileTextB64 = Base64.getEncoder().encodeToString(data);

        try (BufferedWriter jf = new BufferedWriter(new FileWriter(javaFilename))) {
            jf.write("///usr/bin/env jbang \"$0\" \"$@\" ; exit $?" + ls + ls);
            jf.write("// spotless:off" + ls);
            for (String dependency : deps) {
                jf.write("//DEPS " + dependency + ls);
            }
            jf.write("//JAVA " + javaVersion + ls);
            if (graalpyVersion.length() > 0) {
                jf.write("//RUNTIME_OPTIONS -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -Dpolyglot.engine.WarnInterpreterOnly=false" + ls);
            }
            jf.write("// spotless:on" + ls + ls);
            String text = textJythonApp;
            if (graalpyVersion.length() > 0) {
                text = textGraalpyApp;
                text = text.replace("__EMJ__", graalpyEmulateJython);
                text = text.replace("__AAA__", graalpyAllowAllAccess);
            }
            String jtext = text.replace("__CLASSNAME__", javaClassname)
                               .replace("__MAIN_SCRIPT__", scriptFileTextB64)
                               .replace("__MAIN_SCRIPT_FILENAME__", scriptFilename);
            jf.write(jtext);
        }
        // jbang run <script>_py.java param1 param2 ...
        {
            List<String> commandList = new ArrayList<String>();

            commandList.add("run");
            commandList.add(javaFilename);
            for (int i = 1; i < args.length; i++) {
                commandList.add(args[i]);
            }
            Jash.start(
                "jbang",
                commandList.toArray(new String[0]))
                    .stream()
                    .forEach(System.out::println);
        }
    }
}
