package com.fujitsu.model;

import java.util.Date;

public class TestResultModel {
	// 测试结果ID
	private int id;
	// 测试时间
	private Date testTime;
	// 测试类型
	private int testType;
	// 测试波长
	private double waveLength;
	// 脉冲宽度
	private double plusWidth;
	// 量程
	private double range;
	// 测试时长
	private double testDuration;
	// 折射系数
	private double refractCoefficient;
	// 全程传输损耗值
	private String transAttenuation;
	// 全程光学长度值
	private String opticalDistance;
	// 全程反向损耗值
	private String reverseAttenuation;
	// 关联的测试路由ID
	private int testRouteId;
	// 测试结果点
	private String resultPoint;
	// 测试结果信息
	private String testInfo;
	// 测试成功标志
	private int operateResult;
	// 质量评估
	private int evaluation;
	// 质量评估描述
	private String evalDescription;
	// 关联的测试设备名称
	private String rcName;
	// 关联的测试路由名称
	private String testRouteName;
	// 关联的测试周期
	private int testPeriod;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getTestTime() {
		return testTime;
	}
	public void setTestTime(Date testTime) {
		this.testTime = testTime;
	}
	public int getTestType() {
		return testType;
	}
	public void setTestType(int testType) {
		this.testType = testType;
	}
	public double getWaveLength() {
		return waveLength;
	}
	public void setWaveLength(double waveLength) {
		this.waveLength = waveLength;
	}
	public double getPlusWidth() {
		return plusWidth;
	}
	public void setPlusWidth(double plusWidth) {
		this.plusWidth = plusWidth;
	}
	public double getRange() {
		return range;
	}
	public void setRange(double range) {
		this.range = range;
	}
	public double getTestDuration() {
		return testDuration;
	}
	public void setTestDuration(double testDuration) {
		this.testDuration = testDuration;
	}
	public double getRefractCoefficient() {
		return refractCoefficient;
	}
	public void setRefractCoefficient(double refractCoefficient) {
		this.refractCoefficient = refractCoefficient;
	}
	public String getTransAttenuation() {
		return transAttenuation;
	}
	public void setTransAttenuation(String transAttenuation) {
		this.transAttenuation = transAttenuation;
	}
	public String getOpticalDistance() {
		return opticalDistance;
	}
	public void setOpticalDistance(String opticalDistance) {
		this.opticalDistance = opticalDistance;
	}
	public String getReverseAttenuation() {
		return reverseAttenuation;
	}
	public void setReverseAttenuation(String reverseAttenuation) {
		this.reverseAttenuation = reverseAttenuation;
	}
	public int getTestRouteId() {
		return testRouteId;
	}
	public void setTestRouteId(int testRouteId) {
		this.testRouteId = testRouteId;
	}
	public String getResultPoint() {
		return resultPoint;
	}
	public void setResultPoint(String resultPoint) {
		this.resultPoint = resultPoint;
	}
	public String getTestInfo() {
		return testInfo;
	}
	public void setTestInfo(String testInfo) {
		this.testInfo = testInfo;
	}
	public int getOperateResult() {
		return operateResult;
	}
	public void setOperateResult(int operateResult) {
		this.operateResult = operateResult;
	}
	public int getEvaluation() {
		return evaluation;
	}
	public void setEvaluation(int evaluation) {
		this.evaluation = evaluation;
	}
	public String getEvalDescription() {
		return evalDescription;
	}
	public void setEvalDescription(String evalDescription) {
		this.evalDescription = evalDescription;
	}
	public String getRcName() {
		return rcName;
	}
	public void setRcName(String rcName) {
		this.rcName = rcName;
	}
	public String getTestRouteName() {
		return testRouteName;
	}
	public void setTestRouteName(String testRouteName) {
		this.testRouteName = testRouteName;
	}
	public int getTestPeriod() {
		return testPeriod;
	}
	public void setTestPeriod(int testPeriod) {
		this.testPeriod = testPeriod;
	}

}
