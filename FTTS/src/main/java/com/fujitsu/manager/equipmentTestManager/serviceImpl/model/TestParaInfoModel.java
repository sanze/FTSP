package com.fujitsu.manager.equipmentTestManager.serviceImpl.model;

/**
 * @Description：
 * @author cao senrong
 * @date 2015-1-4
 * @version V1.0
 */
public class TestParaInfoModel {
	
	//路由id
	private int routeId;
	private String otdrTestRange;//测试量程
	private String otdrWaveLength;//测试波长
	private String otdrPluseWidth;//脉冲宽度
	private String otdrRefractCoefficient;//折射系数
	private String otdrTestTime;//测试时长
	
	
	public int getRouteId() {
		return routeId;
	}
	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}
	
	public String getOtdrTestRange() {
		return otdrTestRange;
	}
	public void setOtdrTestRange(String otdrTestRange) {
		this.otdrTestRange = otdrTestRange;
	}
	public String getOtdrWaveLength() {
		return otdrWaveLength;
	}
	public void setOtdrWaveLength(String otdrWaveLength) {
		this.otdrWaveLength = otdrWaveLength;
	}
	public String getOtdrPluseWidth() {
		return otdrPluseWidth;
	}
	public void setOtdrPluseWidth(String otdrPluseWidth) {
		this.otdrPluseWidth = otdrPluseWidth;
	}
	public String getOtdrRefractCoefficient() {
		return otdrRefractCoefficient;
	}
	public void setOtdrRefractCoefficient(String otdrRefractCoefficient) {
		this.otdrRefractCoefficient = otdrRefractCoefficient;
	}
	public String getOtdrTestTime() {
		return otdrTestTime;
	}
	public void setOtdrTestTime(String otdrTestTime) {
		this.otdrTestTime = otdrTestTime;
	}
	
	
}
