package com.fujitsu.manager.resourceManager.service;

import com.fujitsu.IService.IResourceSystemCorrelationService;
import com.fujitsu.abstractService.AbstractService;

public abstract class ResourceSystemCorrelationService extends AbstractService implements
	IResourceSystemCorrelationService{
	
	public static final int RESOURCE_CORRELATION_AREA = 1;
	public static final int RESOURCE_CORRELATION_STATION = 2;
	public static final int RESOURCE_CORRELATION_ROOM = 3;
	public static final int RESOURCE_CORRELATION_TRANS_SYS = 4;
	public static final int RESOURCE_CORRELATION_NE = 5;
	public static final int RESOURCE_CORRELATION_PORT = 6;
	public static final int RESOURCE_CORRELATION_TRANS_SEGMENT = 7;
	public static final int RESOURCE_CORRELATION_CIRCUIT = 8;
	public static final int RESOURCE_CORRELATION_OPTICAL_PATH = 11;
	
	public static final String TASK_STATUS_ENABLE = "1";
	public static final String TASK_STATUS_DISABLE = "0";

}
