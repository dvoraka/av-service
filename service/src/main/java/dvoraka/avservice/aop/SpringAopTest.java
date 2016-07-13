package dvoraka.avservice.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * Aspect testing.
 */
@Aspect
public class SpringAopTest {

    private static final Logger log = LogManager.getLogger(SpringAopTest.class.getName());

    @Before("execution(* dvoraka.avservice.MessageProcessor.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.debug("Before: " + joinPoint.getSignature().getName());
    }

    @After("execution(* dvoraka.avservice.MessageProcessor.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        log.debug("After: " + joinPoint.getSignature().getName());
    }

    @Around("execution(* dvoraka.avservice.service.AvService.*(..))")
    public boolean printInfo(ProceedingJoinPoint pjp) {

        long start = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();
        String methodName = pjp.getSignature().getName();

        boolean returnValue = false;
        try {
            returnValue = (Boolean) pjp.proceed();
        } catch (Throwable throwable) {
            log.warn(throwable);
        }
        long elapsedTime = System.currentTimeMillis() - start;

        final long msPerSec = 1000;
        log.debug("T: " + threadName + ", M: " + methodName
                + ", S: " + (start / msPerSec) + ", Duration: " + elapsedTime);

        return returnValue;
    }

    @Around("execution(* org.springframework.amqp.rabbit.core.RabbitTemplate.receive(..))")
    public Object printRabbitReceiveInfo(ProceedingJoinPoint pjp) {

        long start = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();
        String methodName = pjp.getSignature().getName();

        Object returnValue = null;
        try {
            returnValue = pjp.proceed();
        } catch (Throwable throwable) {
            log.warn(throwable);
        }
        long elapsedTime = System.currentTimeMillis() - start;

        final long msPerSec = 1000;
        log.debug("T: " + threadName + ", M: " + methodName
                + ", S: " + (start / msPerSec) + ", Duration: " + elapsedTime);

        return returnValue;
    }
}
