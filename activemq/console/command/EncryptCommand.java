// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import java.util.List;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class EncryptCommand extends AbstractCommand
{
    protected String[] helpFile;
    StandardPBEStringEncryptor encryptor;
    String input;
    String password;
    
    public EncryptCommand() {
        this.helpFile = new String[] { "Task Usage: Main encrypt --password <password> --input <input>", "Description: Encrypts given text.", "", "Encrypt Options:", "    --password <password>      Password to be used by the encryptor.  Defaults to", "                               the value in the ACTIVEMQ_ENCRYPTION_PASSWORD env variable.", "    --input <input>            Text to be encrypted.", "    --version                  Display the version information.", "    -h,-?,--help               Display the stop broker help information.", "" };
        this.encryptor = new StandardPBEStringEncryptor();
    }
    
    @Override
    public String getName() {
        return "encrypt";
    }
    
    @Override
    public String getOneLineDescription() {
        return "Encrypts given text";
    }
    
    @Override
    protected void printHelp() {
        this.context.printHelp(this.helpFile);
    }
    
    @Override
    protected void runTask(final List<String> tokens) throws Exception {
        if (this.password == null) {
            this.password = System.getenv("ACTIVEMQ_ENCRYPTION_PASSWORD");
        }
        if (this.password == null || this.input == null) {
            this.context.printException(new IllegalArgumentException("input and password parameters are mandatory"));
            return;
        }
        this.encryptor.setPassword(this.password);
        this.context.print("Encrypted text: " + this.encryptor.encrypt(this.input));
    }
    
    @Override
    protected void handleOption(final String token, final List<String> tokens) throws Exception {
        if (token.startsWith("--input")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("input not specified"));
                return;
            }
            this.input = tokens.remove(0);
        }
        else if (token.startsWith("--password")) {
            if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
                this.context.printException(new IllegalArgumentException("password not specified"));
                return;
            }
            this.password = tokens.remove(0);
        }
        else {
            super.handleOption(token, tokens);
        }
    }
}
