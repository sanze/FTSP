package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

/**
 * @author xuxiaojun
 *
 */
public class MSTPBindingPathModel {

	private int direction;
	private NameAndStringValue_T[][] allPathList;
	private NameAndStringValue_T[][] usedPathList;
	private NameAndStringValue_T[] additionalInfo;
	//中兴特有
	private NameAndStringValue_T[] vcgTpName;
	
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public NameAndStringValue_T[][] getAllPathList() {
		return allPathList;
	}
	public void setAllPathList(NameAndStringValue_T[][] allPathList) {
		this.allPathList = allPathList;
	}
	public NameAndStringValue_T[][] getUsedPathList() {
		return usedPathList;
	}
	public void setUsedPathList(NameAndStringValue_T[][] usedPathList) {
		this.usedPathList = usedPathList;
	}
	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public NameAndStringValue_T[] getVcgTpName() {
		return vcgTpName;
	}
	public void setVcgTpName(NameAndStringValue_T[] vcgTpName) {
		this.vcgTpName = vcgTpName;
	}
	
}
