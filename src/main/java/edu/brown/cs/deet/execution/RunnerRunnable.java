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
  private Exception toThrow;

  public RunnerRunnable(String solutionPath, Collection<String> inputs,
    Runner runner) {
    this.runner = runner;
    this.solutionPath = solutionPath;
    this.inputs = inputs;
  }

  @Override
  public void run() {
    // CodeSource nullSource;
    // try {
    // nullSource =
    // new CodeSource(new URL(
    // "file:/Users/Daniel/cs/cs032/deetcode/target/classes/"),
    // (CodeSigner[]) null);
    // } catch (MalformedURLException e1) {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // return;
    // }
    // PermissionCollection perms = new Permissions();
    // perms.add(new RuntimePermission("createClassLoader"));
    // perms.add(new RuntimePermission("getProtectionDomain"));
    // perms.add(new FilePermission(System.getProperty("user.dir"), "read"));
    // perms.add(new FilePermission("testdata/add_one.js", "read"));
    // perms.add(new PropertyPermission("java.vm.name", "read"));
    // perms.add(new PropertyPermission("java.vm.vendor", "read"));
    // perms.add(new PropertyPermission("os.name", "read"));
    // perms.add(new PropertyPermission("os.arch", "read"));
    // perms.add(new PropertyPermission("user.dir", "read"));
    // perms.add(new PropertyPermission("line.seperator", "read"));
    // ProtectionDomain domain = new ProtectionDomain(nullSource, perms);
    // AccessControlContext safeContext =
    // new AccessControlContext(new ProtectionDomain[] { domain });
    SecurityManager old = System.getSecurityManager();
    SecurityManager sm = new MySecurityManager();
    System.setSecurityManager(sm);
    // AccessController.doPrivileged(new PrivilegedAction() {
    // @Override
    // public Object run() {
    try {
      runOutputs = runner.run(solutionPath, inputs);
    } catch (Exception e) {
      toThrow = e;
    }
    System.setSecurityManager(old);
    // return null;
    // }
    // }, safeContext);
  }

  public Map<String, String> getRunOutputs() throws Exception {
    if (toThrow != null) {
      throw toThrow;
    }
    return runOutputs;
  }

}
