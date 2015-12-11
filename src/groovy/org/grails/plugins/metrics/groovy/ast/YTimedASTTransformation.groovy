package org.grails.plugins.metrics.groovy.ast

import com.codahale.metrics.Metric
import com.codahale.metrics.Timer
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.TryCatchStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.grails.plugins.metrics.groovy.Metrics

import java.lang.reflect.Modifier


@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class YTimedASTTransformation extends MetricASTTransformation {

    @Override
    protected Class<? extends Metric> getMetricClass() {
        return Timer.class
    }

    void makeMethod(String timerName, MethodNode methodNode) throws Throwable {
        final String contextVariableName = "ctx" + timerName

        //Create a new method, containing the code from the method we are visiting, add to classNode
        //Note that the new method is private since there is no expectation for clients to call it directly
        def newMethodNode = new MethodNode("_do_${methodNode.name}", Modifier.PRIVATE, methodNode.returnType, methodNode.parameters, methodNode.exceptions, methodNode.code)
        methodNode.declaringClass.addMethod(newMethodNode)

        //Create a call statement to call our new method
        def methodCall = new ExpressionStatement(new MethodCallExpression(new VariableExpression('this'), "_do_${methodNode.name}", new ArgumentListExpression(methodNode.parameters)))

        //Generate statement to start the timer
        def contextAssignmentStatement = new ExpressionStatement(
            new DeclarationExpression(
                new VariableExpression(contextVariableName),
                Token.newSymbol(Types.EQUALS, 0, 0),
                new MethodCallExpression(new VariableExpression(timerName), "time", new ArgumentListExpression())
            )
        )

        //Generate the statement to stop the timer
        def contextStopStatement = new ExpressionStatement(
            new MethodCallExpression(
                new VariableExpression(contextVariableName), "stop", new ArgumentListExpression()
            )
        )

        //New Empty Block to contain method statents
        def mBlock = new BlockStatement([], new VariableScope())

        //Add start statement, outside of the try block
        mBlock.statements.add(contextAssignmentStatement)

        //Create and add try/finally, the try will consist of our original method call
        mBlock.statements.add(new TryCatchStatement(methodCall, contextStopStatement))

        //rewrite the method
        methodNode.code = mBlock
    }
}
