// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.util.LinkedList;
import java.io.Serializable;

public class BitArrayBin implements Serializable
{
    private static final long serialVersionUID = 1L;
    private LinkedList<BitArray> list;
    private int maxNumberOfArrays;
    private int firstIndex;
    private long lastInOrderBit;
    
    public BitArrayBin(final int windowSize) {
        this.firstIndex = -1;
        this.lastInOrderBit = -1L;
        this.maxNumberOfArrays = (windowSize + 1) / 64 + 1;
        this.maxNumberOfArrays = Math.max(this.maxNumberOfArrays, 1);
        this.list = new LinkedList<BitArray>();
        for (int i = 0; i < this.maxNumberOfArrays; ++i) {
            this.list.add(null);
        }
    }
    
    public boolean setBit(final long index, final boolean value) {
        boolean answer = false;
        final BitArray ba = this.getBitArray(index);
        if (ba != null) {
            final int offset = this.getOffset(index);
            if (offset >= 0) {
                answer = ba.set(offset, value);
            }
        }
        return answer;
    }
    
    public boolean isInOrder(final long index) {
        boolean result = false;
        result = (this.lastInOrderBit == -1L || this.lastInOrderBit + 1L == index);
        this.lastInOrderBit = index;
        return result;
    }
    
    public boolean getBit(final long index) {
        boolean answer = index >= this.firstIndex;
        final BitArray ba = this.getBitArray(index);
        if (ba != null) {
            final int offset = this.getOffset(index);
            if (offset >= 0) {
                answer = ba.get(offset);
                return answer;
            }
        }
        else {
            answer = true;
        }
        return answer;
    }
    
    private BitArray getBitArray(final long index) {
        int bin = this.getBin(index);
        BitArray answer = null;
        if (bin >= 0) {
            if (bin >= this.maxNumberOfArrays) {
                for (int overShoot = bin - this.maxNumberOfArrays + 1; overShoot > 0; --overShoot) {
                    this.list.removeFirst();
                    this.firstIndex += 64;
                    this.list.add(new BitArray());
                }
                bin = this.maxNumberOfArrays - 1;
            }
            answer = this.list.get(bin);
            if (answer == null) {
                answer = new BitArray();
                this.list.set(bin, answer);
            }
        }
        return answer;
    }
    
    private int getBin(final long index) {
        int answer = 0;
        if (this.firstIndex < 0) {
            this.firstIndex = (int)(index - index % 64L);
        }
        else if (this.firstIndex >= 0) {
            answer = (int)((index - this.firstIndex) / 64L);
        }
        return answer;
    }
    
    private int getOffset(final long index) {
        int answer = 0;
        if (this.firstIndex >= 0) {
            answer = (int)(index - this.firstIndex - 64 * this.getBin(index));
        }
        return answer;
    }
    
    public long getLastSetIndex() {
        long result = -1L;
        if (this.firstIndex >= 0) {
            result = this.firstIndex;
            BitArray last = null;
            for (int lastBitArrayIndex = this.maxNumberOfArrays - 1; lastBitArrayIndex >= 0; --lastBitArrayIndex) {
                last = this.list.get(lastBitArrayIndex);
                if (last != null) {
                    result += last.length() - 1;
                    result += lastBitArrayIndex * 64;
                    break;
                }
            }
        }
        return result;
    }
}
