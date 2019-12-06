// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FieldDescriptor
{
    public static final String STRING_TYPE;
    public static final String BOOL_TYPE;
    public static final String BYTES_TYPE;
    public static final String DOUBLE_TYPE;
    public static final String FLOAT_TYPE;
    public static final String INT32_TYPE;
    public static final String INT64_TYPE;
    public static final String UINT32_TYPE;
    public static final String UINT64_TYPE;
    public static final String SINT32_TYPE;
    public static final String SINT64_TYPE;
    public static final String FIXED32_TYPE;
    public static final String FIXED64_TYPE;
    public static final String SFIXED32_TYPE;
    public static final String SFIXED64_TYPE;
    public static final String REQUIRED_RULE;
    public static final String OPTIONAL_RULE;
    public static final String REPEATED_RULE;
    public static final Set<String> INT32_TYPES;
    public static final Set<String> INT64_TYPES;
    public static final Set<String> INTEGER_TYPES;
    public static final Set<String> NUMBER_TYPES;
    public static final Set<String> SCALAR_TYPES;
    public static final Set<String> SIGNED_TYPES;
    public static final Set<String> UNSIGNED_TYPES;
    private String name;
    private String type;
    private String rule;
    private int tag;
    private Map<String, OptionDescriptor> options;
    private TypeDescriptor typeDescriptor;
    private final MessageDescriptor parent;
    private MessageDescriptor group;
    
    public FieldDescriptor(final MessageDescriptor parent) {
        this.parent = parent;
    }
    
    public void validate(final List<String> errors) {
        if (this.group != null) {
            this.typeDescriptor = this.group;
        }
        if (!FieldDescriptor.SCALAR_TYPES.contains(this.type)) {
            if (this.typeDescriptor == null) {
                this.typeDescriptor = this.parent.getType(this.type);
            }
            if (this.typeDescriptor == null) {
                this.typeDescriptor = this.parent.getProtoDescriptor().getType(this.type);
            }
            if (this.typeDescriptor == null) {
                errors.add("Field type not found: " + this.type);
            }
        }
    }
    
    public boolean isGroup() {
        return this.group != null;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getRule() {
        return this.rule;
    }
    
    public void setRule(final String rule) {
        this.rule = rule.intern();
    }
    
    public boolean isOptional() {
        return this.rule == FieldDescriptor.OPTIONAL_RULE;
    }
    
    public boolean isRequired() {
        return this.rule == FieldDescriptor.REQUIRED_RULE;
    }
    
    public boolean isRepeated() {
        return this.rule == FieldDescriptor.REPEATED_RULE;
    }
    
    public int getTag() {
        return this.tag;
    }
    
    public void setTag(final int tag) {
        this.tag = tag;
    }
    
    public Map<String, OptionDescriptor> getOptions() {
        return this.options;
    }
    
    public void setOptions(final Map<String, OptionDescriptor> options) {
        this.options = options;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type.intern();
    }
    
    public boolean isMessageType() {
        return !FieldDescriptor.SCALAR_TYPES.contains(this.type);
    }
    
    public boolean isScalarType() {
        return FieldDescriptor.SCALAR_TYPES.contains(this.type);
    }
    
    public boolean isNumberType() {
        return FieldDescriptor.NUMBER_TYPES.contains(this.type);
    }
    
    public boolean isIntegerType() {
        return FieldDescriptor.INTEGER_TYPES.contains(this.type);
    }
    
    public boolean isInteger32Type() {
        return FieldDescriptor.INT32_TYPES.contains(this.type);
    }
    
    public boolean isInteger64Type() {
        return FieldDescriptor.INT64_TYPES.contains(this.type);
    }
    
    public boolean isStringType() {
        return this.type == FieldDescriptor.STRING_TYPE;
    }
    
    public TypeDescriptor getTypeDescriptor() {
        return this.typeDescriptor;
    }
    
    public void setTypeDescriptor(final TypeDescriptor typeDescriptor) {
        this.typeDescriptor = typeDescriptor;
    }
    
    public MessageDescriptor getGroup() {
        return this.group;
    }
    
    public void setGroup(final MessageDescriptor group) {
        this.group = group;
    }
    
    static {
        STRING_TYPE = "string".intern();
        BOOL_TYPE = "bool".intern();
        BYTES_TYPE = "bytes".intern();
        DOUBLE_TYPE = "double".intern();
        FLOAT_TYPE = "float".intern();
        INT32_TYPE = "int32".intern();
        INT64_TYPE = "int64".intern();
        UINT32_TYPE = "uint32".intern();
        UINT64_TYPE = "uint64".intern();
        SINT32_TYPE = "sint32".intern();
        SINT64_TYPE = "sint64".intern();
        FIXED32_TYPE = "fixed32".intern();
        FIXED64_TYPE = "fixed64".intern();
        SFIXED32_TYPE = "sfixed32".intern();
        SFIXED64_TYPE = "sfixed64".intern();
        REQUIRED_RULE = "required".intern();
        OPTIONAL_RULE = "optional".intern();
        REPEATED_RULE = "repeated".intern();
        INT32_TYPES = new HashSet<String>();
        INT64_TYPES = new HashSet<String>();
        INTEGER_TYPES = new HashSet<String>();
        NUMBER_TYPES = new HashSet<String>();
        SCALAR_TYPES = new HashSet<String>();
        SIGNED_TYPES = new HashSet<String>();
        UNSIGNED_TYPES = new HashSet<String>();
        FieldDescriptor.INT32_TYPES.add(FieldDescriptor.INT32_TYPE);
        FieldDescriptor.INT32_TYPES.add(FieldDescriptor.UINT32_TYPE);
        FieldDescriptor.INT32_TYPES.add(FieldDescriptor.SINT32_TYPE);
        FieldDescriptor.INT32_TYPES.add(FieldDescriptor.FIXED32_TYPE);
        FieldDescriptor.INT32_TYPES.add(FieldDescriptor.SFIXED32_TYPE);
        FieldDescriptor.INT64_TYPES.add(FieldDescriptor.INT64_TYPE);
        FieldDescriptor.INT64_TYPES.add(FieldDescriptor.UINT64_TYPE);
        FieldDescriptor.INT64_TYPES.add(FieldDescriptor.SINT64_TYPE);
        FieldDescriptor.INT64_TYPES.add(FieldDescriptor.FIXED64_TYPE);
        FieldDescriptor.INT64_TYPES.add(FieldDescriptor.SFIXED64_TYPE);
        FieldDescriptor.INTEGER_TYPES.addAll(FieldDescriptor.INT32_TYPES);
        FieldDescriptor.INTEGER_TYPES.addAll(FieldDescriptor.INT64_TYPES);
        FieldDescriptor.NUMBER_TYPES.addAll(FieldDescriptor.INTEGER_TYPES);
        FieldDescriptor.NUMBER_TYPES.add(FieldDescriptor.DOUBLE_TYPE);
        FieldDescriptor.NUMBER_TYPES.add(FieldDescriptor.FLOAT_TYPE);
        FieldDescriptor.SCALAR_TYPES.addAll(FieldDescriptor.NUMBER_TYPES);
        FieldDescriptor.SCALAR_TYPES.add(FieldDescriptor.STRING_TYPE);
        FieldDescriptor.SCALAR_TYPES.add(FieldDescriptor.BOOL_TYPE);
        FieldDescriptor.SCALAR_TYPES.add(FieldDescriptor.BYTES_TYPE);
    }
}
