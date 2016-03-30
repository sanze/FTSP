package com.fujitsu.dao.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.fujitsu.common.CommonException;

public interface ConnectionManagerMapper {

	/**
	 * 从列表中选择 前台需要的 连接记录
	 * 
	 * @param map
	 * @param str 
	 * @return
	 */
	public List<Map> getConnectionListByGroupId(@Param(value = "map") Map map);

	/**
	 * 根据网管连接编号查询该连接信息
	 * 
	 * @param emsConnectionId
	 * @return
	 */
	public Map<String, Object> getConnectionByEmsConnectionId(
			@Param(value = "emsConnectionId") Integer emsConnectionId);

	/**
	 * 根据网管分组编号查询该分组的信息
	 * 
	 * @param connectGroupId
	 * @return
	 */
	public List<Map> getConnectGroup(@Param(value = "emsGroupId") int emsGroupId);

	/**
	 * 更新网管采集状态
	 * 
	 * @param map
	 */
	public void updateCollectStatus(@Param(value = "map") Map map);

	/**
	 * 更新网管采集状态以及暂停采集时间
	 * 
	 * @param map
	 */
	public void updateCollectStatusAndTime(@Param(value = "map") Map map);
	
	/**
	 * 删除指定任务暂停时间
	 * 
	 * @param taskId
	 */
	public void deleteTaskForbiddenTime(@Param(value = "taskId") int taskId);

	/**
	 * 向连接表中插入新 Corba 连接记录
	 * 
	 * @param map
	 */
	public void addCorbaConnection(HashMap<String, Object> map);

	/**
	 * 向连接表中插入新 Telnet 连接记录
	 * 
	 * @param map
	 */
	public void addTelnetConnection(HashMap<String, Object> map);

	/**
	 * 向网元表中插入新 网元记录
	 * 
	 * @param hashMap
	 */
	public void addNeInfo(
			@Param(value = "hashMap") HashMap<String, Object> hashMap);

	/**
	 * 根据网元本地名称取出网元编号
	 * 
	 * @param nativeEmsName
	 * @return
	 */
	public String getNeInfoByNativeName(
			@Param(value = "nativeEmsName") String nativeEmsName);

	/**
	 * 根据网元Id取出网元信息
	 * 
	 * @param neId
	 * @return
	 */
	public Map<String, Object> getNeInfoByNeId(@Param(value = "neId") int neId);

	/**
	 * 根据网元neSerialNo(Name)取出网元信息
	 * 
	 * @param neSerialNo
	 * @return
	 */
	public Map<String, Object> getNeInfoByNeSerialNo(
			@Param(value = "neId") int neId);

	/**
	 * 修改连接信息
	 * 
	 * @param map
	 * @throws CommonException
	 */
	public void modifyConnection(
			@Param(value = "map") HashMap<String, Object> map)
			throws CommonException;

	/**
	 * 删除连接信息
	 * 
	 * @param emsConnectionId
	 * @throws CommonException
	 */
	public void deleteConnection(
			@Param(value = "emsConnectionId") int emsConnectionId);

	/**
	 * 根据网元Id更新网元信息
	 * 
	 * @param hashMap
	 * @throws CommonException
	 */
	public void modifyNeInfoByNeId(
			@Param(value = "hashMap") HashMap<String, Object> hashMap);

	/**
	 * 删除网元信息
	 * 
	 * @param neId
	 * @throws CommonException
	 */
	public void deleteNeInfoByNeId(@Param(value = "neId") Integer neId);

	public List<Map> getConnectService();

	public List<Map> startConnect(
			@Param(value = "emsConnectionId") int emsConnectionId);

	public List<Map> disConnect(
			@Param(value = "emsConnectionId") int emsConnectionId);

	/**
	 * 从列表中选择 前台需要的 网管分组记录
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> selectEmsGroupList(
			@Param(value = "map") Map<String, Object> map);

	/**
	 * 新增网管分组
	 * 
	 * @param emsGroupName
	 * @param emsGroupNote
	 * @return
	 * @throws CommonException
	 */
	public void addEmsGroup(@Param(value = "map") Map<String, Object> map);

	/**
	 * 删除网管分组
	 * 
	 * @param emsGroupId
	 */
	public void deleteEmsGroup(@Param(value = "map") Map<String, Object> map);

	/**
	 * 更新网管连接信息
	 * 
	 * @param emsGroupId
	 */
	public void updateEmsConnnectionByEmsGroupId(
			@Param(value = "map") Map<String, Object> map);

	/**
	 * 修改网管分组信息
	 * 
	 * @param map
	 */
	public void modifyEmsGroup(@Param(value = "map") Map<String, Object> map);

	/**
	 * 新增子网
	 * 
	 * @param map
	 */
	public void addSubnet(@Param(value = "map") Map<String, Object> map);

	/**
	 * 修改子网
	 * 
	 * @param map
	 */
	public void modifySubnet(@Param(value = "map") Map<String, Object> map);

	/**
	 * 根据子网编号获取子网信息
	 * 
	 * @param subnetId
	 * @return
	 */
	public Map<String, Object> getSubnetBySubnetId(
			@Param(value = "subnetId") Integer subnetId);

	/**
	 * 更新子网对应的网元信息表
	 * 
	 * @param map
	 */
	public void updateNeInfoBySubnetId(
			@Param(value = "map") Map<String, Object> map);

	/**
	 * 删除子网信息
	 * 
	 * @param map
	 */
	public void deleteSubnet(@Param(value = "map") Map<String, Object> map);

	/**
	 * 获取同步网元信息列表
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getSyncNeListByEmsInfo(
			@Param(value = "map") Map<String, Object> map);

	/**
	 * 获取网管
	 * 
	 * @param connectGroupId
	 * @return
	 */
	public List<Map> getEmsConnection(
			@Param(value = "emsGroupId") Integer emsGroupId);

	/**
	 * 根据网管编号获取本地该网管下的网元列表信息
	 * 
	 * @param emsConnectionId
	 * @return
	 */
	public List<Map> getNeListSyncByEmsConnectionId(
			@Param(value = "emsConnectionId") Integer emsConnectionId);

	/**
	 * 更新网管下子网的下级子网的父节点
	 * 
	 * @param subnetId
	 */
	public void updateSubnetByParentSubnetId(
			@Param(value = "subnetId") Integer subnetId);

	/**
	 * 更新即将删除子网下的网元子网信息
	 * 
	 * @param emsConnectionId
	 */
	public void updateNeBySubnetId(
			@Param(value = "subnetId") Integer emsConnectionId);

	/**
	 * 从子网表中删除子网
	 * 
	 * @param subnetId
	 */
	public void deleteSubnetBySubnetId(
			@Param(value = "subnetId") Integer subnetId);

	/**
	 * 更新子网下子网的父子网为自己的子网
	 * 
	 * @param subnetId
	 */
	public void updateSubnetBySubnetId(
			@Param(value = "parentSubnetId") Integer parentSubnetId,
			@Param(value = "subnetId") Integer subnetId);

	/**
	 * 更新即将删除子网下的网元子网信息
	 * 
	 * @param emsConnectionId
	 */
	public void updateNeByParentSubnetId(
			@Param(value = "parentSubnetId") Integer parentSubnetId,
			@Param(value = "subnetId") Integer subnetId);

	/**
	 * 获取网管下未分组的网元
	 * @@@分权分域到网元@@@
	 * @param emsConnectionId
	 * @return
	 */
	public List<Map> getNeListByEmsConnnectionId(
			@Param(value = "emsConnectionId") Integer emsConnectionId,
			@Param(value = "userId")Integer userId, @Param(value = "Define")Map define);

	/**
	 * 获取网管下具体子网下的网元
	 * @@@分权分域到网元@@@
	 * @param emsConnectionId
	 * @param subnetId
	 * @return
	 */
	public List<Map> getNeListByEmsConnnectionIdAndSubnetId(
			@Param(value = "emsConnectionId") Integer emsConnectionId,
			@Param(value = "subnetId") Integer subnetId, @Param(value = "userId")Integer userId,
			@Param(value = "Define")Map define);

	/**
	 * 新增网元
	 * 
	 * @param neMap
	 */
	public void addTelnetNe(@Param(value = "neMap") Map<String, Object> neMap);

	/**
	 * 根据网元编号获取网元信息
	 * 
	 * @param neId
	 * @return
	 */
	public Map<String, Object> getTelnetNeByNeId(
			@Param(value = "map")  Map<String, Object> map);

	/**
	 * 修改网元
	 * 
	 * @param map
	 */
	public void modifyCorbaNe(@Param(value = "map") Map<String, Object> map);

	/**
	 * 修改网元
	 * 
	 * @param map
	 */
	public void modifyTelnetNe(@Param(value = "map") Map<String, Object> map);

	/**
	 * 删除网元
	 * 
	 * @param map
	 */
	public void deleteTelnetNe(@Param(value = "map") Map<String, Object> map);

	/**
	 * 更新已分组网元为未分组网元
	 * 
	 * @param subnetId
	 */
	public void updateClassifiedNeBySubnetId(
			@Param(value = "subnetId") Integer subnetId);

	
	/**
	 * 保存已分组的网元
	 * 
	 * @param ne
	 */
	public void saveClassifiedNe(@Param(value = "subnetId") Integer subnetId,
			@Param(value = "neId") Integer neId);

	/**
	 * 交叉连接管理页面下 根据网管分组和网管获取交叉连接信息列表
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getCrossConnectListByEmsInfo(
			@Param(value = "map") Map<String, Object> map);

	/**
	 * 交叉连接管理页面下 网元上的交叉连接信息记录
	 * 
	 * @param map
	 * @return
	 */
	public int getCrsSdhNeDetailInfoCount(@Param(value = "map") Map map);

	/**
	 * 交叉连接管理页面下 网元上的交叉连接信息记录
	 * 
	 * @param map
	 * @return
	 */
	public int getCrsOtherNeDetailInfoCount(@Param(value = "map") Map map);

	/**
	 * 获取交叉连接管理页面下某网元上的SDH交叉连接信息
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getCrsSdhNeDetailInfoByNeId(
			@Param(value = "map") Map<String, Object> map);

	/**
	 * 获取交叉连接管理页面下某网元上的Other交叉连接信息
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getCrsOtherNeDetailInfoByNeId(
			@Param(value = "map") Map<String, Object> map);

	/**
	 * 以太网管理页面下 根据网管分组和网管获取交叉连接信息列表
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getMstpListByEmsInfo(
			@Param(value = "map") Map<String, Object> map);

	/**
	 * 拓扑链路同步记录列表获取
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getTopoLinkSyncListByEmsGroupId(
			@Param(value = "map") Map<String, Object> map);

	/**
	 * 网管同步记录信息
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getEmsConnectionSyncInfo(
			@Param(value = "map") Map<String, Object> map);

	/**
	 * 网管同步管理页面 启动任务操作
	 * 
	 * @param map
	 */
	public void startTask(@Param(value = "map") Map map);

	/**
	 * 网管同步管理页面 挂起任务操作
	 * 
	 * @param map
	 */
	public void disTask(@Param(value = "map") Map map);

	/**
	 * 网管管理页面中 任务状态 弹出窗口的页面显示信息
	 * 
	 * @param emsConnectionId
	 * @return
	 */
	public List<Map> getTaskDetailInfo(
			@Param(value = "emsConnectionId") Integer emsConnectionId);

	/**
	 * 接入服务器页面中检索接入服务器记录
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getSysServiceRecordInfo(@Param(value = "map") Map map);

	/**
	 * 新增接入服务器
	 * 
	 * @param map
	 */
	public void addSysService(@Param(value = "map") Map map);

	/**
	 * 删除接入服务器
	 * 
	 * @param map
	 */
	public void deleteSysService(@Param(value = "map") Map map);

	/**
	 * 修改接入服务器
	 * 
	 * @param sysServiceMap
	 */
	public void modifySysService(@Param(value = "map") Map map);

	/**
	 * 检索要修改的接入服务器
	 * 
	 * @param sysSvcRecordId
	 * @return
	 */
	public Map getSysServiceBySysSvcId(
			@Param(value = "sysSvcRecordId") Integer sysSvcRecordId);

	/**
	 * 获取网管下所有的网元
	 * 
	 * @param emsConnectionId
	 * @return
	 */
	public List<Map> getAllNeListByEmsConnnectionId(
			@Param(value = "emsConnectionId") Integer emsConnectionId);
	
	/**
	 * 删除该网管任务下的所有网元执行信息
	 * @param taskId
	 */
	public void deleteAllNeListByTaskId(
			@Param(value = "taskId") Integer taskId);
	
	/**
	 * 获取该任务下需要执行操作的详细信息
	 * @param taskId
	 * @return
	 */
	public List<Map> getTaskDetailInfoByTaskId(
			@Param(value = "taskId") Integer taskId);
	
	/**
	 * 网管同步任务执行时  向任务执行表中新增数据
	 * @param map
	 */
	public void addTaskRunDetailInfo(HashMap<String, Object> map);

	/**
	 * 拓扑链路同步中变化的链路信息
	 * 
	 * @param aEndPtp
	 * @param zEndPtp
	 * @param changeType
	 * @return
	 */
	public Map getLinkByChangeInfo(@Param(value = "endPtp") int endPtp);

	/**
	 * 更新网管同步管理页面任务状态 页面设置暂停时间时更新任务表中任务执行状态
	 * 
	 * @param taskId
	 */
	public void updateEmsConnectionSync(@Param(value = "taskId") Integer taskId);

	/**
	 * 网管同步管理页面任务状态 页面设置暂停时间时向任务参数表中插入设置的暂停时间
	 * 
	 * @param map
	 */
	public void insertEmsSyncTaskParam(@Param(value = "map") Map map);

	/**
	 * 网管同步管理页面任务状态 页面设置暂停时间时 检索该任务是否已设置暂停时间
	 * 
	 * @param map
	 * @return
	 */
	public Map getEmsSyncTaskParamByTaskId(@Param(value = "map") Map map);

	/**
	 * 网管同步管理页面任务状态 页面再次设置暂停时间时 暂停结束时间尚未结束时更新暂停时间
	 * 
	 * @param map
	 */
	public void updateEmsSyncTaskParamAfter(@Param(value = "map") Map map);

	/**
	 * 网管同步管理页面任务状态 页面再次设置暂停时间时 暂停结束时间已经结束时更新暂停时间
	 * 
	 * @param map
	 */
	public void updateEmsSyncTaskParamBefore(@Param(value = "map") Map map);

	/**
	 * 网管同步管理页面任务状态 手动同步操作的时候判断该网管任务的参数表中有没有设置暂停时间
	 * 
	 * @param emsConnectionId
	 * @return
	 */
	public Map getEmsSyncTaskInfoByEmsConnectionId(
			@Param(value = "emsConnectionId") Integer emsConnectionId);

	/**
	 * 检查南向连接管理页面中 网管名称是否存在的问题
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> checkConnectionNameExist(
			@Param(value = "map") Map<String, Object> map);

	/**
	 * 检查南向连接管理页面中 网管IP地址是否已经存在
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> checkIpAddressExist(
			@Param(value = "map") Map<String, Object> map);

	/**
	 * 检查子网管理页面中 新增子网时是否存在子网名称
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> checkSubnetNameExist(
			@Param(value = "map") Map<String, Object> map);

	/**
	 * 更新网管信息
	 * 
	 * @param map
	 */
	public void updateEmsConnection(@Param(value = "map") Map map);

	/**
	 * 添加网管时想任务表中插入任务
	 * 
	 * @param insert
	 */
	public void insertTaskInfo(@Param(value = "map") Map map);

	/**
	 * 添加网管时想任务表中插入任务
	 * 
	 * @param map
	 */
	public void insertTask(@Param(value = "map") Map map);

	/**
	 * 更新任务时间
	 * 
	 * @param map
	 */
	public void updateTask(@Param(value = "map") Map map);

	/**
	 * 获取南向连接总数目
	 * 
	 * @param map
	 * @return
	 */
	public int getConnectionListCount(@Param(value = "map") Map map);

	/**
	 * 获取接入服务器总数
	 * 
	 * @param map
	 * @return
	 */
	public int getSysServiceRecordCount(@Param(value = "map") Map map);

	/**
	 * 获取网管分组管理记录数
	 * 
	 * @param map
	 * @return
	 */
	public int selectEmsGroupListCount(@Param(value = "map") Map map);

	/**
	 * 网元管理页面网元数量统计
	 * 
	 * @param map
	 * @return
	 */
	public int getSyncNeListByEmsInfoCount(@Param(value = "map") Map map);

	/**
	 * 获取交叉连接页面中 网元记录数
	 * 
	 * @param map
	 * @return
	 */
	public int getCrossConnectListCount(@Param(value = "map") Map map);

	/**
	 * 获取以太网页面中同步网元记录数
	 * 
	 * @param map
	 * @return
	 */
	public int getMstpListCount(@Param(value = "map") Map map);

	/**
	 * 获取拓扑链路同步页面网元记录数
	 * 
	 * @param map
	 * @return
	 */
	public int getTopoLinkSyncListCount(@Param(value = "map") Map map);

	/**
	 * 获取网管同步记录数
	 * 
	 * @param map
	 * @return
	 */
	public int getEmsConnectionSyncCount(@Param(value = "map") Map map);

	/**
	 * 获取网管下 的任务信息
	 * 
	 * @param emsId
	 * @param i
	 * @param j
	 * @return
	 */
	public Map getEmsSyncTask(@Param(value = "emsId") int emsId);

	/**
	 * 
	 * @param delete
	 */
	public void deleteByParameter(@Param(value = "map") Map map);

	/**
	 * 网元同步时 更新网元表中的交叉连接同步信息
	 * 
	 * @param neId
	 * @param neSync
	 * @param result
	 */
	public void updateNeCRSInfo(@Param(value = "neId") Integer neId,
			@Param(value = "neSync") Integer neSync,
			@Param(value = "result") String result);

	/**
	 * 网元同步时 更新网元表中的基础信息同步信息
	 * 
	 * @param neId
	 * @param neSync
	 * @param result
	 */
	public void updateNeBasicSyncInfo(@Param(value = "neId") Integer neId,
			@Param(value = "neSync") Integer neSync,
			@Param(value = "result") String result);

	/**
	 * 网元同步时 更新网元表中的以太网同步信息
	 * 
	 * @param neId
	 * @param neSync
	 * @param result
	 */
	public void updateNeMstpSyncInfo(@Param(value = "neId") Integer neId,
			@Param(value = "neSync") Integer neSync,
			@Param(value = "result") String result);

	/**
	 * 链路同步时 更新网管表中链路同步信息
	 * 
	 * @param emsConnectionId
	 * @param linkSync
	 * @param result
	 */
	public void updateEmsLinkSyncInfo(
			@Param(value = "emsConnectionId") Integer emsConnectionId,
			@Param(value = "linkSync") int linkSync,
			@Param(value = "result") String result);

	/**
	 * 网管同步管理页面 更新任务状态
	 * 
	 * @param taskId
	 * @param emsSyncValue
	 */
	public void updateTaskStatus(@Param(value = "taskId") Integer taskId,
			@Param(value = "emsSyncValue") int emsSyncValue,
			@Param(value = "nextTime") String nextTime);
	
	/**
	 * 
	 * @param taskId
	 * @param emsSyncValue
	 */
	public void updateTaskDetailInfo(@Param(value = "taskId") Integer taskId,
			@Param(value = "targetId") Integer targetId,
			@Param(value = "targetType") Integer targetType,
			@Param(value = "taskDetailType") Integer taskDetailType,
			@Param(value = "runResult") Integer runResult,
			@Param(value = "taskDetailInfo") String taskDetailInfo);

	/**
	 * 网管同步管理页面 手动同步操作时获取任务状态信息
	 * 
	 * @param taskId
	 * @return
	 */
	public String getTaskStatusValue(@Param(value = "taskId") Integer taskId);

	/**
	 * 检索记录
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getRecord(@Param(value = "map") Map<String, Object> map);

	/**
	 * 根据网管分组名称查询网管分组
	 * 
	 * @param map
	 * @return
	 */
	public Map getEmsGroupByName(
			@Param(value = "map") HashMap<String, Object> map);

	/**
	 * 新增、修改子网时 检查是否已经存在
	 * 
	 * @param map
	 * @return
	 */
	public Map getSubnetInfo(
			@Param(value = "map") HashMap<String, Object> map);

	/**
	 * 获取网管暂停时间
	 * @param map
	 * @return
	 */
	public Integer pauseCollectTime(@Param(value = "map")  Map map);

	/**
	 * 修改连接时判断连接记录是否存在
	 * @param map
	 * @return
	 */
	public Map getConnectionByInfo(
			@Param(value = "map") HashMap<String, Object> map);

	/**
	 * 修改任务状态
	 * @param map
	 */
	public void updateTaskSetting(@Param(value = "result") Integer result,@Param(value = "taskId") Integer taskId);

	/**
	 * 根据网管编号获取网管采集相关信息
	 * @param map
	 * @return
	 */
	public Map getEmsCollectInfoByEmsConnectionId(@Param(value = "map") Map map);

	/**
	 * 更新任务控制中暂停采集状态下的暂停时间
	 * @param map
	 */
	public void updateCollectTime(@Param(value = "map") Map map);
	
	/**
	 * 获取网元暂停时间
	 * 
	 * @param emsId
	 * @return
	 */
	public String getTaskForbiddenTime(@Param(value = "taskId") int taskId);
	
	/**
	 * 
	 * @param taskId
	 * @param runResult
	 */
	public Integer getTaskRunResultCount(@Param(value = "taskId") Integer taskId,
			@Param(value = "runResult") Integer runResult);
	
	/**
	 * 获取首页显示的南相连接各状态数量
	 * @param map
	 * @return
	 */
	public List<Map> getConnectionInfoByConnectionStatus(@Param(value = "map") Map map);
	
	/**
	 * 根据网元编号获取网元信息
	 * 
	 * @param neId
	 * @return
	 */
	public Map<String, Object> getTelnetNeByNeInfo(
			@Param(value = "map")  Map<String, Object> map);

	/**
	 * 检索是否重名的接入服务器或者Ip
	 * 
	 * @param map
	 * @return
	 */
	public Map getSysServiceBySvcInfo(
			@Param(value = "map") HashMap<String, Object> map);
	
	/**
	 * 检索接入服务器Ip地址以及端口号
	 * 
	 * @param map
	 * @return
	 */
	public Map getSysServiceBySvcIpAddress(@Param(value = "map") HashMap<String, Object> map);
	
	/*
	 * 更新接入服务器状态
	 * 参数：
	 */
	public void updateServerStatus(
			@Param(value = "status") Integer status,
			@Param(value = "recordId") Integer recordId);	
	
	/*
	 * 根据接入服务器Id批量更新网管连接状态
	 * 参数：
	 */
	public void updateEmsConnectStatusByServerId(
			@Param(value = "collectStatus") Integer collectStatus,
			@Param(value = "sysServiceId") Integer sysServiceId,
			@Param(value = "exceptionReason") String exceptionReason);
	
	/**
	 * 查询所有接入服务器配置数据
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> selectAllSvcRecord();
	
	/**
	 * 设置链路同步的同步模式
	 * @param emsConnectionId
	 * @param syncMode
	 */
	public void updateSyncMode(
			@Param(value = "emsConnectionId") Integer emsConnectionId,
			@Param(value = "syncMode") Integer syncMode);
}