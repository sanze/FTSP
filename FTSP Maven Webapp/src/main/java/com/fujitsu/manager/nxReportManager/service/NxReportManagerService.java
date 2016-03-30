package com.fujitsu.manager.nxReportManager.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fujitsu.IService.INxReportManagerService;
import com.fujitsu.abstractService.AbstractService;
import com.fujitsu.common.CommonDefine;

public abstract class NxReportManagerService extends AbstractService implements
	INxReportManagerService {
	
	public static  StringBuffer NX_ALL_TASKTYPE = new StringBuffer();
	static{
	for(Entry<Integer,String> set:CommonDefine.NX_REPORT_TYPE.entrySet()){
			NX_ALL_TASKTYPE.append(set.getKey());
			NX_ALL_TASKTYPE.append(",");
		}
	NX_ALL_TASKTYPE.deleteCharAt(NX_ALL_TASKTYPE.lastIndexOf(","));
	}
	
	protected static Map<String, Object> REPORT_DEFINE = new HashMap<String, Object>();
	static {
		REPORT_DEFINE.put("FALSE", CommonDefine.FALSE);
		REPORT_DEFINE.put("TRUE", CommonDefine.TRUE);
		REPORT_DEFINE.put("NX_ALL_TASKTYPE", NX_ALL_TASKTYPE);
	}
}
