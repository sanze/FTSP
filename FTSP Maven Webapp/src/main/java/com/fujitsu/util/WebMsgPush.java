package com.fujitsu.util;

import java.util.Set;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.directwebremoting.Browser;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.ServerContextFactory;
import org.directwebremoting.ui.dwr.Util;

import com.fujitsu.IService.IFaultManagementService;
import com.fujitsu.common.CommonException;
import com.fujitsu.servlet.dwr.MyScriptSessionListener;

/**
 * 使用DWR向Web页推送字符串消息
 * @author gut
 *
 */
public class WebMsgPush {
	
	@Resource
	public IFaultManagementService faultManagementService;
	
	// 首页状态栏故障信息推送
	public void sendFaultMsgToWeb(final String msg) {
		
		if (ServerContextFactory.get() == null) {
			System.out.println("[WebMsgPush]:页面会话已经中断，请刷新浏览器内容！");
			return;
		}
        // 指定目标页面
        String page = ServerContextFactory.get().getContextPath() + "/jsp/main/main.jsp";
        // 更新指定目标页面的通知信息
        Browser.withPage(page, new Runnable()
        {
            @Override
            public void run()
            {
				Util.setValue("FaultMessageBox", msg);
            }
        });
	}
	
	// 更新首页故障信息
	public void updateFaultMsg() {
	
		String msg = "";
		try {
			msg = faultManagementService.getFaultInfoForFP().getReturnMessage();
		} catch (CommonException e) {
//			e.printStackTrace();
			msg = "获取故障信息异常！";
		}
		
		sendFaultMsgToWeb(msg);
	}
	
	//推送
	public static boolean updateGisMap(JSONObject info){
		boolean result = true;    	
		System.out.println("GIS地图消息推送开始...");
		try {          	
			ScriptBuffer script = new ScriptBuffer();
			script.appendScript("updateGisMap(").appendData(info).appendScript(")");
			//得到登录此页面的scriptSession的集合
			String currentPage = "/FTSP/jsp/gis/gisMap.jsp";
			Set<String> keys = MyScriptSessionListener.scriptSessionMap.keySet();
			for(String key : keys){
				if(key.contains(currentPage)){
					ScriptSession curSS = MyScriptSessionListener.scriptSessionMap.get(key);			
					if(curSS != null){
						curSS.addScript(script);
						System.out.println("GIS地图消息推送处理..."+info);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}        
		System.out.println("GIS地图消息推送结束...");
		return result;
	}
}
