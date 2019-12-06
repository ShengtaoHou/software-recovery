// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler;

import java.util.HashSet;
import org.apache.activemq.protobuf.WireFormat;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.activemq.protobuf.compiler.parser.ParseException;
import java.io.FileNotFoundException;
import java.util.List;
import java.io.InputStream;
import org.apache.activemq.protobuf.compiler.parser.ProtoParser;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.File;

public class JavaGenerator
{
    private File out;
    private File[] path;
    private ProtoDescriptor proto;
    private String javaPackage;
    private String outerClassName;
    private PrintWriter w;
    private int indent;
    private ArrayList<String> errors;
    private boolean multipleFiles;
    private boolean deferredDecode;
    private boolean auto_clear_optional_fields;
    static final char[] HEX_TABLE;
    
    public JavaGenerator() {
        this.out = new File(".");
        this.path = new File[] { new File(".") };
        this.errors = new ArrayList<String>();
    }
    
    public static void main(String[] args) {
        final JavaGenerator generator = new JavaGenerator();
        args = CommandLineSupport.setOptions(generator, args);
        if (args.length == 0) {
            System.out.println("No proto files specified.");
        }
        for (int i = 0; i < args.length; ++i) {
            try {
                System.out.println("Compiling: " + args[i]);
                generator.compile(new File(args[i]));
            }
            catch (CompilerException e) {
                System.out.println("Protocol Buffer Compiler failed with the following error(s):");
                for (final String error : e.getErrors()) {
                    System.out.println("");
                    System.out.println(error);
                }
                System.out.println("");
                System.out.println("Compile failed.  For more details see error messages listed above.");
                return;
            }
        }
    }
    
    public void compile(final File file) throws CompilerException {
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            final ProtoParser parser = new ProtoParser(is);
            (this.proto = parser.ProtoDescriptor()).setName(file.getName());
            this.loadImports(this.proto, file.getParentFile());
            this.proto.validate(this.errors);
        }
        catch (FileNotFoundException e) {
            this.errors.add("Failed to open: " + file.getPath() + ":" + e.getMessage());
        }
        catch (ParseException e2) {
            this.errors.add("Failed to parse: " + file.getPath() + ":" + e2.getMessage());
        }
        finally {
            try {
                is.close();
            }
            catch (Throwable t) {}
        }
        if (!this.errors.isEmpty()) {
            throw new CompilerException(this.errors);
        }
        this.javaPackage = this.javaPackage(this.proto);
        this.outerClassName = this.javaClassName(this.proto);
        this.multipleFiles = this.isMultipleFilesEnabled(this.proto);
        this.deferredDecode = Boolean.parseBoolean(this.getOption(this.proto.getOptions(), "deferred_decode", "false"));
        this.auto_clear_optional_fields = Boolean.parseBoolean(this.getOption(this.proto.getOptions(), "auto_clear_optional_fields", "false"));
        if (this.multipleFiles) {
            this.generateProtoFile();
        }
        else {
            this.writeFile(this.outerClassName, new Closure() {
                public void execute() throws CompilerException {
                    JavaGenerator.this.generateProtoFile();
                }
            });
        }
        if (!this.errors.isEmpty()) {
            throw new CompilerException(this.errors);
        }
    }
    
    private void writeFile(final String className, final Closure closure) throws CompilerException {
        final PrintWriter oldWriter = this.w;
        File outputFile = this.out;
        if (this.javaPackage != null) {
            final String packagePath = this.javaPackage.replace('.', '/');
            outputFile = new File(outputFile, packagePath);
        }
        outputFile = new File(outputFile, className + ".java");
        outputFile.getParentFile().mkdirs();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);
            this.w = new PrintWriter(fos);
            closure.execute();
            this.w.flush();
        }
        catch (FileNotFoundException e) {
            this.errors.add("Failed to write to: " + outputFile.getPath() + ":" + e.getMessage());
        }
        finally {
            try {
                fos.close();
            }
            catch (Throwable t) {}
            this.w = oldWriter;
        }
    }
    
    private void loadImports(final ProtoDescriptor proto, final File protoDir) {
        final LinkedHashMap<String, ProtoDescriptor> children = new LinkedHashMap<String, ProtoDescriptor>();
        for (final String imp : proto.getImports()) {
            File file = new File(protoDir, imp);
            for (int i = 0; i < this.path.length && !file.exists(); file = new File(this.path[i], imp), ++i) {}
            if (!file.exists()) {
                this.errors.add("Cannot load import: " + imp);
            }
            FileInputStream is = null;
            try {
                is = new FileInputStream(file);
                final ProtoParser parser = new ProtoParser(is);
                final ProtoDescriptor child = parser.ProtoDescriptor();
                child.setName(file.getName());
                this.loadImports(child, file.getParentFile());
                children.put(imp, child);
            }
            catch (ParseException e) {
                this.errors.add("Failed to parse: " + file.getPath() + ":" + e.getMessage());
            }
            catch (FileNotFoundException e2) {
                this.errors.add("Failed to open: " + file.getPath() + ":" + e2.getMessage());
            }
            finally {
                try {
                    is.close();
                }
                catch (Throwable t) {}
            }
        }
        proto.setImportProtoDescriptors(children);
    }
    
    private void generateProtoFile() throws CompilerException {
        if (this.multipleFiles) {
            for (final EnumDescriptor o : this.proto.getEnums().values()) {
                final EnumDescriptor value = o;
                final String className = uCamel(o.getName());
                this.writeFile(className, new Closure() {
                    public void execute() throws CompilerException {
                        JavaGenerator.this.generateFileHeader();
                        JavaGenerator.this.generateEnum(o);
                    }
                });
            }
            for (final MessageDescriptor o2 : this.proto.getMessages().values()) {
                final MessageDescriptor value2 = o2;
                final String className = uCamel(o2.getName());
                this.writeFile(className, new Closure() {
                    public void execute() throws CompilerException {
                        JavaGenerator.this.generateFileHeader();
                        JavaGenerator.this.generateMessageBean(o2);
                    }
                });
            }
        }
        else {
            this.generateFileHeader();
            this.p("public class " + this.outerClassName + " {");
            this.indent();
            for (final EnumDescriptor enumType : this.proto.getEnums().values()) {
                this.generateEnum(enumType);
            }
            for (final MessageDescriptor m : this.proto.getMessages().values()) {
                this.generateMessageBean(m);
            }
            this.unindent();
            this.p("}");
        }
    }
    
    private void generateFileHeader() {
        this.p("//");
        this.p("// Generated by protoc, do not edit by hand.");
        this.p("//");
        if (this.javaPackage != null) {
            this.p("package " + this.javaPackage + ";");
            this.p("");
        }
    }
    
    private void generateMessageBean(final MessageDescriptor m) {
        final String className = uCamel(m.getName());
        this.p();
        String staticOption = "static ";
        if (this.multipleFiles && m.getParent() == null) {
            staticOption = "";
        }
        final String javaImplements = this.getOption(m.getOptions(), "java_implments", null);
        String implementsExpression = "";
        if (javaImplements != null) {
            implementsExpression = "implements " + javaImplements + " ";
        }
        String baseClass = "org.apache.activemq.protobuf.BaseMessage";
        if (this.deferredDecode) {
            baseClass = "org.apache.activemq.protobuf.DeferredDecodeMessage";
        }
        if (m.getBaseType() != null) {
            baseClass = this.javaType(m.getBaseType()) + "Base";
        }
        this.p(staticOption + "public final class " + className + " extends " + className + "Base<" + className + "> " + implementsExpression + "{");
        this.p();
        this.indent();
        for (final EnumDescriptor enumType : m.getEnums().values()) {
            this.generateEnum(enumType);
        }
        for (final MessageDescriptor subMessage : m.getMessages().values()) {
            this.generateMessageBean(subMessage);
        }
        for (final FieldDescriptor field : m.getFields().values()) {
            if (this.isInBaseClass(m, field)) {
                continue;
            }
            if (!field.isGroup()) {
                continue;
            }
            this.generateMessageBean(field.getGroup());
        }
        this.generateMethodAssertInitialized(m, className);
        this.generateMethodClear(m);
        this.p("public " + className + " clone() {");
        this.p("   return new " + className + "().mergeFrom(this);");
        this.p("}");
        this.p();
        this.generateMethodMergeFromBean(m, className);
        this.generateMethodSerializedSize(m);
        this.generateMethodMergeFromStream(m, className);
        this.generateMethodWriteTo(m);
        this.generateMethodParseFrom(m, className);
        this.generateMethodToString(m);
        this.generateMethodVisitor(m);
        this.generateMethodType(m, className);
        this.generateMethodEquals(m, className);
        this.unindent();
        this.p("}");
        this.p();
        this.p(staticOption + "abstract class " + className + "Base<T> extends " + baseClass + "<T> {");
        this.p();
        this.indent();
        for (final FieldDescriptor field : m.getFields().values()) {
            if (this.isInBaseClass(m, field)) {
                continue;
            }
            this.generateFieldAccessor(field);
        }
        this.unindent();
        this.p("}");
        this.p();
    }
    
    private boolean isInBaseClass(final MessageDescriptor m, final FieldDescriptor field) {
        return m.getBaseType() != null && m.getBaseType().getFields().containsKey(field.getName());
    }
    
    private void generateMethodVisitor(final MessageDescriptor m) {
        final String javaVisitor = this.getOption(m.getOptions(), "java_visitor", null);
        if (javaVisitor != null) {
            String returns = "void";
            String throwsException = null;
            final StringTokenizer st = new StringTokenizer(javaVisitor, ":");
            final String vistorClass = st.nextToken();
            if (st.hasMoreTokens()) {
                returns = st.nextToken();
            }
            if (st.hasMoreTokens()) {
                throwsException = st.nextToken();
            }
            String throwsClause = "";
            if (throwsException != null) {
                throwsClause = "throws " + throwsException + " ";
            }
            this.p("public " + returns + " visit(" + vistorClass + " visitor) " + throwsClause + "{");
            this.indent();
            if ("void".equals(returns)) {
                this.p("visitor.visit(this);");
            }
            else {
                this.p("return visitor.visit(this);");
            }
            this.unindent();
            this.p("}");
            this.p();
        }
    }
    
    private void generateMethodType(final MessageDescriptor m, final String className) {
        final String typeEnum = this.getOption(m.getOptions(), "java_type_method", null);
        if (typeEnum != null) {
            TypeDescriptor typeDescriptor = m.getType(typeEnum);
            if (typeDescriptor == null) {
                typeDescriptor = m.getProtoDescriptor().getType(typeEnum);
            }
            if (typeDescriptor == null || !typeDescriptor.isEnum()) {
                this.errors.add("The java_type_method option on the " + m.getName() + " message does not point to valid enum type");
                return;
            }
            final String constant = this.constantCase(className);
            final EnumDescriptor enumDescriptor = (EnumDescriptor)typeDescriptor;
            if (enumDescriptor.getFields().get(constant) == null) {
                this.errors.add("The java_type_method option on the " + m.getName() + " message does not points to the " + typeEnum + " enum but it does not have an entry for " + constant);
            }
            final String type = this.javaType(typeDescriptor);
            this.p("public " + type + " type() {");
            this.indent();
            this.p("return " + type + "." + constant + ";");
            this.unindent();
            this.p("}");
            this.p();
        }
    }
    
    private void generateMethodParseFrom(final MessageDescriptor m, final String className) {
        String postMergeProcessing = ".checktInitialized()";
        if (this.deferredDecode) {
            postMergeProcessing = "";
        }
        this.p("public static " + className + " parseUnframed(org.apache.activemq.protobuf.CodedInputStream data) throws org.apache.activemq.protobuf.InvalidProtocolBufferException, java.io.IOException {");
        this.indent();
        this.p("return new " + className + "().mergeUnframed(data)" + postMergeProcessing + ";");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public static " + className + " parseUnframed(org.apache.activemq.protobuf.Buffer data) throws org.apache.activemq.protobuf.InvalidProtocolBufferException {");
        this.indent();
        this.p("return new " + className + "().mergeUnframed(data)" + postMergeProcessing + ";");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public static " + className + " parseUnframed(byte[] data) throws org.apache.activemq.protobuf.InvalidProtocolBufferException {");
        this.indent();
        this.p("return new " + className + "().mergeUnframed(data)" + postMergeProcessing + ";");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public static " + className + " parseUnframed(java.io.InputStream data) throws org.apache.activemq.protobuf.InvalidProtocolBufferException, java.io.IOException {");
        this.indent();
        this.p("return new " + className + "().mergeUnframed(data)" + postMergeProcessing + ";");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public static " + className + " parseFramed(org.apache.activemq.protobuf.CodedInputStream data) throws org.apache.activemq.protobuf.InvalidProtocolBufferException, java.io.IOException {");
        this.indent();
        this.p("return new " + className + "().mergeFramed(data)" + postMergeProcessing + ";");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public static " + className + " parseFramed(org.apache.activemq.protobuf.Buffer data) throws org.apache.activemq.protobuf.InvalidProtocolBufferException {");
        this.indent();
        this.p("return new " + className + "().mergeFramed(data)" + postMergeProcessing + ";");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public static " + className + " parseFramed(byte[] data) throws org.apache.activemq.protobuf.InvalidProtocolBufferException {");
        this.indent();
        this.p("return new " + className + "().mergeFramed(data)" + postMergeProcessing + ";");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public static " + className + " parseFramed(java.io.InputStream data) throws org.apache.activemq.protobuf.InvalidProtocolBufferException, java.io.IOException {");
        this.indent();
        this.p("return new " + className + "().mergeFramed(data)" + postMergeProcessing + ";");
        this.unindent();
        this.p("}");
        this.p();
    }
    
    private void generateMethodEquals(final MessageDescriptor m, final String className) {
        this.p("public boolean equals(Object obj) {");
        this.indent();
        this.p("if( obj==this )");
        this.p("   return true;");
        this.p("");
        this.p("if( obj==null || obj.getClass()!=" + className + ".class )");
        this.p("   return false;");
        this.p("");
        this.p("return equals((" + className + ")obj);");
        this.unindent();
        this.p("}");
        this.p("");
        this.p("public boolean equals(" + className + " obj) {");
        this.indent();
        if (this.deferredDecode) {
            this.p("return toUnframedBuffer().equals(obj.toUnframedBuffer());");
        }
        else {
            for (final FieldDescriptor field : m.getFields().values()) {
                final String uname = uCamel(field.getName());
                String getterMethod = "get" + uname + "()";
                final String hasMethod = "has" + uname + "()";
                if (field.getRule() == FieldDescriptor.REPEATED_RULE) {
                    getterMethod = "get" + uname + "List()";
                }
                this.p("if (" + hasMethod + " ^ obj." + hasMethod + " ) ");
                this.p("   return false;");
                if (field.getRule() != FieldDescriptor.REPEATED_RULE && (field.isNumberType() || field.getType() == FieldDescriptor.BOOL_TYPE)) {
                    this.p("if (" + hasMethod + " && ( " + getterMethod + "!=obj." + getterMethod + " ))");
                }
                else {
                    this.p("if (" + hasMethod + " && ( !" + getterMethod + ".equals(obj." + getterMethod + ") ))");
                }
                this.p("   return false;");
            }
            this.p("return true;");
        }
        this.unindent();
        this.p("}");
        this.p("");
        this.p("public int hashCode() {");
        this.indent();
        final int hc = className.hashCode();
        if (this.deferredDecode) {
            this.p("return " + hc + " ^ toUnframedBuffer().hashCode();");
        }
        else {
            this.p("int rc=" + hc + ";");
            int counter = 0;
            for (final FieldDescriptor field2 : m.getFields().values()) {
                ++counter;
                final String uname2 = uCamel(field2.getName());
                String getterMethod2 = "get" + uname2 + "()";
                final String hasMethod2 = "has" + uname2 + "()";
                if (field2.getRule() == FieldDescriptor.REPEATED_RULE) {
                    getterMethod2 = "get" + uname2 + "List()";
                }
                this.p("if (" + hasMethod2 + ") {");
                this.indent();
                if (field2.getRule() == FieldDescriptor.REPEATED_RULE) {
                    this.p("rc ^= ( " + uname2.hashCode() + "^" + getterMethod2 + ".hashCode() );");
                }
                else if (field2.isInteger32Type()) {
                    this.p("rc ^= ( " + uname2.hashCode() + "^" + getterMethod2 + " );");
                }
                else if (field2.isInteger64Type()) {
                    this.p("rc ^= ( " + uname2.hashCode() + "^(new Long(" + getterMethod2 + ")).hashCode() );");
                }
                else if (field2.getType() == FieldDescriptor.DOUBLE_TYPE) {
                    this.p("rc ^= ( " + uname2.hashCode() + "^(new Double(" + getterMethod2 + ")).hashCode() );");
                }
                else if (field2.getType() == FieldDescriptor.FLOAT_TYPE) {
                    this.p("rc ^= ( " + uname2.hashCode() + "^(new Double(" + getterMethod2 + ")).hashCode() );");
                }
                else if (field2.getType() == FieldDescriptor.BOOL_TYPE) {
                    this.p("rc ^= ( " + uname2.hashCode() + "^ (" + getterMethod2 + "? " + counter + ":-" + counter + ") );");
                }
                else {
                    this.p("rc ^= ( " + uname2.hashCode() + "^" + getterMethod2 + ".hashCode() );");
                }
                this.unindent();
                this.p("}");
            }
            this.p("return rc;");
        }
        this.unindent();
        this.p("}");
        this.p("");
    }
    
    private void generateMethodSerializedSize(final MessageDescriptor m) {
        this.p("public int serializedSizeUnframed() {");
        this.indent();
        if (this.deferredDecode) {
            this.p("if (encodedForm != null) {");
            this.indent();
            this.p("return encodedForm.length;");
            this.unindent();
            this.p("}");
        }
        this.p("if (memoizedSerializedSize != -1)");
        this.p("   return memoizedSerializedSize;");
        this.p();
        this.p("int size = 0;");
        for (final FieldDescriptor field : m.getFields().values()) {
            final String uname = uCamel(field.getName());
            String getter = "get" + uname + "()";
            final String type = this.javaType(field);
            this.p("if (has" + uname + "()) {");
            this.indent();
            if (field.getRule() == FieldDescriptor.REPEATED_RULE) {
                this.p("for (" + type + " i : get" + uname + "List()) {");
                this.indent();
                getter = "i";
            }
            if (field.getType() == FieldDescriptor.STRING_TYPE) {
                this.p("size += org.apache.activemq.protobuf.CodedOutputStream.computeStringSize(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.BYTES_TYPE) {
                this.p("size += org.apache.activemq.protobuf.CodedOutputStream.computeBytesSize(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.BOOL_TYPE) {
                this.p("size += org.apache.activemq.protobuf.CodedOutputStream.computeBoolSize(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.DOUBLE_TYPE) {
                this.p("size += org.apache.activemq.protobuf.CodedOutputStream.computeDoubleSize(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.FLOAT_TYPE) {
                this.p("size += org.apache.activemq.protobuf.CodedOutputStream.computeFloatSize(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.INT32_TYPE) {
                this.p("size += org.apache.activemq.protobuf.CodedOutputStream.computeInt32Size(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.INT64_TYPE) {
                this.p("size += org.apache.activemq.protobuf.CodedOutputStream.computeInt64Size(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.SINT32_TYPE) {
                this.p("size += org.apache.activemq.protobuf.CodedOutputStream.computeSInt32Size(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.SINT64_TYPE) {
                this.p("size += org.apache.activemq.protobuf.CodedOutputStream.computeSInt64Size(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.UINT32_TYPE) {
                this.p("size += org.apache.activemq.protobuf.CodedOutputStream.computeUInt32Size(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.UINT64_TYPE) {
                this.p("size += org.apache.activemq.protobuf.CodedOutputStream.computeUInt64Size(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.FIXED32_TYPE) {
                this.p("size += org.apache.activemq.protobuf.CodedOutputStream.computeFixed32Size(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.FIXED64_TYPE) {
                this.p("size += org.apache.activemq.protobuf.CodedOutputStream.computeFixed64Size(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.SFIXED32_TYPE) {
                this.p("size += org.apache.activemq.protobuf.CodedOutputStream.computeSFixed32Size(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.SFIXED64_TYPE) {
                this.p("size += org.apache.activemq.protobuf.CodedOutputStream.computeSFixed64Size(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getTypeDescriptor().isEnum()) {
                this.p("size += org.apache.activemq.protobuf.CodedOutputStream.computeEnumSize(" + field.getTag() + ", " + getter + ".getNumber());");
            }
            else if (field.getGroup() != null) {
                this.p("size += computeGroupSize(" + field.getTag() + ", " + getter + ");");
            }
            else {
                this.p("size += computeMessageSize(" + field.getTag() + ", " + getter + ");");
            }
            if (field.getRule() == FieldDescriptor.REPEATED_RULE) {
                this.unindent();
                this.p("}");
            }
            this.unindent();
            this.p("}");
        }
        this.p("memoizedSerializedSize = size;");
        this.p("return size;");
        this.unindent();
        this.p("}");
        this.p();
    }
    
    private void generateMethodWriteTo(final MessageDescriptor m) {
        this.p("public void writeUnframed(org.apache.activemq.protobuf.CodedOutputStream output) throws java.io.IOException {");
        this.indent();
        if (this.deferredDecode) {
            this.p("if (encodedForm == null) {");
            this.indent();
            this.p("int size = serializedSizeUnframed();");
            this.p("encodedForm = output.getNextBuffer(size);");
            this.p("org.apache.activemq.protobuf.CodedOutputStream original=null;");
            this.p("if( encodedForm == null ) {");
            this.indent();
            this.p("encodedForm = new org.apache.activemq.protobuf.Buffer(new byte[size]);");
            this.p("original = output;");
            this.p("output = new org.apache.activemq.protobuf.CodedOutputStream(encodedForm);");
            this.unindent();
            this.p("}");
        }
        for (final FieldDescriptor field : m.getFields().values()) {
            final String uname = uCamel(field.getName());
            String getter = "get" + uname + "()";
            final String type = this.javaType(field);
            this.p("if (has" + uname + "()) {");
            this.indent();
            if (field.getRule() == FieldDescriptor.REPEATED_RULE) {
                this.p("for (" + type + " i : get" + uname + "List()) {");
                this.indent();
                getter = "i";
            }
            if (field.getType() == FieldDescriptor.STRING_TYPE) {
                this.p("output.writeString(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.BYTES_TYPE) {
                this.p("output.writeBytes(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.BOOL_TYPE) {
                this.p("output.writeBool(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.DOUBLE_TYPE) {
                this.p("output.writeDouble(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.FLOAT_TYPE) {
                this.p("output.writeFloat(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.INT32_TYPE) {
                this.p("output.writeInt32(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.INT64_TYPE) {
                this.p("output.writeInt64(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.SINT32_TYPE) {
                this.p("output.writeSInt32(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.SINT64_TYPE) {
                this.p("output.writeSInt64(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.UINT32_TYPE) {
                this.p("output.writeUInt32(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.UINT64_TYPE) {
                this.p("output.writeUInt64(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.FIXED32_TYPE) {
                this.p("output.writeFixed32(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.FIXED64_TYPE) {
                this.p("output.writeFixed64(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.SFIXED32_TYPE) {
                this.p("output.writeSFixed32(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getType() == FieldDescriptor.SFIXED64_TYPE) {
                this.p("output.writeSFixed64(" + field.getTag() + ", " + getter + ");");
            }
            else if (field.getTypeDescriptor().isEnum()) {
                this.p("output.writeEnum(" + field.getTag() + ", " + getter + ".getNumber());");
            }
            else if (field.getGroup() != null) {
                this.p("writeGroup(output, " + field.getTag() + ", " + getter + ");");
            }
            else {
                this.p("writeMessage(output, " + field.getTag() + ", " + getter + ");");
            }
            if (field.getRule() == FieldDescriptor.REPEATED_RULE) {
                this.unindent();
                this.p("}");
            }
            this.unindent();
            this.p("}");
        }
        if (this.deferredDecode) {
            this.p("if( original !=null ) {");
            this.indent();
            this.p("output.checkNoSpaceLeft();");
            this.p("output = original;");
            this.p("output.writeRawBytes(encodedForm);");
            this.unindent();
            this.p("}");
            this.unindent();
            this.p("} else {");
            this.indent();
            this.p("output.writeRawBytes(encodedForm);");
            this.unindent();
            this.p("}");
        }
        this.unindent();
        this.p("}");
        this.p();
    }
    
    private void generateMethodMergeFromStream(final MessageDescriptor m, final String className) {
        this.p("public " + className + " mergeUnframed(org.apache.activemq.protobuf.CodedInputStream input) throws java.io.IOException {");
        this.indent();
        this.p("while (true) {");
        this.indent();
        this.p("int tag = input.readTag();");
        this.p("if ((tag & 0x07) == 4) {");
        this.p("   return this;");
        this.p("}");
        this.p("switch (tag) {");
        this.p("case 0:");
        this.p("   return this;");
        this.p("default: {");
        this.p("   break;");
        this.p("}");
        for (final FieldDescriptor field : m.getFields().values()) {
            final String uname = uCamel(field.getName());
            String setter = "set" + uname;
            final boolean repeated = field.getRule() == FieldDescriptor.REPEATED_RULE;
            if (repeated) {
                setter = "get" + uname + "List().add";
            }
            if (field.getType() == FieldDescriptor.STRING_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 2) + ":");
                this.indent();
                this.p(setter + "(input.readString());");
            }
            else if (field.getType() == FieldDescriptor.BYTES_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 2) + ":");
                this.indent();
                this.p(setter + "(input.readBytes());");
            }
            else if (field.getType() == FieldDescriptor.BOOL_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 0) + ":");
                this.indent();
                this.p(setter + "(input.readBool());");
            }
            else if (field.getType() == FieldDescriptor.DOUBLE_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 1) + ":");
                this.indent();
                this.p(setter + "(input.readDouble());");
            }
            else if (field.getType() == FieldDescriptor.FLOAT_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 5) + ":");
                this.indent();
                this.p(setter + "(input.readFloat());");
            }
            else if (field.getType() == FieldDescriptor.INT32_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 0) + ":");
                this.indent();
                this.p(setter + "(input.readInt32());");
            }
            else if (field.getType() == FieldDescriptor.INT64_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 0) + ":");
                this.indent();
                this.p(setter + "(input.readInt64());");
            }
            else if (field.getType() == FieldDescriptor.SINT32_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 0) + ":");
                this.indent();
                this.p(setter + "(input.readSInt32());");
            }
            else if (field.getType() == FieldDescriptor.SINT64_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 0) + ":");
                this.indent();
                this.p(setter + "(input.readSInt64());");
            }
            else if (field.getType() == FieldDescriptor.UINT32_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 0) + ":");
                this.indent();
                this.p(setter + "(input.readUInt32());");
            }
            else if (field.getType() == FieldDescriptor.UINT64_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 0) + ":");
                this.indent();
                this.p(setter + "(input.readUInt64());");
            }
            else if (field.getType() == FieldDescriptor.FIXED32_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 5) + ":");
                this.indent();
                this.p(setter + "(input.readFixed32());");
            }
            else if (field.getType() == FieldDescriptor.FIXED64_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 1) + ":");
                this.indent();
                this.p(setter + "(input.readFixed64());");
            }
            else if (field.getType() == FieldDescriptor.SFIXED32_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 5) + ":");
                this.indent();
                this.p(setter + "(input.readSFixed32());");
            }
            else if (field.getType() == FieldDescriptor.SFIXED64_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 1) + ":");
                this.indent();
                this.p(setter + "(input.readSFixed64());");
            }
            else if (field.getTypeDescriptor().isEnum()) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 0) + ":");
                this.indent();
                final String type = this.javaType(field);
                this.p("{");
                this.indent();
                this.p("int t = input.readEnum();");
                this.p("" + type + " value = " + type + ".valueOf(t);");
                this.p("if( value !=null ) {");
                this.indent();
                this.p(setter + "(value);");
                this.unindent();
                this.p("}");
                this.unindent();
                this.p("}");
            }
            else if (field.getGroup() != null) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 3) + ":");
                this.indent();
                final String type = this.javaType(field);
                if (repeated) {
                    this.p(setter + "(readGroup(input, " + field.getTag() + ", new " + type + "()));");
                }
                else {
                    this.p("if (has" + uname + "()) {");
                    this.indent();
                    this.p("readGroup(input, " + field.getTag() + ", get" + uname + "());");
                    this.unindent();
                    this.p("} else {");
                    this.indent();
                    this.p(setter + "(readGroup(input, " + field.getTag() + ",new " + type + "()));");
                    this.unindent();
                    this.p("}");
                }
                this.p("");
            }
            else {
                this.p("case " + WireFormat.makeTag(field.getTag(), 2) + ":");
                this.indent();
                final String type = this.javaType(field);
                if (repeated) {
                    this.p(setter + "(new " + type + "().mergeFramed(input));");
                }
                else {
                    this.p("if (has" + uname + "()) {");
                    this.indent();
                    this.p("get" + uname + "().mergeFramed(input);");
                    this.unindent();
                    this.p("} else {");
                    this.indent();
                    this.p(setter + "(new " + type + "().mergeFramed(input));");
                    this.unindent();
                    this.p("}");
                }
            }
            this.p("break;");
            this.unindent();
        }
        this.p("}");
        this.unindent();
        this.p("}");
        this.unindent();
        this.p("}");
    }
    
    private void generateMethodMergeFromBean(final MessageDescriptor m, final String className) {
        this.p("public " + className + " mergeFrom(" + className + " other) {");
        this.indent();
        for (final FieldDescriptor field : m.getFields().values()) {
            final String uname = uCamel(field.getName());
            this.p("if (other.has" + uname + "()) {");
            this.indent();
            if (field.isScalarType() || field.getTypeDescriptor().isEnum()) {
                if (field.isRepeated()) {
                    this.p("get" + uname + "List().addAll(other.get" + uname + "List());");
                }
                else {
                    this.p("set" + uname + "(other.get" + uname + "());");
                }
            }
            else {
                final String type = this.javaType(field);
                if (field.isRepeated()) {
                    this.p("for(" + type + " element: other.get" + uname + "List() ) {");
                    this.indent();
                    this.p("get" + uname + "List().add(element.clone());");
                    this.unindent();
                    this.p("}");
                }
                else {
                    this.p("if (has" + uname + "()) {");
                    this.indent();
                    this.p("get" + uname + "().mergeFrom(other.get" + uname + "());");
                    this.unindent();
                    this.p("} else {");
                    this.indent();
                    this.p("set" + uname + "(other.get" + uname + "().clone());");
                    this.unindent();
                    this.p("}");
                }
            }
            this.unindent();
            this.p("}");
        }
        this.p("return this;");
        this.unindent();
        this.p("}");
        this.p();
    }
    
    private void generateMethodClear(final MessageDescriptor m) {
        this.p("public void clear() {");
        this.indent();
        this.p("super.clear();");
        for (final FieldDescriptor field : m.getFields().values()) {
            final String uname = uCamel(field.getName());
            this.p("clear" + uname + "();");
        }
        this.unindent();
        this.p("}");
        this.p();
    }
    
    private void generateMethodAssertInitialized(final MessageDescriptor m, final String className) {
        this.p("public java.util.ArrayList<String> missingFields() {");
        this.indent();
        this.p("java.util.ArrayList<String> missingFields = super.missingFields();");
        for (final FieldDescriptor field : m.getFields().values()) {
            final String uname = uCamel(field.getName());
            if (field.isRequired()) {
                this.p("if(  !has" + uname + "() ) {");
                this.indent();
                this.p("missingFields.add(\"" + field.getName() + "\");");
                this.unindent();
                this.p("}");
            }
        }
        if (!this.deferredDecode) {
            for (final FieldDescriptor field : m.getFields().values()) {
                if (field.getTypeDescriptor() != null && !field.getTypeDescriptor().isEnum()) {
                    final String uname = uCamel(field.getName());
                    this.p("if( has" + uname + "() ) {");
                    this.indent();
                    if (!field.isRepeated()) {
                        this.p("try {");
                        this.indent();
                        this.p("get" + uname + "().assertInitialized();");
                        this.unindent();
                        this.p("} catch (org.apache.activemq.protobuf.UninitializedMessageException e){");
                        this.indent();
                        this.p("missingFields.addAll(prefix(e.getMissingFields(),\"" + field.getName() + ".\"));");
                        this.unindent();
                        this.p("}");
                    }
                    else {
                        final String type = this.javaCollectionType(field);
                        this.p("java.util.List<" + type + "> l = get" + uname + "List();");
                        this.p("for( int i=0; i < l.size(); i++ ) {");
                        this.indent();
                        this.p("try {");
                        this.indent();
                        this.p("l.get(i).assertInitialized();");
                        this.unindent();
                        this.p("} catch (org.apache.activemq.protobuf.UninitializedMessageException e){");
                        this.indent();
                        this.p("missingFields.addAll(prefix(e.getMissingFields(),\"" + field.getName() + "[\"+i+\"]\"));");
                        this.unindent();
                        this.p("}");
                        this.unindent();
                        this.p("}");
                    }
                    this.unindent();
                    this.p("}");
                }
            }
        }
        this.p("return missingFields;");
        this.unindent();
        this.p("}");
        this.p();
    }
    
    private void generateMethodToString(final MessageDescriptor m) {
        this.p("public String toString() {");
        this.indent();
        this.p("return toString(new java.lang.StringBuilder(), \"\").toString();");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public java.lang.StringBuilder toString(java.lang.StringBuilder sb, String prefix) {");
        this.indent();
        if (this.deferredDecode) {
            this.p("load();");
        }
        for (final FieldDescriptor field : m.getFields().values()) {
            final String uname = uCamel(field.getName());
            this.p("if(  has" + uname + "() ) {");
            this.indent();
            if (field.isRepeated()) {
                final String type = this.javaCollectionType(field);
                this.p("java.util.List<" + type + "> l = get" + uname + "List();");
                this.p("for( int i=0; i < l.size(); i++ ) {");
                this.indent();
                if (field.getTypeDescriptor() != null && !field.getTypeDescriptor().isEnum()) {
                    this.p("sb.append(prefix+\"" + field.getName() + "[\"+i+\"] {\\n\");");
                    this.p("l.get(i).toString(sb, prefix+\"  \");");
                    this.p("sb.append(prefix+\"}\\n\");");
                }
                else {
                    this.p("sb.append(prefix+\"" + field.getName() + "[\"+i+\"]: \");");
                    this.p("sb.append(l.get(i));");
                    this.p("sb.append(\"\\n\");");
                }
                this.unindent();
                this.p("}");
            }
            else if (field.getTypeDescriptor() != null && !field.getTypeDescriptor().isEnum()) {
                this.p("sb.append(prefix+\"" + field.getName() + " {\\n\");");
                this.p("get" + uname + "().toString(sb, prefix+\"  \");");
                this.p("sb.append(prefix+\"}\\n\");");
            }
            else {
                this.p("sb.append(prefix+\"" + field.getName() + ": \");");
                this.p("sb.append(get" + uname + "());");
                this.p("sb.append(\"\\n\");");
            }
            this.unindent();
            this.p("}");
        }
        this.p("return sb;");
        this.unindent();
        this.p("}");
        this.p();
    }
    
    private void generateFieldAccessor(final FieldDescriptor field) {
        final String lname = lCamel(field.getName());
        final String uname = uCamel(field.getName());
        final String type = (field.getRule() == FieldDescriptor.REPEATED_RULE) ? this.javaCollectionType(field) : this.javaType(field);
        final String typeDefault = this.javaTypeDefault(field);
        final boolean primitive = field.getTypeDescriptor() == null || field.getTypeDescriptor().isEnum();
        final boolean repeated = field.getRule() == FieldDescriptor.REPEATED_RULE;
        this.p("// " + field.getRule() + " " + field.getType() + " " + field.getName() + " = " + field.getTag() + ";");
        if (repeated) {
            this.p("private java.util.List<" + type + "> f_" + lname + ";");
            this.p();
            this.p("public boolean has" + uname + "() {");
            this.indent();
            if (this.deferredDecode) {
                this.p("load();");
            }
            this.p("return this.f_" + lname + "!=null && !this.f_" + lname + ".isEmpty();");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public java.util.List<" + type + "> get" + uname + "List() {");
            this.indent();
            if (this.deferredDecode) {
                this.p("load();");
            }
            this.p("if( this.f_" + lname + " == null ) {");
            this.indent();
            this.p("this.f_" + lname + " = new java.util.ArrayList<" + type + ">();");
            this.unindent();
            this.p("}");
            this.p("return this.f_" + lname + ";");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public T set" + uname + "List(java.util.List<" + type + "> " + lname + ") {");
            this.indent();
            this.p("loadAndClear();");
            this.p("this.f_" + lname + " = " + lname + ";");
            this.p("return (T)this;");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public int get" + uname + "Count() {");
            this.indent();
            if (this.deferredDecode) {
                this.p("load();");
            }
            this.p("if( this.f_" + lname + " == null ) {");
            this.indent();
            this.p("return 0;");
            this.unindent();
            this.p("}");
            this.p("return this.f_" + lname + ".size();");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public " + type + " get" + uname + "(int index) {");
            this.indent();
            if (this.deferredDecode) {
                this.p("load();");
            }
            this.p("if( this.f_" + lname + " == null ) {");
            this.indent();
            this.p("return null;");
            this.unindent();
            this.p("}");
            this.p("return this.f_" + lname + ".get(index);");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public T set" + uname + "(int index, " + type + " value) {");
            this.indent();
            this.p("loadAndClear();");
            this.p("get" + uname + "List().set(index, value);");
            this.p("return (T)this;");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public T add" + uname + "(" + type + " value) {");
            this.indent();
            this.p("loadAndClear();");
            this.p("get" + uname + "List().add(value);");
            this.p("return (T)this;");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public T addAll" + uname + "(java.lang.Iterable<? extends " + type + "> collection) {");
            this.indent();
            this.p("loadAndClear();");
            this.p("super.addAll(collection, get" + uname + "List());");
            this.p("return (T)this;");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public void clear" + uname + "() {");
            this.indent();
            this.p("loadAndClear();");
            this.p("this.f_" + lname + " = null;");
            this.unindent();
            this.p("}");
            this.p();
        }
        else {
            this.p("private " + type + " f_" + lname + " = " + typeDefault + ";");
            if (primitive) {
                this.p("private boolean b_" + lname + ";");
            }
            this.p();
            this.p("public boolean has" + uname + "() {");
            this.indent();
            if (this.deferredDecode) {
                this.p("load();");
            }
            if (primitive) {
                this.p("return this.b_" + lname + ";");
            }
            else {
                this.p("return this.f_" + lname + "!=null;");
            }
            this.unindent();
            this.p("}");
            this.p();
            this.p("public " + type + " get" + uname + "() {");
            this.indent();
            if (this.deferredDecode) {
                this.p("load();");
            }
            if (field.getTypeDescriptor() != null && !field.getTypeDescriptor().isEnum()) {
                this.p("if( this.f_" + lname + " == null ) {");
                this.indent();
                this.p("this.f_" + lname + " = new " + type + "();");
                this.unindent();
                this.p("}");
            }
            this.p("return this.f_" + lname + ";");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public T set" + uname + "(" + type + " " + lname + ") {");
            this.indent();
            this.p("loadAndClear();");
            if (primitive) {
                if (this.auto_clear_optional_fields && field.isOptional()) {
                    if (field.isStringType() && !"null".equals(typeDefault)) {
                        this.p("this.b_" + lname + " = (" + lname + " != " + typeDefault + ");");
                    }
                    else {
                        this.p("this.b_" + lname + " = (" + lname + " != " + typeDefault + ");");
                    }
                }
                else {
                    this.p("this.b_" + lname + " = true;");
                }
            }
            this.p("this.f_" + lname + " = " + lname + ";");
            this.p("return (T)this;");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public void clear" + uname + "() {");
            this.indent();
            this.p("loadAndClear();");
            if (primitive) {
                this.p("this.b_" + lname + " = false;");
            }
            this.p("this.f_" + lname + " = " + typeDefault + ";");
            this.unindent();
            this.p("}");
            this.p();
        }
    }
    
    private String javaTypeDefault(final FieldDescriptor field) {
        final OptionDescriptor defaultOption = field.getOptions().get("default");
        if (defaultOption != null) {
            if (field.isStringType()) {
                return this.asJavaString(defaultOption.getValue());
            }
            if (field.getType() == FieldDescriptor.BYTES_TYPE) {
                return "new org.apache.activemq.protobuf.Buffer(" + this.asJavaString(defaultOption.getValue()) + ")";
            }
            if (field.isInteger32Type()) {
                int v;
                if (field.getType() == FieldDescriptor.UINT32_TYPE) {
                    v = TextFormat.parseUInt32(defaultOption.getValue());
                }
                else {
                    v = TextFormat.parseInt32(defaultOption.getValue());
                }
                return "" + v;
            }
            if (field.isInteger64Type()) {
                long v2;
                if (field.getType() == FieldDescriptor.UINT64_TYPE) {
                    v2 = TextFormat.parseUInt64(defaultOption.getValue());
                }
                else {
                    v2 = TextFormat.parseInt64(defaultOption.getValue());
                }
                return "" + v2 + "l";
            }
            if (field.getType() == FieldDescriptor.DOUBLE_TYPE) {
                final double v3 = Double.valueOf(defaultOption.getValue());
                return "" + v3 + "d";
            }
            if (field.getType() == FieldDescriptor.FLOAT_TYPE) {
                final float v4 = Float.valueOf(defaultOption.getValue());
                return "" + v4 + "f";
            }
            if (field.getType() == FieldDescriptor.BOOL_TYPE) {
                final boolean v5 = Boolean.valueOf(defaultOption.getValue());
                return "" + v5;
            }
            if (field.getTypeDescriptor() != null && field.getTypeDescriptor().isEnum()) {
                return this.javaType(field) + "." + defaultOption.getValue();
            }
            return defaultOption.getValue();
        }
        else {
            if (field.isNumberType()) {
                return "0";
            }
            if (field.getType() == FieldDescriptor.BOOL_TYPE) {
                return "false";
            }
            return "null";
        }
    }
    
    private String asJavaString(final String value) {
        final StringBuilder sb = new StringBuilder(value.length() + 2);
        sb.append("\"");
        for (int i = 0; i < value.length(); ++i) {
            final char b = value.charAt(i);
            switch (b) {
                case '\b': {
                    sb.append("\\b");
                    break;
                }
                case '\f': {
                    sb.append("\\f");
                    break;
                }
                case '\n': {
                    sb.append("\\n");
                    break;
                }
                case '\r': {
                    sb.append("\\r");
                    break;
                }
                case '\t': {
                    sb.append("\\t");
                    break;
                }
                case '\\': {
                    sb.append("\\\\");
                    break;
                }
                case '\'': {
                    sb.append("\\'");
                    break;
                }
                case '\"': {
                    sb.append("\\\"");
                    break;
                }
                default: {
                    if (b >= ' ' && b < 'Z') {
                        sb.append(b);
                        break;
                    }
                    sb.append("\\u");
                    sb.append(JavaGenerator.HEX_TABLE[b >>> 12 & 0xF]);
                    sb.append(JavaGenerator.HEX_TABLE[b >>> 8 & 0xF]);
                    sb.append(JavaGenerator.HEX_TABLE[b >>> 4 & 0xF]);
                    sb.append(JavaGenerator.HEX_TABLE[b & '\u000f']);
                    break;
                }
            }
        }
        sb.append("\"");
        return sb.toString();
    }
    
    private void generateEnum(final EnumDescriptor ed) {
        final String uname = uCamel(ed.getName());
        String staticOption = "static ";
        if (this.multipleFiles && ed.getParent() == null) {
            staticOption = "";
        }
        this.p();
        this.p("public " + staticOption + "enum " + uname + " {");
        this.indent();
        this.p();
        int counter = 0;
        for (final EnumFieldDescriptor field : ed.getFields().values()) {
            final boolean last = counter + 1 == ed.getFields().size();
            this.p(field.getName() + "(\"" + field.getName() + "\", " + field.getValue() + ")" + (last ? ";" : ","));
            ++counter;
        }
        this.p();
        this.p("private final String name;");
        this.p("private final int value;");
        this.p();
        this.p("private " + uname + "(String name, int value) {");
        this.p("   this.name = name;");
        this.p("   this.value = value;");
        this.p("}");
        this.p();
        this.p("public final int getNumber() {");
        this.p("   return value;");
        this.p("}");
        this.p();
        this.p("public final String toString() {");
        this.p("   return name;");
        this.p("}");
        this.p();
        this.p("public static " + uname + " valueOf(int value) {");
        this.p("   switch (value) {");
        final HashSet<Integer> values = new HashSet<Integer>();
        for (final EnumFieldDescriptor field2 : ed.getFields().values()) {
            if (!values.contains(field2.getValue())) {
                this.p("   case " + field2.getValue() + ":");
                this.p("      return " + field2.getName() + ";");
                values.add(field2.getValue());
            }
        }
        this.p("   default:");
        this.p("      return null;");
        this.p("   }");
        this.p("}");
        this.p();
        final String createMessage = this.getOption(ed.getOptions(), "java_create_message", null);
        if ("true".equals(createMessage)) {
            this.p("public org.apache.activemq.protobuf.Message createMessage() {");
            this.indent();
            this.p("switch (this) {");
            this.indent();
            for (final EnumFieldDescriptor field3 : ed.getFields().values()) {
                this.p("case " + field3.getName() + ":");
                final String type = this.constantToUCamelCase(field3.getName());
                this.p("   return new " + type + "();");
            }
            this.p("default:");
            this.p("   return null;");
            this.unindent();
            this.p("}");
            this.unindent();
            this.p("}");
            this.p();
        }
        this.unindent();
        this.p("}");
        this.p();
    }
    
    private String javaCollectionType(final FieldDescriptor field) {
        if (field.isInteger32Type()) {
            return "java.lang.Integer";
        }
        if (field.isInteger64Type()) {
            return "java.lang.Long";
        }
        if (field.getType() == FieldDescriptor.DOUBLE_TYPE) {
            return "java.lang.Double";
        }
        if (field.getType() == FieldDescriptor.FLOAT_TYPE) {
            return "java.lang.Float";
        }
        if (field.getType() == FieldDescriptor.STRING_TYPE) {
            return "java.lang.String";
        }
        if (field.getType() == FieldDescriptor.BYTES_TYPE) {
            return "org.apache.activemq.protobuf.Buffer";
        }
        if (field.getType() == FieldDescriptor.BOOL_TYPE) {
            return "java.lang.Boolean";
        }
        final TypeDescriptor descriptor = field.getTypeDescriptor();
        return this.javaType(descriptor);
    }
    
    private String javaType(final FieldDescriptor field) {
        if (field.isInteger32Type()) {
            return "int";
        }
        if (field.isInteger64Type()) {
            return "long";
        }
        if (field.getType() == FieldDescriptor.DOUBLE_TYPE) {
            return "double";
        }
        if (field.getType() == FieldDescriptor.FLOAT_TYPE) {
            return "float";
        }
        if (field.getType() == FieldDescriptor.STRING_TYPE) {
            return "java.lang.String";
        }
        if (field.getType() == FieldDescriptor.BYTES_TYPE) {
            return "org.apache.activemq.protobuf.Buffer";
        }
        if (field.getType() == FieldDescriptor.BOOL_TYPE) {
            return "boolean";
        }
        final TypeDescriptor descriptor = field.getTypeDescriptor();
        return this.javaType(descriptor);
    }
    
    private String javaType(final TypeDescriptor descriptor) {
        final ProtoDescriptor p = descriptor.getProtoDescriptor();
        if (p == this.proto) {
            return descriptor.getQName();
        }
        final String othePackage = this.javaPackage(p);
        if (this.equals(othePackage, this.javaPackage(this.proto))) {
            return this.javaClassName(p) + "." + descriptor.getQName();
        }
        return othePackage + "." + this.javaClassName(p) + "." + descriptor.getQName();
    }
    
    private boolean equals(final String o1, final String o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
    }
    
    private String javaClassName(final ProtoDescriptor proto) {
        return this.getOption(proto.getOptions(), "java_outer_classname", uCamel(removeFileExtension(proto.getName())));
    }
    
    private boolean isMultipleFilesEnabled(final ProtoDescriptor proto) {
        return "true".equals(this.getOption(proto.getOptions(), "java_multiple_files", "false"));
    }
    
    private String javaPackage(final ProtoDescriptor proto) {
        String name = proto.getPackageName();
        if (name != null) {
            name = name.replace('-', '.');
            name = name.replace('/', '.');
        }
        return this.getOption(proto.getOptions(), "java_package", name);
    }
    
    private void indent() {
        ++this.indent;
    }
    
    private void unindent() {
        --this.indent;
    }
    
    private void p(final String line) {
        for (int i = 0; i < this.indent; ++i) {
            this.w.print("   ");
        }
        this.w.println(line);
    }
    
    private void p() {
        this.w.println();
    }
    
    private String getOption(final Map<String, OptionDescriptor> options, final String optionName, final String defaultValue) {
        final OptionDescriptor optionDescriptor = options.get(optionName);
        if (optionDescriptor == null) {
            return defaultValue;
        }
        return optionDescriptor.getValue();
    }
    
    private static String removeFileExtension(final String name) {
        return name.replaceAll("\\..*", "");
    }
    
    private static String uCamel(final String name) {
        boolean upNext = true;
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (Character.isJavaIdentifierPart(c) && Character.isLetterOrDigit(c)) {
                if (upNext) {
                    c = Character.toUpperCase(c);
                    upNext = false;
                }
                sb.append(c);
            }
            else {
                upNext = true;
            }
        }
        return sb.toString();
    }
    
    private static String lCamel(final String name) {
        if (name == null || name.length() < 1) {
            return name;
        }
        final String uCamel = uCamel(name);
        return uCamel.substring(0, 1).toLowerCase() + uCamel.substring(1);
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
    
    private String constantCase(final String name) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); ++i) {
            final char c = name.charAt(i);
            if (i != 0 && Character.isUpperCase(c)) {
                sb.append("_");
            }
            sb.append(Character.toUpperCase(c));
        }
        return sb.toString();
    }
    
    public File getOut() {
        return this.out;
    }
    
    public void setOut(final File outputDirectory) {
        this.out = outputDirectory;
    }
    
    public File[] getPath() {
        return this.path;
    }
    
    public void setPath(final File[] path) {
        this.path = path;
    }
    
    static {
        HEX_TABLE = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
    
    interface Closure
    {
        void execute() throws CompilerException;
    }
}
