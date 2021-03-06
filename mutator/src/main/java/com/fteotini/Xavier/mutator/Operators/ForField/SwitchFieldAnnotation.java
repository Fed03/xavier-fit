package com.fteotini.Xavier.mutator.Operators.ForField;

import com.fteotini.Xavier.mutator.MutationIdentifiers.FieldIdentifier;
import com.fteotini.Xavier.mutator.Operators.Base.FieldOperator;
import com.fteotini.Xavier.mutator.Visitors.AnnotationVisitors.RemoveAnnotation;
import com.fteotini.Xavier.mutator.Visitors.Chain;
import com.fteotini.Xavier.mutator.Visitors.ForField;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.asm.MemberAttributeExtension;
import net.bytebuddy.description.annotation.AnnotationDescription;

import java.lang.annotation.Annotation;

public class SwitchFieldAnnotation<S extends Annotation, T extends Annotation> extends FieldOperator {

    private final Class<S> srcAnnotation;
    private final Class<T> trgAnnotation;

    protected SwitchFieldAnnotation(ByteBuddy byteBuddy, Class<S> srcAnnotation, Class<T> trgAnnotation) {
        super(byteBuddy);
        this.srcAnnotation = srcAnnotation;
        this.trgAnnotation = trgAnnotation;
    }

    @Override
    protected AsmVisitorWrapper visitor(FieldIdentifier identifier) {
        return new Chain(
                new ForField(new RemoveAnnotation<>(srcAnnotation), getMatcher(identifier)),
                new MemberAttributeExtension.ForField().annotate(AnnotationDescription.Builder.ofType(trgAnnotation).build()).on(getMatcher(identifier))
        );
    }
}
