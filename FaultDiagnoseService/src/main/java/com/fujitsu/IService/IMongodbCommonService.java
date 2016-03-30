package com.fujitsu.IService;

import com.fujitsu.common.CommonException;

public interface IMongodbCommonService {
	
	/**
	 * Method name: getSequenceId <BR>
	 * Description: 根据表名获取主键id的值<BR>
	 * Remark: 2013-12-18<BR>
	 * @author CaiJiaJia
	 * @param tableName 表名
	 * @return int<BR>
	 * @throws CommonException 
	 */
	public int getSequenceId(String tableName)throws CommonException;
	
	
	
	
}
