// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.index;

import java.util.Iterator;
import java.util.List;

public interface BTreeVisitor<Key, Value>
{
    boolean isInterestedInKeysBetween(final Key p0, final Key p1);
    
    void visit(final List<Key> p0, final List<Value> p1);
    
    public abstract static class PredicateVisitor<Key, Value> implements BTreeVisitor<Key, Value>, Predicate<Key>
    {
        @Override
        public void visit(final List<Key> keys, final List<Value> values) {
            for (int i = 0; i < keys.size(); ++i) {
                final Key key = keys.get(i);
                if (this.isInterestedInKey(key)) {
                    this.matched(key, values.get(i));
                }
            }
        }
        
        protected void matched(final Key key, final Value value) {
        }
    }
    
    public static class OrVisitor<Key, Value> extends PredicateVisitor<Key, Value>
    {
        private final List<Predicate<Key>> conditions;
        
        public OrVisitor(final List<Predicate<Key>> conditions) {
            this.conditions = conditions;
        }
        
        @Override
        public boolean isInterestedInKeysBetween(final Key first, final Key second) {
            for (final Predicate<Key> condition : this.conditions) {
                if (condition.isInterestedInKeysBetween(first, second)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean isInterestedInKey(final Key key) {
            for (final Predicate<Key> condition : this.conditions) {
                if (condition.isInterestedInKey(key)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (final Predicate<Key> condition : this.conditions) {
                if (!first) {
                    sb.append(" OR ");
                }
                first = false;
                sb.append("(");
                sb.append(condition);
                sb.append(")");
            }
            return sb.toString();
        }
    }
    
    public static class AndVisitor<Key, Value> extends PredicateVisitor<Key, Value>
    {
        private final List<Predicate<Key>> conditions;
        
        public AndVisitor(final List<Predicate<Key>> conditions) {
            this.conditions = conditions;
        }
        
        @Override
        public boolean isInterestedInKeysBetween(final Key first, final Key second) {
            for (final Predicate<Key> condition : this.conditions) {
                if (!condition.isInterestedInKeysBetween(first, second)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public boolean isInterestedInKey(final Key key) {
            for (final Predicate<Key> condition : this.conditions) {
                if (!condition.isInterestedInKey(key)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (final Predicate<Key> condition : this.conditions) {
                if (!first) {
                    sb.append(" AND ");
                }
                first = false;
                sb.append("(");
                sb.append(condition);
                sb.append(")");
            }
            return sb.toString();
        }
    }
    
    public static class BetweenVisitor<Key extends Comparable<Key>, Value> extends PredicateVisitor<Key, Value>
    {
        private final Key first;
        private final Key last;
        
        public BetweenVisitor(final Key first, final Key last) {
            this.first = first;
            this.last = last;
        }
        
        @Override
        public boolean isInterestedInKeysBetween(final Key first, final Key second) {
            return (second == null || second.compareTo(this.first) >= 0) && (first == null || first.compareTo(this.last) < 0);
        }
        
        @Override
        public boolean isInterestedInKey(final Key key) {
            return key.compareTo(this.first) >= 0 && key.compareTo(this.last) < 0;
        }
        
        @Override
        public String toString() {
            return this.first + " <= key < " + this.last;
        }
    }
    
    public static class GTVisitor<Key extends Comparable<Key>, Value> extends PredicateVisitor<Key, Value>
    {
        private final Key value;
        
        public GTVisitor(final Key value) {
            this.value = value;
        }
        
        @Override
        public boolean isInterestedInKeysBetween(final Key first, final Key second) {
            return second == null || second.compareTo(this.value) > 0;
        }
        
        @Override
        public boolean isInterestedInKey(final Key key) {
            return key.compareTo(this.value) > 0;
        }
        
        @Override
        public String toString() {
            return "key > " + this.value;
        }
    }
    
    public static class GTEVisitor<Key extends Comparable<Key>, Value> extends PredicateVisitor<Key, Value>
    {
        private final Key value;
        
        public GTEVisitor(final Key value) {
            this.value = value;
        }
        
        @Override
        public boolean isInterestedInKeysBetween(final Key first, final Key second) {
            return second == null || second.compareTo(this.value) >= 0;
        }
        
        @Override
        public boolean isInterestedInKey(final Key key) {
            return key.compareTo(this.value) >= 0;
        }
        
        @Override
        public String toString() {
            return "key >= " + this.value;
        }
    }
    
    public static class LTVisitor<Key extends Comparable<Key>, Value> extends PredicateVisitor<Key, Value>
    {
        private final Key value;
        
        public LTVisitor(final Key value) {
            this.value = value;
        }
        
        @Override
        public boolean isInterestedInKeysBetween(final Key first, final Key second) {
            return first == null || first.compareTo(this.value) < 0;
        }
        
        @Override
        public boolean isInterestedInKey(final Key key) {
            return key.compareTo(this.value) < 0;
        }
        
        @Override
        public String toString() {
            return "key < " + this.value;
        }
    }
    
    public static class LTEVisitor<Key extends Comparable<Key>, Value> extends PredicateVisitor<Key, Value>
    {
        private final Key value;
        
        public LTEVisitor(final Key value) {
            this.value = value;
        }
        
        @Override
        public boolean isInterestedInKeysBetween(final Key first, final Key second) {
            return first == null || first.compareTo(this.value) <= 0;
        }
        
        @Override
        public boolean isInterestedInKey(final Key key) {
            return key.compareTo(this.value) <= 0;
        }
        
        @Override
        public String toString() {
            return "key <= " + this.value;
        }
    }
    
    public interface Predicate<Key>
    {
        boolean isInterestedInKeysBetween(final Key p0, final Key p1);
        
        boolean isInterestedInKey(final Key p0);
    }
}
