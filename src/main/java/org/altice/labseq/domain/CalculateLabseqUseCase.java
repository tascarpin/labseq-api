package org.altice.labseq.domain;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.altice.labseq.infrastructure.LabseqStrategyFactory;

import java.math.BigInteger;

@ApplicationScoped
public class CalculateLabseqUseCase {

    @Inject
    LabseqStrategyFactory factory;

    @ConfigProperty(name = "labseq.threshold.iterative", defaultValue = "1000")
    long iterativeThreshold;

    @ConfigProperty(name = "labseq.threshold.batched", defaultValue = "100000")
    long batchedThreshold;

    public BigInteger calculate(long n) {
        String method = selectStrategyName(n);
        LabseqStrategy strategy = factory.getStrategy(method);

        return strategy.calculate(n);
    }

    private String selectStrategyName(long n) {
        return switch (getRange(n)) {
            case SMALL -> "iterative-labseq";
            case MEDIUM -> "labseq-batched";
            case LARGE -> "labseq-parallel-segmented";
        };
    }

    private Range getRange(long n) {
        if (n <= iterativeThreshold) return Range.SMALL;
        if (n <= batchedThreshold) return Range.MEDIUM;
        return Range.LARGE;
    }

    private enum Range {
        SMALL, MEDIUM, LARGE
    }
}
