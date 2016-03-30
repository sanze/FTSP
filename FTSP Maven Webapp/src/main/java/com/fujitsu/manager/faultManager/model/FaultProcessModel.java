package com.fujitsu.manager.faultManager.model;

public class FaultProcessModel {
	private int errorCode;
	private int faultId;
	// 故障处理 2：响应 3：恢复 5：归档
	private int status;
	public FaultProcessModel(){}
	public FaultProcessModel(int faultId,int processType){
		this.faultId = faultId;
		this.status = processType;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public int getFaultId() {
		return faultId;
	}
	public void setFaultId(int faultId) {
		this.faultId = faultId;
	}
	public int getProcessType() {
		return status;
	}
	public void setProcessType(int processType) {
		this.status = processType;
	}

}
