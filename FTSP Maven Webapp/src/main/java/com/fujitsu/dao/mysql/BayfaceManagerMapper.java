package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;



public interface BayfaceManagerMapper {
	 
	public Map<String,Object> getUnitAttribute(@Param(value = "unitId") String unitId);
	
	public Map<String,Object> getBayfaceDataFromNE(@Param(value = "neId")String neId);
	
	public List<Map<String,Object>> getBayfaceDataFromShelf(@Param(value = "neId")String neId);
	
	public List<Map<String,Object>> getBayfaceDataFromUnit(@Param(value = "map")Map map);
	
	public Map<String,Object> getPortDomain (@Param(value = "map")Map map); 
	
	public Map<String,Object> getBayfaceUintId (@Param(value = "map")Map map); 
	
	public String getEmsId(@Param(value = "neId")String neId); 
	
	public String getEmsGroupId(@Param(value = "emsId")String neId); 
	
	public List<Map<String,Object>> getDetailPTP(@Param (value = "unitId")String unitId);
}