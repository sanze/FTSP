package com.fujitsu.manager.resourceManager.model;

import com.fujitsu.common.CommonResult;
import com.fujitsu.handler.NeNameMapHandler;

public class Location {
	{
		origianlJ = 1;
		emsIp = NeNameMapHandler.getNeName("emsIp").getReturnMessage();
	}
	public Location(String location) {
		String[] str = location.split("-");
		for(int i = 0; i < str.length;i++){
			switch(i){
			case 0 : {setNeOriName(str[i]);setNeName(str[i]);}break;
			case 1 : this.slotNo = Integer.parseInt(str[i]);break;
			case 2 : this.portNo = Integer.parseInt(str[i]);break;
			case 3 : this.setOrigianlJ(Integer.parseInt(str[i]));break;
			}
		}
	}
	//表格中提供的ne名称：
	private String emsIp;
	private String neOriName;
	//网元名
	private String neName;
	//槽道号
	private int slotNo;
	//端口号
	private int portNo;
	
	private int origianlJ;

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
			CommonResult cr = NeNameMapHandler.getNeName(neName);
		this.neName = cr.getReturnMessage();
	}

	public int getSlotNo() {
		return slotNo;
	}

	public void setSlotNo(int slotNo) {
		this.slotNo = slotNo;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public int getOrigianlJ() {
		return origianlJ;
	}

	public void setOrigianlJ(int origianlJ) {
		this.origianlJ = origianlJ;
	}

	public String getNeOriName() {
		return neOriName;
	}

	public void setNeOriName(String neOriName) {
		this.neOriName = neOriName;
	}

	public String getEmsIp() {
		return emsIp;
	}

	public void setEmsIp(String emsIp) {
		this.emsIp = emsIp;
	}
	
}
