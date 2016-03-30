package com.fujitsu.manager.equipmentTestManager.service;

import com.fujitsu.IService.IEquipmentTestManagerService;

public abstract class EquipmentTestManagerService  implements IEquipmentTestManagerService{

	//参数键值
	protected static String PARAM_KEY_EQPT = "eqptInfo";
	protected static String PARAM_KEY_SYS = "sysInfo";
	protected static String PARAM_KEY_ROUTE_POINT = "routePoint";
	protected static String PARAM_KEY_TEST_PARA = "testParaInfo";
	//命令类型
	protected final static int COLLECT_TYPE_ALARM = 1;
	protected final static int COLLECT_TYPE_CONFIG = 2;
	protected final static int COLLECT_TYPE_OTDR_TEST = 3;
}
