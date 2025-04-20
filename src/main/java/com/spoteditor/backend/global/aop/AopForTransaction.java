package com.spoteditor.backend.global.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Component
public class AopForTransaction {

	// 트랜잭션 전파 옵션 : 독립된 트랜잭션을 생성하는 REQUIRES_NEW
	@Transactional(propagation = REQUIRES_NEW)
	public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
		return joinPoint.proceed();
	}
}
