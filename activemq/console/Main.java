// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console;

import java.net.URI;
import java.net.JarURLConnection;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.io.PrintStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.io.File;

public class Main
{
    public static final String TASK_DEFAULT_CLASS = "org.apache.activemq.console.command.ShellCommand";
    private static boolean useDefExt;
    private File activeMQHome;
    private File activeMQBase;
    private ClassLoader classLoader;
    private final Set<File> extensions;
    private final Set<File> activeMQClassPath;
    
    public Main() {
        this.extensions = new LinkedHashSet<File>();
        this.activeMQClassPath = new LinkedHashSet<File>();
    }
    
    public static void main(final String[] args) {
        final File tmpdir = new File(System.getProperty("java.io.tmpdir"));
        if (!tmpdir.exists()) {
            tmpdir.mkdirs();
        }
        final Main app = new Main();
        final List<String> tokens = new LinkedList<String>(Arrays.asList(args));
        app.parseExtensions(tokens);
        final File confDir = app.getActiveMQConfig();
        app.addClassPath(confDir);
        if (Main.useDefExt && app.canUseExtdir()) {
            final boolean baseIsHome = app.getActiveMQBase().equals(app.getActiveMQHome());
            final File baseLibDir = new File(app.getActiveMQBase(), "lib");
            final File homeLibDir = new File(app.getActiveMQHome(), "lib");
            if (!baseIsHome) {
                app.addExtensionDirectory(baseLibDir);
            }
            app.addExtensionDirectory(homeLibDir);
            if (!baseIsHome) {
                app.addExtensionDirectory(new File(baseLibDir, "camel"));
                app.addExtensionDirectory(new File(baseLibDir, "optional"));
                app.addExtensionDirectory(new File(baseLibDir, "web"));
                app.addExtensionDirectory(new File(baseLibDir, "extra"));
            }
            app.addExtensionDirectory(new File(homeLibDir, "camel"));
            app.addExtensionDirectory(new File(homeLibDir, "optional"));
            app.addExtensionDirectory(new File(homeLibDir, "web"));
            app.addExtensionDirectory(new File(homeLibDir, "extra"));
        }
        app.addClassPathList(System.getProperty("activemq.classpath"));
        try {
            app.runTaskClass(tokens);
            System.exit(0);
        }
        catch (ClassNotFoundException e) {
            System.out.println("Could not load class: " + e.getMessage());
            try {
                final ClassLoader cl = app.getClassLoader();
                if (cl != null) {
                    System.out.println("Class loader setup: ");
                    printClassLoaderTree(cl);
                }
            }
            catch (MalformedURLException ex) {}
            System.exit(1);
        }
        catch (Throwable e2) {
            System.out.println("Failed to execute main task. Reason: " + e2);
            System.exit(1);
        }
    }
    
    private static int printClassLoaderTree(final ClassLoader cl) {
        int depth = 0;
        if (cl.getParent() != null) {
            depth = printClassLoaderTree(cl.getParent()) + 1;
        }
        final StringBuffer indent = new StringBuffer();
        for (int i = 0; i < depth; ++i) {
            indent.append("  ");
        }
        if (cl instanceof URLClassLoader) {
            final URLClassLoader ucl = (URLClassLoader)cl;
            System.out.println((Object)indent + cl.getClass().getName() + " {");
            final URL[] urls = ucl.getURLs();
            for (int j = 0; j < urls.length; ++j) {
                System.out.println((Object)indent + "  " + urls[j]);
            }
            System.out.println((Object)indent + "}");
        }
        else {
            System.out.println((Object)indent + cl.getClass().getName());
        }
        return depth;
    }
    
    public void parseExtensions(final List<String> tokens) {
        if (tokens.isEmpty()) {
            return;
        }
        int count = tokens.size();
        int i = 0;
        while (i < count) {
            final String token = tokens.get(i);
            if (token.equals("--extdir")) {
                --count;
                tokens.remove(i);
                if (i >= count || tokens.get(i).startsWith("-")) {
                    System.out.println("Extension directory not specified.");
                    System.out.println("Ignoring extension directory option.");
                }
                else {
                    --count;
                    final File extDir = new File(tokens.remove(i));
                    if (!this.canUseExtdir()) {
                        System.out.println("Extension directory feature not available due to the system classpath being able to load: org.apache.activemq.console.command.ShellCommand");
                        System.out.println("Ignoring extension directory option.");
                    }
                    else if (!extDir.isDirectory()) {
                        System.out.println("Extension directory specified is not valid directory: " + extDir);
                        System.out.println("Ignoring extension directory option.");
                    }
                    else {
                        this.addExtensionDirectory(extDir);
                    }
                }
            }
            else if (token.equals("--noDefExt")) {
                --count;
                tokens.remove(i);
                Main.useDefExt = false;
            }
            else {
                ++i;
            }
        }
    }
    
    public void runTaskClass(final List<String> tokens) throws Throwable {
        StringBuilder buffer = new StringBuilder();
        buffer.append(System.getProperty("java.vendor"));
        buffer.append(" ");
        buffer.append(System.getProperty("java.version"));
        buffer.append(" ");
        buffer.append(System.getProperty("java.home"));
        System.out.println("Java Runtime: " + buffer.toString());
        buffer = new StringBuilder();
        buffer.append("current=");
        buffer.append(Runtime.getRuntime().totalMemory() / 1024L);
        buffer.append("k  free=");
        buffer.append(Runtime.getRuntime().freeMemory() / 1024L);
        buffer.append("k  max=");
        buffer.append(Runtime.getRuntime().maxMemory() / 1024L);
        buffer.append("k");
        System.out.println("  Heap sizes: " + buffer.toString());
        final List<?> jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        buffer = new StringBuilder();
        for (final Object arg : jvmArgs) {
            buffer.append(" ").append(arg);
        }
        System.out.println("    JVM args:" + buffer.toString());
        System.out.println("Extensions classpath:\n  " + this.getExtensionDirForLogging());
        System.out.println("ACTIVEMQ_HOME: " + this.getActiveMQHome());
        System.out.println("ACTIVEMQ_BASE: " + this.getActiveMQBase());
        System.out.println("ACTIVEMQ_CONF: " + this.getActiveMQConfig());
        System.out.println("ACTIVEMQ_DATA: " + this.getActiveMQDataDir());
        final ClassLoader cl = this.getClassLoader();
        Thread.currentThread().setContextClassLoader(cl);
        try {
            final String[] args = tokens.toArray(new String[tokens.size()]);
            final Class<?> task = cl.loadClass("org.apache.activemq.console.command.ShellCommand");
            final Method runTask = task.getMethod("main", String[].class, InputStream.class, PrintStream.class);
            runTask.invoke(task.newInstance(), args, System.in, System.out);
        }
        catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
    
    public void addExtensionDirectory(final File directory) {
        this.extensions.add(directory);
    }
    
    public void addClassPathList(final String fileList) {
        if (fileList != null && fileList.length() > 0) {
            final StringTokenizer tokenizer = new StringTokenizer(fileList, ";");
            while (tokenizer.hasMoreTokens()) {
                this.addClassPath(new File(tokenizer.nextToken()));
            }
        }
    }
    
    public void addClassPath(final File classpath) {
        this.activeMQClassPath.add(classpath);
    }
    
    public boolean canUseExtdir() {
        try {
            Main.class.getClassLoader().loadClass("org.apache.activemq.console.command.ShellCommand");
            return false;
        }
        catch (ClassNotFoundException e) {
            return true;
        }
    }
    
    public ClassLoader getClassLoader() throws MalformedURLException {
        if (this.classLoader == null) {
            this.classLoader = Main.class.getClassLoader();
            if (!this.extensions.isEmpty() || !this.activeMQClassPath.isEmpty()) {
                final ArrayList<URL> urls = new ArrayList<URL>();
                for (final File dir : this.activeMQClassPath) {
                    urls.add(dir.toURI().toURL());
                }
                for (final File dir : this.extensions) {
                    if (dir.isDirectory()) {
                        final File[] files = dir.listFiles();
                        if (files == null) {
                            continue;
                        }
                        Arrays.sort(files, new Comparator<File>() {
                            @Override
                            public int compare(final File f1, final File f2) {
                                return f1.getName().compareTo(f2.getName());
                            }
                        });
                        for (int j = 0; j < files.length; ++j) {
                            if (files[j].getName().endsWith(".zip") || files[j].getName().endsWith(".jar")) {
                                urls.add(files[j].toURI().toURL());
                            }
                        }
                    }
                }
                final URL[] u = new URL[urls.size()];
                urls.toArray(u);
                this.classLoader = new URLClassLoader(u, this.classLoader);
            }
            Thread.currentThread().setContextClassLoader(this.classLoader);
        }
        return this.classLoader;
    }
    
    public void setActiveMQHome(final File activeMQHome) {
        this.activeMQHome = activeMQHome;
    }
    
    public File getActiveMQHome() {
        if (this.activeMQHome == null) {
            if (System.getProperty("activemq.home") != null) {
                this.activeMQHome = new File(System.getProperty("activemq.home"));
            }
            if (this.activeMQHome == null) {
                URL url = Main.class.getClassLoader().getResource("org/apache/activemq/console/Main.class");
                if (url != null) {
                    try {
                        final JarURLConnection jarConnection = (JarURLConnection)url.openConnection();
                        url = jarConnection.getJarFileURL();
                        final URI baseURI = new URI(url.toString()).resolve("..");
                        this.activeMQHome = new File(baseURI).getCanonicalFile();
                        System.setProperty("activemq.home", this.activeMQHome.getAbsolutePath());
                    }
                    catch (Exception ex) {}
                }
            }
            if (this.activeMQHome == null) {
                this.activeMQHome = new File("../.");
                System.setProperty("activemq.home", this.activeMQHome.getAbsolutePath());
            }
        }
        return this.activeMQHome;
    }
    
    public File getActiveMQBase() {
        if (this.activeMQBase == null) {
            if (System.getProperty("activemq.base") != null) {
                this.activeMQBase = new File(System.getProperty("activemq.base"));
            }
            if (this.activeMQBase == null) {
                this.activeMQBase = this.getActiveMQHome();
                System.setProperty("activemq.base", this.activeMQBase.getAbsolutePath());
            }
        }
        return this.activeMQBase;
    }
    
    public File getActiveMQConfig() {
        File activeMQConfig = null;
        if (System.getProperty("activemq.conf") != null) {
            activeMQConfig = new File(System.getProperty("activemq.conf"));
        }
        else {
            activeMQConfig = new File(this.getActiveMQBase() + "/conf");
            System.setProperty("activemq.conf", activeMQConfig.getAbsolutePath());
        }
        return activeMQConfig;
    }
    
    public File getActiveMQDataDir() {
        File activeMQDataDir = null;
        if (System.getProperty("activemq.data") != null) {
            activeMQDataDir = new File(System.getProperty("activemq.data"));
        }
        else {
            activeMQDataDir = new File(this.getActiveMQBase() + "/data");
            System.setProperty("activemq.data", activeMQDataDir.getAbsolutePath());
        }
        return activeMQDataDir;
    }
    
    public String getExtensionDirForLogging() {
        final StringBuilder sb = new StringBuilder("[");
        final Iterator<File> it = this.extensions.iterator();
        while (it.hasNext()) {
            final File file = it.next();
            sb.append(file.getPath());
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    static {
        Main.useDefExt = true;
    }
}
