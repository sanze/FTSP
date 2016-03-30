package com.fujitsu.handler;

import org.apache.log4j.Logger;


/**
 * @author xuxiaojun
 * 
 */
public class LogHandler {

	static Logger log = Logger.getLogger(LogHandler.class);
	/**
	 * @param message
	 */
	public static void handleLog(String message){
		log.error(message);
	}
	
}
