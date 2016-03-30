package com.fujitsu.IService;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.fujitsu.common.CommonException;
import com.fujitsu.model.EmsConnectionModel;
import com.fujitsu.model.EmsGroupModel;
import com.fujitsu.model.LinkAlterModel;
import com.fujitsu.model.NeModel;
import com.fujitsu.model.SdhCrsModel;
import com.fujitsu.model.SubnetModel;
import com.fujitsu.model.SysServiceModel;

public interface ISouthConnectionService {

	/**
	 * 获取ems的相关信息
	 * @param emsGroupId 
	 * 
	 * @return
	 */
	public Map<String, Object> getConnectionListByGroupId(int userId, int flag ,int start,
			int limit, Integer emsGroupId) throws CommonException;

	/**
	 * 网管分组列表
	 * 
	 * @return
	 * @throws CommonException
	 */
	public Map getConnectGroup(int connectGroupId) throws CommonException;

	/**
	 * 
	 * @return
	 * @throws CommonException
	 */
	public Map getConnectService() throws CommonException;

	/**
	 * 新增 Corba 连接
	 * 
	 * @param map
	 * @throws CommonException
	 */
	public String addCorbaConnection(EmsConnectionModel emsConnectionModel)
			throws CommonException;

	/**
	 * 新增Telnet 连接
	 * 
	 * @param map
	 * @throws CommonException
	 */
	public String addTelnetConnection(EmsConnectionModel emsConnectionModel,
			NeModel neModel) throws CommonException;

	/**
	 * 向网元表中插入新纪录
	 * 
	 * @param hashMap
	 * @throws CommonException
	 */
	public void addNeInfo(HashMap<String, Object> hashMap)
			throws CommonException;

	/**
	 * 根据网元本地名称获取网元编号
	 * 
	 * @param nativeEmsName
	 * @return
	 * @throws CommonException
	 */
	public String getNeInfoByNativeName(String nativeEmsName)
			throws CommonException;

	/**
	 * 根据网管连接编号查询对应的网管连接信息
	 * 
	 * @param emsConnectionId
	 * @return
	 * @throws CommonException
	 */
	public Map getConnectionByEmsConnectionId(
			EmsConnectionModel emsConnectionModel) throws CommonException;

	/**
	 * 根据网元编号取出网元信息
	 * 
	 * @param parseInt
	 * @return
	 */
	public Map getNeInfoByNeId(int neId) throws CommonException;

	/**
	 * 根据网元Id更新网管网元信息
	 * 
	 * @param hashMap
	 */
	public void modifyNeInfoByNeId(HashMap<String, Object> hashMap);

	/**
	 * 修改Corba 连接
	 * 
	 * @param map
	 * @throws CommonException
	 */
	public void modifyConnection(EmsConnectionModel emsConnectionModel,
			NeModel neModel) throws CommonException;

	/**
	 * 删除连接
	 * 
	 * @param emsConnectionId
	 * @throws CommonException
	 */
	public void deleteConnection(EmsConnectionModel emsConnectionModel)
			throws CommonException;

	/**
	 * 根据网元编号删除网元表中网元信息
	 * 
	 * @param neId
	 * @throws CommonException
	 */
	public void deleteNeInfoByNeId(Integer neId) throws CommonException;

	/**
	 * 检索网管分组信息
	 * 
	 * @param start
	 * @param limit
	 * @param emsGroupId
	 * @return
	 */
	public Map<String, Object> getEmsGroupListByGroupId(int start, int limit,
			Integer emsGroupId) throws CommonException;

	/**
	 * 新增网管分组
	 * 
	 * @param emsGroupModel
	 * @return
	 * @throws CommonException
	 */
	public void addEmsGroup(EmsGroupModel emsGroupModel) throws CommonException;

	/**
	 * 删除网管分组
	 * 
	 * @param emsGroupId
	 */
	public void deleteEmsGroup(Integer emsGroupId) throws CommonException;

	/**
	 * 修改网管分组
	 * 
	 * @param emsGroupModels
	 */
	public void modifyEmsGroup(EmsGroupModel emsGroupModel)
			throws CommonException;

	/**
	 * 新增子网
	 * 
	 * @param subnetName
	 * @param subnetNote
	 * @param parentSubnetId
	 */
	public void addSubnet(SubnetModel subnetModel) throws CommonException;

	/**
	 * 修改子网
	 * 
	 * @param emsConnectionId
	 * @param parentSubnetId
	 * @param subnetId
	 * @param subnetName
	 * @param subnetNote
	 */
	public void modifySubnet(SubnetModel subnetModel) throws CommonException;

	/**
	 * 获取要修改的子网信息
	 * 
	 * @param subnetId
	 * @return
	 * @throws CommonException
	 */
	public Map getSubnetBySubnetId(Integer subnetId) throws CommonException;

	/**
	 * 删除子网
	 * 
	 * @param subnetId
	 * @throws CommonException
	 */
	public void deleteSubnet(Integer subnetId) throws CommonException;

	/**
	 * 获取网元同步列表信息
	 * 
	 * @param start
	 * @param limit
	 * @param connectGroupId
	 * @param emsConnectionId
	 * @param emsConnectionId2 
	 * @return
	 */
	public Map<String, Object> getSyncNeListByEmsInfo(int userId,int start, int limit,
			Integer connectGroupId, Integer emsConnectionId)
			throws CommonException;

	/**
	 * 获取所有网管信息
	 * 
	 * @param connectGroupId
	 * @return
	 * @throws CommonException
	 */
	public Map getEmsConnection(Integer connectGroupId) throws CommonException;

	/**
	 * 检索该网管下的网元列表信息
	 * 
	 * @param emsConnectionId
	 * @return
	 */
	public List<Map> getNeListSyncByEmsConnectionId(Integer emsConnectionId)
			throws CommonException;

	/**
	 * 删除网管下子网的操作
	 * 
	 * @param emsConnectionId
	 * @param subnetId
	 */
	public void deleteEmsSubnet(Integer emsConnectionId, Integer subnetId)
			throws CommonException;

	/**
	 * 删除子网下的子网操作
	 * 
	 * @param parentSubnetId
	 * @param subnetId
	 */
	public void deleteSubnetSubnet(Integer parentSubnetId, Integer subnetId)
			throws CommonException;

	/**
	 * 获取网管下未分组的网元
	 * @@@分权分域到网元@@@
	 * @param emsConnectionId
	 * @return
	 * @throws CommonException
	 */
	public Map getNeListByEmsConnnectionId(Integer emsConnectionId, Integer sysUserId)
			throws CommonException;

	/**
	 * 获取网管下具体子网下的网元信息
	 * @@@分权分域到网元@@@
	 * @param emsConnectionId
	 * @param subnetId
	 * @return
	 * @throws CommonException
	 */
	public Map getNeListByEmsConnnectionIdAndSubnetId(NeModel neModel, Integer sysUserId)
			throws CommonException;

	/**
	 * 新增网元
	 * 
	 * @param neList
	 * @throws CommonException
	 */
	public void addTelnetNe(List<Map> neList) throws CommonException;

	/**
	 * 检索网元信息
	 * 
	 * @param neId
	 * @return
	 */
	public Map getTelnetNeByNeId(NeModel neModel) throws CommonException;

	/**
	 * 修改网元
	 * 
	 * @param neId
	 * @param displayName
	 * @param userName
	 * @param password
	 * @param connectionMode
	 * @throws CommonException
	 */
	public void modifyTelnetNe(NeModel neModel) throws CommonException;

	/**
	 * 删除网元
	 * 
	 * @param emsConnectionId
	 * @param neId
	 * @throws CommonException
	 */
	public void deleteTelnetNe(Integer emsConnectionId, Integer neId)
			throws CommonException;

	/**
	 * 保存已分组的子网下网元
	 * 
	 * @param subnetId
	 * @param jString
	 * @throws CommonException
	 */
	public void saveClassifiedNe(Integer subnetId, String jString)
			throws CommonException;

	/**
	 * 获取同步得到的新增/删除网元
	 * 
	 * @param emsConnectionId
	 * @return
	 */
	public Map getAlterNeByEmsConnectionId(Integer emsConnectionId)
			throws CommonException;

	/**
	 * 断开连接
	 * 
	 * @param emsConnectionModel
	 */
	public void disConnect(EmsConnectionModel emsConnectionModel)
			throws CommonException;

	/**
	 * 启动连接
	 * 
	 * @param emsConnectionModel
	 */
	public void startConnect(EmsConnectionModel emsConnectionModel)
			throws CommonException;

	/**
	 * 网元列表同步按钮弹窗中新增按钮操作
	 * 
	 * @throws CommonException
	 */
	public void neListSyncAdd(Integer emsConnectionId) throws CommonException;

	/**
	 * 网元管理页面下的 网元同步 操作
	 * 
	 * @param neModel
	 * @throws CommonException
	 */
	public void syncSelectedNe(String jsonString) throws CommonException;

	/**
	 * 交叉链接页面下 根据网管分组和网管检索交叉连接信息列表
	 * 
	 * @param start
	 * @param limit
	 * @param connectGroupId
	 * @param emsConnectionId
	 * @return
	 */
	public Map<String, Object> getCrossConnectListByEmsInfo(int userId, int start,
			int limit, Integer connectGroupId, Integer emsConnectionId)
			throws CommonException;

	/**
	 * 网元管理页面中 登录网元 操作
	 * 
	 * @param neModel
	 * @throws CommonException
	 */
	public void logonTelnetNe(String jsonString) throws CommonException;

	/**
	 * 网元管理页面中 登录网元 操作
	 * 
	 * @param neModel
	 * @throws CommonException
	 */
	public void logoutTelnetNe(String jsonString) throws CommonException;

	/**
	 * 交叉连接管理页面中 同步操作
	 * 
	 * @param neModel
	 * @throws CommonException
	 */
	public void syncNeCrossConnnection(String jsonString)
			throws CommonException;

	/**
	 * 获取交叉连接具体网元的交叉连接详细信息
	 * 
	 * @param start
	 * @param limit
	 * @param neModel
	 * @param sdhCrsModel
	 * @return
	 */
	public Map getCrsNeDetailInfoByNeId(int start, int limit, NeModel neModel,
			SdhCrsModel sdhCrsModel) throws CommonException;

	/**
	 * 以太网管理页面下 根据网管分组和网管检索以太网信息列表
	 * 
	 * @param start
	 * @param limit
	 * @param connectGroupId
	 * @param emsConnectionId
	 * @return
	 */
	public Map<String, Object> getMstpListByEmsInfo(int userId,int start, int limit,
			Integer connectGroupId, Integer emsConnectionId)
			throws CommonException;

	/**
	 * 以太网同步操作
	 * 
	 * @param jsonString
	 * @throws CommonException
	 */
	public void syncMstpNe(String jsonString) throws CommonException;

	/**
	 * 拓扑链路同步管理页面检索功能
	 * 
	 * @param emsGroupId
	 * @return
	 * @throws CommonException
	 */
	public Map getTopoLinkSyncListByEmsGroupId(int userId,int start, int limit,
			Integer emsGroupId) throws CommonException;

	/**
	 * 拓扑链路同步管理页面同步功能
	 * 
	 * @param emsConnectionModel
	 * @return
	 * @throws CommonException
	 */
	public Map topoLinkSync(EmsConnectionModel emsConnectionModel)
			throws CommonException;

	/**
	 * 拓扑链路同步管理页面同步功能 下链路变化情况
	 * 
	 * @param emsConnectionModel
	 * @return
	 * @throws CommonException
	 */
	public void topoLinkSyncChangeList(Integer emsConnectionId,Integer collectLevel,List<LinkAlterModel> changeList,Integer taskId)
			throws CommonException;

	/**
	 * 网管同步管理页面查询功能
	 * 
	 * @param start
	 * @param limit
	 * @param emsGroupId
	 * @return
	 */
	public Map getEmsConnectionSyncInfo(int userId,int start, int limit, Integer emsGroupId)
			throws CommonException;

	/**
	 * 网管同步管理页面启动任务功能
	 * 
	 * @param map
	 * @throws CommonException
	 */
	public void startTask(Map map) throws CommonException, ParseException;

	/**
	 * 网管同步管理页面挂起任务功能
	 * 
	 * @param map
	 * @throws CommonException
	 */
	public void disTask(Map map) throws CommonException;

	/**
	 * 网管同步管理页面 手动同步操作
	 * 
	 * @param emsConnectionId
	 * @param emsConnectionId2
	 * @throws CommonException
	 * @throws Exception
	 */
	public void manualSyncEms(Integer taskId, Integer emsConnectionId)
			throws CommonException, Exception;

	/**
	 * 网管管理页面中 任务状态 弹出窗口的页面显示信息
	 * 
	 * @param emsConnectionId
	 * @return
	 * @throws CommonException
	 */
	public Map getTaskDetailInfo(Integer emsConnectionId)
			throws CommonException;

	/**
	 * 接入管理器页面 检索功能
	 * 
	 * @param start
	 * @param limit
	 * @param sysSvcRecordId
	 * @return
	 */
	public Map getSysServiceRecord(int start, int limit, Integer sysSvcRecordId)
			throws CommonException;

	/**
	 * 接入管理器页面的 新增接入管理器
	 * 
	 * @param sysServiceModel
	 * @throws CommonException
	 */
	public void addSysService(SysServiceModel sysServiceModel)
			throws CommonException;

	/**
	 * 接入管理器页面的 删除接入管理器
	 * 
	 * @param sysSvcRecordId
	 * @throws CommonException
	 */
	public void deleteSysService(Integer sysSvcRecordId) throws CommonException;

	/**
	 * 接入管理器页面的 修改接入管理器
	 * 
	 * @param sysServiceModel
	 * @throws CommonException
	 */
	public void modifySysService(SysServiceModel sysServiceModel)
			throws CommonException;

	/**
	 * 检索要修改的接入管理器信息
	 * 
	 * @param sysSvcRecordId
	 * @return
	 * @throws CommonException
	 */
	public Map getSysServiceBySysSvcId(Integer sysSvcRecordId)
			throws CommonException;

	/**
	 * 检索接入服务器信息判断
	 * 
	 * @param sysServiceModel
	 * @return
	 * @throws CommonException
	 */
	public boolean getSysServiceBySvcInfo(SysServiceModel sysServiceModel)
			throws CommonException;

	/**
	 * 更新南相连接管理页面中 采集状态
	 * 
	 * @param emsConnectionModel
	 * @param minutes
	 * @throws CommonException
	 */
	public void updateCollectStatus(EmsConnectionModel emsConnectionModel,
			Integer minutes) throws CommonException;

	/**
	 * 更新网管同步管理界面任务状态查看 设置
	 * 
	 * @param emsConnectionId
	 * @param taskId
	 * @param minutes
	 * @throws ParseException
	 */
	public void updateEmsConnectionSync(Integer emsConnectionId,
			Integer taskId, Integer minutes) throws CommonException,
			ParseException;

	/**
	 * 检查网管名称是否存在
	 * 
	 * @param emsConnectionModel
	 * @return
	 * @throws CommonException
	 */
	public Boolean checkConnectionNameExist(
			EmsConnectionModel emsConnectionModel) throws CommonException;

	/**
	 * 检查子网名称是否存在
	 * 
	 * @param subnetModel
	 * @return
	 * @throws CommonException
	 */
	public Boolean checkSubnetNameExist(SubnetModel subnetModel)
			throws CommonException;

	/**
	 * 添加、修改网管连接时，检查网管IP地址是否已经存在
	 * 
	 * @param emsConnectionModel
	 * @return
	 * @throws CommonException
	 */
	public Boolean checkIpAddressExist(EmsConnectionModel emsConnectionModel)
			throws CommonException;

	/**
	 * 修改 Corba 网元
	 * 
	 * @param neId
	 * @param displayName
	 * @throws CommonException
	 */
	public void modifyCorbaNe(NeModel neModel) throws CommonException;

	/**
	 * 设置周期时间
	 * 
	 * @param map
	 * @throws CommonException
	 */
	public void setCycle(Map map) throws CommonException;

	/**
	 * 数据采集模块名称检查
	 * 
	 * @param emsConnectionModel
	 * @param sysServiceModel
	 * @param subnetModel
	 * @param emsGroupModel
	 * @return
	 * @throws CommonException
	 */
	public Boolean checkNameExist(EmsConnectionModel emsConnectionModel,
			SysServiceModel sysServiceModel, SubnetModel subnetModel,
			EmsGroupModel emsGroupModel) throws CommonException;

	/**
	 * 根据网管分组名称检索网管分组信息
	 * 
	 * @param emsGroupModel
	 * @return
	 */
	public boolean getEmsGroupByName(EmsGroupModel emsGroupModel)
			throws CommonException;

	/**
	 * 获取接入服务器下的南向连接信息
	 * 
	 * @param sysServiceModel
	 * @return
	 * @throws CommonException
	 */
	public Map getConnectionListBySysServiceId(SysServiceModel sysServiceModel)
			throws CommonException;

	/**
	 * 获取连接信息
	 * 
	 * @param emsConnectionModel
	 * @return
	 * @throws CommonException
	 */
	public boolean getConnectionByInfo(EmsConnectionModel emsConnectionModel)
			throws CommonException;

	/**
	 * 获取子网信息
	 * 
	 * @param subnetModel
	 * @return
	 * @throws CommonException
	 */
	public boolean getSubnetInfo(SubnetModel subnetModel)
			throws CommonException;
	
	/**
	 * 计时更新采集任务状态
	 * @param emsConnectionModel
	 * @throws CommonException
	 */
	public void timeUpdateCollectStatus(EmsConnectionModel emsConnectionModel) throws CommonException ;

	/**
	 * 网管同步任务  继续操作
	 * @param emsConnectionId
	 * @param taskId
	 * @throws CommonException
	 */
	public void proceedTaskSetting(Integer emsConnectionId, Integer taskId) throws CommonException ;

	/**
	 * 网管同步任务  停止操作
	 * @param emsConnectionId
	 * @param taskId
	 * @throws CommonException
	 */
	public void stopTaskSetting(Integer emsConnectionId, Integer taskId) throws CommonException ;
	
//	/**
//	 * 同步网管下的网元
//	 * @param emsConnectionId
//	 * @param neId
//	 * @param taskId
//	 * @param layerRateList
//	 * @param collectLevel
//	 * @throws CommonException
//	 */
//	public void syncEmsconnectionNe(Integer emsConnectionId, Integer neId,List<Map> neList,
//			Integer taskId, short[] layerRateList, Integer collectLevel)
//			throws CommonException ;

	/**
	 * 网管分组列表
	 * 
	 * @return
	 * @throws CommonException
	 */
	public Map getEmsConnectionGroup(int emsGroupId) throws CommonException;

	/**
	 * 拓扑链路同步操作返回信息
	 * @param emsConnectionModel
	 * @return
	 * @throws CommonException
	 */
	//与topoLinkSync合并
//	public Map topoLinkSyncReturnInfo(EmsConnectionModel emsConnectionModel)
//			throws CommonException;
	
	/**
	 * 
	 * @param map
	 * @param toMap
	 * @return
	 * @throws CommonException
	 */
	public String exportExcel(Map map , int excelFlag)throws CommonException;

	/**
	 * 设置任务周期时间
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map setBeginTime(Map map)throws CommonException;
	
	/**
	 * 计算任务同步完成以后下次同步时间
	 * @param time
	 * @param cycle
	 * @return
	 */
	public String calculateDate(String time, String cycle) throws ParseException;
	
	/**
	 * 获取网元连接速率
	 * @param layRatesString
	 * @return
	 */
	public short[] constructLayRates(String layRatesString);
	
	/**
	 * 网管同步任务操作时，更新网元表以及任务执行详细表
	 * @param neList
	 * @param taskId
	 * @param neMessage
	 * @param taskMessage
	 * @param runResult
	 * @param neFlag 要不要更新网管级别的拓扑链路同步的状态标志网元级别是1，包括网管级别的全部是0
	 * @param flag 正常1，异常0表示字段
	 */
	public void updateEmsSyncTaskStatus(List<Map> neList, Integer taskId,
			String neMessage,String taskMessage, Integer runResult, Integer neFlag,Integer flag);
	
	/**
	 * 获取首页显示的南相连接各状态数量
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public Map getParamFromDataCollectionFP(int userId) throws CommonException;

	/**
	 * 检查同一网管下的网元名称是否重复
	 * @param neModel
	 * @return
	 * @throws CommonException
	 */
	public boolean checkNeNameExist(NeModel neModel) throws CommonException;

	/**
	 * 接入服务器的Ip地址以及端口号重复判断
	 * @param sysServiceModel
	 * @return
	 * @throws CommonException
	 */
	public boolean getSysServiceBySvcIpAddress(SysServiceModel sysServiceModel)throws CommonException;

	/**
	 * 设置链路同步的同步模式
	 * @param emsConnectionId
	 * @throws CommonException
	 */
	public void editSyncMode(int emsConnectionId,int syncMode)throws CommonException;

}