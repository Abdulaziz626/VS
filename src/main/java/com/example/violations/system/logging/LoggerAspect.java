package com.example.violations.system.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggerAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

    @Around("execution(* com.example.violations.system.controller..*(..)) || execution(* com.example.violations.system.service..*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Started execution of method: {}", methodName);

        Object[] arguments = joinPoint.getArgs();
        if (arguments.length > 0) {
            logger.info("Method arguments: {}", (Object) arguments);
        } else {
            logger.info("Method has no arguments.");
        }

        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception ex) {
            logger.error("Exception while executing method: {}", methodName, ex);
            throw ex;
        }
        long endTime = System.currentTimeMillis();

        logger.info("Completed execution of method: {} in {}ms", methodName, (endTime - startTime));
        logger.info("Method return value: {}", result);

        return result;
    }
}
