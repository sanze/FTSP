package com.fujitsu.model;

public class LinkEvaluateLineModel {

	// 对象内容区分，例：网元：node；Link：line
    private String nodeOrLine;
    // 链路ID，例：88
    private String linkId;
    // 起始节点ID，例：9
    private String fromNode;
    // 起始节点类型，例：SDH：1；WDM：2；OTN：3；PTN：4；微波：5；FTTX：6；VirtualNE：9；未知：99
    private String fromNodeType;
    // 终端节点ID，例：11
    private String toNode;
    // 终端节点类型，例：SDH：1；WDM：2；OTN：3；PTN：4；微波：5；FTTX：6；VirtualNE：9；未知：99
    private String toNodeType;
    // Link线类型，例：单线：sdh；双线：wdm
    private String lineType;
    // 主信号质量等级，例：无评估数据：0；A级：1；B级：2；C级：3；D级：4
    private String mainQuality;
    // OSX信号质量等级，例：无评估数据：0；A级：1；B级：2；C级：3；D级：4
    private String oscQuality;
    
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
	public String getLineType() {
		return lineType;
	}
	public void setLineType(String lineType) {
		this.lineType = lineType;
	}
	public String getMainQuality() {
		return mainQuality;
	}
	public void setMainQuality(String mainQuality) {
		this.mainQuality = mainQuality;
	}
	public String getOscQuality() {
		return oscQuality;
	}
	public void setOscQuality(String oscQuality) {
		this.oscQuality = oscQuality;
	}
    
}
