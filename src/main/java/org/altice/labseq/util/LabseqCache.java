package org.altice.labseq.util;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LabseqCache {
    public static final Map<Long, BigInteger> CACHE = new ConcurrentHashMap<>();

    static {
        CACHE.put(0L, BigInteger.ZERO);
        CACHE.put(1L, BigInteger.ONE);
        CACHE.put(2L, BigInteger.ZERO);
        CACHE.put(3L, BigInteger.ONE);
    }
}
