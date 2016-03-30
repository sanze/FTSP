package com.fujitsu.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.util.CommonUtil;


/**
 * @author meihailiang
 * 
 */
public class WebServiceHandler{
	
	static Logger log = Logger.getLogger(WebServiceHandler.class);
	
	static SimpleDateFormat sf = CommonUtil.getDateFormatter(CommonDefine.COMMON_FORMAT);
	
	/**
	 * @param e
	 */
	public static void handleException(Exception e){
		
		if(CommonException.class.isInstance(e)){
			log.error("[ERROR]："+((CommonException)e).getErrorMessage()+" "+MessageHandler.getErrorMessage(((CommonException)e).getErrorCode()));
			System.out.println("[ERROR]："+((CommonException)e).getErrorMessage()+" "+MessageHandler.getErrorMessage(((CommonException)e).getErrorCode()));
		}else{
			
		}
		}
	
	//打印异常信息堆栈
	public static String getExceptionTrace(Throwable e){
		if(e!=null){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		}
		return "NO Exception";
	}
	
}
