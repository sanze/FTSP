package com.fujitsu.model;

public class EmsConnectionModel {
	
	private Integer emsConnectionId;
	
	private Integer emsGroupId;
	//IP地址
	private String ip;
	//显示名称
	private String emsDisplayName;
	//用户名
	private String userName;
	//密码
	private String password;
	//网管类型     11.T2000 12.U2000 21.E300 23.U31 31.lucent oms 41.烽火 otnm2000 51.ALU 91.Fujitsu
	private Integer emsType;
	
	//emsNeName,emsNeUser,emsNePassword
	/**********************************************/
	//网管类型     51.Fujitsu 
	private Integer telnetType;
/**	//telnet 网关网元id
	private String emsNeName;
	//telnet 用户名
	private String emsNeUser;
	//telnet 密码
	private String emsNePassword;*/
	/**********************************************/
	//连接方式 0.自动连接 1.手动连接
	private long connectMode;//连接模式:自动、手动
	//1.corba类型连接 2.telnet类型连接
	private Integer connectionType;
	//端口
	private String port;
	//ems内部名称
	private String emsName;
	//内部ems名称，适配中兴网管采集板卡数据ems不一致问题
	private String internalEmsName;
	//编码格式 
	private String encode;
	//ems版本
	private String emsVersion;
	//idl版本号
	private String idlVersion;
	//连接异常原因
	private String exceptionReason;
	//命令间隔时间
	private Integer intervalTime;
	//命令超时时间
	private Integer timeOut;
	//允许采集开始时间
	private String collecStartTime;
	//允许采集结束时间
	private String collecEndTime;
	//设备厂家
	private Integer factory;
	//连接状态 1.连接正常 2.连接中断 3.连接异常
	private Integer connectStatus;
	//网关网元id
	private Integer gateWayNeId;
	//  同步周期--cron表达式 
	private String syncPeriod;
	// 同步周期--显示用 
	private String syncPeriodDisplay;
	// ems最近同步时间 
	private String latestSyncTime;
	// ems下次同步时间 
	private String nextSyncTime;
	// 接入服务器Id 
	private Integer connectServer;
	//COLLECT_SOURCE 数据采集源 1.当前性能 2.历史性能 
	private Integer collectSource;
	// 1.等待执行、2.正常采集、3.暂停采集 4.禁止采集
	private Integer collectStatus;
	// 同步状态 1.已同步 2.未同步 3.同步失败 
	private Integer linkSyncStatus;
	// 拓扑链路同步时间 
	private String linkSyncTime;
	// 同步结果 
	private String linkSyncResult;
	// POSITION_X 
	private Integer positionX;
	// POSITION_Y
	private Integer positionY;
	// 是否删除 0：不是 1：是 
	private Integer isDel;
	//更新时间
	private String updateTime;
	//创建时间
	private String createTime;
	
	/**
	 * @author daihuijun 
	 * @descript 新增<param>线程数</param>和<param>迭代数</param>
	 * @since svn: 2014/8/1
	 */
	// 线程数
	private Integer threadNum;
	// 迭代数
	private Integer iteratorNum;
	
	public Integer getEmsConnectionId() {
		return emsConnectionId;
	}
	public void setEmsConnectionId(Integer emsConnectionId) {
		this.emsConnectionId = emsConnectionId;
	}
	public Integer getEmsGroupId() {
		return emsGroupId;
	}
	public void setEmsGroupId(Integer emsGroupId) {
		this.emsGroupId = emsGroupId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getEmsDisplayName() {
		return emsDisplayName;
	}
	public void setEmsDisplayName(String emsDisplayName) {
		this.emsDisplayName = emsDisplayName;
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
	public Integer getEmsType() {
		return emsType;
	}
	public void setEmsType(Integer emsType) {
		this.emsType = emsType;
	}

	public Integer getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(Integer connectionType) {
		this.connectionType = connectionType;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getEmsName() {
		return emsName;
	}
	public void setEmsName(String emsName) {
		this.emsName = emsName;
	}
	public String getInternalEmsName() {
		return internalEmsName;
	}
	public void setInternalEmsName(String internalEmsName) {
		this.internalEmsName = internalEmsName;
	}
	public String getEncode() {
		return encode;
	}
	public void setEncode(String encode) {
		this.encode = encode;
	}
	public String getEmsVersion() {
		return emsVersion;
	}
	public void setEmsVersion(String emsVersion) {
		this.emsVersion = emsVersion;
	}
	public String getIdlVersion() {
		return idlVersion;
	}
	public void setIdlVersion(String idlVersion) {
		this.idlVersion = idlVersion;
	}
	public String getExceptionReason() {
		return exceptionReason;
	}
	public void setExceptionReason(String exceptionReason) {
		this.exceptionReason = exceptionReason;
	}
	public Integer getIntervalTime() {
		return intervalTime;
	}
	public void setIntervalTime(Integer intervalTime) {
		this.intervalTime = intervalTime;
	}
	public Integer getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(Integer timeOut) {
		this.timeOut = timeOut;
	}
	public String getCollecStartTime() {
		return collecStartTime;
	}
	public void setCollecStartTime(String collecStartTime) {
		this.collecStartTime = collecStartTime;
	}
	public String getCollecEndTime() {
		return collecEndTime;
	}
	public void setCollecEndTime(String collecEndTime) {
		this.collecEndTime = collecEndTime;
	}
	public Integer getFactory() {
		return factory;
	}
	public void setFactory(Integer factory) {
		this.factory = factory;
	}
	public Integer getConnectStatus() {
		return connectStatus;
	}
	public void setConnectStatus(Integer connectStatus) {
		this.connectStatus = connectStatus;
	}
	public Integer getGateWayNeId() {
		return gateWayNeId;
	}
	public void setGateWayNeId(Integer gateWayNeId) {
		this.gateWayNeId = gateWayNeId;
	}
	public String getSyncPeriod() {
		return syncPeriod;
	}
	public void setSyncPeriod(String syncPeriod) {
		this.syncPeriod = syncPeriod;
	}
	public String getSyncPeriodDisplay() {
		return syncPeriodDisplay;
	}
	public void setSyncPeriodDisplay(String syncPeriodDisplay) {
		this.syncPeriodDisplay = syncPeriodDisplay;
	}
	public String getLatestSyncTime() {
		return latestSyncTime;
	}
	public void setLatestSyncTime(String latestSyncTime) {
		this.latestSyncTime = latestSyncTime;
	}
	public String getNextSyncTime() {
		return nextSyncTime;
	}
	public void setNextSyncTime(String nextSyncTime) {
		this.nextSyncTime = nextSyncTime;
	}
	public Integer getConnectServer() {
		return connectServer;
	}
	public void setConnectServer(Integer connectServer) {
		this.connectServer = connectServer;
	}
	public Integer getCollectStatus() {
		return collectStatus;
	}
	public void setCollectStatus(Integer collectStatus) {
		this.collectStatus = collectStatus;
	}
	public Integer getLinkSyncStatus() {
		return linkSyncStatus;
	}
	public void setLinkSyncStatus(Integer linkSyncStatus) {
		this.linkSyncStatus = linkSyncStatus;
	}
	public String getLinkSyncTime() {
		return linkSyncTime;
	}
	public void setLinkSyncTime(String linkSyncTime) {
		this.linkSyncTime = linkSyncTime;
	}
	public String getLinkSyncResult() {
		return linkSyncResult;
	}
	public void setLinkSyncResult(String linkSyncResult) {
		this.linkSyncResult = linkSyncResult;
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
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public Integer getTelnetType() {
		return telnetType;
	}
	public void setTelnetType(Integer telnetType) {
		this.telnetType = telnetType;
	}
	public long getConnectMode() {
		return connectMode;
	}
	public void setConnectMode(long connectMode) {
		this.connectMode = connectMode;
	}
	public Integer getThreadNum() {
		return threadNum;
	}
	public void setThreadNum(Integer threadNum) {
		this.threadNum = threadNum;
	}
	public Integer getIteratorNum() {
		return iteratorNum;
	}
	public void setIteratorNum(Integer iteratorNum) {
		this.iteratorNum = iteratorNum;
	}
	

}