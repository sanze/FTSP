package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

public interface IResourceCableManagerService {

	/**
	 * 获取光缆信息列表
	 */
	public Map<String,Object> getCables(Map<String,Object> map,int start,int limit) throws CommonException;
	/**
	 * 新增一条光缆段
	 */
	public void addCables(Map<String,Object> map) throws CommonException;
	/**
	 * 检测光缆是否存在（光缆名称和代号唯一性检查）
	 */
	public boolean cablesExist(Map<String,Object> map)throws CommonException;
	/**
	 * 获取指定光缆信息
	 */
	public Map<String,Object> getCablesInfo(int cablesId) throws CommonException;	
	/**
	 * 检测修改的光缆(名称和代号唯一性检查)是否存在重复记录
	 */
	boolean modCablesCheck(Map<String,Object> map) throws CommonException;
	/**
	 * 修改指定光缆的信息
	 */
	public void modCables(Map<String,Object> map) throws CommonException;
	/**
	 * 获取指定的光缆下包含的光缆段信息列表
	 */
	public List<Map<String, Object>> getSubCable(int cablesId) throws CommonException;
	/**
	 * 删除指定的光缆
	 */
	public void delCables(int RESOURCE_CABLES_ID) throws CommonException;
	/**
	 * 获取光缆段信息列表
	 */
	public Map<String,Object> getCableList(Map<String,Object> map,int start,int limit) throws CommonException;  
	/**
	 * 获取所有的光缆信息（ID,名称和代号）列表
	 */
	public Map<String,Object> getAllCodeNames() throws CommonException; 
	/**
	 * 检测光缆段是否存在（名称和代号唯一性检查）
	 */
	public boolean cableExist(Map<String,Object> map)throws CommonException;
	/**
	 * 新增一条光缆段
	 */
	public void addCable(Map<String,Object> map) throws CommonException; 
	/**
	 * 获取指定光缆段的信息
	 */
	public Map<String, Object> getCableInfo(int cableId) throws CommonException;	
	/**
	 * 检测修改的光缆段(名称和代号唯一性检查)是否存在重复记录
	 */
	boolean modCableCheck(Map<String,Object> map) throws CommonException;

	/**
	 * 修改指定光缆段的信息
	 */
	public int modifyCable(Map<String,Object> map) throws CommonException; 
	/**
	 * 获取指定光缆段下所含的链路信息(base_link和resource_link)
	 */
	public List<Map<String, Object>> getLinkById(int cableId) throws CommonException;
	/**
	 * 获取指定光缆段下包含的ODF信息
	 */
	public List<Map<String, Object>> getOdfById(int cableId) throws CommonException;  
	/**
	 * 删除指定的光缆段
	 */
	public void deleteCable(int cableId) throws CommonException;
	/**
	 * 获取指定光缆段的光纤信息
	 */
	public Map<String,Object> getFiberResourceList(int cableId,int limit,int start) throws CommonException;

	/**
	 * 获取指定光缆段的下属所有光纤信息列表(FTTS)
	 */
	public Map<String,Object> getFiberListByCableId(int cableId, int limit, int start) throws CommonException;
	
	/**
	 * 批量修改光纤信息
	 */
	public void modifyFiberResource(List<Map<String, Object>> fiberList)  throws CommonException; 
}
