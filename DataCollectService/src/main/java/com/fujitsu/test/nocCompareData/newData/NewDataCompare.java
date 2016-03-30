package com.fujitsu.test.nocCompareData.newData;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;


/**
 * @author xuxiaojun
 *
 */
public interface NewDataCompare {
	
	public List selectNeList_OLA(
			@Param(value = "emsConnectionId") int emsConnectionId,
			@Param(value = "neModel") String neModel);
	
	public List selectTableListById(
			@Param(value = "tableName") String tableName,
			@Param(value = "idName") String idName,
			@Param(value = "id") int id);
	
	public void updateNeById(Map ne);
}
