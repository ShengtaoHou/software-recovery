// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

public abstract class BinaryExpression implements Expression
{
    protected Expression left;
    protected Expression right;
    
    public BinaryExpression(final Expression left, final Expression right) {
        this.left = left;
        this.right = right;
    }
    
    public Expression getLeft() {
        return this.left;
    }
    
    public Expression getRight() {
        return this.right;
    }
    
    @Override
    public String toString() {
        return "(" + this.left.toString() + " " + this.getExpressionSymbol() + " " + this.right.toString() + ")";
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && this.getClass().equals(o.getClass()) && this.toString().equals(o.toString());
    }
    
    public abstract String getExpressionSymbol();
    
    public void setRight(final Expression expression) {
        this.right = expression;
    }
    
    public void setLeft(final Expression expression) {
        this.left = expression;
    }
}
