package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

public interface IChannelManagerService {
	/**
	 * @return 获取EMS分组
	 */
	public List<Map<String, Object>> getEmsGroup() throws CommonException;
	/**
	 * @param EMS分组Id 
	 * @return 获取EMS列表
	 */
	public List<Map<String, Object>> getEmsList(int emsGroupId) throws CommonException;
	/**
	 * @param EMS Id 
	 * @return 获取Sub net列表
	 */
	public List<Map<String, Object>> getSubnetList(int emsGroupId) throws CommonException;

}
