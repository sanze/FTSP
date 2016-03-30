package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

/**
 * @author xuxiaojun
 *
 */
public class ForwardEndPointModel {

	private NameAndStringValue_T[] logicTPName;
	private NameAndStringValue_T[] paraList;
	private NameAndStringValue_T[] additionalInfo;
	public NameAndStringValue_T[] getLogicTPName() {
		return logicTPName;
	}
	public void setLogicTPName(NameAndStringValue_T[] logicTPName) {
		this.logicTPName = logicTPName;
	}
	public NameAndStringValue_T[] getParaList() {
		return paraList;
	}
	public void setParaList(NameAndStringValue_T[] paraList) {
		this.paraList = paraList;
	}
	public NameAndStringValue_T[] getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(NameAndStringValue_T[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	
	
}
