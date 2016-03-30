package com.fujitsu.model;

public class LinkEvaluateNodeModel {

	// 对象内容区分，例：网元：node；Link：line
    private String nodeOrLine;
    // 网元ID，例：55
    private String neId;
    // 站名，例：鼓楼
    private String stationName;
    // 网元名，例：1-新华6楼3500
    private String neName;
    // 网元类型，例：SDH：1；WDM：2；OTN：3；PTN：4；微波：5；FTTX：6；VirtualNE：9；未知：99
    private String neType;
    // 网元座标，例：33
    private String position_X;
    private String position_Y;
    
	public String getNodeOrLine() {
		return nodeOrLine;
	}
	public void setNodeOrLine(String nodeOrLine) {
		this.nodeOrLine = nodeOrLine;
	}
	public String getNeId() {
		return neId;
	}
	public void setNeId(String neId) {
		this.neId = neId;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getNeName() {
		return neName;
	}
	public void setNeName(String neName) {
		this.neName = neName;
	}
	public String getNeType() {
		return neType;
	}
	public void setNeType(String neType) {
		this.neType = neType;
	}
	public String getPosition_X() {
		return position_X;
	}
	public void setPosition_X(String position_X) {
		this.position_X = position_X;
	}
	public String getPosition_Y() {
		return position_Y;
	}
	public void setPosition_Y(String position_Y) {
		this.position_Y = position_Y;
	}
}
