package com.fujitsu.model;

import java.util.List;

public class TopoNodeModel {
	
	private String nodeOrLine;
	private String nodeId;
	private String nodeType;
	private String displayName;
	private String childrenName;
	private String parentId;
	private String connectStatus;
	private String position_X;
	private String position_Y;
	//子网和网元节点用
	private String emsId;
	private String emsGroupId;
	
	private List<EMSInfoModel> childrenEMS;
	
	//子网节点用
	private String hasSDH;
	private String hasOTN;
	private String hasWDM;
	private String hasPTN;

	private List<String> subnetIdList;
	//子网、网元节点用
	private int crCount;
	private int mjCount;
	private int mnCount;
	private int wrCount;
	//网元节点用
	private String neType;
	private String productName;
	private int needAckAlmCount;
	//子网节点用
	private int crSDHCount;
	private int mjSDHCount;
	private int mnSDHCount;
	private int wrSDHCount;
	
	private int crWDMCount;
	private int mjWDMCount;
	private int mnWDMCount;
	private int wrWDMCount;
	
	private int crOTNCount;
	private int mjOTNCount;
	private int mnOTNCount;
	private int wrOTNCount;
	
	private int crPTNCount;
	private int mjPTNCount;
	private int mnPTNCount;
	private int wrPTNCount;
	
	//APA电路路由图专用（端口）
	private int crPtpCount;
	private int mjPtpCount;
	private int mnPtpCount;
	private int wrPtpCount;
	private List<Integer> ptpIdList;
	
//	 @@@分权分域到网元@@@
	private String domainAuth;
	
	public String getEmsId() {
		return emsId;
	}
	public void setEmsId(String emsId) {
		this.emsId = emsId;
	}
	public int getCrSDHCount() {
		return crSDHCount;
	}
	public void setCrSDHCount(int crSDHCount) {
		this.crSDHCount = crSDHCount;
	}
	public int getMjSDHCount() {
		return mjSDHCount;
	}
	public void setMjSDHCount(int mjSDHCount) {
		this.mjSDHCount = mjSDHCount;
	}
	public int getMnSDHCount() {
		return mnSDHCount;
	}
	public void setMnSDHCount(int mnSDHCount) {
		this.mnSDHCount = mnSDHCount;
	}
	public int getWrSDHCount() {
		return wrSDHCount;
	}
	public void setWrSDHCount(int wrSDHCount) {
		this.wrSDHCount = wrSDHCount;
	}
	public int getCrWDMCount() {
		return crWDMCount;
	}
	public void setCrWDMCount(int crWDMCount) {
		this.crWDMCount = crWDMCount;
	}
	public int getMjWDMCount() {
		return mjWDMCount;
	}
	public void setMjWDMCount(int mjWDMCount) {
		this.mjWDMCount = mjWDMCount;
	}
	public int getMnWDMCount() {
		return mnWDMCount;
	}
	public void setMnWDMCount(int mnWDMCount) {
		this.mnWDMCount = mnWDMCount;
	}
	public int getWrWDMCount() {
		return wrWDMCount;
	}
	public void setWrWDMCount(int wrWDMCount) {
		this.wrWDMCount = wrWDMCount;
	}
	public int getCrOTNCount() {
		return crOTNCount;
	}
	public void setCrOTNCount(int crOTNCount) {
		this.crOTNCount = crOTNCount;
	}
	public int getMjOTNCount() {
		return mjOTNCount;
	}
	public void setMjOTNCount(int mjOTNCount) {
		this.mjOTNCount = mjOTNCount;
	}
	public int getMnOTNCount() {
		return mnOTNCount;
	}
	public void setMnOTNCount(int mnOTNCount) {
		this.mnOTNCount = mnOTNCount;
	}
	public int getWrOTNCount() {
		return wrOTNCount;
	}
	public void setWrOTNCount(int wrOTNCount) {
		this.wrOTNCount = wrOTNCount;
	}
	public int getCrPTNCount() {
		return crPTNCount;
	}
	public void setCrPTNCount(int crPTNCount) {
		this.crPTNCount = crPTNCount;
	}
	public int getMjPTNCount() {
		return mjPTNCount;
	}
	public void setMjPTNCount(int mjPTNCount) {
		this.mjPTNCount = mjPTNCount;
	}
	public int getMnPTNCount() {
		return mnPTNCount;
	}
	public void setMnPTNCount(int mnPTNCount) {
		this.mnPTNCount = mnPTNCount;
	}
	public int getWrPTNCount() {
		return wrPTNCount;
	}
	public void setWrPTNCount(int wrPTNCount) {
		this.wrPTNCount = wrPTNCount;
	}
	
	public int getCrCount() {
		return crCount;
	}
	public void setCrCount(int crCount) {
		this.crCount = crCount;
	}
	public int getMjCount() {
		return mjCount;
	}
	public void setMjCount(int mjCount) {
		this.mjCount = mjCount;
	}
	public int getMnCount() {
		return mnCount;
	}
	public void setMnCount(int mnCount) {
		this.mnCount = mnCount;
	}
	public int getWrCount() {
		return wrCount;
	}
	public void setWrCount(int wrCount) {
		this.wrCount = wrCount;
	}
	public String getHasPTN() {
		return hasPTN;
	}
	public void setHasPTN(String hasPTN) {
		this.hasPTN = hasPTN;
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
	public int getNeedAckAlmCount() {
		return needAckAlmCount;
	}
	public void setNeedAckAlmCount(int AckAlmCount) {
		this.needAckAlmCount = AckAlmCount;
	}
	public List<String> getSubnetIdList() {
		return subnetIdList;
	}
	public void setSubnetIdList(List<String> subnetIdList) {
		this.subnetIdList = subnetIdList;
	}
	public String getHasSDH() {
		return hasSDH;
	}
	public void setHasSDH(String hasSDH) {
		this.hasSDH = hasSDH;
	}
	public String getHasOTN() {
		return hasOTN;
	}
	public void setHasOTN(String hasOTN) {
		this.hasOTN = hasOTN;
	}
	public String getHasWDM() {
		return hasWDM;
	}
	public void setHasWDM(String hasWDM) {
		this.hasWDM = hasWDM;
	}
	public List<EMSInfoModel> getChildrenEMS() {
		return childrenEMS;
	}
	public void setChildrenEMS(List<EMSInfoModel> childrenEMS) {
		this.childrenEMS = childrenEMS;
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
	public String getConnectStatus() {
		return connectStatus;
	}
	public void setConnectStatus(String connectStatus) {
		this.connectStatus = connectStatus;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
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
	public String getChildrenName() {
		return childrenName;
	}
	public void setChildrenName(String childrenName) {
		this.childrenName = childrenName;
	}
	public String getEmsGroupId() {
		return emsGroupId;
	}
	public void setEmsGroupId(String emsGroupId) {
		this.emsGroupId = emsGroupId;
	}
	public String getDomainAuth() {
		return domainAuth;
	}
	public void setDomainAuth(String domainAuth) {
		this.domainAuth = domainAuth;
	}
	public int getCrPtpCount() {
		return crPtpCount;
	}
	public void setCrPtpCount(int crPtpCount) {
		this.crPtpCount = crPtpCount;
	}
	public int getMjPtpCount() {
		return mjPtpCount;
	}
	public void setMjPtpCount(int mjPtpCount) {
		this.mjPtpCount = mjPtpCount;
	}
	public int getMnPtpCount() {
		return mnPtpCount;
	}
	public void setMnPtpCount(int mnPtpCount) {
		this.mnPtpCount = mnPtpCount;
	}
	public int getWrPtpCount() {
		return wrPtpCount;
	}
	public void setWrPtpCount(int wrPtpCount) {
		this.wrPtpCount = wrPtpCount;
	}
	public List<Integer> getPtpIdList() {
		return ptpIdList;
	}
	public void setPtpIdList(List<Integer> ptpIdList) {
		this.ptpIdList = ptpIdList;
	}
	
}
