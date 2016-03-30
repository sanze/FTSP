package com.fujitsu.advice;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.aop.AfterReturningAdvice;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.common.BaseDefine;
import com.fujitsu.util.BaseCommonUtil;

/**
 * @author xuxiaojun
 * 
 */
public class AfterAdvice implements AfterReturningAdvice {
	
	SimpleDateFormat sf = BaseCommonUtil.getDateFormatter(BaseDefine.COMMON_FORMAT);
	
	public void afterReturning(Object returnValue, Method method,
			Object[] args, Object target) throws Throwable {
		IMethodLog log = method.getAnnotation(IMethodLog.class);
		if(log!=null){
			System.out.println(sf.format(new Date())+" 操作日志："+log.desc()+" 结束");
		}
	}
}
