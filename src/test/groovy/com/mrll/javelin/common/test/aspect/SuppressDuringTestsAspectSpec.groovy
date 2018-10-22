package com.mrll.javelin.common.test.aspect

import com.mrll.javelin.common.test.filter.TestSuppressionContext
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature
import spock.lang.Specification
import spock.lang.Subject

import java.lang.reflect.Method

class SuppressDuringTestsAspectSpec extends Specification {
    ProceedingJoinPoint pjp = Mock()
    MethodSignature methodSignature = Mock()

    @Subject
    SuppressDuringTestsAspect debugSuppressAspect

    def setup() {
        pjp = Mock()
        debugSuppressAspect = new SuppressDuringTestsAspect()
        TestSuppressionContext.clear()
    }

    def 'manageTransaction returns null if suppression is on'() {
        given:
        TestSuppressionContext.turnSuppressionOn()

        when:
        def result = debugSuppressAspect.manageTransaction(pjp)

        then:
        1 * pjp.getSignature() >> methodSignature
        1 * methodSignature.getMethod() >> testMethodVoid()
        result == null
    }

    def 'manageTransaction executes method when suppression off'() {
        given:
        TestSuppressionContext.turnSuppressionOff()

        when:
        def result = debugSuppressAspect.manageTransaction(pjp)

        then:
        0 * pjp.getSignature()
        0 * methodSignature.getMethod()
        1 * pjp.proceed() >> nonVoidMethod()
        result == 1
    }

    def 'manageTransaction throws exception when used with non-void method and suppression on'() {
        given:
        TestSuppressionContext.turnSuppressionOn()

        when:
        debugSuppressAspect.manageTransaction(pjp)

        then:
        1 * pjp.getSignature() >> methodSignature
        1 * methodSignature.getMethod() >> testVoidMethod()
        thrown(IllegalStateException.class)
    }

    private Method testMethodVoid() {
        return getClass().getDeclaredMethod('methodWithVoidReturn')
    }

    private Method testVoidMethod() {
        return getClass().getDeclaredMethod('nonVoidMethod')
    }

    private static void methodWithVoidReturn() {
        //does nothing
    }

    private static int nonVoidMethod() {
        return 1
    }
}
