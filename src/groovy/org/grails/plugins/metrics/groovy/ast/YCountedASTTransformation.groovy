package org.grails.plugins.metrics.groovy.ast

import com.codahale.metrics.Counter
import com.codahale.metrics.Metric
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.transform.GroovyASTTransformation


@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class YCountedASTTransformation extends MetricASTTransformation {

    @Override
    protected Class<? extends Metric> getMetricClass() {
        return Counter.class
    }

    void makeMethod(String meterName, MethodNode methodNode) throws Throwable {
        ExpressionStatement metricCall = new ExpressionStatement(new MethodCallExpression(new VariableExpression(meterName), "inc", new ArgumentListExpression()))
        methodNode.code.statements.add(0, metricCall)
    }
}