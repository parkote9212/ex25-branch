package com.pgc.mybatis.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ServiceLoggingAspect {

   /*
    * @Around란
    * @Before, @AfterReturning, @AfterThrowing 통합
    * 중재자(Interceptor) 또는 **프록시(Proxy)** 역할
    * "메소드 호출을 가로채서 일단 AOP 코드를 먼저 실행할게. 내가 허락하면(pjp.proceed()) 그때 원본 메소드를 실행해."
    * 실행 여부, 반환 값 수정 등 모든 제어권을 가짐
    */
    @Around(("execution(* com.pgc.mybatis.service.*.*(..))"))
    /*
      ProceedingJoinPoint
      ProceedingJoinPoint는 JoinPoint를 상속(extends)받은 자식 인터페이스입니다. 즉, JoinPoint의 모든 기능을 가지면서 매우 강력한 기능 하나가 추가되었습니다.
          * 주요 사용 어노테이션: @Around 에서만 사용됩니다.
      비유: "중재자(Interceptor)" 또는 "게이트키퍼(Gatekeeper)"
      역할: 원본 메소드의 실행 전/후를 모두 제어합니다.
      주요 기능 (가장 중요):
      Object proceed(): 이 메소드를 호출해야만 원본 메소드가 실제로 실행됩니다.
     */
    public Object measureTimeAndLog(ProceedingJoinPoint pjp) throws Throwable{

//        1. @Befoe(메소드 실행전)
        log.info("--- [AOP] 실행 시작: {}", pjp.getSignature().toShortString());
        long start = System.currentTimeMillis();

        try {
//            2. 원본 메소드 실행
            Object result = pjp.proceed();

//            3. @AfterReturning (메소드 성공시)
            long end = System.currentTimeMillis();
            log.info("--- [AOP] 실행 성공: {}, 실행 시간: {}ms", pjp.getSignature().toShortString(), (end - start));
            log.info("--- [AOP] 반환 값: {}", result);

            return result;
        } catch (Throwable e){
//            4. @AfterThrowing (메소드 예외시)
            long end = System.currentTimeMillis();
            log.error("--- [AOP] 예외 발생: {}, 실행 시간:  {}ms", pjp.getSignature().toShortString(), (end - start));
            log.error("--- [AOP] 예외 메세지: {}", e.getMessage());

//            5. 예외를 다시 던져 상위로 전파
            throw e; 
        }
    }
}
