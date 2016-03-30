package com.fujitsu.filter;

import java.util.Map;

import net.sf.json.JSONObject;

import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.handler.MessageHandler;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AuthInterceptor extends AbstractInterceptor {

	/**
	 * 拦截器，验证用户是否登录
	 */
	private static final long serialVersionUID = -7481471340154128227L;

	public String intercept(ActionInvocation invocation) throws Exception {
		ActionContext ctx = invocation.getInvocationContext();
		Map session = ctx.getSession();
		String actionName = invocation.getInvocationContext().getName();
		if (actionName.equals("login")
				||actionName.equals("cancelOperation")
				||actionName.equals("getProcessPercent")
				||session.get("SYS_USER_ID")!=null) {
			return invocation.invoke();
		}
		//登陆信息失效返回统一消息
		CommonResult result=new CommonResult();
		result.setReturnResult(CommonDefine.FAILED);
		result.setReturnMessage(MessageHandler.getErrorMessage(MessageCodeDefine.USER_LOGIN_AGAIN));
		((AbstractAction)invocation.getAction()).setResultObj(JSONObject.fromObject(result));
		return AbstractAction.RESULT_OBJ;
	}
}
