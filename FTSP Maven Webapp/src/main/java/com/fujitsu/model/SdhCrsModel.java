package com.fujitsu.model;

public class SdhCrsModel {

	//网元Id
	private Integer sdhCrsId;

	//网元Id
	private Integer emsConnectionId; 
	
	//网元Id
	private Integer neId;
 
	//是否活动状态
	private Integer active;

	//交叉连接类型
	private Integer ccType;

	//交叉连接类型
	private String ccName;
	
	//方向 0._D_NA 1._D_BIDIRECTIONAL 2._D_SOURCE 3._D_SINK
	private Integer direction;
	
	//A_END_CTP
	private Integer aEndCtp;
	
	//Z_END_CTP
	private Integer zEndCtp;
    
	//连交叉连接速率速率  VC12,VC3,VC4,4c,16C,64C
	private String rate;
	
	//电路生成过程中临时变量保存，是否查找过电路 1.查找过 0.没有查找过
	private Integer isInCircuit;
	
	//标记删除的状态：1是最近一次新增，2是最近一次删除，3是以前删除
	private Integer changeState;
	
	//电路数量
	private Integer circuitCount;
	
	//是否虚拟交叉 0：不是 1：是
	private Integer isVirtual;
	
	//是否删除 0：不是 1：是
	private Integer isDel;

	//创建时间
	private String createTime;
	
	//更新时间
	private String updateTime;

	public Integer getSdhCrsId() {
		return sdhCrsId;
	}

	public void setSdhCrsId(Integer sdhCrsId) {
		this.sdhCrsId = sdhCrsId;
	}

	public Integer getEmsConnectionId() {
		return emsConnectionId;
	}

	public void setEmsConnectionId(Integer emsConnectionId) {
		this.emsConnectionId = emsConnectionId;
	}

	public Integer getNeId() {
		return neId;
	}

	public void setNeId(Integer neId) {
		this.neId = neId;
	}

	public Integer getActive() {
		return active;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

	public Integer getCcType() {
		return ccType;
	}

	public void setCcType(Integer ccType) {
		this.ccType = ccType;
	}

	public String getCcName() {
		return ccName;
	}

	public void setCcName(String ccName) {
		this.ccName = ccName;
	}

	public Integer getDirection() {
		return direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	public Integer getaEndCtp() {
		return aEndCtp;
	}

	public void setaEndCtp(Integer aEndCtp) {
		this.aEndCtp = aEndCtp;
	}

	public Integer getzEndCtp() {
		return zEndCtp;
	}

	public void setzEndCtp(Integer zEndCtp) {
		this.zEndCtp = zEndCtp;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public Integer getIsInCircuit() {
		return isInCircuit;
	}

	public void setIsInCircuit(Integer isInCircuit) {
		this.isInCircuit = isInCircuit;
	}

	public Integer getChangeState() {
		return changeState;
	}

	public void setChangeState(Integer changeState) {
		this.changeState = changeState;
	}

	public Integer getCircuitCount() {
		return circuitCount;
	}

	public void setCircuitCount(Integer circuitCount) {
		this.circuitCount = circuitCount;
	}

	public Integer getIsVirtual() {
		return isVirtual;
	}

	public void setIsVirtual(Integer isVirtual) {
		this.isVirtual = isVirtual;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
 
}
