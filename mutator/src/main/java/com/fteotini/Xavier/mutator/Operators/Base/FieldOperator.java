package com.fteotini.Xavier.mutator.Operators.Base;

import com.fteotini.Xavier.mutator.IMutationTarget;
import com.fteotini.Xavier.mutator.MutationIdentifiers.FieldIdentifier;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.named;

public abstract class FieldOperator extends MemberOperator<FieldIdentifier, FieldDescription.InDefinedShape> {
    protected FieldOperator(ByteBuddy byteBuddy) {
        super(byteBuddy);
    }

    FieldOperator(ByteBuddy byteBuddy, ClassReloadingStrategy classLoadingStrategy) {
        super(byteBuddy, classLoadingStrategy);
    }

    @Override
    protected ElementMatcher<? super FieldDescription.InDefinedShape> getMatcher(FieldIdentifier identifier) {
        return named(identifier.getName());
    }

    @Override
    protected final String className(FieldIdentifier identifier) {
        return identifier.getBelongingClass().getName();
    }

    @Override
    protected final Optional<FieldIdentifier> getIdentifier(IMutationTarget mutationDetailsInterface) {
        return mutationDetailsInterface.getFieldIdentifier();
    }
}
