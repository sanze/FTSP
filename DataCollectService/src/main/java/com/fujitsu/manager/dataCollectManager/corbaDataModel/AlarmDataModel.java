package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import java.io.Serializable;

import globaldefs.NameAndStringValue_T;

public class AlarmDataModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1938033185925648187L;
	// 告警通知事件唯一ID编号
	private String notificationId = "";
	// 方向
	private String direction = "";
	// 位置
	private String location = "";
	// 定位信息
	private String locationInfo = "";
	// EMS上告警确认信息
	private String confirmStatusOri = "";
	// EMS上告警清除信息
	private String clearStatus = "";
	// 告警清除时间
	private String clearTime = "";
	// 处理建议列表
	private String handlingSuggestion = "";
	// 网管上报时间
	private String emsTime = "";
	// 网元上报时间
	private String neTime = "";
	// 告警的基本类型
	private int alarmType;
	// 告警影响业务
	private int serviceAffecting;
	// 告警级别
	private int perceivedSeverity;
	// TMF规定的标准告警名称
	private String probableCause = "";
	// 层速率
	private short layerRate;
	// 告警源实体类型
	private Integer objectType;
	// 产生告警的对象类型(objectType)的扩展
	private String objectTypeQualifier = "";
	// EMS上描述的告警名称
	private String nativeProbableCause = "";
	// 告警对象的相关信息
	private String nativeEmsName = "";
	// 告警源对象名称
	private NameAndStringValue_T[] objectName;
	// 是否可以清除标志
	private boolean isClearable;
	// 影响端口列表--暂时没用
	private NameAndStringValue_T[] affectedTPList;
	// 标准告警名称扩展
	private String probableCauseQualifier = "";
	// EMS产生的告警序号（告警清除唯一标识）
	private String alarmSerialNo = "";
	// 告警产生原因
	private String alarmReason = "";
	//附加信息
	private NameAndStringValue_T[] additionalInfo;
	//原始信息
	private String originalInfo;
	// 厂家(1:华为  2:中兴 3:朗讯 4:烽火 5:富士通 )
	private int factory;
	// 网管ID
	private int emsId;
	
	
	public String getNotificationId() {
		return notificationId;
	}
	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLocationInfo() {
		return locationInfo;
	}
	public void setLocationInfo(String locationInfo) {
		this.locationInfo = locationInfo;
	}
	public String getConfirmStatusOri() {
		return confirmStatusOri;
	}
	public void setConfirmStatusOri(String confirmStatusOri) {
		this.confirmStatusOri = confirmStatusOri;
	}
	public String getClearStatus() {
		return clearStatus;
	}
	public void setClearStatus(String clearStatus) {
		this.clearStatus = clearStatus;
	}
	public String getClearTime() {
		return clearTime;
	}
	public void setClearTime(String clearTime) {
		this.clearTime = clearTime;
	}
	public String getHandlingSuggestion() {
		return handlingSuggestion;
	}
	public void setHandlingSuggestion(String handlingSuggestion) {
		this.handlingSuggestion = handlingSuggestion;
	}
	public String getEmsTime() {
		return emsTime;
	}
	public void setEmsTime(String emsTime) {
		this.emsTime = emsTime;
	}
	public String getNeTime() {
		return neTime;
	}
	public void setNeTime(String neTime) {
		this.neTime = neTime;
	}
	public int getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}
	public int getServiceAffecting() {
		return serviceAffecting;
	}
	public void setServiceAffecting(int serviceAffecting) {
		this.serviceAffecting = serviceAffecting;
	}
	public int getPerceivedSeverity() {
		return perceivedSeverity;
	}
	public void setPerceivedSeverity(int perceivedSeverity) {
		this.perceivedSeverity = perceivedSeverity;
	}
	public String getProbableCause() {
		return probableCause;
	}
	public void setProbableCause(String probableCause) {
		this.probableCause = probableCause;
	}
	public short getLayerRate() {
		return layerRate;
	}
	public void setLayerRate(short layerRate) {
		this.layerRate = layerRate;
	}
	public Integer getObjectType() {
		return objectType;
	}
	public void setObjectType(Integer objectType) {
		this.objectType = objectType;
	}
	public String getObjectTypeQualifier() {
		return objectTypeQualifier;
	}
	public void setObjectTypeQualifier(String objectTypeQualifier) {
		this.objectTypeQualifier = objectTypeQualifier;
	}
	public String getNativeProbableCause() {
		return nativeProbableCause;
	}
	public void setNativeProbableCause(String nativeProbableCause) {
		this.nativeProbableCause = nativeProbableCause;
	}
	public String getNativeEmsName() {
		return nativeEmsName;
	}
	public void setNativeEmsName(String nativeEmsName) {
		this.nativeEmsName = nativeEmsName;
	}
	public NameAndStringValue_T[] getObjectName() {
		return objectName;
	}
	public void setObjectName(NameAndStringValue_T[] objectName) {
		this.objectName = objectName;
	}
	public boolean isClearable() {
		return isClearable;
	}
	public void setClearable(boolean isClearable) {
		this.isClearable = isClearable;
	}
	public NameAndStringValue_T[] getAffectedTPList() {
		return affectedTPList;
	}
	public void setAffectedTPList(NameAndStringValue_T[] affectedTPList) {
		this.affectedTPList = affectedTPList;
	}
	public String getProbableCauseQualifier() {
		return probableCauseQualifier;
	}
	public void setProbableCauseQualifier(String probableCauseQualifier) {
		this.probableCauseQualifier = probableCauseQualifier;
	}
	public String getAlarmSerialNo() {
		return alarmSerialNo;
	}
	public void setAlarmSerialNo(String alarmSerialNo) {
		this.alarmSerialNo = alarmSerialNo;
	}
	public String getAlarmReason() {
		return alarmReason;
	}
	public void setAlarmReason(String alarmReason) {
		this.alarmReason = alarmReason;
	}
	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public String getOriginalInfo() {
		return originalInfo;
	}
	public void setOriginalInfo(String originalInfo) {
		this.originalInfo = originalInfo;
	}
	public int getFactory() {
		return factory;
	}
	public void setFactory(int factory) {
		this.factory = factory;
	}
	public int getEmsId() {
		return emsId;
	}
	public void setEmsId(int emsId) {
		this.emsId = emsId;
	}
}
