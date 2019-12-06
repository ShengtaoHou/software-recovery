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

public class AltJavaGenerator
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
    private boolean auto_clear_optional_fields;
    static final char[] HEX_TABLE;
    
    public AltJavaGenerator() {
        this.out = new File(".");
        this.path = new File[] { new File(".") };
        this.errors = new ArrayList<String>();
    }
    
    public static void main(String[] args) {
        final AltJavaGenerator generator = new AltJavaGenerator();
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
        this.auto_clear_optional_fields = Boolean.parseBoolean(this.getOption(this.proto.getOptions(), "auto_clear_optional_fields", "false"));
        if (this.multipleFiles) {
            this.generateProtoFile();
        }
        else {
            this.writeFile(this.outerClassName, new Closure() {
                public void execute() throws CompilerException {
                    AltJavaGenerator.this.generateProtoFile();
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
                        AltJavaGenerator.this.generateFileHeader();
                        AltJavaGenerator.this.generateEnum(o);
                    }
                });
            }
            for (final MessageDescriptor o2 : this.proto.getMessages().values()) {
                final MessageDescriptor value2 = o2;
                final String className = uCamel(o2.getName());
                this.writeFile(className, new Closure() {
                    public void execute() throws CompilerException {
                        AltJavaGenerator.this.generateFileHeader();
                        AltJavaGenerator.this.generateMessageBean(o2);
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
        final String beanClassName = className + "Bean";
        final String bufferClassName = className + "Buffer";
        this.p();
        String staticOption = "static ";
        if (this.multipleFiles && m.getParent() == null) {
            staticOption = "";
        }
        String extendsClause = " extends org.apache.activemq.protobuf.PBMessage<" + className + "." + beanClassName + ", " + className + "." + bufferClassName + ">";
        for (final EnumFieldDescriptor enumFeild : m.getAssociatedEnumFieldDescriptors()) {
            String name = uCamel(enumFeild.getParent().getName());
            name = name + "." + name + "Creatable";
            extendsClause = extendsClause + ", " + name;
        }
        this.p(staticOption + "public interface " + className + extendsClause + " {");
        this.p();
        this.indent();
        for (final EnumDescriptor enumType : m.getEnums().values()) {
            this.generateEnum(enumType);
        }
        for (final MessageDescriptor subMessage : m.getMessages().values()) {
            this.generateMessageBean(subMessage);
        }
        for (final FieldDescriptor field : m.getFields().values()) {
            if (field.isGroup()) {
                this.generateMessageBean(field.getGroup());
            }
        }
        for (final FieldDescriptor field : m.getFields().values()) {
            this.generateFieldGetterSignatures(field);
        }
        this.p("public " + beanClassName + " copy();");
        this.p("public " + bufferClassName + " freeze();");
        this.p("public java.lang.StringBuilder toString(java.lang.StringBuilder sb, String prefix);");
        this.p();
        this.p("static public final class " + beanClassName + " implements " + className + " {");
        this.p();
        this.indent();
        this.p("" + bufferClassName + " frozen;");
        this.p("" + beanClassName + " bean;");
        this.p();
        this.p("public " + beanClassName + "() {");
        this.indent();
        this.p("this.bean = this;");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public " + beanClassName + "(" + beanClassName + " copy) {");
        this.indent();
        this.p("this.bean = copy;");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public " + beanClassName + " copy() {");
        this.indent();
        this.p("return new " + beanClassName + "(bean);");
        this.unindent();
        this.p("}");
        this.p();
        this.generateMethodFreeze(m, bufferClassName);
        this.p("private void copyCheck() {");
        this.indent();
        this.p("assert frozen==null : org.apache.activemq.protobuf.MessageBufferSupport.FORZEN_ERROR_MESSAGE;");
        this.p("if (bean != this) {");
        this.indent();
        this.p("copy(bean);");
        this.unindent();
        this.p("}");
        this.unindent();
        this.p("}");
        this.p();
        this.generateMethodCopyFromBean(m, beanClassName);
        for (final FieldDescriptor field : m.getFields().values()) {
            this.generateFieldAccessor(beanClassName, field);
        }
        this.generateMethodToString(m);
        this.generateMethodMergeFromStream(m, beanClassName);
        this.generateBeanEquals(m, beanClassName);
        this.generateMethodMergeFromBean(m, className);
        this.generateMethodClear(m);
        this.generateReadWriteExternal(m);
        for (final EnumFieldDescriptor enumFeild : m.getAssociatedEnumFieldDescriptors()) {
            final String enumName = uCamel(enumFeild.getParent().getName());
            this.p("public " + enumName + " to" + enumName + "() {");
            this.indent();
            this.p("return " + enumName + "." + enumFeild.getName() + ";");
            this.unindent();
            this.p("}");
            this.p();
        }
        this.unindent();
        this.p("}");
        this.p();
        this.p("static public final class " + bufferClassName + " implements org.apache.activemq.protobuf.MessageBuffer<" + className + "." + beanClassName + ", " + className + "." + bufferClassName + ">, " + className + " {");
        this.p();
        this.indent();
        this.p("private " + beanClassName + " bean;");
        this.p("private org.apache.activemq.protobuf.Buffer buffer;");
        this.p("private int size=-1;");
        this.p("private int hashCode;");
        this.p();
        this.p("private " + bufferClassName + "(org.apache.activemq.protobuf.Buffer buffer) {");
        this.indent();
        this.p("this.buffer = buffer;");
        this.unindent();
        this.p("}");
        this.p();
        this.p("private " + bufferClassName + "(" + beanClassName + " bean) {");
        this.indent();
        this.p("this.bean = bean;");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public " + beanClassName + " copy() {");
        this.indent();
        this.p("return bean().copy();");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public " + bufferClassName + " freeze() {");
        this.indent();
        this.p("return this;");
        this.unindent();
        this.p("}");
        this.p();
        this.p("private " + beanClassName + " bean() {");
        this.indent();
        this.p("if (bean == null) {");
        this.indent();
        this.p("try {");
        this.indent();
        this.p("bean = new " + beanClassName + "().mergeUnframed(new org.apache.activemq.protobuf.CodedInputStream(buffer));");
        this.p("bean.frozen=this;");
        this.unindent();
        this.p("} catch (org.apache.activemq.protobuf.InvalidProtocolBufferException e) {");
        this.indent();
        this.p("throw new RuntimeException(e);");
        this.unindent();
        this.p("} catch (java.io.IOException e) {");
        this.indent();
        this.p("throw new RuntimeException(\"An IOException was thrown (should never happen in this method).\", e);");
        this.unindent();
        this.p("}");
        this.unindent();
        this.p("}");
        this.p("return bean;");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public String toString() {");
        this.indent();
        this.p("return bean().toString();");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public java.lang.StringBuilder toString(java.lang.StringBuilder sb, String prefix) {");
        this.indent();
        this.p("return bean().toString(sb, prefix);");
        this.unindent();
        this.p("}");
        this.p();
        for (final FieldDescriptor field : m.getFields().values()) {
            this.generateBufferGetters(field);
        }
        this.generateMethodWrite(m);
        this.generateMethodSerializedSize(m);
        this.generateMethodParseFrom(m, bufferClassName, beanClassName);
        this.generateBufferEquals(m, bufferClassName);
        this.p("public boolean frozen() {");
        this.indent();
        this.p("return true;");
        this.unindent();
        this.p("}");
        for (final EnumFieldDescriptor enumFeild : m.getAssociatedEnumFieldDescriptors()) {
            final String enumName = uCamel(enumFeild.getParent().getName());
            this.p("public " + enumName + " to" + enumName + "() {");
            this.indent();
            this.p("return " + enumName + "." + enumFeild.getName() + ";");
            this.unindent();
            this.p("}");
            this.p();
        }
        this.unindent();
        this.p("}");
        this.p();
        this.unindent();
        this.p("}");
        this.p();
    }
    
    private void generateMethodFreeze(final MessageDescriptor m, final String bufferClassName) {
        this.p("public boolean frozen() {");
        this.indent();
        this.p("return frozen!=null;");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public " + bufferClassName + " freeze() {");
        this.indent();
        this.p("if( frozen==null ) {");
        this.indent();
        this.p("frozen = new " + bufferClassName + "(bean);");
        this.p("assert deepFreeze();");
        this.unindent();
        this.p("}");
        this.p("return frozen;");
        this.unindent();
        this.p("}");
        this.p();
        this.p("private boolean deepFreeze() {");
        this.indent();
        this.p("frozen.serializedSizeUnframed();");
        this.p("return true;");
        this.unindent();
        this.p("}");
        this.p();
    }
    
    private void generateMethodCopyFromBean(final MessageDescriptor m, final String className) {
        this.p("private void copy(" + className + " other) {");
        this.indent();
        this.p("this.bean = this;");
        for (final FieldDescriptor field : m.getFields().values()) {
            final String lname = lCamel(field.getName());
            final String type = (field.getRule() == FieldDescriptor.REPEATED_RULE) ? this.javaCollectionType(field) : this.javaType(field);
            final boolean primitive = field.getTypeDescriptor() == null || field.getTypeDescriptor().isEnum();
            if (field.isRepeated()) {
                if (primitive) {
                    this.p("this.f_" + lname + " = other.f_" + lname + ";");
                    this.p("if( this.f_" + lname + " !=null && !other.frozen()) {");
                    this.indent();
                    this.p("this.f_" + lname + " = new java.util.ArrayList<" + type + ">(this.f_" + lname + ");");
                    this.unindent();
                    this.p("}");
                }
                else {
                    this.p("this.f_" + lname + " = other.f_" + lname + ";");
                    this.p("if( this.f_" + lname + " !=null) {");
                    this.indent();
                    this.p("this.f_" + lname + " = new java.util.ArrayList<" + type + ">(other.f_" + lname + ".size());");
                    this.p("for( " + type + " e :  other.f_" + lname + ") {");
                    this.indent();
                    this.p("this.f_" + lname + ".add(e.copy());");
                    this.unindent();
                    this.p("}");
                    this.unindent();
                    this.p("}");
                }
            }
            else if (primitive) {
                this.p("this.f_" + lname + " = other.f_" + lname + ";");
                this.p("this.b_" + lname + " = other.b_" + lname + ";");
            }
            else {
                this.p("this.f_" + lname + " = other.f_" + lname + ";");
                this.p("if( this.f_" + lname + " !=null ) {");
                this.indent();
                this.p("this.f_" + lname + " = this.f_" + lname + ".copy();");
                this.unindent();
                this.p("}");
            }
        }
        this.unindent();
        this.p("}");
        this.p();
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
    
    private void generateMethodParseFrom(final MessageDescriptor m, final String bufferClassName, final String beanClassName) {
        this.p("public static " + beanClassName + " parseUnframed(org.apache.activemq.protobuf.CodedInputStream data) throws org.apache.activemq.protobuf.InvalidProtocolBufferException, java.io.IOException {");
        this.indent();
        this.p("return new " + beanClassName + "().mergeUnframed(data);");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public static " + beanClassName + " parseUnframed(java.io.InputStream data) throws org.apache.activemq.protobuf.InvalidProtocolBufferException, java.io.IOException {");
        this.indent();
        this.p("return parseUnframed(new org.apache.activemq.protobuf.CodedInputStream(data));");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public static " + bufferClassName + " parseUnframed(org.apache.activemq.protobuf.Buffer data) throws org.apache.activemq.protobuf.InvalidProtocolBufferException {");
        this.indent();
        this.p("return new " + bufferClassName + "(data);");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public static " + bufferClassName + " parseUnframed(byte[] data) throws org.apache.activemq.protobuf.InvalidProtocolBufferException {");
        this.indent();
        this.p("return parseUnframed(new org.apache.activemq.protobuf.Buffer(data));");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public static " + bufferClassName + " parseFramed(org.apache.activemq.protobuf.CodedInputStream data) throws org.apache.activemq.protobuf.InvalidProtocolBufferException, java.io.IOException {");
        this.indent();
        this.p("int length = data.readRawVarint32();");
        this.p("int oldLimit = data.pushLimit(length);");
        this.p("" + bufferClassName + " rc = parseUnframed(data.readRawBytes(length));");
        this.p("data.popLimit(oldLimit);");
        this.p("return rc;");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public static " + bufferClassName + " parseFramed(org.apache.activemq.protobuf.Buffer data) throws org.apache.activemq.protobuf.InvalidProtocolBufferException {");
        this.indent();
        this.p("try {");
        this.indent();
        this.p("org.apache.activemq.protobuf.CodedInputStream input = new org.apache.activemq.protobuf.CodedInputStream(data);");
        this.p("" + bufferClassName + " rc = parseFramed(input);");
        this.p("input.checkLastTagWas(0);");
        this.p("return rc;");
        this.unindent();
        this.p("} catch (org.apache.activemq.protobuf.InvalidProtocolBufferException e) {");
        this.indent();
        this.p("throw e;");
        this.unindent();
        this.p("} catch (java.io.IOException e) {");
        this.indent();
        this.p("throw new RuntimeException(\"An IOException was thrown (should never happen in this method).\", e);");
        this.unindent();
        this.p("}");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public static " + bufferClassName + " parseFramed(byte[] data) throws org.apache.activemq.protobuf.InvalidProtocolBufferException {");
        this.indent();
        this.p("return parseFramed(new org.apache.activemq.protobuf.Buffer(data));");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public static " + bufferClassName + " parseFramed(java.io.InputStream data) throws org.apache.activemq.protobuf.InvalidProtocolBufferException, java.io.IOException {");
        this.indent();
        this.p("return parseUnframed(org.apache.activemq.protobuf.MessageBufferSupport.readFrame(data));");
        this.unindent();
        this.p("}");
        this.p();
    }
    
    private void generateBeanEquals(final MessageDescriptor m, final String className) {
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
        this.unindent();
        this.p("}");
        this.p("");
        this.p("public int hashCode() {");
        this.indent();
        final int hc = className.hashCode();
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
        this.unindent();
        this.p("}");
        this.p("");
    }
    
    private void generateBufferEquals(final MessageDescriptor m, final String className) {
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
        this.p("return toUnframedBuffer().equals(obj.toUnframedBuffer());");
        this.unindent();
        this.p("}");
        this.p("");
        this.p("public int hashCode() {");
        this.indent();
        final int hc = className.hashCode();
        this.p("if( hashCode==0 ) {");
        this.p("hashCode=" + hc + " ^ toUnframedBuffer().hashCode();");
        this.p("}");
        this.p("return hashCode;");
        this.unindent();
        this.p("}");
        this.p("");
    }
    
    private void generateMethodSerializedSize(final MessageDescriptor m) {
        this.p("public int serializedSizeFramed() {");
        this.indent();
        this.p("int t = serializedSizeUnframed();");
        this.p("return org.apache.activemq.protobuf.CodedOutputStream.computeRawVarint32Size(t) + t;");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public int serializedSizeUnframed() {");
        this.indent();
        this.p("if (buffer != null) {");
        this.indent();
        this.p("return buffer.length;");
        this.unindent();
        this.p("}");
        this.p("if (size != -1)");
        this.p("   return size;");
        this.p();
        this.p("size = 0;");
        for (final FieldDescriptor field : m.getFields().values()) {
            final String uname = uCamel(field.getName());
            String getter = "get" + uname + "()";
            final String type = this.javaType(field);
            if (!field.isRequired()) {
                this.p("if (has" + uname + "()) {");
                this.indent();
            }
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
                this.errors.add("This code generator does not support group fields.");
            }
            else {
                this.p("size += org.apache.activemq.protobuf.MessageBufferSupport.computeMessageSize(" + field.getTag() + ", " + getter + ".freeze());");
            }
            if (field.getRule() == FieldDescriptor.REPEATED_RULE) {
                this.unindent();
                this.p("}");
            }
            if (!field.isRequired()) {
                this.unindent();
                this.p("}");
            }
        }
        this.p("return size;");
        this.unindent();
        this.p("}");
        this.p();
    }
    
    private void generateMethodWrite(final MessageDescriptor m) {
        this.p("public org.apache.activemq.protobuf.Buffer toUnframedBuffer() {");
        this.indent();
        this.p("if( buffer !=null ) {");
        this.indent();
        this.p("return buffer;");
        this.unindent();
        this.p("}");
        this.p("return org.apache.activemq.protobuf.MessageBufferSupport.toUnframedBuffer(this);");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public org.apache.activemq.protobuf.Buffer toFramedBuffer() {");
        this.indent();
        this.p("return org.apache.activemq.protobuf.MessageBufferSupport.toFramedBuffer(this);");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public byte[] toUnframedByteArray() {");
        this.indent();
        this.p("return toUnframedBuffer().toByteArray();");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public byte[] toFramedByteArray() {");
        this.indent();
        this.p("return toFramedBuffer().toByteArray();");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public void writeFramed(org.apache.activemq.protobuf.CodedOutputStream output) throws java.io.IOException {");
        this.indent();
        this.p("output.writeRawVarint32(serializedSizeUnframed());");
        this.p("writeUnframed(output);");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public void writeFramed(java.io.OutputStream output) throws java.io.IOException {");
        this.indent();
        this.p("org.apache.activemq.protobuf.CodedOutputStream codedOutput = new org.apache.activemq.protobuf.CodedOutputStream(output);");
        this.p("writeFramed(codedOutput);");
        this.p("codedOutput.flush();");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public void writeUnframed(java.io.OutputStream output) throws java.io.IOException {");
        this.indent();
        this.p("org.apache.activemq.protobuf.CodedOutputStream codedOutput = new org.apache.activemq.protobuf.CodedOutputStream(output);");
        this.p("writeUnframed(codedOutput);");
        this.p("codedOutput.flush();");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public void writeUnframed(org.apache.activemq.protobuf.CodedOutputStream output) throws java.io.IOException {");
        this.indent();
        this.p("if (buffer == null) {");
        this.indent();
        this.p("int size = serializedSizeUnframed();");
        this.p("buffer = output.getNextBuffer(size);");
        this.p("org.apache.activemq.protobuf.CodedOutputStream original=null;");
        this.p("if( buffer == null ) {");
        this.indent();
        this.p("buffer = new org.apache.activemq.protobuf.Buffer(new byte[size]);");
        this.p("original = output;");
        this.p("output = new org.apache.activemq.protobuf.CodedOutputStream(buffer);");
        this.unindent();
        this.p("}");
        for (final FieldDescriptor field : m.getFields().values()) {
            final String uname = uCamel(field.getName());
            String getter = "bean.get" + uname + "()";
            final String type = this.javaType(field);
            if (!field.isRequired()) {
                this.p("if (bean.has" + uname + "()) {");
                this.indent();
            }
            if (field.getRule() == FieldDescriptor.REPEATED_RULE) {
                this.p("for (" + type + " i : bean.get" + uname + "List()) {");
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
                this.errors.add("This code generator does not support group fields.");
            }
            else {
                this.p("org.apache.activemq.protobuf.MessageBufferSupport.writeMessage(output, " + field.getTag() + ", " + getter + ".freeze());");
            }
            if (field.getRule() == FieldDescriptor.REPEATED_RULE) {
                this.unindent();
                this.p("}");
            }
            if (!field.isRequired()) {
                this.unindent();
                this.p("}");
            }
        }
        this.p("if( original !=null ) {");
        this.indent();
        this.p("output.checkNoSpaceLeft();");
        this.p("output = original;");
        this.p("output.writeRawBytes(buffer);");
        this.unindent();
        this.p("}");
        this.unindent();
        this.p("} else {");
        this.indent();
        this.p("output.writeRawBytes(buffer);");
        this.unindent();
        this.p("}");
        this.unindent();
        this.p("}");
        this.p();
    }
    
    private void generateMethodMergeFromStream(final MessageDescriptor m, final String className) {
        this.p("public " + className + " mergeUnframed(java.io.InputStream input) throws java.io.IOException {");
        this.indent();
        this.p("return mergeUnframed(new org.apache.activemq.protobuf.CodedInputStream(input));");
        this.unindent();
        this.p("}");
        this.p();
        this.p("public " + className + " mergeUnframed(org.apache.activemq.protobuf.CodedInputStream input) throws java.io.IOException {");
        this.indent();
        this.p("copyCheck();");
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
                setter = "create" + uname + "List().add";
            }
            if (field.getType() == FieldDescriptor.STRING_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 2) + ":");
                this.indent();
                this.p(setter + "(input.readString());");
            }
            else if (field.getType() == FieldDescriptor.BYTES_TYPE) {
                this.p("case " + WireFormat.makeTag(field.getTag(), 2) + ":");
                this.indent();
                final String override = this.getOption(field.getOptions(), "java_override_type", null);
                if ("AsciiBuffer".equals(override)) {
                    this.p(setter + "(new org.apache.activemq.protobuf.AsciiBuffer(input.readBytes()));");
                }
                else if ("UTF8Buffer".equals(override)) {
                    this.p(setter + "(new org.apache.activemq.protobuf.UTF8Buffer(input.readBytes()));");
                }
                else {
                    this.p(setter + "(input.readBytes());");
                }
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
                this.errors.add("This code generator does not support group fields.");
            }
            else {
                this.p("case " + WireFormat.makeTag(field.getTag(), 2) + ":");
                this.indent();
                final String type = this.javaType(field);
                if (repeated) {
                    this.p(setter + "(" + this.javaRelatedType(type, "Buffer") + ".parseFramed(input));");
                }
                else {
                    this.p("if (has" + uname + "()) {");
                    this.indent();
                    this.p("set" + uname + "(get" + uname + "().copy().mergeFrom(" + this.javaRelatedType(type, "Buffer") + ".parseFramed(input)));");
                    this.unindent();
                    this.p("} else {");
                    this.indent();
                    this.p(setter + "(" + this.javaRelatedType(type, "Buffer") + ".parseFramed(input));");
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
        this.p("public " + className + "Bean mergeFrom(" + className + " other) {");
        this.indent();
        this.p("copyCheck();");
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
                    this.p("get" + uname + "List().add(element.copy());");
                    this.unindent();
                    this.p("}");
                }
                else {
                    this.p("if (has" + uname + "()) {");
                    this.indent();
                    this.p("set" + uname + "(get" + uname + "().copy().mergeFrom(other.get" + uname + "()));");
                    this.unindent();
                    this.p("} else {");
                    this.indent();
                    this.p("set" + uname + "(other.get" + uname + "().copy());");
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
        for (final FieldDescriptor field : m.getFields().values()) {
            final String uname = uCamel(field.getName());
            this.p("clear" + uname + "();");
        }
        this.unindent();
        this.p("}");
        this.p();
    }
    
    private void generateReadWriteExternal(final MessageDescriptor m) {
        this.p("public void readExternal(java.io.DataInput in) throws java.io.IOException {");
        this.indent();
        this.p("assert frozen==null : org.apache.activemq.protobuf.MessageBufferSupport.FORZEN_ERROR_MESSAGE;");
        this.p("bean = this;");
        this.p("frozen = null;");
        for (final FieldDescriptor field : m.getFields().values()) {
            final String lname = lCamel(field.getName());
            final String type = this.javaType(field);
            final boolean repeated = field.getRule() == FieldDescriptor.REPEATED_RULE;
            if (repeated) {
                this.p("{");
                this.indent();
                this.p("int size = in.readShort();");
                this.p("if( size>=0 ) {");
                this.indent();
                this.p("f_" + lname + " = new java.util.ArrayList<" + this.javaCollectionType(field) + ">(size);");
                this.p("for(int i=0; i<size; i++) {");
                this.indent();
                if (field.isInteger32Type()) {
                    this.p("f_" + lname + ".add(in.readInt());");
                }
                else if (field.isInteger64Type()) {
                    this.p("f_" + lname + ".add(in.readLong());");
                }
                else if (field.getType() == FieldDescriptor.DOUBLE_TYPE) {
                    this.p("f_" + lname + ".add(in.readDouble());");
                }
                else if (field.getType() == FieldDescriptor.FLOAT_TYPE) {
                    this.p("f_" + lname + ".add(in.readFloat());");
                }
                else if (field.getType() == FieldDescriptor.BOOL_TYPE) {
                    this.p("f_" + lname + ".add(in.readBoolean());");
                }
                else if (field.getType() == FieldDescriptor.STRING_TYPE) {
                    this.p("f_" + lname + ".add(in.readUTF());");
                }
                else if (field.getType() == FieldDescriptor.BYTES_TYPE) {
                    this.p("byte b[] = new byte[in.readInt()];");
                    this.p("in.readFully(b);");
                    this.p("f_" + lname + ".add(new " + type + "(b));");
                }
                else if (field.getTypeDescriptor().isEnum()) {
                    this.p("f_" + lname + ".add(" + type + ".valueOf(in.readShort()));");
                }
                else {
                    this.p("" + this.javaRelatedType(type, "Bean") + " o = new " + this.javaRelatedType(type, "Bean") + "();");
                    this.p("o.readExternal(in);");
                    this.p("f_" + lname + ".add(o);");
                }
                this.unindent();
                this.p("}");
                this.unindent();
                this.p("} else {");
                this.indent();
                this.p("f_" + lname + " = null;");
                this.unindent();
                this.p("}");
                this.unindent();
                this.p("}");
            }
            else if (field.isInteger32Type()) {
                this.p("f_" + lname + " = in.readInt();");
                this.p("b_" + lname + " = true;");
            }
            else if (field.isInteger64Type()) {
                this.p("f_" + lname + " = in.readLong();");
                this.p("b_" + lname + " = true;");
            }
            else if (field.getType() == FieldDescriptor.DOUBLE_TYPE) {
                this.p("f_" + lname + " = in.readDouble();");
                this.p("b_" + lname + " = true;");
            }
            else if (field.getType() == FieldDescriptor.FLOAT_TYPE) {
                this.p("f_" + lname + " = in.readFloat();");
                this.p("b_" + lname + " = true;");
            }
            else if (field.getType() == FieldDescriptor.BOOL_TYPE) {
                this.p("f_" + lname + " = in.readBoolean();");
                this.p("b_" + lname + " = true;");
            }
            else if (field.getType() == FieldDescriptor.STRING_TYPE) {
                this.p("if( in.readBoolean() ) {");
                this.indent();
                this.p("f_" + lname + " = in.readUTF();");
                this.p("b_" + lname + " = true;");
                this.unindent();
                this.p("} else {");
                this.indent();
                this.p("f_" + lname + " = null;");
                this.p("b_" + lname + " = false;");
                this.unindent();
                this.p("}");
            }
            else if (field.getType() == FieldDescriptor.BYTES_TYPE) {
                this.p("{");
                this.indent();
                this.p("int size = in.readInt();");
                this.p("if( size>=0 ) {");
                this.indent();
                this.p("byte b[] = new byte[size];");
                this.p("in.readFully(b);");
                this.p("f_" + lname + " = new " + type + "(b);");
                this.p("b_" + lname + " = true;");
                this.unindent();
                this.p("} else {");
                this.indent();
                this.p("f_" + lname + " = null;");
                this.p("b_" + lname + " = false;");
                this.unindent();
                this.p("}");
                this.unindent();
                this.p("}");
            }
            else if (field.getTypeDescriptor().isEnum()) {
                this.p("if( in.readBoolean() ) {");
                this.indent();
                this.p("f_" + lname + " = " + type + ".valueOf(in.readShort());");
                this.p("b_" + lname + " = true;");
                this.unindent();
                this.p("} else {");
                this.indent();
                this.p("f_" + lname + " = null;");
                this.p("b_" + lname + " = false;");
                this.unindent();
                this.p("}");
            }
            else {
                this.p("if( in.readBoolean() ) {");
                this.indent();
                this.p("" + this.javaRelatedType(type, "Bean") + " o = new " + this.javaRelatedType(type, "Bean") + "();");
                this.p("o.readExternal(in);");
                this.p("f_" + lname + " = o;");
                this.unindent();
                this.p("} else {");
                this.indent();
                this.p("f_" + lname + " = null;");
                this.unindent();
                this.p("}");
            }
        }
        this.unindent();
        this.p("}");
        this.p();
        this.p("public void writeExternal(java.io.DataOutput out) throws java.io.IOException {");
        this.indent();
        for (final FieldDescriptor field : m.getFields().values()) {
            final String lname = lCamel(field.getName());
            final boolean repeated2 = field.getRule() == FieldDescriptor.REPEATED_RULE;
            if (repeated2) {
                this.p("if( bean.f_" + lname + "!=null ) {");
                this.indent();
                this.p("out.writeShort(bean.f_" + lname + ".size());");
                this.p("for(" + this.javaCollectionType(field) + " o : bean.f_" + lname + ") {");
                this.indent();
                if (field.isInteger32Type()) {
                    this.p("out.writeInt(o);");
                }
                else if (field.isInteger64Type()) {
                    this.p("out.writeLong(o);");
                }
                else if (field.getType() == FieldDescriptor.DOUBLE_TYPE) {
                    this.p("out.writeDouble(o);");
                }
                else if (field.getType() == FieldDescriptor.FLOAT_TYPE) {
                    this.p("out.writeFloat(o);");
                }
                else if (field.getType() == FieldDescriptor.BOOL_TYPE) {
                    this.p("out.writeBoolean(o);");
                }
                else if (field.getType() == FieldDescriptor.STRING_TYPE) {
                    this.p("out.writeUTF(o);");
                }
                else if (field.getType() == FieldDescriptor.BYTES_TYPE) {
                    this.p("out.writeInt(o.getLength());");
                    this.p("out.write(o.getData(), o.getOffset(), o.getLength());");
                }
                else if (field.getTypeDescriptor().isEnum()) {
                    this.p("out.writeShort(o.getNumber());");
                }
                else {
                    this.p("o.copy().writeExternal(out);");
                }
                this.unindent();
                this.p("}");
                this.unindent();
                this.p("} else {");
                this.indent();
                this.p("out.writeShort(-1);");
                this.unindent();
                this.p("}");
            }
            else if (field.isInteger32Type()) {
                this.p("out.writeInt(bean.f_" + lname + ");");
            }
            else if (field.isInteger64Type()) {
                this.p("out.writeLong(bean.f_" + lname + ");");
            }
            else if (field.getType() == FieldDescriptor.DOUBLE_TYPE) {
                this.p("out.writeDouble(bean.f_" + lname + ");");
            }
            else if (field.getType() == FieldDescriptor.FLOAT_TYPE) {
                this.p("out.writeFloat(bean.f_" + lname + ");");
            }
            else if (field.getType() == FieldDescriptor.BOOL_TYPE) {
                this.p("out.writeBoolean(bean.f_" + lname + ");");
            }
            else if (field.getType() == FieldDescriptor.STRING_TYPE) {
                this.p("if( bean.f_" + lname + "!=null ) {");
                this.indent();
                this.p("out.writeBoolean(true);");
                this.p("out.writeUTF(bean.f_" + lname + ");");
                this.unindent();
                this.p("} else {");
                this.indent();
                this.p("out.writeBoolean(false);");
                this.unindent();
                this.p("}");
            }
            else if (field.getType() == FieldDescriptor.BYTES_TYPE) {
                this.p("if( bean.f_" + lname + "!=null ) {");
                this.indent();
                this.p("out.writeInt(bean.f_" + lname + ".getLength());");
                this.p("out.write(bean.f_" + lname + ".getData(), bean.f_" + lname + ".getOffset(), bean.f_" + lname + ".getLength());");
                this.unindent();
                this.p("} else {");
                this.indent();
                this.p("out.writeInt(-1);");
                this.unindent();
                this.p("}");
            }
            else if (field.getTypeDescriptor().isEnum()) {
                this.p("if( bean.f_" + lname + "!=null ) {");
                this.indent();
                this.p("out.writeBoolean(true);");
                this.p("out.writeShort(bean.f_" + lname + ".getNumber());");
                this.unindent();
                this.p("} else {");
                this.indent();
                this.p("out.writeBoolean(false);");
                this.unindent();
                this.p("}");
            }
            else {
                this.p("if( bean.f_" + lname + "!=null ) {");
                this.indent();
                this.p("out.writeBoolean(true);");
                this.p("bean.f_" + lname + ".copy().writeExternal(out);");
                this.unindent();
                this.p("} else {");
                this.indent();
                this.p("out.writeBoolean(false);");
                this.unindent();
                this.p("}");
            }
        }
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
    
    private void generateBufferGetters(final FieldDescriptor field) {
        final String uname = uCamel(field.getName());
        final String type = (field.getRule() == FieldDescriptor.REPEATED_RULE) ? this.javaCollectionType(field) : this.javaType(field);
        final boolean repeated = field.getRule() == FieldDescriptor.REPEATED_RULE;
        this.p("// " + field.getRule() + " " + field.getType() + " " + field.getName() + " = " + field.getTag() + ";");
        if (repeated) {
            this.p("public boolean has" + uname + "() {");
            this.indent();
            this.p("return bean().has" + uname + "();");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public java.util.List<" + type + "> get" + uname + "List() {");
            this.indent();
            this.p("return bean().get" + uname + "List();");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public int get" + uname + "Count() {");
            this.indent();
            this.p("return bean().get" + uname + "Count();");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public " + type + " get" + uname + "(int index) {");
            this.indent();
            this.p("return bean().get" + uname + "(index);");
            this.unindent();
            this.p("}");
            this.p();
        }
        else {
            this.p("public boolean has" + uname + "() {");
            this.indent();
            this.p("return bean().has" + uname + "();");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public " + type + " get" + uname + "() {");
            this.indent();
            this.p("return bean().get" + uname + "();");
            this.unindent();
            this.p("}");
            this.p();
        }
    }
    
    private void generateFieldGetterSignatures(final FieldDescriptor field) {
        final String uname = uCamel(field.getName());
        final String type = (field.getRule() == FieldDescriptor.REPEATED_RULE) ? this.javaCollectionType(field) : this.javaType(field);
        final boolean repeated = field.getRule() == FieldDescriptor.REPEATED_RULE;
        this.p("// " + field.getRule() + " " + field.getType() + " " + field.getName() + " = " + field.getTag() + ";");
        if (repeated) {
            this.p("public boolean has" + uname + "();");
            this.p("public java.util.List<" + type + "> get" + uname + "List();");
            this.p("public int get" + uname + "Count();");
            this.p("public " + type + " get" + uname + "(int index);");
        }
        else {
            this.p("public boolean has" + uname + "();");
            this.p("public " + type + " get" + uname + "();");
        }
    }
    
    private void generateFieldAccessor(final String beanClassName, final FieldDescriptor field) {
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
            this.p("return bean.f_" + lname + "!=null && !bean.f_" + lname + ".isEmpty();");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public java.util.List<" + type + "> get" + uname + "List() {");
            this.indent();
            this.p("return bean.f_" + lname + ";");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public java.util.List<" + type + "> create" + uname + "List() {");
            this.indent();
            this.p("copyCheck();");
            this.p("if( this.f_" + lname + " == null ) {");
            this.indent();
            this.p("this.f_" + lname + " = new java.util.ArrayList<" + type + ">();");
            this.unindent();
            this.p("}");
            this.p("return bean.f_" + lname + ";");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public " + beanClassName + " set" + uname + "List(java.util.List<" + type + "> " + lname + ") {");
            this.indent();
            this.p("copyCheck();");
            this.p("this.f_" + lname + " = " + lname + ";");
            this.p("return this;");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public int get" + uname + "Count() {");
            this.indent();
            this.p("if( bean.f_" + lname + " == null ) {");
            this.indent();
            this.p("return 0;");
            this.unindent();
            this.p("}");
            this.p("return bean.f_" + lname + ".size();");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public " + type + " get" + uname + "(int index) {");
            this.indent();
            this.p("if( bean.f_" + lname + " == null ) {");
            this.indent();
            this.p("return null;");
            this.unindent();
            this.p("}");
            this.p("return bean.f_" + lname + ".get(index);");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public " + beanClassName + " set" + uname + "(int index, " + type + " value) {");
            this.indent();
            this.p("this.create" + uname + "List().set(index, value);");
            this.p("return this;");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public " + beanClassName + " add" + uname + "(" + type + " value) {");
            this.indent();
            this.p("this.create" + uname + "List().add(value);");
            this.p("return this;");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public " + beanClassName + " addAll" + uname + "(java.lang.Iterable<? extends " + type + "> collection) {");
            this.indent();
            this.p("org.apache.activemq.protobuf.MessageBufferSupport.addAll(collection, this.create" + uname + "List());");
            this.p("return this;");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public void clear" + uname + "() {");
            this.indent();
            this.p("copyCheck();");
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
            if (primitive) {
                this.p("return bean.b_" + lname + ";");
            }
            else {
                this.p("return bean.f_" + lname + "!=null;");
            }
            this.unindent();
            this.p("}");
            this.p();
            this.p("public " + type + " get" + uname + "() {");
            this.indent();
            this.p("return bean.f_" + lname + ";");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public " + beanClassName + " set" + uname + "(" + type + " " + lname + ") {");
            this.indent();
            this.p("copyCheck();");
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
            this.p("return this;");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public void clear" + uname + "() {");
            this.indent();
            this.p("copyCheck();");
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
                return "new " + this.javaType(field) + "(" + this.asJavaString(defaultOption.getValue()) + ")";
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
                    sb.append(AltJavaGenerator.HEX_TABLE[b >>> 12 & 0xF]);
                    sb.append(AltJavaGenerator.HEX_TABLE[b >>> 8 & 0xF]);
                    sb.append(AltJavaGenerator.HEX_TABLE[b >>> 4 & 0xF]);
                    sb.append(AltJavaGenerator.HEX_TABLE[b & '\u000f']);
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
            this.p("public interface " + uname + "Creatable {");
            this.indent();
            this.p("" + uname + " to" + uname + "();");
            this.unindent();
            this.p("}");
            this.p();
            this.p("public " + uname + "Creatable createBean() {");
            this.indent();
            this.p("switch (this) {");
            this.indent();
            for (final EnumFieldDescriptor field3 : ed.getFields().values()) {
                this.p("case " + field3.getName() + ":");
                final String type = field3.getAssociatedType().getName();
                this.p("   return new " + this.javaRelatedType(type, "Bean") + "();");
            }
            this.p("default:");
            this.p("   return null;");
            this.unindent();
            this.p("}");
            this.unindent();
            this.p("}");
            this.p();
            this.generateParseDelegate(ed, "parseUnframed", "org.apache.activemq.protobuf.Buffer", "org.apache.activemq.protobuf.InvalidProtocolBufferException");
            this.generateParseDelegate(ed, "parseFramed", "org.apache.activemq.protobuf.Buffer", "org.apache.activemq.protobuf.InvalidProtocolBufferException");
            this.generateParseDelegate(ed, "parseUnframed", "byte[]", "org.apache.activemq.protobuf.InvalidProtocolBufferException");
            this.generateParseDelegate(ed, "parseFramed", "byte[]", "org.apache.activemq.protobuf.InvalidProtocolBufferException");
            this.generateParseDelegate(ed, "parseFramed", "org.apache.activemq.protobuf.CodedInputStream", "org.apache.activemq.protobuf.InvalidProtocolBufferException, java.io.IOException");
            this.generateParseDelegate(ed, "parseFramed", "java.io.InputStream", "org.apache.activemq.protobuf.InvalidProtocolBufferException, java.io.IOException");
        }
        this.unindent();
        this.p("}");
        this.p();
    }
    
    private void generateParseDelegate(final EnumDescriptor descriptor, final String methodName, final String inputType, final String exceptions) {
        this.p("public org.apache.activemq.protobuf.MessageBuffer " + methodName + "(" + inputType + " data) throws " + exceptions + " {");
        this.indent();
        this.p("switch (this) {");
        this.indent();
        for (final EnumFieldDescriptor field : descriptor.getFields().values()) {
            this.p("case " + field.getName() + ":");
            final String type = this.constantToUCamelCase(field.getName());
            this.p("   return " + this.javaRelatedType(type, "Buffer") + "." + methodName + "(data);");
        }
        this.p("default:");
        this.p("   return null;");
        this.unindent();
        this.p("}");
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
            final String override = this.getOption(field.getOptions(), "java_override_type", null);
            if ("AsciiBuffer".equals(override)) {
                return "org.apache.activemq.protobuf.AsciiBuffer";
            }
            if ("UTF8Buffer".equals(override)) {
                return "org.apache.activemq.protobuf.UTF8Buffer";
            }
            return "org.apache.activemq.protobuf.Buffer";
        }
        else {
            if (field.getType() == FieldDescriptor.BOOL_TYPE) {
                return "java.lang.Boolean";
            }
            final TypeDescriptor descriptor = field.getTypeDescriptor();
            return this.javaType(descriptor);
        }
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
            final String override = this.getOption(field.getOptions(), "java_override_type", null);
            if ("AsciiBuffer".equals(override)) {
                return "org.apache.activemq.protobuf.AsciiBuffer";
            }
            if ("UTF8Buffer".equals(override)) {
                return "org.apache.activemq.protobuf.UTF8Buffer";
            }
            return "org.apache.activemq.protobuf.Buffer";
        }
        else {
            if (field.getType() == FieldDescriptor.BOOL_TYPE) {
                return "boolean";
            }
            final TypeDescriptor descriptor = field.getTypeDescriptor();
            return this.javaType(descriptor);
        }
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
    
    private String javaRelatedType(final String type, final String suffix) {
        final int ix = type.lastIndexOf(".");
        if (ix == -1) {
            return type + "." + type + suffix;
        }
        return type + "." + type.substring(ix + 1) + suffix;
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
