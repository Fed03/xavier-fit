package com.fteotini.amf.mutator;

import io.github.classgraph.ScanResult;

import java.util.Set;

public interface Operator {
    Set<MutationDetails> findMutations(ScanResult scanResult);
}