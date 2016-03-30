package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

public interface IResourceCableManagerService {

	/**
	 * 获取光缆段信息列表
	 */
	public Map<String,Object> getCableList(Map<String,Object> map,int start,int limit) throws CommonException;
	
	/**
	 * 获取指定光缆段的下属所有光纤信息列表(FTTS)
	 */
	public Map<String,Object> getFiberListByCableId(int cableId, int limit, int start) throws CommonException;
	
	
	
}
