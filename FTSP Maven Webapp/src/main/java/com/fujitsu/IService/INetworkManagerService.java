package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;


public interface INetworkManagerService {

	public Map<String, Object> getEarlyAlarmSetting(String selectText)throws CommonException;
	public CommonResult updateEarlyAlarmSetting(Map<String,String> paramMap) throws CommonException;
	public Map<String,Object> searchNeEarlyWarn(Map<String,String> paramMap,int start,int limit)throws CommonException;
	public Map<String,Object> initMultiEarlyWarn(Map<String,String> paramMap,Integer userId)  throws CommonException;
	public Map<String,Object> searchMultiEarlyWarn(Map<String,String> paramMap,Integer userId,int start,int limit)throws CommonException;
	public Map<String,Object> searchDetailMulti(Map<String,String> paramMap) throws CommonException;
	public String exportExcel(Map<String,String> paramMap,Integer userId) throws CommonException;  
	public String exportExcel(Map<String,Object> map) throws CommonException;  
	public Map<String,Object> searchCommonEarlyAlarm(Map<String,String> paramMap,Integer userId,int start,int limit)throws CommonException;
	public Map<String,Object> searchAreaNodeList(Map<String,String> paramMap) throws CommonException; 
	public Map<String,Object> getTopoNodeAndLink(Map<String,String> paramMap) throws CommonException; 
	
	
	/**
	 * 多环节点、大汇聚点网元信息查询
	 * 返回多环节点网元信息列表
	 * 
	 * @param Map<String,Object> - 子网(或网管)节点Id、预警级别
	 * @return Map<String,Object> - 网元信息列表
	 * @throws CommonException
	 */
	public Map<String,Object> searchNodeNeList(Map<String,Object> map) throws CommonException; 
	
	/**
	 * 预警参数值获取
	 * 指定环、链类型的预警参数值
	 * 
	 * @param Map<String,Object> - 环、链类型
	 * @return Map<String,Object> - 指定环类型的预警参数值
	 * @throws CommonException
	 */
	public Map<String,Object> getWRConfig(int getWRConfig) throws CommonException;
	/**
	 * 多环节点、大汇聚点环链信息查询
	 * 返回多环节点、大汇聚点环链信息列表
	 * 
	 * @param Map<String,Object> - 网元Id、分页参数
	 * @return Map<String,Object> - 环信息列表
	 * @throws CommonException
	 */
	public Map<String,Object> searchCycleList(Map<String,Object> map) throws CommonException; 
	/**
	 * 未成环网元信息查询
	 * 返回未成环网元信息列表
	 * 
	 * @param Map<String,Object> - 子网(或网管)节点Id
	 * @return Map<String,Object> - 网元信息列表
	 * @throws CommonException
	 */
	public Map<String,Object> searchNoCyclicNodeList(Map<String,Object> map) throws CommonException;

	/**
	 * 查询可用率表头
	 * @throws CommonException
	 */
	public Map<String, Object> searchAvailabilityHeader(int type,List<Map> equipList)
			throws CommonException;
	
	/**
	 * 查询槽道可用率统计信息
	 * @throws CommonException
	 */
	public Map<String, Object> searchAvailabilityData(int type,List<Map> equipList,int warningType,int start,int limit)
			throws CommonException;

	/**
	 * 生成槽道可用率统计信息图表数据
	 * @param type 11 槽道-综合 12 槽道-各类 
	 * 							21 端口-综合 22 端口-各类
	 * 							31 时隙-综合 32 时隙-各类
	 * @return
	 * @throws CommonException
	 */
	public Map generateDiagramXml(Map paramMap,List<Map> equipList) throws CommonException;
	

	/**
	 * 修改预警值
	 * @param map
	 * @throws CommonException
	 */
	public void modifyWarningValue(Map map) throws CommonException;
	
	/**
	 * 查询预警值
	 * @param map
	 * @throws CommonException
	 */
	public Map searchWarningValue(int type) throws CommonException;
	/**
	 * 查询板卡类别名称自定义列表
	 * @throws CommonException
	 */
	public Map<String, Object> ctpNameCustomList(int start,int limit)
			throws CommonException;
	/**
	 * 新增板卡类别
	 * @throws CommonException
	 */
	public void addCtpCategory(int sortA,String sortB)throws CommonException;
	/**
	 * 删除板卡类别
	 * @throws CommonException
	 */
	public Map<String,Object> deleteCtpCategory(List<Integer> unitTypeList)throws CommonException;
	/**
	 * 保存板卡类别
	 * @throws CommonException
	 */
	public void updateCtpCategory(List<Map> unitTypeList)
			throws CommonException;
	/**
	 * 查询板卡类别自定义列表
	 * @throws CommonException
	 */
	public Map<String, Object> getCtpCategoryListById(int factoryId,int start,int limit)
			throws CommonException;
	/**
	 * 板卡类别名称唯一性检测
	 * @throws CommonException
	 */
	public Map<String, Object> validateCtpName(String sortB) throws CommonException;
	/**
	 * 保存板卡类别
	 * @throws CommonException
	 */
	public void setCtpCategory(List<Map> unitTypeList) throws CommonException;
	/**
	 * 导出
	 * @throws CommonException
	 */
	public String exportAvailabilityData(int type,List<Map>equipList) throws CommonException;
	/**
	 * 查询网元端口使用详情
	 * @throws CommonException
	 */
	public Map<String, Object> getPortDetial(int neId,int type) throws CommonException;
}
