package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ChannelManagerMapper {
	public List<Map<String,Object>> getAllEMSGroup();
	public List<Map<String,Object>> getEMSInGroup(@Param(value = "id")int id);
	public List<Map<String,Object>> getSubnetInEMS(@Param(value = "id")int id);
}
