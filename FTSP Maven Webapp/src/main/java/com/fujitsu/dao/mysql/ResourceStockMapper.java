package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface ResourceStockMapper{
	
	public List<Map<String,Object>> getNeResourceStock(@Param(value = "map")Map<String,Object> map);
	public int countNeResourceStock(@Param(value = "map")Map<String,Object> map);
	
	public List<Map<String,Object>> getShelfResourceStock(@Param(value = "map")Map<String,Object> map);
	public int countShelfResourceStock(@Param(value = "map")Map<String,Object> map);
	
	public List<Map<String,Object>> getUnitResourceStock(@Param(value = "map")Map<String,Object> map);
	public int countUnitResourceStock(@Param(value = "map")Map<String,Object> map);
	
	public List<Map<String,Object>> getPtpResourceStock(@Param(value = "map")Map<String,Object> map);
	public int countPtpResourceStock(@Param(value = "map")Map<String,Object> map);
	
	
	public List<Map<String,Object>> getNeIdListBySubnetId(@Param(value = "map")Map<String,Object> map);
	
	public List<Map<String,Object>> getNeIdListByEmsId(@Param(value = "map")Map<String,Object> map);
	
	public List<Map<String,Object>> getNeIdListByEmsGroupId(@Param(value = "map")Map<String,Object> map);
	
	public boolean changeDisplayMode(@Param(value = "map")Map<String,Object> map);
	
	public List<Map<String,Object>> checkNeNameExit(@Param(value = "map")Map<String,Object> map);
	
	public boolean saveChangedInfo(@Param(value = "map")Map<String,Object> map);
	
}