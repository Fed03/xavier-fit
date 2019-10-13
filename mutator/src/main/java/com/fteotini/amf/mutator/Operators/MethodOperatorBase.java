package com.fteotini.amf.mutator.Operators;

import com.fteotini.amf.mutator.MutationDetails;
import com.fteotini.amf.mutator.MutationIdentifiers.MethodIdentifier;
import net.bytebuddy.ByteBuddy;

import java.util.Optional;

abstract class MethodOperatorBase extends OperatorBase<MethodIdentifier> {

    public MethodOperatorBase(ByteBuddy byteBuddy) {
        super(byteBuddy);
    }

    @Override
    protected final String className(MethodIdentifier identifier) {
        return identifier.getBelongingClass().getName();
    }

    @Override
    protected final Optional<MethodIdentifier> getMutationTarget(MutationDetails mutationDetails) {
        return mutationDetails.getMethodIdentifier();
    }
}
