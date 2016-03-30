/**
 * All rights Reserved, Copyright (C) JFTT 2011<BR>
 * 
 * FileName: SpringContextUtil.java <BR>
 * Version: $Id: SpringContextUtil.java, v 1.00 2011-9-22 $<BR>
 * Modify record: <BR>
 * NO. |     Date         |    Name                  |      Content <BR>
 * 1   |    2011-9-22       | JFTT)Cheng Yingqi        | original version <BR>
 */
package com.fujitsu.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * @author jftt
 * 
 */
public class SpringContextUtil extends BaseSpringContextUtil{

	public SpringContextUtil(boolean isDebug) {
		//如果是debug
		if (isDebug) {
			ApplicationContext applicationContext = null;
			if (applicationContext == null) {
				String[] configFileList = new String[] {
						"resourceConfig/springConfig/applicationContext.xml",
						"resourceConfig/springConfig/applicationContext-service.xml",
						"resourceConfig/springConfig/applicationContext-persistence.xml",
						"resourceConfig/springConfig/applicationContext-database.xml" };

				applicationContext = new ClassPathXmlApplicationContext(
						configFileList);
			}
			super.setApplicationContext(applicationContext);
		} else {

		}
	}
}
