package com.itq.document_management_service.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DocumentProceedingAspect {

    @Around("@annotation(logDocumentProceeding)")
    public Object logAndMeasure(ProceedingJoinPoint pjp, LogDocumentProceeding logDocumentProceeding) throws Throwable {

        String methodName = pjp.getSignature().getName();

        long start = System.currentTimeMillis();
        log.info("Метод {} начал свое выполнение в {}", methodName, new Date(start).toInstant());

        Object result = pjp.proceed();

        long finish = System.currentTimeMillis();
        log.info("Метод {} закончил свое выполнение в {}", methodName, new Date(finish).toInstant());

        long durationMs = finish - start;
        log.info("Метод {} выполнен за {} мс", methodName, durationMs);

        return result;
    }
}
