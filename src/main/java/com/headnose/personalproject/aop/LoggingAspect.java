package com.headnose.personalproject.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingAspect.class);

    // Log entry into ALL service layer methods
    @Before("execution(* com.headnose.personalproject.service.*.*(..))")
    public void logBeforeService(JoinPoint joinPoint) {

        log.info("Entering method: {} with arguments: {}",
                joinPoint.getSignature().toShortString(),
                joinPoint.getArgs());
    }

    // Log successful completion of service methods
    @AfterReturning(
            pointcut = "execution(* com.headnose.personalproject.service.*.*(..))",
            returning = "result")
    public void logAfterService(JoinPoint joinPoint, Object result) {

        log.info("Method {} executed successfully",
                joinPoint.getSignature().toShortString());
    }

    // Log exceptions thrown from service layer
    @AfterThrowing(
            pointcut = "execution(* com.headnose.personalproject.service.*.*(..))",
            throwing = "ex")
    public void logServiceException(JoinPoint joinPoint, Exception ex) {

        log.error("Exception in method {}: {}",
                joinPoint.getSignature().toShortString(),
                ex.getMessage());
    }
}
