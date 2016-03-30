package com.fujitsu.manager.dataCollectManager.corbaDataModel;

import globaldefs.NameAndStringValue_T;

/**
 * @author xuxiaojun
 *
 */
public class LayeredParametersModel {

	private short layer;
	private NameAndStringValue_T[] transmissionParams;
	
	public short getLayer() {
		return layer;
	}
	public void setLayer(short layer) {
		this.layer = layer;
	}
	public NameAndStringValue_T[] getTransmissionParams() {
		return transmissionParams;
	}
	public void setTransmissionParams(NameAndStringValue_T[] transmissionParams) {
		this.transmissionParams = transmissionParams;
	}
	
	
}
