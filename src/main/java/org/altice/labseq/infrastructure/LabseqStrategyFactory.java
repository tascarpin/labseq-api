package org.altice.labseq.infrastructure;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.altice.labseq.domain.LabseqStrategy;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class LabseqStrategyFactory {

    private final Map<String, LabseqStrategy> strategyMap = new HashMap<>();

    @Inject
    public LabseqStrategyFactory(@Any Instance<LabseqStrategy> strategies) {
        for (LabseqStrategy strategy : strategies) {

            Class<?> clazz = unwrapProxy(strategy.getClass());

            Named named = clazz.getAnnotation(Named.class);

            if (named != null) {
                strategyMap.put(named.value().toLowerCase(), strategy);
            }
        }
    }

    public LabseqStrategy getStrategy(String name) {
        LabseqStrategy strategy = strategyMap.get(name.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown strategy: " + name);
        }
        return strategy;
    }

    private static Class<?> unwrapProxy(Class<?> clazz) {
        while (clazz.getName().contains("$$") || clazz.getSimpleName().contains("_ClientProxy")) {
            clazz = clazz.getSuperclass();
        }
        return clazz;
    }
}
