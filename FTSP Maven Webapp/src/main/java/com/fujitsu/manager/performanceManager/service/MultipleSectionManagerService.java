package com.fujitsu.manager.performanceManager.service;

import java.util.HashMap;
import java.util.Map;

import com.fujitsu.IService.IMultipleSectionManagerService;
import com.fujitsu.abstractService.AbstractService;
import com.fujitsu.common.CommonDefine;

/**
 * @author wangjian
 * 
 */
public abstract class MultipleSectionManagerService extends AbstractService
		implements IMultipleSectionManagerService {
	protected static Map<String, Object> REPORT_DEFINE = new HashMap<String, Object>();
	static {
		REPORT_DEFINE.put("FALSE", CommonDefine.FALSE);
		REPORT_DEFINE.put("TRUE", CommonDefine.TRUE);
		REPORT_DEFINE.put("NE_REPORT", CommonDefine.QUARTZ.JOB_REPORT_NE);
		REPORT_DEFINE.put("MS_REPORT", CommonDefine.QUARTZ.JOB_REPORT_MS);
		REPORT_DEFINE.put("NODE_TL", CommonDefine.TASK_TARGET_TYPE.TRUNK_LINE);
		REPORT_DEFINE.put("NODE_MS", CommonDefine.TASK_TARGET_TYPE.MULTI_SEC);
	}
}
