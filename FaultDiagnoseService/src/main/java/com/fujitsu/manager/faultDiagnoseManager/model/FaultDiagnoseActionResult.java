package com.fujitsu.manager.faultDiagnoseManager.model;

public class FaultDiagnoseActionResult {
	// 主告警监测对象本身
	private MainAlarmObjectSelf objSelf;
	private int objSelfStatus;
	// 主告警监测对象相邻端口
	private MainAlarmAdjacentPort adjacentPort;
	private int adjacentPortStatus;
	// 主告警监测对象所属传输系统
	private MainAlarmTransmissionSys transmissionSys;
	private int transmissionSysStatus;
	// 主告警监测对象同缆端口
	private MainAlarmPeerCablePort peerCablePort;
	private int peerCablePortStatus;
	
	public FaultDiagnoseActionResult() {
		objSelf = new MainAlarmObjectSelf();
		adjacentPort = new MainAlarmAdjacentPort();
		transmissionSys = new MainAlarmTransmissionSys();
		peerCablePort = new MainAlarmPeerCablePort();
	}
	
	public MainAlarmObjectSelf getMainAlarmObjectSelf() {
		return objSelf;
	}
	public MainAlarmAdjacentPort getMainAlarmAdjacentPort() {
		return adjacentPort;
	}
	public MainAlarmTransmissionSys getMainAlarmTransmissionSys() {
		return transmissionSys;
	}
	public MainAlarmPeerCablePort getMainAlarmPeerCablePort() {
		return peerCablePort;
	}
}
