package com.fujitsu.advice;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.aop.MethodBeforeAdvice;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.common.BaseDefine;
import com.fujitsu.util.BaseCommonUtil;

/**
 * @author xuxiaojun
 * 
 */
public class BeforeAdvice implements MethodBeforeAdvice {
	
	SimpleDateFormat sf = BaseCommonUtil.getDateFormatter(BaseDefine.COMMON_FORMAT);
	// 实现MethodBeforeAdvice的before方法
	public void before(Method method, Object[] args, Object target) {
		IMethodLog log = method.getAnnotation(IMethodLog.class);
		if(log!=null){
			System.out.println(sf.format(new Date())+" 操作日志："+log.desc()+" 开始");
		}
	}
}
