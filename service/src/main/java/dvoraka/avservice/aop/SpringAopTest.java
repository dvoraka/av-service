package dvoraka.avservice.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Aspect testing.
 */
@Aspect
public class SpringAopTest {

//    @Before("execution(* dvoraka.avservice.MessageProcessor.*(..))")
//    public void logBefore(JoinPoint joinPoint) {
//        System.out.println("Before");
//    }
//
//    @After("execution(* dvoraka.avservice.MessageProcessor.*(..))")
//    public void logAfter(JoinPoint joinPoint) {
//        System.out.println("After");
//    }

    @Around("execution(* dvoraka.avservice.service.AVService.*(..))")
    public boolean printInfo(ProceedingJoinPoint pjp) {

        long start = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();
        String methodName = pjp.getSignature().getName();

        boolean returnValue = false;
        try {
            returnValue = (Boolean) pjp.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        long elapsedTime = System.currentTimeMillis() - start;

        final long msPerSec = 1000;
        System.out.println("T: " + threadName + ", M: " + methodName
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
            throwable.printStackTrace();
        }
        long elapsedTime = System.currentTimeMillis() - start;

        final long msPerSec = 1000;
        System.out.println("T: " + threadName + ", M: " + methodName
                + ", S: " + (start / msPerSec) + ", Duration: " + elapsedTime);

        return returnValue;
    }
}
