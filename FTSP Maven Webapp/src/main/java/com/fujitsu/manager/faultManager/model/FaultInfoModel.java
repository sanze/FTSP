package com.fujitsu.manager.faultManager.model;

import net.sf.json.JSONObject;

public class FaultInfoModel {
	private int faultId;
	//故障源(1:人工 2：自动)
	private int source;
	//故障类型(1：设备 2：线路)
	private int type;
	private int reasonFirst;
	private int reasonSecond;
	//系统名称
	private String sysName;
	private String stationName;
	private String emsName;
	private String neName;
	private String unitName;
	private int unitId;
	private String danwei;
	private String aStation;
	private String zStation;
	private String nearStation;
	private int distance;
	private double longitude;
	private double latitude;
	private int isBroken;
	private String startTime;
	private String endTime;
	private String confirmTime;
	private int accuracy;
	private String memo;
	private int status =1;
	private int errorCode;
	public FaultInfoModel(){}
	public FaultInfoModel(String jsonString){
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		if(jsonObject.get("type")!= null){
			this.type  = jsonObject.getInt("type");
		}
		if(jsonObject.get("faultId")!=null){
			this.faultId = jsonObject.getInt("faultId");
		}
		if(jsonObject.get("source")!=null){
			this.source = jsonObject.getInt("source");
		}
		if(jsonObject.get("reason1")!=null&&!"".equals(jsonObject.getString("reason1"))){
			this.reasonFirst = jsonObject.getInt("reason1");
		}
		if(jsonObject.get("sysName")!=null&&!"".equals(jsonObject.getString("sysName"))){
			this.sysName = jsonObject.getString("sysName");
		}
		if(jsonObject.get("distance")!=null&&!"".equals(jsonObject.getString("distance"))){
			this.distance = jsonObject.getInt("distance");
		}
		if(jsonObject.get("reason2")!=null&&!"".equals(jsonObject.getString("reason2"))){
			this.reasonSecond = jsonObject.getInt("reason2");
		}
		if(jsonObject.get("ems")!=null){
			this.emsName = jsonObject.getString("ems");
		}
		if(jsonObject.get("neName")!=null){
			this.neName = jsonObject.getString("neName");
		}
		if(jsonObject.get("unitName")!=null){
			this.unitName = jsonObject.getString("unitName");
		}
		if(jsonObject.get("unitId")!=null&&!"".equals(jsonObject.getString("unitId"))){
			this.unitId = jsonObject.getInt("unitId");
		}
		if(jsonObject.get("stationName")!=null&&!"".equals(jsonObject.getString("stationName"))){
			this.stationName = jsonObject.getString("stationName");
		}
		if(jsonObject.get("danwei")!=null&&!"".equals(jsonObject.getString("danwei"))){
			this.danwei = jsonObject.getString("danwei");
		}
		if(jsonObject.get("aStation")!=null&&!"".equals(jsonObject.getString("aStation"))){
			this.aStation = jsonObject.getString("aStation");
		}
		if(jsonObject.get("zStation")!=null&&!"".equals(jsonObject.getString("zStation"))){
			this.zStation = jsonObject.getString("zStation");
		}
		if(jsonObject.get("nearStation")!=null&&!"".equals(jsonObject.getString("nearStation"))){
			this.nearStation = jsonObject.getString("nearStation");
		}
		if(jsonObject.get("longitude")!=null&&!"".equals(jsonObject.getString("longitude"))){
			this.longitude = jsonObject.getDouble("longitude");
		}
		if(jsonObject.get("latitude")!=null&&!"".equals(jsonObject.getString("latitude"))){
			this.latitude = jsonObject.getDouble("latitude");
		}
		if(jsonObject.get("isBroken")!=null&&!"".equals(jsonObject.getString("isBroken"))){
			this.isBroken = jsonObject.getInt("isBroken");
		}
		if(jsonObject.get("startTime")!=null&&!"".equals(jsonObject.getString("startTime"))){
			this.startTime = jsonObject.getString("startTime");
		}
		if(jsonObject.get("endTime")!=null&&(!"".equals(jsonObject.getString("endTime")))){
			this.endTime = jsonObject.getString("endTime");
		}
		if(jsonObject.get("confirmTime")!=null&&!("".equals(jsonObject.getString("confirmTime")))){
			this.confirmTime = jsonObject.getString("confirmTime");
		}
		if(jsonObject.get("accuracy")!=null&&!("").equals(jsonObject.getString("accuracy"))){
			this.accuracy = jsonObject.getInt("accuracy");
		}
		if(jsonObject.get("memo")!=null&&!("".equals(jsonObject.getString("memo")))){
			this.memo = jsonObject.getString("memo");
		}
		if(jsonObject.get("status")!=null&&!("".equals(jsonObject.getString("status")))){
			this.status = jsonObject.getInt("status");
		}
	}
	public int getFaultId() {
		return faultId;
	}
	public void setFaultId(int faultId) {
		this.faultId = faultId;
	}
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getReasonFirst() {
		return reasonFirst;
	}
	public void setReasonFirst(int reasonFirst) {
		this.reasonFirst = reasonFirst;
	}
	public int getReasonSecond() {
		return reasonSecond;
	}
	public void setReasonSecond(int reasonSecond) {
		this.reasonSecond = reasonSecond;
	}
	public String getSysName() {
		return sysName;
	}
	public void setSysName(String sysName) {
		this.sysName = sysName;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getEmsName() {
		return emsName;
	}
	public void setEmsName(String emsName) {
		this.emsName = emsName;
	}
	public String getNeName() {
		return neName;
	}
	public void setNeName(String neName) {
		this.neName = neName;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public int getUnitId() {
		return unitId;
	}
	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}
	public String getDanwei() {
		return danwei;
	}
	public void setDanwei(String danwei) {
		this.danwei = danwei;
	}
	public String getaStation() {
		return aStation;
	}
	public void setaStation(String aStation) {
		this.aStation = aStation;
	}
	public String getzStation() {
		return zStation;
	}
	public void setzStation(String zStation) {
		this.zStation = zStation;
	}
	public String getNearStation() {
		return nearStation;
	}
	public void setNearStation(String nearStation) {
		this.nearStation = nearStation;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public int getIsBroken() {
		return isBroken;
	}
	public void setIsBroken(int isBroken) {
		this.isBroken = isBroken;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getConfirmTime() {
		return confirmTime;
	}
	public void setConfirmTime(String confirmTime) {
		this.confirmTime = confirmTime;
	}
	public int getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
}
