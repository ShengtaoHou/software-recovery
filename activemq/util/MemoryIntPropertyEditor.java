// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.beans.PropertyEditorSupport;

public class MemoryIntPropertyEditor extends PropertyEditorSupport
{
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        Pattern p = Pattern.compile("^\\s*(\\d+)\\s*(b)?\\s*$", 2);
        Matcher m = p.matcher(text);
        if (m.matches()) {
            this.setValue(Integer.parseInt(m.group(1)));
            return;
        }
        p = Pattern.compile("^\\s*(\\d+)\\s*k(b)?\\s*$", 2);
        m = p.matcher(text);
        if (m.matches()) {
            this.setValue(Integer.parseInt(m.group(1)) * 1024);
            return;
        }
        p = Pattern.compile("^\\s*(\\d+)\\s*m(b)?\\s*$", 2);
        m = p.matcher(text);
        if (m.matches()) {
            this.setValue(Integer.parseInt(m.group(1)) * 1024 * 1024);
            return;
        }
        p = Pattern.compile("^\\s*(\\d+)\\s*g(b)?\\s*$", 2);
        m = p.matcher(text);
        if (m.matches()) {
            this.setValue(Integer.parseInt(m.group(1)) * 1024 * 1024 * 1024);
            return;
        }
        throw new IllegalArgumentException("Could convert not to a memory size: " + text);
    }
    
    @Override
    public String getAsText() {
        final Integer value = (Integer)this.getValue();
        return (value != null) ? value.toString() : "";
    }
}
