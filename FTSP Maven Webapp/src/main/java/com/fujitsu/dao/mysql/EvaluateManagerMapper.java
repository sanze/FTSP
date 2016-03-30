package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface EvaluateManagerMapper {
	public List<Map<String, Object>> callFiberLinkPmSP(
			@Param(value = "param") Map<String, Object> param,
			@Param(value = "start") Integer start,
			@Param(value = "limit") Integer limit);

	public Integer getFendLinkId(@Param(value = "param") Map<String, Object> param);
	
	public Integer cntAllResourceLink(
			@Param(value = "param") Map<String, Object> param);
	public List<Map<String, Object>> getAllResourceLink(
			@Param(value = "param") Map<String, Object> param,
			@Param(value = "startNumber") int startNumber, 
			@Param(value = "pageSize") int pageSize);
	public void deleteResourceLink(
			@Param(value = "param") Map<String, Object> param);
	public void relateResourceLink(
			@Param(value = "param") Map<String, Object> param);
	public void setResourceLink(
			@Param(value = "param") Map<String, Object> param);
	
	public List<Map<String, Object>> callEvaluatelinkPmSP(
			@Param(value = "param") Map<String, Object> param,
			@Param(value = "start") Integer start,
			@Param(value = "limit") Integer limit);
}
