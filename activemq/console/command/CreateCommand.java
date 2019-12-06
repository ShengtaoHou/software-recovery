// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import java.nio.channels.WritableByteChannel;
import java.io.FileInputStream;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import java.nio.channels.FileChannel;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Attr;
import javax.xml.xpath.XPathFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.io.File;

public class CreateCommand extends AbstractCommand
{
    protected final String[] helpFile;
    protected final String DEFAULT_TARGET_ACTIVEMQ_CONF = "conf/activemq.xml";
    protected final String DEFAULT_BROKERNAME_XPATH = "/beans/broker/@brokerName";
    protected final String[] BASE_SUB_DIRS;
    protected final String BROKER_NAME_REGEX = "[$][{]brokerName[}]";
    protected String amqConf;
    protected String[][] fileWriteMap;
    protected String brokerName;
    protected File amqHome;
    protected File targetAmqBase;
    private static final String winActivemqData = "@echo off\nset ACTIVEMQ_HOME=\"${activemq.home}\"\nset ACTIVEMQ_BASE=\"${activemq.base}\"\n\nset PARAM=%1\n:getParam\nshift\nif \"%1\"==\"\" goto end\nset PARAM=%PARAM% %1\ngoto getParam\n:end\n\n%ACTIVEMQ_HOME%/bin/activemq %PARAM%";
    
    public CreateCommand() {
        this.helpFile = new String[] { "Task Usage: Main create path/to/brokerA [create-options]", "Description:  Creates a runnable broker instance in the specified path.", "", "List Options:", "    --amqconf <file path>   Path to ActiveMQ conf file that will be used in the broker instance. Default is: conf/activemq.xml", "    --version               Display the version information.", "    -h,-?,--help            Display the create broker help information.", "" };
        this.BASE_SUB_DIRS = new String[] { "bin", "conf" };
        this.amqConf = "conf/activemq.xml";
        this.fileWriteMap = new String[][] { { "winActivemq", "bin/${brokerName}.bat" }, { "unixActivemq", "bin/${brokerName}" } };
    }
    
    @Override
    public String getName() {
        return "create";
    }
    
    @Override
    public String getOneLineDescription() {
        return "Creates a runnable broker instance in the specified path.";
    }
    
    @Override
    protected void runTask(final List<String> tokens) throws Exception {
        this.context.print("Running create broker task...");
        this.amqHome = new File(System.getProperty("activemq.home"));
        for (final String token : tokens) {
            this.targetAmqBase = new File(token);
            this.brokerName = this.targetAmqBase.getName();
            Label_0191: {
                if (this.targetAmqBase.exists()) {
                    final BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
                    String resp;
                    do {
                        this.context.print("Target directory (" + this.targetAmqBase.getCanonicalPath() + ") already exists. Overwrite (y/n): ");
                        resp = console.readLine();
                        if (resp.equalsIgnoreCase("y")) {
                            break Label_0191;
                        }
                        if (resp.equalsIgnoreCase("yes")) {
                            break Label_0191;
                        }
                    } while (!resp.equalsIgnoreCase("n") && !resp.equalsIgnoreCase("no"));
                    return;
                }
            }
            this.context.print("Creating directory: " + this.targetAmqBase.getCanonicalPath());
            this.targetAmqBase.mkdirs();
            this.createSubDirs(this.targetAmqBase, this.BASE_SUB_DIRS);
            this.writeFileMapping(this.targetAmqBase, this.fileWriteMap);
            this.copyActivemqConf(this.amqHome, this.targetAmqBase, this.amqConf);
            this.copyConfDirectory(new File(this.amqHome, "conf"), new File(this.targetAmqBase, "conf"));
        }
    }
    
    @Override
    protected void handleOption(final String token, final List<String> tokens) throws Exception {
        if (token.startsWith("--amqconf")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("Attributes to amqconf not specified"));
                return;
            }
            this.amqConf = tokens.remove(0);
        }
        else {
            super.handleOption(token, tokens);
        }
    }
    
    protected void createSubDirs(final File target, final String[] subDirs) throws IOException {
        for (final String subDir : this.BASE_SUB_DIRS) {
            final File subDirFile = new File(target, subDir);
            this.context.print("Creating directory: " + subDirFile.getCanonicalPath());
            subDirFile.mkdirs();
        }
    }
    
    protected void writeFileMapping(final File targetBase, final String[][] fileWriteMapping) throws IOException {
        for (final String[] fileWrite : fileWriteMapping) {
            final File dest = new File(targetBase, this.resolveParam("[$][{]brokerName[}]", this.brokerName, fileWrite[1]));
            this.context.print("Creating new file: " + dest.getCanonicalPath());
            this.writeFile(fileWrite[0], dest);
        }
    }
    
    protected void copyActivemqConf(final File srcBase, final File targetBase, final String activemqConf) throws IOException, ParserConfigurationException, SAXException, TransformerException, XPathExpressionException {
        final File src = new File(srcBase, activemqConf);
        if (!src.exists()) {
            throw new FileNotFoundException("File: " + src.getCanonicalPath() + " not found.");
        }
        final File dest = new File(targetBase, "conf/activemq.xml");
        this.context.print("Copying from: " + src.getCanonicalPath() + "\n          to: " + dest.getCanonicalPath());
        final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Element docElem = builder.parse(src).getDocumentElement();
        final XPath xpath = XPathFactory.newInstance().newXPath();
        final Attr brokerNameAttr = (Attr)xpath.evaluate("/beans/broker/@brokerName", docElem, XPathConstants.NODE);
        brokerNameAttr.setValue(this.brokerName);
        this.writeToFile(new DOMSource(docElem), dest);
    }
    
    @Override
    protected void printHelp() {
        this.context.printHelp(this.helpFile);
    }
    
    private void writeFile(final String typeName, final File dest) throws IOException {
        String data;
        if (typeName.equals("winActivemq")) {
            data = "@echo off\nset ACTIVEMQ_HOME=\"${activemq.home}\"\nset ACTIVEMQ_BASE=\"${activemq.base}\"\n\nset PARAM=%1\n:getParam\nshift\nif \"%1\"==\"\" goto end\nset PARAM=%PARAM% %1\ngoto getParam\n:end\n\n%ACTIVEMQ_HOME%/bin/activemq %PARAM%";
            data = this.resolveParam("[$][{]activemq.home[}]", this.amqHome.getCanonicalPath().replaceAll("[\\\\]", "/"), data);
            data = this.resolveParam("[$][{]activemq.base[}]", this.targetAmqBase.getCanonicalPath().replaceAll("[\\\\]", "/"), data);
        }
        else {
            if (!typeName.equals("unixActivemq")) {
                throw new IllegalStateException("Unknown file type: " + typeName);
            }
            data = this.getUnixActivemqData();
            data = this.resolveParam("[$][{]activemq.home[}]", this.amqHome.getCanonicalPath().replaceAll("[\\\\]", "/"), data);
            data = this.resolveParam("[$][{]activemq.base[}]", this.targetAmqBase.getCanonicalPath().replaceAll("[\\\\]", "/"), data);
        }
        final ByteBuffer buf = ByteBuffer.allocate(data.length());
        buf.put(data.getBytes());
        buf.flip();
        final FileChannel destinationChannel = new FileOutputStream(dest).getChannel();
        destinationChannel.write(buf);
        destinationChannel.close();
        dest.setExecutable(true);
        dest.setReadable(true);
        dest.setWritable(true);
    }
    
    private void writeToFile(final Source src, final File file) throws TransformerException {
        final TransformerFactory tFactory = TransformerFactory.newInstance();
        final Transformer fileTransformer = tFactory.newTransformer();
        final Result res = new StreamResult(file);
        fileTransformer.transform(src, res);
    }
    
    private void copyFile(final File from, final File dest) throws IOException {
        if (!from.exists()) {
            return;
        }
        final FileChannel sourceChannel = new FileInputStream(from).getChannel();
        final FileChannel destinationChannel = new FileOutputStream(dest).getChannel();
        sourceChannel.transferTo(0L, sourceChannel.size(), destinationChannel);
        sourceChannel.close();
        destinationChannel.close();
    }
    
    private void copyConfDirectory(final File from, final File dest) throws IOException {
        if (from.isDirectory()) {
            final String[] list;
            final String[] files = list = from.list();
            for (final String file : list) {
                final File srcFile = new File(from, file);
                if (srcFile.isFile() && !srcFile.getName().equals("activemq.xml")) {
                    final File destFile = new File(dest, file);
                    this.context.print("Copying from: " + srcFile.getCanonicalPath() + "\n          to: " + destFile.getCanonicalPath());
                    this.copyFile(srcFile, destFile);
                }
            }
            return;
        }
        throw new IOException(from + " is not a directory");
    }
    
    private String resolveParam(final String paramName, final String paramValue, final String target) {
        return target.replaceAll(paramName, paramValue);
    }
    
    private String getUnixActivemqData() {
        final StringBuffer res = new StringBuffer();
        res.append("## Figure out the ACTIVEMQ_BASE from the directory this script was run from\n");
        res.append("PRG=\"$0\"\n");
        res.append("progname=`basename \"$0\"`\n");
        res.append("saveddir=`pwd`\n");
        res.append("# need this for relative symlinks\n");
        res.append("dirname_prg=`dirname \"$PRG\"`\n");
        res.append("cd \"$dirname_prg\"\n");
        res.append("while [ -h \"$PRG\" ] ; do\n");
        res.append("  ls=`ls -ld \"$PRG\"`\n");
        res.append("  link=`expr \"$ls\" : '.*-> \\(.*\\)$'`\n");
        res.append("  if expr \"$link\" : '.*/.*' > /dev/null; then\n");
        res.append("    PRG=\"$link\"\n");
        res.append("  else\n");
        res.append("    PRG=`dirname \"$PRG\"`\"/$link\"\n");
        res.append("  fi\n");
        res.append("done\n");
        res.append("ACTIVEMQ_BASE=`dirname \"$PRG\"`/..\n");
        res.append("cd \"$saveddir\"\n\n");
        res.append("ACTIVEMQ_BASE=`cd \"$ACTIVEMQ_BASE\" && pwd`\n\n");
        res.append("## Enable remote debugging\n");
        res.append("#export ACTIVEMQ_DEBUG_OPTS=\"-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005\"\n\n");
        res.append("## Add system properties for this instance here (if needed), e.g\n");
        res.append("#export ACTIVEMQ_OPTS_MEMORY=\"-Xms256M -Xmx1G\"\n");
        res.append("#export ACTIVEMQ_OPTS=\"$ACTIVEMQ_OPTS_MEMORY -Dorg.apache.activemq.UseDedicatedTaskRunner=true -Djava.util.logging.config.file=logging.properties\"\n\n");
        res.append("export ACTIVEMQ_HOME=${activemq.home}\n");
        res.append("export ACTIVEMQ_BASE=$ACTIVEMQ_BASE\n\n");
        res.append("${ACTIVEMQ_HOME}/bin/activemq \"$@\"");
        return res.toString();
    }
}
