package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
public interface NetworkManagerMapper {
	/**
	 * 获取预警设置
	 * @param map 
	 * @return Map
	 */
	public Map<String, Object> getEarlyAlarmSetting(@Param(value = "selectText") String selectText); 
	@SuppressWarnings("rawtypes")
	/**
	 * 初始新增预警设置值
	 * @param map 
	 * @return void
	 */
	public void insertEarlyAlarmSetting(@Param(value = "map")Map map); 
	/**
	 * 更新预警设置值
	 * @param map 
	 * @return void
	 */
	@SuppressWarnings("rawtypes")
	public void updateEarlyAlarmSetting(@Param(value = "map")Map map);
	/**
	 *根据网元ID查询槽道数
	 * @param int 
	 * @return int
	 */
	public int getslotCntByneId(@Param(value = "neId") int neId); 
	/**
	 *根据网元ID查询板卡数
	 * @param int 
	 * @return int
	 */
	public int getunitCntByneId(@Param(value = "neId") int neId); 
	/**
	 *根据网元ID查询端口速率的个数
	 * @param int 
	 * @return map
	 */
	public Map<String, Object> getRateCntALL(@Param(value = "neId") int neId); 
	/**
	 *根据网元ID查询端口交叉连接的个数
	 * @param int 
	 * @return map
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getRateCntCrossconnect(@Param(value = "neId") int neId,
			@Param(value = "map") Map map); 
	/**
	 *根据网元ID判断端口的ptpType是否存在
	 * @param int 
	 * @return List<Map>
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> judgePtpTypeIsNull(@Param(value = "neId") int neId); 
	/**
	 *根据网元ID判断端口的ptpType是否存在交叉连接
	 * @param int 
	 * @return List<Map>
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> judgePtpTypeCrsIsNull(@Param(value = "neId") int neId,
			@Param(value = "map") Map map); 
	/**
	 *根据网元ID查询所在系统
	 * @param int 
	 * @return List<Map>
	 */
	@SuppressWarnings("rawtypes")
 	public List<Map> getRLInfoByneId(@Param(value = "neId") int neId); 
	/**
	 *根据系统ID查询系统占用的端口
	 * @param int 
	 * @return List<Map>
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getRLptpIds(@Param(value = "rlId") int rlId); 
	/**
	 *查询一组端口VC12占用情况
	 * @param String 
	 * @return List<Map>
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getVC12Data(@Param(value = "ptpIds") String ptpIds); 
	/**
	 *查询一组端口VC4占用情况
	 * @param String 
	 * @return List<Map>
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getVC4Data(@Param(value = "ptpIds") String ptpIds);  
	/**
	 *qz定时任务将计算结果写入数据库
	 * @param map 
	 * @return void
	 */
	@SuppressWarnings("rawtypes")
	public void insertNeEarlyWarn(@Param(value = "map")Map map); 
	/**
	 *qz定时任务将计算结果更新数据库
	 * @param map 
	 * @return void
	 */
	@SuppressWarnings("rawtypes")
	public void updateNeEarlyWarn(@Param(value = "map")Map map); 
	/**
	 *查询网元预警情况
	 * @param map 
	 * @return List<Map> 
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> searchNeEarlyWarn(@Param(value = "map") Map map,
			@Param(value = "start") int start,
			@Param(value = "limit") int limit); 
	/**
	 *查询网元预警个数
	 * @param map 
	 * @return int
	 */
	@SuppressWarnings("rawtypes")
	public int countNeEarlyWarn(@Param(value = "map") Map map);   
	/**
	 *查询系统
	 * @param map 
	 * @return  List<Map>
	 */
	@SuppressWarnings("rawtypes") 
	public List<Map> getResourceTransSysByCond(@Param(value = "map") Map map,
			@Param(value = "userId")Integer userId,
			@Param(value = "Define") Map define);
	/**
	 *查询系统个数
	 * @param map 
	 * @return  List<Map>
	 */
	@SuppressWarnings("rawtypes") 
	public int countResourceTransSysByCond(@Param(value = "map") Map map,
			@Param(value = "userId")Integer userId,
			@Param(value = "Define") Map define); 
	/**
	 *查询节点链接详情
	 * @param map 
	 * @return  List<Map>
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> searchAreaNodeList(@Param(value = "map") Map map);    
	/**
	 *获取拓扑图的网元节点信息
	 * @param map 
	 * @return  List<Map>
	 */
	
	@SuppressWarnings("rawtypes")
	public List<Map> getTopoNodes(@Param(value = "rlId") int rlId); 
	/**
	 *获取拓扑图的link节点信息
	 * @param map 
	 * @return  List<Map>
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getTopoLinks(@Param(value = "rlId") int rlId);   
	
	/**
	 *定时任务中分页取网元
	 * @param map 
	 * @return  List<Map>
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getAllNeList(@Param(value = "start")int start, @Param(value = "limit")	int limit);  
	/**
	 *查询是否存在环计算结果
	 * @param int 
	 * @return  int
	 */
	public int searchMsOccupyEarlyWarn(@Param(value = "rlId") int rlId); 
	/**
	 *插入qz计算的复用段结果
	 * @param map 
	 * @return  void
	 */
	@SuppressWarnings("rawtypes")
	public void insertMsOccupyEarlyWarn(@Param(value = "map")Map map); 
	/**
	 *更新qz计算的复用段结果
	 * @param map 
	 * @return  void
	 */
	@SuppressWarnings("rawtypes")
	public void updateMsOccupyEarlyWarn(@Param(value = "map")Map map);  
	/**
	 *查询复用段预警情况
	 * @param map 
	 * @return  void
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> searchMsEarlyWarn(@Param(value = "map") Map map,
			@Param(value = "userId")Integer userId,
			@Param(value = "Define") Map define);
	/**
	 *查询复用段预警个数
	 * @param map 
	 * @return  void
	 */
	@SuppressWarnings("rawtypes")
	public int countMsEarlyWarn(@Param(value = "map") Map map,
			@Param(value = "userId")Integer userId,
			@Param(value = "Define") Map define);
	/**
	 *判断网元是否需要做定时任务计算
	 * @param map 
	 * @return  void
	 */
	public Map<String,Object>  isTriggeredQz(@Param(value = "neId") int neId); 
	/**
	 * 根据子网ID列表查询多环节点中的网元信息列表
	 * @param map 
	 * @return
	 */
	public List<Map<String,Object>> searchPolycyclicNodeNeList(@Param(value = "map") Map<String,Object> map);
	/**
	 * 多环节点中的网元查询个数统计
	 * @param map 
	 * @return
	 */
	public int countPolycyclicNodeNeList(@Param(value = "map") Map<String,Object> map);
	/**
	 * 多环节点中的网元查询个数统计
	 * @param map 
	 * @return
	 */
	public Map<String,Object> getWRConfig();
	/**
	 * 查询多环节点中的环信息列表
	 * @param map 
	 * @return
	 */
	public List<Map<String,Object>> searchCycleList(@Param(value = "map") Map<String,Object> map);
	/**
	 * 根据子网ID列表查询未成环网元信息列表
	 * @param map 
	 * @return
	 */
	public List<Map<String,Object>> searchNoCyclicNeList(@Param(value = "map") Map<String,Object> map);
	/**
	 * 未成环网元查询个数统计
	 * @param map 
	 * @return
	 */
	public int countNoCyclicNeList(@Param(value = "map") Map<String,Object> map);
	/**
	 * 未成环网元是否存在t_resource_sys_ne表中
	 * @param map 
	 * @return
	 */
	public int isExistInResourceSysNe(@Param(value = "map") Map<String,Object> map);
	
	
	/**
	 * 查询可用率表头信息
	 * @param map 
	 * @return
	 */
	public List<Map> searchAvailabilityHeader(@Param(value = "tableName")String tableName);

	
	/**
	 * 查询可用率统计数据
	 * @param map 
	 * @return
	 */
	public List<Map> searchAvailabilityData(@Param(value = "tableName")String tableName,
											@Param(value = "neIdList") List<Integer> neIdList,
											@Param(value = "start")Integer start,
											@Param(value = "limit")Integer limit);

	
	/**
	 * 查询可用率统计数据
	 * @param map 
	 * @return
	 */
	public List<Map> searchAvailabilityData4Ctp(@Param(value = "neIdList") List<Integer> neIdList,
												@Param(value = "start")Integer start,
												@Param(value = "limit")Integer limit);
	
	/**
	 * 查询可用率统计数据详细
	 * @param map 
	 * @return
	 */
	public List<Map> searchAvailabilityDataDetail(@Param(value = "tableName")String tableName,
												  @Param(value = "neIdList") List<Integer> neIdList);
	
	
	
	/**
	 * 查询可用率统计数据用于图表
	 * @param map 
	 * @return
	 */
	public List<Map> searchAvailabilityData4Chart(@Param(value = "tableName")String tableName,
												  @Param(value = "neIdList") List<Integer> neIdList);
	
	/**
	 * 查询可用率统计数据用于图表
	 * @param map 
	 * @return
	 */
	public List<Map> searchAvailabilityData4Chart_CTP(@Param(value = "neIdList") List<Integer> neIdList);
	
	/**
	 * 查询可用率统计数据用于图表
	 * @param map 
	 * @return
	 */
	public Map<String,Object> searchAvailabilityData4Chart_ZONGHE(@Param(value = "tableName")String tableName,
																@Param(value = "neIdList") List<Integer> neIdList);
	
	/**
	 * 查询可用率统计数据用于图表
	 * @param map 
	 * @return
	 */
	public Map<String,Object> searchAvailabilityData4Chart_ZONGHE_VC4(@Param(value = "neIdList") List<Integer> neIdList);
	
	/**
	 * 查询可用率统计数据用于图表
	 * @param map 
	 * @return
	 */
	public Map<String,Object> searchAvailabilityData4Chart_ZONGHE_VC12(@Param(value = "neIdList") List<Integer> neIdList);
	
	/**
	 * 更新网络分析预警设置值
	 * @param map 
	 * @return
	 */
	public void modifyWarningValue(@Param(value = "map") Map<String,Object> map);
	
	/**
	 * 查询网络分析预警设置值
	 * @param map 
	 * @return
	 */
	public Map searchWarningValue(@Param(value = "type") int type);
	
	/**
	 * 插入网络分析预警设置值
	 * @param map 
	 * @return
	 */
	public void insertWarningValue(@Param(value = "map") Map map);
	/**
	 * 查询板卡类别名称自定义列表个数
	 * @param none 
	 * @return
	 */
	public int countCtpNameCustomList();
	/**
	 * 查询板卡类别名称自定义列表
	 * @param start,limit  
	 * @return
	 */
	public List<Map<String, Object>> searchCtpNameCustomList(@Param(value = "start") int start,
													  @Param(value = "limit") int limit);
	/**
	 * 新增板卡类别
	 * @param map 
	 * @return
	 */
	public void addCtpCategory(@Param(value = "map") Map map);
	/**
	 * 查询板卡类别关联板卡类别自定义个数
	 * @param none 
	 * @return
	 */
	public int countCtpCategoryRel(@Param(value = "unitTypeList") List<Integer> unitTypeList);
	/**
	 * 删除板卡类别
	 * @param unitTypeId 
	 * @return
	 */
	public void deleteCtpCategory(@Param(value = "unitTypeList") List<Integer> unitTypeList);
	/**
	 * 修改板卡类别
	 * @param map 
	 * @return
	 */
	public void updateCtpCategory(@Param(value = "map") Map map);
	/**
	 * 查询板卡类别自定义列表个数
	 * @param none 
	 * @return
	 */
	public int countCtpCategoryListById(@Param(value = "factoryId") int factoryId);
	/**
	 * 查询板卡类别自定义列表
	 * @param start,limit  
	 * @return
	 */
	public List<Map<String, Object>> getCtpCategoryListById(@Param(value = "factoryId") int factoryId,
													  @Param(value = "start") int start,
													  @Param(value = "limit") int limit);
	/**
	 * 板卡类别名称唯一性检测
	 * @param none 
	 * @return
	 */
	public int getListBySortB(@Param(value = "sortB") String sortB);
	/**
	 * 设定板卡类别
	 * @param map 
	 * @return
	 */
	public void setCtpCategory(@Param(value = "map") Map map);
	
	public List<Map> selectNeListByEmsId(
			@Param(value = "emsGroupId") int emsGroupId);
	
	/**
	 * 查询网元端口使用详情列表数
	 * @param int 
	 * @return
	 */
	public int countPortDetial(@Param(value = "neId") int neId,@Param(value = "tableName")String tableName);
	/**
	 * 查询网元端口使用详情
	 * @param int 
	 * @return
	 */
	public List<Map<String, Object>> getPortDetial(
			@Param(value = "neId") int neId,
			@Param(value = "tableName") String tableName,
			@Param(value = "leftJoinTableName") String leftJoinTableName);

public List<Map> getLinkAnalysisInfo(@Param(value = "linkIds") List<Integer> linkIds);
}
