package com.fujitsu.model;

import java.util.List;

public class TopoLineModel {
	
	private Integer linkId;
	private Integer unoccupiedVc4Count;
	private Integer vc4Total;
	private Integer unoccupiedVc12Count;
	private Integer vc12Total;
	
	private String nodeOrLine;
	private String fromNode;
	private String fromNodeType;
	private String toNode;
	private String toNodeType;
	private String connectStatus;
	private String tipString;
	private String lineType;
	//用于网管分组与FTSP之间的连线
	private List<EMSInfoModel> emsConnectStatus;
	//用于传输网络拓扑
	private List<LinkAlarmModel> linkAlarm;

	public List<LinkAlarmModel> getLinkAlarm() {
		return linkAlarm;
	}
	public void setLinkAlarm(List<LinkAlarmModel> linkAlarm) {
		this.linkAlarm = linkAlarm;
	}
	public List<EMSInfoModel> getEmsConnectStatus() {
		return emsConnectStatus;
	}
	public void setEmsConnectStatus(List<EMSInfoModel> emsConnectStatus) {
		this.emsConnectStatus = emsConnectStatus;
	}
	public String getLineType() {
		return lineType;
	}
	public void setLineType(String lineType) {
		this.lineType = lineType;
	}
	public String getFromNodeType() {
		return fromNodeType;
	}
	public void setFromNodeType(String fromNodeType) {
		this.fromNodeType = fromNodeType;
	}
	public String getToNodeType() {
		return toNodeType;
	}
	public void setToNodeType(String toNodeType) {
		this.toNodeType = toNodeType;
	}
	public String getNodeOrLine() {
		return nodeOrLine;
	}
	public void setNodeOrLine(String nodeOrLine) {
		this.nodeOrLine = nodeOrLine;
	}
	public String getFromNode() {
		return fromNode;
	}
	public void setFromNode(String fromNode) {
		this.fromNode = fromNode;
	}
	public String getToNode() {
		return toNode;
	}
	public void setToNode(String toNode) {
		this.toNode = toNode;
	}
	public String getConnectStatus() {
		return connectStatus;
	}
	public void setConnectStatus(String connectStatus) {
		this.connectStatus = connectStatus;
	}
	public String getTipString() {
		return tipString;
	}
	public void setTipString(String tipString) {
		this.tipString = tipString;
	}
	public Integer getLinkId() {
		return linkId;
	}
	public void setLinkId(Integer linkId) {
		this.linkId = linkId;
	}
	public Integer getUnoccupiedVc4Count() {
		return unoccupiedVc4Count;
	}
	public void setUnoccupiedVc4Count(Integer unoccupiedVc4Count) {
		this.unoccupiedVc4Count = unoccupiedVc4Count;
	}
	public Integer getVc4Total() {
		return vc4Total;
	}
	public void setVc4Total(Integer vc4Total) {
		this.vc4Total = vc4Total;
	}
	public Integer getUnoccupiedVc12Count() {
		return unoccupiedVc12Count;
	}
	public void setUnoccupiedVc12Count(Integer unoccupiedVc12Count) {
		this.unoccupiedVc12Count = unoccupiedVc12Count;
	}
	public Integer getVc12Total() {
		return vc12Total;
	}
	public void setVc12Total(Integer vc12Total) {
		this.vc12Total = vc12Total;
	}
	
}
