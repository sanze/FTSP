package com.fujitsu.model;

import java.util.List;

public class ResourceTopoNodeModel {

    private String nodeOrLine;  // 对象内容区分，例：网元：node；Link：line
    private String nodeId;
	private String nodeType;
	private String displayName;
	private String position_X;
	private String position_Y;
	//网元节点用
	private String neType;
	private String productName;
	//子网节点用
	private List<String> subnetIdList;	
//	 @@@分权分域到网元@@@
	private String domainAuth;
	
	public String getNodeOrLine() {
		return nodeOrLine;
	}
	public void setNodeOrLine(String nodeOrLine) {
		this.nodeOrLine = nodeOrLine;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
	public String getNeType() {
		return neType;
	}
	public void setNeType(String neType) {
		this.neType = neType;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public List<String> getSubnetIdList() {
		return subnetIdList;
	}
	public void setSubnetIdList(List<String> subnetIdList) {
		this.subnetIdList = subnetIdList;
	}
	public String getDomainAuth() {
		return domainAuth;
	}
	public void setDomainAuth(String domainAuth) {
		this.domainAuth = domainAuth;
	}
    

}
