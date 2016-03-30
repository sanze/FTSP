package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.Result;
import com.fujitsu.dao.mysql.bean.ResourceUnitManager;


public interface INxReportManagerService {
	//--------------------------THJ-----------------------------
	/**
	 * 根据板卡获取ptp
	 * @param jsonString
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getPtpByUnit(String jsonString) throws CommonException;
	/**
	 * 保存板卡接口信息
	 * @param paramMap
	 * @param sysUserId
	 * @return
	 * @throws CommonException
	 */
	public int saveUnitInterface(Map<String, String> paramMap, Integer sysUserId) throws CommonException;

	/**
	 * 板卡接口信息是否存在
	 * @param paramMap
	 * @param sysUserId
	 * @return
	 * @throws CommonException
	 */
	public int isUnitInterfaceExist(Map<String, String> paramMap,
			Integer sysUserId) throws CommonException;


	/**
	 * 根据网元表查找板卡接口
	 * @param nodeList
	 * @param paramMap
	 * @param start
	 * @param limit
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> searchUnitInterfaceByNeList(List<Map> nodeList,
			Map<String, String> paramMap,
			int start, int limit) throws CommonException;

	/**
	 * 删除板卡接口
	 * @param paramMap
	 * @throws CommonException
	 */
	public void delUnitInterface(Map<String, String> paramMap)throws CommonException;
	/**
	 * 关联光口标准
	 * @param paramMap
	 * @throws CommonException
	 */
	public void relateOpticalStandardValue(Map<String, String> paramMap)throws CommonException;

	/**
	 * 获取板卡接口信息，修改时候用
	 * @param paramMap
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getUnitInterface(Map<String, String> paramMap)throws CommonException;
	
	/**
	 * 获取已经关联的ptp信息
	 * @param paramMap
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getUsedPtp(Map<String, String> paramMap)
			throws CommonException;
	/**
	 * 更新板卡接口信息
	 * @param paramMap
	 * @return
	 * @throws CommonException
	 */
	public int updateUnitInterface(Map<String, String> paramMap)throws CommonException;
	
	/**
	 * 预览Excel文件
	 * @param paramMap
	 * @return
	 * @throws CommonException
	 */
	public String getExcelPreview(Map<String, String> paramMap)throws CommonException;
	
	/**
	 * 保存板卡接口信息
	 * @param paramMap
	 * @param sysUserId
	 * @return
	 * @throws CommonException
	 */
	public int saveOptSwitch(Map<String, String> paramMap, List<Map> nodeList) throws CommonException;
	/**
	 * 删除板卡接口
	 * @param paramMap
	 * @throws CommonException
	 */
	public void delOptSwitch(Map<String, String> paramMap)throws CommonException;
	/**
	 * 更新板卡接口信息
	 * @param paramMap
	 * @return
	 * @throws CommonException
	 */
	public int updateOptSwitch(Map<String, String> paramMap, List<Map> modifyList)throws CommonException;
	/**
	 * 根据ptpId查询业务板卡信息
	 * @param unitId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getBusinessPtpInfo(int unitId)throws CommonException;
	
	/**
	 * 获取保存的业务板卡信息
	 * @param paramMap
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getSavedBusinessPtpInfo(
			Map<String, String> paramMap)throws CommonException;
	
	
	
	//--------------------------报表导出相关----------------------------
		/**
		 * 波长转换表导出功能实现
		 * @param targetList 目标波分方向ID列表
		 * @param searchCond 条件
		 * @param sysUserId 用户ID
		 * @param genType 生成类型<br>0：计划任务 <br>1：即时生成
		 * @return 生成的文件路径
		 * @throws CommonException
		 */
		public String getReport_WaveTransOUT(List<Map>targetList, Map<String, String> searchCond, Integer sysUserId, int genType)
				throws CommonException;
		/**
		 * 波长转换表预览功能实现
		 * @param targetList
		 * @param searchCond
		 * @return
		 * @throws CommonException
		 */
		public String getReportPreview_WaveTransOUT(List<Map>targetList, Map<String, String> searchCond)
				throws CommonException;
		
		/**
		 * PTN/IPRAN端口作业计划功能实现
		 * @param targetList ptpId
		 * @param condMap 条件
		 * @param genType 是周期还是即时（暂时没用）
		 * @param isPreview 是否是预览，是则生成空
		 * @param sysUserId 用户ID
		 * @return 生成的文件路径
		 * @throws CommonException
		 */
	public String getReport_PTN_IPRAN(List<Integer> targetList,
			Map<String, String> condMap, int genType, boolean isPreview,
			Integer sysUserId) throws CommonException;
		
	//--------------------------THJ-----------------------------
	
	
	//--------------------------WSS-----------------------------
	
	/**
	 * 获取网元下的板卡
	 * @param paramMap 包含neId
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> getUnitByNeOrWaveDirId(Map<String, String> paramMap) throws CommonException;

	/**
	 * 保存波分方向
	 * @param unitList
	 * @param paramMap
	 * @param sysUserId
	 * @return
	 * @throws CommonException
	 */
	int saveWaveDir(List<Map> unitList, Map<String, String> paramMap,
			Integer sysUserId)throws CommonException;

	/**
	 * 查询波分方向
	 * @param nodeList
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> searchWaveDir(List<Map> nodeList, int start, int limit)throws CommonException;

	/**
	 * 修改波分方向
	 * @param unitList
	 * @param paramMap
	 * @param sysUserId
	 * @return
	 * @throws CommonException
	 */
	int editWaveDir(List<Map> unitList, Map<String, String> paramMap,
			Integer sysUserId) throws CommonException;

	/**
	 * 删除波分方向
	 * @param paramMap
	 * @throws CommonException
	 */
	void deleteWaveDir(Map<String, String> paramMap) throws CommonException;


	/**
	 * 保存报表任务
	 * @param nodeList
	 * @param paramMap
	 * @param sysUserId
	 * @throws CommonException
	 */
	void saveReportTask(List<Map> targetList, Map<String, String> paramMap,
			Integer sysUserId)throws CommonException;

	/**
	 * 加载任务名称下拉框
	 * @param paramMap
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> getTaskNameComboValue(Map<String, String> paramMap)
			throws CommonException;

	/**
	 * 查询报表任务
	 * @param paramMap
	 * @param start
	 * @param limit
	 * @return
	 */
	Map<String, Object> searchReportTask(Map<String, String> paramMap,
			int start, int limit) throws CommonException;

	/**
	 * 初始化报表任务
	 * @param paramMap
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> initReportTaskInfo(Map<String, String> paramMap)
			throws CommonException;

	/**
	 * 根据ID查找波分方向（报表用）
	 * @param nodeList
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> searchWaveDirById(List<Map> nodeList) throws CommonException;

	/**
	 * 更新报表任务
	 * @param targetList
	 * @param paramMap
	 * @param intList 
	 * @throws CommonException
	 */
	void updateReportTask(List<Map> targetList, Map<String, String> paramMap, List<Integer> intList)
			throws CommonException;

	/**
	 * 报表下载-加载报表名称
	 * @param paramMap
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> getTaskNameComboValuePrivilege(
			Map<String, String> paramMap, Integer userId)
			throws CommonException;

	
	/**
	 * 保存报表任务-光放
	 * @param manages 光放盘list
	 * @param paramMap 任务信息
	 * @param sysUserId
	 */
	public void saveReportTaskAMP(List<ResourceUnitManager> manages,
			Map<String, String> paramMap, Integer sysUserId) throws CommonException;
	
	/**
	 *  插入manage记录和manage中的unit记录---需要返回的ID
	 * @param manageList
	 * @return
	 * @throws CommonException
	 */
	List<Map> insertManageWithUnitReturnIds(
			List<ResourceUnitManager> manageList) throws CommonException;
	
	
	/**
	 * 获取NODE信息
	 * @param nodeList
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> getNodeInfo(List<Map> nodeList) throws CommonException;
	
	
	/**
	 * 删除报表任务
	 * @param taskId
	 * @param reportType 
	 * @throws CommonException
	 */
	public void deleteReportTask(int taskId, int reportType) throws CommonException;
	
	
	public Map<String, Object> getCreatorComboValuePrivilege(Integer userId)
			throws CommonException;
	
	
	/**
	 * 根据一个ptplist当做A端，去查找link的Z端
	 * @param intList
	 * @return
	 */
	public List<Map> getLinkByAEnd(List<Integer> intList) throws CommonException;
	
	/**
	 * 保存PTN系统
	 * @param intList 端口id
	 * @param paramMap 系统信息
	 * @param sysUserId
	 * @return
	 */
	public int savePtnSys(List<Integer> intList, Map<String, String> paramMap,
			Integer sysUserId) throws CommonException;
	
	
	
	/**
	 * 查找PTN系统
	 * @param paramMap
	 * @param intList TODO
	 * @param start
	 * @param limit
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getPtnSysList(Map<String, String> paramMap,
			List<Integer> intList, int start, int limit) throws CommonException;

	
	
	/**
	 * 查找系统下的ptp供查看
	 * @param paramMap
	 * @return
	 * @throws CommonException
	 */
	public List<Map> getLinksBySysId(Map<String, String> paramMap)
			throws CommonException;

	/**
	 * 删除PTN系统
	 * @param paramMap
	 * @throws CommonException
	 */
	public void delPtnSys(Map<String, String> paramMap) throws CommonException;
	
	
	/**
	 * 生成PTN峰值流量报表
	 * @param targetIds
	 * @param paramMap
	 * @param reportSchedule
	 * @param b
	 * @param creator
	 */
	public String getReport_PTN_FlowPeak(List<Integer> targetIds,
			Map<String, String> condMap, int genType, boolean isPreview,
			Integer sysUserId) throws CommonException ;

//--------------------------WSS-----------------------------
	
	
//================================dhj============================
	/**
	 * 根据manageIdList删除
	 * @param manageIds
	 * @return	
	 * @throws CommonException
	 */
	public Result deleteUnitReportByManageId(List<Integer> manageIds)throws CommonException;
	/**
	 * 根据manageId删除
	 * @param manageIds
	 * @return
	 * @throws CommonException
	 */
	public Result deleteUnitReportByManageId(int manageIds)throws CommonException;
	/**
	 * 根据ResourceUnitManager删除
	 * @param res
	 * @return
	 * @throws CommonException
	 */
	public Result deleteUnitReport(List<ResourceUnitManager> res)throws CommonException;
	/**
	 * 按manageId和unitId删除t_resource_unit_manage_rel_unit中的一条记录
	 * @param manageId
	 * @param unitId
	 * @return
	 * @throws CommonException
	 */
	public Result deleteUnitInfo(int manageId,int unitId)throws CommonException;
	/**
	 * 插入manage记录和manage中的unit记录
	 * @param manageList
	 * @return
	 * @throws CommonException
	 */
	public Result insertManageWithUnit(List<ResourceUnitManager> manageList)throws CommonException;
	
	/**
	 * 根据manageId获取板卡信息
	 * @param manageId
	 * @return
	 * @throws CommonException
	 */

	public Map<String ,Object>getUnitInfoByManageId(int manageId,int reportType)throws CommonException;
	/**
	 * 更新manage和板卡信息
	 * @param manage
	 * @return
	 * @throws CommonException
	 */
	Result updateManageInfo(ResourceUnitManager manage)throws CommonException;
	/**
	 * 根据taskid获取manage信息
	 * @param taskId
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> getManageInfoByTaskId(int taskId,int reportType)
			throws CommonException;
	
	/**
	 * 光放大器报表导出
	 * @param targetIds
	 * @param searchCond
	 * @param genType
	 * @param isPreview
	 * @return
	 * @throws CommonException
	 */
	public String getAmplifierDataForReport(List<Integer> targetIds, 
			Map<String, String> searchCond, int genType, 
			boolean isPreview, Integer sysUserId) throws CommonException;
	
	/**
	 * 光开关盘报表导出
	 * @param targetIds
	 * @param searchCond
	 * @param genType
	 * @param isPreview
	 * @param sysUserId
	 * @return
	 * @throws CommonException
	 */
	public String getSwitchDataForReport(List<Integer> targetIds, 
			Map<String, String> searchCond, int genType, boolean isPreview,
			Integer sysUserId) throws CommonException;
	
	/**
	 * 合波盘/分波盘报表导出
	 * @param targetIds
	 * @param searchCond
	 * @param genType
	 * @param isPreview
	 * @param sysUserId
	 * @return
	 * @throws CommonException
	 */
	public String getWaveDataForReport(List<Integer> targetIds, 
			Map<String, String> searchCond, int genType, boolean isPreview,
			Integer sysUserId) throws CommonException;
	
	/**
	 * 根据板卡id获取所有端口信息
	 * @param unitId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getPortByUnitId(int unitId)throws CommonException;
	/**
	 * 根据factoryId获取设备型号信息(不包括sdh)
	 * @param factoryId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getProductNameByFactoryIdNoSDH(int factoryId)throws CommonException;
	/**
	 * 根据unitId获取节点详细信息
	 * @param unitIds
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getNodeInfoByUnitId(List<Integer> unitIds)throws CommonException;
	
}
