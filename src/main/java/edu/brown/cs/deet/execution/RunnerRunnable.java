package edu.brown.cs.deet.execution;

import java.io.FilePermission;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.Map;
import java.util.PropertyPermission;

public class RunnerRunnable implements Runnable {

  private Runner runner;
  private String solutionPath;
  private Collection<String> inputs;
  private Map<String, String> runOutputs;

  public RunnerRunnable(String solutionPath, Collection<String> inputs,
    Runner runner) {
    this.runner = runner;
    this.solutionPath = solutionPath;
    this.inputs = inputs;
  }

  @Override
  public void run() {
    CodeSource nullSource;
    try {
      nullSource =
        new CodeSource(
          new URL(
            "file:/Users/Daniel/.m2/repository/org/python/jython-standalone/2.7.1b3/jython-standalone-2.7.1b3.pom"),
          (CodeSigner[]) null);
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }
    // SecurityManager sm = new SecurityManager();
    // System.setSecurityManager(sm);
    PermissionCollection perms = new Permissions();
    perms.add(new RuntimePermission("exitVM.1"));
    perms.add(new RuntimePermission("createClassLoader"));
    perms.add(new RuntimePermission("getProtectionDomain"));
    perms.add(new FilePermission("${user.dir}/*", "read"));
    perms.add(new PropertyPermission("java.vm.name", "read"));
    perms.add(new PropertyPermission("java.vm.vendor", "read"));
    perms.add(new PropertyPermission("os.name", "read"));
    perms.add(new PropertyPermission("os.arch", "read"));
    perms.add(new PropertyPermission("user.dir", "read"));
    perms.add(new PropertyPermission("line.seperator", "read"));
    ProtectionDomain domain = new ProtectionDomain(nullSource, perms);
    AccessControlContext safeContext =
      new AccessControlContext(new ProtectionDomain[] { domain });

    AccessController.doPrivileged(new PrivilegedAction() {
      @Override
      public Object run() {
        runOutputs = runner.run(solutionPath, inputs);
        notify();
        return null;
      }
    }, safeContext);
  }

  public Map<String, String> getRunOutputs() {
    return runOutputs;
  }

}
