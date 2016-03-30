package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

public interface QuartzManagerMapper {
	// 获取所有SimpleTrigger
	public List<Map<String, Object>> getSimpleTriggers();
	// 获取所有CronTriggers
	public List<Map<String, Object>> getCronTriggers();
	
}
