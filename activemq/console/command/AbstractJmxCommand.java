// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import java.lang.management.ManagementFactory;
import java.io.IOException;
import java.util.Map;
import javax.management.remote.JMXConnectorFactory;
import java.util.HashMap;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.List;
import java.net.URLClassLoader;
import java.net.URL;
import java.io.File;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

public abstract class AbstractJmxCommand extends AbstractCommand
{
    public static String DEFAULT_JMX_URL;
    private static String jmxUser;
    private static String jmxPassword;
    private static final String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";
    private JMXServiceURL jmxServiceUrl;
    private boolean jmxUseLocal;
    private JMXConnector jmxConnector;
    private MBeanServerConnection jmxConnection;
    
    protected JMXServiceURL getJmxServiceUrl() {
        return this.jmxServiceUrl;
    }
    
    public static String getJVM() {
        return System.getProperty("java.vm.specification.vendor");
    }
    
    public static boolean isSunJVM() {
        return getJVM().equals("Sun Microsystems Inc.") || getJVM().startsWith("Oracle");
    }
    
    protected String findJMXUrlByProcessId(final int pid) {
        if (isSunJVM()) {
            try {
                final String javaHome = System.getProperty("java.home");
                final String tools = javaHome + File.separator + ".." + File.separator + "lib" + File.separator + "tools.jar";
                final URLClassLoader loader = new URLClassLoader(new URL[] { new File(tools).toURI().toURL() });
                final Class virtualMachine = Class.forName("com.sun.tools.attach.VirtualMachine", true, loader);
                final Class virtualMachineDescriptor = Class.forName("com.sun.tools.attach.VirtualMachineDescriptor", true, loader);
                final Method getVMList = virtualMachine.getMethod("list", (Class[])null);
                final Method attachToVM = virtualMachine.getMethod("attach", String.class);
                final Method getAgentProperties = virtualMachine.getMethod("getAgentProperties", (Class[])null);
                final Method getVMId = virtualMachineDescriptor.getMethod("id", (Class[])null);
                final List allVMs = (List)getVMList.invoke(null, (Object[])null);
                for (final Object vmInstance : allVMs) {
                    final String id = (String)getVMId.invoke(vmInstance, (Object[])null);
                    if (id.equals(Integer.toString(pid))) {
                        final Object vm = attachToVM.invoke(null, id);
                        final Properties agentProperties = (Properties)getAgentProperties.invoke(vm, (Object[])null);
                        final String connectorAddress = agentProperties.getProperty("com.sun.management.jmxremote.localConnectorAddress");
                        if (connectorAddress != null) {
                            return connectorAddress;
                        }
                        break;
                    }
                }
            }
            catch (Exception ex) {}
        }
        return null;
    }
    
    protected JMXServiceURL useJmxServiceUrl() throws MalformedURLException {
        if (this.getJmxServiceUrl() == null) {
            String jmxUrl = AbstractJmxCommand.DEFAULT_JMX_URL;
            int connectingPid = -1;
            if (isSunJVM()) {
                try {
                    final String javaHome = System.getProperty("java.home");
                    final String tools = javaHome + File.separator + ".." + File.separator + "lib" + File.separator + "tools.jar";
                    final URLClassLoader loader = new URLClassLoader(new URL[] { new File(tools).toURI().toURL() });
                    final Class virtualMachine = Class.forName("com.sun.tools.attach.VirtualMachine", true, loader);
                    final Class virtualMachineDescriptor = Class.forName("com.sun.tools.attach.VirtualMachineDescriptor", true, loader);
                    final Method getVMList = virtualMachine.getMethod("list", (Class[])null);
                    final Method attachToVM = virtualMachine.getMethod("attach", String.class);
                    final Method getAgentProperties = virtualMachine.getMethod("getAgentProperties", (Class[])null);
                    final Method getVMDescriptor = virtualMachineDescriptor.getMethod("displayName", (Class[])null);
                    final Method getVMId = virtualMachineDescriptor.getMethod("id", (Class[])null);
                    final List allVMs = (List)getVMList.invoke(null, (Object[])null);
                    for (final Object vmInstance : allVMs) {
                        final String displayName = (String)getVMDescriptor.invoke(vmInstance, (Object[])null);
                        if (displayName.contains("activemq.jar start")) {
                            final String id = (String)getVMId.invoke(vmInstance, (Object[])null);
                            final Object vm = attachToVM.invoke(null, id);
                            final Properties agentProperties = (Properties)getAgentProperties.invoke(vm, (Object[])null);
                            final String connectorAddress = agentProperties.getProperty("com.sun.management.jmxremote.localConnectorAddress");
                            if (connectorAddress != null) {
                                jmxUrl = connectorAddress;
                                connectingPid = Integer.parseInt(id);
                                this.context.print("useJmxServiceUrl Found JMS Url: " + jmxUrl);
                                break;
                            }
                            continue;
                        }
                    }
                }
                catch (Exception ex) {}
            }
            if (connectingPid != -1) {
                this.context.print("Connecting to pid: " + connectingPid);
            }
            else {
                this.context.print("Connecting to JMX URL: " + jmxUrl);
            }
            this.setJmxServiceUrl(jmxUrl);
        }
        return this.getJmxServiceUrl();
    }
    
    protected void setJmxServiceUrl(final JMXServiceURL jmxServiceUrl) {
        this.jmxServiceUrl = jmxServiceUrl;
    }
    
    protected void setJmxServiceUrl(final String jmxServiceUrl) throws MalformedURLException {
        this.setJmxServiceUrl(new JMXServiceURL(jmxServiceUrl));
    }
    
    public String getJmxUser() {
        return AbstractJmxCommand.jmxUser;
    }
    
    public void setJmxUser(final String jmxUser) {
        AbstractJmxCommand.jmxUser = jmxUser;
    }
    
    public String getJmxPassword() {
        return AbstractJmxCommand.jmxPassword;
    }
    
    public void setJmxPassword(final String jmxPassword) {
        AbstractJmxCommand.jmxPassword = jmxPassword;
    }
    
    public boolean isJmxUseLocal() {
        return this.jmxUseLocal;
    }
    
    public void setJmxUseLocal(final boolean jmxUseLocal) {
        this.jmxUseLocal = jmxUseLocal;
    }
    
    private JMXConnector createJmxConnector() throws IOException {
        if (this.jmxConnector != null) {
            this.jmxConnector.connect();
            return this.jmxConnector;
        }
        if (AbstractJmxCommand.jmxUser != null && AbstractJmxCommand.jmxPassword != null) {
            final Map<String, Object> props = new HashMap<String, Object>();
            props.put("jmx.remote.credentials", new String[] { AbstractJmxCommand.jmxUser, AbstractJmxCommand.jmxPassword });
            this.jmxConnector = JMXConnectorFactory.connect(this.useJmxServiceUrl(), props);
        }
        else {
            this.jmxConnector = JMXConnectorFactory.connect(this.useJmxServiceUrl());
        }
        return this.jmxConnector;
    }
    
    protected void closeJmxConnection() {
        try {
            if (this.jmxConnector != null) {
                this.jmxConnector.close();
                this.jmxConnector = null;
            }
        }
        catch (IOException ex) {}
    }
    
    protected MBeanServerConnection createJmxConnection() throws IOException {
        if (this.jmxConnection == null) {
            if (this.isJmxUseLocal()) {
                this.jmxConnection = ManagementFactory.getPlatformMBeanServer();
            }
            else {
                this.jmxConnection = this.createJmxConnector().getMBeanServerConnection();
            }
        }
        return this.jmxConnection;
    }
    
    @Override
    protected void handleOption(final String token, final List<String> tokens) throws Exception {
        if (token.equals("--jmxurl")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("JMX URL not specified."));
            }
            if (this.getJmxServiceUrl() != null) {
                this.context.printException(new IllegalArgumentException("Multiple JMX URL cannot be specified."));
                tokens.clear();
            }
            final String strJmxUrl = tokens.remove(0);
            try {
                this.setJmxServiceUrl(new JMXServiceURL(strJmxUrl));
            }
            catch (MalformedURLException e) {
                this.context.printException(e);
                tokens.clear();
            }
        }
        else if (token.equals("--pid")) {
            if (isSunJVM()) {
                if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                    this.context.printException(new IllegalArgumentException("pid not specified"));
                    return;
                }
                final int pid = Integer.parseInt(tokens.remove(0));
                this.context.print("Connecting to pid: " + pid);
                final String jmxUrl = this.findJMXUrlByProcessId(pid);
                if (jmxUrl != null) {
                    if (this.getJmxServiceUrl() != null) {
                        this.context.printException(new IllegalArgumentException("JMX URL already specified."));
                        tokens.clear();
                    }
                    try {
                        this.setJmxServiceUrl(new JMXServiceURL(jmxUrl));
                    }
                    catch (MalformedURLException e2) {
                        this.context.printException(e2);
                        tokens.clear();
                    }
                }
                else {
                    this.context.printInfo("failed to resolve jmxUrl for pid:" + pid + ", using default JMX url");
                }
            }
            else {
                this.context.printInfo("--pid option is not available for this VM, using default JMX url");
            }
        }
        else if (token.equals("--jmxuser")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("JMX user not specified."));
            }
            this.setJmxUser(tokens.remove(0));
        }
        else if (token.equals("--jmxpassword")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("JMX password not specified."));
            }
            this.setJmxPassword(tokens.remove(0));
        }
        else if (token.equals("--jmxlocal")) {
            this.setJmxUseLocal(true);
        }
        else {
            super.handleOption(token, tokens);
        }
    }
    
    @Override
    public void execute(final List<String> tokens) throws Exception {
        try {
            super.execute(tokens);
        }
        finally {
            this.closeJmxConnection();
        }
    }
    
    static {
        AbstractJmxCommand.DEFAULT_JMX_URL = System.getProperty("activemq.jmx.url", "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi");
        AbstractJmxCommand.jmxUser = System.getProperty("activemq.jmx.user");
        AbstractJmxCommand.jmxPassword = System.getProperty("activemq.jmx.password");
    }
}
