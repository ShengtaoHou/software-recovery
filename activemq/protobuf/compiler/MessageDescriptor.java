// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MessageDescriptor implements TypeDescriptor
{
    private String name;
    private ExtensionsDescriptor extensions;
    private Map<String, FieldDescriptor> fields;
    private Map<String, MessageDescriptor> messages;
    private Map<String, EnumDescriptor> enums;
    private final ProtoDescriptor protoDescriptor;
    private List<MessageDescriptor> extendsList;
    private Map<String, OptionDescriptor> options;
    private List<EnumFieldDescriptor> associatedEnumFieldDescriptors;
    private final MessageDescriptor parent;
    private MessageDescriptor baseType;
    
    public MessageDescriptor(final ProtoDescriptor protoDescriptor, final MessageDescriptor parent) {
        this.fields = new LinkedHashMap<String, FieldDescriptor>();
        this.messages = new LinkedHashMap<String, MessageDescriptor>();
        this.enums = new LinkedHashMap<String, EnumDescriptor>();
        this.extendsList = new ArrayList<MessageDescriptor>();
        this.options = new LinkedHashMap<String, OptionDescriptor>();
        this.associatedEnumFieldDescriptors = new ArrayList<EnumFieldDescriptor>();
        this.protoDescriptor = protoDescriptor;
        this.parent = parent;
    }
    
    public void validate(final List<String> errors) {
        final String baseName = this.getOption(this.getOptions(), "base_type", null);
        if (baseName != null) {
            if (this.baseType == null) {
                this.baseType = (MessageDescriptor)this.getType(baseName);
            }
            if (this.baseType == null) {
                this.baseType = (MessageDescriptor)this.getProtoDescriptor().getType(baseName);
            }
            if (this.baseType == null) {
                errors.add("base_type option not valid, type not found: " + baseName);
            }
            final HashSet<String> baseFieldNames = new HashSet<String>(this.baseType.getFields().keySet());
            baseFieldNames.removeAll(this.getFields().keySet());
            if (!baseFieldNames.isEmpty()) {
                for (final String fieldName : baseFieldNames) {
                    errors.add("base_type " + baseName + " field " + fieldName + " not defined in " + this.getName());
                }
            }
        }
        for (final FieldDescriptor field : this.fields.values()) {
            field.validate(errors);
        }
        for (final EnumDescriptor o : this.enums.values()) {
            o.validate(errors);
        }
        for (final MessageDescriptor o2 : this.messages.values()) {
            o2.validate(errors);
        }
    }
    
    public String getOption(final Map<String, OptionDescriptor> options, final String optionName, final String defaultValue) {
        final OptionDescriptor optionDescriptor = options.get(optionName);
        if (optionDescriptor == null) {
            return defaultValue;
        }
        return optionDescriptor.getValue();
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setExtensions(final ExtensionsDescriptor extensions) {
        this.extensions = extensions;
    }
    
    public void setExtends(final List<MessageDescriptor> extendsList) {
        this.extendsList = extendsList;
    }
    
    public List<MessageDescriptor> getExtends() {
        return this.extendsList;
    }
    
    public void setFields(final Map<String, FieldDescriptor> fields) {
        this.fields = fields;
    }
    
    public void setMessages(final Map<String, MessageDescriptor> messages) {
        this.messages = messages;
    }
    
    public void setEnums(final Map<String, EnumDescriptor> enums) {
        this.enums = enums;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getQName() {
        if (this.parent == null) {
            return this.name;
        }
        return this.parent.getQName() + "." + this.name;
    }
    
    public ExtensionsDescriptor getExtensions() {
        return this.extensions;
    }
    
    public Map<String, FieldDescriptor> getFields() {
        return this.fields;
    }
    
    public Map<String, MessageDescriptor> getMessages() {
        return this.messages;
    }
    
    public Map<String, EnumDescriptor> getEnums() {
        return this.enums;
    }
    
    public ProtoDescriptor getProtoDescriptor() {
        return this.protoDescriptor;
    }
    
    public Map<String, OptionDescriptor> getOptions() {
        return this.options;
    }
    
    public void setOptions(final Map<String, OptionDescriptor> options) {
        this.options = options;
    }
    
    public MessageDescriptor getParent() {
        return this.parent;
    }
    
    public TypeDescriptor getType(final String t) {
        for (final MessageDescriptor o : this.messages.values()) {
            if (t.equals(o.getName())) {
                return o;
            }
            if (t.startsWith(o.getName() + ".")) {
                return o.getType(t.substring(o.getName().length() + 1));
            }
        }
        for (final EnumDescriptor o2 : this.enums.values()) {
            if (t.equals(o2.getName())) {
                return o2;
            }
        }
        return null;
    }
    
    public boolean isEnum() {
        return false;
    }
    
    public MessageDescriptor getBaseType() {
        return this.baseType;
    }
    
    public void associate(final EnumFieldDescriptor desc) {
        this.associatedEnumFieldDescriptors.add(desc);
    }
    
    public List<EnumFieldDescriptor> getAssociatedEnumFieldDescriptors() {
        return this.associatedEnumFieldDescriptors;
    }
}
