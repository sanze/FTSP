package com.fujitsu.manager.evaluateManager.service;

import java.util.HashMap;
import java.util.Map;

import com.fujitsu.IService.IEvaluateManagerService;
import com.fujitsu.abstractService.AbstractService;

public abstract class EvaluateManagerService extends AbstractService  
implements IEvaluateManagerService {

	public static Map<String,String> DIAGRAM_MAP = new HashMap<String, String>();
	static {
		DIAGRAM_MAP.put("ATT_VALUE", "链路衰耗：主信号");
		DIAGRAM_MAP.put("ATT_VALUE_OSC", "链路衰耗：OSC信号");
		DIAGRAM_MAP.put("ATT_STD", "衰耗基准值");
		DIAGRAM_MAP.put("ATT_COEFFICIENT", "衰耗系数α0：主信号");
		DIAGRAM_MAP.put("ATT_COEFFICIENT_OSC", "衰耗系数α0：OSC信号");
		DIAGRAM_MAP.put("ATT_COEFFICIENT_THEORY", "衰耗系数理论值");
		DIAGRAM_MAP.put("ATT_COEFFICIENT_BUILD", "衰耗系数竣工值");
		DIAGRAM_MAP.put("ATT_COEFFICIENT_EXPERIENCE", "衰耗系数经验值");
	}
}

