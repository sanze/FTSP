package com.fujitsu.model;

public class ResourceTopoLineModel {

	private String nodeOrLine;	// node或line
    private String linkId;
	private String fromNode;
	private String fromNodeType;	// 参见共通定义视图部分的节点类型
	private String toNode;
	private String toNodeType;
	private String unoccupiedVc4Count;
	private String vc4Total;		//ems分组之间或子网间的连线此数据为空
	private String unoccupiedVc12Count;
	private String vc12Total;		//ems分组之间或子网间的连线此数据为空
	
	public String getNodeOrLine() {
		return nodeOrLine;
	}
	public void setNodeOrLine(String nodeOrLine) {
		this.nodeOrLine = nodeOrLine;
	}
    public String getLinkId() {
        return linkId;
    }
    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }
	public String getFromNode() {
		return fromNode;
	}
	public void setFromNode(String fromNode) {
		this.fromNode = fromNode;
	}
	public String getFromNodeType() {
		return fromNodeType;
	}
	public void setFromNodeType(String fromNodeType) {
		this.fromNodeType = fromNodeType;
	}
	public String getToNode() {
		return toNode;
	}
	public void setToNode(String toNode) {
		this.toNode = toNode;
	}
	public String getToNodeType() {
		return toNodeType;
	}
	public void setToNodeType(String toNodeType) {
		this.toNodeType = toNodeType;
	}
	public String getUnoccupiedVc4Count() {
		return unoccupiedVc4Count;
	}
	public void setUnoccupiedVc4Count(String unoccupiedVc4Count) {
		this.unoccupiedVc4Count = unoccupiedVc4Count;
	}
	public String getVc4Total() {
		return vc4Total;
	}
	public void setVc4Total(String vc4Total) {
		this.vc4Total = vc4Total;
	}
	public String getUnoccupiedVc12Count() {
		return unoccupiedVc12Count;
	}
	public void setUnoccupiedVc12Count(String unoccupiedVc12Count) {
		this.unoccupiedVc12Count = unoccupiedVc12Count;
	}
	public String getVc12Total() {
		return vc12Total;
	}
	public void setVc12Total(String vc12Total) {
		this.vc12Total = vc12Total;
	} 

}
