package com.fujitsu.IService;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;

/**
 * @author wangjian
 * 
 */
public interface IMultipleSectionManagerService {

	/**
	 * 根据网管分组id查询网管
	 * 
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectAllEMS(int emsGroupId, int isAll)
			throws CommonException;

	/**
	 * 查询网管分组
	 * 
	 * @return
	 * @throws CommonException
	 */
	public List<Map> selectAllGroup() throws CommonException;

	/**
	 * 查询干线信息
	 * 
	 * @param map
	 *            网管id，网管分组id ，start ，limit
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectTrunkLine(Map map) throws CommonException;

	/**
	 * 新增一条干线
	 * 
	 * @param map
	 *            网管id，网管分组id ，start ，limit
	 * @return
	 * @throws CommonException
	 */
	public CommonResult addTrunkLine(Map map) throws CommonException;

	/**
	 * 删除干线
	 * 
	 * @param 干线id
	 * @return
	 * @throws CommonException
	 */
	public CommonResult deleteTrunkLine(List<Map> listMap)
			throws CommonException;

	/**
	 * 修改干线
	 * 
	 * @param 干线id
	 * @return
	 * @throws CommonException
	 */
	public CommonResult modifyTrunkLine(List<Map> listMap)
			throws CommonException;

	/**
	 * 查询光复用段
	 * 
	 * @param 查询光复用段
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectMultipleSection(Map map)
			throws CommonException;

	/**
	 * 新增光复用段
	 * 
	 * @param
	 * @return
	 * @throws CommonException
	 */
	public CommonResult addMultipleSection(Map map) throws CommonException;

	/**
	 * 删除光复用段
	 * 
	 * @param 光复用段id
	 * @return
	 * @throws CommonException
	 */
	public void deleteMultipleSection(List<Map> listMap) throws CommonException;

	/**
	 * 修改光复用段
	 * 
	 * @param
	 * @return
	 * @throws CommonException
	 */
	public CommonResult modifyMultipleSection(List<Map> listMap)
			throws CommonException;

	/**
	 * 查询光复用段所涉及的网元
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectMultipleSectionNe(Map map)
			throws CommonException;

	/**
	 * 根据网元id查询网元相关信息
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectByNeId(Map map) throws CommonException;

	/**
	 * 修改光复用段网元信息
	 * 
	 * @param
	 * @return
	 * @throws CommonException
	 */
	public void saveNeForward(List<Map> listMap, int mulId, int direction)
			throws CommonException;

	/**
	 * 根据ptpId获取ptpName
	 * 
	 * @param
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selecrPtpName(List<Map> listMap)
			throws CommonException;

	/**
	 * 查询光放型号
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectModelType(Map map) throws CommonException;

	/**
	 * 查询去重的光放型号
	 * 
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectMultipleModel() throws CommonException;

	/**
	 * 查询去重的光放型号
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectStandOptVal(Map map)
			throws CommonException;

	/**
	 * 新增光放
	 * 
	 * @param
	 * @return
	 * @throws CommonException
	 */
	public CommonResult addStandOptVal(Map map) throws CommonException;

	/**
	 * 删除光放单元
	 * 
	 * @param
	 * @return
	 * @throws CommonException
	 */
	public CommonResult deleteStandOptVal(List<Map> listMap)
			throws CommonException;

	/**
	 * 修改光放单元
	 * 
	 * @param
	 * @return
	 * @throws CommonException
	 */
	public CommonResult modifyStandOptVal(List<Map> listMap)
			throws CommonException;

	/**
	 * 根据网元id查询光复用段ptp路由
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectPtpRouteList(Map map)
			throws CommonException;

	/**
	 * 修改光复用段ptp路由设置
	 * 
	 * @param
	 * @return
	 * @throws CommonException
	 */
	public void savePtpForward(List<Map> listMap, int neId, int direction)
			throws CommonException;

	/**
	 * 光复用段排序
	 * 
	 * @param
	 * @return
	 * @throws CommonException
	 */
	public CommonResult sortMultipleSection(List<Map> listMap)
			throws CommonException;

	/**
	 * 查询光复用段相关信息
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectMultipleAbout(Map map)
			throws CommonException;

	/**
	 * 查询光复用段详细信息
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> selectMultiplePtpRoute(Map map)
			throws CommonException;

	/**
	 * 保存光复用段详细信息
	 * 
	 * @param
	 * @return
	 * @throws CommonException
	 */
	public void saveMultipleDetail(List<Map> listMap) throws CommonException;

	/**
	 * 同步光复用段性能
	 * 
	 * @param cutoverFlag
	 * 
	 * @param
	 * @return
	 * @throws CommonException
	 */
	public void sycPmByMultiple(List<Map> listMap, int cutoverFlag)
			throws CommonException;

	/**
	 * 根据端口同步光复用段性能
	 * 
	 * @param cutoverFlag
	 * 
	 * @param
	 * @return
	 * @throws CommonException
	 */
	public void sycPmByMultipleByPort(List<Map> listMap)
			throws CommonException;
	
	/**
	 * 同步历史性能
	 * 
	 * @param
	 * @return
	 * @throws CommonException
	 */
	public void sycPmHistory(Map map) throws CommonException;

	/**
	 * 由任务查询出复用段性能
	 * 
	 * @param taskId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getPmFromTaskId(int taskId)
			throws CommonException;

	/**
	 * 由任务查询出复用段性能
	 * 
	 * @param taskId
	 * @param steps
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> getPmFromTaskId(int taskId, int steps, Date startTime,
			Date endTime) throws CommonException;

	/**
	 * 由任务查询出复用段性能
	 * 
	 * @param taskId
	 * @param steps
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getPmFromTaskId(int taskId, int steps)
			throws CommonException;

	/**
	 * 将复用段性能整理为报表格式
	 * 
	 * @param multiSecPm
	 *            getPmFromTaskId获得的性能列表
	 * @return
	 */
	public Map<String, List<Map<String, Object>>> getExportPmInfo(
			Map<String, Object> multiSecPm);

	/**
	 * 导出光复用段详细信息
	 * 
	 * @param
	 * @return
	 * @throws CommonException
	 */
	public CommonResult ecportSecDetail(Map map) throws CommonException;

	/**
	 * 导出光复用段所有信息
	 * 
	 * @param
	 * @return
	 * @throws CommonException
	 */
	public CommonResult exportAllInformation(List<Map> listMap) throws CommonException;
	
	/**
	 * 导入干线及光复用段
	 * 
	 * @param uploadFile
	 * @param fileName
	 * @param path
	 * @return
	 * @throws CommonException
	 */
	public Map uploadTrunkLine(File uploadFile, String fileName, String path,
			int emsId) throws CommonException;
	

	/**
	 * 导入光复用段全部信息
	 * 
	 * @param uploadFile
	 * @param fileName
	 * @param path
	 * @return
	 * @throws CommonException
	 */
	public Map uploadSectionAll(File uploadFile, String fileName, String path) throws CommonException;
	
	
	/**
	 * 返回复用段状态
	 * 
	 * @param map
	 *            （包含复用段Id）
	 * @return
	 */
	public int multiState(Map map);

	/**
	 * 复用段公共导出方法
	 * 
	 * @param taskId
	 * @throws CommonException
	 */
	public CommonResult exportMsPmReport(int taskId, boolean isInstantGen)
			throws CommonException;

	/**
	 * 复用段即时生成导出方法
	 * 
	 * @param nodeList
	 * @param searchCond
	 * @param currentUserId
	 * @return
	 * @throws CommonException
	 */
	CommonResult exportMsPmReportInstant(List<Map> nodeList,
			Map<String, String> searchCond, int currentUserId)
			throws CommonException;

	/**
	 * 获取复用段统计信息（步径一天）
	 * 
	 * @param idList
	 *            复用段ID列表
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return
	 */
	List<Map> getMultiSecDetailInfoForReportSummary(List<Integer> idList,
			Date startTime, Date endTime);

	/**
	 * 根据网元id查询光复用段ptp备用路由
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> selectSubPtpRouteList(Map map) throws CommonException;
	
	/**
	 * 根据传递的端口(startPtp),为指定的光复用段(mulId)，生成指定方向(direction)的路由。
	 * @param mulId 光复用段id
	 * @param direction 方向 1：正向，2：反向
	 * @param startPtp 起始的ptpId
	 * @return CommonResult 返回操作是否成功的信息
	 * @throws CommonException
	 */
	public CommonResult autoCreateRoute(int mulId,int direction,int startPtp)throws CommonException;
	
	/**
	 * 判断指定方向(direction),指定(mulId)上是否已经存在路由记录
	 * @param  mulId 光复用段id
	 * @param direction 方向 1：正向，2：反向
	 * @return true:有记录,false:没有记录
	 * @throws CommonException
	 */
	public Boolean hasRecord(int mulId,int direction)throws CommonException;
	
	/**
	 * 标记删除光复用段上的路由记录,即将t_pm_multi_sec_ne,t_pm_multi_sec_ptp表中IS_DEL字段置1
	 * @param mulId 指定的光复用段id
	 * @param direction 指定的方向：1正向,2反向
	 * @throws CommonException
	 */
	public void deleteRouteRecordByMark(int mulId,int direction)throws CommonException;
	
	/**
	 * 真实删除光复用段上的路由记录,即将t_pm_multi_sec_ne,t_pm_multi_sec_ptp表中标记删除的数据彻底删除
	 * @param mulId 指定的光复用段id
	 * @param direction 指定的方向：1正向,2反向
	 * @throws CommonException
	 */
	public void deleteRouteRecord(int mulId,int direction)throws CommonException;

}
