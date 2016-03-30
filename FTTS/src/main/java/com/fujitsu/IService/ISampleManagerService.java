package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;


/**
 * @author xuxiaojun
 * 
 */
public interface ISampleManagerService {
	
	/**
	 * 获取表数据
	 * @param tableName
	 * @return
	 */
	public List<Map> getSampleData(String tableName) throws CommonException;

}
