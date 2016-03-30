package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
public interface IColorMapper {
	Map getAlarmSetInfoByLevel(@Param(value = "alarmLevel")String alarmLevel);
	void insertAlarmSetInfo(@Param(value = "map")Map map);
	void updateAlarmSetInfo(@Param(value = "map")Map map);
	List<Map> getAlarmColorConfig(@Param(value = "map")Map map);
}
