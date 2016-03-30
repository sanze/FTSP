package com.fujitsu.IService;

import java.util.Map;

import com.fujitsu.common.CommonException;

public interface IProcessModuleManageService {
	public Map<String, Object> getProcessModuleManageData(int startNumber, int pageSize) throws CommonException;

	public boolean changeState(String ids,int flag);
}
