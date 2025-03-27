package org.altice.labseq.util;

import org.altice.labseq.infrastructure.strategy.IterativeLabseqStrategy;

import java.math.BigInteger;
import java.util.Map;

public class LabseqHelper {

    private static final IterativeLabseqStrategy fallback = new IterativeLabseqStrategy();

    public static BigInteger getOrCompute(long n, Map<Long, BigInteger> cache) {
        return cache.computeIfAbsent(n, fallback::calculate);
    }
}
