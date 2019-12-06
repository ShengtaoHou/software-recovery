// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProtoDescriptor
{
    private String packageName;
    private Map<String, OptionDescriptor> options;
    private Map<String, MessageDescriptor> messages;
    private Map<String, EnumDescriptor> enums;
    private List<MessageDescriptor> extendsList;
    private Map<String, ServiceDescriptor> services;
    List<String> imports;
    Map<String, ProtoDescriptor> importProtoDescriptors;
    private String name;
    
    public ProtoDescriptor() {
        this.options = new LinkedHashMap<String, OptionDescriptor>();
        this.messages = new LinkedHashMap<String, MessageDescriptor>();
        this.enums = new LinkedHashMap<String, EnumDescriptor>();
        this.extendsList = new ArrayList<MessageDescriptor>();
        this.services = new LinkedHashMap<String, ServiceDescriptor>();
        this.imports = new ArrayList<String>();
        this.importProtoDescriptors = new LinkedHashMap<String, ProtoDescriptor>();
    }
    
    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }
    
    public void setOptions(final Map<String, OptionDescriptor> options) {
        this.options = options;
    }
    
    public void setMessages(final Map<String, MessageDescriptor> messages) {
        this.messages = messages;
    }
    
    public void setEnums(final Map<String, EnumDescriptor> enums) {
        this.enums = enums;
    }
    
    public void setExtends(final List<MessageDescriptor> extendsList) {
        this.extendsList = extendsList;
    }
    
    public List<MessageDescriptor> getExtends() {
        return this.extendsList;
    }
    
    public String getPackageName() {
        return this.packageName;
    }
    
    public Map<String, OptionDescriptor> getOptions() {
        return this.options;
    }
    
    public Map<String, MessageDescriptor> getMessages() {
        return this.messages;
    }
    
    public Map<String, EnumDescriptor> getEnums() {
        return this.enums;
    }
    
    public void setServices(final Map<String, ServiceDescriptor> services) {
        this.services = services;
    }
    
    public Map<String, ServiceDescriptor> getServices() {
        return this.services;
    }
    
    public void validate(final List<String> errors) {
        for (final ProtoDescriptor o : this.importProtoDescriptors.values()) {
            o.validate(errors);
        }
        for (final OptionDescriptor o2 : this.options.values()) {
            o2.validate(errors);
        }
        for (final MessageDescriptor o3 : this.messages.values()) {
            o3.validate(errors);
        }
        for (final EnumDescriptor o4 : this.enums.values()) {
            o4.validate(errors);
        }
        for (final MessageDescriptor o3 : this.extendsList) {
            o3.validate(errors);
        }
        for (final ServiceDescriptor o5 : this.services.values()) {
            o5.validate(errors);
        }
    }
    
    public List<String> getImports() {
        return this.imports;
    }
    
    public void setImports(final List<String> imports) {
        this.imports = imports;
    }
    
    public Map<String, ProtoDescriptor> getImportProtoDescriptors() {
        return this.importProtoDescriptors;
    }
    
    public void setImportProtoDescriptors(final Map<String, ProtoDescriptor> importProtoDescriptors) {
        this.importProtoDescriptors = importProtoDescriptors;
    }
    
    public TypeDescriptor getType(final String type) {
        for (final MessageDescriptor o : this.messages.values()) {
            if (type.equals(o.getName())) {
                return o;
            }
            if (type.startsWith(o.getName() + ".")) {
                return o.getType(type.substring(o.getName().length() + 1));
            }
        }
        for (final EnumDescriptor o2 : this.enums.values()) {
            if (type.equals(o2.getName())) {
                return o2;
            }
        }
        for (final ProtoDescriptor o3 : this.importProtoDescriptors.values()) {
            if (o3.getPackageName() != null && type.startsWith(o3.getPackageName() + ".")) {
                return o3.getType(type.substring(o3.getPackageName().length() + 1));
            }
        }
        for (final ProtoDescriptor o3 : this.importProtoDescriptors.values()) {
            final TypeDescriptor rc = o3.getType(type);
            if (rc != null) {
                return rc;
            }
        }
        return null;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}
