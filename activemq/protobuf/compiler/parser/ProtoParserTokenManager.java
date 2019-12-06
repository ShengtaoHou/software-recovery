// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf.compiler.parser;

import java.io.IOException;
import java.io.PrintStream;

public class ProtoParserTokenManager implements ProtoParserConstants
{
    public PrintStream debugStream;
    static final long[] jjbitVec0;
    static final int[] jjnextStates;
    public static final String[] jjstrLiteralImages;
    public static final String[] lexStateNames;
    public static final int[] jjnewLexState;
    static final long[] jjtoToken;
    static final long[] jjtoSkip;
    static final long[] jjtoSpecial;
    static final long[] jjtoMore;
    protected SimpleCharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    StringBuffer image;
    int jjimageLen;
    int lengthOfMatch;
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
    
    private final int jjStopStringLiteralDfa_0(final int pos, final long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0xFFFF00L) != 0x0L) {
                    this.jjmatchedKind = 41;
                    return 22;
                }
                if ((active0 & 0x100000000L) != 0x0L) {
                    return 4;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x400000L) != 0x0L) {
                    return 22;
                }
                if ((active0 & 0xBFFF00L) != 0x0L) {
                    this.jjmatchedKind = 41;
                    this.jjmatchedPos = 1;
                    return 22;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x800800L) != 0x0L) {
                    return 22;
                }
                if ((active0 & 0x3FF700L) != 0x0L) {
                    this.jjmatchedKind = 41;
                    this.jjmatchedPos = 2;
                    return 22;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x10000L) != 0x0L) {
                    return 22;
                }
                if ((active0 & 0x3EF700L) != 0x0L) {
                    this.jjmatchedKind = 41;
                    this.jjmatchedPos = 3;
                    return 22;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x20000L) != 0x0L) {
                    return 22;
                }
                if ((active0 & 0x3CF700L) != 0x0L) {
                    this.jjmatchedKind = 41;
                    this.jjmatchedPos = 4;
                    return 22;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x89100L) != 0x0L) {
                    return 22;
                }
                if ((active0 & 0x346600L) != 0x0L) {
                    if (this.jjmatchedPos != 5) {
                        this.jjmatchedKind = 41;
                        this.jjmatchedPos = 5;
                    }
                    return 22;
                }
                return -1;
            }
            case 6: {
                if ((active0 & 0x202600L) != 0x0L) {
                    return 22;
                }
                if ((active0 & 0x1C4000L) != 0x0L) {
                    this.jjmatchedKind = 41;
                    this.jjmatchedPos = 6;
                    return 22;
                }
                return -1;
            }
            case 7: {
                if ((active0 & 0x1C0000L) != 0x0L) {
                    return 22;
                }
                if ((active0 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 41;
                    this.jjmatchedPos = 7;
                    return 22;
                }
                return -1;
            }
            case 8: {
                if ((active0 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 41;
                    this.jjmatchedPos = 8;
                    return 22;
                }
                return -1;
            }
            default: {
                return -1;
            }
        }
    }
    
    private final int jjStartNfa_0(final int pos, final long active0) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0), pos + 1);
    }
    
    private final int jjStopAtPos(final int pos, final int kind) {
        this.jjmatchedKind = kind;
        return (this.jjmatchedPos = pos) + 1;
    }
    
    private final int jjStartNfaWithStates_0(final int pos, final int kind, final int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_0(state, pos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '(': {
                return this.jjStopAtPos(0, 30);
            }
            case ')': {
                return this.jjStopAtPos(0, 31);
            }
            case ',': {
                return this.jjStopAtPos(0, 33);
            }
            case '.': {
                return this.jjStartNfaWithStates_0(0, 32, 4);
            }
            case '/': {
                return this.jjMoveStringLiteralDfa1_0(32L);
            }
            case ';': {
                return this.jjStopAtPos(0, 27);
            }
            case '=': {
                return this.jjStopAtPos(0, 26);
            }
            case '[': {
                return this.jjStopAtPos(0, 28);
            }
            case ']': {
                return this.jjStopAtPos(0, 29);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa1_0(114688L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa1_0(131072L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa1_0(256L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa1_0(8396800L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa1_0(528384L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa1_0(512L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa1_0(3409920L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa1_0(1024L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_0(4194304L);
            }
            case '{': {
                return this.jjStopAtPos(0, 24);
            }
            case '}': {
                return this.jjStopAtPos(0, 25);
            }
            default: {
                return this.jjMoveNfa_0(9, 0);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa1_0(final long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '/': {
                if ((active0 & 0x20L) != 0x0L) {
                    return this.jjStopAtPos(1, 5);
                }
                break;
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_0(active0, 8389120L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa2_0(active0, 3417088L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa2_0(active0, 256L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa2_0(active0, 65536L);
            }
            case 'o': {
                if ((active0 & 0x400000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(1, 22, 22);
                }
                break;
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa2_0(active0, 530432L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa2_0(active0, 131072L);
            }
            case 'x': {
                return this.jjMoveStringLiteralDfa2_0(active0, 49152L);
            }
        }
        return this.jjStartNfa_0(0, active0);
    }
    
    private final int jjMoveStringLiteralDfa2_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case 'c': {
                if ((active0 & 0x800L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 11, 22);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 512L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa3_0(active0, 131072L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa3_0(active0, 1048832L);
            }
            case 'q': {
                return this.jjMoveStringLiteralDfa3_0(active0, 262144L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa3_0(active0, 1024L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa3_0(active0, 8192L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa3_0(active0, 2674688L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_0(active0, 65536L);
            }
            case 'x': {
                if ((active0 & 0x800000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 23, 22);
                }
                break;
            }
        }
        return this.jjStartNfa_0(1, active0);
    }
    
    private final int jjMoveStringLiteralDfa3_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(2, active0);
            return 3;
        }
        switch (this.curChar) {
            case 'e': {
                return this.jjMoveStringLiteralDfa4_0(active0, 1097728L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa4_0(active0, 528384L);
            }
            case 'k': {
                return this.jjMoveStringLiteralDfa4_0(active0, 512L);
            }
            case 'm': {
                if ((active0 & 0x10000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 16, 22);
                }
                break;
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa4_0(active0, 256L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_0(active0, 8192L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa4_0(active0, 2490368L);
            }
            case 'v': {
                return this.jjMoveStringLiteralDfa4_0(active0, 1024L);
            }
        }
        return this.jjStartNfa_0(2, active0);
    }
    
    private final int jjMoveStringLiteralDfa4_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(3, active0);
            return 4;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa5_0(active0, 1057280L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa5_0(active0, 263168L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa5_0(active0, 49152L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa5_0(active0, 528384L);
            }
            case 'p': {
                if ((active0 & 0x20000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 17, 22);
                }
                break;
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa5_0(active0, 2097408L);
            }
        }
        return this.jjStartNfa_0(3, active0);
    }
    
    private final int jjMoveStringLiteralDfa5_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(3, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(4, active0);
            return 5;
        }
        switch (this.curChar) {
            case 'c': {
                return this.jjMoveStringLiteralDfa6_0(active0, 1024L);
            }
            case 'd': {
                if ((active0 & 0x8000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 15, 22);
                }
                break;
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa6_0(active0, 8704L);
            }
            case 'n': {
                if ((active0 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 2621440L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa6_0(active0, 262144L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa6_0(active0, 16384L);
            }
            case 't': {
                if ((active0 & 0x100L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 8, 22);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 1048576L);
            }
        }
        return this.jjStartNfa_0(4, active0);
    }
    
    private final int jjMoveStringLiteralDfa6_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(4, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(5, active0);
            return 6;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa7_0(active0, 524288L);
            }
            case 'e': {
                if ((active0 & 0x200L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 9, 22);
                }
                if ((active0 & 0x400L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 10, 22);
                }
                if ((active0 & 0x2000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 13, 22);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 1310720L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa7_0(active0, 16384L);
            }
            case 's': {
                if ((active0 & 0x200000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 21, 22);
                }
                break;
            }
        }
        return this.jjStartNfa_0(5, active0);
    }
    
    private final int jjMoveStringLiteralDfa7_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(5, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(6, active0);
            return 7;
        }
        switch (this.curChar) {
            case 'd': {
                if ((active0 & 0x40000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 18, 22);
                }
                if ((active0 & 0x100000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 20, 22);
                }
                break;
            }
            case 'l': {
                if ((active0 & 0x80000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 19, 22);
                }
                break;
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa8_0(active0, 16384L);
            }
        }
        return this.jjStartNfa_0(6, active0);
    }
    
    private final int jjMoveStringLiteralDfa8_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(6, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(7, active0);
            return 8;
        }
        switch (this.curChar) {
            case 'n': {
                return this.jjMoveStringLiteralDfa9_0(active0, 16384L);
            }
            default: {
                return this.jjStartNfa_0(7, active0);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa9_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(7, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(8, active0);
            return 9;
        }
        switch (this.curChar) {
            case 's': {
                if ((active0 & 0x4000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 14, 22);
                }
                break;
            }
        }
        return this.jjStartNfa_0(8, active0);
    }
    
    private final void jjCheckNAdd(final int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }
    }
    
    private final void jjAddStates(int start, final int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = ProtoParserTokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private final void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private final void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(ProtoParserTokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    private final void jjCheckNAddStates(final int start) {
        this.jjCheckNAdd(ProtoParserTokenManager.jjnextStates[start]);
        this.jjCheckNAdd(ProtoParserTokenManager.jjnextStates[start + 1]);
    }
    
    private final int jjMoveNfa_0(final int startState, int curPos) {
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
                        case 9: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(0, 6);
                            }
                            else if (this.curChar == '-') {
                                this.jjAddStates(7, 10);
                            }
                            else if (this.curChar == '\"') {
                                this.jjCheckNAddStates(11, 13);
                            }
                            else if (this.curChar == '.') {
                                this.jjCheckNAdd(4);
                            }
                            if ((0x3FE000000000000L & l) != 0x0L) {
                                if (kind > 34) {
                                    kind = 34;
                                }
                                this.jjCheckNAddTwoStates(1, 2);
                                continue;
                            }
                            if (this.curChar == '0') {
                                if (kind > 34) {
                                    kind = 34;
                                }
                                this.jjCheckNAddStates(14, 16);
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if ((0x3FE000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddTwoStates(1, 2);
                            continue;
                        }
                        case 1: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddTwoStates(1, 2);
                            continue;
                        }
                        case 3: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(4);
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 38) {
                                kind = 38;
                            }
                            this.jjCheckNAddStates(17, 19);
                            continue;
                        }
                        case 6: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(7);
                                continue;
                            }
                            continue;
                        }
                        case 7: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 38) {
                                kind = 38;
                            }
                            this.jjCheckNAddTwoStates(7, 8);
                            continue;
                        }
                        case 10: {
                            if ((0xFFFFFFFBFFFFDBFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 12: {
                            if ((0x8400000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 13: {
                            if (this.curChar == '\"' && kind > 40) {
                                kind = 40;
                                continue;
                            }
                            continue;
                        }
                        case 15: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 16;
                                continue;
                            }
                            continue;
                        }
                        case 16: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 17: {
                            if ((0xFF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(20, 23);
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if ((0xFF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 19: {
                            if ((0xF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 20;
                                continue;
                            }
                            continue;
                        }
                        case 20: {
                            if ((0xFF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(18);
                                continue;
                            }
                            continue;
                        }
                        case 22: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 41) {
                                kind = 41;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            continue;
                        }
                        case 23: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(0, 6);
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(24, 25);
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if (this.curChar != '.') {
                                continue;
                            }
                            if (kind > 38) {
                                kind = 38;
                            }
                            this.jjCheckNAddStates(24, 26);
                            continue;
                        }
                        case 26: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 38) {
                                kind = 38;
                            }
                            this.jjCheckNAddStates(24, 26);
                            continue;
                        }
                        case 28: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(29);
                                continue;
                            }
                            continue;
                        }
                        case 29: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 38) {
                                kind = 38;
                            }
                            this.jjCheckNAddTwoStates(29, 8);
                            continue;
                        }
                        case 30: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(30, 31);
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(33);
                                continue;
                            }
                            continue;
                        }
                        case 33: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 38) {
                                kind = 38;
                            }
                            this.jjCheckNAddTwoStates(33, 8);
                            continue;
                        }
                        case 34: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(27, 29);
                                continue;
                            }
                            continue;
                        }
                        case 36: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(37);
                                continue;
                            }
                            continue;
                        }
                        case 37: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(37, 8);
                                continue;
                            }
                            continue;
                        }
                        case 38: {
                            if (this.curChar != '0') {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddStates(14, 16);
                            continue;
                        }
                        case 40: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddTwoStates(40, 2);
                            continue;
                        }
                        case 41: {
                            if ((0xFF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddTwoStates(41, 2);
                            continue;
                        }
                        case 42: {
                            if (this.curChar == '-') {
                                this.jjAddStates(7, 10);
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
            else if (this.curChar < '\u0080') {
                final long l = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 9: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 41) {
                                kind = 41;
                            }
                            this.jjCheckNAdd(22);
                            continue;
                        }
                        case 2: {
                            if ((0x100000001000L & l) != 0x0L && kind > 34) {
                                kind = 34;
                                continue;
                            }
                            continue;
                        }
                        case 5: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjAddStates(30, 31);
                                continue;
                            }
                            continue;
                        }
                        case 8: {
                            if ((0x5000000050L & l) != 0x0L && kind > 38) {
                                kind = 38;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 11: {
                            if (this.curChar == '\\') {
                                this.jjAddStates(32, 35);
                                continue;
                            }
                            continue;
                        }
                        case 12: {
                            if ((0x54404610000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 14: {
                            if ((0x100000001000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 15;
                                continue;
                            }
                            continue;
                        }
                        case 15: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 16;
                                continue;
                            }
                            continue;
                        }
                        case 16: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 22: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 41) {
                                kind = 41;
                            }
                            this.jjCheckNAdd(22);
                            continue;
                        }
                        case 27: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjAddStates(36, 37);
                                continue;
                            }
                            continue;
                        }
                        case 31: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjAddStates(38, 39);
                                continue;
                            }
                            continue;
                        }
                        case 35: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjAddStates(40, 41);
                                continue;
                            }
                            continue;
                        }
                        case 39: {
                            if ((0x100000001000000L & l) != 0x0L) {
                                this.jjCheckNAdd(40);
                                continue;
                            }
                            continue;
                        }
                        case 40: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddTwoStates(40, 2);
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else {
                final int i2 = (this.curChar & '\u00ff') >> 6;
                final long l2 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 10: {
                            if ((ProtoParserTokenManager.jjbitVec0[i2] & l2) != 0x0L) {
                                this.jjAddStates(11, 13);
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
            }
            catch (IOException e) {
                return curPos;
            }
        }
        return curPos;
    }
    
    private final int jjMoveStringLiteralDfa0_1() {
        return this.jjMoveNfa_1(0, 0);
    }
    
    private final int jjMoveNfa_1(final int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 3;
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
                        case 0: {
                            if ((0x2400L & l) != 0x0L && kind > 6) {
                                kind = 6;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && kind > 6) {
                                kind = 6;
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
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
            else if (this.curChar < '\u0080') {
                final long l = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (this.curChar == '|') {
                                kind = 6;
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
            else {
                final int i2 = (this.curChar & '\u00ff') >> 6;
                final long l2 = 1L << (this.curChar & '?');
                do {
                    final int n = this.jjstateSet[--i];
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            ++curPos;
            final int n2 = i = this.jjnewStateCnt;
            final int n3 = 3;
            final int jjnewStateCnt = startsAt;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n2 == (startsAt = n3 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException e) {
                return curPos;
            }
        }
        return curPos;
    }
    
    public ProtoParserTokenManager(final SimpleCharStream stream) {
        this.debugStream = System.out;
        this.jjrounds = new int[43];
        this.jjstateSet = new int[86];
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = stream;
    }
    
    public ProtoParserTokenManager(final SimpleCharStream stream, final int lexState) {
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
    
    private final void ReInitRounds() {
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
        if (lexState >= 2 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }
    
    protected Token jjFillToken() {
        final Token t = Token.newToken(this.jjmatchedKind);
        t.kind = this.jjmatchedKind;
        final String im = ProtoParserTokenManager.jjstrLiteralImages[this.jjmatchedKind];
        t.image = ((im == null) ? this.input_stream.GetImage() : im);
        t.beginLine = this.input_stream.getBeginLine();
        t.beginColumn = this.input_stream.getBeginColumn();
        t.endLine = this.input_stream.getEndLine();
        t.endColumn = this.input_stream.getEndColumn();
        return t;
    }
    
    public Token getNextToken() {
        Token specialToken = null;
        int curPos = 0;
    Label_0005_Outer:
        while (true) {
        Label_0005:
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
                this.image = null;
                this.jjimageLen = 0;
                while (true) {
                    switch (this.curLexState) {
                        case 0: {
                            try {
                                this.input_stream.backup(0);
                                while (this.curChar <= ' ' && (0x100002600L & 1L << this.curChar) != 0x0L) {
                                    this.curChar = this.input_stream.BeginToken();
                                }
                            }
                            catch (IOException e2) {
                                continue Label_0005;
                            }
                            this.jjmatchedKind = Integer.MAX_VALUE;
                            this.jjmatchedPos = 0;
                            curPos = this.jjMoveStringLiteralDfa0_0();
                            break;
                        }
                        case 1: {
                            this.jjmatchedKind = Integer.MAX_VALUE;
                            this.jjmatchedPos = 0;
                            curPos = this.jjMoveStringLiteralDfa0_1();
                            if (this.jjmatchedPos == 0 && this.jjmatchedKind > 7) {
                                this.jjmatchedKind = 7;
                                break;
                            }
                            break;
                        }
                    }
                    if (this.jjmatchedKind == Integer.MAX_VALUE) {
                        break Label_0005_Outer;
                    }
                    if (this.jjmatchedPos + 1 < curPos) {
                        this.input_stream.backup(curPos - this.jjmatchedPos - 1);
                    }
                    if ((ProtoParserTokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                        final Token matchedToken = this.jjFillToken();
                        matchedToken.specialToken = specialToken;
                        if (ProtoParserTokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                            this.curLexState = ProtoParserTokenManager.jjnewLexState[this.jjmatchedKind];
                        }
                        return matchedToken;
                    }
                    if ((ProtoParserTokenManager.jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) == 0x0L) {
                        this.jjimageLen += this.jjmatchedPos + 1;
                        if (ProtoParserTokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                            this.curLexState = ProtoParserTokenManager.jjnewLexState[this.jjmatchedKind];
                        }
                        curPos = 0;
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        try {
                            this.curChar = this.input_stream.readChar();
                            continue Label_0005_Outer;
                        }
                        catch (IOException ex) {}
                        break Label_0005_Outer;
                    }
                    if ((ProtoParserTokenManager.jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
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
                        this.SkipLexicalActions(matchedToken);
                    }
                    else {
                        this.SkipLexicalActions(null);
                    }
                    if (ProtoParserTokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = ProtoParserTokenManager.jjnewLexState[this.jjmatchedKind];
                        break;
                    }
                    break;
                }
                break;
            }
        }
        int error_line = this.input_stream.getEndLine();
        int error_column = this.input_stream.getEndColumn();
        String error_after = null;
        boolean EOFSeen = false;
        try {
            this.input_stream.readChar();
            this.input_stream.backup(1);
        }
        catch (IOException e3) {
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
    
    void SkipLexicalActions(final Token matchedToken) {
        final int jjmatchedKind = this.jjmatchedKind;
    }
    
    static {
        jjbitVec0 = new long[] { 0L, 0L, -1L, -1L };
        jjnextStates = new int[] { 24, 25, 30, 31, 34, 35, 8, 0, 3, 23, 38, 10, 11, 13, 39, 41, 2, 4, 5, 8, 10, 11, 18, 13, 26, 27, 8, 34, 35, 8, 6, 7, 12, 14, 17, 19, 28, 29, 32, 33, 36, 37 };
        jjstrLiteralImages = new String[] { "", null, null, null, null, null, null, null, "import", "package", "service", "rpc", "option", "message", "extensions", "extend", "enum", "group", "required", "optional", "repeated", "returns", "to", "max", "{", "}", "=", ";", "[", "]", "(", ")", ".", ",", null, null, null, null, null, null, null, null };
        lexStateNames = new String[] { "DEFAULT", "COMMENT" };
        jjnewLexState = new int[] { -1, -1, -1, -1, -1, 1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        jjtoToken = new long[] { 3607772528385L };
        jjtoSkip = new long[] { 126L };
        jjtoSpecial = new long[] { 96L };
        jjtoMore = new long[] { 128L };
    }
}
