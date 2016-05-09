package edu.brown.cs.deet.execution.java;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.sun.tools.javac.Main;

import edu.brown.cs.deet.execution.MyCompiler;

public class MyJavaCompiler implements MyCompiler {

  @Override
  public String compile(String filePath) {
    // JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    // ByteArrayOutputStream os = new ByteArrayOutputStream();
    // int compilationResult = compiler.run(null, null, os, filePath);
    StringWriter stringWriter = new StringWriter();
    PrintWriter pw = new PrintWriter(stringWriter);
    // PrintWriter pw = new PrintWriter(os);
    int errorCode =
      Main.compile(new String[] { "-classpath", "target/deetcode-1.0.jar",
        "-d", "temporary/", filePath }, pw);
    System.out.println(errorCode);
    // if (errorCode == 0) {
    // return null;
    // } else {
    return stringWriter.toString();
    // }
  }

}
