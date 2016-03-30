package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface SampleMapper {
	
	/**
	 * 获取表数据
	 * @param tableName
	 * @return
	 */
	public List<Map> getSampleData(@Param(value = "tableName")String tableName);
	
}
