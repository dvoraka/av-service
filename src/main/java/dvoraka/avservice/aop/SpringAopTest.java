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

    @Around("execution(* dvoraka.avservice.AVService.*(..))")
    public boolean logTime(ProceedingJoinPoint pjp) {

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

        System.out.println("T: " + threadName + ", M: " + methodName + ", S: " + (start / 1000) + ", Duration: " + elapsedTime);

        return returnValue;
    }

//    long start = System.currentTimeMillis();
//                System.out.println("Going to call the method.");
//                Object output = pjp.proceed();
//                System.out.println("Method execution completed.");
//                long elapsedTime = System.currentTimeMillis() - start;
//                System.out.println("Method execution time: " + elapsedTime + " milliseconds.");
//                return output;
}
