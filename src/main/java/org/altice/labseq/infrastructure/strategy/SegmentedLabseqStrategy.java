package org.altice.labseq.infrastructure.strategy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.altice.labseq.domain.LabseqStrategy;
import org.altice.labseq.util.LabseqCache;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;

@ApplicationScoped
@Named("labseq-parallel-segmented")
public class SegmentedLabseqStrategy implements LabseqStrategy {

    private final Map<Long, BigInteger> cache = LabseqCache.CACHE;
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final long BLOCK_SIZE = 100_000;

    @Override
    public BigInteger calculate(long n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        if (cache.containsKey(n)) return cache.get(n);

        long lastCached = cache.keySet().stream().mapToLong(Long::longValue).max().orElse(3L);
        long firstToCompute = lastCached + 1;
        long totalBlocks = (n - firstToCompute + 1 + BLOCK_SIZE - 1) / BLOCK_SIZE;

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Future<?>> tasks = new ArrayList<>();

        // Últimos 4 valores conhecidos
        BigInteger l0 = cache.get(lastCached - 3);
        BigInteger l1 = cache.get(lastCached - 2);
        BigInteger l2 = cache.get(lastCached - 1);
        BigInteger l3 = cache.get(lastCached);

        for (int block = 0; block < totalBlocks; block++) {
            final long start = firstToCompute + block * BLOCK_SIZE;
            final long end = Math.min(start + BLOCK_SIZE - 1, n);

            final BigInteger prev0 = l0;
            final BigInteger prev1 = l1;
            final BigInteger prev2 = l2;
            final BigInteger prev3 = l3;

            Future<?> future = executor.submit(() -> {
                BigInteger a = prev0;
                BigInteger b = prev1;
                BigInteger c = prev2;
                BigInteger d = prev3;

                for (long i = start; i <= end; i++) {
                    BigInteger result = a.add(b);
                    cache.put(i, result);
                    a = b;
                    b = c;
                    c = d;
                    d = result;
                }
                return null;
            });

            tasks.add(future);

            // Atualiza os últimos 4 valores do final do bloco para a próxima iteração
            long tail = Math.max(end - 3, start);
            l0 = cache.get(tail);
            l1 = cache.get(tail + 1);
            l2 = cache.get(tail + 2);
            l3 = cache.get(tail + 3);
        }

        // Aguarda todas as threads terminarem
        for (Future<?> task : tasks) {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Block computation failed", e);
            }
        }

        executor.shutdown();
        return cache.get(n);
    }
}
