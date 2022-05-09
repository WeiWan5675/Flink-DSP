package com.weiwan.dsp.console.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Controller层日志切面
 * @author: xiaozhennan
 */
//@Aspect
@Component
@Slf4j
public class ControllerLogAspect {
    /**
     * 切入点： 所有Controller类的public方法，忽略@LogIgnore注解的方法
     */
//    @Pointcut("execution(public * com.weiwan.dsp.console..*Controller.*(..))&&!@annotation(com.weiwan.dsp.console.aspect.LogIgnore)")
    public void controllerLogPointcut() {
    }

//    @Around("controllerLogPointcut()")
    public Object aroundMethod(ProceedingJoinPoint point) throws Throwable {
        long time = System.currentTimeMillis();
        Signature signature = point.getSignature();
        try {
            log.info(">>> Begin execute {}, args: {}", signature, point.getArgs());
            Object object = point.proceed();
            log.info("<<< End execute {} in {} ms", signature, System.currentTimeMillis() - time);
            return object;
        } catch (Throwable e) {
            log.warn(">>> Execute {} has occurred exception: {}", signature, e.toString());
            throw e;
        }
    }

}
