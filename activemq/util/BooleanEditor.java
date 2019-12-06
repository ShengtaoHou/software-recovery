// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.beans.PropertyEditorSupport;

public class BooleanEditor extends PropertyEditorSupport
{
    @Override
    public String getJavaInitializationString() {
        return String.valueOf((boolean)this.getValue());
    }
    
    @Override
    public String getAsText() {
        return this.getJavaInitializationString();
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        if (text.toLowerCase().equals("true")) {
            this.setValue(Boolean.TRUE);
        }
        else {
            if (!text.toLowerCase().equals("false")) {
                throw new IllegalArgumentException(text);
            }
            this.setValue(Boolean.FALSE);
        }
    }
    
    @Override
    public String[] getTags() {
        final String[] result = { "true", "false" };
        return result;
    }
}
