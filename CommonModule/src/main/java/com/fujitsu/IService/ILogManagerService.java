package com.fujitsu.IService;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;



public interface ILogManagerService {
		/**
		 * Method name: JournalToMongodb <BR>
		 * Description: 日志信息入库<BR>
		 * Remark: 2013-12-18<BR>
		 * @author CaiJiaJia
		 * @return void<BR>
		 * @throws ParseException 
		 */
		public void JournalToMongodb(Map<String, Object> model)throws CommonException, ParseException;
		/**
		 * Method name: getUserGroupByUserId <BR>
		 * Description: 根据用户ID,查询用户组信息<BR>
		 * Remark: 2014-02-07<BR>
		 * @author CaiJiaJia
		 * @return Map<String, Object><BR>
		 * @throws ParseException
		 */
		public List<Map<String, Object>> getUserGroupByUserId(int userId)throws CommonException, ParseException;
}
