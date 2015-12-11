package org.grails.plugins.metrics.groovy.annotation

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target


@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.METHOD])
@GroovyASTTransformationClass(["org.grails.plugins.metrics.groovy.ast.YCountedASTTransformation"])
public @interface Counted {
    String name() default "";
}
