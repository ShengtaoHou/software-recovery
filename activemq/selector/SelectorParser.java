// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.selector;

import java.util.Collections;
import org.apache.activemq.util.LRUCache;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import org.apache.activemq.filter.ConstantExpression;
import org.apache.activemq.filter.FunctionCallExpression;
import org.apache.activemq.filter.ArithmeticExpression;
import java.util.List;
import java.util.ArrayList;
import org.apache.activemq.filter.LogicExpression;
import org.apache.activemq.filter.UnaryExpression;
import org.apache.activemq.filter.PropertyExpression;
import org.apache.activemq.filter.Expression;
import java.io.Reader;
import java.io.StringReader;
import org.apache.activemq.filter.ComparisonExpression;
import javax.jms.InvalidSelectorException;
import org.apache.activemq.filter.BooleanExpression;
import java.util.Map;

public class SelectorParser implements SelectorParserConstants
{
    private static final Map cache;
    private static final String CONVERT_STRING_EXPRESSIONS_PREFIX = "convert_string_expressions:";
    private String sql;
    public SelectorParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private final LookaheadSuccess jj_ls;
    
    public static BooleanExpression parse(String sql) throws InvalidSelectorException {
        final Object result = SelectorParser.cache.get(sql);
        if (result instanceof InvalidSelectorException) {
            throw (InvalidSelectorException)result;
        }
        if (result instanceof BooleanExpression) {
            return (BooleanExpression)result;
        }
        boolean convertStringExpressions = false;
        if (sql.startsWith("convert_string_expressions:")) {
            convertStringExpressions = true;
            sql = sql.substring("convert_string_expressions:".length());
        }
        if (convertStringExpressions) {
            ComparisonExpression.CONVERT_STRING_EXPRESSIONS.set(true);
        }
        try {
            final BooleanExpression e = new SelectorParser(sql).parse();
            SelectorParser.cache.put(sql, e);
            return e;
        }
        catch (InvalidSelectorException t) {
            SelectorParser.cache.put(sql, t);
            throw t;
        }
        finally {
            if (convertStringExpressions) {
                ComparisonExpression.CONVERT_STRING_EXPRESSIONS.remove();
            }
        }
    }
    
    public static void clearCache() {
        SelectorParser.cache.clear();
    }
    
    protected SelectorParser(final String sql) {
        this(new StringReader(sql));
        this.sql = sql;
    }
    
    protected BooleanExpression parse() throws InvalidSelectorException {
        try {
            return this.JmsSelector();
        }
        catch (Throwable e) {
            throw (InvalidSelectorException)new InvalidSelectorException(this.sql).initCause(e);
        }
    }
    
    private BooleanExpression asBooleanExpression(final Expression value) throws ParseException {
        if (value instanceof BooleanExpression) {
            return (BooleanExpression)value;
        }
        if (value instanceof PropertyExpression) {
            return UnaryExpression.createBooleanCast(value);
        }
        throw new ParseException("Expression will not result in a boolean value: " + value);
    }
    
    public final BooleanExpression JmsSelector() throws ParseException {
        Expression left = null;
        left = this.orExpression();
        return this.asBooleanExpression(left);
    }
    
    public final Expression orExpression() throws ParseException {
        Expression left = this.andExpression();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 10: {
                    this.jj_consume_token(10);
                    final Expression right = this.andExpression();
                    left = LogicExpression.createOR(this.asBooleanExpression(left), this.asBooleanExpression(right));
                    continue;
                }
                default: {
                    return left;
                }
            }
        }
    }
    
    public final Expression andExpression() throws ParseException {
        Expression left = this.equalityExpression();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 9: {
                    this.jj_consume_token(9);
                    final Expression right = this.equalityExpression();
                    left = LogicExpression.createAND(this.asBooleanExpression(left), this.asBooleanExpression(right));
                    continue;
                }
                default: {
                    return left;
                }
            }
        }
    }
    
    public final Expression equalityExpression() throws ParseException {
        Expression left = this.comparisonExpression();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 15:
                case 28:
                case 29: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 28: {
                            this.jj_consume_token(28);
                            final Expression right = this.comparisonExpression();
                            left = ComparisonExpression.createEqual(left, right);
                            continue;
                        }
                        case 29: {
                            this.jj_consume_token(29);
                            final Expression right = this.comparisonExpression();
                            left = ComparisonExpression.createNotEqual(left, right);
                            continue;
                        }
                        default: {
                            if (this.jj_2_1(2)) {
                                this.jj_consume_token(15);
                                this.jj_consume_token(18);
                                left = ComparisonExpression.createIsNull(left);
                                continue;
                            }
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 15: {
                                    this.jj_consume_token(15);
                                    this.jj_consume_token(8);
                                    this.jj_consume_token(18);
                                    left = ComparisonExpression.createIsNotNull(left);
                                    continue;
                                }
                                default: {
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                default: {
                    return left;
                }
            }
        }
    }
    
    public final Expression comparisonExpression() throws ParseException {
        Expression left = this.addExpression();
    Label_0005:
        while (true) {
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 8:
                    case 11:
                    case 12:
                    case 14:
                    case 30:
                    case 31:
                    case 32:
                    case 33: {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                            case 30: {
                                this.jj_consume_token(30);
                                final Expression right = this.addExpression();
                                left = ComparisonExpression.createGreaterThan(left, right);
                                continue;
                            }
                            case 31: {
                                this.jj_consume_token(31);
                                final Expression right = this.addExpression();
                                left = ComparisonExpression.createGreaterThanEqual(left, right);
                                continue;
                            }
                            case 32: {
                                this.jj_consume_token(32);
                                final Expression right = this.addExpression();
                                left = ComparisonExpression.createLessThan(left, right);
                                continue;
                            }
                            case 33: {
                                this.jj_consume_token(33);
                                final Expression right = this.addExpression();
                                left = ComparisonExpression.createLessThanEqual(left, right);
                                continue;
                            }
                            case 12: {
                                String u = null;
                                this.jj_consume_token(12);
                                final String t = this.stringLitteral();
                                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                    case 13: {
                                        this.jj_consume_token(13);
                                        u = this.stringLitteral();
                                        break;
                                    }
                                }
                                left = ComparisonExpression.createLike(left, t, u);
                                continue;
                            }
                            default: {
                                if (this.jj_2_2(2)) {
                                    String u = null;
                                    this.jj_consume_token(8);
                                    this.jj_consume_token(12);
                                    final String t = this.stringLitteral();
                                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                        case 13: {
                                            this.jj_consume_token(13);
                                            u = this.stringLitteral();
                                            break;
                                        }
                                    }
                                    left = ComparisonExpression.createNotLike(left, t, u);
                                    continue;
                                }
                                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                    case 11: {
                                        this.jj_consume_token(11);
                                        final Expression low = this.addExpression();
                                        this.jj_consume_token(9);
                                        final Expression high = this.addExpression();
                                        left = ComparisonExpression.createBetween(left, low, high);
                                        continue;
                                    }
                                    default: {
                                        if (this.jj_2_3(2)) {
                                            this.jj_consume_token(8);
                                            this.jj_consume_token(11);
                                            final Expression low = this.addExpression();
                                            this.jj_consume_token(9);
                                            final Expression high = this.addExpression();
                                            left = ComparisonExpression.createNotBetween(left, low, high);
                                            continue;
                                        }
                                        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                            case 14: {
                                                this.jj_consume_token(14);
                                                this.jj_consume_token(34);
                                                String t = this.stringLitteral();
                                                final ArrayList list = new ArrayList();
                                                list.add(t);
                                                while (true) {
                                                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                                        case 35: {
                                                            this.jj_consume_token(35);
                                                            t = this.stringLitteral();
                                                            list.add(t);
                                                            continue;
                                                        }
                                                        default: {
                                                            this.jj_consume_token(36);
                                                            left = ComparisonExpression.createInFilter(left, list);
                                                            continue Label_0005;
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                            default: {
                                                if (!this.jj_2_4(2)) {
                                                    this.jj_consume_token(-1);
                                                    throw new ParseException();
                                                }
                                                this.jj_consume_token(8);
                                                this.jj_consume_token(14);
                                                this.jj_consume_token(34);
                                                String t = this.stringLitteral();
                                                final ArrayList list = new ArrayList();
                                                list.add(t);
                                                while (true) {
                                                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                                        case 35: {
                                                            this.jj_consume_token(35);
                                                            t = this.stringLitteral();
                                                            list.add(t);
                                                            continue;
                                                        }
                                                        default: {
                                                            this.jj_consume_token(36);
                                                            left = ComparisonExpression.createNotInFilter(left, list);
                                                            continue Label_0005;
                                                        }
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                    default: {
                        return left;
                    }
                }
            }
            break;
        }
    }
    
    public final Expression addExpression() throws ParseException {
        Expression left = this.multExpr();
        while (this.jj_2_5(Integer.MAX_VALUE)) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 37: {
                    this.jj_consume_token(37);
                    final Expression right = this.multExpr();
                    left = ArithmeticExpression.createPlus(left, right);
                    continue;
                }
                case 38: {
                    this.jj_consume_token(38);
                    final Expression right = this.multExpr();
                    left = ArithmeticExpression.createMinus(left, right);
                    continue;
                }
                default: {
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        return left;
    }
    
    public final Expression multExpr() throws ParseException {
        Expression left = this.unaryExpr();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 39:
                case 40:
                case 41: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 39: {
                            this.jj_consume_token(39);
                            final Expression right = this.unaryExpr();
                            left = ArithmeticExpression.createMultiply(left, right);
                            continue;
                        }
                        case 40: {
                            this.jj_consume_token(40);
                            final Expression right = this.unaryExpr();
                            left = ArithmeticExpression.createDivide(left, right);
                            continue;
                        }
                        case 41: {
                            this.jj_consume_token(41);
                            final Expression right = this.unaryExpr();
                            left = ArithmeticExpression.createMod(left, right);
                            continue;
                        }
                        default: {
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    break;
                }
                default: {
                    return left;
                }
            }
        }
    }
    
    public final Expression unaryExpr() throws ParseException {
        String s = null;
        Expression left = null;
        Label_0322: {
            if (this.jj_2_6(Integer.MAX_VALUE)) {
                this.jj_consume_token(37);
                left = this.unaryExpr();
            }
            else {
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 38: {
                        this.jj_consume_token(38);
                        left = this.unaryExpr();
                        left = UnaryExpression.createNegate(left);
                        break;
                    }
                    case 8: {
                        this.jj_consume_token(8);
                        left = this.unaryExpr();
                        left = UnaryExpression.createNOT(this.asBooleanExpression(left));
                        break;
                    }
                    case 19: {
                        this.jj_consume_token(19);
                        s = this.stringLitteral();
                        left = UnaryExpression.createXPath(s);
                        break;
                    }
                    case 20: {
                        this.jj_consume_token(20);
                        s = this.stringLitteral();
                        left = UnaryExpression.createXQuery(s);
                        break;
                    }
                    default: {
                        if (this.jj_2_7(Integer.MAX_VALUE)) {
                            left = this.functionCallExpr();
                            break;
                        }
                        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                            case 16:
                            case 17:
                            case 18:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                            case 26:
                            case 27:
                            case 34: {
                                left = this.primaryExpr();
                                break Label_0322;
                            }
                            default: {
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        break;
                    }
                }
            }
        }
        return left;
    }
    
    public final Expression functionCallExpr() throws ParseException {
        final FunctionCallExpression func_call = null;
        Expression arg = null;
        final ArrayList arg_list = new ArrayList();
        final Token func_name = this.jj_consume_token(27);
        this.jj_consume_token(34);
        arg = this.unaryExpr();
        arg_list.add(arg);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 35: {
                    this.jj_consume_token(35);
                    arg = this.unaryExpr();
                    arg_list.add(arg);
                    continue;
                }
                default: {
                    this.jj_consume_token(36);
                    try {
                        return FunctionCallExpression.createFunctionCall(func_name.image, arg_list);
                    }
                    catch (FunctionCallExpression.invalidFunctionExpressionException inv_exc) {
                        throw new Error("invalid function call expression", inv_exc);
                    }
                    break;
                }
            }
        }
    }
    
    public final Expression primaryExpr() throws ParseException {
        Expression left = null;
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 16:
            case 17:
            case 18:
            case 21:
            case 22:
            case 23:
            case 24:
            case 26: {
                left = this.literal();
                break;
            }
            case 27: {
                left = this.variable();
                break;
            }
            case 34: {
                this.jj_consume_token(34);
                left = this.orExpression();
                this.jj_consume_token(36);
                break;
            }
            default: {
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return left;
    }
    
    public final ConstantExpression literal() throws ParseException {
        ConstantExpression left = null;
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 26: {
                final String s = this.stringLitteral();
                left = new ConstantExpression(s);
                break;
            }
            case 21: {
                final Token t = this.jj_consume_token(21);
                left = ConstantExpression.createFromDecimal(t.image);
                break;
            }
            case 22: {
                final Token t = this.jj_consume_token(22);
                left = ConstantExpression.createFromHex(t.image);
                break;
            }
            case 23: {
                final Token t = this.jj_consume_token(23);
                left = ConstantExpression.createFromOctal(t.image);
                break;
            }
            case 24: {
                final Token t = this.jj_consume_token(24);
                left = ConstantExpression.createFloat(t.image);
                break;
            }
            case 16: {
                this.jj_consume_token(16);
                left = ConstantExpression.TRUE;
                break;
            }
            case 17: {
                this.jj_consume_token(17);
                left = ConstantExpression.FALSE;
                break;
            }
            case 18: {
                this.jj_consume_token(18);
                left = ConstantExpression.NULL;
                break;
            }
            default: {
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return left;
    }
    
    public final String stringLitteral() throws ParseException {
        final StringBuffer rc = new StringBuffer();
        final boolean first = true;
        final Token t = this.jj_consume_token(26);
        final String image = t.image;
        for (int i = 1; i < image.length() - 1; ++i) {
            final char c = image.charAt(i);
            if (c == '\'') {
                ++i;
            }
            rc.append(c);
        }
        return rc.toString();
    }
    
    public final PropertyExpression variable() throws ParseException {
        PropertyExpression left = null;
        final Token t = this.jj_consume_token(27);
        left = new PropertyExpression(t.image);
        return left;
    }
    
    private boolean jj_2_1(final int xla) {
        this.jj_la = xla;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !this.jj_3_1();
        }
        catch (LookaheadSuccess ls) {
            return true;
        }
    }
    
    private boolean jj_2_2(final int xla) {
        this.jj_la = xla;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !this.jj_3_2();
        }
        catch (LookaheadSuccess ls) {
            return true;
        }
    }
    
    private boolean jj_2_3(final int xla) {
        this.jj_la = xla;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !this.jj_3_3();
        }
        catch (LookaheadSuccess ls) {
            return true;
        }
    }
    
    private boolean jj_2_4(final int xla) {
        this.jj_la = xla;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !this.jj_3_4();
        }
        catch (LookaheadSuccess ls) {
            return true;
        }
    }
    
    private boolean jj_2_5(final int xla) {
        this.jj_la = xla;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !this.jj_3_5();
        }
        catch (LookaheadSuccess ls) {
            return true;
        }
    }
    
    private boolean jj_2_6(final int xla) {
        this.jj_la = xla;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !this.jj_3_6();
        }
        catch (LookaheadSuccess ls) {
            return true;
        }
    }
    
    private boolean jj_2_7(final int xla) {
        this.jj_la = xla;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !this.jj_3_7();
        }
        catch (LookaheadSuccess ls) {
            return true;
        }
    }
    
    private boolean jj_3_3() {
        return this.jj_scan_token(8) || this.jj_scan_token(11) || this.jj_3R_47() || this.jj_scan_token(9) || this.jj_3R_47();
    }
    
    private boolean jj_3R_49() {
        return this.jj_scan_token(28) || this.jj_3R_45();
    }
    
    private boolean jj_3R_46() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_49()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_50()) {
                this.jj_scanpos = xsp;
                if (this.jj_3_1()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_51()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean jj_3R_31() {
        return this.jj_scan_token(27);
    }
    
    private boolean jj_3R_58() {
        return this.jj_scan_token(11) || this.jj_3R_47() || this.jj_scan_token(9) || this.jj_3R_47();
    }
    
    private boolean jj_3R_29() {
        return this.jj_scan_token(34) || this.jj_3R_32() || this.jj_scan_token(36);
    }
    
    private boolean jj_3R_62() {
        return this.jj_scan_token(13) || this.jj_3R_23();
    }
    
    private boolean jj_3R_28() {
        return this.jj_3R_31();
    }
    
    private boolean jj_3R_22() {
        return this.jj_scan_token(41) || this.jj_3R_11();
    }
    
    private boolean jj_3R_27() {
        return this.jj_3R_30();
    }
    
    private boolean jj_3R_43() {
        if (this.jj_3R_45()) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_46());
        this.jj_scanpos = xsp;
        return false;
    }
    
    private boolean jj_3_2() {
        if (this.jj_scan_token(8)) {
            return true;
        }
        if (this.jj_scan_token(12)) {
            return true;
        }
        if (this.jj_3R_23()) {
            return true;
        }
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_63()) {
            this.jj_scanpos = xsp;
        }
        return false;
    }
    
    private boolean jj_3R_57() {
        if (this.jj_scan_token(12)) {
            return true;
        }
        if (this.jj_3R_23()) {
            return true;
        }
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_62()) {
            this.jj_scanpos = xsp;
        }
        return false;
    }
    
    private boolean jj_3R_21() {
        return this.jj_scan_token(40) || this.jj_3R_11();
    }
    
    private boolean jj_3R_25() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_27()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_28()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_29()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean jj_3R_12() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_20()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_21()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_22()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean jj_3R_20() {
        return this.jj_scan_token(39) || this.jj_3R_11();
    }
    
    private boolean jj_3R_23() {
        return this.jj_scan_token(26);
    }
    
    private boolean jj_3R_44() {
        return this.jj_scan_token(9) || this.jj_3R_43();
    }
    
    private boolean jj_3R_10() {
        if (this.jj_3R_11()) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_12());
        this.jj_scanpos = xsp;
        return false;
    }
    
    private boolean jj_3R_61() {
        return this.jj_scan_token(38) || this.jj_3R_10();
    }
    
    private boolean jj_3R_41() {
        if (this.jj_3R_43()) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_44());
        this.jj_scanpos = xsp;
        return false;
    }
    
    private boolean jj_3_5() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(37)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(38)) {
                return true;
            }
        }
        return this.jj_3R_10();
    }
    
    private boolean jj_3R_26() {
        return this.jj_scan_token(35) || this.jj_3R_11();
    }
    
    private boolean jj_3R_56() {
        return this.jj_scan_token(33) || this.jj_3R_47();
    }
    
    private boolean jj_3R_40() {
        return this.jj_scan_token(18);
    }
    
    private boolean jj_3R_60() {
        return this.jj_scan_token(37) || this.jj_3R_10();
    }
    
    private boolean jj_3R_55() {
        return this.jj_scan_token(32) || this.jj_3R_47();
    }
    
    private boolean jj_3R_65() {
        return this.jj_scan_token(35) || this.jj_3R_23();
    }
    
    private boolean jj_3R_39() {
        return this.jj_scan_token(17);
    }
    
    private boolean jj_3R_52() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_60()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_61()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean jj_3R_42() {
        return this.jj_scan_token(10) || this.jj_3R_41();
    }
    
    private boolean jj_3R_54() {
        return this.jj_scan_token(31) || this.jj_3R_47();
    }
    
    private boolean jj_3_7() {
        return this.jj_scan_token(27) || this.jj_scan_token(34);
    }
    
    private boolean jj_3R_24() {
        if (this.jj_scan_token(27)) {
            return true;
        }
        if (this.jj_scan_token(34)) {
            return true;
        }
        if (this.jj_3R_11()) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_26());
        this.jj_scanpos = xsp;
        return this.jj_scan_token(36);
    }
    
    private boolean jj_3R_53() {
        return this.jj_scan_token(30) || this.jj_3R_47();
    }
    
    private boolean jj_3R_48() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_53()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_54()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_55()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_56()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_57()) {
                            this.jj_scanpos = xsp;
                            if (this.jj_3_2()) {
                                this.jj_scanpos = xsp;
                                if (this.jj_3R_58()) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_3_3()) {
                                        this.jj_scanpos = xsp;
                                        if (this.jj_3R_59()) {
                                            this.jj_scanpos = xsp;
                                            if (this.jj_3_4()) {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private boolean jj_3R_38() {
        return this.jj_scan_token(16);
    }
    
    private boolean jj_3R_47() {
        if (this.jj_3R_10()) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_52());
        this.jj_scanpos = xsp;
        return false;
    }
    
    private boolean jj_3R_32() {
        if (this.jj_3R_41()) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_42());
        this.jj_scanpos = xsp;
        return false;
    }
    
    private boolean jj_3R_19() {
        return this.jj_3R_25();
    }
    
    private boolean jj_3R_37() {
        return this.jj_scan_token(24);
    }
    
    private boolean jj_3R_18() {
        return this.jj_3R_24();
    }
    
    private boolean jj_3R_64() {
        return this.jj_scan_token(35) || this.jj_3R_23();
    }
    
    private boolean jj_3R_36() {
        return this.jj_scan_token(23);
    }
    
    private boolean jj_3R_17() {
        return this.jj_scan_token(20) || this.jj_3R_23();
    }
    
    private boolean jj_3R_45() {
        if (this.jj_3R_47()) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_48());
        this.jj_scanpos = xsp;
        return false;
    }
    
    private boolean jj_3R_63() {
        return this.jj_scan_token(13) || this.jj_3R_23();
    }
    
    private boolean jj_3_4() {
        if (this.jj_scan_token(8)) {
            return true;
        }
        if (this.jj_scan_token(14)) {
            return true;
        }
        if (this.jj_scan_token(34)) {
            return true;
        }
        if (this.jj_3R_23()) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_65());
        this.jj_scanpos = xsp;
        return this.jj_scan_token(36);
    }
    
    private boolean jj_3_6() {
        return this.jj_scan_token(37) || this.jj_3R_11();
    }
    
    private boolean jj_3R_16() {
        return this.jj_scan_token(19) || this.jj_3R_23();
    }
    
    private boolean jj_3R_35() {
        return this.jj_scan_token(22);
    }
    
    private boolean jj_3R_15() {
        return this.jj_scan_token(8) || this.jj_3R_11();
    }
    
    private boolean jj_3R_34() {
        return this.jj_scan_token(21);
    }
    
    private boolean jj_3R_13() {
        return this.jj_scan_token(37) || this.jj_3R_11();
    }
    
    private boolean jj_3R_59() {
        if (this.jj_scan_token(14)) {
            return true;
        }
        if (this.jj_scan_token(34)) {
            return true;
        }
        if (this.jj_3R_23()) {
            return true;
        }
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_64());
        this.jj_scanpos = xsp;
        return this.jj_scan_token(36);
    }
    
    private boolean jj_3R_51() {
        return this.jj_scan_token(15) || this.jj_scan_token(8) || this.jj_scan_token(18);
    }
    
    private boolean jj_3R_14() {
        return this.jj_scan_token(38) || this.jj_3R_11();
    }
    
    private boolean jj_3R_33() {
        return this.jj_3R_23();
    }
    
    private boolean jj_3_1() {
        return this.jj_scan_token(15) || this.jj_scan_token(18);
    }
    
    private boolean jj_3R_11() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_13()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_14()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_15()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_16()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_17()) {
                            this.jj_scanpos = xsp;
                            if (this.jj_3R_18()) {
                                this.jj_scanpos = xsp;
                                if (this.jj_3R_19()) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private boolean jj_3R_30() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_33()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_34()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_35()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_36()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_37()) {
                            this.jj_scanpos = xsp;
                            if (this.jj_3R_38()) {
                                this.jj_scanpos = xsp;
                                if (this.jj_3R_39()) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_3R_40()) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private boolean jj_3R_50() {
        return this.jj_scan_token(29) || this.jj_3R_45();
    }
    
    public SelectorParser(final InputStream stream) {
        this(stream, null);
    }
    
    public SelectorParser(final InputStream stream, final String encoding) {
        this.jj_ls = new LookaheadSuccess();
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new SelectorParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
    }
    
    public void ReInit(final InputStream stream) {
        this.ReInit(stream, null);
    }
    
    public void ReInit(final InputStream stream, final String encoding) {
        try {
            this.jj_input_stream.ReInit(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
    }
    
    public SelectorParser(final Reader stream) {
        this.jj_ls = new LookaheadSuccess();
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new SelectorParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
    }
    
    public void ReInit(final Reader stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
    }
    
    public SelectorParser(final SelectorParserTokenManager tm) {
        this.jj_ls = new LookaheadSuccess();
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
    }
    
    public void ReInit(final SelectorParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
    }
    
    private Token jj_consume_token(final int kind) throws ParseException {
        final Token oldToken;
        if ((oldToken = this.token).next != null) {
            this.token = this.token.next;
        }
        else {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            return this.token;
        }
        this.token = oldToken;
        throw this.generateParseException();
    }
    
    private boolean jj_scan_token(final int kind) {
        if (this.jj_scanpos == this.jj_lastpos) {
            --this.jj_la;
            if (this.jj_scanpos.next == null) {
                final Token jj_scanpos = this.jj_scanpos;
                final Token nextToken = this.token_source.getNextToken();
                jj_scanpos.next = nextToken;
                this.jj_scanpos = nextToken;
                this.jj_lastpos = nextToken;
            }
            else {
                final Token next = this.jj_scanpos.next;
                this.jj_scanpos = next;
                this.jj_lastpos = next;
            }
        }
        else {
            this.jj_scanpos = this.jj_scanpos.next;
        }
        if (this.jj_scanpos.kind != kind) {
            return true;
        }
        if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            throw this.jj_ls;
        }
        return false;
    }
    
    public final Token getNextToken() {
        if (this.token.next != null) {
            this.token = this.token.next;
        }
        else {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        return this.token;
    }
    
    public final Token getToken(final int index) {
        Token t = this.token;
        for (int i = 0; i < index; ++i) {
            if (t.next != null) {
                t = t.next;
            }
            else {
                final Token token = t;
                final Token nextToken = this.token_source.getNextToken();
                token.next = nextToken;
                t = nextToken;
            }
        }
        return t;
    }
    
    private int jj_ntk() {
        final Token next = this.token.next;
        this.jj_nt = next;
        if (next == null) {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            return this.jj_ntk = nextToken.kind;
        }
        return this.jj_ntk = this.jj_nt.kind;
    }
    
    public ParseException generateParseException() {
        final Token errortok = this.token.next;
        final int line = errortok.beginLine;
        final int column = errortok.beginColumn;
        final String mess = (errortok.kind == 0) ? SelectorParser.tokenImage[0] : errortok.image;
        return new ParseException("Parse error at line " + line + ", column " + column + ".  Encountered: " + mess);
    }
    
    public final void enable_tracing() {
    }
    
    public final void disable_tracing() {
    }
    
    static {
        cache = Collections.synchronizedMap(new LRUCache<Object, Object>(100));
    }
    
    private static final class LookaheadSuccess extends Error
    {
    }
}
