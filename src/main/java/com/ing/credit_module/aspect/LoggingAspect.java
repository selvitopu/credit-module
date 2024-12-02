package com.ing.credit_module.aspect;


import com.ing.credit_module.model.ServiceRequestHistory;
import com.ing.credit_module.repository.IServiceRequestHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class LoggingAspect {

    private final IServiceRequestHistoryRepository requestHistoryRepository;

    private final ObjectMapper objectMapper;

    @Around("execution(* com.ing.credit_module.controller..*.*(..))")
    public Object serviceRequestLogger(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequestUri();
        Object result = proceedingJoinPoint.proceed();
        Signature signature = proceedingJoinPoint.getSignature();
        saveLog(proceedingJoinPoint.getArgs(), result, builder.build().getHost(), signature);
        return result;
    }

    @AfterThrowing(pointcut = "execution(* com.ing.credit_module.controller..*.*(..))", throwing = "ex")
    public void doRecoveryActions(JoinPoint joinPoint, Throwable ex) {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequestUri();

        Signature signature = joinPoint.getSignature();
        saveLog(joinPoint.getArgs(), ex, builder.build().getHost(), signature);
    }

    private void saveLog(Object[] args, Object result, String clientIP, Signature signature) {

        CompletableFuture.runAsync(() -> {
            try {
                MethodSignature methodSignature = (MethodSignature) signature;
                String className = methodSignature.getDeclaringType().getSimpleName();
                String methodName = methodSignature.getName();
                ServiceRequestHistory serviceRequestHistory = new ServiceRequestHistory();
                serviceRequestHistory.setMethodName(methodName);
                serviceRequestHistory.setClassName(className);

                String requestData = objectMapper.writeValueAsString(args);
                serviceRequestHistory.setRequestData(requestData);
                String responseString = objectMapper.writeValueAsString(result);
                serviceRequestHistory.setResponseData(responseString);
                serviceRequestHistory.setClientIP(clientIP);
                requestHistoryRepository.save(serviceRequestHistory);

            } catch (Exception e) {
                log.error("Error with saving request response to mongo db" + e.getMessage(), e);
            }
        });
    }

}
