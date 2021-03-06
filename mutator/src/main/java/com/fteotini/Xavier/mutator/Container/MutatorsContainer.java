package com.fteotini.Xavier.mutator.Container;

import com.fteotini.Xavier.mutator.IMutator;
import com.fteotini.Xavier.mutator.MutatorsBuilder;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.util.*;
import java.util.stream.Collectors;

public final class MutatorsContainer {
    private final List<MutatorsBuilder> mutatorsBuilders;
    private Map<String, Set<IMutator>> mutators;

    private MutatorsContainer(List<MutatorsBuilder> mutatorsBuilders) {
        this.mutatorsBuilders = mutatorsBuilders;
    }

    public static MutatorsContainer loadMutatorModules() {
        var mutatorsBuilders = ServiceLoader.load(MutatorModule.class)
                .stream()
                .flatMap(module -> module.get().registerAdditionalMutators().stream())
                .collect(Collectors.toUnmodifiableList());

        var loaded = new MutatorsContainer(mutatorsBuilders);
        loaded.loadMutators();
        return loaded;
    }

    public Set<IMutator> getAll() {
        return mutators.values().stream().flatMap(Collection::stream).collect(Collectors.toUnmodifiableSet());
    }

    public Set<IMutator> getMutatorsByMutationId(String mutationId) {
        return mutators.get(mutationId);
    }

    private static ScanResult buildScanResult() {
        return new ClassGraph()
                .enableAllInfo()
                .scan();
    }

    private void loadMutators() {
        try (var scanResult = buildScanResult()) {
            mutators = mutatorsBuilders.stream()
                    .collect(Collectors.toMap(MutatorsBuilder::uniqueMutationOperationId, builder -> builder.buildMutators(scanResult)));
        }
    }
}
