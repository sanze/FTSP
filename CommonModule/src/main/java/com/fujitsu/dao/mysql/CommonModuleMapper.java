package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;


public interface CommonModuleMapper {
	//获取当前用户的用户组信息
		public List<Map<String, Object>> getUserGroupByUserId(@Param(value = "userId") int userId);
}