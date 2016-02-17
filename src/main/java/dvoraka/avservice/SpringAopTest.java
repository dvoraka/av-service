package dvoraka.avservice;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * Aspect testing.
 */
@Aspect
public class SpringAopTest {

    @Before("execution(* dvoraka.avservice.*.listen(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Before");
    }

    @After("execution(* dvoraka.avservice.*.listen(..))")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("After");
    }
}
