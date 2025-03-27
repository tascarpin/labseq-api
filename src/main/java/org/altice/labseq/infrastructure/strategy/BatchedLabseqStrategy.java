package org.altice.labseq.infrastructure.strategy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.altice.labseq.domain.LabseqStrategy;
import org.altice.labseq.util.LabseqCache;
import org.altice.labseq.util.LabseqHelper;

import java.math.BigInteger;
import java.util.Map;

@ApplicationScoped
@Named("labseq-batched")
public class BatchedLabseqStrategy implements LabseqStrategy {

    private final Map<Long, BigInteger> cache = LabseqCache.CACHE;

    @Override
    public BigInteger calculate(long n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        if (cache.containsKey(n)) return cache.get(n);

        long last = 3L;
        long batchSize = 64;

        for (long batchStart = last + 1; batchStart <= n; batchStart += batchSize) {
            long batchEnd = Math.min(batchStart + batchSize - 1, n);

            for (long i = batchStart; i <= batchEnd; i++) {
                BigInteger a = LabseqHelper.getOrCompute(i - 4, cache);
                BigInteger b = LabseqHelper.getOrCompute(i - 3, cache);
                BigInteger value = a.add(b);
                cache.put(i, value);
            }
        }

        return cache.get(n);
    }
}
