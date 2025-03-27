package org.altice.labseq.controller;

import org.altice.labseq.infrastructure.strategy.BatchedLabseqStrategy;
import org.altice.labseq.infrastructure.strategy.IterativeLabseqStrategy;
import org.altice.labseq.infrastructure.strategy.SegmentedLabseqStrategy;
import org.altice.labseq.util.LabseqCache;
import org.junit.jupiter.api.*;

import java.math.BigInteger;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LabseqStrategyTest {

    private final Map<Long, BigInteger> cache = LabseqCache.CACHE;

    @BeforeEach
    void cleanCache() {
        cache.clear();
        cache.put(0L, BigInteger.ZERO);
        cache.put(1L, BigInteger.ONE);
        cache.put(2L, BigInteger.ZERO);
        cache.put(3L, BigInteger.ONE);
    }

    @Test
    void testIterativeStrategy() {
        IterativeLabseqStrategy strategy = new IterativeLabseqStrategy();
        BigInteger result = strategy.calculate(10);
        assertEquals(BigInteger.valueOf(3), result);
    }

    @Test
    void testBatchedStrategy() {
        BatchedLabseqStrategy strategy = new BatchedLabseqStrategy();
        BigInteger result = strategy.calculate(10000);
        assertNotNull(result);
        System.out.println("Batched Result (10000): " + result);
    }

    @Test
    void testSegmentedStrategy() {
        SegmentedLabseqStrategy strategy = new SegmentedLabseqStrategy();
        BigInteger result = strategy.calculate(100_000);
        assertNotNull(result);
        System.out.println("Segmented Result (100000): " + result);
    }

    @Test
    void testConsistencyBetweenStrategies() {
        IterativeLabseqStrategy iterative = new IterativeLabseqStrategy();
        BatchedLabseqStrategy batched = new BatchedLabseqStrategy();
        SegmentedLabseqStrategy segmented = new SegmentedLabseqStrategy();

        BigInteger r1 = iterative.calculate(5000);
        BigInteger r2 = batched.calculate(5000);
        BigInteger r3 = segmented.calculate(5000);

        assertEquals(r1, r2);
        assertEquals(r1, r3);
    }
}
