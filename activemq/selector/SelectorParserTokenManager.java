// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.selector;

import java.io.IOException;
import java.io.PrintStream;

public class SelectorParserTokenManager implements SelectorParserConstants
{
    public PrintStream debugStream;
    static final long[] jjbitVec0;
    static final long[] jjbitVec2;
    static final int[] jjnextStates;
    public static final String[] jjstrLiteralImages;
    public static final String[] lexStateNames;
    static final long[] jjtoToken;
    static final long[] jjtoSkip;
    static final long[] jjtoSpecial;
    protected SimpleCharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    protected char curChar;
    int curLexState;
    int defaultLexState;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;
    
    public void setDebugStream(final PrintStream ds) {
        this.debugStream = ds;
    }
    
    private int jjStopAtPos(final int pos, final int kind) {
        this.jjmatchedKind = kind;
        return (this.jjmatchedPos = pos) + 1;
    }
    
    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '\t': {
                this.jjmatchedKind = 2;
                return this.jjMoveNfa_0(5, 0);
            }
            case '\n': {
                this.jjmatchedKind = 3;
                return this.jjMoveNfa_0(5, 0);
            }
            case '\f': {
                this.jjmatchedKind = 5;
                return this.jjMoveNfa_0(5, 0);
            }
            case '\r': {
                this.jjmatchedKind = 4;
                return this.jjMoveNfa_0(5, 0);
            }
            case ' ': {
                this.jjmatchedKind = 1;
                return this.jjMoveNfa_0(5, 0);
            }
            case '%': {
                this.jjmatchedKind = 41;
                return this.jjMoveNfa_0(5, 0);
            }
            case '(': {
                this.jjmatchedKind = 34;
                return this.jjMoveNfa_0(5, 0);
            }
            case ')': {
                this.jjmatchedKind = 36;
                return this.jjMoveNfa_0(5, 0);
            }
            case '*': {
                this.jjmatchedKind = 39;
                return this.jjMoveNfa_0(5, 0);
            }
            case '+': {
                this.jjmatchedKind = 37;
                return this.jjMoveNfa_0(5, 0);
            }
            case ',': {
                this.jjmatchedKind = 35;
                return this.jjMoveNfa_0(5, 0);
            }
            case '-': {
                this.jjmatchedKind = 38;
                return this.jjMoveNfa_0(5, 0);
            }
            case '/': {
                this.jjmatchedKind = 40;
                return this.jjMoveNfa_0(5, 0);
            }
            case '<': {
                this.jjmatchedKind = 32;
                return this.jjMoveStringLiteralDfa1_0(9126805504L);
            }
            case '=': {
                this.jjmatchedKind = 28;
                return this.jjMoveNfa_0(5, 0);
            }
            case '>': {
                this.jjmatchedKind = 30;
                return this.jjMoveStringLiteralDfa1_0(2147483648L);
            }
            case 'A': {
                return this.jjMoveStringLiteralDfa1_0(512L);
            }
            case 'B': {
                return this.jjMoveStringLiteralDfa1_0(2048L);
            }
            case 'E': {
                return this.jjMoveStringLiteralDfa1_0(8192L);
            }
            case 'F': {
                return this.jjMoveStringLiteralDfa1_0(131072L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa1_0(49152L);
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa1_0(4096L);
            }
            case 'N': {
                return this.jjMoveStringLiteralDfa1_0(262400L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa1_0(1024L);
            }
            case 'T': {
                return this.jjMoveStringLiteralDfa1_0(65536L);
            }
            case 'X': {
                return this.jjMoveStringLiteralDfa1_0(1572864L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa1_0(512L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa1_0(2048L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa1_0(8192L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_0(131072L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa1_0(49152L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa1_0(4096L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa1_0(262400L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa1_0(1024L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_0(65536L);
            }
            case 'x': {
                return this.jjMoveStringLiteralDfa1_0(1572864L);
            }
            default: {
                return this.jjMoveNfa_0(5, 0);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa1_0(final long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(5, 0);
        }
        switch (this.curChar) {
            case '=': {
                if ((active0 & 0x80000000L) != 0x0L) {
                    this.jjmatchedKind = 31;
                    this.jjmatchedPos = 1;
                    break;
                }
                if ((active0 & 0x200000000L) != 0x0L) {
                    this.jjmatchedKind = 33;
                    this.jjmatchedPos = 1;
                    break;
                }
                break;
            }
            case '>': {
                if ((active0 & 0x20000000L) != 0x0L) {
                    this.jjmatchedKind = 29;
                    this.jjmatchedPos = 1;
                    break;
                }
                break;
            }
            case 'A': {
                return this.jjMoveStringLiteralDfa2_0(active0, 131072L);
            }
            case 'E': {
                return this.jjMoveStringLiteralDfa2_0(active0, 2048L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa2_0(active0, 4096L);
            }
            case 'N': {
                if ((active0 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 14;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 512L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa2_0(active0, 256L);
            }
            case 'P': {
                return this.jjMoveStringLiteralDfa2_0(active0, 524288L);
            }
            case 'Q': {
                return this.jjMoveStringLiteralDfa2_0(active0, 1048576L);
            }
            case 'R': {
                if ((active0 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 10;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 65536L);
            }
            case 'S': {
                if ((active0 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 15;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 8192L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa2_0(active0, 262144L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_0(active0, 131072L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa2_0(active0, 2048L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa2_0(active0, 4096L);
            }
            case 'n': {
                if ((active0 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 14;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 512L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa2_0(active0, 256L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa2_0(active0, 524288L);
            }
            case 'q': {
                return this.jjMoveStringLiteralDfa2_0(active0, 1048576L);
            }
            case 'r': {
                if ((active0 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 10;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 65536L);
            }
            case 's': {
                if ((active0 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 15;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 8192L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa2_0(active0, 262144L);
            }
        }
        return this.jjMoveNfa_0(5, 1);
    }
    
    private int jjMoveStringLiteralDfa2_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjMoveNfa_0(5, 1);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(5, 1);
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa3_0(active0, 524288L);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa3_0(active0, 8192L);
            }
            case 'D': {
                if ((active0 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 9;
                    this.jjmatchedPos = 2;
                    break;
                }
                break;
            }
            case 'K': {
                return this.jjMoveStringLiteralDfa3_0(active0, 4096L);
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa3_0(active0, 393216L);
            }
            case 'T': {
                if ((active0 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 8;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 2048L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa3_0(active0, 1114112L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa3_0(active0, 524288L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa3_0(active0, 8192L);
            }
            case 'd': {
                if ((active0 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 9;
                    this.jjmatchedPos = 2;
                    break;
                }
                break;
            }
            case 'k': {
                return this.jjMoveStringLiteralDfa3_0(active0, 4096L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa3_0(active0, 393216L);
            }
            case 't': {
                if ((active0 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 8;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 2048L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_0(active0, 1114112L);
            }
        }
        return this.jjMoveNfa_0(5, 2);
    }
    
    private int jjMoveStringLiteralDfa3_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjMoveNfa_0(5, 2);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(5, 2);
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa4_0(active0, 8192L);
            }
            case 'E': {
                if ((active0 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 3;
                }
                else if ((active0 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 16;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 1048576L);
            }
            case 'L': {
                if ((active0 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 18;
                    this.jjmatchedPos = 3;
                    break;
                }
                break;
            }
            case 'S': {
                return this.jjMoveStringLiteralDfa4_0(active0, 131072L);
            }
            case 'T': {
                return this.jjMoveStringLiteralDfa4_0(active0, 524288L);
            }
            case 'W': {
                return this.jjMoveStringLiteralDfa4_0(active0, 2048L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa4_0(active0, 8192L);
            }
            case 'e': {
                if ((active0 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 3;
                }
                else if ((active0 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 16;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 1048576L);
            }
            case 'l': {
                if ((active0 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 18;
                    this.jjmatchedPos = 3;
                    break;
                }
                break;
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_0(active0, 131072L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa4_0(active0, 524288L);
            }
            case 'w': {
                return this.jjMoveStringLiteralDfa4_0(active0, 2048L);
            }
        }
        return this.jjMoveNfa_0(5, 3);
    }
    
    private int jjMoveStringLiteralDfa4_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjMoveNfa_0(5, 3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(5, 3);
        }
        switch (this.curChar) {
            case 'E': {
                if ((active0 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 17;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 2048L);
            }
            case 'H': {
                if ((active0 & 0x80000L) != 0x0L) {
                    this.jjmatchedKind = 19;
                    this.jjmatchedPos = 4;
                    break;
                }
                break;
            }
            case 'P': {
                return this.jjMoveStringLiteralDfa5_0(active0, 8192L);
            }
            case 'R': {
                return this.jjMoveStringLiteralDfa5_0(active0, 1048576L);
            }
            case 'e': {
                if ((active0 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 17;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 2048L);
            }
            case 'h': {
                if ((active0 & 0x80000L) != 0x0L) {
                    this.jjmatchedKind = 19;
                    this.jjmatchedPos = 4;
                    break;
                }
                break;
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa5_0(active0, 8192L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa5_0(active0, 1048576L);
            }
        }
        return this.jjMoveNfa_0(5, 4);
    }
    
    private int jjMoveStringLiteralDfa5_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjMoveNfa_0(5, 4);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(5, 4);
        }
        switch (this.curChar) {
            case 'E': {
                if ((active0 & 0x2000L) != 0x0L) {
                    this.jjmatchedKind = 13;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 2048L);
            }
            case 'Y': {
                if ((active0 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 20;
                    this.jjmatchedPos = 5;
                    break;
                }
                break;
            }
            case 'e': {
                if ((active0 & 0x2000L) != 0x0L) {
                    this.jjmatchedKind = 13;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 2048L);
            }
            case 'y': {
                if ((active0 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 20;
                    this.jjmatchedPos = 5;
                    break;
                }
                break;
            }
        }
        return this.jjMoveNfa_0(5, 5);
    }
    
    private int jjMoveStringLiteralDfa6_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjMoveNfa_0(5, 5);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return this.jjMoveNfa_0(5, 5);
        }
        switch (this.curChar) {
            case 'N': {
                if ((active0 & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 11;
                    this.jjmatchedPos = 6;
                    break;
                }
                break;
            }
            case 'n': {
                if ((active0 & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 11;
                    this.jjmatchedPos = 6;
                    break;
                }
                break;
            }
        }
        return this.jjMoveNfa_0(5, 6);
    }
    
    private int jjMoveNfa_0(final int startState, int curPos) {
        final int strKind = this.jjmatchedKind;
        final int strPos = this.jjmatchedPos;
        final int seenUpto;
        this.input_stream.backup(seenUpto = curPos + 1);
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            throw new Error("Internal Error");
        }
        curPos = 0;
        int startsAt = 0;
        this.jjnewStateCnt = 43;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long l = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 5: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(0, 3);
                            }
                            else if (this.curChar == '$') {
                                if (kind > 27) {
                                    kind = 27;
                                }
                                this.jjCheckNAdd(27);
                            }
                            else if (this.curChar == '\'') {
                                this.jjCheckNAddStates(4, 6);
                            }
                            else if (this.curChar == '.') {
                                this.jjCheckNAdd(17);
                            }
                            else if (this.curChar == '/') {
                                this.jjstateSet[this.jjnewStateCnt++] = 6;
                            }
                            else if (this.curChar == '-') {
                                this.jjstateSet[this.jjnewStateCnt++] = 0;
                            }
                            if ((0x3FE000000000000L & l) != 0x0L) {
                                if (kind > 21) {
                                    kind = 21;
                                }
                                this.jjCheckNAddTwoStates(14, 15);
                                continue;
                            }
                            if (this.curChar == '0') {
                                if (kind > 23) {
                                    kind = 23;
                                }
                                this.jjCheckNAddTwoStates(40, 42);
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if (this.curChar == '-') {
                                this.jjCheckNAddStates(7, 9);
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(7, 9);
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if ((0x2400L & l) != 0x0L && kind > 6) {
                                kind = 6;
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if (this.curChar == '\n' && kind > 6) {
                                kind = 6;
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                                continue;
                            }
                            continue;
                        }
                        case 6: {
                            if (this.curChar == '*') {
                                this.jjCheckNAddTwoStates(7, 8);
                                continue;
                            }
                            continue;
                        }
                        case 7: {
                            if ((0xFFFFFBFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(7, 8);
                                continue;
                            }
                            continue;
                        }
                        case 8: {
                            if (this.curChar == '*') {
                                this.jjCheckNAddStates(10, 12);
                                continue;
                            }
                            continue;
                        }
                        case 9: {
                            if ((0xFFFF7BFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(10, 8);
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if ((0xFFFFFBFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(10, 8);
                                continue;
                            }
                            continue;
                        }
                        case 11: {
                            if (this.curChar == '/' && kind > 7) {
                                kind = 7;
                                continue;
                            }
                            continue;
                        }
                        case 12: {
                            if (this.curChar == '/') {
                                this.jjstateSet[this.jjnewStateCnt++] = 6;
                                continue;
                            }
                            continue;
                        }
                        case 13: {
                            if ((0x3FE000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(14, 15);
                            continue;
                        }
                        case 14: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(14, 15);
                            continue;
                        }
                        case 16: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(17);
                                continue;
                            }
                            continue;
                        }
                        case 17: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 24) {
                                kind = 24;
                            }
                            this.jjCheckNAddTwoStates(17, 18);
                            continue;
                        }
                        case 19: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(20);
                                continue;
                            }
                            continue;
                        }
                        case 20: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 24) {
                                kind = 24;
                            }
                            this.jjCheckNAdd(20);
                            continue;
                        }
                        case 21:
                        case 22: {
                            if (this.curChar == '\'') {
                                this.jjCheckNAddStates(4, 6);
                                continue;
                            }
                            continue;
                        }
                        case 23: {
                            if (this.curChar == '\'') {
                                this.jjstateSet[this.jjnewStateCnt++] = 22;
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(4, 6);
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if (this.curChar == '\'' && kind > 26) {
                                kind = 26;
                                continue;
                            }
                            continue;
                        }
                        case 26: {
                            if (this.curChar != '$') {
                                continue;
                            }
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 27: {
                            if ((0x3FF001000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 28: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(0, 3);
                                continue;
                            }
                            continue;
                        }
                        case 29: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(29, 30);
                                continue;
                            }
                            continue;
                        }
                        case 30: {
                            if (this.curChar != '.') {
                                continue;
                            }
                            if (kind > 24) {
                                kind = 24;
                            }
                            this.jjCheckNAddTwoStates(31, 32);
                            continue;
                        }
                        case 31: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 24) {
                                kind = 24;
                            }
                            this.jjCheckNAddTwoStates(31, 32);
                            continue;
                        }
                        case 33: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(34);
                                continue;
                            }
                            continue;
                        }
                        case 34: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 24) {
                                kind = 24;
                            }
                            this.jjCheckNAdd(34);
                            continue;
                        }
                        case 35: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(35, 36);
                                continue;
                            }
                            continue;
                        }
                        case 37: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(38);
                                continue;
                            }
                            continue;
                        }
                        case 38: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 24) {
                                kind = 24;
                            }
                            this.jjCheckNAdd(38);
                            continue;
                        }
                        case 39: {
                            if (this.curChar != '0') {
                                continue;
                            }
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(40, 42);
                            continue;
                        }
                        case 41: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 41;
                            continue;
                        }
                        case 42: {
                            if ((0xFF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAdd(42);
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else if (this.curChar < '\u0080') {
                final long l = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 5:
                        case 27: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 15: {
                            if ((0x100000001000L & l) != 0x0L && kind > 21) {
                                kind = 21;
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjAddStates(13, 14);
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjAddStates(15, 16);
                                continue;
                            }
                            continue;
                        }
                        case 36: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjAddStates(17, 18);
                                continue;
                            }
                            continue;
                        }
                        case 40: {
                            if ((0x100000001000000L & l) != 0x0L) {
                                this.jjCheckNAdd(41);
                                continue;
                            }
                            continue;
                        }
                        case 41: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjCheckNAdd(41);
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 1: {
                            this.jjAddStates(7, 9);
                            continue;
                        }
                        case 7: {
                            this.jjCheckNAddTwoStates(7, 8);
                            continue;
                        }
                        case 9:
                        case 10: {
                            this.jjCheckNAddTwoStates(10, 8);
                            continue;
                        }
                        case 24: {
                            this.jjAddStates(4, 6);
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else {
                final int hiByte = this.curChar >> 8;
                final int i2 = hiByte >> 6;
                final long l2 = 1L << (hiByte & 0x3F);
                final int i3 = (this.curChar & '\u00ff') >> 6;
                final long l3 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(7, 9);
                                continue;
                            }
                            continue;
                        }
                        case 7: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(7, 8);
                                continue;
                            }
                            continue;
                        }
                        case 9:
                        case 10: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(10, 8);
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(4, 6);
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            ++curPos;
            final int n = i = this.jjnewStateCnt;
            final int n2 = 43;
            final int jjnewStateCnt = startsAt;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n == (startsAt = n2 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
                continue;
            }
            catch (IOException e2) {}
            break;
        }
        if (this.jjmatchedPos > strPos) {
            return curPos;
        }
        final int toRet = Math.max(curPos, seenUpto);
        if (curPos < toRet) {
            i = toRet - Math.min(curPos, seenUpto);
            while (i-- > 0) {
                try {
                    this.curChar = this.input_stream.readChar();
                    continue;
                }
                catch (IOException e3) {
                    throw new Error("Internal Error : Please send a bug report.");
                }
                break;
            }
        }
        if (this.jjmatchedPos < strPos) {
            this.jjmatchedKind = strKind;
            this.jjmatchedPos = strPos;
        }
        else if (this.jjmatchedPos == strPos && this.jjmatchedKind > strKind) {
            this.jjmatchedKind = strKind;
        }
        return toRet;
    }
    
    private static final boolean jjCanMove_0(final int hiByte, final int i1, final int i2, final long l1, final long l2) {
        switch (hiByte) {
            case 0: {
                return (SelectorParserTokenManager.jjbitVec2[i2] & l2) != 0x0L;
            }
            default: {
                return (SelectorParserTokenManager.jjbitVec0[i1] & l1) != 0x0L;
            }
        }
    }
    
    public SelectorParserTokenManager(final SimpleCharStream stream) {
        this.debugStream = System.out;
        this.jjrounds = new int[43];
        this.jjstateSet = new int[86];
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = stream;
    }
    
    public SelectorParserTokenManager(final SimpleCharStream stream, final int lexState) {
        this(stream);
        this.SwitchTo(lexState);
    }
    
    public void ReInit(final SimpleCharStream stream) {
        final int n = 0;
        this.jjnewStateCnt = n;
        this.jjmatchedPos = n;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        this.ReInitRounds();
    }
    
    private void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 43;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }
    
    public void ReInit(final SimpleCharStream stream, final int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }
    
    public void SwitchTo(final int lexState) {
        if (lexState >= 1 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }
    
    protected Token jjFillToken() {
        final String im = SelectorParserTokenManager.jjstrLiteralImages[this.jjmatchedKind];
        final String curTokenImage = (im == null) ? this.input_stream.GetImage() : im;
        final int beginLine = this.input_stream.getBeginLine();
        final int beginColumn = this.input_stream.getBeginColumn();
        final int endLine = this.input_stream.getEndLine();
        final int endColumn = this.input_stream.getEndColumn();
        final Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
        t.beginLine = beginLine;
        t.endLine = endLine;
        t.beginColumn = beginColumn;
        t.endColumn = endColumn;
        return t;
    }
    
    public Token getNextToken() {
        Token specialToken = null;
        int curPos = 0;
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (IOException e) {
                this.jjmatchedKind = 0;
                final Token matchedToken = this.jjFillToken();
                matchedToken.specialToken = specialToken;
                return matchedToken;
            }
            this.jjmatchedKind = Integer.MAX_VALUE;
            this.jjmatchedPos = 0;
            curPos = this.jjMoveStringLiteralDfa0_0();
            if (this.jjmatchedKind == Integer.MAX_VALUE) {
                int error_line = this.input_stream.getEndLine();
                int error_column = this.input_stream.getEndColumn();
                String error_after = null;
                boolean EOFSeen = false;
                try {
                    this.input_stream.readChar();
                    this.input_stream.backup(1);
                }
                catch (IOException e2) {
                    EOFSeen = true;
                    error_after = ((curPos <= 1) ? "" : this.input_stream.GetImage());
                    if (this.curChar == '\n' || this.curChar == '\r') {
                        ++error_line;
                        error_column = 0;
                    }
                    else {
                        ++error_column;
                    }
                }
                if (!EOFSeen) {
                    this.input_stream.backup(1);
                    error_after = ((curPos <= 1) ? "" : this.input_stream.GetImage());
                }
                throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
            }
            if (this.jjmatchedPos + 1 < curPos) {
                this.input_stream.backup(curPos - this.jjmatchedPos - 1);
            }
            if ((SelectorParserTokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                final Token matchedToken = this.jjFillToken();
                matchedToken.specialToken = specialToken;
                return matchedToken;
            }
            if ((SelectorParserTokenManager.jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) == 0x0L) {
                continue;
            }
            final Token matchedToken = this.jjFillToken();
            if (specialToken == null) {
                specialToken = matchedToken;
            }
            else {
                matchedToken.specialToken = specialToken;
                final Token token = specialToken;
                final Token next = matchedToken;
                token.next = next;
                specialToken = next;
            }
        }
    }
    
    private void jjCheckNAdd(final int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }
    }
    
    private void jjAddStates(int start, final int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = SelectorParserTokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(SelectorParserTokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    static {
        jjbitVec0 = new long[] { -2L, -1L, -1L, -1L };
        jjbitVec2 = new long[] { 0L, 0L, -1L, -1L };
        jjnextStates = new int[] { 29, 30, 35, 36, 23, 24, 25, 1, 2, 4, 8, 9, 11, 19, 20, 33, 34, 37, 38 };
        jjstrLiteralImages = new String[] { "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "=", "<>", ">", ">=", "<", "<=", "(", ",", ")", "+", "-", "*", "/", "%" };
        lexStateNames = new String[] { "DEFAULT" };
        jjtoToken = new long[] { 4398012956417L };
        jjtoSkip = new long[] { 254L };
        jjtoSpecial = new long[] { 62L };
    }
}
