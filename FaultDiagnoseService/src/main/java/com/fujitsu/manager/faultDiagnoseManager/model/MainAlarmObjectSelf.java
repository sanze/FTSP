package com.fujitsu.manager.faultDiagnoseManager.model;

import java.util.List;
import java.util.Map;

import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;

public class MainAlarmObjectSelf {
	private boolean hasPm;
	private List<PmDataModel> pmData;
	private boolean hasOptStd;
	private Map<String, Object> optStd;
	
	public MainAlarmObjectSelf() {
		hasPm = false;
		pmData = null;
		hasOptStd = false;
		optStd = null;
	}
	
	public boolean isHasPm() {
		return this.hasPm;
	}
	public List<PmDataModel> getPmData() {
		return this.pmData;
	}
	public void setPmData(List<PmDataModel> value) {
		if (value != null && value.size() > 0) {
			this.hasPm = true;
			this.pmData = value;
		}
	}
	public Map<String, Object> getOptStd() {
		return this.optStd;
	}
	public void setOptStd(Map<String, Object> value) {
		if (value != null) {
			this.hasOptStd = true;
			this.optStd = value;
		} else {
			this.hasOptStd = false;
			this.optStd = null;
		}
	}

	public boolean isHasOptStd() {
		return hasOptStd;
	}

}
