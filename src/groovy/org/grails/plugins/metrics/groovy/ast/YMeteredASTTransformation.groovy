package org.grails.plugins.metrics.groovy.ast

import com.codahale.metrics.Meter
import com.codahale.metrics.Metric
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.grails.plugins.metrics.groovy.Metrics

import java.lang.reflect.Modifier

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*


@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class YMeteredASTTransformation extends MetricASTTransformation {

    @Override
    protected Class<? extends Metric> getMetricClass() {
        return Meter.class
    }

    void makeMethod(String meterName, MethodNode methodNode) throws Throwable {
        ExpressionStatement meterCall = new ExpressionStatement(new MethodCallExpression(new VariableExpression(meterName), "mark", new ArgumentListExpression()))
        methodNode.code.statements.add(0, meterCall)
    }
}