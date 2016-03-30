package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ReportTreeMapper {
	List<Map> getChildNodesByNodeId(@Param(value = "nodeId") int nodeId, 
									@Param(value = "nodeType") int nodeType);

	Map getEmsGroupByEmsGroupId(@Param(value = "emsGroupId") String emsGroupId);
	List<Map> getEmsByEmsGroupId(@Param(value = "userId") Integer userId,@Param(value = "emsGroupId") String emsGroupId,@Param(value = "Define") Map Define);
	
	
}