package com.fujitsu.manager.faultDiagnoseManager.model;

public class FaultAnalysis {
	
	// 主告警监测对象本身性能
	private StringBuilder objSelfPm;

	// 主告警监测对象相邻端口告警
	private StringBuilder adjacentPortAlm;
	
	// 主告警监测对象相邻端口性能
	private StringBuilder adjacentPortPm;

	// 主告警监测对象所属传输系统
	private StringBuilder transmissionSysSw;

	// 主告警监测对象同缆端口
	private StringBuilder peerCablePortAlm;
	
	public FaultAnalysis() {
		objSelfPm = new StringBuilder();
		adjacentPortAlm = new StringBuilder();
		adjacentPortPm = new StringBuilder();
		transmissionSysSw = new StringBuilder();
		peerCablePortAlm = new StringBuilder();
	}
	
	public StringBuilder getObjSelfPm() {
		return this.objSelfPm;
	}
	public StringBuilder getAdjacentPortAlm() {
		return this.adjacentPortAlm;
	}
	public StringBuilder getAdjacentPortPm() {
		return this.adjacentPortPm;
	}
	public StringBuilder getTransmissionSysSw() {
		return this.transmissionSysSw;
	}
	public StringBuilder getPeerCablePortAlm() {
		return this.peerCablePortAlm;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("主告警对象本身性能：\r");
		result.append(objSelfPm.toString());
		
		result.append("主告警对象相邻端口告警：\r");
		result.append(adjacentPortAlm.toString());
		
		result.append("主告警对象相邻端口性能：\r");
		result.append(adjacentPortPm.toString());
		
		result.append("主告警对象所属传输系统倒换状态：\r");
		result.append(transmissionSysSw.toString());
		
		result.append("主告警对象同缆端口告警：\r");
		result.append(peerCablePortAlm.toString());
		
		return result.toString();
	}
}
