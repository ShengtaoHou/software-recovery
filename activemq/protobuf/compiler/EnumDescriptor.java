// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class EnumDescriptor implements TypeDescriptor
{
    private String name;
    private Map<String, EnumFieldDescriptor> fields;
    private final ProtoDescriptor protoDescriptor;
    private final MessageDescriptor parent;
    private Map<String, OptionDescriptor> options;
    
    public EnumDescriptor(final ProtoDescriptor protoDescriptor, final MessageDescriptor parent) {
        this.fields = new LinkedHashMap<String, EnumFieldDescriptor>();
        this.options = new LinkedHashMap<String, OptionDescriptor>();
        this.protoDescriptor = protoDescriptor;
        this.parent = parent;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Map<String, EnumFieldDescriptor> getFields() {
        return this.fields;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setFields(final Map<String, EnumFieldDescriptor> fields) {
        this.fields = fields;
    }
    
    public ProtoDescriptor getProtoDescriptor() {
        return this.protoDescriptor;
    }
    
    private String getOption(final Map<String, OptionDescriptor> options, final String optionName, final String defaultValue) {
        final OptionDescriptor optionDescriptor = options.get(optionName);
        if (optionDescriptor == null) {
            return defaultValue;
        }
        return optionDescriptor.getValue();
    }
    
    private String constantToUCamelCase(final String name) {
        boolean upNext = true;
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (Character.isJavaIdentifierPart(c) && Character.isLetterOrDigit(c)) {
                if (upNext) {
                    c = Character.toUpperCase(c);
                    upNext = false;
                }
                else {
                    c = Character.toLowerCase(c);
                }
                sb.append(c);
            }
            else {
                upNext = true;
            }
        }
        return sb.toString();
    }
    
    public void validate(final List<String> errors) {
        final String createMessage = this.getOption(this.getOptions(), "java_create_message", null);
        if ("true".equals(createMessage)) {
            for (final EnumFieldDescriptor field : this.getFields().values()) {
                final String type = this.constantToUCamelCase(field.getName());
                TypeDescriptor typeDescriptor = null;
                if (this.parent != null) {
                    typeDescriptor = this.parent.getType(type);
                }
                if (typeDescriptor == null) {
                    typeDescriptor = this.protoDescriptor.getType(type);
                }
                if (typeDescriptor == null) {
                    errors.add("ENUM constant '" + field.getName() + "' did not find expected associated message: " + type);
                }
                else {
                    field.associate(typeDescriptor);
                    typeDescriptor.associate(field);
                }
            }
        }
    }
    
    public MessageDescriptor getParent() {
        return this.parent;
    }
    
    public String getQName() {
        if (this.parent == null) {
            return this.name;
        }
        return this.parent.getQName() + "." + this.name;
    }
    
    public boolean isEnum() {
        return true;
    }
    
    public Map<String, OptionDescriptor> getOptions() {
        return this.options;
    }
    
    public void setOptions(final Map<String, OptionDescriptor> options) {
        this.options = options;
    }
    
    public void associate(final EnumFieldDescriptor desc) {
        throw new RuntimeException("not supported.");
    }
}
