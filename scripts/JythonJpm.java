
///usr/bin/env jbang "$0" "$@" ; exit $?

import java.io.File;
import java.lang.Runtime;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class JythonJpm
{

    public static void main(String[] args) throws IOException, InterruptedException  {
      // Path of the specific directory 
      String directoryPath = "deps";
      String fileSeparator = File.separator;
      String pathSeparator = File.pathSeparator;

      
      // Using File class create an object for specific directory
      File directory = new File(directoryPath);
      
      // Using listFiles method we get all the files of a directory 
      // return type of listFiles is array
      File[] files = directory.listFiles();
      
      // Print name of the all files present in that path
      String py_path = "";
      String jython_jar = "";
      if (files != null) {
        for (File file : files) {
          if (file.getName().startsWith("jython-standalone")) {
            jython_jar = "deps" + fileSeparator + file.getName();
          } else {
            //System.out.println(file.getName());
            if (py_path.length() > 0) {
              py_path += pathSeparator;
            }
            py_path += "deps" + fileSeparator + file.getName();
          }
        }
        py_path = "-Dpython.path=" + py_path;
      }
      //System.out.println(py_path);
      String[] cmdArray = new String[4+args.length];
      cmdArray[0] = "java";
      cmdArray[1] = py_path;
      cmdArray[2] = "-jar";
      cmdArray[3] = jython_jar;
      int idx = 3;
      for (String val : args) {	
        idx += 1;
        cmdArray[idx] = val;
      }
      for (String v : cmdArray) {
        System.out.println("  " + v);
      }
      var process = Runtime.getRuntime().exec(cmdArray);
      BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
      String line = null;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
      reader.close();
      process.waitFor();
    }
}
