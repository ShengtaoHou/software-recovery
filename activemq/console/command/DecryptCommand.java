// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import java.util.List;

public class DecryptCommand extends EncryptCommand
{
    protected String[] helpFile;
    
    public DecryptCommand() {
        this.helpFile = new String[] { "Task Usage: Main decrypt --password <password> --input <input>", "Description: Decrypts given text.", "", "Encrypt Options:", "    --password <password>      Password to be used by the encryptor.  Defaults to", "                               the value in the ACTIVEMQ_ENCRYPTION_PASSWORD env variable.", "    --input <input>            Text to be encrypted.", "    --version                  Display the version information.", "    -h,-?,--help               Display the stop broker help information.", "" };
    }
    
    @Override
    public String getName() {
        return "decrypt";
    }
    
    @Override
    public String getOneLineDescription() {
        return "Decrypts given text";
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
        try {
            this.context.print("Decrypted text: " + this.encryptor.decrypt(this.input));
        }
        catch (EncryptionOperationNotPossibleException e) {
            this.context.print("ERROR: Text cannot be decrypted, check your input and password and try again!");
        }
    }
}
