package com.banco.bff.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* com.banco.bff.controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String traceId = resolveTraceId(joinPoint.getArgs());
        log.info("BFF - TraceId: {} - Metodo: {} - Argumentos: {}",
            traceId,
            joinPoint.getSignature().getName(),
            Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.banco.bff.controller.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("BFF - Metodo: {} - Retorno exitoso", joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "execution(* com.banco.bff.*.*.*(..))", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        log.error("BFF - Excepcion en metodo: {} - Error: {}",
            joinPoint.getSignature().getName(),
            error.getMessage());
    }

    private String resolveTraceId(Object[] args) {
        if (args == null) {
            return "NO_TRACE";
        }
        for (Object arg : args) {
            if (arg instanceof String str && !str.isBlank()) {
                return str;
            }
            if (arg != null) {
                try {
                    Method method = arg.getClass().getMethod("getTraceId");
                    Object value = method.invoke(arg);
                    if (value != null && !value.toString().isBlank()) {
                        return value.toString();
                    }
                } catch (Exception ignored) {
                    // ignore and continue
                }
            }
        }
        return "NO_TRACE";
    }
}
