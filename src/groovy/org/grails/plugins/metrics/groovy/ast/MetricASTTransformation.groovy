package org.grails.plugins.metrics.groovy.ast

import com.codahale.metrics.Metric
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.grails.plugins.metrics.groovy.Metrics

import java.lang.reflect.Modifier


abstract class MetricASTTransformation implements ASTTransformation {

    protected abstract Class<? extends Metric> getMetricClass()

    @Override
    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        if ((!nodes) || (!nodes[0]) || (!nodes[1]) || (!(nodes[0] instanceof AnnotationNode)) || (!(nodes[1] instanceof MethodNode))) {
            throw new RuntimeException("Internal error: wrong types: $nodes/ $sourceUnit")
        }

        AnnotationNode annotationNode = (AnnotationNode) nodes[0]
        MethodNode methodNode = (MethodNode) nodes[1]
        String metricName = ensureMetricConfigured(annotationNode, methodNode.declaringClass, methodNode)

        try {
            makeMethod(metricName, methodNode)
        } catch (Throwable t) {
            // Not sure that this ia appropriate, but this WILL stop the build which IS what I want
            throw new RuntimeException("Unable to execute AST Transformation", t)
        }
    }

    abstract void makeMethod(String timerName, MethodNode methodNode) throws Throwable

    public String ensureMetricConfigured(AnnotationNode annotationNode, ClassNode classNode, MethodNode methodNode) {
        String metricName = methodNode.name + metricClass.simpleName

        // The metric name can be configured from the metric Annotation.
        Expression annotationName = annotationNode.getMember('name')
        if (annotationName && (annotationName in ConstantExpression) && annotationName.value) {
            metricName = ((ConstantExpression) annotationName).value
        }

        // Allowing the code author to define their own metric, we only write a new one if it was not found.
        if (!methodNode.declaringClass.fields.find { it.name == metricName }) {
            FieldNode metricField = new FieldNode(
                metricName,
                Modifier.PRIVATE,
                new ClassNode(metricClass),
                new ClassNode(classNode.getClass()),
                new StaticMethodCallExpression(
                    new ClassNode(Metrics.class),
                    "new" + metricClass.simpleName,
                    new ArgumentListExpression([
                        new ConstantExpression(metricName),
                    ])
                )
            )

            classNode.addField(metricField)
        }

        return metricName
    }
}