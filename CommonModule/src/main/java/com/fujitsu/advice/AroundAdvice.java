package com.fujitsu.advice;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.struts2.ServletActionContext;

import com.fujitsu.IService.ILogManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.common.BaseDefine;
import com.fujitsu.common.BaseMessageCodeDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.util.BaseCommonUtil;

/**
 * @author xuxiaojun
 * 
 */
public class AroundAdvice implements MethodInterceptor {
	@Resource
	public ILogManagerService iLogManagerService;
	
	SimpleDateFormat sf = BaseCommonUtil.getDateFormatter(BaseDefine.COMMON_FORMAT);

	public Object invoke(MethodInvocation methodInterceptor) throws Throwable {
		Method method = methodInterceptor.getMethod();
		IMethodLog log = method.getAnnotation(IMethodLog.class);
		Object obj = null;
		boolean isSuccess = true;
		try{
			//---methodInterceptor.proceed之前可以添加前置操作，相当于MethodBeforeAdvice
			if(log!=null){
				System.out.println(sf.format(new Date())+" 操作日志："+log.desc()+" 开始");
			}
			//目标方法执行
			obj = methodInterceptor.proceed();
			
			//---methodInterceptor.proceed之后可以添加后续操作，相当于AfterReturningAdvice
			if(log!=null){
				System.out.println(sf.format(new Date())+" 操作日志："+log.desc()+" 结束");
			}
		} catch(CommonException e){
			isSuccess = false;
			//在执行目标对象方法的过程中，如果发生异常，可以在catch中捕获异常，相当于ThrowsAdvice
			ExceptionHandler.handleException(e);
			//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
//			throw e;
			throw new CommonException(new NullPointerException(),e.getErrorCode(),e.getErrorMessage());
		} catch(Exception e){
			isSuccess = false;
			ExceptionHandler.handleException(e);
			throw new CommonException(new NullPointerException(),BaseMessageCodeDefine.MESSAGE_CODE_999999);
		} finally{
			//记录操作日志
			recordOperate(log,isSuccess);
		}

		return obj;
	}
	
	//记录操作日志
	private void recordOperate(IMethodLog log,boolean isSuccess){
		try{
			if(log !=null){
				//删除或修改操作入库
				if(IMethodLog.InfoType.DELETE.equals(log.type()) || IMethodLog.InfoType.MOD.equals(log.type())){
					//数据入库
					// 操作日志入库
					HttpSession session = ServletActionContext.getRequest().getSession();
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("USER_ID", session.getAttribute("SYS_USER_ID"));
					paramMap.put("USER_NAME", session.getAttribute("USER_NAME"));
					if(session.getAttribute("SYS_USER_ID")!=null){
						List<Map<String, Object>> map = iLogManagerService.getUserGroupByUserId(Integer.parseInt(session.getAttribute("SYS_USER_ID").toString()));
						if(map!=null && map.size()>0){
							paramMap.put("USER_GROUP_ID", map.get(0).get("sys_user_group_id")!=null?map.get(0).get("sys_user_group_id"):null);
							paramMap.put("USER_GROUP_NAME", map.get(0).get("group_name")!=null?map.get(0).get("group_name"):null);
						}
					}
					paramMap.put("CREATE_TIME", new Date());
					paramMap.put("OPERATION", log.desc());
//					paramMap.put("ACTION_NAME", methodInterceptor.getMethod().getDeclaringClass().getSimpleName());
//					paramMap.put("METHOD_NAME", methodInterceptor.getMethod().getName());
					if(paramMap.get("USER_ID")==null){
						paramMap.put("USER_ID", -1);
						paramMap.put("USER_NAME", "");
					}
					iLogManagerService.JournalToMongodb(paramMap);
					
				}
//				if(isSuccess){
//					//数据输出控制台
//					System.out.println(sf.format(new Date())+" 操作日志："+log.desc()+" 成功！");
//				}else{
//					//数据输出控制台
//					System.out.println(sf.format(new Date())+" 操作日志："+log.desc()+" 失败！");
//				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
