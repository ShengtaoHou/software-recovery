// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.io.OutputStream;
import java.io.PrintWriter;

public class IndentPrinter
{
    private int indentLevel;
    private String indent;
    private PrintWriter out;
    
    public IndentPrinter() {
        this(new PrintWriter(System.out), "  ");
    }
    
    public IndentPrinter(final PrintWriter out) {
        this(out, "  ");
    }
    
    public IndentPrinter(final PrintWriter out, final String indent) {
        this.out = out;
        this.indent = indent;
    }
    
    public void println(final Object value) {
        this.out.print(value.toString());
        this.out.println();
    }
    
    public void println(final String text) {
        this.out.print(text);
        this.out.println();
    }
    
    public void print(final String text) {
        this.out.print(text);
    }
    
    public void printIndent() {
        for (int i = 0; i < this.indentLevel; ++i) {
            this.out.print(this.indent);
        }
    }
    
    public void println() {
        this.out.println();
    }
    
    public void incrementIndent() {
        ++this.indentLevel;
    }
    
    public void decrementIndent() {
        --this.indentLevel;
    }
    
    public int getIndentLevel() {
        return this.indentLevel;
    }
    
    public void setIndentLevel(final int indentLevel) {
        this.indentLevel = indentLevel;
    }
    
    public void flush() {
        this.out.flush();
    }
}
