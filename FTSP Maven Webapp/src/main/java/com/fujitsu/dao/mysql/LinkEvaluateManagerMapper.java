package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface LinkEvaluateManagerMapper {
	public List<Map<String, Object>> callFiberLinkPmSP(
			@Param(value = "param") Map<String, Object> param,
			@Param(value = "start") Integer start,
			@Param(value = "limit") Integer limit);

	public int getFendLinkId(@Param(value = "param") Map<String, Object> param);
}
