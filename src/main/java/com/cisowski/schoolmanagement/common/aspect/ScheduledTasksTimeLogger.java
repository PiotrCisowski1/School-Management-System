package com.cisowski.schoolmanagement.common.aspect;

import com.cisowski.schoolmanagement.common.utility.DbLogger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class ScheduledTasksTimeLogger {

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void logScheduledTaskExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String taskClassName = proceedingJoinPoint.getTarget().getClass().toString();
        DbLogger.info(String.format("Starting '%s' scheduled task at %s", taskClassName, LocalDateTime.now()));

        long startTime = System.currentTimeMillis();
        proceedingJoinPoint.proceed();
        long actualExecutionTime = System.currentTimeMillis() - startTime;

        DbLogger.info(String.format("'%s' scheduled task execution time: %s ms", taskClassName, actualExecutionTime));
    }
}
