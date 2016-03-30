package com.fujitsu.advice;

import java.lang.reflect.Method;

import org.springframework.aop.ThrowsAdvice;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.BaseMessageCodeDefine;
import com.fujitsu.handler.ExceptionHandler;

/**
 * @author xuxiaojun
 * 
 */
public class AfterThorwsAdvice implements ThrowsAdvice {

	public void afterThrowing(Method method, Object[] args, Object target,Throwable e) {
		
		if(CommonException.class.isInstance(e)){
			ExceptionHandler.handleException((CommonException) e);
		}
		else if(Exception.class.isInstance(e)){
			CommonException exception = new CommonException((Exception) e,BaseMessageCodeDefine.MESSAGE_CODE_999999);
			ExceptionHandler.handleException(exception);
		}
	}
}
