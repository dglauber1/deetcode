package edu.brown.cs.deet.execution;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

public class MySecurityManager extends SecurityManager {

  private boolean enabled = true;

  public void enable() {
    enabled = true;
  }

  public void disable() {
    enabled = false;
  }

  @Override
  public void checkPermission(Permission perm) {
    return;
    // if (enabled == true)
    // return;
    // throw new SecurityException();
  }

  @Override
  public void checkPermission(Permission perm, Object context) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkAccess(Thread t) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkAccess(ThreadGroup g) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkExit(int status) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkExec(String cmd) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkLink(String lib) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkRead(FileDescriptor fd) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkRead(String file) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkRead(String file, Object context) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkWrite(FileDescriptor fd) {
    // if (enabled == true)
    // return;
    throw new SecurityException();
  }

  @Override
  public void checkWrite(String file) {
    // if (enabled == true)
    // return;
    throw new SecurityException();
  }

  @Override
  public void checkDelete(String file) {
    // if (enabled == true)
    // return;
    throw new SecurityException();
  }

  @Override
  public void checkConnect(String host, int port) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkConnect(String host, int port, Object context) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkListen(int port) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkAccept(String host, int port) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkMulticast(InetAddress maddr) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkPropertiesAccess() {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkPropertyAccess(String key) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public boolean checkTopLevelWindow(Object window) {
    if (enabled == true)
      return true;
    return false;
  }

  @Override
  public void checkPrintJobAccess() {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkSystemClipboardAccess() {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkAwtEventQueueAccess() {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkPackageAccess(String pkg) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkPackageDefinition(String pkg) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkSetFactory() {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public void checkSecurityAccess(String target) {
    if (enabled == true)
      return;
    throw new SecurityException();
  }

  @Override
  public ThreadGroup getThreadGroup() {
    if (enabled == true)
      return super.getThreadGroup();
    throw new SecurityException();
  }

}
