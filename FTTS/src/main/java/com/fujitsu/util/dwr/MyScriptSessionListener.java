package com.fujitsu.util.dwr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.event.ScriptSessionEvent;
import org.directwebremoting.event.ScriptSessionListener;
import org.directwebremoting.impl.DefaultScriptSession;

//新建自己的scriptsession监听
public class MyScriptSessionListener implements ScriptSessionListener {

	 //维护一个Map key为http session的Id， value为ScriptSession对象
    public static final Map<String, ScriptSession> scriptSessionMap = new HashMap<String, ScriptSession>();
	
	@Override
	public void sessionCreated(ScriptSessionEvent scriptsessionevent) {
		WebContext webContext = WebContextFactory.get();
		HttpSession session = webContext.getSession();
		DefaultScriptSession source = (DefaultScriptSession)scriptsessionevent.getSource();
		String key = session.getId()+source.getPage();
		ScriptSession scriptSession = scriptsessionevent.getSession();
		scriptSessionMap.put(key, scriptSession);     //添加scriptSession
		//System. out.println( "key: " + key + " scriptSession: " + scriptSession.getId() + " is created!");
	}

	@Override
	public void sessionDestroyed(ScriptSessionEvent scriptsessionevent) {
		WebContext webContext = WebContextFactory.get();
		HttpSession session = webContext.getSession();
		DefaultScriptSession source = (DefaultScriptSession)scriptsessionevent.getSource();
		String key = session.getId()+source.getPage();
		if(scriptSessionMap.containsKey(key)){
			scriptSessionMap.remove(key);  //移除scriptSession
		}
	}
	
	/**
     * 获取所有ScriptSession
     */
    public static Collection<ScriptSession> getScriptSessions(){
           return scriptSessionMap.values();
    }

}
