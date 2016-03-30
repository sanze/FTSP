package com.fujitsu.model;

public class TopoTreeNodeModel {

	private String nodeId;
	private String nodeType;
	private String displayName;
	private String parentNodeId;
	private String parentNodeType;
	//拓扑树右键菜单
	private String returnResult;
	private String returnMessage;
//	 @@@分权分域到网元@@@
	private String domainAuth;
	
	public String getReturnResult() {
		return returnResult;
	}
	public void setReturnResult(String returnResult) {
		this.returnResult = returnResult;
	}
	public String getReturnMessage() {
		return returnMessage;
	}
	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}
	public String getParentNodeId() {
		return parentNodeId;
	}
	public void setParentNodeId(String parentNodeId) {
		this.parentNodeId = parentNodeId;
	}
	public String getParentNodeType() {
		return parentNodeType;
	}
	public void setParentNodeType(String parentNodeType) {
		this.parentNodeType = parentNodeType;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
	public String getDomainAuth() {
		return domainAuth;
	}
	public void setDomainAuth(String domainAuth) {
		this.domainAuth = domainAuth;
	}
	
}
