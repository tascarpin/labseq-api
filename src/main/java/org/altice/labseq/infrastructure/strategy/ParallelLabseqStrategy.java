package org.altice.labseq.infrastructure.strategy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.altice.labseq.domain.LabseqStrategy;
import org.altice.labseq.util.LabseqCache;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.*;

@ApplicationScoped
@Named("parallel-labseq")
public class ParallelLabseqStrategy implements LabseqStrategy {

    private final Map<Long, BigInteger> cache = LabseqCache.CACHE;
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    @Override
    public BigInteger calculate(long n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        if (cache.containsKey(n)) return cache.get(n);

        long lastCached = 3L;

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        CompletionService<Void> completion = new ExecutorCompletionService<>(executor);

        for (long i = lastCached + 1; i <= n; i++) {
            final long index = i;
            completion.submit(() -> {
                BigInteger val = cache.get(index - 4).add(cache.get(index - 3));
                cache.put(index, val);
                return null;
            });
        }

        try {
            for (long i = 0; i < n - lastCached; i++) {
                completion.take();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }

        return cache.get(n);
    }
}
