package com.fujitsu.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.util.CommonUtil;


/**
 * @author xuxiaojun
 * 
 */
public class ExceptionHandler{
	
	static Logger log = Logger.getLogger(ExceptionHandler.class);
	
	static SimpleDateFormat sf = CommonUtil.getDateFormatter(DataCollectDefine.COMMON_FORMAT);
	
	/**
	 * @param e
	 */
	public static void handleException(Exception e){
		
		if(CommonException.class.isInstance(e)){
			log.error("[ERROR]："+((CommonException)e).getErrorMessage());
			log.error("[ERROR]："+getExceptionTrace(((CommonException)e).getE()));
//			System.out.println("[ERROR]："+((CommonException)e).getErrorMessage());
		}else{
			log.error("[ERROR]："+getExceptionTrace(e));
		}
//		System.out.println(getExceptionTrace(e));
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
