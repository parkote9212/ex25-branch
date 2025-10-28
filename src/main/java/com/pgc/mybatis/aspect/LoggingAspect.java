package com.pgc.mybatis.aspect;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.pgc.mybatis.service.*.*(..))")
    public void logBefore(JoinPoint jp){
        log.info("--- 메소드 실행전: {}", jp.getSignature());
    }

    @AfterReturning(pointcut = "execution(* com.pgc.mybatis.service.*.*(..))", returning = "result")
    public void logAfter(JoinPoint jp, Object result){
        log.info("##### 메서드 실행성공: {}, \n결과: {} #####AfterReturning#####", jp.getSignature(),result);
    }
}
