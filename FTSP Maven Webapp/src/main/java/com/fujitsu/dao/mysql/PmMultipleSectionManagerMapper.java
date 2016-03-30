package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * @author wangjian
 * 
 */
public interface PmMultipleSectionManagerMapper {

	/**
	 * 根据网管分组id查询网管
	 * 
	 * @param emsGroupId
	 *            网管分组id
	 * 
	 * @param start
	 *            记录起始位置
	 * @param limit
	 *            每次查询的条数
	 * @return List<Map>
	 */
	public List<Map> selectAllEMS(@Param(value = "emsGroupId") int emsGroupId);

	/**
	 * 查询网管分组
	 * 
	 * @return
	 */
	public List<Map> selectAllGroup();

	/**
	 * 查询干线信息
	 * 
	 * @return
	 */
	public List<Map> selectTrunkLine(@Param(value = "map") Map map);

	/**
	 * 查询干线信息的数量
	 * 
	 * @return
	 */
	public Map selectTrunkLineTotal(@Param(value = "map") Map map);

	/**
	 * 新增干线信息
	 * 
	 * @return
	 */
	public void insertTrunkLine(@Param(value = "map") Map map);

	/**
	 * 查询光复用段信息
	 * 
	 * @return
	 */
	public List<Map> selectMultipleSection(@Param(value = "map") Map map);

	/**
	 * 查询光复用段信息总数
	 * 
	 * @return
	 */
	public Map selectMultipleSectionTotal(@Param(value = "map") Map map);

	/**
	 * 新增光复用段
	 * 
	 * @return
	 */
	public void insertMultipleSection(@Param(value = "map") Map map);

	/**
	 * 查询光复用段所含网元信息
	 * 
	 * @return
	 */
	public List<Map> selectMultipleSectionNe(@Param(value = "map") Map map);

	/**
	 * 根据网元id查询网元相关信息
	 * 
	 * @return
	 */
	public List<Map> selectByNeId(@Param(value = "map") Map map);

	/**
	 * 新增光复用段网元路由信息
	 * 
	 * @return
	 */
	public void insertMulSecNe(@Param(value = "map") Map map);

	/**
	 * 查询去重的光放型号
	 * 
	 * @return
	 */
	public List<Map> selectMultipleModel();

	/**
	 * 查询光放段
	 * 
	 * @return
	 */
	public List<Map> selectStandOptVal(@Param(value = "map") Map map);
	
	/**
	 * 查询光放段
	 * 
	 * @return
	 */
	public Map selectStandOptValTotal(@Param(value = "map") Map map);
	

	/**
	 * 新增光放
	 * 
	 * @return
	 */
	public void insertStandOptVal(@Param(value = "map") Map map);

	/**
	 * 根据网元id查询光复用段ptpList
	 * 
	 * @return
	 */
	public List<Map> selectPtpRouteList(@Param(value = "map") Map map);
	/**
	 * 根据网元id查询光复用段备用ptpList
	 * 
	 * @return
	 */
	public List<Map> selectSubPtpRouteList(@Param(value = "map") Map map);

	/**
	 * 跟据厂家查询网元型号
	 * 
	 * @return
	 */
	public List<Map> selectModelType(@Param(value = "map") Map map);

	/**
	 * 更新光放段Ptp路由信息
	 * 
	 * @return
	 */
	public void updateMultiplePtp(@Param(value = "map") Map map);

	/**
	 * 新增光放段Ptp路由信息
	 * 
	 * @return
	 */
	public void insertMultiplePtp(@Param(value = "map") Map map);

	/**
	 * 根据网元id查询光复用段ptpList
	 * 
	 * @return
	 */
	public List<Map> selectMultipleAbout(@Param(value = "map") Map map);

	/**
	 * 查询光复用段详细信息
	 * 
	 * @return
	 */
	public List<Map> selectMultiplePtpRoute(@Param(value = "map") Map map);

	/**
	 * 查询光复用段详细信息（报表用）
	 * 
	 * @return
	 */
	public List<Map> selectMultiplePtpRouteForReport(
			@Param(value = "map") Map map);
	
	/**
	 * 查询光复用段详细信息（简略版）
	 * 
	 * @return
	 */
	public List<Map> selectSimpleMultiplePtpRouteForReport(
			@Param(value = "map") Map map);
	
	/**
	 * 获取报表统计信息显示用复用段信息
	 * 
	 * @param multiSecId
	 * @return
	 */
	public List<Map> selectMultipleInfoForReport(
			@Param(value = "multiSecId") String multiSecId);
	
	/**
	 * 取得指定复用段的最高异常等级
	 * 
	 * @param tableName
	 * @param multiSecId
	 * @param startTime
	 * @param emdTime
	 * @return
	 */
	public Integer selectMultiplePmInfoForReport(
			@Param(value = "tableName") String tableName,
			@Param(value = "multiSecId") int multiSecId,
			@Param(value = "startTime") String startTime,
			@Param(value = "emdTime") String emdTime);
	
	/**
	 * 获取复用段方向
	 * 
	 * @param multiSecId
	 * @return
	 */
	public String getMultiSecDirection(int multiSecId);

	/**
	 * 按顺序查询光复用段详细信息,用于计算理论值
	 * 
	 * @return
	 */
	public List<Map> selectMultiplePtpList(@Param(value = "map") Map map);

	/**
	 * 获取端口历史性能值
	 * 
	 */
	public List<Map> generateDiagramNend(
			@Param(value = "searchCond") Map searchCond,
			@Param(value = "pmIndex") String pmIndex,
			@Param(value = "tableName") String tableName,
			@Param(value = "listPort") List listPort);
	
	
	
	/**
	 * 根据targetList查询出MSId和EMSId
	 * @param map
	 * @return
	 */
	public List<Map> getMSIdList(@Param(value = "targetList") List<Map> targetList,
								@Param(value="Define")  Map<String, Object> Define);

	/**
	 * 通过一个MSId查询出相关信息
	 * @param map
	 * @return
	 */
	public Map getMSInfoForCSV(@Param(value = "map") Map map);
	
	//333 START
	/**
	 * 割接前刷新-ptp
	 * @param map
	 */
	public void updateMSPtpBeforeCutover(@Param(value = "map") Map map);
	/**
	 * 割接前刷新-MS
	 * @param map
	 */
	public void updateMSBeforeCutover(@Param(value = "map") Map map);
	/**
	 * 割接后刷新-ptp
	 * @param map
	 */
	public void updateMSPtpAfterCutover(@Param(value = "map") Map map);
	/**
	 * 割接后刷新-MS
	 * @param map
	 */
	public void updateMSAfterCutover(@Param(value = "map") Map map);

	/**
	 * 直接获取到最大的差值
	 * @param map
	 * @return
	 */
	public double getGreatestDiff(@Param(value = "map") Map map);
	/**
	 * 计算t_pm_multi_sec_ne表中指定光复用段Id，方向的记录个数
	 * @param mulId
	 * @param direction
	 * @return
	 */
	public int getMultipleRouteRecordTotal(@Param(value="mulId")int mulId,@Param(value="direction")int direction);
	
	/**
	 * 计算t_pm_multi_sec_ptp表中指定光复用段Id，方向的记录个数
	 * @param mulId
	 * @param direction
	 * @return
	 */
	public int getMultipleRouteRecordTotalOnPtp(@Param(value="mulId")int mulId,@Param(value="direction")int direction);
	
	/**
	 * 标记删除t_pm_multi_sec_ne表中指定光复用段Id和方向的路由记录,仅将表中IS_DEL字段置1
	 * @param mulId
	 * @param direction
	 * 一般情况下与deleteRouteOnPtp方法连用;
	 */
	public void deleteRouteOnNeByMark(@Param(value="mulId")int mulId,@Param(value="direction")int direction);
	
	/**
	 * 标记删除t_pm_multi_sec_ptp表中指定光复用段Id和方向的路由记录,仅将表中IS_DEL字段置1
	 * @param mulId
	 * @param direction
	 * 一般情况下与deleteRouteOnNe方法连用
	 */
	public void deleteRouteOnPtpByMark(@Param(value="mulId")int mulId,@Param(value="direction")int direction);
	
	/**
	 * 真实删除t_pm_multi_sec_ne表中指定光复用段Id和方向的路由记录
	 * @param mulId
	 * @param direction
	 * 一般情况下与deleteRouteOnPtp方法连用;
	 */
	public void deleteRouteOnNe(@Param(value="mulId")int mulId,@Param(value="direction")int direction);
	
	/**
	 * 真实删除t_pm_multi_sec_ptp表中指定光复用段Id和方向的路由记录
	 * @param mulId
	 * @param direction
	 * 一般情况下与deleteRouteOnNe方法连用
	 */
	public void deleteRouteOnPtp(@Param(value="mulId")int mulId,@Param(value="direction")int direction);
	
	/**
	 * 根据电路中ptpId查询出所有相关的otn电路信息
	 * @param ptpId 指点的端口Id
	 * @return 例:[{CIR_OTN_CIRCUIT_ID=1,CIR_OTN_CIRCUIT_ROUTE_ID=1},...,{CIR_OTN_CIRCUIT_ID=2,CIR_OTN_CIRCUIT_ROUTE_ID=4}]
	 */
	public List<Map<String,Integer>>getCircuitRouteInfoByPtp(@Param(value="ptpId")int ptpId);
	
	/**
	 * 根据电路Id和路由Id获取路由中符合条件的网元信息
	 * @param cirId 电路Id
	 * @param routeId 路由Id
	 * @return
	 */
	public List<Map>getNeInRoute(@Param(value="cirId")int cirId,@Param(value="routeId")int routeId,@Param(value="endRouteId")int endRouteId);
	
	/**
	 * 根据电路Id和路由Id获取路由中符合条件的端口信息
	 * @param cirId 电路Id
	 * @param routeId 路由Id
	 * @return
	 */
	public List<Map>getRouteInfo(@Param(value="cirId")int cirId,@Param(value="routeId")int routeId);
	
	/**
	 * 根据复用段Id(mulId)和网元Id(neId)在t_pm_multi_sec_ne表中查询MULTI_SEC_NE_ID
	 * @param mulId 复用段Id
	 * @param neId 网元Id
	 * @param direction 方向
	 * @return
	 */
	public List<Map>getMultiSecNeId(@Param(value="mulId")int mulId,@Param(value="neId")int neId,@Param(value="direction")int direction);

	public List<Map> getMSInfoListForReport	(@Param(value = "targetList") List<Map> targetList,
			@Param(value="Define")  Map<String, Object> Define);
	
	/**
	 * 查询任务节点信息
	 * 
	 * @param searchCond
	 * @return
	 */
	public List<Map> getTargetList(
			@Param(value = "taskId") int taskId);
}