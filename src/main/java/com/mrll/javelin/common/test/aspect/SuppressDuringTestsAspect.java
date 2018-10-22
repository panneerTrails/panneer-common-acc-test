package com.mrll.javelin.common.test.aspect;

import com.mrll.javelin.common.test.filter.TestSuppressionContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

@Aspect
@Component
public class SuppressDuringTestsAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuppressDuringTestsAspect.class);

    @Around("@within(com.mrll.javelin.common.test.aspect.SuppressDuringTests)")
    public Object manageTransaction(ProceedingJoinPoint pjp) throws NoSuchMethodException {

        if (TestSuppressionContext.isSupressionOn()) {
            LOGGER.info("message=suppressingMethod", new Exception("Diagnostic exception. I exist so you can see my stacktrace."));
            Method method = getMethod(pjp);
            if (returnTypeIsNotVoid(method)) {
                LOGGER.error("message=cannotSuppressNonVoidMethod method={}", method.getName());
                throw new IllegalStateException("Suppressing Methods should only be able to return void.");
            }
            return null;
        }

        return executeMethodRegularly(pjp);
    }

    private Object executeMethodRegularly(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        //This is really gross, but appeases sonar for now. It does not like catching or throwing "Throwable", even
        //if it is outside of our control in a 3rd party lib.
        return ReflectionUtils.invokeMethod(ProceedingJoinPoint.class.getDeclaredMethod("proceed"), pjp);
    }

    private boolean returnTypeIsNotVoid(Method method) {
        Class returnType = method.getReturnType();
        return !returnType.equals(Void.TYPE);
    }

    private Method getMethod(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        return signature.getMethod();
    }
}
