package com.fujitsu.servlet;

import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionListener;  
import javax.servlet.http.HttpSessionEvent;

import com.fujitsu.IService.ISystemManagerService;
import com.fujitsu.util.SpringContextUtil;

public class SessionListener implements HttpSessionListener{
	
	public static ISystemManagerService systemManagerService;
	
	private static Integer numSessions =0;//在线用户数统计
	/* Session创建事件 */
	public void sessionCreated(HttpSessionEvent event) {
		ServletContext ctx = event.getSession().getServletContext( );  
		numSessions = (Integer) ctx.getAttribute("numSessions");  
		if (numSessions == null) {
			numSessions = new Integer(1);  
		}  
		else {  
			int count = numSessions.intValue( );  
			numSessions = new Integer(count + 1);  
		}
		ctx.setAttribute("numSessions", numSessions);  
	}  
	/* Session失效事件 */
	public void sessionDestroyed(HttpSessionEvent event) {
		try{
			int userId = Integer.valueOf(event.getSession().getAttribute("SYS_USER_ID").toString());
			if(systemManagerService==null){
				systemManagerService = (ISystemManagerService) SpringContextUtil
					.getBean("systemManagerServiceImpl");
			}
			systemManagerService.logout(userId,new Date(event.getSession().getLastAccessedTime()));
		}catch(Exception e){}
		ServletContext ctx=event.getSession().getServletContext();  
		numSessions = (Integer)ctx.getAttribute("numSessions");  
		if(numSessions == null){
			numSessions = new Integer(0);  
		}
		else {  
			int count = numSessions.intValue( );  
			numSessions = new Integer(count - 1);  
		}
		ctx.setAttribute("numSessions", numSessions);
	}
}


  
