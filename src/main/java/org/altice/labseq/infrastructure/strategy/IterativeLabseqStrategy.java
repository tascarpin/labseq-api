package org.altice.labseq.infrastructure.strategy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.altice.labseq.domain.LabseqStrategy;
import org.altice.labseq.util.LabseqCache;
import org.altice.labseq.util.LabseqHelper;

import java.math.BigInteger;
import java.util.Map;

@ApplicationScoped
@Named("iterative-labseq")
public class IterativeLabseqStrategy implements LabseqStrategy {

    private final Map<Long, BigInteger> cache = LabseqCache.CACHE;

    @Override
    public BigInteger calculate(long n) {
        if (n < 0) return BigInteger.ZERO;
        if (n > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Iterative strategy supports up to Integer.MAX_VALUE");

        if (cache.containsKey(n)) return cache.get(n);

        long last = 3L;

        for (long i = last + 1; i <= n; i++) {
            BigInteger a = LabseqHelper.getOrCompute(i - 4, cache);
            BigInteger b = LabseqHelper.getOrCompute(i - 3, cache);
            BigInteger value = a.add(b);
            cache.put(i, value);
        }

        return cache.get(n);
    }
}
