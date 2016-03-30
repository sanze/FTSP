package com.fujitsu.model;

import java.util.List;

public class NeModel {

	//网元Id
	private Integer neId;
	//网元Id列表
	private List<Integer> neIdList;
	//网管编号
	private Integer emsConnectionId;
	//子网编号
	private Integer subnetId;
	//网元名，网管内部标识
	private String name;
	//用户标签
	private String userLabel;
	//本地名称
	private String nativeEmsName;
	//显示名称
	private String displayName;
	//owner
	private String owner;
	//局站--corba传回
	private String location;
	//版本
	private String version;
	//网元型号名称
	private String productName;
	//通信状态 0.在线 1.离线 2.未知
	private Integer communicationState;
	//网元是否已同步--网管侧
	private Integer emsInSyncState;
	//支持层速率
	private String suportRates;
	//网元类型1.SDH 2.WDM 3.OTN 4.PTN 5.微波 6.FTTX
	private Integer type;
	//操作状态 中兴
	private String operationalStatus;
	//告警状态 中兴
	private Integer alarmStatus;
	//描述信息
	private String descriptionInfo;
	//网元功能分类 中兴
	private String meType;
	//网络地址 中兴U31
	private String netAddress;
	//机房Id
	private Integer resourceRoomId;
	//1.HW 2.ZTE 3.朗讯 4.烽火 5.ALC 9.富士通
	private Integer factory;
	//telnet连接登录用户名
	private String userName;
	//telnet连接登录密码
	private String password;
	//连接方式 0.自动 1.人工
	private Integer connectionMode;
	//同步状态 1.已同步 2.未同步 3.同步失败
	private Integer basicSyncStatus;
	//网元同步时间
	private String baseSyncTime;
	//同步结果
	private String basicSyncResult;
	//同步状态 1.已同步 2.未同步 3.同步失败
	private Integer mstpSyncStatus;
	//mstp同步时间
	private String mstpSyncTime;
	//同步结果
	private String mstpSyncResult;
	//同步状态 1.已同步 2.未同步 3.同步失败
	private Integer crsSyncStatus;
	//交叉连接同步时间
	private String crsSyncTime;
	//同步结果
	private String crsSyncResult;
	//网元采集等级 1.重点采集 2.循环采集 3.不采集
	private Integer neLevel;
	//采集结果
	private String collectResult;
	//两次采集间隔时间
	private String collectInterval;
	//最近采集时间
	private String lastCollectTime;
	//是否虚拟网元 0：不是 1：是
	private Integer isVirtualNe;
	//是否采集计数值 0：不是 1：是
	private Integer collectNumbic;
	//是否采集物理量 0：不是 1：是
	private Integer collectPhysical;
	//是否采集通道信息 0：不是 1：是 WDM通道 hw:每信道中心波长（current max min） zte:channelNo SDH通道 CTP性能
	private Integer collectCtp;
	//POSITION_X
	private Integer positionX;
	//POSITION_Y
	private Integer positionY;
	//是否删除 0：不是 1：是 2：标记删除
	private Integer isDel;
	//同步进度条表示
	private String syncName;
	//创建时间
	private String createTime;
	//更新时间
	private String updateTime;
	// corbor同步模式 ： 1：手动2：自动
	private Integer syncMode;
	
	public Integer getNeId() {
		return neId;
	}
	public void setNeId(Integer neId) {
		this.neId = neId;
	}
	public Integer getEmsConnectionId() {
		return emsConnectionId;
	}
	public void setEmsConnectionId(Integer emsConnectionId) {
		this.emsConnectionId = emsConnectionId;
	}
	public Integer getSubnetId() {
		return subnetId;
	}
	public void setSubnetId(Integer subnetId) {
		this.subnetId = subnetId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserLabel() {
		return userLabel;
	}
	public void setUserLabel(String userLabel) {
		this.userLabel = userLabel;
	}
	public String getNativeEmsName() {
		return nativeEmsName;
	}
	public void setNativeEmsName(String nativeEmsName) {
		this.nativeEmsName = nativeEmsName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Integer getCommunicationState() {
		return communicationState;
	}
	public void setCommunicationState(Integer communicationState) {
		this.communicationState = communicationState;
	}
	public Integer getEmsInSyncState() {
		return emsInSyncState;
	}
	public void setEmsInSyncState(Integer emsInSyncState) {
		this.emsInSyncState = emsInSyncState;
	}
	public String getSuportRates() {
		return suportRates;
	}
	public void setSuportRates(String suportRates) {
		this.suportRates = suportRates;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getOperationalStatus() {
		return operationalStatus;
	}
	public void setOperationalStatus(String operationalStatus) {
		this.operationalStatus = operationalStatus;
	}
	public Integer getAlarmStatus() {
		return alarmStatus;
	}
	public void setAlarmStatus(Integer alarmStatus) {
		this.alarmStatus = alarmStatus;
	}
	public String getDescriptionInfo() {
		return descriptionInfo;
	}
	public void setDescriptionInfo(String descriptionInfo) {
		this.descriptionInfo = descriptionInfo;
	}
	public String getMeType() {
		return meType;
	}
	public void setMeType(String meType) {
		this.meType = meType;
	}
	public String getNetAddress() {
		return netAddress;
	}
	public void setNetAddress(String netAddress) {
		this.netAddress = netAddress;
	}
	public Integer getResourceRoomId() {
		return resourceRoomId;
	}
	public void setResourceRoomId(Integer resourceRoomId) {
		this.resourceRoomId = resourceRoomId;
	}
	public Integer getFactory() {
		return factory;
	}
	public void setFactory(Integer factory) {
		this.factory = factory;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getConnectionMode() {
		return connectionMode;
	}
	public void setConnectionMode(Integer connectionMode) {
		this.connectionMode = connectionMode;
	}
	public Integer getBasicSyncStatus() {
		return basicSyncStatus;
	}
	public void setBasicSyncStatus(Integer basicSyncStatus) {
		this.basicSyncStatus = basicSyncStatus;
	}
	public String getBaseSyncTime() {
		return baseSyncTime;
	}
	public void setBaseSyncTime(String baseSyncTime) {
		this.baseSyncTime = baseSyncTime;
	}
	public String getBasicSyncResult() {
		return basicSyncResult;
	}
	public void setBasicSyncResult(String basicSyncResult) {
		this.basicSyncResult = basicSyncResult;
	}
	public Integer getMstpSyncStatus() {
		return mstpSyncStatus;
	}
	public void setMstpSyncStatus(Integer mstpSyncStatus) {
		this.mstpSyncStatus = mstpSyncStatus;
	}
	public String getMstpSyncTime() {
		return mstpSyncTime;
	}
	public void setMstpSyncTime(String mstpSyncTime) {
		this.mstpSyncTime = mstpSyncTime;
	}
	public String getMstpSyncResult() {
		return mstpSyncResult;
	}
	public void setMstpSyncResult(String mstpSyncResult) {
		this.mstpSyncResult = mstpSyncResult;
	}
	public Integer getCrsSyncStatus() {
		return crsSyncStatus;
	}
	public void setCrsSyncStatus(Integer crsSyncStatus) {
		this.crsSyncStatus = crsSyncStatus;
	}
	public String getCrsSyncTime() {
		return crsSyncTime;
	}
	public void setCrsSyncTime(String crsSyncTime) {
		this.crsSyncTime = crsSyncTime;
	}
	public String getCrsSyncResult() {
		return crsSyncResult;
	}
	public void setCrsSyncResult(String crsSyncResult) {
		this.crsSyncResult = crsSyncResult;
	}
	public Integer getNeLevel() {
		return neLevel;
	}
	public void setNeLevel(Integer neLevel) {
		this.neLevel = neLevel;
	}
	public String getCollectResult() {
		return collectResult;
	}
	public void setCollectResult(String collectResult) {
		this.collectResult = collectResult;
	}
	public String getCollectInterval() {
		return collectInterval;
	}
	public void setCollectInterval(String collectInterval) {
		this.collectInterval = collectInterval;
	}
	public String getLastCollectTime() {
		return lastCollectTime;
	}
	public void setLastCollectTime(String lastCollectTime) {
		this.lastCollectTime = lastCollectTime;
	}
	public Integer getIsVirtualNe() {
		return isVirtualNe;
	}
	public void setIsVirtualNe(Integer isVirtualNe) {
		this.isVirtualNe = isVirtualNe;
	}
	public Integer getCollectNumbic() {
		return collectNumbic;
	}
	public void setCollectNumbic(Integer collectNumbic) {
		this.collectNumbic = collectNumbic;
	}
	public Integer getCollectPhysical() {
		return collectPhysical;
	}
	public void setCollectPhysical(Integer collectPhysical) {
		this.collectPhysical = collectPhysical;
	}
	public Integer getCollectCtp() {
		return collectCtp;
	}
	public void setCollectCtp(Integer collectCtp) {
		this.collectCtp = collectCtp;
	}
	public Integer getPositionX() {
		return positionX;
	}
	public void setPositionX(Integer positionX) {
		this.positionX = positionX;
	}
	public Integer getPositionY() {
		return positionY;
	}
	public void setPositionY(Integer positionY) {
		this.positionY = positionY;
	}
	public Integer getIsDel() {
		return isDel;
	}
	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}
	public String getSyncName() {
		return syncName;
	}
	public void setSyncName(String syncName) {
		this.syncName = syncName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public Integer getSyncMode() {
		return syncMode;
	}
	public void setSyncMode(Integer syncMode) {
		this.syncMode = syncMode;
	}
	public List<Integer> getNeIdList() {
		return neIdList;
	}
	public void setNeIdList(List<Integer> neIdList) {
		this.neIdList = neIdList;
	}
}